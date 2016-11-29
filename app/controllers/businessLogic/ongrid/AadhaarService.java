package controllers.businessLogic.ongrid;

import api.http.httpResponse.ongrid.OngridAadhaarVerificationResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.staticdao.IdProofDAO;
import dao.OnGridVerificationStatusDAO;
import models.entity.Candidate;
import models.entity.OM.IDProofReference;
import models.entity.Static.IdProof;
import models.entity.ongrid.OnGridProfessions;
import models.entity.ongrid.OnGridVerificationFields;
import models.entity.ongrid.OnGridVerificationStatus;
import models.entity.ongrid.transactional.OngridRequestStats;
import models.entity.ongrid.transactional.OngridVerificationResults;
import okhttp3.*;
import play.Logger;
import play.api.Play;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by archana on 11/16/16.
 */
public class AadhaarService {

    private static final String VERIFICATION_TYPE = "AadhaarSync";

    private static final String SDF_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final SimpleDateFormat sfd_yyyymmdd = new SimpleDateFormat(SDF_FORMAT_YYYYMMDD);

    public static final String AA_NAME = "\"name\"";
    public static final String AA_GENDER = "\"gender\"";
    public static final String AA_CITY = "\"city\"";
    public static final String AA_PROFESSIONID = "\"professionId\"";
    public static final String AA_OTHER_PROFESSIONID = "\"otherProfession\"";
    public static final String AA_PHONE = "\"phone\"";
    public static final String AA_EMAIL = "\"email\"";
    public static final String AA_DOB = "\"dob\"";
    public static final String AA_AGE = "\"age\"";
    public static final String AA_ADDRESS = "\"aadhaarAddress\"";
    public static final String AA_ADDRESS_CO = "\"co";
    public static final String AA_ADDRESS_LINE1 = "\"line1\"";
    public static final String AA_ADDRESS_LINE2 = "\"line2\"";
    public static final String AA_ADDRESS_LOCALITY = "\"locality\"";
    public static final String AA_ADDRESS_LANDMARK = "\"landmark\"";
    public static final String AA_ADDRESS_DISTRICT = "\"district\"";
    public static final String AA_ADDRESS_VTC = "\"vtc\"";
    public static final String AA_ADDRESS_STATE = "\"state\"";
    public static final String AA_ADDRESS_PIN = "\"pincod\"";
    public static final String AA_COMMUNITY_ID = "\"communityid\"";

    private static Map<String, OnGridVerificationStatus> myStatusNameToStatusRecord =
            new HashMap<String, OnGridVerificationStatus>();
    private OnGridVerificationStatusDAO myStatusDAO = new OnGridVerificationStatusDAO();

    private String myAuthKey;
    private Integer myCommunityId;

    public AadhaarService(String authKey, Integer communityId)
    {
        myAuthKey = authKey;
        myCommunityId = communityId;
    }

    public OngridAadhaarVerificationResponse sendAadharSyncVerificationRequest(Candidate candidate)
    {
        OngridAadhaarVerificationResponse response = new OngridAadhaarVerificationResponse();

        IdProof aadhaarStaticRecord =
                IdProof.find.where().eq("idProofId", IdProofDAO.IDPROOF_AADHAAR_ID).findUnique();

        if (aadhaarStaticRecord == null) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("Static table idproof does not have entry for Aadhaar Card");

            return response;
        }

        IDProofReference candidateAadhaarRecord =
                IDProofReference.find.where().eq("candidate", candidate).eq("idProof", aadhaarStaticRecord).findUnique();

        if (candidateAadhaarRecord == null) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("No Aadhaar record found for candidate. "
                    + "Cannot proceed with Aadhaar verification for candidate with mobile number "
                    + candidate.getCandidateMobile());
            return response;
        }
        else if (candidateAadhaarRecord.getIdProofNumber() == null || candidateAadhaarRecord.getIdProofNumber().isEmpty()) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("Do not have Aadhaar id number for candidate. "
                    + "Cannot proceed with Aadhaar verification for candidate with mobile number "
                    + candidate.getCandidateMobile());
            return response;
        }

        String aadhaarUID = candidateAadhaarRecord.getIdProofNumber();
        String reqParams = constructRequestParams(candidate);

        Response onGridResponse = null;
        String responseBody = null;
        int trialCount = 0;

        while (trialCount <= 3) {
            try {
                trialCount++;
                responseBody = sendRequest(reqParams, aadhaarUID, myAuthKey);
                //responseBody = onGridResponse.body().string();
                break;
            } catch (IOException ioEx) {
                Logger.error("Exception on Ongrid Aadhaar verification request for " + aadhaarUID
                        + " Trial Count: " + trialCount + " => IOException " + ioEx.getMessage() + " Retyring..");
                ioEx.printStackTrace();
            }
        }

        if (responseBody == null) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_TIMEDOUT);
            response.setResponseMessage("FATAL: Ongrid Aadhaar verification request failed for " + aadhaarUID);

            Logger.error("FATAL: Ongrid Aadhaar verification request failed for " + aadhaarUID);

            return response;
        }

        OnGridAadharResponse onGridAadharResponse = parseVerificationResponse(responseBody);
        response.setOngridResponse(onGridAadharResponse);

        saveVerificationResponse(candidate, onGridAadharResponse, myCommunityId);

        Logger.info("Parsed response: " + response.getOngridResponse().toString());

        response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_SUCCESS);
        response.setResponseMessage("Aadhaar verification executed succesfully!");

        return response;
    }

    private String constructRequestParams(Candidate candidate)
    {
        Long professionId = null;

        if (candidate.getCandidateCurrentJobDetail() != null) {
            Logger.info("current job roleid: " + candidate.getCandidateCurrentJobDetail().getJobRole().getJobName());

            OnGridProfessions profession =
                    OnGridProfessions.find.where().eq("jobRole",
                            candidate.getCandidateCurrentJobDetail().getJobRole()).findUnique();

            if (profession != null) {
                professionId = profession.getProfessionId();
            }
        }

        StringBuilder reqBuilder  = new StringBuilder();
        String req = "{\n  \"name\": \"Archana\",\n  \"gender\": \"F\",\n  \"city\": \"Coimbatore\",\n  \"professionId\": \"69\",\n  \"otherProfession\": \"business\",\n  \"phone\": \"8197222248\",\n \"email\": \"sp.archana@gmail.com\",\n  \"dob\": \"1985-01-16\",\n  \"age\": \"31\",\n  \"aadhaarAddress\": {\n    \"co\": \"\",\n    \"line1\": \"\",\n    \"line2\": \"\",\n    \"locality\": \"\",\n    \"landmark\": \"\",\n    \"vtc\": \"\",\n    \"district\": \"\",\n    \"state\": \"\",\n    \"pincode\": \"\"\n  },\n “communityId” : “66095”\n}";

        String gender = candidate.getCandidateGender() == null ? "": candidate.getCandidateGender() == 0 ? "M" : "F";

        reqBuilder.append("{\n");
        reqBuilder.append(AA_NAME + ":" + "\"" + candidate.getCandidateFirstName() + "\",\n");
        reqBuilder.append(AA_GENDER + ":" + "\"" + gender + "\",\n");
        reqBuilder.append(AA_CITY + ":" + "\"" + candidate.getLocality().getCity() + "\",\n");

        if (professionId != null) {
            reqBuilder.append(AA_PROFESSIONID + ":" + "\"" + professionId + "\",\n");
        }
        else {
            reqBuilder.append(AA_PROFESSIONID + ":" + "\"" + 69 + "\",\n");
            reqBuilder.append(AA_OTHER_PROFESSIONID + ":" + "\"" + "unknown" + "\",\n");
        }

        reqBuilder.append(AA_PHONE + ":" +  "\"" + candidate.getCandidateMobile()  + "\",\n");

        if (candidate.getCandidateEmail() != null) {
            reqBuilder.append(AA_EMAIL + ":" + "\"" + candidate.getCandidateEmail() + "\",\n");
        }

        if (candidate.getCandidateDOB() != null) {
            reqBuilder.append(AA_DOB + ":" + "\"" + sfd_yyyymmdd.format(candidate.getCandidateDOB()) + "\",\n");
            reqBuilder.append(AA_AGE + ":" + "\"" + candidate.getCandidateAge() + "\"");
        }

        // we are not collecting aadhaar address at this point. Hence we will not pass these params

        reqBuilder.append("\n}");

        return reqBuilder.toString();
    }

    private String sendRequest(String reqParams, String aadhaarUID, String authKey) throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        Logger.info("Req Params: " + reqParams);
        RequestBody body = RequestBody.create(mediaType, reqParams);
        String url = "https://api-staging.ongrid.in/app/v1/aadhaar/" + aadhaarUID + "/verifysync";

        // RequestBody.create(mediaType, "{\n  \"name\": \"Archana\",\n  \"gender\": \"F\",\n  \"city\": \"Coimbatore\",\n  \"professionId\": \"69\",\n  \"otherProfession\": \"business\",\n  \"phone\": \"8197222248\",\n \"email\": \"sp.archana@gmail.com\",\n  \"dob\": \"1985-01-16\",\n  \"age\": \"31\",\n  \"aadhaarAddress\": {\n    \"co\": \"\",\n    \"line1\": \"\",\n    \"line2\": \"\",\n    \"locality\": \"\",\n    \"landmark\": \"\",\n    \"vtc\": \"\",\n    \"district\": \"\",\n    \"state\": \"\",\n    \"pincode\": \"\"\n  }\n}");

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + authKey)
                .build();

        Logger.info("Request: " + request.toString() + " \n Response Body: " + request.body().toString()
                + " \n Response Header: " + request.headers().toString());

        Response onGridResponse = client.newCall(request).execute();
        String responseBody = onGridResponse.body().string();
        String responseMessage =  onGridResponse.message().toString();

        Logger.info("Response: " + onGridResponse.toString() + " \n Response Headers: "
                + onGridResponse.headers().toString() + "\n Response Body: " + responseBody
                + " \n Response Message: " + responseMessage);

        OngridRequestStats stats =
                new OngridRequestStats(VERIFICATION_TYPE, url, request.toString(), responseBody, responseMessage);
        stats.save();

        return responseBody;
    }

    private OnGridAadharResponse parseVerificationResponse(String ongridVerificationResponse)
    {
        ObjectMapper newMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OnGridAadharResponse parsedResponse;

        try {
            parsedResponse = newMapper.readValue(ongridVerificationResponse, OnGridAadharResponse.class);
        }
        catch (IOException ioEx) {
            Logger.error("Exception: " + ioEx.getMessage());
            ioEx.printStackTrace();
            return null;
        }

        return parsedResponse;
    }

    private void saveVerificationResponse(Candidate candidate, OnGridAadharResponse ongridResponse, Integer communityId)
    {
        // check if the candidate already has verification results
        Map<OnGridVerificationFields, OngridVerificationResults> existingResults =
                OngridVerificationResults.find.where().eq("candidate.candidateId",
                        candidate.getCandidateId()).findMap("ongridField", OnGridVerificationFields.class);

        // Get mapping of current verification status
        Map<OnGridVerificationFields, OnGridVerificationStatus> newResults = getFieldToResponseMap(ongridResponse);

        boolean isInsertNew;

        // iterate on all new results obtained from ongrid response
        for (Map.Entry<OnGridVerificationFields, OnGridVerificationStatus> newResultEntry : newResults.entrySet()) {
            OnGridVerificationFields field = newResultEntry.getKey();
            OnGridVerificationStatus status = newResultEntry.getValue();
            isInsertNew = true;

            // if this field was verified earlier and has a corressponding record, then update it
            if (existingResults != null && !existingResults.isEmpty()) {
                OngridVerificationResults existingRecord = existingResults.get(field);
                if (existingRecord != null) {
                    OnGridVerificationStatus existingStatus = existingRecord.getOngridVerificationStatus();

                    if (!existingStatus.equals(status)) {
                        existingRecord.setOngridVerificationStatus(status);
                        existingRecord.update();
                    }

                    // we dont need to insert a new record when a record for the same field existed earlier,
                    // irrespective of whether the value matched or not
                    isInsertNew = false;
                }
            }

            // if no record existed before, then create a new one and insert
            if (isInsertNew) {
                OngridVerificationResults newResult = new OngridVerificationResults(candidate,
                        field,
                        status,
                        Long.valueOf(communityId),
                        Long.valueOf(ongridResponse.getIndividualId()));

                newResult.save();
            }
        }
    }

    private Map<OnGridVerificationFields, OnGridVerificationStatus>
    getFieldToResponseMap(OnGridAadharResponse ongridResponse)
    {
        Map<OnGridVerificationFields, OnGridVerificationStatus> fieldToVerificationStatus =
                new HashMap<OnGridVerificationFields, OnGridVerificationStatus>();

        for (Map.Entry<OnGridVerificationFields, String> entry : ongridResponse.getMyFieldToStatusMap().entrySet()) {
            // get the value
            String responseValue = entry.getValue();

            if (responseValue == null) continue;

            // get the DB record corressponding to the response
            OnGridVerificationStatus statusRecord = getStatusRecord(responseValue);

            // get the corresponding DB object for verification field
            OnGridVerificationFields field = entry.getKey();

            fieldToVerificationStatus.put(field, statusRecord);
        }

        return fieldToVerificationStatus;
    }

    private OnGridVerificationStatus getStatusRecord(String statusName) {

        OnGridVerificationStatus status = myStatusNameToStatusRecord.get(statusName);
        if (status == null) {
            status = myStatusDAO.getByName(statusName);
            if (status == null) {
                Logger.error("Status not found on ongrid_verification_status table for value: " + statusName);
                throw new RuntimeException("FATAL: Status not found on ongrid_verification_status table for value: " + statusName);
            }
        }
        return status;
    }
}

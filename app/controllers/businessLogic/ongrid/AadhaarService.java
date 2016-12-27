package controllers.businessLogic.ongrid;

import api.http.httpResponse.ongrid.OngridAadhaarVerificationResponse;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.CandidateService;
import dao.staticdao.IdProofDAO;
import dao.OnGridVerificationStatusDAO;
import models.entity.Candidate;
import models.entity.OM.IDProofReference;
import models.entity.OM.JobHistory;
import models.entity.Static.IdProof;
import models.entity.Static.JobRole;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private String myBaseUrl;

    public AadhaarService(String authKey, Integer communityId, String baseUrl)
    {
        myAuthKey = authKey;
        myCommunityId = communityId;
        myBaseUrl = baseUrl;
    }

    public OngridAadhaarVerificationResponse sendAadharSyncVerificationRequest(String candidateMobile)
    {
        Candidate candidate = CandidateService.isCandidateExists(candidateMobile);
        OngridAadhaarVerificationResponse response = new OngridAadhaarVerificationResponse();

        if (candidate == null) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("Candidate with mobile " + candidateMobile + " does not exist");
            Logger.warn(response.getResponseMessage());
            return response;
        }

        IdProof aadhaarStaticRecord =
                IdProof.find.where().eq("idProofId", IdProofDAO.IDPROOF_AADHAAR_ID).findUnique();

        if (aadhaarStaticRecord == null) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("Static table idproof does not have entry for Aadhaar Card");
            Logger.warn(response.getResponseMessage());

            return response;
        }

        IDProofReference candidateAadhaarRecord =
                IDProofReference.find.where().eq("candidate", candidate).eq("idProof", aadhaarStaticRecord).findUnique();

        if (candidateAadhaarRecord == null) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("No Aadhaar record found for candidate. "
                    + "Cannot proceed with Aadhaar verification for candidate with mobile number "
                    + candidate.getCandidateMobile());
            Logger.warn(response.getResponseMessage());

            return response;
        }
        else if (candidateAadhaarRecord.getIdProofNumber() == null || candidateAadhaarRecord.getIdProofNumber().isEmpty()) {
            response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_ERROR);
            response.setResponseMessage("Do not have Aadhaar id number for candidate. "
                    + "Cannot proceed with Aadhaar verification for candidate with mobile number "
                    + candidate.getCandidateMobile());
            Logger.warn(response.getResponseMessage());
            return response;
        }

        String aadhaarUID = candidateAadhaarRecord.getIdProofNumber();
        String reqParams = constructRequestParams(candidate);

        Response onGridResponse = null;
        String responseBody = null;
        int trialCount = 0;

        while (trialCount <= 3) {
            trialCount++;
            responseBody = sendRequest(reqParams, aadhaarUID, myAuthKey);

            if (responseBody != null) break;

            Logger.error("Exception on Ongrid Aadhaar verification request for " + aadhaarUID
                    + " Trial Count: " + trialCount + ". Retrying..");
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

        Logger.info("Parsed ongrid response: " + response.getOngridResponse().toString());

        response.setResponseStatus(OngridAadhaarVerificationResponse.STATUS_SUCCESS);
        response.setResponseMessage("Aadhaar verification executed succesfully for " + candidateMobile);

        Logger.info(response.getResponseMessage());

        return response;
    }

    /**
     * Queries the tables IdProofReference for all candidates who have aadhaar number mentioned,
     * joins with OnGridVerificationResults to understand already verified candidates
     * and returns the delta list of candidates that are yet to be verified
     * @return
     */
    public List<Candidate> getNonVerifiedAadhaarList() {

        // Query all candidates that have aadhaar document number given
        List<IDProofReference> aadhaarRecordsWithNumber =
                IDProofReference.find.where().and(Expr.eq("idProof.idProofId", IdProofDAO.IDPROOF_AADHAAR_ID),
                        Expr.isNotNull("idProofNumber")).findList();

        List<Candidate> candidatesToBeVerified = new ArrayList<Candidate>();

        for (IDProofReference idProofReference : aadhaarRecordsWithNumber) {
            candidatesToBeVerified.add(idProofReference.getCandidate());
        }

        // join with OnGridVerificationResults
        List<OngridVerificationResults> ongridResultsRecords =
                OngridVerificationResults.find.select("candidate").setDistinct(true).findList();

        // return the list of candidates that have aadhaar numebr given, but dont have an entry in
        // OnGridVerificationResults
        for (OngridVerificationResults result : ongridResultsRecords) {
            candidatesToBeVerified.remove(result.getCandidate());
        }

        return candidatesToBeVerified;
    }

    private String constructRequestParams(Candidate candidate)
    {
        Long professionId = null;

        if (candidate.getJobHistoryList() != null && !candidate.getJobHistoryList().isEmpty()) {

            JobRole currentJobRole = null;

            for (JobHistory jobHistory : candidate.getJobHistoryList()) {
                if(jobHistory.getCurrentJob() != null && jobHistory.getCurrentJob()) {
                    currentJobRole = jobHistory.getJobRole();
                }
            }

            List<OnGridProfessions> professionsList =
                    OnGridProfessions.find.where().eq("jobRole", currentJobRole).findList();

            if (professionsList != null && !professionsList.isEmpty()) {
                // Ongrid has lots of job roles that trujobs doesnt have. So currently multiple trujobs jobroles
                // are mapped to single ongrid jobrole
                // we will take the first item from the list for such cases to construct the request
                professionId = professionsList.get(0).getProfessionId();
            }
        }

        StringBuilder reqBuilder  = new StringBuilder();

        String gender = null;

        if (candidate.getCandidateGender() != null) {
            gender = candidate.getCandidateGender() == 0 ? "M" : "F";
        }

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

        reqBuilder.append(AA_PHONE + ":" +  "\"" + candidate.getCandidateMobile() + "\"");

        if (candidate.getCandidateEmail() != null) {
            reqBuilder.append(",\n" + AA_EMAIL + ":" + "\"" + candidate.getCandidateEmail() + "\"");
        }

        if (candidate.getCandidateDOB() != null) {
            reqBuilder.append(",\n" + AA_DOB + ":" + "\"" + sfd_yyyymmdd.format(candidate.getCandidateDOB())+ "\"");
            reqBuilder.append(",\n" + AA_AGE + ":" + "\"" + candidate.getCandidateAge() + "\"");
        }

        if (reqBuilder.charAt(reqBuilder.length()-1) == ',') {
            reqBuilder.deleteCharAt(reqBuilder.length()-1);
        }

        // we are not collecting aadhaar address at this point. Hence we will not pass these params

        reqBuilder.append("\n}");

        return reqBuilder.toString();
    }

    private String sendRequest(String reqParams, String aadhaarUID, String authKey)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, reqParams);
        String url = myBaseUrl + "/app/v1/aadhaar/" + aadhaarUID + "/verifysync";

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + authKey)
                .build();

        Logger.info("Request: " + request.toString() + " \n Request Params: " + reqParams
                + " \n Request Header: " + request.headers().toString());

        Response onGridResponse = null;
        String responseBody = null;
        String responseMessage = null;

        try {
            onGridResponse = client.newCall(request).execute();
            responseBody = onGridResponse.body().string();
        }
        catch (IOException ex) {
            responseMessage = "FAILURE";
            OngridRequestStats stats =
                    new OngridRequestStats(VERIFICATION_TYPE, url, request.toString(), ex.getMessage(), responseMessage);
            stats.save();

            Logger.error("IOException on sending aadhaar request:" + ex.getMessage());
            ex.printStackTrace();

            return responseBody;
        }

        responseMessage =  onGridResponse.message().toString();

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
                        Logger.info("Updating " + existingRecord.getOngridField().getFieldName() + " with status " + status.getStatusName());
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
                throw new RuntimeException("FATAL: Status not found on ongrid_verification_status table for value: " + statusName);
            }
        }
        return status;
    }
}

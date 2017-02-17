package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.PartnerSignUpRequest;
import api.http.httpResponse.TruResponse;
import controllers.businessLogic.employee.EmployeeCSVBean;
import controllers.businessLogic.employee.ParseEmployeeCSV;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.Locality;
import models.util.Message;
import play.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 16/2/17.
 */
public class EmployeeService {
    private int channel;

    public EmployeeService(int channel) {
        this.channel = channel;
    }

    public ParseEmployeeCSV.ParseResponse parseEmployeeCsv(File file, RecruiterProfile recruiterProfile) throws Exception {

        ParseEmployeeCSV parser = new ParseEmployeeCSV();
        ParseEmployeeCSV.ParseResponse response = parser.parseCSV(file);

        if(response.getStatus() == TruResponse.STATUS_SUCCESS) {

                // remove duplicate info
            parser.removeDuplicates(response.getParsedList());

                // create partner with access level 2
            response.getMessages().addAll(createEmployee(response.getParsedList(), recruiterProfile));
        }

        return response;
    }

    private List<Message> createEmployee(List<EmployeeCSVBean> parsedList, RecruiterProfile recruiterProfile) throws Exception {
        List<Message> messageList = new ArrayList<>();
        if(parsedList.size() == 0) {
            return messageList;
        }

        for(EmployeeCSVBean bean: parsedList) {
            Logger.warn("operating on bean mobile: " + bean.getMobile());
            PartnerSignUpRequest signUpRequest = new PartnerSignUpRequest();
            signUpRequest.setPartnerEmail(bean.getEmail());
            signUpRequest.setpartnerAuthMobile(bean.getMobile());
            signUpRequest.setPartnerMobile(bean.getMobile());
            signUpRequest.setPartnerName(bean.getName());
            signUpRequest.setPartnerType(ServerConstants.PARTNER_TYPE_PRIVATE_EMPLOYEE);

            SearchJobService searchJobService = new SearchJobService();
            Locality locality = searchJobService.determineLocality(bean.getLocality());

            if (locality == null){
                Message message = new Message(Message.MESSAGE_ERROR, "Unable to resolve locality: " + bean.getLocality() + " for mobile: "+ bean.getMobile());
                messageList.add(message);
                Logger.error("removed element " + bean.getMobile());
                parsedList.remove(bean);
                continue;
            }

            signUpRequest.setPartnerLocality(locality.getLocalityId());

            signUpRequest.setPartnerCompanyCode(String.valueOf(recruiterProfile.getCompany().getCompanyCode()));
            PartnerService.signUpPartner(signUpRequest, channel, ServerConstants.LEAD_SOURCE_UNKNOWN);
        }

        return messageList;
    }
}

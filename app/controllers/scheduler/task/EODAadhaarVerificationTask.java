package controllers.scheduler.task;

import api.ServerConstants;
import api.http.httpResponse.ongrid.OngridAadhaarVerificationResponse;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.ongrid.AadhaarService;
import controllers.businessLogic.ongrid.OnGridConstants;
import dao.staticdao.IdProofDAO;
import models.entity.Candidate;
import models.entity.OM.IDProofReference;
import play.Logger;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by zero on 14/12/16.
 */

/**
 * Task that runs every day once and triggers aadhaar verification for those candidate
 * who have given their Aadhaar card number and haven't been verified yet
 *
 * */
public class EODAadhaarVerificationTask extends TimerTask {

    private AadhaarService myAadhaarService;

    private void bulkAadharVerification(List<Candidate> toBeVerfiedList){
        new Thread(() -> {
                for(Candidate candidate: toBeVerfiedList) {
                    myAadhaarService.sendAadharSyncVerificationRequest(candidate.getCandidateMobile());
                }
            }).start();
    }

    @Override
    public void run() {
        // fetch all non verified aadhaar card IdProofReference
        // trigger bulkVerification method;
        Logger.info("Starting EOD Aadhaar Task..");

        myAadhaarService = new AadhaarService(OnGridConstants.AUTH_STRING,
                OnGridConstants.COMMUNITY_ID, OnGridConstants.BASE_URL);

        bulkAadharVerification(myAadhaarService.getNonVerifiedAadhaarList());
    }
}

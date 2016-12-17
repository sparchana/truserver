package controllers.scheduler.task;

import api.ServerConstants;
import controllers.businessLogic.CandidateService;
import dao.staticdao.IdProofDAO;
import models.entity.Candidate;
import models.entity.OM.IDProofReference;

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

    private List<IDProofReference> getNonVeririfedAadhaarList() {

        return IDProofReference.find
                .where()
                .isNull("verification_status")
                .eq("idProof.idProofId", IdProofDAO.IDPROOF_AADHAAR_ID)
                .findList();
    }

    private void bulkAadharVerification(List<IDProofReference> idProofReferenceList){
        for(IDProofReference idProofReference: idProofReferenceList) {
            CandidateService.verifyAadhaar(idProofReference.getCandidate().getCandidateMobile());
        }
    }

    @Override
    public void run() {
        // fetch all non verified aadhaar card IdProofReference
        // trigger bulkVerification method;

        bulkAadharVerification( getNonVeririfedAadhaarList());
    }
}

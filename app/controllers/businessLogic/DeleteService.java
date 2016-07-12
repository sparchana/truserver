package controllers.businessLogic;

import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

import java.util.List;

/**
 * Created by zero on 11/7/16.
 */

//
//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//
//
//
//          to bless this code to be bug free
//
//
//
//   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    /*
    *  Order Rank 1: Delete Auth
    *  Order Rank 2: Delete Interaction
    *  Order Rank 3: Delete Candidate
    *  Order Rank 4: Delete Lead
    *
    *  'Delete Order' for Deleting a Candidate
    *  1<-2<-3<-4
    *
    *  'Delete Order' for Deleting Lead
    *  2<-4
    *
    * */

public class DeleteService {
    private static void DeleteAuthService(Candidate candidate){
        /* Order Rank 1 : Should be invoked before candidate obj is deleted */
        // delete auth
        Auth auth = Auth.find.where().eq("candidateId", candidate.getCandidateId()).findUnique();
        if(auth != null){
            auth.delete();
        }
    }

    private static void DeleteInteractionService(String uuId){
        /* Order Rank 2 */
        if(uuId != null || !uuId.isEmpty()){
            // delete interaction
            List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", uuId).findList();
            for(Interaction interactionToDelete : interactionList){
                Logger.info("Delete Interaction for : " + interactionToDelete.getObjectAUUId());
                interactionToDelete.delete();
            }
        }
    }

    public static Candidate DeleteCandidateServiceButPreserveOne(String mobile){
        List<Candidate> existingCandidateList = Candidate.find.where().eq("candidateMobile", mobile).findList();
        if(existingCandidateList != null && existingCandidateList.size() > 1){
            existingCandidateList.sort((l1, l2) -> l1.getCandidateId() <= l2.getCandidateId() ? 1 : 0);
            int candidateListSize = existingCandidateList.size();

            // perish all duplicate candidate data but preserve oldest one
            for(int i =1; i<candidateListSize ; i++) {
                // leave the old one and delete the rest
                // delete auth
                DeleteAuthService(existingCandidateList.get(i));
                // delete interaction
                DeleteInteractionService(existingCandidateList.get(i).getCandidateUUId());
                // candidate perish
                existingCandidateList.get(i).delete();
            }
            Candidate nonPerishedCandidate = existingCandidateList.get(0);
            if(nonPerishedCandidate == null ){
                Logger.info("something terribly went wrong in deletion of candidate " + mobile);
            } else {
                DeleteLeadServiceButPreserveOne(mobile);
            }
            return nonPerishedCandidate;
        }
        return null;
    }

    public static Lead DeleteLeadServiceButPreserveOne(String mobile){
        Candidate nonPerishedCandidate = DeleteCandidateServiceButPreserveOne(mobile);

        List<Lead> existingLeadList = Lead.find.where().eq("leadMobile", mobile).findList();
        if(existingLeadList.isEmpty()){
            return null;
        }
        if(existingLeadList.size() > 1) {
            existingLeadList.sort((l1, l2) -> l1.getLeadId() >= l2.getLeadId() ? 1 : 0);
            // no corresponding candidate found for this lead
            // hence preserve the least pk value obj and delete rest
            if(nonPerishedCandidate == null){
                for(int i=1; i<existingLeadList.size(); i++) {
                    // delete lead records except the least leadId record
                    // delete lead interaction forever
                    DeleteInteractionService(existingLeadList.get(i).getLeadUUId());
                    // delete lead forever
                    existingLeadList.get(i).delete();
                }
            } else {
                /* preserve the bound lead obj and delete rest */
                for(Lead lead: existingLeadList){
                    if(lead.getLeadId() != nonPerishedCandidate.getLead().getLeadId()){
                        // delete lead interaction forever
                        DeleteInteractionService(nonPerishedCandidate.getCandidateUUId());
                        // delete lead forever
                        lead.delete();
                    }
                }
            }
        }
        return existingLeadList.get(0);
    }
}

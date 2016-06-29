package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class InteractionService {

    public static void createInteractionForSignUpCandidate(String objectAUUId, String result, boolean isSupport) {
        if(!isSupport){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    ServerConstants.INTERACTION_TYPE_WEBSITE,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    result + " & " + ServerConstants.INTERACTION_NOTE_SELF_SIGNEDUP,
                    ServerConstants.INTERACTION_CREATED_SELF
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForCreateCandidateProfile(String uuId, Integer interactionType, String interactionNote, String interactionResult, String createdBy){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy
        );

        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForFollowUpRequest(String followUpMobile, Timestamp followUpSchedule){
        Candidate candidate = CandidateService.isCandidateExists(followUpMobile);
        String uuId = "";
        int objectAType = 99;
        Logger.info("FollowUpDateTime: " + followUpSchedule);
        SimpleDateFormat sfdFollowUp = new SimpleDateFormat(ServerConstants.SDF_FORMAT_FOLLOWUP);
        int interactionType = ServerConstants.INTERACTION_TYPE_FOLLOWUP_CALL;
        String interactionNote = ServerConstants.INTERACTION_NOTE_BLANK ;
        String interactionResult = "";
        try {
            if(candidate != null) {
                objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                uuId = candidate.getCandidateUUId();
                interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_FOLLOWED_UP_REQUEST + " " + sfdFollowUp.format(followUpSchedule);
            } else {
                Lead lead = LeadService.isLeadExists(followUpMobile);
                if(lead != null) {
                    objectAType = ServerConstants.OBJECT_TYPE_LEAD;
                    uuId = lead.getLeadUUId();
                    interactionResult = ServerConstants.INTERACTION_RESULT_LEAD_FOLLOWED_UP_REQUEST + " " + sfdFollowUp.format(followUpSchedule);
                }
            }

            Interaction interaction = new Interaction(
                    uuId,
                    objectAType,
                    interactionType,
                    interactionNote,
                    interactionResult,
                    session().get("sessionUsername")
            );

            InteractionService.createInteraction(interaction);
        } catch (NullPointerException npe){
            Logger.info("Followup deactivated");
        }
    }

    public static void createInteractionForJobApplication(String objectAUUId, String objectBUUId, String result) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST,
                ServerConstants.INTERACTION_TYPE_APPLIED_JOB,
                result,
                ServerConstants.INTERACTION_CREATED_SELF
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteraction(Interaction interaction){
        Interaction.addInteraction(interaction);
        Logger.info("Interaction saved");
    }
}

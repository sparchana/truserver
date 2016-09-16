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
    /* */
    public enum InteractionChannelType {
        UNKNOWN,
        SELF,
        SELF_ANDROID,
        SUPPORT,
        KNOWLARITY;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static void createInteractionForSignUpCandidate(String objectAUUId, String result, InteractionChannelType channelType) {
        if(channelType == InteractionChannelType.SELF || channelType == InteractionChannelType.SELF_ANDROID ){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    channelType == InteractionChannelType.SELF ? ServerConstants.INTERACTION_TYPE_WEBSITE : ServerConstants.INTERACTION_TYPE_ANDROID_SIGNUP,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    result,
                    channelType.toString()
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

    public static void createInteractionForJobApplication(String objectAUUId, String objectBUUId, String result, InteractionChannelType channelType) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST,
                ServerConstants.INTERACTION_TYPE_APPLIED_JOB,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteraction(Interaction interaction){
        Interaction.addInteraction(interaction);
        Logger.info("Interaction saved");
    }

    public static void createInteractionForLoginCandidate(String objectAUUId, InteractionChannelType channelType) {
        if(channelType == InteractionChannelType.SELF || channelType == InteractionChannelType.SELF_ANDROID){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    channelType == InteractionChannelType.SELF ? ServerConstants.INTERACTION_TYPE_WEBSITE : ServerConstants.INTERACTION_TYPE_ANDROID_LOGIN,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    ServerConstants.INTERACTION_RESULT_SELF_SIGNEDIN,
                    channelType.toString()
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForJobApplicationAttempt(String objectAUUId, String objectBUUId, String result, InteractionChannelType channelType) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                ServerConstants.INTERACTION_TYPE_TRIED_JOB_APPLY,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForDeactivateCandidate(String objectAUUId, boolean isSupport){
        if(isSupport){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    ServerConstants.INTERACTION_TYPE_CALL_OUT,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    ServerConstants.INTERACTION_RESULT_CANDIDATE_DEACTIVATED,
                    session().get("sessionUsername")
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForActivateCandidate(String objectAUUId, boolean isSupport) {
        if(isSupport){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    ServerConstants.INTERACTION_TYPE_CALL_OUT,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    ServerConstants.INTERACTION_RESULT_CANDIDATE_ACTIVATED,
                    session().get("sessionUsername")
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForResetPasswordAttempt(String objectAUUId, String result, InteractionChannelType channelType){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                ServerConstants.INTERACTION_TYPE_TRIED_PASSWORD_RESET,
                ServerConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForResetPassword(String objectAUUId, String result, InteractionChannelType channelType){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                ServerConstants.INTERACTION_TYPE_PASSWORD_RESET_SUCCESS,
                ServerConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateAlertService(String objectAUUId, String result, InteractionChannelType channelType){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                ServerConstants.INTERACTION_TYPE_CANDIDATE_ALERT,
                ServerConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }
    public static void createInteractionForSearch(String objectAUUId, String result, InteractionChannelType  channelType){
        Logger.info("Search Interaction Saved for UUID: " + objectAUUId);
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                ServerConstants.INTERACTION_TYPE_ANDROID_SEARCH,
                ServerConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }
    public static void createInteractionForViewJobPostInfo(String objectAUUId, String objectBUUId, String result, InteractionChannelType  channelType){
        Logger.info("View JobPost Info Interaction Saved for UUID: " + objectAUUId == null ? ServerConstants.TRU_DROID_NOT_LOGGED_UUID : objectAUUId);
        Interaction interaction = new Interaction(
                objectAUUId == null ? ServerConstants.TRU_DROID_NOT_LOGGED_UUID : objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST_VIEW,
                ServerConstants.INTERACTION_TYPE_ANDROID_JOP_POST_VIEW,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }
}

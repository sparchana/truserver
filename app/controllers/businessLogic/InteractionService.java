package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static api.InteractionConstants.*;
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
        KNOWLARITY,
        PARTNER;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static void createInteractionForFollowUpRequest(String followUpMobile, Timestamp followUpSchedule){
        Candidate candidate = CandidateService.isCandidateExists(followUpMobile);
        String uuId = "";
        int objectAType = 99;
        Logger.info("FollowUpDateTime: " + followUpSchedule);
        SimpleDateFormat sfdFollowUp = new SimpleDateFormat(ServerConstants.SDF_FORMAT_FOLLOWUP);
        int interactionType = InteractionConstants.INTERACTION_TYPE_FOLLOWUP_CALL;
        String interactionNote = InteractionConstants.INTERACTION_NOTE_BLANK ;
        String interactionResult = "";
        try {
            if(candidate != null) {
                objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                uuId = candidate.getCandidateUUId();
                interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_FOLLOWED_UP_REQUEST + " " + sfdFollowUp.format(followUpSchedule);
            } else {
                Lead lead = LeadService.isLeadExists(followUpMobile);
                if(lead != null) {
                    objectAType = ServerConstants.OBJECT_TYPE_LEAD;
                    uuId = lead.getLeadUUId();
                    interactionResult = InteractionConstants.INTERACTION_RESULT_LEAD_FOLLOWED_UP_REQUEST + " " + sfdFollowUp.format(followUpSchedule);
                }
            }

            Interaction interaction = new Interaction(
                    uuId,
                    objectAType,
                    interactionType,
                    interactionNote,
                    interactionResult,
                    session().get("sessionUsername"),
                    INTERACTION_CHANNEL_SUPPORT_WEBSITE
            );

            InteractionService.createInteraction(interaction);
        } catch (NullPointerException npe){
            Logger.info("Followup deactivated");
        }
    }

    public static void createInteractionForJobApplicationViaWebsite(String objectAUUId, String objectBUUId, String result) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST,
                InteractionConstants.INTERACTION_TYPE_APPLIED_JOB,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForJobApplicationViaAndroid(String objectAUUId, String objectBUUId, String result) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST,
                InteractionConstants.INTERACTION_TYPE_APPLIED_JOB,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteraction(Interaction interaction){
        Interaction.addInteraction(interaction);
        Logger.info("Interaction saved");
    }

    public static void createInteractionForLoginCandidateViaWebsite(String objectAUUId) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                INTERACTION_TYPE_CANDIDATE_LOG_IN,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_SELF_SIGNEDIN,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForLoginCandidateViaAndroid(String objectAUUId) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                INTERACTION_TYPE_CANDIDATE_LOG_IN,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_SELF_SIGNEDIN,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForJobApplicationAttemptViaWebsite(String objectAUUId, String objectBUUId, String result) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST,
                InteractionConstants.INTERACTION_TYPE_TRIED_JOB_APPLY,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForDeactivateCandidate(String objectAUUId, boolean isSupport){
        if(isSupport){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    InteractionConstants.INTERACTION_TYPE_CANDIDATE_DEACTIVATED,
                    InteractionConstants.INTERACTION_NOTE_BLANK,
                    InteractionConstants.INTERACTION_RESULT_CANDIDATE_DEACTIVATED,
                    session().get("sessionUsername"),
                    INTERACTION_CHANNEL_SUPPORT_WEBSITE
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForActivateCandidate(String objectAUUId, boolean isSupport) {
        if(isSupport){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    InteractionConstants.INTERACTION_TYPE_CANDIDATE_ACTIVATED,
                    InteractionConstants.INTERACTION_NOTE_BLANK,
                    InteractionConstants.INTERACTION_RESULT_CANDIDATE_ACTIVATED,
                    session().get("sessionUsername"),
                    INTERACTION_CHANNEL_SUPPORT_WEBSITE
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForResetPasswordAttemptViaWebsite(String objectAUUId, String result){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_TRIED_PASSWORD_RESET,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForResetPasswordAttemptViaAndroid(String objectAUUId, String result){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_TRIED_PASSWORD_RESET,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateResetPasswordViaWebsite(String objectAUUId, String result){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_PASSWORD_RESET_SUCCESS,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateResetPasswordViaAndroid(String objectAUUId, String result){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_PASSWORD_RESET_SUCCESS,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateAddPasswordViaWebsite(String objectAUUId){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_PASSWORD_ADDED,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_NEW_CANDIDATE + " & " + InteractionConstants.INTERACTION_NOTE_SELF_PASSWORD_CHANGED,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateAddPasswordViaAndroid(String objectAUUId){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_PASSWORD_ADDED,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_NEW_CANDIDATE + " & " + InteractionConstants.INTERACTION_NOTE_SELF_PASSWORD_CHANGED,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateAlertService(String objectAUUId, String result){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_ALERT,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }
    public static void createInteractionForSearch(String objectAUUId, String result){
        Logger.info("Search Interaction Saved for UUID: " + objectAUUId);
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_SEARCH,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }
    public static void createInteractionForViewJobPostInfo(String objectAUUId, String objectBUUId, String result){
        Logger.info("View JobPost Info Interaction Saved for UUID: " + objectAUUId == null ? ServerConstants.TRU_DROID_NOT_LOGGED_UUID : objectAUUId);
        Interaction interaction = new Interaction(
                objectAUUId == null ? ServerConstants.TRU_DROID_NOT_LOGGED_UUID : objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objectBUUId,
                ServerConstants.OBJECT_TYPE_JOB_POST_VIEW,
                InteractionConstants.INTERACTION_TYPE_JOP_POST_VIEW,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    /* NEW INTERACTIONS */
    public static void createInteractionForSignUpCandidateViaWebsite(String objectAUUId, String result, Integer interactionType) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                interactionType,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);

    }

    public static void createInteractionForSignUpCandidateViaAndroid(String objectAUUId, String result, Integer interactionType) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                interactionType,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCreateCandidateProfileViaSupport(String objAuuId, String objBuuId, Integer objBType, Integer interactionType, String interactionNote, String interactionResult, String createdBy){
        Interaction interaction = new Interaction(
                objAuuId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBuuId,
                objBType,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy,
                INTERACTION_CHANNEL_SUPPORT_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCreateCandidateProfileViaPartner(String objAuuId, String objBuuId, Integer objBType, Integer interactionType, String interactionNote, String interactionResult, String createdBy){
        Interaction interaction = new Interaction(
                objAuuId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBuuId,
                objBType,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy + "(Partner)",
                INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCreateCandidateProfileViaAndroidByCandidate(String objAuuId, String objBuuId, Integer objBType, Integer interactionType, String interactionNote, String interactionResult){
        Interaction interaction = new Interaction(
                objAuuId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBuuId,
                objBType,
                interactionType,
                interactionNote,
                interactionResult,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCreateCandidateProfileViaWebsiteByCandidate(String objAuuId, String objBuuId, Integer objBType, Integer interactionType, String interactionNote, String interactionResult){
        Interaction interaction = new Interaction(
                objAuuId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBuuId,
                objBType,
                interactionType,
                interactionNote,
                interactionResult,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }
}

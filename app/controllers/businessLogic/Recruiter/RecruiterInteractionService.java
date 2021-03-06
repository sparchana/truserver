package controllers.businessLogic.Recruiter;

import api.InteractionConstants;
import api.ServerConstants;
import controllers.businessLogic.InteractionService;
import models.entity.Interaction;

import static api.InteractionConstants.*;
import static api.InteractionConstants.INTERACTION_NOTE_BLANK;

/**
 * Created by dodo on 19/10/16.
 */
public class RecruiterInteractionService {
    public static void createInteractionForRecruiterSignUp(String objectAUUId, String result, Integer interactionType, String createdBy) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                interactionType,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                createdBy,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterLogin(String objectAUUId) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_LOG_IN,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_RECRUITER_SIGNEDIN,
                InteractionConstants.INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterAddPasswordViaWebsite(String objectAUUId){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_PASSWORD_ADDED,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_NEW_RECRUITER + " & " + InteractionConstants.INTERACTION_NOTE_RECRUITER_PASSWORD_CHANGED,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterProfileUpdate(String uuId, String result, int channelType){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_PROFILE_UPDATE,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                InteractionConstants.INTERACTION_CHANNEL_MAP.get(channelType),
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterSearchCandidate(String uuId, String result){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_SEARCH_CANDIDATE,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterLead(String objectAUUId, String result, Integer interactionType) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUITER_LEAD,
                interactionType,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterUnlockCandidateContact(String objAUUID, String objBUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBUUID,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                INTERACTION_TYPE_RECRUITER_CONTACT_UNLOCK,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_RECRUITER_CONTACT_UNLOCK,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterCreditRequest(String uuId, String result){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_CREDIT_REQUEST,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterTriedToResetPassword(String objectAUUId, String result){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_TRIED_PASSWORD_RESET,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterShortlistJobApplicationWithoutDate(String objAUUID, String objBUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBUUID,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                INTERACTION_TYPE_RECRUITER_SHORTLIST_JOB_APPLICATION_INTERVIEW,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_RECRUITER_SHORTLISTS_JOB_INTERVIEW_WITHOUT_DATE,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterAcceptingInterviewDate(String objAUUID, String objBUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBUUID,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                INTERACTION_TYPE_RECRUITER_ACCEPT_JOB_APPLICATION_INTERVIEW,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_RECRUITER_ACCEPT_JOB_INTERVIEW_DATE,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterRejectingInterviewDate(String objAUUID, String objBUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBUUID,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                INTERACTION_TYPE_RECRUITER_REJECT_JOB_APPLICATION_INTERVIEW,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_RECRUITER_REJECT_JOB_INTERVIEW_DATE,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterReschedulingInterviewDate(String objAUUID, String objBUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                objBUUID,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                INTERACTION_TYPE_RECRUITER_RESCHEDULE_JOB_APPLICATION_INTERVIEW,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_RECRUITER_RESCHEDULE_JOB_INTERVIEW_DATE,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateAcceptingRescheduledInterviewViaWebsite(String objAUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_ACCEPTS_RESCHEDULED_INTERVIEW,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_CANDIDATE_ACCEPTS_RESCHEDULED_INTERVIEW,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateAcceptingRescheduledInterviewViaAndroid(String objAUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_ACCEPTS_RESCHEDULED_INTERVIEW,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_CANDIDATE_ACCEPTS_RESCHEDULED_INTERVIEW,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateRejectingRescheduledInterviewViaWebsite(String objAUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_REJECTS_RESCHEDULED_INTERVIEW,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_CANDIDATE_REJECTS_RESCHEDULED_INTERVIEW,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForCandidateRejectingRescheduledInterviewViaAndroid(String objAUUID) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_REJECTS_RESCHEDULED_INTERVIEW,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_CANDIDATE_REJECTS_RESCHEDULED_INTERVIEW,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_CANDIDATE_ANDROID
        );
        InteractionService.createInteraction(interaction);
    }
}

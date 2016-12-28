package dao;

import models.entity.Recruiter.RecruiterProfile;
import models.entity.RecruiterCreditHistory;

/**
 * Created by dodo on 28/12/16.
 */
public class RecruiterCreditHistoryDAO {
    public static RecruiterCreditHistory getRecruiterLatestCreditHistoryById(RecruiterProfile recruiterProfile, Integer categoryId){
        return RecruiterCreditHistory.find.where()
                .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                .eq("RecruiterCreditCategory", categoryId)
                .setMaxRows(1)
                .orderBy("create_timestamp desc")
                .findUnique();
    }
}

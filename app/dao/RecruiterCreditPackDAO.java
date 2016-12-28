package dao;

import api.ServerConstants;
import models.entity.Recruiter.RecruiterCreditPack;
import models.entity.Recruiter.RecruiterProfile;

import java.util.Calendar;
import java.util.List;

/**
 * Created by dodo on 28/12/16.
 */
public class RecruiterCreditPackDAO {
    public static List<RecruiterCreditPack> getInterviewCreditPackById(RecruiterProfile recruiterProfile, Integer id){
        return RecruiterCreditPack.find.where()
                .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                .eq("RecruiterCreditCategory", id)
                .findList();
    }

    public static RecruiterCreditPack getLatestCreditPack(RecruiterProfile recruiterProfile){
        return RecruiterCreditPack.find.where()
                .eq("recruiterProfileId", recruiterProfile.getRecruiterProfileId())
                .setMaxRows(1)
                .orderBy().desc("create_timestamp")
                .findUnique();
    }

    public static List<RecruiterCreditPack> getAllPacksExpiringToday(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);

        String dateToday = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH)+1) + "-" + today.get(Calendar.DATE);

        return RecruiterCreditPack.find.where()
                .eq("expiryDate", dateToday)
                .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                .findList();
    }
}

package dao;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.RecruiterCreditHistory;
import play.Logger;

import java.util.List;

/**
 * Created by dodo on 29/12/16.
 */
public class RecruiterCreditHistoryDAO {
    public static RecruiterCreditHistory getLatestRecruiterCreditPack(Integer creditType){

        String workFlowQueryBuilder = "select distinct recruiter_credit_pack_no, recruiter_credits_available, " +
                "recruiter_credits_used, expiry_date, credit_is_expired, is_latest" +
                " FROM trujobsdev.recruiter_credit_history where is_latest = '1' and credit_is_expired = '0' and" +
                " recruitercreditcategory = " + creditType +
                " order by recruiter_credit_pack_no desc";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("recruiter_credit_pack_no", "recruiterCreditPackNo")
                .columnMapping("recruiter_credits_available", "recruiterCreditsAvailable")
                .columnMapping("recruiter_credits_used", "recruiterCreditsUsed")
                .columnMapping("expiry_date", "expiryDate")
                .columnMapping("credit_is_expired", "creditIsExpired")
                .columnMapping("is_latest", "isLatest")
                .create();

        return Ebean.find(RecruiterCreditHistory.class)
                .setRawSql(rawSql)
                .setMaxRows(1)
                .findUnique();
    }

    public static List<RecruiterCreditHistory> getAllRecruiterPack(RecruiterProfile recruiterProfile, Integer creditType){

        String workFlowQueryBuilder = "select distinct recruiter_credit_pack_no, recruiter_credit_history_id, recruiter_credits_available, " +
                "recruiter_credits_used, expiry_date, credit_is_expired, is_latest" +
                " FROM trujobsdev.recruiter_credit_history where is_latest = '1' and credit_is_expired = '0' and" +
                " recruitercreditcategory = " + creditType +
                " and recruiterprofileid = " + recruiterProfile.getRecruiterProfileId() +
                " and recruiter_credit_pack_no is not null" +
                " order by recruiter_credit_pack_no";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("recruiter_credit_pack_no", "recruiterCreditPackNo")
                .columnMapping("recruiter_credit_history_id", "recruiterCreditHistoryId")
                .columnMapping("recruiter_credits_available", "recruiterCreditsAvailable")
                .columnMapping("recruiter_credits_used", "recruiterCreditsUsed")
                .columnMapping("expiry_date", "expiryDate")
                .columnMapping("credit_is_expired", "creditIsExpired")
                .columnMapping("is_latest", "isLatest")
                .create();

        return Ebean.find(RecruiterCreditHistory.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static RecruiterCreditHistory getLatestRecruiterCreditPackDetails(RecruiterProfile recruiterProfile){

        return RecruiterCreditHistory.find.where()
                .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                .orderBy().desc("recruiter_credit_pack_no").setMaxRows(1).findUnique();
    }


}

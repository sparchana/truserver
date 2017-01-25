package dao;

import models.entity.OM.SmsReport;
import models.entity.Static.SmsDeliveryStatus;

import java.util.List;

/**
 * Created by dodo on 20/1/17.
 */
public class SmsReportDAO {
    public static List<SmsReport> getAllSMSByStatus(SmsDeliveryStatus status){
        return SmsReport.find.where()
                .eq("SmsDeliveryStatus", status.getStatusId())
                .findList();
    }

    public static int getTotalSMSByRecruiterNJobPost(Long recruiterId, Long jobPostId){
        return SmsReport.find.where()
                .eq("recruiterProfile.recruiterProfileId", recruiterId)
                .eq("jobPost.jobPostId", jobPostId)
                .findList().size();
    }
}

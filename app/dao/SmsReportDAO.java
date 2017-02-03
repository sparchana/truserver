package dao;

import api.ServerConstants;
import models.entity.OM.SmsReport;
import models.entity.Static.SmsDeliveryStatus;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static List<SmsReport> getAllSMSByStatusSinceLastOneDay(SmsDeliveryStatus status){
        final SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        Date date = DateUtils.addDays(new Date(), -1);
        String convertedDate = sdf.format(date);

        return SmsReport.find.where()
                .eq("SmsDeliveryStatus", status.getStatusId())
                .ge("creation_timestamp", convertedDate)
                .findList();
    }

    public static int getTotalSMSByRecruiterNJobPost(Long recruiterId, Long jobPostId){
        return SmsReport.find.where()
                .eq("recruiterProfile.recruiterProfileId", recruiterId)
                .eq("jobPost.jobPostId", jobPostId)
                .findList().size();
    }
}

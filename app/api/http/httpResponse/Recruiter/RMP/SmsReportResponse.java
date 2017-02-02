package api.http.httpResponse.Recruiter.RMP;

import models.entity.OM.SmsReport;

import java.util.List;

/**
 * Created by dodo on 25/1/17.
 */
public class SmsReportResponse {
    List<SmsReport> smsReportList;
    Integer totalSms;

    public List<SmsReport> getSmsReportList() {
        return smsReportList;
    }

    public void setSmsReportList(List<SmsReport> smsReportList) {
        this.smsReportList = smsReportList;
    }

    public Integer getTotalSms() {
        return totalSms;
    }

    public void setTotalSms(Integer totalSms) {
        this.totalSms = totalSms;
    }
}

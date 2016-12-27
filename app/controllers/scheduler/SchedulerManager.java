package controllers.scheduler;

import controllers.scheduler.task.*;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import play.Logger;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


/**
 * Created by zero on 8/12/16.
 *
 * TODO Improve this to Implement Java 8 ScheduledExecutorService
 * http://stackoverflow.com/questions/20387881/how-to-run-certain-task-every-day-at-a-particular-time-using-scheduledexecutorse
 */
public class SchedulerManager implements Runnable {


    private final Timer timer = new Timer(); // Instantiate Timer Object
    private final long oneDay = 24 * 1000 * 60 * 60; // 24 hr


    @Override
    public void run() {
        int mEODMailTaskStartHr = (play.Play.application().configuration().getInt("schedulertask.eod.mail.start.hr"));
        int mEODMailTaskStartMin = (play.Play.application().configuration().getInt("schedulertask.eod.mail.start.min"));
        int mEODMailTaskStartSec = (play.Play.application().configuration().getInt("schedulertask.eod.mail.start.sec"));

        int mEODNextDayInterviewTaskStartHr = (play.Play.application().configuration().getInt("schedulertask.eod.ndi.start.hr"));
        int mEODNextDayInterviewStartMin = (play.Play.application().configuration().getInt("schedulertask.eod.ndi.start.min"));
        int mEODNextDayInterviewStartSec = (play.Play.application().configuration().getInt("schedulertask.eod.ndi.start.sec"));

        int mEODAadhaarTaskStartHr = (play.Play.application().configuration().getInt("schedulertask.eod.aadhaar.verification.start.hr"));
        int mEODAadhaarTaskStartMin = (play.Play.application().configuration().getInt("schedulertask.eod.aadhaar.verification.start.min"));
        int mEODAadhaarTaskStartSec = (play.Play.application().configuration().getInt("schedulertask.eod.aadhaar.verification.start.sec"));

        int mSODJobPostInfoStartHr = (play.Play.application().configuration().getInt("schedulertask.sod.jobpost.notifier.start.hr"));
        int mSODJobPostInfoStartMin = (play.Play.application().configuration().getInt("schedulertask.sod.jobpost.notifier.start.min"));
        int mSODJobPostInfoStartSec = (play.Play.application().configuration().getInt("schedulertask.sod.jobpost.notifier.start.sec"));

        int mEODRateUsPostInterviewHr = (play.Play.application().configuration().getInt("schedulertask.eod.rateus.notifier.start.hr"));
        int mEODRateUsPostInterviewMin = (play.Play.application().configuration().getInt("schedulertask.eod.rateus.notifier.start.min"));
        int mEODRateUsPostInterviewSec = (play.Play.application().configuration().getInt("schedulertask.eod.rateus.notifier.start.sec"));

        int sameDayInterviewAlertEventPeriod = Integer.parseInt(play.Play.application().configuration().getString("schedulertask.sameDay.alert.period"));

        long eodMailDelay = computeDelay(mEODMailTaskStartHr, mEODMailTaskStartMin , mEODMailTaskStartSec);
        long ndiMailDelay = computeDelay(mEODNextDayInterviewTaskStartHr, mEODNextDayInterviewStartMin , mEODNextDayInterviewStartSec);
        long aadhaarVerificationDelay = computeDelay(mEODAadhaarTaskStartHr, mEODAadhaarTaskStartMin , mEODAadhaarTaskStartSec);

        long jobPostInfoDelay = computeDelay(mSODJobPostInfoStartHr, mSODJobPostInfoStartMin , mSODJobPostInfoStartSec);
        long rateUsPostInterviewDelay = computeDelay(mEODRateUsPostInterviewHr, mEODRateUsPostInterviewMin , mEODRateUsPostInterviewSec);


        // createSameDayInterviewAlertEvent method takes time period (in hrs) as input
        createSameDayInterviewAlertEvent(sameDayInterviewAlertEventPeriod);

        createNextDayInterviewAlertEvent(ndiMailDelay);

        createRecruiterEODEmailAlertEvent(eodMailDelay);

        createAadhaarVerificationEvent(aadhaarVerificationDelay);

//        createStartOfTheDayJobPostEvent(jobPostInfoDelay);

//        createEODRateUsPostInterview(rateUsPostInterviewDelay);

//        createEODCreditDebitAndExpireInterviewCredit(aadhaarVerificationDelay);
    }

    private void createSameDayInterviewAlertEvent(int hr) {
        Logger.info("Same Day Interview Alert Event Scheduled!");
        if (hr < 1) return;

        long xHr = hr * 1000 * 60 * 60; // 3 hr

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        SameDayInterviewAlertTask sameDayInterviewTask = new SameDayInterviewAlertTask(hr, classLoader);
        timer.schedule(sameDayInterviewTask, 0, xHr);
    }

    private void createNextDayInterviewAlertEvent(long delay) {
        Logger.info("Next Day Interview Alert Event Scheduled!");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        NextDayInterviewAlertTask nextDayInterviewAlertTask = new NextDayInterviewAlertTask(classLoader);
        timer.schedule(nextDayInterviewAlertTask, delay, oneDay);
    }

    private void createRecruiterEODEmailAlertEvent(long delay){
        Logger.info("Recruiter EOD Email Alert Event Scheduled!");

        EODRecruiterEmailAlertTask eodRecruiterEmailAlertTask = new EODRecruiterEmailAlertTask();
        timer.schedule(eodRecruiterEmailAlertTask, delay, oneDay);
    }

    private void createAadhaarVerificationEvent(long delay){
        Logger.info("Aadhaar Verification Event Scheduled!");

        EODAadhaarVerificationTask eodAadhaarVerificationTask = new EODAadhaarVerificationTask();
        timer.schedule(eodAadhaarVerificationTask, delay, oneDay);
    }

    private void createStartOfTheDayJobPostEvent(long delay){
        Logger.info("Send job post message to candidate!");

        SODNotifyCandidateAboutJobPostTask sodNotifyCandidateAboutJobPostTask = new SODNotifyCandidateAboutJobPostTask();
        timer.schedule(sodNotifyCandidateAboutJobPostTask, delay, oneDay);
    }

    private void createEODRateUsPostInterview(long delay){
        Logger.info("Send alert to rate on play store after interview to candidate!");

        EODCandidateCompletedInterviewTask eodCandidateCompletedInterviewTask = new EODCandidateCompletedInterviewTask();
        timer.schedule(eodCandidateCompletedInterviewTask, delay, oneDay);
    }

    private void createEODCreditDebitAndExpireInterviewCredit(long delay){
        Logger.info("Auto debit interview credit + auto credit interview credit if feedback provided and expire interview credits" +
                " which needs to be expired");

        EODDebitInterviewCreditTask eodDebitInterviewCreditTask = new EODDebitInterviewCreditTask();
        timer.schedule(eodDebitInterviewCreditTask, delay, oneDay);
    }


    public static void saveNewSchedulerStats(Timestamp startTime, SchedulerType schedulerType,
                                             SchedulerSubType schedulerSubType,
                                             String note, Timestamp endTime, boolean status){
        // make entry in db for this.
        SchedulerStats newSchedulerStats = new SchedulerStats();
        newSchedulerStats.setStartTimestamp(startTime);
        newSchedulerStats.setCompletionStatus(status);
        newSchedulerStats.setEndTimestamp(endTime);

        newSchedulerStats.setSchedulerType(schedulerType);
        newSchedulerStats.setSchedulerSubType(schedulerSubType);

        newSchedulerStats.setNote(note);
        newSchedulerStats.save();
    }


    public static boolean checkIfEODTaskShouldRun(int type, int subType){
        Calendar newCalendar = Calendar.getInstance();
        Date today = newCalendar.getTime();
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", type)
                .eq("schedulerSubType.schedulerSubTypeId", subType)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            shouldRunThisTask = true;

        } else {
            if((schedulerStats.getStartTimestamp().getDate() < (today).getDate())) {
                // last run was 'x++' hr back, hence re run
                shouldRunThisTask = true;
            }
        }
        return shouldRunThisTask;
    }

    public long computeDelay(int hour, int min, int sec) {

        if(min>60) {
            ++hour;
            min -= 60;
        }
        if(sec>60) {
            ++min;
            sec -= 60;
        }

        Calendar cal = Calendar.getInstance();

        min = min - cal.getTime().getMinutes() ;
        if(min < 0) {
            --hour;
            min += 60;
        }

        hour = hour - cal.getTime().getHours();

        // start time already passed hence delay for a day
        if(hour < 0) {
            hour += 24;
        }

        return (hour * 60 * 60 + min * 60 + sec) * 1000;
    }
}

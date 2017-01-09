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
    private final long oneWeek = oneDay * 7; // 1 week


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

        int mEODJobPostInfoStartHr = (play.Play.application().configuration().getInt("schedulertask.eod.jobalert.notifier.start.hr"));
        int mEODJobPostInfoStartMin = (play.Play.application().configuration().getInt("schedulertask.eod.jobalert.notifier.start.min"));
        int mEODJobPostInfoStartSec = (play.Play.application().configuration().getInt("schedulertask.eod.jobalert.notifier.start.sec"));

        int mEODRateUsPostInterviewHr = (play.Play.application().configuration().getInt("schedulertask.eod.rateus.notifier.start.hr"));
        int mEODRateUsPostInterviewMin = (play.Play.application().configuration().getInt("schedulertask.eod.rateus.notifier.start.min"));
        int mEODRateUsPostInterviewSec = (play.Play.application().configuration().getInt("schedulertask.eod.rateus.notifier.start.sec"));

        int sameDayInterviewAlertEventPeriod = Integer.parseInt(play.Play.application().configuration().getString("schedulertask.sameDay.alert.period"));

        int mSODJobPostInfoStartHr = (play.Play.application().configuration().getInt("schedulertask.sod.jobpost.notifier.start.hr"));
        int mSODJobPostInfoStartMin = (play.Play.application().configuration().getInt("schedulertask.sod.jobpost.notifier.start.min"));
        int mSODJobPostInfoStartSec = (play.Play.application().configuration().getInt("schedulertask.sod.jobpost.notifier.start.sec"));

        int mSODCandidateActivationStartHr = (play.Play.application().configuration().getInt("schedulertask.sod.candidate.activation.start.hr"));
        int mSODCandidateActivationStartMin = (play.Play.application().configuration().getInt("schedulertask.sod.candidate.activation.start.min"));
        int mSODCandidateActivationStartSec = (play.Play.application().configuration().getInt("schedulertask.sod.candidate.activation.start.sec"));

        int mWeeklyNotifyAppDownloadDay = (play.Play.application().configuration().getInt("schedulertask.weekly.appdownload.notifier.start.day"));
        int mWeeklyNotifyAppDownloadHr = (play.Play.application().configuration().getInt("schedulertask.weekly.appdownload.notifier.start.hr"));
        int mWeeklyNotifyAppDownloadMin = (play.Play.application().configuration().getInt("schedulertask.weekly.appdownload.notifier.start.min"));
        int mWeeklyNotifyAppDownloadSec = (play.Play.application().configuration().getInt("schedulertask.weekly.appdownload.notifier.start.sec"));

        int mWeeklyProfileCompletionDay = (play.Play.application().configuration().getInt("schedulertask.weekly.profilecompletion.notifier.start.day"));
        int mWeeklyProfileCompletionHr = (play.Play.application().configuration().getInt("schedulertask.weekly.profilecompletion.notifier.start.hr"));
        int mWeeklyProfileCompletionMin = (play.Play.application().configuration().getInt("schedulertask.weekly.profilecompletion.notifier.start.min"));
        int mWeeklyProfileCompletionSec = (play.Play.application().configuration().getInt("schedulertask.weekly.profilecompletion.notifier.start.sec"));

        long eodMailDelay = computeDelay(mEODMailTaskStartHr, mEODMailTaskStartMin , mEODMailTaskStartSec);
        long ndiMailDelay = computeDelay(mEODNextDayInterviewTaskStartHr, mEODNextDayInterviewStartMin , mEODNextDayInterviewStartSec);
        long aadhaarVerificationDelay = computeDelay(mEODAadhaarTaskStartHr, mEODAadhaarTaskStartMin , mEODAadhaarTaskStartSec);

        long jobPostInfoDelay = computeDelay(mSODJobPostInfoStartHr, mSODJobPostInfoStartMin , mSODJobPostInfoStartSec);
        long eodJobPostInfoDelay = computeDelay(mEODJobPostInfoStartHr, mEODJobPostInfoStartMin, mEODJobPostInfoStartSec);
        long sodActivationDelay = computeDelay(mSODCandidateActivationStartHr, mSODCandidateActivationStartMin, mSODCandidateActivationStartSec);

        long rateUsPostInterviewDelay = computeDelay(mEODRateUsPostInterviewHr, mEODRateUsPostInterviewMin , mEODRateUsPostInterviewSec);

        long sdiDelay = computeDelayForSDI(sameDayInterviewAlertEventPeriod);

        // weekly tasks
        long weeklyCandidateAlertTaskDelay = computeDelayForWeeklyTask(mWeeklyNotifyAppDownloadDay, mWeeklyNotifyAppDownloadHr,
                mWeeklyNotifyAppDownloadMin , mWeeklyNotifyAppDownloadSec);

        long weeklyProfileCompletionTaskDelay = computeDelayForWeeklyTask(mWeeklyProfileCompletionDay, mWeeklyProfileCompletionHr,
                mWeeklyProfileCompletionMin , mWeeklyProfileCompletionSec);

        // createSameDayInterviewAlertEvent method takes time period (in hrs) as input
        createSameDayInterviewAlertEvent(sameDayInterviewAlertEventPeriod, sdiDelay);

        createNextDayInterviewAlertEvent(ndiMailDelay);

        createRecruiterEODEmailAlertEvent(eodMailDelay);

        createAadhaarVerificationEvent(aadhaarVerificationDelay);

        createStartOfTheDayJobPostEvent(jobPostInfoDelay);

        createEndOfTheDayJobPostEvent(eodJobPostInfoDelay);

        createEODRateUsPostInterviewEvent(rateUsPostInterviewDelay);

        createWeeklyAlertEvent(weeklyCandidateAlertTaskDelay);

        createWeeklyProfileCompletionEvent(weeklyProfileCompletionTaskDelay);

        createSODCandidateActivationEvent(sodActivationDelay);

    }

    public void createSameDayInterviewAlertEvent(int periodInHr, long delay) {
        Logger.info("Same Day Interview Alert Event Scheduled!");
        if (periodInHr < 1) return;

        long xHr = periodInHr * 1000 * 60 * 60; // 3 hr

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        SameDayInterviewAlertTask sameDayInterviewTask = new SameDayInterviewAlertTask(periodInHr, classLoader);
        timer.schedule(sameDayInterviewTask, delay, xHr);
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

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        SODJobPostNotificationTask SODJobPostNotificationTask = new SODJobPostNotificationTask(classLoader);
        timer.schedule(SODJobPostNotificationTask, delay, oneDay);
    }

    private void createEODRateUsPostInterviewEvent(long delay){
        Logger.info("Send alert to rate on play store after interview to candidate!");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        EODCandidateCompletedInterviewTask eodCandidateCompletedInterviewTask = new EODCandidateCompletedInterviewTask(classLoader);
        timer.schedule(eodCandidateCompletedInterviewTask, delay, oneDay);
    }

    private void createWeeklyAlertEvent(long delay){
        Logger.info("Send alert candidate who have not downloaded the app yet!");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        WeeklyCandidateAlertTask weeklyCandidateAlertTask = new WeeklyCandidateAlertTask(classLoader);
        timer.schedule(weeklyCandidateAlertTask, delay, oneWeek);
    }

    private void createWeeklyProfileCompletionEvent(long delay){
        Logger.info("Send alert to candidate to complete profile!");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        WeeklyCompleteProfileAlertTask weeklyCompleteProfileAlertTask = new WeeklyCompleteProfileAlertTask(classLoader);
        timer.schedule(weeklyCompleteProfileAlertTask, delay, oneWeek);
    }

    private void createEndOfTheDayJobPostEvent(long delay){
        Logger.info("Send job post fcm notification to candidate!");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        EODJobAlertFcmTask eodJobAlertFcmTask = new EODJobAlertFcmTask(classLoader);
        timer.schedule(eodJobAlertFcmTask, delay, oneDay);
    }

    private void createSODCandidateActivationEvent(long delay){
        Logger.info("Re-Activate candidates, due next day!");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        SODCandidateActivationTask SODCandidateActivationTask = new SODCandidateActivationTask(classLoader);
        timer.schedule(SODCandidateActivationTask, delay, oneDay);
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
            if((schedulerStats.getStartTimestamp().before(today) || schedulerStats.getStartTimestamp().equals(today))) {
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

    public long computeDelayForWeeklyTask(int day, int hour, int min, int sec) {

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
            day += 6;
        }

        day = day - cal.get(Calendar.DAY_OF_WEEK);
        if(day < 0){
            day += 6;
        }

        return ((day * 60 * 60 * 24) + hour * 60 * 60 + min * 60 + sec) * 1000;
    }

    public long computeDelayForSDI(int period) {

        Calendar cal = Calendar.getInstance();
        // current time
        int hour = cal.getTime().getHours() ;
        int min = cal.getTime().getMinutes() ;
        int sec = cal.getTime().getSeconds() ;

        int delayedHr = 0;
        int delayedMin = 0;
        int delayedSec = 0;

        // this should run at 7(10), 10(1), 1(4)
        // 4 + 3 = 7
        if (hour > 13){
            // delay by 24 - currentHour + 7
            delayedHr =  24 - hour + 10 - period;
        } else if(hour > 10) {
            delayedHr = 16 - hour - period;
        } else if(hour > 7) {
            delayedHr = 13 - hour - period;
        } else if(hour < 7){
            delayedHr = 7 - hour;
        } else {
            delayedHr = 0;
        }

        if(delayedHr!= 0 && min > 0) {
            delayedMin = 60 - min;
            delayedHr-- ;
        }

        return (delayedHr * 60 * 60 + delayedMin * 60) * 1000;
    }
}

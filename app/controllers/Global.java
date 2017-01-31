package controllers;

import api.ServerConstants;
import controllers.scheduler.SchedulerManager;
import notificationService.NotificationHandler;
import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * Created by dodo on 12/12/16.
 */
public class Global extends GlobalSettings {

    private static NotificationHandler mNotificationHandler;

    public void onStart(play.Application app) {
        boolean notificationHandlerShouldRun = (play.Play.application().configuration().getBoolean("notification.handler.run"));
        boolean schedulerManagerShouldRun = (play.Play.application().configuration().getBoolean("scheduler.manager.run"));


        ServerConstants.BASE_URL =
                play.Play.application().configuration().getInt("base.url.code") == 0 ?
                        play.Play.application().configuration().getString("base.url.trujobs") : play.Play.application().configuration().getInt("base.url.code") == 1?
                        play.Play.application().configuration().getString("base.url.trutest") :
                        play.Play.application().configuration().getString("base.url.localhost");

        Logger.warn("[Conf] Global base url set : " + ServerConstants.BASE_URL);

        //mNotificationHandler class instantiated
        mNotificationHandler = new NotificationHandler();
        SchedulerManager mSchedulerManager = new SchedulerManager();

        //started the thread
        if(notificationHandlerShouldRun) {
            Logger.warn("[Conf] Notification Handler started");
            new Thread(mNotificationHandler).start();

            printOutBoundRuleStatus();

            if(schedulerManagerShouldRun){
                Logger.warn("[Conf] Scheduler Manager started");
                new Thread(mSchedulerManager).start();
            } else {
                Logger.warn("[Conf] Scheduler Manager not running");
            }

        } else {
            Logger.warn("[Conf] Notification Handler not running");
            Logger.warn("[Conf] Scheduler Manager not running");
        }

        Logger.info("Global settings started");
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    public static NotificationHandler getmNotificationHandler() {
        return mNotificationHandler;
    }

    public static void setmNotificationHandler(NotificationHandler mNotificationHandler) {
        Global.mNotificationHandler = mNotificationHandler;
    }

    public static void printOutBoundRuleStatus() {
        boolean outboundSMS = (play.Play.application().configuration().getBoolean("outbound.sms.enabled"));
        boolean outboundFCM = (play.Play.application().configuration().getBoolean("outbound.fcm.enabled"));
        boolean outboundEmail = (play.Play.application().configuration().getBoolean("outbound.email.enabled"));

        if(outboundSMS) {
            Logger.warn("[Cong] OutBound sms enabled");
        } else {
            Logger.warn("[Cong] OutBound sms Disabled");
        }
        if(outboundEmail) {
            Logger.warn("[Cong] OutBound email enabled");
        } else {
            Logger.warn("[Cong] OutBound email Disabled");
        }
        if(outboundFCM) {
            Logger.warn("[Cong] OutBound fcm enabled");
        } else {
            Logger.warn("[Cong] OutBound fcm Disabled");
        }
    }

}
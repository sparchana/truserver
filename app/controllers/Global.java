package controllers;

import controllers.scheduler.SchedulerManager;
import notificationService.NotificationHandler;
import play.*;
import play.Application;

/**
 * Created by dodo on 12/12/16.
 */
public class Global extends GlobalSettings {

    private static NotificationHandler mNotificationHandler;

    public void onStart(play.Application app) {
        boolean notificationHandlerShouldRun = (play.Play.application().configuration().getBoolean("notification.handler.run"));
        boolean schedulerManagerShouldRun = (play.Play.application().configuration().getBoolean("scheduler.manager.run"));

        //mNotificationHandler class instantiated
        mNotificationHandler = new NotificationHandler();
        SchedulerManager mSchedulerManager = new SchedulerManager();

        //started the thread
        if(notificationHandlerShouldRun) {
            Logger.warn("[Conf] Notification Handler started");
            new Thread(mNotificationHandler).start();

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
}
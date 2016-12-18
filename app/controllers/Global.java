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
        //mNotificationHandler class instantiated
        mNotificationHandler = new NotificationHandler();
        SchedulerManager mSchedulerManager = new SchedulerManager();

        //started the thread
        new Thread(mNotificationHandler).start();
        new Thread(mSchedulerManager).start();

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
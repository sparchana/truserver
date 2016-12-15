package controllers;

import controllers.scheduler.SchedulerManager;
import notificationService.NotificationHandler;
import play.*;
import play.Application;

/**
 * Created by dodo on 12/12/16.
 */
public class Global extends GlobalSettings {

    private NotificationHandler myNotificationHandler;
    private SchedulerManager mSchedulerManager;

    public void onStart(play.Application app) {
        //myNotificationHandler class instantiated
        myNotificationHandler = new NotificationHandler();
        mSchedulerManager = new SchedulerManager();

        //started the thread
        new Thread(myNotificationHandler).start();
        new Thread(mSchedulerManager).start();

        Logger.info("Global settings started");
        SharedSettings.setGlobalSettings(this);
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    public NotificationHandler getMyNotificationHandler() {
        return myNotificationHandler;
    }

    public void setMyNotificationHandler(NotificationHandler myNotificationHandler) {
        this.myNotificationHandler = myNotificationHandler;
    }
}
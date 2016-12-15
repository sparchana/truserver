package controllers;

import notificationService.NotificationHandler;
import play.*;
import play.Application;

/**
 * Created by dodo on 12/12/16.
 */
public class Global extends GlobalSettings {

    private static NotificationHandler myNotificationHandler;

    public void onStart(play.Application app) {
        //myNotificationHandler class instantiated
        myNotificationHandler = new NotificationHandler();

        //started the thread
        new Thread(myNotificationHandler).start();

        Logger.info("Application has started");
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    public static NotificationHandler getMyNotificationHandler() {
        return myNotificationHandler;
    }

    public static void setMyNotificationHandler(NotificationHandler myNotificationHandler) {
        Global.myNotificationHandler = myNotificationHandler;
    }
}
package notificationService;

import controllers.scheduler.SchedulerManager;
import play.*;

/**
 * Created by dodo on 12/12/16.
 */
public class Global extends GlobalSettings {

    private NotificationHandler notificationHandler;
    private SchedulerManager schedulerManager;

    public void onStart(Application app) {
        //notificationHandler class instantiated
        notificationHandler = new NotificationHandler();
        schedulerManager = new SchedulerManager();


        //started the thread
        new Thread(notificationHandler).start();
        new Thread(schedulerManager).start();

        Logger.info("Application has started");
        Shared.setGlobalSettings(this);
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public void setNotificationHandler(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }
}
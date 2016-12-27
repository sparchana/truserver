package notificationService;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dodo on 12/12/16.
 */
public class NotificationHandler implements Runnable {
    private LinkedBlockingQueue<NotificationEvent> queue;

    public NotificationHandler() {
        queue = new LinkedBlockingQueue<>();
    }

    public void addToQueue(NotificationEvent event) {
        queue.add(event);
    }

    @Override
    public void run() {
        while (true) {
            try {
                NotificationEvent notificationEvent = queue.take();
                notificationEvent.send();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

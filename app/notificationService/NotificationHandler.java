package notificationService;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by dodo on 12/12/16.
 */
public class NotificationHandler implements Runnable {
    ConcurrentLinkedQueue<NotificationEvent> clq;

    public NotificationHandler() {
        clq = new ConcurrentLinkedQueue<NotificationEvent>();
    }

    public void addToQueue(NotificationEvent event){
        clq.add(event);
    }

    @Override
    public void run() {
        while (true){
            if(!clq.isEmpty()){
                NotificationEvent notificationEvent = clq.poll();
                notificationEvent.send();
            }
        }
    }
}

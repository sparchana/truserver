package models.util;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

import java.io.IOException;

/**
 * Created by dodo on 1/12/16.
 */
public class NotificationUtil {
    public void SendNotification(String messageText, String title, String token){
        final Sender sender = new Sender("AAAAYK9P22w:APA91bHF7nJZ7BPFYTAnNEYtnnjqRxJA11vzli3cVdmLwu5OeHadupdrX5zyDT4W1hFT-DtQRCemQfSR9lVmfcEfPk3uUGVyEAvxaIew1cBqtF1SANUFzjWp9j8aAyLJ0B7N3nZVr3rYkiLifQulkClwhwUi3cHJcQ");
        com.google.android.gcm.server.Result result = null;

        final Message message = new Message.Builder().timeToLive(30)
                .delayWhileIdle(true)
                .addData("title", title)
                .addData("message", messageText)
                .build();

        try {
            result = sender.send(message, token, 1);
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }
}
package project.final_year.opkomstadmin.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import project.final_year.opkomstadmin.Faculty.ChatActivity;
import project.final_year.opkomstadmin.MainActivity;
import project.final_year.opkomstadmin.R;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    String message_body, message_title, click_action;
    String sub_name, user, userId;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FROM", remoteMessage.getFrom());

            Log.e("Message Data", remoteMessage.getNotification().toString());
            message_title = remoteMessage.getNotification().getTitle();
            message_body = remoteMessage.getNotification().getBody();

            if(remoteMessage.getData().size()>0){
                sub_name = remoteMessage.getData().get("sub_name");
                user = remoteMessage.getData().get("user");
                userId = remoteMessage.getData().get("userId");
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                sendOreoNotification(message_body, message_title, sub_name, user, userId);
            }
            else {
                sendNotification(message_body, message_title, sub_name, user, userId);
            }



        /*if(remoteMessage.getNotification()!=null){
            Log.e("Message Body", remoteMessage.getNotification().getBody());
            String click_action = remoteMessage.getNotification().getClickAction();
            Log.e("Click", click_action);
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), click_action);
        }*/
    }

    private void sendNotification(String body, String title, String sub_name, String user, String userId) {
        Intent intent = new Intent(this, ChatActivity.class);
        ChatActivity.sub_name = sub_name;
        ChatActivity.user = user;
        ChatActivity.userId = userId;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0/*ID of Notification*/, notifiBuilder.build());
    }

    private void sendOreoNotification(String message_body, String message_title, String sub_name, String user, String userId) {


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder =  oreoNotification.getOreoNotification(message_title, message_body, pendingIntent,
                defaultSound);

        int i = 0;

        oreoNotification.getManager().notify(i, builder.build());
    }
}

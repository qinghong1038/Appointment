package com.gtaandteam.android.wellcure;

/**
 * Created by Glenn Alex on 24-Aug-18.
 */
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Map<String,String> Message;
    static String Activity="default";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        if(remoteMessage.getData().size() > 0){
            //handle the data message here
            Log.d("FirebaseMessageActivity", "Message data payload: " + remoteMessage.getData());
            Message=remoteMessage.getData();
            String activity="default";
            if(Message.containsKey("Action")) {
                activity = Message.get("Action");
                Log.d("FCMActivityMFMS", activity);
                //Toast.makeText(this, "You Have A New Update Pending in Play Store", Toast.LENGTH_SHORT).show();
                if (activity.equals("update")) {
                    final String appPackageName = "com.gtaandteam.android.wellcure"; // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

                    }
                }
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("FirebaseNotifActivity", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //getting the title and the body
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();


        //then here we can use the title and body to build a notification
    }
}

package com.example.kristian.fooduse;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("VitelWasser", "Es ist etwas angekommen!");
        String typ = "default";
        String payload = "default";
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        try {
            typ = object.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(typ.equals("Nachricht")){
            try {
                payload = object.getString("text");
                // Nachricht an Chat weiterleiter falls dieser aktiv ist oder eine falls nicht eine Notification erstellen

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            sendNotification(payload);
        }
    private void sendNotification(String payload){
        Notification notification = new NotificationCompat.Builder(getApplication(),BaseApplication.CHANNEL_1_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.frische_lebensmittel_icon)
                .setTicker("Achtung!")
                .setContentText(payload)
                .setContentTitle("FoodUse")
                .build();
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1,notification);
    }
}

package com.tfg.vjovenmo.cvnotificacions;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by teknival on 16/05/2016.
 */
public class GCMIntentService extends IntentService
{
    private static final int NOTIF_ALERTA_ID = 1;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    /*protected void onHandleIntent(Intent intent)
    {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                mostrarNotification(extras.getString("msg"));
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }*/

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String message = intent.getExtras().getString("gcm.notification.body");
        Log.i("INFO", "Received message >> " + message);
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            mostrarNotification(message);
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void mostrarNotification(String msg) {

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Login.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_cv)
                        .setContentTitle("Nota Practica")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
    }
}

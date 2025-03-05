package com.bbilandzi.diplomskiandroidapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bbilandzi.diplomskiandroidapp.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "simple_channel";
    private static final String CHANNEL_NAME = "Simple Notifications";

    public void createNotification(Context context) {
        createNotificationChannel(context);

        // Build the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Simple Notification")
                .setContentText("This is a basic notification.")
                .setSmallIcon(R.drawable.d20) // Replace with your icon
                .build();

        // Get the Notification Manager and notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = "Channel for simple notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.presentation.MainScreen;

public class NotificationAgent {

    private static final NotificationAgent instance = new NotificationAgent();
    private static final String TAG = NotificationAgent.class.getSimpleName();

    public static NotificationAgent getInstance() {
        return instance;
    }

    public void sendNotification(Context context) {
        NotificationCompat.Builder builder = getNotificationDefaultBuilder(context);
        // Get an instance of the Notification manager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Issue the notification
        notificationManager.notify(0, builder.build());
    }

    @NonNull
    private NotificationCompat.Builder getNotificationDefaultBuilder(Context context) {
        Log.d(TAG, "Sending Notification...");
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainScreen.class);
        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);
        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_message)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(notificationPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);
        // Dismiss notification once the user touches it.
        return builder;
    }
}

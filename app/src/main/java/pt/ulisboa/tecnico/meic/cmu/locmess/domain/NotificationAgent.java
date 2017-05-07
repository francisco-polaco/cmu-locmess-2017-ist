package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.presentation.MainScreen;

public class NotificationAgent {
    private static final NotificationAgent ourInstance = new NotificationAgent();
    private static final String TAG = NotificationAgent.class.getSimpleName();
    private static final long[] VIBRATION_PATTERN = new long[]{500, 500};


    private NotificationAgent() {
    }

    public static NotificationAgent getInstance() {
        return ourInstance;
    }

    public void sendNotification(Context context) {

        NotificationCompat.Builder builder = getNotificationDefaultBuilder(context);

        // Get an instance of the Notification manager
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    @NonNull
    private NotificationCompat.Builder getNotificationDefaultBuilder(Context context) {
        Log.d(TAG, "Sending Notification...");
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainScreen.class); // TODO change this to read activity
        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

       /* // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainScreen.class);*/

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_message)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(notificationPendingIntent);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        //builder.setVibrate(VIBRATION_PATTERN);
        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);
        return builder;
    }
}

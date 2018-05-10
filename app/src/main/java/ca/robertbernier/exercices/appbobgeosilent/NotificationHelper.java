package ca.robertbernier.exercices.appbobgeosilent;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx) {
        super(ctx);

        NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT);


        // Sets whether notifications posted to this channel should display notification lights
        chan1.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        chan1.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        chan1.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);




        getManager().createNotificationChannel(chan1);

        NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
        chan2.setLightColor(Color.BLUE);
        chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        // Sets whether notifications posted to this channel should display notification lights
        chan2.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        chan2.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        chan2.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        chan2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(chan2);
    }


    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.common_google_signin_btn_icon_light))
                .setColor(Color.RED)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true);
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotification2(String title, String body) {
        return new Notification.Builder(getApplicationContext(), SECONDARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.common_google_signin_btn_icon_light))
                .setColor(Color.BLUE)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return  R.drawable.common_google_signin_btn_icon_dark;
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}

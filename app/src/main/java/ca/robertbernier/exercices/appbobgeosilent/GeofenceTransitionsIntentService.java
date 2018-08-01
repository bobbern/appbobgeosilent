package ca.robertbernier.exercices.appbobgeosilent;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bob on 2017-03-30.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";
    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;
    public static AudioManager mAudioManager;
    private NotificationHelper noti;
    private Notification.Builder nb = null;
    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        noti = new NotificationHelper(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "onCreate "   );
        playSound(4);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String strInOrOut = "EMPTY";
        String strZone = "EMPTY";
        String strLongitude = "EMPTY";
        String  strLatitude = "EMPTY";

        playSound(10);
        String geofenceTransitionDetails ="";
        Log.i(TAG, "on handle intent "   );
        int geofenceTransition = 0;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError())
            {
                String errorMessage = GeofenceErrorMessages.getErrorString(this,
                        geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }

        // Get the transition type.

        geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Get the transition details as a String.
            geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
            }
        else
            {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
            }

        // set rigner oi or out job
        if (geofenceTransitionDetails.contains("GL"))
        {
            strZone     = "GL";
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            {
                strInOrOut  = "ENTREE";
                Log.d(TAG, "EXTRééé" );
                    try {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.RINGER_MODE_VIBRATE);
                    }
                    catch ( Exception xException) {

                    Log.e(TAG, "STRAM RING RINEGER MODE VIBE", xException);
                    }

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            }

            if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            {
                strInOrOut  = "SORTIE";
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 15, AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15,0);
            }
            strLongitude = (String.valueOf(geofencingEvent.getTriggeringLocation().getLongitude()));
            strLatitude = (String.valueOf(geofencingEvent.getTriggeringLocation().getLatitude()));
        }

        if (geofenceTransitionDetails.contains("EVAL"))
        {
            strZone     = "EVAL";
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            {
                strInOrOut  = "ENTREE";
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.RINGER_MODE_VIBRATE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            }

            if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
                {
                    strInOrOut  = "SORTIE";
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 15, AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15,0);
                }
            strLongitude = (String.valueOf(geofencingEvent.getTriggeringLocation().getLongitude()));
            strLatitude = (String.valueOf(geofencingEvent.getTriggeringLocation().getLatitude()));
        }

        if (geofenceTransitionDetails.contains("HOME"))
        {
            strZone     = "HOME";
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            {
                strInOrOut  = "ENTREE";
                playSound(4);
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 15, AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15,0);
            }
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            {
                strInOrOut  = "SORTIE";
                playSound(4);
            }

            strLongitude = (String.valueOf(geofencingEvent.getTriggeringLocation().getLongitude()));
            strLatitude = (String.valueOf(geofencingEvent.getTriggeringLocation().getLatitude()));
        }

        publishResults( strZone, strInOrOut, strLongitude, strLatitude );
    }
    // end  set rigner oi or out job


    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {


        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        // Define the notification settings.
//        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                // In a real app, you may want to use a library like Volley
//                // to decode the Bitmap.
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.common_google_signin_btn_icon_light))
//                .setColor(Color.RED)
//                .setContentTitle(notificationDetails)
//                .setContentText(getString(R.string.geofence_transition_notification_text))
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());

        nb = noti.getNotification2(notificationDetails + " " +  datetime, getString(R.string.geofence_transition_notification_text));
        nb.setContentIntent(notificationPendingIntent);
//        nb.setStyle(new Notification.BigTextStyle().bigText("Time : " + datetime)
//        .setBigContentTitle("BIG TITLE" + notificationDetails)
//        .setSummaryText("SUMMARY TEXT" + notificationDetails));



        if (nb != null) {
            noti.notify(NOTI_PRIMARY2, nb);
        }
    }




    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    void playSound(int duree){
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_RING, 400);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
    }
    void playSoundIN( ){
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 400);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING);
    }
    private void publishResults(String p_zone_name, String p_In_or_Out, String p_Longi, String p_Latit) {


        Intent intent = new Intent("1");
        intent.putExtra("p_zone_name", p_zone_name);
        intent.putExtra("p_In_or_Out", p_In_or_Out);
        intent.putExtra("p_Longi", p_Longi);
        intent.putExtra("p_Latit", p_Latit);
        sendBroadcast(intent);
    }

}

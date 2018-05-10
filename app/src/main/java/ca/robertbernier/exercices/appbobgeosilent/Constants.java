package ca.robertbernier.exercices.appbobgeosilent;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by bob on 2017-03-30.
 */

public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 1;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 50; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> SILENTS_AREAS_A_BOB = new HashMap<String, LatLng>();
    static {
        // EVAL
        SILENTS_AREAS_A_BOB.put("EVAL", new LatLng(45.5505188, -73.7436257));

        //  GL
        SILENTS_AREAS_A_BOB.put("GL", new LatLng(45.5807945,-73.705501));

        //  DOMICILE CASGRAIN
        SILENTS_AREAS_A_BOB.put("HOME", new LatLng(45.531811,-73.608006));


    }
}

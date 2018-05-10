package ca.robertbernier.exercices.appbobgeosilent;


import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemLongClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import android.content.BroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.PopupWindow;
import  android.view.LayoutInflater;

import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.app.Activity;
import android.view.Gravity;


public class MainActivity extends AppCompatActivity
        implements OnItemLongClickListener,
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>, ActivityCompat.OnRequestPermissionsResultCallback
{
    protected static final String TAG = "GEO_SILENT";
    //         * Provides the entry point to Google Play services.
    protected GoogleApiClient mGoogleApiClient;
    //  * The list of geofences used in this sample.
    protected ArrayList<Geofence> mGeofenceList;
    //   * Used to keep track of whether geofences were addedMAinActivityTOTOLocalisation.
    private boolean mGeofencesAdded;
    //   * Used when requesting to add or remove geofences.
    private PendingIntent mGeofencePendingIntent;
    //  * Used to persist application state about whether geofences were added.
    private SharedPreferences mSharedPreferences;
    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;
    private TextView mtxtLatitude;
    private TextView  mtxtLongitude;
    private TextView  mtxtRadius;
    private float mfRadius;
    public ListView mlvHisto;
    public ArrayAdapter madapter;
    public MySimpleArrayAdapter myAdapter;
    public ArrayList<LogEvenement> mlistItems;
    DBHelper dbhelper; // = new DBHelper(this);
    public SQLiteDatabase db;
    public boolean boSauteReadTable;



    private LinearLayout mLinearLayout;
    private ImageButton mButton;
    private PopupWindow mPopupWindow;

    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        mLinearLayout = (LinearLayout) findViewById(R.id.main) ;
        mButton = (ImageButton) findViewById(R.id.ib_close);


        // debugging au debut
        //   registerReceiver(br, new IntentFilter("1"));
        requestPermissions( new String[]{ Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},0);
        //     requestPermissions( new String[]{ Manifest.permission.READ_PHONE_STATE},0);
        //    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        dbhelper = new DBHelper(this);

        mlvHisto = (ListView) findViewById(R.id.lvHisto);

        mlistItems = new ArrayList<LogEvenement>();
        mlistItems.addAll(dbhelper.LoadLogs());

        //    madapter  = new ArrayAdapter<LogEvenement>(this, R.layout.simple_list_item_1 ,R.id.textView , mlistItems);
        myAdapter = new MySimpleArrayAdapter(this, mlistItems );

        mlvHisto.setAdapter(myAdapter);

        mlvHisto.setOnItemLongClickListener(this);
        // new OnItemClickListener()
//                {
//                    @Override
//                    public void onItemClick(AdapterView<?> arg0, View view,
//                                            int position, long id) {
//                        LogEvenement item = (LogEvenement) myAdapter.getItem(position);
//
//                       // Toast.makeText(this, item.m_i_evenement_ID  + " " + item.m_s_even + " selected", Toast.LENGTH_LONG).show();
//                        Toast.makeText(this, "kkjk");
//                                //Take action here.
//                    }
//                }
//        );


        boSauteReadTable = true;

        LogInEvenement("LOG  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>(onCreate) ");
        LogInEvenement( "   savedInstanceState null: " +  (savedInstanceState == null)) ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Reset DB Logs", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                dbhelper.resetDB();
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK   | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });
        mfRadius = Constants.GEOFENCE_RADIUS_IN_METERS;
        mtxtRadius = (TextView) findViewById(R.id.txtRadius);
        mtxtRadius.setText(String.valueOf(mfRadius));

        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Toast.makeText(this, String.format("permission " + permissionGranted), Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this,new String[]{  Manifest.permission.ACCESS_FINE_LOCATION } , 200);
        // Get the UI widgets.
        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);
        mtxtLatitude = (TextView) findViewById(R.id.txtLatitude);
        mtxtLongitude = (TextView) findViewById(R.id.txtLongitude);



        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;
        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        setButtonsEnabledState();
        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        //   addGeofence();

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        // if (!br.isOrderedBroadcast())
        //  registerReceiver(br, new IntentFilter("1"));

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mGoogleApiClient.disconnect();
        LogInEvenement(  "onStop ()" );
        InsLogdb();
    }

    protected void OnDestroy()
    {

    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(br, new IntentFilter("1"));
        LogInEvenement(  "onResume ()" );

        if (boSauteReadTable) {
            boSauteReadTable = false;
        }
        else {
            mlistItems = new ArrayList<LogEvenement>();
            mlistItems.addAll(dbhelper.LoadLogs());
            // madapter = new ArrayAdapter<LogEvenement>(this, R.layout.simple_list_item_1, R.id.textView, mlistItems);
            //    mlvHisto.setAdapter(madapter);
            myAdapter = new MySimpleArrayAdapter(this, mlistItems );
            mlvHisto.setAdapter(myAdapter);
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();
        unregisterReceiver(br);
        LogInEvenement(  "onPause ()" );

    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");

//        String[] StArr = new String[madapter.getCount() + 1];
//        for(int i=0 ; i<madapter.getCount() ; i++){
//            StArr [i]  = madapter.getItem(i).toString();
//        }
//            outState.putStringArray("lvLogs", StArr);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");

        //   mlistItems =   savedInstanceState.getStringArrayList("lvLogs");


    }

    // /*   //  * Runs when a GoogleApiClient object successfully connects.
    @Override
    public void onConnected(Bundle connectionHint) {
        LogInEvenement(  "onConnected ()" + "Connected to GoogleApiClient");
        Log.i(TAG, "Connected to GoogleApiClient");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null )
        {    LogInEvenement(  "onConnected  mLastLocation != null ");
            Toast.makeText(this, "latitude/ longitude :" + String.valueOf(mLastLocation.getLatitude()) + "/" + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            LogInEvenement(  "latitude/ longitude :" + String.valueOf(mLastLocation.getLatitude()) + "/" + String.valueOf(mLastLocation.getLongitude()));
            mtxtLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
            mtxtLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
            addGeofence();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    // * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
    protected synchronized void buildGoogleApiClient() {
        LogInEvenement(  "buildGoogleApiClient " );
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private GeofencingRequest getGeofencingRequest() {
        LogInEvenement(  "getGeofencingRequest " );
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        LogInEvenement(  "addGeofencesButtonHandler " );
        Toast.makeText(this, "debu boutton", Toast.LENGTH_SHORT).show();
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            LogInEvenement( getString(R.string.not_connected));
            return;
        }
        addGeofence();


    }

    private void addGeofence()
    {

        try {

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            LogInEvenement(  " private void addGeofence() securityException -> " + securityException.toString());
            logSecurityException(securityException);
        }

    }
    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        LogInEvenement(  "removeGeofencesButtonHandler ");
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        LogInEvenement(  " onResult(Status status) is success : " + status.isSuccess() +  " urn grey " );
        ConstraintLayout mconstraintLayout =  (ConstraintLayout)findViewById(R.id.constraintLayout);
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
            LogInEvenement(  getString(mGeofencesAdded ? R.string.geofences_added :
                    R.string.geofences_removed));


            //    LogInEvenement(  "  mconstraintLayout.setBackgroundColor(Color.GRAY);  ");
            //           mconstraintLayout.setBackgroundColor(Color.GRAY);
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
            mconstraintLayout.setBackgroundColor(Color.MAGENTA);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            LogInEvenement(  "mGeofencePendingIntent != null");
            return mGeofencePendingIntent;
        }
        LogInEvenement(  "new Intent(this, GeofenceTransitionsIntentService.class);   ");
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        LogInEvenement(  "populateGeofenceList ");
        for (Map.Entry<String, LatLng> entry : Constants.SILENTS_AREAS_A_BOB.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            mfRadius
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)   //  .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT )

                    // Create the geofence.
                    .build());
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
            LogInEvenement(  "mGeofencesAdded ");
            mAddGeofencesButton.setEnabled(false);
            mRemoveGeofencesButton.setEnabled(true);
        } else {
            LogInEvenement(  "mGeofencesAdded false ");
            mAddGeofencesButton.setEnabled(true);
            mRemoveGeofencesButton.setEnabled(false);
        }
    }
    private BroadcastReceiver br = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            ConstraintLayout mconstraintLayout =  (ConstraintLayout)findViewById(R.id.constraintLayout);
            Bundle bundle = intent.getExtras();
            String smess =   "BroadcastReceiverOnReceive InOut: " +
                    "ZONE: "   +       bundle.getString("p_zone_name") +
                    "direction: "   +     bundle.getString("p_In_or_Out");

            LogInEvenement(smess);
            //  InsLogdb(smess);


            if (bundle != null) {
                ((TextView) findViewById(R.id.txt_zone_name))
                        .setText(bundle.getString("p_zone_name"));
                ((TextView) findViewById(R.id.txt_In_or_Out))
                        .setText(bundle.getString("p_In_or_Out"));
                ((TextView) findViewById(R.id.txtLongitude))
                        .setText(bundle.getString("p_Longi"));
                ((TextView) findViewById(R.id.txtLatitude))
                        .setText(bundle.getString("p_Latit"));

                if  (bundle.getString("p_In_or_Out").equals("ENTREE"))   { mconstraintLayout.setBackgroundColor(Color.GREEN);}
                if  (bundle.getString("p_In_or_Out").equals("SORTIE"))   { mconstraintLayout.setBackgroundColor(Color.RED);}
            }
        }
    };


    private String showTimeFormattee()
    {
        String resultat;
        Date datenow = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new    SimpleDateFormat("hh:mm:ss yyyy/MM/dd");
        resultat = sdf.format(datenow) + " : " ;
        return    resultat;
    }

    private void   InsLogdb()
    {

        LogInEvenement(  "INSERT EVENTS  ************************ ");
        dbhelper.resetDB();

        db =  dbhelper.getWritableDatabase();

        for (LogEvenement temp : mlistItems) {
            ContentValues values = new ContentValues();
            values.put(LogEvenement.KEY_date_evenement,  temp.m_dt_event);  // new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            values.put(LogEvenement.KEY_event, temp.m_s_even);
            long evenement_id = db.insert(LogEvenement.TABLE, null, values);

        }
        db.close();
    }

    private void LogInEvenement(String p_event)
    {
        LogEvenement lg = new LogEvenement(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime()), p_event);


        //  madapter.insert(lg, 0);
        myAdapter.insert(lg, 0);
    }

    @Override

    public boolean onItemLongClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        LogEvenement item = (LogEvenement)adapter.getItemAtPosition(position);
        //   Toast.makeText(this, item.m_i_evenement_ID  + " " + item.m_s_even + " selected", Toast.LENGTH_LONG).show();
        poppopupWindow(item);



        return  true;
    }

    public void poppopupWindow(LogEvenement p_item)

    {

        // Inflate the custom layout/view

        LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_layout_2,null);
        if(mPopupWindow != null) mPopupWindow.dismiss();
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        TextView tvpop1 = (TextView)  customView.findViewById(R.id.firstLine);
        TextView tvpop2 = (TextView)  customView.findViewById(R.id.secondLine);
        TextView tvpop3 = (TextView)  customView.findViewById(R.id.seq_id);
        if(p_item.m_i_evenement_ID != null)  tvpop3.setText(Integer.toString(p_item.m_i_evenement_ID));
        tvpop2.setText(p_item.m_s_even);
        tvpop1.setText(p_item.m_dt_event);


        if(android.os.Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(mLinearLayout, Gravity.TOP,0,0);

    }


}
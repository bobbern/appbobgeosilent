package ca.robertbernier.exercices.appbobgeosilent;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bob on 12/7/2017.
 */

public class DBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                                                  + File.separator +  "logsGeoFence.db";;
    private final String CREATE_TABLE_LOGS_GEO_FENCE_COMMAND = "CREATE TABLE IF NOT EXISTS " + LogEvenement.TABLE + "("
        + LogEvenement.KEY_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT , "
        + LogEvenement.KEY_date_evenement +  " TEXT , " // DATETIME DEFAULT CURRENT_TIMESTAMP , "
        + LogEvenement.KEY_event + " TEXT )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null,  DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_LOGS_GEO_FENCE_COMMAND = "CREATE TABLE IF NOT EXISTS " + LogEvenement.TABLE + "("
                + LogEvenement.KEY_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + LogEvenement.KEY_date_evenement +  " TEXT , " // DATETIME DEFAULT CURRENT_TIMESTAMP , "
                + LogEvenement.KEY_event + " TEXT )";
        db.execSQL(CREATE_TABLE_LOGS_GEO_FENCE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + LogEvenement.TABLE);
//        onCreate(db);
    }

    public void resetDB()
    {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + LogEvenement.TABLE);

        this.getWritableDatabase().execSQL(CREATE_TABLE_LOGS_GEO_FENCE_COMMAND);

    }
    public ArrayList<LogEvenement> LoadLogs()
    {

        ArrayList<LogEvenement> arLogs = new ArrayList<LogEvenement>();
        Cursor c = null;
        c = this.getReadableDatabase().rawQuery("Select * FROM " + LogEvenement.TABLE + " ORDER BY id DESC " , null);

        while (c.moveToNext())
        {
            LogEvenement le = new LogEvenement( c.getInt(c.getColumnIndex(LogEvenement.KEY_ID)), c.getString(c.getColumnIndex(LogEvenement.KEY_date_evenement)), c.getString(c.getColumnIndex(LogEvenement.KEY_event)));

            arLogs.add(0 , le);
        }

        c.close();

        return arLogs;
    }

}

package com.example.didoy.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Didoy on 10/8/2016.
 */

public class WeatherDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public  static final String DATABASE_NAME = "weather.db";


    public WeatherDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " (" +
                WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY," +
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                WeatherContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                WeatherContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL); ";

//                 + " UNIQUE (" + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +") ON CONFLICT IGNORE"+
//                " );";


        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME  + " (" +

                        WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherContract.WeatherEntry.COLUMN_LOC_KEY +" INTEGER NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_DATETEXT +" TEXT NOT NULL,   " +
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC +" TEXT NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID +" TEXT NOT NULL, " +

                        WeatherContract.WeatherEntry.COLUMN_MIN +" REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_MAX +" REAL NOT NULL, " +

                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY +" REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_PRESSURE +" REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED +" REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_DEGREES +" REAL NOT NULL, " +

                        "FOREIGN KEY ( "+  WeatherContract.WeatherEntry.COLUMN_LOC_KEY +  " ) REFERENCES  " +
                        WeatherContract.LocationEntry.TABLE_NAME + " ( "+ WeatherContract.LocationEntry._ID +" ) " +


                        "UNIQUE ("+ WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", " +
                        WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE ); ";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}

package com.example.didoy.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.didoy.sunshine.data.WeatherContract;
import com.example.didoy.sunshine.data.WeatherDBHelper;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Didoy on 10/9/2016.
 */

public class TestUtility {


    final  static  String TEST_DATE = "1475979433";
    final  static String TEST_LOCATION = "1704703"; // Mabalacat
    //final  static  String TEST_LOCATION = "1730737"; // Angeles city

    static ContentValues createWeatherValues(long locationRowId) {

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN, 65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }
    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        WeatherDBHelper dbHelper = new WeatherDBHelper(context, null, null, 0);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtility.getLocationContentValue();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
        //locationRowId = db.insertWithOnConflict(WeatherContract.LocationEntry.TABLE_NAME, null, testValues, CONFLICT_REPLACE);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    static ContentValues getLocationContentValue() {
        // Create a new map of values, where column names are the keys
        String testlName = "Mabalacat";

        double testLang = 15.177270 ;
        double testLong = 120.593894;

        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testlName);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLang);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLong);

        return contentValues;
    }

}

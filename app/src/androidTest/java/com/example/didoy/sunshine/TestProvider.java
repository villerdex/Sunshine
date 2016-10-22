package com.example.didoy.sunshine;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.didoy.sunshine.data.WeatherContract;
import com.example.didoy.sunshine.data.WeatherDBHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Didoy on 10/13/2016.
 */



@RunWith(AndroidJUnit4.class)

public class TestProvider {

    final String LOG_TAG = TestProvider.class.getSimpleName();

    @Before
    public void setUp() throws Exception {
        deleteAllRecords();
    }

    @After
    public void tearDown() throws Exception {
        deleteAllRecords();
        }

    Context mMockContext = InstrumentationRegistry.getTargetContext();


    public void deleteAllRecords(){

        mMockContext.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null
                );


        mMockContext.getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mMockContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mMockContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();


    }

    /*
        This test doesn't touch the database.  It verifies that the ContentProvider returns
        the correct type for each type of URI that it can handle.
        Students: Uncomment this test to verify that your implementation of GetType is
        functioning correctly.
     */
    @Test
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mMockContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE",
                WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mMockContext.getContentResolver().getType(
                WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
                WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testDate = TestUtility.TEST_DATE ; // 13 Oct 2016

        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mMockContext.getContentResolver().getType(
                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1476324089950
        assertEquals("Error: the WeatherEntry CONTENT_URI with location and date should return WeatherEntry.CONTENT_ITEM_TYPE",
                WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mMockContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE", WeatherContract.LocationEntry.CONTENT_TYPE, type);
    }


    @Test
    public void testUpdateLocation(){

        deleteAllRecords();

        ContentValues values = TestUtility.getLocationContentValue();

        Uri uri = mMockContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, values);

        long id = ContentUris.parseId(uri);

        assertTrue( id != -1);
        Log.d(LOG_TAG, "New Row id" +  id);

        ContentValues uodateValues = new ContentValues(values);
        uodateValues.put(WeatherContract.LocationEntry._ID, id);
        uodateValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int updateRow = mMockContext.getContentResolver().update(WeatherContract.LocationEntry.CONTENT_URI,
                uodateValues,
                WeatherContract.LocationEntry._ID + "= ?",
                new String[] {String.valueOf(id)}
        );

        assertEquals(updateRow, 1);


        Cursor cursor = mMockContext.getContentResolver().query(
                WeatherContract.LocationEntry.buildLocationUri(id),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );


        assertTrue(cursor.moveToFirst());

        cursor.close();

    }


    @Test
    public void testInserReadProvider(){

        final   String TEST_DATE = TestUtility.TEST_DATE;
        final   String TEST_LOCATION = TestUtility.TEST_LOCATION;
        final String LOG_TAG = TestProvider.class.getSimpleName();

        SQLiteDatabase db = new WeatherDBHelper(mMockContext, null, null, 0).getWritableDatabase();

        // Insert Location values
        ContentValues testLocationValues = TestUtility.getLocationContentValue();
         Uri uri = mMockContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, testLocationValues);
        long rowId = ContentUris.parseId(uri);
        assertTrue(rowId != -1) ;


        ContentValues testValues = TestUtility.createWeatherValues(rowId);
        long weatherID;

         uri =  mMockContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, testValues );
        weatherID = ContentUris.parseId(uri);

        // Verify we got a row back.
        assertTrue(weatherID != -1);
        Log.d(LOG_TAG, "New row id: " + weatherID);


//        ----------------- Location test ---------------------------

        Cursor locationCursor = mMockContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null, // passing null will return all columns
                null, // column where clause
                null, // value for where clause
                null  // sortOrder
        );


        assertTrue( locationCursor.moveToFirst() );

        locationCursor = mMockContext.getContentResolver().query(
                WeatherContract.LocationEntry.buildLocationUri(rowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        assertTrue( locationCursor.moveToFirst() );



//        ----------------- weather test ---------------------------


        Cursor weatherCursor = mMockContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI,
                null, // passing null will return all columns
                null, // column where clause
                null, // value for where clause
                null  // sortOrder
                );

        assertTrue( weatherCursor.moveToFirst() );

//        TESTING FIRST JOIN STATEMENT
         weatherCursor = mMockContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                null, // passing null will return all columns
                null, // column where clause
                null, // value for where clause
                null  // sortOrder
        );

        assertTrue( weatherCursor.moveToFirst() );

        // content://com.example.android.sunshine.app/weather/94074/20140612
        weatherCursor = mMockContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, (TEST_DATE)),
                null, // passing null will return all columns
                null, // column where clause
                null, // value for where clause
                null  // sortOrder
        );

        assertTrue( weatherCursor.moveToFirst() );

        // content://com.example.android.sunshine.app/weather/94074/20140612
        weatherCursor = mMockContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                null, // passing null will return all columns
                null, // column where clause
                null, // value for where clause
                null  // sortOrder
        );

        assertTrue( weatherCursor.moveToFirst() );

        Log.v("testInsertDataToDb", "testInsertDataToDb");
        weatherCursor.close();
        locationCursor .close();
        db.close();
    }

}

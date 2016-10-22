package com.example.didoy.sunshine;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.didoy.sunshine.data.WeatherContract;
import com.example.didoy.sunshine.data.WeatherContract.WeatherEntry;
import com.example.didoy.sunshine.data.WeatherDBHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

/**
 * Created by Didoy on 10/8/2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestDB  extends Application {

    Context  mMockContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void testCreateDb() throws Throwable{

        mMockContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDBHelper(mMockContext, null, null, 0).getWritableDatabase();

        assertEquals(true, db.isOpen());
        db.close();

    }

    @Test
    public void testInsertDataToDb(){
        SQLiteDatabase db = new WeatherDBHelper(mMockContext, null, null, 0).getWritableDatabase();

        ContentValues testLocationValues = TestUtility.getLocationContentValue();
        long rowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testLocationValues);
        assertTrue(rowId != -1) ;


        ContentValues weatherValues = TestUtility.createWeatherValues(rowId);
        long weatherID = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherID != -1) ;

        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,
                null,
                null, // Columns for the where clause
                null, // Value for the where clause
                null, // Columns to group by
                null, // Columns to filter by row group
                null // order by
        );


        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,
                null,
                null, // Columns for the where clause
                null, // Value for the where clause
                null, // Columns to group by
                null, // Columns to filter by row group
                null // order by
        );

        assertTrue( weatherCursor.moveToFirst() );
        assertTrue( cursor.moveToFirst() );

        Log.v("testInsertDataToDb", "testInsertDataToDb");
        weatherCursor.close();
        cursor.close();
        db.close();
    }


}

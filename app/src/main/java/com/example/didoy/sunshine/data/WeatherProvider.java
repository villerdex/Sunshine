package com.example.didoy.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Didoy on 10/11/2016.
 */

public class WeatherProvider extends ContentProvider {

    final static String  LOG_TAG  = WeatherProvider.class.getSimpleName();

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301 ;

    private UriMatcher sUriMatcher = buildUriMatcher();
    private WeatherDBHelper mDbHelper;


    private static final SQLiteQueryBuilder sWeatherQueryBuilder;

    static{
        sWeatherQueryBuilder = new SQLiteQueryBuilder();

        sWeatherQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";

    private static final String sLocationAndDate =
            WeatherContract.LocationEntry.TABLE_NAME + "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND "
                    + WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ?";


//get weather by location settings
    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, startDate};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    //get weather by location settings and date setting
    private Cursor getWeatherByLocationAndDate(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                sLocationAndDate,
                new String[] {locationSetting, date},
                null,
                null,
                sortOrder
        );
    }



    private static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority  = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }



    @Override
    public boolean onCreate() {
        mDbHelper = new WeatherDBHelper(getContext(), null, null, 0);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

    Cursor cursor;

        switch (sUriMatcher.match(uri)){

            case  WEATHER:
                cursor =  mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case  WEATHER_WITH_LOCATION:
                cursor =  getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            case  WEATHER_WITH_LOCATION_AND_DATE:
                cursor =  getWeatherByLocationAndDate(uri, projection, sortOrder);
                break;
            case  LOCATION:
                cursor =  mDbHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection ,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case  LOCATION_ID:
                selection = WeatherContract.LocationEntry._ID  + " = ? ";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };

                cursor =  mDbHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(LOG_TAG, "query method is called");
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case  WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case  WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case  WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case  LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case  LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match){
            case WEATHER: {
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "insert method is called");

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereParameters) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowAffected = 0;
        switch (match){
            case WEATHER:
                rowAffected  = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, where, whereParameters);
                break;
            case LOCATION:
                 rowAffected = db.delete(WeatherContract.LocationEntry.TABLE_NAME, where, whereParameters);
                break;
            default:
                throw new android.database.SQLException("Failed to delete row into " + uri);
        }

        if (rowAffected != 0 || where == null){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(LOG_TAG, "delete method is called");

        return rowAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereParameters) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowAffected = 0;

        switch (match){
            case WEATHER:
                rowAffected  = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values ,where, whereParameters);
                break;
            case LOCATION:
                rowAffected = db.update(WeatherContract.LocationEntry.TABLE_NAME, values , where, whereParameters);
                break;
            default:
                throw new android.database.SQLException("Failed to update row into " + uri);
        }

        if (rowAffected != 0 ){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(LOG_TAG, "update emthod is called");
        return rowAffected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int insertedRow = 0;
        switch (match){
            case WEATHER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values){
                        long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (id != -1){
                            insertedRow++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null, false);
                return insertedRow;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}

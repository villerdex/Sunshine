package com.example.didoy.sunshine.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.didoy.sunshine.Activity.SettingsActivity;
import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility.Utility;
import com.example.didoy.sunshine.data.WeatherContract;
import com.example.didoy.sunshine.data.WeatherContract.LocationEntry;
import com.example.didoy.sunshine.data.WeatherContract.WeatherEntry;
import com.example.didoy.sunshine.sync.SunshineSyncAdapter;

import java.util.Date;

/**
 * Created by Didoy on 10/6/2016.
 */

public class ForeCastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    static final String LOG_TAG = ForeCastFragment.class.getSimpleName();
    private String mlocation;
    private boolean mUseTodaylayout = false;

    private ForeCastAdapter mForeCastAdapter;
    public static final int LoaderID  = 0;
    private ListView listView;
    private int CURSOR_POSITION = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." +WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MIN,
            WeatherEntry.COLUMN_MAX,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.COLUMN_COORD_LONG
    };

//    indices that are tied to FORECAST_COLUMNS, if FORECAST_COLUMNS changes then these must change
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MIN = 3;
    public static final int COL_WEATHER_MAX = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_HUMIDITY = 6;
    public static final int COL_WEATHER_WINDSPEED= 7;
    public static final int COL_WEATHER_PRESSURE = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;
    public static final int COL_COORD_LAT = 10;
    public static final int COL_COORD_LANG = 11;


    public interface CallBack {
        public void onItemSelected(String date);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public ForeCastFragment() {
            setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LoaderID, null, this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mlocation != null && !Utility.getPreferredLocation(getActivity()).equals(mlocation)){
            getLoaderManager().restartLoader(LoaderID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (CURSOR_POSITION != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, CURSOR_POSITION);
        }
        super.onSaveInstanceState(outState);
    }

    @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

             mForeCastAdapter = new ForeCastAdapter(getActivity(), null, 0);

            View rootView = inflater.inflate(R.layout.fragment_forcast_layout, container, false);

            listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForeCastAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    mForeCastAdapter = (ForeCastAdapter) parent.getAdapter();

                    Cursor cursor =  mForeCastAdapter.getCursor();

                    if (cursor != null && cursor.moveToPosition(position)){
                            ((CallBack) getActivity()).onItemSelected(cursor.getString(COL_WEATHER_DATE));
                             CURSOR_POSITION = position;
                    }
                }
            });

                if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
                    CURSOR_POSITION = savedInstanceState.getInt(SELECTED_KEY);
                }

            mForeCastAdapter.setmUseTodaylayout(mUseTodaylayout);

            return rootView;
        }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.refresh ){
            updateWeather();
        }


        if ( item.getItemId() == R.id.action_map ){
            openPreferedMapLocation();
        }

        if ( item.getItemId() == R.id.settings ){
            Log.d(LOG_TAG, "Setting activity is called");
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }


    // Method from Callback interface
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String startDate = WeatherContract.dateToMills(new Date());

        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mlocation = Utility.getPreferredLocation(getActivity());
        Uri weatherURI = WeatherEntry.buildWeatherLocationWithStartDate(mlocation , startDate);

        return new CursorLoader(
                getActivity(),
                weatherURI,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {

        mForeCastAdapter.swapCursor(dataCursor);

        if (CURSOR_POSITION != ListView.INVALID_POSITION){
            listView.setSelection(CURSOR_POSITION  );
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForeCastAdapter.swapCursor(null);
    }

    private void updateWeather(){

        SunshineSyncAdapter.syncImmediately(getActivity());

/*
  Fires the alarm in SunshineService.AlarmReceiver class which is responsible for fetching weather data
    This only reference for using alarmManager.
   */
//        Intent intent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis()+5000, pendingIntent);

    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        if (mForeCastAdapter != null){
            mForeCastAdapter.setmUseTodaylayout(useTodayLayout);
        }
    }

    private void openPreferedMapLocation(){


        if (mForeCastAdapter != null){

            Cursor c = mForeCastAdapter.getCursor();

                if (c != null){
                    c.moveToPosition(0);
                    String posLat = c.getString(COL_COORD_LAT);
                    String posLong = c.getString(COL_COORD_LANG);
                    Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(geoLocation);

                    // finds an activity if a class has not been explicitly specified.
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                        startActivity(intent);
                    }
                    else {
                        Log.e(LOG_TAG, "Could not find any appopriate app for location intent");
                    }

                }
        }

    }



}

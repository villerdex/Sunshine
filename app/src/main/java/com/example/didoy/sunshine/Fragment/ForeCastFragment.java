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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.didoy.sunshine.Activity.DetailActivity;
import com.example.didoy.sunshine.Activity.SettingsActivity;
import com.example.didoy.sunshine.FetchWeatherTask;
import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility;
import com.example.didoy.sunshine.data.WeatherContract;
import com.example.didoy.sunshine.data.WeatherContract.WeatherEntry;
import com.example.didoy.sunshine.data.WeatherContract.LocationEntry;

import java.util.Date;

/**
 * Created by Didoy on 10/6/2016.
 */

public class ForeCastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    static final String LOG_TAG = ForeCastFragment.class.getSimpleName();
    private String mlocation;

    private SimpleCursorAdapter simpleCursorAdapter;
    public static final int LoaderID  = 0;
    ListView listView;

    String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." +WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MIN,
            WeatherEntry.COLUMN_MAX,
            LocationEntry.COLUMN_LOCATION_SETTING,
    };

//    indices that are tied to FORECAST_COLUMNS, if FORECAST_COLUMNS changes then these must change
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MIN = 3;
    public static final int COL_WEATHER_MAX = 4;
    public static final int COL_LOCATION_SETTING = 5;


    public ForeCastFragment() {
            setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

             simpleCursorAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.list_item_forecast,
                    null,
                    new String[]{
                            WeatherEntry.COLUMN_DATETEXT,
                            WeatherEntry.COLUMN_SHORT_DESC,
                            WeatherEntry.COLUMN_MIN,
                            WeatherEntry.COLUMN_MAX
                    },

                    new int[]{
                            R.id.list_item_date_textview,
                            R.id.list_item_forecast_textview,
                            R.id.list_item_low_textview,
                            R.id.list_item_high_textview
                    },
                    0
            );

            simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    boolean isMetric = Utility.isMetric(getActivity());

                    switch (columnIndex){

                        case COL_WEATHER_MAX:
                        case COL_WEATHER_MIN:
                            ((TextView) view).setText(Utility.formatTemperature( cursor.getDouble(columnIndex), isMetric));
                            return true;
                        case COL_WEATHER_DATE:
                            String dateString = cursor.getString(columnIndex);
                            ((TextView) view).setText(Utility.formatDate(dateString));

                            return true;
                    }


                    return false;
                }
            });

            View rootView = inflater.inflate(R.layout.fragment_forcast_layout, container, false);

            listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(simpleCursorAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SimpleCursorAdapter simpleCursorAdapter = (SimpleCursorAdapter) parent.getAdapter();

                    Cursor cursor =  simpleCursorAdapter.getCursor();

                    if (cursor != null && cursor.moveToPosition(position)){
//                        boolean isMetric = Utility.isMetric(getActivity());

//                        String foreCast = String.format("%s - %s -%s/%s",
//                                Utility.formatDate(cursor.getString(COL_WEATHER_DATE)),
//                                cursor.getString(COL_WEATHER_DESC),
//                                Utility.formatTemperature(cursor.getDouble(COL_WEATHER_MIN), isMetric ),
//                                Utility.formatTemperature(cursor.getDouble(COL_WEATHER_MAX), isMetric )
//                        );

                        // Start the Detail activity intent
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(DetailActivity.DATE_KEY, cursor.getString(COL_WEATHER_DATE));
                        startActivity(intent);
                    }

                }
            });


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

        CursorLoader loader =  new CursorLoader(
                getActivity(),
                weatherURI,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {
        simpleCursorAdapter.swapCursor(dataCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }


    private void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPreferences.getString( getString(R.string.preference_location_key), getString(R.string.preference_location_default) );
        weatherTask.execute(location); // call the doInBackground method in FetchWeatherTask class
    }

}

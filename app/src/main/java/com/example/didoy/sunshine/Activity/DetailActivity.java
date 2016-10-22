package com.example.didoy.sunshine.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility;
import com.example.didoy.sunshine.data.WeatherContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    String mForeCast;
    final String SHARE_HASTAG = " #Sunshine App";
    final String LOG_TAG = DetailActivity.class.getSimpleName();

    final int DETAIL_ID_LOADER = 0;
    public  static  String DATE_KEY = "date";
    private String LOCATION_KEY = "location";

      @BindView(R.id.list_item_high_textview) TextView highTempTextView ;
      @BindView(R.id.list_item_low_textview) TextView  lowTempTextView ;
      @BindView(R.id.list_item_forecast_textview) TextView foreCastTextView ;
      @BindView(R.id.list_item_date_textview) TextView dateTextView ;


    private String mForeCastLocation;
    private String mLocation;
    private String forecastDate ;
    private String foreCastShare;

    String[] columns = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MIN,
            WeatherContract.WeatherEntry.COLUMN_MAX,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        // Bind the butterknife to this activity
        ButterKnife.bind(this);


        Intent intent = getIntent();
        forecastDate = intent.getStringExtra(DATE_KEY);

        // initialize the loader using Loadermanager
        getSupportLoaderManager().initLoader(DETAIL_ID_LOADER, null, this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mLocation != null){
            outState.putString(LOCATION_KEY, mLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( mLocation != null && !mLocation.equals(Utility.getPreferredLocation(this)) ){
            getSupportLoaderManager().restartLoader(DETAIL_ID_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.detail_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mSharedProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mSharedProvider != null){
            mSharedProvider.setShareIntent(createShareForecastIntent());
        }else {
            Log.e(LOG_TAG, "mSharedProvider might be null");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.settings ){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        if ( item.getItemId() == R.id.action_share ){
            startActivity(createShareForecastIntent());
        }

        return true;
    }

    private Intent createShareForecastIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                mForeCast + SHARE_HASTAG);

        return intent;
    }


//    ==================== LOADER METHODS ===================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onn onCreateLoader");


        mLocation = Utility.getPreferredLocation(getApplication());
        Uri weatherURI = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, forecastDate);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                weatherURI,
                columns,
                null,
                null,
                null
                );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(LOG_TAG, "onLoadFinished is Called");

        if (data.moveToFirst()){

            String desc = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            String date = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));

            double high  = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX));
            double low  = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN));

            boolean isMetric = Utility.isMetric(this);

            dateTextView.setText(Utility.formatDate(date));
            foreCastTextView.setText(desc);
            highTempTextView.setText(Utility.formatTemperature(high, isMetric));
            lowTempTextView.setText(Utility.formatTemperature(low, isMetric));

            foreCastShare = String.format("%s - %s - %s/%s",
                    dateTextView.getText(),
                    foreCastTextView.getText(),
                    highTempTextView.getText(),
                    lowTempTextView.getText());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

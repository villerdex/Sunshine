package com.example.didoy.sunshine.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.didoy.sunshine.Activity.DetailActivity;
import com.example.didoy.sunshine.Activity.SettingsActivity;
import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility.Utility;
import com.example.didoy.sunshine.Utility.UtilityLocation;
import com.example.didoy.sunshine.data.WeatherContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Didoy on 10/28/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String mForeCast;
    final String SHARE_HASHTAG = " #Sunshine App";
    final String LOG_TAG = DetailActivity.class.getSimpleName();

    final int DETAIL_ID_LOADER = 0;
    public  static  String DATE_KEY = "date";
    private String LOCATION_KEY = "location";
    private final  String shareKey = "ShareWeather";

    @BindView(R.id.detail_day_textview) TextView mDayTextView;
    @BindView(R.id.detail_high_textview) TextView mHighTempTextView;
    @BindView(R.id.detail_low_textview) TextView mLowTempTextView;
    @BindView(R.id.detail_forecast_textview) TextView mForeCastTextView;
    @BindView(R.id.detail_date_textview) TextView mDateTextView;
    @BindView(R.id.detail_humidity_textview) TextView mHumidityTextView;
    @BindView(R.id.detail_wind_textview) TextView mWindTextView;
    @BindView(R.id.detail_pressure_textview) TextView mPressureTextView;

    @BindView(R.id.detail_icon)
    ImageView icon;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView );

        mDateTextView.setText("");
        // Bind the ButterKnife to this activity

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mLocation != null){
            outState.putString(LOCATION_KEY, mLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = getArguments();
        if ( mLocation != null &&
                !mLocation.equals(UtilityLocation.getPreferredLocation(getActivity())) &&
                bundle != null &&
                bundle.containsKey(DetailActivity.DATE_KEY ) )
        {
            getLoaderManager().restartLoader(DETAIL_ID_LOADER, null, this);
        }
    }

    @Override
    public void  onCreateOptionsMenu(Menu menu,  MenuInflater inflater) {

        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mSharedProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mSharedProvider != null){
            mSharedProvider.setShareIntent(createShareForecastIntent());
        }else {
            Log.e(LOG_TAG, "mSharedProvider might be null");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.settings ){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }

        if ( item.getItemId() == R.id.action_share ){

            Intent shareIntent = createShareForecastIntent();
                startActivity(shareIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                mForeCast + SHARE_HASHTAG);

        return intent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        Bundle bundle = getArguments();

        if (bundle != null && bundle.containsKey(DetailActivity.DATE_KEY)){
            getLoaderManager().initLoader(DETAIL_ID_LOADER, null, this);
        }

    }

    //    ==================== LOADER METHODS ===================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onn onCreateLoader");

        forecastDate = getArguments().getString(DetailActivity.DATE_KEY);

        mLocation = UtilityLocation.getPreferredLocation(getActivity());
        Uri weatherURI = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, forecastDate);

        return new CursorLoader(
                getActivity(),
                weatherURI,
                columns,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(LOG_TAG, "onLoadFinished is Called");

        if (data.moveToFirst()){

            int weatherID = data.getInt(ForeCastFragment.COL_WEATHER_CONDITION_ID);

            boolean isMetric = Utility.isMetric(getActivity());

            String desc = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            String date = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));

            double high  = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX));
            double low  = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN));

            float humidity = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
            float pressure = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
            float wind = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
            float windDir = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));

            mForeCast = String.format("%s - %s - %s/%s", Utility.formatDate(date), desc, high, low);

            mDateTextView.setText(Utility.formatDate(date));
            mDayTextView.setText(Utility.friendlyDateFormat(getActivity(), date));
            mForeCastTextView.setText(desc);
            mHighTempTextView.setText(Utility.formatTemperature(getActivity(), high, isMetric));
            mLowTempTextView.setText(Utility.formatTemperature(getActivity(),low, isMetric));

            mHumidityTextView.setText(this.getString(R.string.format_humidity, humidity));

            mWindTextView.setText(Utility.getFormattedWind(getActivity(), wind, windDir));
            mPressureTextView.setText(this.getString(R.string.format_pressure, pressure));

            foreCastShare = String.format("%s - %s - %s/%s",
                    mDateTextView.getText(),
                    mForeCastTextView.getText(),
                    mHighTempTextView.getText(),
                    mLowTempTextView.getText());

            icon.setImageResource(Utility.getColorReourceIconByWeatherID(weatherID));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

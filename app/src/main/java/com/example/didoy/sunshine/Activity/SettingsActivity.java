package com.example.didoy.sunshine.Activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.didoy.sunshine.FetchWeatherTask;
import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility;
import com.example.didoy.sunshine.data.WeatherContract;

import java.util.List;


public class SettingsActivity extends PreferenceActivity {


    private int MY_PERMISSIONS_STORAGE = 123;
    final static String LOG_TAG = SettingsActivity.class.getSimpleName();


    private static boolean onPrefChange(Preference preference, Object newValue, Context context, boolean mBindingPreference) {
        String value = newValue.toString();

        if (!mBindingPreference) {
            if (preference.getKey().equals( context.getString(R.string.preference_location_key))) {
                FetchWeatherTask weatherTask = new FetchWeatherTask(context);
                String location = newValue.toString();
                weatherTask.execute(location);
                Log.d(LOG_TAG, "starting Featch weather task");

            } else {
                // notify code that weather may be impacted
                Log.d(LOG_TAG, "Preference change notifying  Data");

                context.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
            }
        }


        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;

            int prefIndex = listPreference.findIndexOfValue(value);

            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(value);
        }
        return true;

    }

    private static boolean bindPreferenceSummaryToValue(Preference preference, boolean mBindingPreference ,
                                                     Preference.OnPreferenceChangeListener prefListener) {
        mBindingPreference = true;

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(prefListener);

        // Trigger the listener immediately with the preference's
        // current value.        prefListener.onPreferenceChange(preference,
        PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");


        mBindingPreference = false;

        return mBindingPreference;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        //checkReadingStoragePermission();

        //   all the header in the R.xml.pref_headers files are fragments
        // which extends the android.preference.fragment
        loadHeadersFromResource(R.xml.pref_headers, target);  // Load all header in the resource file first
    }


    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) ||
                PreferenceTemperatureFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_STORAGE) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "WE really need this permission to use in app", Toast.LENGTH_SHORT).show();
                System.exit(1);
            }
        }
    }

    private void checkReadingStoragePermission() {
        String[] uri = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, uri,
                            MY_PERMISSIONS_STORAGE);
                }
            }
        }
    }


    // ===================== PREFERENCE FRAGMENT ATTACHES IN pref_header_xml ==============================
    public static class PreferenceFragment extends android.preference.PreferenceFragment implements Preference.OnPreferenceChangeListener {
        boolean mBindingPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            // this will inflate the resource file
            addPreferencesFromResource(R.xml.pref_general);

            // get the value associated with preference name
            String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.preference_location_key), "");

            // get the preference associated with preference name
            Preference preference = findPreference(getString(R.string.preference_location_key));

            if (preference != null) {
                mBindingPreference =   bindPreferenceSummaryToValue(preference, mBindingPreference, this);

                if (!value.equals("")) {
                    preference.setSummary(value);
                }

            }

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return onPrefChange(preference, newValue, getActivity(), mBindingPreference);
        }
    }


    public static class PreferenceTemperatureFragment extends android.preference.PreferenceFragment implements Preference.OnPreferenceChangeListener {

        boolean mBindingPreference;

        @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                // Load the preferences from an XML resource
                // this will inflate the resource file
                addPreferencesFromResource(R.xml.pref_temp);

                // get the value associated with preference name
                String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_units_key), "");

                // get the preference associated with preference name
                Preference preference = findPreference(getString(R.string.preference_temperature_key));

                if (preference != null) {

                  mBindingPreference =   bindPreferenceSummaryToValue(preference, mBindingPreference, this);

                    if (!value.equals("")) {
                        preference.setSummary(value);
                    }

                }

            }

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return onPrefChange(preference, newValue, getActivity(), mBindingPreference);
            }
        }


}

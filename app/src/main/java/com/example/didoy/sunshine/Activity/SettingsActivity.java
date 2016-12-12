package com.example.didoy.sunshine.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility.AppCompatPreferenceActivity;
import com.example.didoy.sunshine.data.WeatherContract;
import com.example.didoy.sunshine.sync.SunshineSyncAdapter;

import java.util.List;

/*
AppCompatPreferenceActivity is a customize class which
is necessary to add Actionbar in Preference Activity
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private int MY_PERMISSIONS_STORAGE = 123;
    final static String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enabling the back button from the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // this method is already deprecated http://stackoverflow.com/questions/6822319/what-to-use-instead-of-addpreferencesfromresource-in-a-preferenceactivity
        //addPreferencesFromResource(R.xml.pref_location);

    }

//  the back button that we enable are presented but not working
//  Using onOptionsItemSelected and calling the finish() as below solves the problem
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static boolean  onPrefChange(Preference preference, Object newValue, Context context, boolean mBindingPreference) {
        String value = newValue.toString();

        if (!mBindingPreference) {
            if (preference.getKey().equals(context.getString(R.string.preference_location_key))) {

               SunshineSyncAdapter.syncImmediately(context);

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

    private static boolean bindPreferenceSummaryToValue(Preference preference, boolean mBindingPreference,
                                                        Preference.OnPreferenceChangeListener prefListener, String Datatype) {

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(prefListener);

        if (Datatype.toLowerCase().equals("string")){
            PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), "");
        }

        if (Datatype.toLowerCase().equals("boolean")){
            PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getBoolean(preference.getKey(), true );
        }


        mBindingPreference = false;
        return mBindingPreference;
    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        //checkReadingStoragePermission();

        // all the header in the R.xml.pref_headers files are fragments
        // which extends the android.preference.fragment
       loadHeadersFromResource(R.xml.pref_headers, target);  // Load all header in the resource file first
    }


    // make sure to make your fragment as valid to avoid illegal state fragment
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceLocationFragment.class.getName().equals(fragmentName) ||
               PreferenceTemperatureFragment.class.getName().equals(fragmentName) ||
                PreferenceNotificationFragment.class.getName().equals(fragmentName);
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
    public static class PreferenceLocationFragment extends android.preference.PreferenceFragment implements Preference.OnPreferenceChangeListener {
        boolean mBindingPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            // this will inflate the resource file
            addPreferencesFromResource(R.xml.pref_location);

            // get the value associated with preference name
            String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.preference_location_key), "");

            // get the preference associated with preference name
            Preference preference = findPreference(getString(R.string.preference_location_key));

            if (preference != null) {
                mBindingPreference = bindPreferenceSummaryToValue(preference, mBindingPreference, this, "string");

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

                mBindingPreference = bindPreferenceSummaryToValue(preference, mBindingPreference, this,  "string");

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

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public static class PreferenceNotificationFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        boolean mBindingPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            // this will inflate the resource file
            addPreferencesFromResource(R.xml.pref_notification);

            // get the value associated with preference name
            String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_enable_notifications_default), "");

            // get the preference associated with preference name
            Preference preference = findPreference(getString(R.string.pref_enable_notifications_key));

            if (preference != null) {

                mBindingPreference = bindPreferenceSummaryToValue(preference, mBindingPreference, this, "boolean");

                if (!value.equals("")) {
                    preference.setSummary(value);
                }
            }

        }
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return  onPrefChange(preference, newValue, getActivity(), mBindingPreference);
        }
    }
}

package com.example.didoy.sunshine.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.didoy.sunshine.Fragment.ForeCastFragment;

import com.example.didoy.sunshine.R;
public class MainActivity extends AppCompatActivity {

    final static String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main, new ForeCastFragment())  // Load the fragment
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ( item.getItemId() == R.id.action_map ){
            Log.d(LOG_TAG, "  Refresh is click " );
            openPreferedMapLocation();
        }

        if (item.getItemId() == R.id.settings){
            Log.d(LOG_TAG, "  Settings is click " );
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferedMapLocation(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        String location = sharedPreferences.getString(
            getString(R.string.preference_location_key),
            getString(R.string.preference_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }

        else {
            Log.e(LOG_TAG, "Could not find any appopriate app for location intent");
        }

    }
}

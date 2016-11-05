package com.example.didoy.sunshine.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.didoy.sunshine.Fragment.DetailFragment;
import com.example.didoy.sunshine.Fragment.ForeCastFragment;
import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.sync.SunshineSyncAdapter;

public class MainActivity extends AppCompatActivity implements ForeCastFragment.CallBack{

    final static String LOG_TAG = MainActivity.class.getSimpleName();

    boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null){

            mTwoPane = true;

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.weather_detail_container, new DetailFragment())
                    .commit();
        }else{
            mTwoPane = false;
        }

        ForeCastFragment foreCastFragment = (ForeCastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        foreCastFragment.setUseTodayLayout(!mTwoPane);

        // make sure that we've gotten an account and sync it
        SunshineSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.settings){
            Log.d(LOG_TAG, "  Settings is click " );
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemSelected(String date) {
        if (mTwoPane){

            Bundle arguments = new Bundle();
            arguments.putString(DetailActivity.DATE_KEY, date);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment)
                    .commit();

        }else {

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);

        }
    }


}

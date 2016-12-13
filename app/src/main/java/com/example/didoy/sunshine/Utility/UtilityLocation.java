package com.example.didoy.sunshine.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.sync.SunshineSyncAdapter;

import static com.example.didoy.sunshine.sync.SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID;

/**
 * Created by Didoy on 12/13/2016.
 */

public class UtilityLocation {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.preference_location_key),
                context.getString(R.string.preference_location_default));
    }

    public static void setLocationStatus(int locationStatus, Context context){
        SharedPreferences preference =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(context.getString(R.string.location_status), locationStatus);
        editor.commit();
    }

    public static int getLocationStatus(Context context){
        SharedPreferences  preference =  PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getInt(context.getString(R.string.location_status), 404);
    }

    public static void resetLocationStatus(Context context){
        SharedPreferences preference =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(context.getString(R.string.location_status), SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN);
        editor.apply();
    }
}

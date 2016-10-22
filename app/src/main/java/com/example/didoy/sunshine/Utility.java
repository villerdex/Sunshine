package com.example.didoy.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.example.didoy.sunshine.data.WeatherContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Didoy on 10/15/2016.
 */

public class Utility extends ActivityCompat {

        public static String getPreferredLocation(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getString(context.getString(R.string.preference_location_key),
                    context.getString(R.string.preference_location_default));
        }

        public static boolean isMetric(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getString(context.getString(R.string.preference_temperature_key), context.getString(R.string.preference_temperature_key))
                    .equals(context.getString(R.string.pref_units_metric));
        }

        public static String formatTemperature(double temperature, boolean isMetric) {
            double temp;
            if ( !isMetric ) {
                temp = 9*temperature/5+32;
            } else {
                temp = temperature;
            }
            return String.format("%.0f", temp);
        }

        public static String formatDate(String dateString) {
            Date date = WeatherContract.millsToDate(dateString);
            return  DateFormat.getDateInstance().format(date);
        }

    public static String friendlyDateFormat(Context context, String givenDate){

        final String TODAY = "Today ";
        final String TOMORROW = "Tomorrow";
        String friendlyString = "";

        Date todayDate = new Date();

        String todayAsMills  = WeatherContract.dateToMills(todayDate);

        if (todayAsMills.equals(givenDate)){
            friendlyString = TODAY + getFormattedMonthDay(givenDate);
        }else {

            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 1);

            Date tomorrowDate = cal.getTime();

            // The string date for tomorrow
            if (tomorrowDate.equals(givenDate)){
                friendlyString = TOMORROW;
            }else {
                Date tommorow = WeatherContract.millsToDate(givenDate);

                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(tommorow);
            }

        }


        return friendlyString;
    }


    public static String getFormattedMonthDay( String  givenDate){
        String monthDay = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd");
        monthDay =   dateFormat.format(givenDate);
        return monthDay;
    }


}

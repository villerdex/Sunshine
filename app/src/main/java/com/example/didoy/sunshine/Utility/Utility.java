package com.example.didoy.sunshine.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.example.didoy.sunshine.R;
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

    public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }

        // the 2 line of code below are used for my exploration using
        // xliff:g tag on xml
//            int formatId = R.string.format_full_friendly_date;
//            String x = context.getString(formatId, "G", "H");

        return context.getString(R.string.format_temperature,  temp);
    }

    public static String formatDate(String dateString) {
        Date date = WeatherContract.millsToDate(dateString);
        return  DateFormat.getDateInstance().format(date);
    }

    public static String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }


    public static String friendlyDateFormat(Context context, String givenDate){

        final String TODAY = "Today ";
        String friendlyString = "";

        Date todayDate = new Date();

        String todayAsMills  = WeatherContract.dateToMills(todayDate);

        if (todayAsMills.equals(givenDate)){
            friendlyString = TODAY + getFormattedMonthDay(todayAsMills);
        }else {

            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);

            String futureWeek = WeatherContract.dateToMills(cal.getTime());

            // The string date for for the whole week
            if (givenDate.compareTo(futureWeek) < 0){
                friendlyString = getFormmattedDayName(givenDate);
            }else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd");
                return simpleDateFormat.format(WeatherContract.millsToDate(givenDate));
            }

        }


        return friendlyString;
    }

    public static String getFormmattedDayName(String givenDate){
        String TOMORROW = "Tomorrow ";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);

        String tomDate = WeatherContract.dateToMills(cal.getTime());

        // return "Tomorrow" if givenDate is set for tomorrow
        if (tomDate.equals(givenDate)){
            return TOMORROW;

            // return day name
        }else {
            Date tommorow = WeatherContract.millsToDate(givenDate);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            TOMORROW =   dayFormat.format(tommorow);
        }
        return TOMORROW;

    }


    public static String getFormattedMonthDay( String  givenDate){
        String monthDay = "";

        Date date = new Date(String.valueOf(WeatherContract.millsToDate(givenDate)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd");
        monthDay =   dateFormat.format(date);
        return monthDay;
    }


    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind;
        } else {
            windFormat = R.string.format_wind;
            windSpeed = .621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 || degrees < 22.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }

    public static int getReourceIconByWeatherID(int weatherId){

        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    public static int getColorReourceIconByWeatherID(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_rain;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

}

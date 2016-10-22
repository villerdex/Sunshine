package com.example.didoy.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.didoy.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Didoy on 10/14/2016.
 */


public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    HttpURLConnection urlConnection = null;
    BufferedReader bufferedReader = null;
    static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    Context mContext;

    public FetchWeatherTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        String JsonForCast = "";
        String format = "Json";
        String unit = "metrics";
        int numDays = 14;
        String key_id = "faf6c39b2d581bd640311bf423209199";

        if (params.length == 0) {
            return null;
        }

        try {

            final String FORE_CAST_BASEURL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";

            final String ID_PARAM = "id";
            final String FORMAT_PARAM = "mode";
            final String UNIT_PARAM = "unit";
            final String DAYS_PARAM = "cnt";
            final String APP_ID = "appid";


            Uri uri = Uri.parse(FORE_CAST_BASEURL).buildUpon()
                    .appendQueryParameter(ID_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNIT_PARAM, unit)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APP_ID, key_id)
                    .build();


            URL url = new URL(uri.toString());

            Log.v("BUILD URI", uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                // do nothing
                JsonForCast = null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


            // Append every line in Json
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            JsonForCast = stringBuffer.toString();
            Log.i(LOG_TAG, JsonForCast);


        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error closing stream", e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
              getWeatherDataFromJson(JsonForCast,numDays, params[0]);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private String[] getWeatherDataFromJson(String jsonStringData, int numDays, String locationSetting) throws JSONException {

        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_Description = "description";

        //      Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";
        final String OWM_COORD_LAT = "lat";
        final String OWM_COORD_LONG = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_DATETIME = "dt";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        final String OWM_MAIN = "main";
        final String OWM_WEATHER = "weather";
        final String OWM_WEATHER_ID = "id";


        JSONObject foreCastJson = new JSONObject(jsonStringData);
        JSONArray weatherArray = foreCastJson.getJSONArray(OWM_LIST);

//         City Json objects
        JSONObject cityJson = foreCastJson.getJSONObject(OWM_CITY);
        String cityName = cityJson.getString(OWM_CITY_NAME);
        JSONObject coordJSON = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = coordJSON.getLong(OWM_COORD_LAT);
        double cityLongitude = coordJSON.getLong(OWM_COORD_LONG);


        long locationID = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);


        String[] resultStr = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {

            String day;
            String description;
            String hignAndLow;

            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;
            long weatherId;


            JSONObject dayForeCast = weatherArray.getJSONObject(i);

            dateTime = dayForeCast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            pressure = dayForeCast.getDouble(OWM_PRESSURE);
            humidity = dayForeCast.getInt(OWM_HUMIDITY);
            windSpeed = dayForeCast.getDouble(OWM_WINDSPEED);
            windDirection = dayForeCast.getDouble(OWM_WIND_DIRECTION);

            JSONObject weatherObject = dayForeCast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_Description);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            // get the weather description in child "weather"
            JSONObject temperatureObject = dayForeCast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationID);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, WeatherContract.dateToMills(new Date( dateTime  * 1000L )));
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);


            Vector<ContentValues> listOfContentValue = new Vector<ContentValues>(weatherArray.length());
            listOfContentValue.add(weatherValues);


            //       Bulk insert
            if (listOfContentValue.size() > 0){
                ContentValues[] cvArray = new ContentValues[listOfContentValue.size()];
                    listOfContentValue.toArray(cvArray);

                mContext.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);

            }


            hignAndLow = formatHighAndLow(high, low);
            day = getReadableDateString(dateTime);

            resultStr[i] = day + " - " + description + " - " + hignAndLow;
        }

        for (String str : resultStr) {
            Log.v("FORECAST ENTRY", str);
        }
        return resultStr;

    }

    private String formatHighAndLow(double H, double Low) {
        return String.valueOf(Low).substring(0, 2) + "/" + String.valueOf(H).substring(0, 2);
    }


    private long addLocation(String locationSetting, String cityName, double lat, double lon){

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);

        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG, "Found it in the database!");
            int locationIdIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            return cursor.getLong(locationIdIndex);
        } else {
            Log.v(LOG_TAG, "Didn't find it in the database, inserting now!");
            ContentValues locationValues = new ContentValues();
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

            Uri locationInsertUri = mContext.getContentResolver()
                    .insert(WeatherContract.LocationEntry.CONTENT_URI, locationValues);

            return ContentUris.parseId(locationInsertUri);
        }


    }
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }


}

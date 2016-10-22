package com.example.didoy.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.didoy.sunshine.Fragment.ForeCastFragment;

/**
 * Created by Didoy on 10/22/2016.
 */



public class ForeCastAdapter extends CursorAdapter {

    public ForeCastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView dateTextView = (TextView) view.findViewById(R.id.list_item_date_textview);
        TextView foreCastTextView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        TextView highTextView = (TextView) view.findViewById(R.id.list_item_high_textview);
        TextView lowTextView = (TextView) view.findViewById(R.id.list_item_low_textview);
        ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);


        double weatherID = cursor.getDouble(ForeCastFragment.COL_WEATHER_ID);

        String date = cursor.getString(ForeCastFragment.COL_WEATHER_DATE);
        String foreCastDesc = cursor.getString(ForeCastFragment.COL_WEATHER_DESC);
        double maxWeather = cursor.getDouble(ForeCastFragment.COL_WEATHER_MAX);
        double minWeather = cursor.getDouble(ForeCastFragment.COL_WEATHER_MIN);

        boolean isMetric = Utility.isMetric(context);

        dateTextView.setText(Utility.friendlyDateFormat(context, date));
        foreCastTextView.setText(foreCastDesc);
        highTextView.setText(Utility.formatTemperature(maxWeather, isMetric));
        lowTextView.setText(Utility.formatTemperature(minWeather, isMetric));

    }
}

package com.example.didoy.sunshine.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.didoy.sunshine.Fragment.ForeCastFragment;
import com.example.didoy.sunshine.R;
import com.example.didoy.sunshine.Utility.Utility;

/**
 * Created by Didoy on 10/22/2016.
 */



public class ForeCastAdapter extends CursorAdapter {

    private final int VIEWTYPE_TODAY = 0;
    private final int VIEWTYPE_FUTUREDAY = 1;
    private boolean mUseTodaylayout = false;



    public void setmUseTodaylayout(boolean mUseTodaylayout) {
        this.mUseTodaylayout = mUseTodaylayout;
    }

    public ForeCastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mUseTodaylayout? VIEWTYPE_TODAY : VIEWTYPE_FUTUREDAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutID = 0;

        if (viewType == VIEWTYPE_TODAY){
            layoutID  = R.layout.list_item_forecast_today;
        }else {
            layoutID = R.layout.list_item_forecast;
        }


        View v = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int weatherID = cursor.getInt(ForeCastFragment.COL_WEATHER_CONDITION_ID);

        String date = cursor.getString(ForeCastFragment.COL_WEATHER_DATE);
        String foreCastDesc = cursor.getString(ForeCastFragment.COL_WEATHER_DESC);
        double maxWeather = cursor.getDouble(ForeCastFragment.COL_WEATHER_MAX);
        double minWeather = cursor.getDouble(ForeCastFragment.COL_WEATHER_MIN);

        boolean isMetric = Utility.isMetric(context);

        viewHolder.dateTextView.setText(Utility.friendlyDateFormat(context, date));
        viewHolder.foreCastTextView.setText(foreCastDesc);
        viewHolder.highTextView.setText(Utility.formatTemperature(context, maxWeather, isMetric));
        viewHolder.lowTextView.setText(Utility.formatTemperature(context, minWeather, isMetric));

        int viewType = getItemViewType(cursor.getPosition());

        switch (viewType){
            case VIEWTYPE_TODAY:
                viewHolder.icon.setImageResource(Utility.getColorReourceIconByWeatherID(weatherID));
                break;
            case VIEWTYPE_FUTUREDAY:
                viewHolder.icon.setImageResource(Utility.getReourceIconByWeatherID(weatherID));
                break;
        }

    }

    public static  class ViewHolder{
        TextView dateTextView ;
        TextView foreCastTextView ;
        TextView highTextView ;
        TextView lowTextView ;
        ImageView icon ;

        public ViewHolder(View view) {
             dateTextView     = (TextView) view.findViewById(R.id.list_item_date_textview);
             foreCastTextView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
             highTextView     = (TextView) view.findViewById(R.id.list_item_high_textview);
             lowTextView      = (TextView) view.findViewById(R.id.list_item_low_textview);
             icon             = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }
}

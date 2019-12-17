package com.zehaogao.weatherapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class TodayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";

    private String param1;
    private JSONObject weatherObj;

    private TextView wsText;
    private TextView presText;
    private TextView prepText;
    private TextView tempText;
    private TextView statusText;
    private TextView humText;
    private TextView visText;
    private TextView ccText;
    private TextView ozoneText;
    private ImageView statusIcon;

    public TodayFragment() { }

    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(String param1) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM1);
        }
        try {
            weatherObj = new JSONObject(param1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wsText = view.findViewById(R.id.detail_wind_speed_value);
        presText = view.findViewById(R.id.detail_pressure_value);
        prepText = view.findViewById(R.id.detail_precipitation_value);
        tempText = view.findViewById(R.id.detail_temperature_value);
        statusText = view.findViewById(R.id.detail_weather_info);
        humText = view.findViewById(R.id.detail_humidity_value);
        visText = view.findViewById(R.id.detail_visibility_value);
        ccText = view.findViewById(R.id.detail_cloud_value);
        ozoneText = view.findViewById(R.id.detail_ozone_value);
        statusIcon = view.findViewById(R.id.detail_weather_icon);

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        try {
            Double wsVal, presVal, prepVal, tempVal, humVal, visVal, ccVal, ozoneVal;
            String iconSource;
            JSONObject currentlyObj = weatherObj.getJSONObject("currently");
            iconSource = currentlyObj.has("icon") ? currentlyObj.getString("icon") : "";
            wsVal = currentlyObj.has("windSpeed") ? currentlyObj.getDouble("windSpeed") : 0.0;
            presVal = currentlyObj.has("pressure") ? currentlyObj.getDouble("pressure") : 0.0;
            prepVal = currentlyObj.has("precipIntensity") ? currentlyObj.getDouble("precipIntensity") : 0.0;
            tempVal = currentlyObj.has("temperature") ? currentlyObj.getDouble("temperature") : 0.0;
            humVal = currentlyObj.has("humidity") ? currentlyObj.getDouble("humidity") : 0.0;
            visVal = currentlyObj.has("visibility") ? currentlyObj.getDouble("visibility") : 0.0;
            ccVal = currentlyObj.has("cloudCover") ? currentlyObj.getDouble("cloudCover") : 0.0;
            ozoneVal = currentlyObj.has("ozone") ? currentlyObj.getDouble("ozone") : 0.0;

            wsText.setText(df.format(wsVal) + " mph");
            presText.setText(df.format(presVal) + " mb");
            prepText.setText(df.format(prepVal) + " mmph");
            tempText.setText(MyUtility.round(tempVal) + "°F");
            if (iconSource.equals("clear-night")) statusIcon.setImageResource(R.drawable.weather_night);
            else if (iconSource.equals("rain")) statusIcon.setImageResource(R.drawable.weather_rainy);
            else if (iconSource.equals("sleet")) statusIcon.setImageResource(R.drawable.weather_snowy_rainy);
            else if (iconSource.equals("snow")) statusIcon.setImageResource(R.drawable.weather_snowy);
            else if (iconSource.equals("wind")) statusIcon.setImageResource(R.drawable.weather_windy);
            else if (iconSource.equals("fog")) statusIcon.setImageResource(R.drawable.weather_fog);
            else if (iconSource.equals("cloudy")) statusIcon.setImageResource(R.drawable.weather_cloudy);
            else if (iconSource.equals("partly-cloudy-night")) statusIcon.setImageResource(R.drawable.weather_night_partly_cloudy);
            else if (iconSource.equals("partly-cloudy-day")) statusIcon.setImageResource(R.drawable.weather_partly_cloudy);
            else statusIcon.setImageResource(R.drawable.weather_sunny);
            statusText.setText(MyUtility.getStatus(iconSource));
            humText.setText(((int)(humVal * 100)) + "%");
            visText.setText(df.format(visVal) + " km");
            ccText.setText(MyUtility.round(ccVal) + "%");
            ozoneText.setText(df.format(ozoneVal) + " DU");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

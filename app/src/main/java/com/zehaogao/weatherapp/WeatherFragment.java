package com.zehaogao.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private TextView temperateVal;
    private TextView temperateStatus;
    private TextView city;
    private TextView humidity;
    private TextView windSpeed;
    private TextView visibility;
    private TextView pressure;
    private ImageView weatherIcon;
    private CardView first_card;
    private CardView third_card;
    private FloatingActionButton favBtn;

    private RecyclerView recycler;
    private List<Weather> weatherList;
    private LinearAdapter linear;

    private JSONObject weatherInfo;
    private String locationURL;
    private String param1;
    private String param2;
    private int param3;

    public WeatherFragment() { }

    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2, int param3) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);
            param3 = getArguments().getInt(ARG_PARAM3);
        }
        try {
            weatherInfo = new JSONObject(param1);
            locationURL = "http://zehaogao-csci571hw8.us-east-2.elasticbeanstalk.com/getCityInfer?city=" + param2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherIcon = view.findViewById(R.id.weather_condition);
        temperateVal = view.findViewById(R.id.temperature_value);
        temperateStatus = view.findViewById(R.id.temperature_status);
        city = view.findViewById(R.id.city_info);
        first_card = view.findViewById(R.id.first_card);
        humidity = view.findViewById(R.id.humidity_value);
        windSpeed = view.findViewById(R.id.wind_speed_value);
        visibility = view.findViewById(R.id.visibility_value);
        pressure = view.findViewById(R.id.gauge_value);
        third_card = view.findViewById(R.id.third_card);
        recycler = view.findViewById(R.id.third_card_single);
        favBtn = view.findViewById(R.id.favorite_weather_fragment);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        weatherList = new ArrayList<>();

        third_card.setVisibility(View.GONE);
        favBtn.hide();

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = param2 + " was removed from favorites";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                MainActivity activity = (MainActivity)getActivity();
                activity.removeOneFragment(param3);
            }
        });

        if (param3 != 0) { favBtn.show(); }

        try {
            String timezone = weatherInfo.has("timezone") ? weatherInfo.getString("timezone") : "";
            String iconSource, summary;
            Double humVal, wsVal, visVal, presVal, tempVal;
            if (weatherInfo.has("currently")) {
                JSONObject currentlyObj = weatherInfo.getJSONObject("currently");
                DecimalFormat df = new DecimalFormat("0.00");
                df.setMaximumFractionDigits(2);
                df.setMinimumFractionDigits(2);
                iconSource = currentlyObj.has("icon") ? currentlyObj.getString("icon") : "";
                summary = currentlyObj.has("summary") ? currentlyObj.getString("summary") : "";
                humVal = currentlyObj.has("humidity") ? currentlyObj.getDouble("humidity") : 0.0;
                wsVal = currentlyObj.has("windSpeed") ? currentlyObj.getDouble("windSpeed") : 0.0;
                visVal = currentlyObj.has("visibility") ? currentlyObj.getDouble("visibility") : 0.0;
                presVal = currentlyObj.has("pressure") ? currentlyObj.getDouble("pressure") : 0.0;
                tempVal = currentlyObj.has("temperature") ? currentlyObj.getDouble("temperature") : 0.0;
                if (iconSource.equals("clear-night"))
                    weatherIcon.setImageResource(R.drawable.weather_night);
                else if (iconSource.equals("rain"))
                    weatherIcon.setImageResource(R.drawable.weather_rainy);
                else if (iconSource.equals("sleet"))
                    weatherIcon.setImageResource(R.drawable.weather_snowy_rainy);
                else if (iconSource.equals("snow"))
                    weatherIcon.setImageResource(R.drawable.weather_snowy);
                else if (iconSource.equals("wind"))
                    weatherIcon.setImageResource(R.drawable.weather_windy);
                else if (iconSource.equals("fog"))
                    weatherIcon.setImageResource(R.drawable.weather_fog);
                else if (iconSource.equals("cloudy"))
                    weatherIcon.setImageResource(R.drawable.weather_cloudy);
                else if (iconSource.equals("partly-cloudy-night"))
                    weatherIcon.setImageResource(R.drawable.weather_night_partly_cloudy);
                else if (iconSource.equals("partly-cloudy-day"))
                    weatherIcon.setImageResource(R.drawable.weather_partly_cloudy);
                else weatherIcon.setImageResource(R.drawable.weather_sunny);
                temperateVal.setText(MyUtility.round(tempVal) + "°F");
                temperateStatus.setText(summary);
                city.setText(param2);
                humidity.setText(((int) (humVal * 100)) + "%");
                windSpeed.setText(df.format(wsVal) + " mph");
                visibility.setText(df.format(visVal) + " km");
                pressure.setText(df.format(presVal) + " mb");

                if (weatherInfo.has("daily")) {
                    JSONArray dailyArr = weatherInfo.getJSONObject("daily").getJSONArray("data");
                    Long unixTime;
                    String subIcon;
                    Double minTemp, maxTemp;
                    for (int i = 0; i < dailyArr.length(); i++) {
                        JSONObject singleObj = dailyArr.getJSONObject(i);
                        unixTime = singleObj.has("time") ? singleObj.getLong("time") : 0L;
                        subIcon = singleObj.has("icon") ? singleObj.getString("icon") : "";
                        minTemp = singleObj.has("temperatureLow") ? singleObj.getDouble("temperatureLow") : 0.0;
                        maxTemp = singleObj.has("temperatureHigh") ? singleObj.getDouble("temperatureHigh") : 0.0;
                        Weather singleWeather = new Weather(unixTime, timezone, subIcon, minTemp, maxTemp);
                        weatherList.add(singleWeather);
                    }

                    linear = new LinearAdapter(getActivity(), weatherList);
                    recycler.setAdapter(linear);
                    third_card.setVisibility(View.VISIBLE);
                    first_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent detailIntent = new Intent(getActivity(), DetailsActivity.class);
                            detailIntent.putExtra("weatherInfo", weatherInfo.toString());
                            detailIntent.putExtra("locationInfo", param2);
                            startActivity(detailIntent);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

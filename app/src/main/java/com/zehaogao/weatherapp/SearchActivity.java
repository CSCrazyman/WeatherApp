package com.zehaogao.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import android.view.View;

public class SearchActivity extends AppCompatActivity {

    private String cityName;
    private String standardCityName;

    private TextView city;
    private ImageView back;

    private ProgressBar progress;
    private TextView progressText;
    private TextView noResults;
    private TextView networkError;
    private ConstraintLayout content;

    private TextView temperateVal;
    private TextView temperateStatus;
    private TextView cityCard;
    private TextView humidity;
    private TextView windSpeed;
    private TextView visibility;
    private TextView pressure;
    private ImageView weatherIcon;
    private CardView first_card;
    private FloatingActionButton favBtn;

    private RecyclerView recycler;
    private List<Weather> weatherList;
    private LinearAdapter linear;

    private JSONObject specificWeatherObj;
    private JSONObject locationObj;
    private String cityURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");

        final SharedPreferences sharedFavs = this.getSharedPreferences("favorites", this.MODE_PRIVATE);
        final SharedPreferences.Editor edt = sharedFavs.edit();
        if (!sharedFavs.contains("TotalFavList")) {
            List<String> list = new ArrayList<>();
            Gson gson = new Gson();
            String json = gson.toJson(list);
            edt.putString("TotalFavList", json);
            edt.apply();
        }

        try {
            cityURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    URLEncoder.encode(cityName, "UTF-8") +
                    "&key=AIzaSyC6jc4kUlf8bts0eCdXtIIz8-XxYcnVR1E";
        } catch (Exception e) {
            e.printStackTrace();
        }

        city = findViewById(R.id.city_name_search);
        back = findViewById(R.id.back3);
        progress = findViewById(R.id.progress3);
        progressText = findViewById(R.id.pending3);
        noResults = findViewById(R.id.no_result3);
        networkError = findViewById(R.id.network_error3);
        content = findViewById(R.id.search_content);
        favBtn = findViewById(R.id.favorite_search);

        weatherIcon = findViewById(R.id.weather_condition_search);
        temperateVal = findViewById(R.id.temperature_value_search);
        temperateStatus = findViewById(R.id.temperature_status_search);
        cityCard = findViewById(R.id.city_info_search);
        first_card = findViewById(R.id.first_card_search);
        humidity = findViewById(R.id.humidity_value_search);
        windSpeed = findViewById(R.id.wind_speed_value_search);
        visibility = findViewById(R.id.visibility_value_search);
        pressure = findViewById(R.id.gauge_value_search);
        recycler = findViewById(R.id.third_card_single_search);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        weatherList = new ArrayList<>();

        city.setText(cityName);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearch();
            }
        });

        noResults.setVisibility(View.GONE);
        networkError.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedFavs.contains(standardCityName)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                    String json = sharedFavs.getString("TotalFavList", "");
                    List<String> realList = gson.fromJson(json, type);
                    if (!realList.get(0).equals(standardCityName)) {
                        realList.remove(standardCityName);
                        json = gson.toJson(realList);

                        edt.putString("TotalFavList", json);
                        edt.remove(standardCityName);
                        edt.apply();

                        String msg = standardCityName + " was removed from favorites";
                        favBtn.setImageResource(R.drawable.favorite);
                        Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String msg = "Current location cannot be removed from favorites";
                        Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                    String json = sharedFavs.getString("TotalFavList", "");
                    List<String> realList = gson.fromJson(json, type);
                    realList.add(standardCityName);
                    json = gson.toJson(realList);

                    edt.putString("TotalFavList", json);
                    edt.putString(standardCityName, specificWeatherObj.toString());
                    edt.apply();
                    String msg = standardCityName + " was added to favorites";
                    favBtn.setImageResource(R.drawable.unfavorite);
                    Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        getCurrent(this, sharedFavs);

    }


    private void getCurrent(final Context context, final SharedPreferences sharedFavs) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, cityURL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    locationObj = response;
                    Double latitude = 0.0, longitude = 0.0;
                    if (locationObj.has("results")) {
                        JSONObject resultObj = locationObj.getJSONArray("results").getJSONObject(0);
                        standardCityName = resultObj.has("formatted_address") ? resultObj.getString("formatted_address") : cityName;
                        if (resultObj.has("geometry")) {
                            JSONObject subResObj = resultObj.getJSONObject("geometry");
                            if (subResObj.has("location")) {
                                JSONObject subsubResObj = subResObj.getJSONObject("location");
                                latitude = subsubResObj.has("lat") ? subsubResObj.getDouble("lat") : 0.0;
                                longitude = subsubResObj.has("lng") ? subsubResObj.getDouble("lng") : 0.0;
                            }
                        }
                    }

                    final String currentURL = "http://zehaogao-csci571hw8.us-east-2.elasticbeanstalk.com/getWeather?latitude="
                            + latitude + "&longitude=" + longitude;

                    RequestQueue queue = Volley.newRequestQueue(context);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, currentURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    noResults.setVisibility(View.GONE);
                                    networkError.setVisibility(View.GONE);
                                    try {
                                        specificWeatherObj = new JSONObject(response);
                                        if (specificWeatherObj != null) {
                                            String timezone = specificWeatherObj.has("timezone") ? specificWeatherObj.getString("timezone") : "";
                                            String iconSource, summary;
                                            Double humVal, wsVal, visVal, presVal, tempVal;
                                            if (specificWeatherObj.has("currently")) {
                                                JSONObject currentlyObj = specificWeatherObj.getJSONObject("currently");
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
                                                else
                                                    weatherIcon.setImageResource(R.drawable.weather_sunny);
                                                temperateVal.setText(MyUtility.round(tempVal) + "°F");
                                                temperateStatus.setText(summary);
                                                cityCard.setText(standardCityName);
                                                humidity.setText(((int) (humVal * 100)) + "%");
                                                windSpeed.setText(df.format(wsVal) + " mph");
                                                visibility.setText(df.format(visVal) + " km");
                                                pressure.setText(df.format(presVal) + " mb");

                                                if (specificWeatherObj.has("daily")) {
                                                    JSONArray dailyArr = specificWeatherObj.getJSONObject("daily").getJSONArray("data");
                                                    Long unixTime;
                                                    String subIcon;
                                                    Double minTemp, maxTemp;
                                                    for (int i = 0 ; i < dailyArr.length() ; i++) {
                                                        JSONObject singleObj = dailyArr.getJSONObject(i);
                                                        unixTime = singleObj.has("time") ? singleObj.getLong("time") : 0L;
                                                        subIcon = singleObj.has("icon") ? singleObj.getString("icon") : "";
                                                        minTemp = singleObj.has("temperatureLow") ? singleObj.getDouble("temperatureLow") : 0.0;
                                                        maxTemp = singleObj.has("temperatureHigh") ? singleObj.getDouble("temperatureHigh") : 0.0;
                                                        Weather singleWeather = new Weather(unixTime, timezone, subIcon, minTemp, maxTemp);
                                                        weatherList.add(singleWeather);
                                                    }

                                                    linear = new LinearAdapter(getBaseContext(), weatherList);
                                                    recycler.setAdapter(linear);
                                                    first_card.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent detailIntent = new Intent(getBaseContext(), DetailsActivity.class);
                                                            detailIntent.putExtra("weatherInfo", specificWeatherObj.toString());
                                                            detailIntent.putExtra("locationInfo", standardCityName);
                                                            startActivity(detailIntent);
                                                        }
                                                    });
                                                    favBtn.setImageResource(sharedFavs.contains(standardCityName) ? R.drawable.unfavorite : R.drawable.favorite);
                                                    progress.setVisibility(View.GONE);
                                                    progressText.setVisibility(View.GONE);
                                                    noResults.setVisibility(View.GONE);
                                                    networkError.setVisibility(View.GONE);
                                                    content.setVisibility(View.VISIBLE);
                                                }
                                                else {
                                                    throw new Exception();
                                                }
                                            }
                                            else {
                                                throw new Exception();
                                            }
                                        }
                                        else {
                                            throw new Exception();
                                        }
                                    } catch (Exception e) {
                                        networkError.setVisibility(View.GONE);
                                        progress.setVisibility(View.GONE);
                                        progressText.setVisibility(View.GONE);
                                        noResults.setVisibility(View.VISIBLE);
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            noResults.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "Please go back and check network...",
                                    Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            networkError.setVisibility(View.VISIBLE);
                        }
                    });

                    queue.add(stringRequest);

                } catch (Exception e) {
                    noResults.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Please go back and check network...",
                            Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    networkError.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                noResults.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "Please go back and check network...",
                        Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                networkError.setVisibility(View.VISIBLE);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjRequest);
    }

    private void closeSearch() {
        finish();
    }

}

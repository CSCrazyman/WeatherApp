package com.zehaogao.weatherapp;

import java.lang.reflect.Type;
import java.util.*;
import android.os.*;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.*;
import android.view.*;
import org.json.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.content.Intent;
import java.lang.String;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class MainActivity extends AppCompatActivity {

    private TextView appName;
    private ImageView search;
    private ImageView back;
    private ImageView cross;
    private AutoCompleteTextView location;
    private ConstraintLayout locationBox;
    private ConstraintLayout locationBoxExtra;
    private ProgressBar progress;
    private TextView pending;
    private TextView noResults;
    private TextView networkError;

    private String cityName;
    private boolean pressBack = false;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    private ViewPager viewPager;
    private SectionsPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    private JSONObject currentInfo;
    private JSONObject currentWeatherInfo;
    private List<WeatherFragment> weatherFragmentList = new ArrayList<>();

    private SharedPreferences sharedFavs;
    private SharedPreferences.Editor edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = findViewById(R.id.content);
        appName = findViewById(R.id.app_name);
        search = findViewById(R.id.search_button);
        back = findViewById(R.id.back_search);
        location = findViewById(R.id.search_input);
        locationBox = findViewById(R.id.search_input_box);
        locationBoxExtra = findViewById(R.id.search_input_box_extra);
        cross = findViewById(R.id.cross);
        progress = findViewById(R.id.progress1);
        pending = findViewById(R.id.pending1);
        noResults = findViewById(R.id.no_result1);
        networkError = findViewById(R.id.network_error1);
        tabLayout = findViewById(R.id.tab_layout);

        progress.setVisibility(View.VISIBLE);
        pending.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.GONE);
        networkError.setVisibility(View.GONE);

        sharedFavs = this.getSharedPreferences("favorites", this.MODE_PRIVATE);
        edt = sharedFavs.edit();
        if (!sharedFavs.contains("TotalFavList")) {
            List<String> list = new ArrayList<>();
            Gson gson = new Gson();
            String json = gson.toJson(list);
            edt.putString("TotalFavList", json);
            edt.apply();
        }
        else {
            String json = sharedFavs.getString("TotalFavList", "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> realList = gson.fromJson(json, type);
            for (int i = 0 ; i < realList.size() ; i++) {
                weatherFragmentList.add(WeatherFragment.newInstance(sharedFavs.getString(realList.get(i),""), realList.get(i), i));
            }

        }

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), weatherFragmentList);
        
        final AppCompatAutoCompleteTextView autoCompleteTextView = findViewById(R.id.search_input);
        final TextView selectedText = findViewById(R.id.selected_item);
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectedText.setText(autoSuggestAdapter.getObject(position));
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressBack = true;
                search.setVisibility(View.VISIBLE);
                appName.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                locationBoxExtra.setVisibility(View.GONE);
                locationBox.setVisibility(View.GONE);
                location.setVisibility(View.GONE);
                location.setText("");
                cross.setVisibility(View.GONE);
                pressBack = false;
            }
        });


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location.setText("");
                cross.setVisibility(View.GONE);
                locationBoxExtra.setVisibility(View.VISIBLE);
            }
        });

        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!location.getText().toString().trim().equals("")) {
                    locationBoxExtra.setVisibility(View.GONE);
                    cross.setVisibility(View.VISIBLE);
                }
                else if (!pressBack) {
                    locationBoxExtra.setVisibility(View.VISIBLE);
                    cross.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompleteTextView.setOnKeyListener(
                new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            // Perform action on key press
                            String cityVal = location.getText().toString().trim();
                            if (!cityVal.equals("")) {
                                Intent searchIntent = new Intent(getBaseContext(), SearchActivity.class);
                                searchIntent.putExtra("cityName", cityVal);
                                startActivity(searchIntent);
                            }
                            return true;
                        }
                        return false;
                }
        });

        getCurrent(this);

    }

    private void getCurrent(final Context context) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, "http://ip-api.com/json",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    currentInfo = response;
                    final String currentURL = "http://zehaogao-csci571hw8.us-east-2.elasticbeanstalk.com/getWeather?latitude="
                        + currentInfo.get("lat").toString() + "&longitude=" + currentInfo.get("lon").toString();

                    cityName = "Los Angeles, CA, USA";

                        RequestQueue queue = Volley.newRequestQueue(context);

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, currentURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        noResults.setVisibility(View.GONE);
                                        networkError.setVisibility(View.GONE);
                                        try {
                                            currentWeatherInfo = new JSONObject(response);
                                            if (currentWeatherInfo != null) {
                                                progress.setVisibility(View.GONE);
                                                pending.setVisibility(View.GONE);
                                                if (!sharedFavs.contains(cityName)) {
                                                    Gson gson = new Gson();
                                                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                                                    String json = sharedFavs.getString("TotalFavList", "");
                                                    List<String> realList = gson.fromJson(json, type);
                                                    realList.add(cityName);
                                                    json = gson.toJson(realList);
                                                    edt.putString("TotalFavList", json);
                                                    edt.putString(cityName, currentWeatherInfo.toString());
                                                    edt.apply();
                                                }
                                                else {
                                                    edt.putString(cityName, currentWeatherInfo.toString());
                                                    edt.apply();
                                                }
                                                viewPager.setAdapter(pagerAdapter);
                                                tabLayout.setupWithViewPager(viewPager, true);
                                                search.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        search.setVisibility(View.GONE);
                                                        appName.setVisibility(View.GONE);
                                                        back.setVisibility(View.VISIBLE);
                                                        location.setText("");
                                                        location.setVisibility(View.VISIBLE);
                                                        locationBox.setVisibility(View.VISIBLE);
                                                        locationBoxExtra.setVisibility(View.VISIBLE);
                                                        cross.setVisibility(View.GONE);
                                                    }
                                                });
                                                viewPager.setVisibility(View.VISIBLE);
                                            }
                                            else {
                                                throw new Exception();
                                            }
                                        } catch (Exception e) {
                                            networkError.setVisibility(View.GONE);
                                            progress.setVisibility(View.GONE);
                                            pending.setVisibility(View.GONE);
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
                                pending.setVisibility(View.GONE);
                                networkError.setVisibility(View.VISIBLE);
                            }
                        });

                        queue.add(stringRequest);

                } catch (Exception e) {
                    noResults.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Please go back and check network...",
                            Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    pending.setVisibility(View.GONE);
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
                pending.setVisibility(View.GONE);
                networkError.setVisibility(View.VISIBLE);
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjRequest);
    }

    private void makeApiCall(String text) {
        ApiCall.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("predictions")) {
                        JSONArray predictions = responseObject.getJSONArray("predictions");
                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject oneDescription = predictions.getJSONObject(i);
                            stringList.add(oneDescription.getString("description"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<WeatherFragment> weathers;
        private long baseId = 0;

        public SectionsPagerAdapter(FragmentManager fm, List<WeatherFragment> weathers) {
            super(fm);
            this.weathers = weathers;
        }

        @Override
        public Fragment getItem(int position) {
            return weathers.get(position);
        }

        @Override
        public int getCount() {
            return weathers.size();
        }

        @Override
        public int getItemPosition(Object object){
            int index = weathers.indexOf (object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        weatherFragmentList.clear();
        String json = sharedFavs.getString("TotalFavList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> realList = gson.fromJson(json, type);
        for (int i = 0 ; i < realList.size() ; i++) {
            weatherFragmentList.add(WeatherFragment.newInstance(sharedFavs.getString(realList.get(i),""), realList.get(i), i));
        }
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), weatherFragmentList);
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();
    }

    public void removeOneFragment(int position) {
        weatherFragmentList.clear();
        String json = sharedFavs.getString("TotalFavList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> realList = gson.fromJson(json, type);
        edt.remove(realList.get(position));
        edt.commit();
        realList.remove(position);
        json = gson.toJson(realList);
        edt.putString("TotalFavList", json);
        edt.commit();
        for (int i = 0 ; i < realList.size() ; i++) {
            weatherFragmentList.add(WeatherFragment.newInstance(sharedFavs.getString(realList.get(i),""), realList.get(i), i));
        }
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), weatherFragmentList);
        viewPager.setAdapter(pagerAdapter);
        if (weatherFragmentList.size() != 1)
            pagerAdapter.notifyChangeInPosition(1);
        pagerAdapter.notifyDataSetChanged();
    }
}
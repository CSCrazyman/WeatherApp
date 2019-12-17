package com.zehaogao.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.net.URLEncoder;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class DetailsActivity extends AppCompatActivity {

    private String weatherInfo;
    private String locationInfo;
    private String photosURL;
    private JSONObject photosObj;
    private JSONObject weatherObj;

    private TextView cityName;
    private ImageView back;
    private ImageView twitter;
    private ProgressBar progress;
    private TextView progressText;
    private TextView noResults;
    private TextView networkError;
    private TabLayout tabs;
    private ViewPager content;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        weatherInfo = intent.getStringExtra("weatherInfo");
        locationInfo = intent.getStringExtra("locationInfo");
        try{
            weatherObj = new JSONObject(weatherInfo);
            photosURL = "https://www.googleapis.com/customsearch/v1?q=" +
                    URLEncoder.encode(locationInfo, "UTF-8") + "&cx=007670190240247653046:ux8qiyguti4&imgSize=huge&imgType=news&num=8&searchType=image&key=AIzaSyDGsg_HoAwCaoeRIqz0rgiy_nYQPsKZA-I";
        } catch(Exception e) {
            e.printStackTrace();
        }

        cityName = findViewById(R.id.city_name);
        back = findViewById(R.id.back2);
        progress = findViewById(R.id.progress2);
        progressText = findViewById(R.id.pending2);
        noResults = findViewById(R.id.no_result2);
        networkError = findViewById(R.id.network_error2);
        twitter = findViewById(R.id.twitter);
        tabs = findViewById(R.id.detail_tabs);
        content = findViewById(R.id.detail_content);
        cityName.setText(locationInfo);
        progress.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.GONE);
        networkError.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        tabs.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDetails();
            }
        });

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        content.setAdapter(sectionsPagerAdapter);
        content.setOffscreenPageLimit(3);

        content.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(content));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                tabs.getTabAt(0).setIcon((position == 0 ? R.drawable.today_chosen : R.drawable.today_unchosen));
                tabs.getTabAt(1).setIcon((position == 1 ? R.drawable.weekly_chosen : R.drawable.weekly_unchosen));
                tabs.getTabAt(2).setIcon((position == 2 ? R.drawable.photos_chosen : R.drawable.photos_unchosen));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

        try {
            Double tempVal;
            JSONObject obj = weatherObj.getJSONObject("currently");
            tempVal = obj.has("temperature") ? obj.getDouble("temperature") : 0.0;
            final String twitterURL = "https://twitter.com/intent/tweet?text=" +
                    URLEncoder.encode("Check Out " + locationInfo + "'s Weather! " +
                            "It is " + MyUtility.round(tempVal) + "°F! " +
                            "#CSCI571WeatherSearch ", "UTF-8");
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentShare = new Intent(Intent.ACTION_VIEW);
                    intentShare.setData(Uri.parse(twitterURL));
                    startActivity(intentShare);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, photosURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            photosObj = new JSONObject(response);
                            if (photosObj != null) {
                                progress.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);
                                noResults.setVisibility(View.GONE);
                                networkError.setVisibility(View.GONE);
                                tabs.setVisibility(View.VISIBLE);
                                content.setVisibility(View.VISIBLE);
                            }
                            else {
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            networkError.setVisibility(View.GONE);
                            tabs.setVisibility(View.GONE);
                            content.setVisibility(View.GONE);
                            progress.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            noResults.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        noResults.setVisibility(View.GONE);
                        tabs.setVisibility(View.GONE);
                        content.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Please go back and check network...",
                                Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        networkError.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
            });

        queue.add(stringRequest);

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return TodayFragment.newInstance(weatherInfo);
                case 1: return WeeklyFragment.newInstance(weatherInfo);
                case 2: return PhotoFragment.newInstance(photosObj.toString());
            }
            return TodayFragment.newInstance(weatherInfo);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void closeDetails() {
        finish();
    }
}
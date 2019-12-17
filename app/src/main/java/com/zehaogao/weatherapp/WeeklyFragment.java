package com.zehaogao.weatherapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;


public class WeeklyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";

    private String param1;
    private JSONObject weatherObj;

    private ImageView statusIcon;
    private TextView statusText;

    private LineChart chart;

    public WeeklyFragment() { }

    // TODO: Rename and change types and number of parameters
    public static WeeklyFragment newInstance(String param1) {
        WeeklyFragment fragment = new WeeklyFragment();
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
        return inflater.inflate(R.layout.fragment_weekly, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusIcon = view.findViewById(R.id.detail_weekly_icon);
        statusText = view.findViewById(R.id.detail_weekly_summary);
        chart = view.findViewById(R.id.detail_chart);
        Legend legend = chart.getLegend();
        chart.getAxisLeft().setTextColor(Color.rgb(144, 143, 144));
        chart.getAxisRight().setTextColor(Color.rgb(144, 143, 144));
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getXAxis().setDrawAxisLine(false);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(15f);
        legend.setFormSize(14f);
        legend.setXEntrySpace(18f);

        try {
            String iconSource, summary;
            JSONObject dailyObj = weatherObj.getJSONObject("daily");
            JSONArray dailyArr = dailyObj.getJSONArray("data");
            List<Entry> maxTempList = new ArrayList<>();
            List<Entry> minTempList = new ArrayList<>();
            iconSource = dailyObj.has("icon") ? dailyObj.getString("icon") : "";
            summary = dailyObj.has("summary") ? dailyObj.getString("summary") : "";
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
            statusText.setText(summary);
            for (int i = 0 ; i < dailyArr.length() ; i++) {
                JSONObject singleObj = dailyArr.getJSONObject(i);
                Double minTempVal, maxTempVal;
                minTempVal = singleObj.has("temperatureLow") ? singleObj.getDouble("temperatureLow") : 0.0;
                maxTempVal = singleObj.has("temperatureHigh") ? singleObj.getDouble("temperatureHigh") : 0.0;
                Entry firstEntry = new Entry(i, MyUtility.round(maxTempVal));
                Entry secondEntry = new Entry(i, MyUtility.round(minTempVal));
                maxTempList.add(firstEntry);
                minTempList.add(secondEntry);
            }

            LineDataSet setMax = new LineDataSet(maxTempList, "Maximum Temperature");
            LineDataSet setMin = new LineDataSet(minTempList, "Minimum Temperature");
            setMax.setColors(new int[] {R.color.maxTemperature}, getContext());
            setMin.setColors(new int[] {R.color.minTemperature}, getContext());

            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(setMin);
            dataSets.add(setMax);
            LineData data = new LineData(dataSets);

            chart.setData(data);
            chart.invalidate(); // refresh

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

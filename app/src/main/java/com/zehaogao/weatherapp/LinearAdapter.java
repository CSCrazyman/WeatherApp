package com.zehaogao.weatherapp;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.List;

public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> {

    private Context context;
    private List<Weather> allWeather;

    public LinearAdapter(Context context, List<Weather> allWeather) {
        this.context = context;
        this.allWeather = allWeather;
    }

    @NonNull
    @Override
    public LinearAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.single_weather_record, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final LinearAdapter.LinearViewHolder viewHolder, final int i) {
        if (i > 0) {
            setMargins(viewHolder.container, 0, 2, 0, 0 );
        }
        Weather single = allWeather.get(i);
        String subIcon = single.getCondition();

        // Set single record
        viewHolder.date.setText(single.getDate());
        if (subIcon.equals("clear-night")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_night);
        else if (subIcon.equals("rain")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_rainy);
        else if (subIcon.equals("sleet")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_snowy_rainy);
        else if (subIcon.equals("snow")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_snowy);
        else if (subIcon.equals("wind")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_windy);
        else if (subIcon.equals("fog")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_fog);
        else if (subIcon.equals("cloudy")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_cloudy);
        else if (subIcon.equals("partly-cloudy-night")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_night_partly_cloudy);
        else if (subIcon.equals("partly-cloudy-day")) viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_partly_cloudy);
        else viewHolder.weatherSubIcon.setImageResource(R.drawable.weather_sunny);
        viewHolder.minTemp.setText(single.getMinTemp() + "");
        viewHolder.maxTemp.setText(single.getMaxTemp() + "");
    }

    @Override
    public int getItemCount() { return allWeather.size(); }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private TextView date;
        private TextView maxTemp;
        private TextView minTemp;
        private ImageView weatherSubIcon;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.single_weather_info);
            date = itemView.findViewById(R.id.date);
            weatherSubIcon = itemView.findViewById(R.id.date_weather);
            minTemp = itemView.findViewById(R.id.min_temp);
            maxTemp = itemView.findViewById(R.id.max_temp);
        }
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}

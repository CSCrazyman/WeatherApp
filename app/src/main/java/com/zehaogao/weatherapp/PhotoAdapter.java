package com.zehaogao.weatherapp;

import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;
import java.util.List;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.support.v7.widget.RecyclerView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<String> photosUrl;

    public PhotoAdapter(Context context, List<String> photosUrl) {
        this.context = context;
        this.photosUrl = photosUrl;
    }

    @NonNull
    @Override
    public PhotoAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhotoAdapter.PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.single_google_photo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoAdapter.PhotoViewHolder viewHolder, final int i) {
        if (i > 0) {
            setMargins(viewHolder.container, 0, 30, 0, 0 );
        }
        String url = photosUrl.get(i);
        if (!url.equals("")) {
            Picasso.with(context).load(url).resize(400, 240).into(viewHolder.singlePhoto, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    viewHolder.singlePhoto.setImageResource(R.drawable.no_photo);
                }
            });
        }
        else {
            viewHolder.singlePhoto.setImageResource(R.drawable.no_photo);
        }
    }

    @Override
    public int getItemCount() { return photosUrl.size(); }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView singlePhoto;
        private ConstraintLayout container;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.ggggoole);
            singlePhoto = itemView.findViewById(R.id.google_single_photo_image);
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


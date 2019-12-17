package com.zehaogao.weatherapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";

    private String param1;
    private JSONObject photoObj;

    private RecyclerView recycler;
    private List<String> photoUrlList;
    private PhotoAdapter photos;

    public PhotoFragment() { }

    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1) {
        PhotoFragment fragment = new PhotoFragment();
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
            photoObj = new JSONObject(param1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = view.findViewById(R.id.google_photo_container);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        photoUrlList = new ArrayList<>();

        try {
            if (photoObj.has("items")) {
                JSONArray photoArr = photoObj.getJSONArray("items");
                for (int i = 0 ; i < photoArr.length() ; i++) {
                    String url = photoArr.getJSONObject(i).has("link") ? photoArr.getJSONObject(i).getString("link") : "";
                    photoUrlList.add(url);
                }
                photos = new PhotoAdapter(getActivity(), photoUrlList);
                recycler.setAdapter(photos);
            }
            else {
                throw new Exception();
            }
        } catch (Exception e) {

        }
    }
}

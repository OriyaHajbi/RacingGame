package com.example.racinggame;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;


public class ListFragment extends Fragment {

    private RecyclerView list_RV_records;
    private CallBack_List callbackList;
    private AppCompatActivity activity;
    private MyDB myDB;

    public void setCallbackList(CallBack_List callbackList) {
        this.callbackList = callbackList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list , container , false);

        String fromMSP = MSP.getInstance(activity).getStrSP("MY_DB", "");
        myDB = new Gson().fromJson(fromMSP, MyDB.class);

        list_RV_records = view.findViewById(R.id.list_RV_records);
        RecordAdapter recordAdapter = new RecordAdapter(this, myDB.getRecords());

        recordAdapter.setRecordItemClickedListener(new RecordAdapter.RecordItemClickedListener() {
            @Override
            public void RecordItemClicked(Record record, int position) {
            }
        });

        list_RV_records.setLayoutManager(new LinearLayoutManager(this.getContext() , LinearLayoutManager.VERTICAL , false));
        list_RV_records.setHasFixedSize(true);
        list_RV_records.setItemAnimator(new DefaultItemAnimator());
        list_RV_records.setAdapter(recordAdapter);
        recordAdapter.setRecordItemClickedListener(new RecordAdapter.RecordItemClickedListener() {
            @Override
            public void RecordItemClicked(Record record, int position) {
                if (callbackList != null){
                    double lat = record.getLat();
                    double lon = record.getLon();
                    callbackList.setMapLocation(lat, lon);
                }
            }
        });
        return view;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}
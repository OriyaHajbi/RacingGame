package com.example.racinggame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

public class ScoreMapActivity extends AppCompatActivity {


    private ImageButton score_BTN_back;
    private MyDB myDB;
    private String fromJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_map);

        findViews();

        //Read Data From MSP
        fromJSON = MSP.getInstance(this).getStrSP("MY_DB", "");
        myDB = new Gson().fromJson(fromJSON, MyDB.class);
        Log.d("ptttt", "Records: " + fromJSON);

        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.score_FRM_map, mapFragment).commit();

        ListFragment listFragment = new ListFragment();
        listFragment.setActivity(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.score_FRM_score, listFragment).commit();

        CallBack_List callBack_list = new CallBack_List() {
            @Override
            public void setMapLocation(double lat, double lon) {
                mapFragment.changeMap(lat, lon);
            }
        };

        listFragment.setActivity(this);
        listFragment.setCallbackList(callBack_list);


        score_BTN_back.setOnClickListener(V -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        });


    }

    private void findViews() {
        score_BTN_back = findViewById(R.id.score_BTN_back);
        score_BTN_back.setImageResource(R.drawable.backarrow);
//        mapFragment = findViewById(R.id.map);

    }
}
package com.example.racinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

public class MenuActivity extends AppCompatActivity {

    private MaterialButton menu_BTN_playgame;
    private MaterialButton menu_BTN_score;
    private SwitchMaterial menu_SWT_sensor;
    private SwitchMaterial menu_SWT_sound;
    private MediaPlayer music;

    private boolean isSoundOn = false;
    private boolean isSensorOn = false;

    private MyDB myDB;
    private String fromJSON;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();

        //Read Data From MSP
        if (MSP.getInstance(this) != null) {
            fromJSON = MSP.getInstance(this).getStrSP("MY_DB", "");
            myDB = new Gson().fromJson(fromJSON, MyDB.class);
        }



        menu_BTN_score.setOnClickListener(V-> {
            Intent intent = new Intent(this , ScoreMapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(GameActivity.SCORE , fromJSON);
            startActivity(intent);
            finish();
        });

        menu_SWT_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSoundOn = isChecked;
                playSound();
            }
        });

        menu_SWT_sensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSensorOn = isChecked;

            }
        });
        menu_BTN_playgame.setOnClickListener(V-> {
            Intent intent = new Intent(this , GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(GameActivity.SOUND ,isSoundOn );
            bundle.putBoolean(GameActivity.SENSOR ,isSensorOn );
            bundle.putString(GameActivity.SCORE , fromJSON);
            intent.putExtra("bundle" , bundle);
            startActivity(intent);
            finish();

        });
    }

    private void playSound() {
        if (music==null){
            music = MediaPlayer.create(this , R.raw.needforspeed);
            music.start();
            music.setLooping(true);
        }else{
            music.release();
            music=null;
        }
    }

    private void findViews() {
        menu_BTN_playgame = findViewById(R.id.menu_BTN_playGame);
        menu_BTN_score = findViewById(R.id.menu_BTN_score);
        menu_SWT_sensor = findViewById(R.id.menu_SWT_sensor);
        menu_SWT_sound = findViewById(R.id.menu_SWT_sound);
    }
}
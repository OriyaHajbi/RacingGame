package com.example.racinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class MenuActivity extends AppCompatActivity {

    private MaterialButton menu_BTN_playgame;
    private MaterialButton menu_BTN_score;
    private SwitchMaterial menu_SWT_sensor;
    private SwitchMaterial menu_SWT_sound;
    private MediaPlayer music;
    private TextInputEditText menu_LBL_PlayerName;

    private boolean isSoundOn = false;
    private boolean isSensorOn = false;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat;
    private double lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();


        menu_BTN_score.setOnClickListener(V-> {
            Intent intent = new Intent(this , ScoreMapActivity.class);
            Bundle bundle = new Bundle();
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

            if (!isEmpty(menu_LBL_PlayerName)){
                Intent intent = new Intent(this , GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(GameActivity.SOUND ,isSoundOn );
                bundle.putBoolean(GameActivity.SENSOR ,isSensorOn );
                bundle.putDouble(GameActivity.LATITUDE , lat);
                bundle.putDouble(GameActivity.LATITUDE , lon);
                bundle.putString(GameActivity.PLAYER_NAME , menu_LBL_PlayerName.getText().toString());
                intent.putExtra("bundle" , bundle);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "You must fill PlayerName", Toast.LENGTH_LONG).show();

//                toast("You must fill PlayerName");
                Log.d("pttt" , "haaaaaaaaaaaaa");
            }



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
        menu_LBL_PlayerName = findViewById(R.id.menu_LBL_PlayerName);
    }
    private boolean isEmpty(TextInputEditText text) {
        if (text.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
    private void locationPermission() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
        }
    }
}
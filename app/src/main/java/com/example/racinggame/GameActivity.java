package com.example.racinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private ImageView[] main_IMG_Heart;
    private int numHeart =3;

    private LinearLayout main_LL_arrows;
    private ImageButton[] main_BTN_Arrows;
    private final int MOVE_LEFT =0;
    private final int MOVE_RIGHT =1;

    private ImageView[][] main_IMG_Matrix;
    private int [][] main_Num_Pic_In_Matrix;
    private final int ROCK_IN_ROW=5;
    private final int ROCK_IN_COL=7;

    private TextView lblScore;
    private int score=0;
    

    private  String[] main_STR_Types_In_Matrix;

    private ImageView[] main_IMG_RacingCar;
    private int currectIndexCar =2;
    private int beforeCurrectIndexCar =0;

    private ImageView main_IMG_arrows;

    private ImageView main_IMG_Background;

    public static String SOUND = "SOUND";
    public static String SENSOR = "SENSOR";
    private Boolean isSoundOn = false;
    private Boolean isSensorOn = false;

    private Timer timer;
    private static final int REGULAR_DELAY =1000;
    private static final int FAST_DELAY=1500;
    private static final int EASY_DELAY=500;
    private int speed = REGULAR_DELAY;


    Random r = new Random();

    public static final String SCORE = "SCORE";
    private MyDB myDB;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat;
    private double lon;

    private SensorManager sensorManager;
    private Sensor sensor;
    private final double MOVE_RIGHT_LIMIT=1.5;
    private final double MOVE_LEFT_LIMIT=-1.5;
    private double x;
    private double y;
    private double z;
    private Handler handler;
    private Runnable runnable;
    private Boolean isHandlerStop = false;
    private Boolean lock = true;
    private SensorEventListener accSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            x = (int) event.values[0];
            y = (int) event.values[1];
            z = (int) event.values[2];

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Sound && sensor
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        isSoundOn = bundle.getBoolean(SOUND);
        isSensorOn = bundle.getBoolean(SENSOR);

        String fromJSON = bundle.getString(SCORE);


        //Read Data From MSP
        if (MSP.getInstance(this) != null){
            myDB = new Gson().fromJson(fromJSON,MyDB.class);
        }else{
            myDB= new MyDB();
        }



        findViews();
        setViews();
        initSensor();
        locationPermission();
        startGame();
        initHandler();



        if (isSensorOn){
            setArrowGone();
            moveCarWithSensors(MOVE_RIGHT);
        }
        main_BTN_Arrows[MOVE_LEFT].setOnClickListener(V->{
            moveCarWithArrows(MOVE_LEFT);
        });
        main_BTN_Arrows[MOVE_RIGHT].setOnClickListener(V->{
            moveCarWithArrows(MOVE_RIGHT);
        });


    }

    private void initHandler() {
        if (isSensorOn){
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    double currentX = x;
                    if (currentX < MOVE_RIGHT_LIMIT)
                        moveCarWithSensors(MOVE_RIGHT);
                    if (currentX > MOVE_LEFT_LIMIT)
                        moveCarWithSensors(MOVE_LEFT);
                    double currentY = y;
                    if (currentY > 1)
                        speed = FAST_DELAY;
                    if (currentY < -1)
                        speed = EASY_DELAY;

                    handler.postDelayed(this , 400);
                    if (isHandlerStop)
                        handler.removeCallbacks(runnable);
                }
            };
            handler.post(runnable);
        }
    }

    private void moveCarWithSensors(int diraction) {
        moveCarWithArrows(diraction);
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

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accSensorEventListener , sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accSensorEventListener);
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }
    public  boolean isSensorExists(int sensorType){
        return (sensorManager.getDefaultSensor(sensorType) != null);
    }

    private void moveCarWithArrows(int direction) {
        int nextIndexCar =0;

        if (direction == 1){
            nextIndexCar = currectIndexCar+1;
            if (nextIndexCar == 5)
                nextIndexCar =4;
            setCarIndexVisible(nextIndexCar ,currectIndexCar);

        }else{
            nextIndexCar = currectIndexCar-1;
            if (nextIndexCar == -1)
                nextIndexCar =0;
            setCarIndexVisible(nextIndexCar ,currectIndexCar);
        }
        beforeCurrectIndexCar = currectIndexCar;
        currectIndexCar = nextIndexCar;



    }

    private void setArrowGone() {
        main_BTN_Arrows[0].setVisibility(View.GONE);
        main_BTN_Arrows[1].setVisibility(View.GONE);
        main_IMG_arrows.setVisibility(View.GONE);
        main_LL_arrows.setVisibility(View.GONE);
    }

    private void setCarIndexVisible(int nextIndexCar , int currectIndex) {
        main_IMG_RacingCar[currectIndex].setVisibility(View.INVISIBLE);
        main_IMG_RacingCar[nextIndexCar].setVisibility(View.VISIBLE);
    }

    private void startGame() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() ->{
                    playGame();
                });
            }
        },0, speed);
    }

    private void playGame() {
        downRocks();
        randomPic();
        setViewInMatrix();
    }

    private void randomPic() {
        int colIndex = r.nextInt(7);
        int typeView = r.nextInt(5);
        switch (colIndex){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                setOnePicInCol(colIndex ,typeView);
                break;
        }
    }

    private void setOnePicInCol(int colIndex, int typeView) {
        setminusOne(0);
        main_Num_Pic_In_Matrix[0][colIndex] = typeView;
    }

    private void setminusOne(int row) {
        for (int i=0; i<ROCK_IN_ROW; i++)
            main_Num_Pic_In_Matrix[row][i] = -1;
    }

    private void setViewInMatrix() {
        for (int i=0; i<ROCK_IN_COL; i++){
            for (int j=0; j<ROCK_IN_ROW; j++){
                if (main_Num_Pic_In_Matrix[i][j] != -1){
                    int imageID = getResources().getIdentifier(main_STR_Types_In_Matrix[main_Num_Pic_In_Matrix[i][j]] , "drawable", getPackageName());
                    main_IMG_Matrix[i][j].setImageResource(imageID);
                    main_IMG_Matrix[i][j].setVisibility(View.VISIBLE);
                }else{
                    main_IMG_Matrix[i][j].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void downRocks(){
        for (int i= ROCK_IN_COL-1 ; i>0;i--){
            for (int j=ROCK_IN_ROW-1; j>=0;j--) {
                if (i == ROCK_IN_COL - 1 && main_Num_Pic_In_Matrix[i][j] != -1) {
                    checkCar(j, main_Num_Pic_In_Matrix[i][j]);
                }
                main_Num_Pic_In_Matrix[i][j] = main_Num_Pic_In_Matrix[i - 1][j];
                main_Num_Pic_In_Matrix[i - 1][j] = -1;
            }
        }
    }

    private void checkCar(int col, int type) {

        if (getIndexCar() == col){
            if (type == 0){
                getMoney();
            }else{
                vibrate();
                removeHealth();
            }
        }
    }

    private void getMoney() {
        score += r.nextInt(50) + 20;
        lblScore.setText(" " + score);
    }

    private void removeHealth() {
        makeSound();
        main_IMG_Heart[numHeart-1].setVisibility(View.INVISIBLE);
        numHeart--;
        if (numHeart == 0) {
            gameOver();
        }
        toest("you have " + numHeart +" Lives");
    }

    private void makeSound() {
        if (isSoundOn){
            MediaPlayer music = MediaPlayer.create(GameActivity.this, R.raw.videogamebombalert);
            music.start();
        }
    }

    private void gameOver() {
        if (isSensorOn)
            isHandlerStop=true;
        checkAndSaveRecord();
        timer.cancel();
        Intent intent = new Intent(this , ScoreMapActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkAndSaveRecord() {
        myDB.checkRecord(new Record().setName("Oriya").setScore(score).setLat(lat).setLon(lon));

        //save to MSP
        String jsonRecords = new Gson().toJson(myDB);
        MSP.getInstance(this).putStringSP("MY_DB" ,jsonRecords);
    }

    private void toest(String text) {
        Toast.makeText(this , text, Toast.LENGTH_LONG).show();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }

    private void setViews() {
        setHeartsOnView();
        setIMBTNArrowsOnView();
        setStonesOnView();
        setRacingCarsView();
        setMatrixs();
    }

    private void setRacingCarsView() {
        main_IMG_RacingCar = new ImageView[] {
                findViewById(R.id.main_IMG_RacingCar0),
                findViewById(R.id.main_IMG_RacingCar1),
                findViewById(R.id.main_IMG_RacingCar2),
                findViewById(R.id.main_IMG_RacingCar3),
                findViewById(R.id.main_IMG_RacingCar4)};
        main_IMG_RacingCar[0].setVisibility(View.INVISIBLE);
        main_IMG_RacingCar[1].setVisibility(View.INVISIBLE);
        main_IMG_RacingCar[2].setVisibility(View.VISIBLE);
        main_IMG_RacingCar[3].setVisibility(View.INVISIBLE);
        main_IMG_RacingCar[4].setVisibility(View.INVISIBLE);

        setCarIndexVisible(currectIndexCar, beforeCurrectIndexCar);

    }

    private void setStonesOnView() {
        for (int i=0; i<ROCK_IN_COL; i++)
            for (int j=0; j<ROCK_IN_ROW; j++)
                main_IMG_Matrix[i][j].setVisibility(View.INVISIBLE);
    }

    private void setIMBTNArrowsOnView() {
        ((ImageView) main_BTN_Arrows[0]).setImageResource(R.drawable.leftarrow);
        ((ImageView) main_BTN_Arrows[1]).setImageResource(R.drawable.rightarrow);
    }

    private void setHeartsOnView() {
        for(int i=0; i<3; i++)
            main_IMG_Heart[i].setVisibility(View.VISIBLE);
    }

    private void findViews() {
        main_LL_arrows = findViewById(R.id.main_LL_arrows);
        main_IMG_Heart = new ImageView[] {findViewById(R.id.main_IMG_Heart3),findViewById(R.id.main_IMG_Heart2),findViewById(R.id.main_IMG_Heart1)};
        main_BTN_Arrows = new ImageButton[] {findViewById(R.id.main_BTN_Left),findViewById(R.id.main_BTN_Right)};
        main_IMG_arrows = findViewById(R.id.main_IMG_arrows);
        main_IMG_arrows.setVisibility(View.INVISIBLE);
        main_IMG_Background = findViewById(R.id.main_IMG_background);

        lblScore = findViewById(R.id.main_LBL_score);


        main_STR_Types_In_Matrix = new String[] {"money", "stone","stone1","stone2","stone3"};

        main_IMG_Matrix = new ImageView[][]{
                {findViewById(R.id.main_IMG_Stone_0_0), findViewById(R.id.main_IMG_Stone_0_1), findViewById(R.id.main_IMG_Stone_0_2), findViewById(R.id.main_IMG_Stone_0_3), findViewById(R.id.main_IMG_Stone_0_4)},
                {findViewById(R.id.main_IMG_Stone_1_0), findViewById(R.id.main_IMG_Stone_1_1), findViewById(R.id.main_IMG_Stone_1_2), findViewById(R.id.main_IMG_Stone_1_3), findViewById(R.id.main_IMG_Stone_1_4)},
                {findViewById(R.id.main_IMG_Stone_2_0), findViewById(R.id.main_IMG_Stone_2_1), findViewById(R.id.main_IMG_Stone_2_2), findViewById(R.id.main_IMG_Stone_2_3), findViewById(R.id.main_IMG_Stone_2_4)},
                {findViewById(R.id.main_IMG_Stone_3_0), findViewById(R.id.main_IMG_Stone_3_1), findViewById(R.id.main_IMG_Stone_3_2), findViewById(R.id.main_IMG_Stone_3_3), findViewById(R.id.main_IMG_Stone_3_4)},
                {findViewById(R.id.main_IMG_Stone_4_0), findViewById(R.id.main_IMG_Stone_4_1), findViewById(R.id.main_IMG_Stone_4_2), findViewById(R.id.main_IMG_Stone_4_3), findViewById(R.id.main_IMG_Stone_4_4)},
                {findViewById(R.id.main_IMG_Stone_5_0), findViewById(R.id.main_IMG_Stone_5_1), findViewById(R.id.main_IMG_Stone_5_2), findViewById(R.id.main_IMG_Stone_5_3), findViewById(R.id.main_IMG_Stone_5_4)},
                {findViewById(R.id.main_IMG_Stone_6_0), findViewById(R.id.main_IMG_Stone_6_1), findViewById(R.id.main_IMG_Stone_6_2), findViewById(R.id.main_IMG_Stone_6_3), findViewById(R.id.main_IMG_Stone_6_4)}};

    }

    private void setMatrixs() {
        main_Num_Pic_In_Matrix = new int[][] {{-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1}};
    }

    private int getIndexCar(){
        return currectIndexCar;
    }
}
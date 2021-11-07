package com.example.racinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView[] main_IMG_Heart;
    private int numHeart =3;

    private ImageButton[] main_BTN_Arrows;
    private final int BTNLEFT =0;
    private final int BTNRIGHT =1;

    private final int ROCK_IN_ROW=3;
    private final int ROCK_IN_COL=5;
    private ImageView[][] main_IMG_Stones;



    private ImageView[] main_IMG_RacingCar;
    private ImageView main_IMG_GameOver;

    private ImageView main_IMG_Background;

    private static final int DELAY=1000;
    private static final int ROCK_DELAY=5000;

    private float locRacingCar;
    private  LinearLayout root_RacingCar;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setViews();
        startGame();


        main_BTN_Arrows[BTNLEFT].setOnClickListener(V->{
            moveCar(BTNLEFT);
        });
        main_BTN_Arrows[BTNRIGHT].setOnClickListener(V->{
            moveCar(BTNRIGHT);
        });


    }

    private void moveCar(int direction) {

        if (main_IMG_RacingCar[0].getVisibility() == View.VISIBLE) { //Car Left is Visible
            if (direction == BTNRIGHT) {//Car Move Right
                main_IMG_RacingCar[0].setVisibility(View.INVISIBLE);
                main_IMG_RacingCar[1].setVisibility(View.VISIBLE);
            }
        } else if (main_IMG_RacingCar[1].getVisibility() == View.VISIBLE) {//Car Middle is Visible
            if (direction == BTNLEFT) {//Car Move Left
                main_IMG_RacingCar[1].setVisibility(View.INVISIBLE);
                main_IMG_RacingCar[0].setVisibility(View.VISIBLE);
            } else {//Car Move Right
                main_IMG_RacingCar[1].setVisibility(View.INVISIBLE);
                main_IMG_RacingCar[2].setVisibility(View.VISIBLE);
            }
        } else {//Car Right is Visible
            if (direction == BTNLEFT) {//Car Move Left
                main_IMG_RacingCar[2].setVisibility(View.INVISIBLE);
                main_IMG_RacingCar[1].setVisibility(View.VISIBLE);
            }
        }
    }

    private void startGame() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() ->{
                    downRocks();
                    playGame();
                });
            }
        },0,DELAY);
    }

    private void playGame() {
        Random r = new Random();
        int colIndex = r.nextInt(3);
        main_IMG_Stones[0][colIndex].setVisibility(View.VISIBLE);
    }

    private void downRocks(){
        for (int i= ROCK_IN_COL-1 ; i>=0;i--){
            for (int j=ROCK_IN_ROW-1; j>=0;j--) {
                if (main_IMG_Stones[i][j].getVisibility() == View.VISIBLE) {
                    if ( i==ROCK_IN_COL-1) {
                        checkCar(j);
                        main_IMG_Stones[i][j].setVisibility(View.INVISIBLE);
                    }else{
                        main_IMG_Stones[i][j].setVisibility(View.INVISIBLE);
                        main_IMG_Stones[i+1][j].setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void checkCar(int j) {
        if (main_IMG_RacingCar[j].getVisibility() == View.VISIBLE) {
            removeHealth();
            vibrate();
        }
    }

    private void removeHealth() {
        makeSound();
        main_IMG_Heart[numHeart-1].setVisibility(View.INVISIBLE);
        numHeart--;
        if (numHeart == 0) {
            reGame();
        }
        toest("you have " + numHeart +" Lives");
    }

    private void makeSound() {
        MediaPlayer music = MediaPlayer.create(MainActivity.this, R.raw.videogamebombalert);
        music.start();
    }

    private void reGame() {
        numHeart=3;
        setHeartsOnView();
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
    }

    private void setRacingCarsView() {
        main_IMG_RacingCar = new ImageView[] {findViewById(R.id.main_IMG_RacingCarLeft),findViewById(R.id.main_IMG_RacingCarMiddle),findViewById(R.id.main_IMG_RacingCarRight)};
        main_IMG_RacingCar[0].setVisibility(View.INVISIBLE);
        main_IMG_RacingCar[2].setVisibility(View.INVISIBLE);
    }

    private void setStonesOnView() {
        for (int i=0; i<ROCK_IN_COL; i++)
            for (int j=0; j<ROCK_IN_ROW; j++)
                main_IMG_Stones[i][j].setVisibility(View.INVISIBLE);
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
        main_IMG_Heart = new ImageView[] {findViewById(R.id.main_IMG_Heart3),findViewById(R.id.main_IMG_Heart2),findViewById(R.id.main_IMG_Heart1)};
        main_BTN_Arrows = new ImageButton[] {findViewById(R.id.main_BTN_Left),findViewById(R.id.main_BTN_Right)};
        main_IMG_GameOver = findViewById(R.id.main_IMG_GameOver);
        main_IMG_GameOver.setVisibility(View.INVISIBLE);
        main_IMG_Background = findViewById(R.id.main_IMG_background);

        main_IMG_Stones = new ImageView[][]{{findViewById(R.id.main_IMG_Stone_Left1), findViewById(R.id.main_IMG_Stone_Middle1), findViewById(R.id.main_IMG_Stone_Right1)},
                {findViewById(R.id.main_IMG_Stone_Left2), findViewById(R.id.main_IMG_Stone_Middle2), findViewById(R.id.main_IMG_Stone_Right2)},
                {findViewById(R.id.main_IMG_Stone_Left3), findViewById(R.id.main_IMG_Stone_Middle3), findViewById(R.id.main_IMG_Stone_Right3)},
                {findViewById(R.id.main_IMG_Stone_Left4), findViewById(R.id.main_IMG_Stone_Middle4), findViewById(R.id.main_IMG_Stone_Right4)},
                {findViewById(R.id.main_IMG_Stone_Left5), findViewById(R.id.main_IMG_Stone_Middle5), findViewById(R.id.main_IMG_Stone_Right5)}};

    }
}
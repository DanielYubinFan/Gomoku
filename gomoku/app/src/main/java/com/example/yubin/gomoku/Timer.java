package com.example.yubin.gomoku;


import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;
import android.view.View;

/**
 * Created by jialuwang on 2/7/17.
 */
public class Timer extends Activity{
    public static final int ONEMINUTE = 60000;
    public static final int TENMINUTE = 600000;

    private Handler timerHandler = new Handler();
    private TextView timerText;
    private boolean one_minute;
    private boolean timesup;
    private long leftTime;
    private int minutes;
    private int seconds;
    public long millis;
    public Runnable runnable;
    public int start(){
        // return 1 if one minute timer runs off
        if(one_minute){
            leftTime = ONEMINUTE;
        }
        runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                millis = leftTime - elapsedTime;
                if (millis <= 0){
                    if(one_minute == true){
                        timesup = true;
                        return;
                    } else {
                        one_minute = true;
                        leftTime = ONEMINUTE;
                        startTime = System.currentTimeMillis();
                    }
                }
                seconds = (int) (millis / 1000);
                minutes = seconds / 60;
                seconds = seconds % 60;
                //timerText = (TextView) findViewById(R.id.timerText);
                timerText.setText(String.format("%d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 300);
            }
        };
        runnable.run();
        if(timesup){

            return 1;

        }
        return 0;
    }
    public void pause(){
        timerText.setText(String.format("%d:%02d", minutes, seconds));
        leftTime = millis;
        timerHandler.removeCallbacks(runnable);
    }
    Timer(TextView tv){
        timerText = tv;
        one_minute = false;
        timesup = false;
        leftTime = TENMINUTE;
        millis = TENMINUTE;
        minutes = 10;
        seconds = 0;
    }
};

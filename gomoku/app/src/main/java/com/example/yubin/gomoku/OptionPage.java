package com.example.yubin.gomoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

/**
 * Created by jialuwang on 2/20/17.
 */

public class OptionPage extends AppCompatActivity {
    public int playWith;    //0 for AI, 1 for offline, 2 for online
    public boolean standardMode;   // true for standardStyle, false for freeStyle
    public int boardSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);



        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i;
                if(playWith == 0){
                    //i = new Intent(OptionPage.this, AIActivity.class);
                    i = new Intent(OptionPage.this, GameActivity.class);
                } else {
                    i = new Intent(OptionPage.this, GameActivity.class);
                }
                i.putExtra("playWith", playWith);
                Log.i("in OptionPage,std:", Boolean.toString(standardMode));

                i.putExtra("playStyle", standardMode);
                i.putExtra("boardSize", boardSize);
                i.putExtra("playWith", playWith);
                Log.i("in buttonNext", Integer.toString(boardSize));

                startActivity(i);
            }
        });
        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(OptionPage.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
    public void onPlayWithClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonRobot:
                if (checked) {
                    playWith = 0;
                }
                break;

            case R.id.radioButtonOffline:
                if (checked) {
                    playWith = 1;
                }
                break;
            case R.id.radioButtonOnline:
                if (checked){
                    playWith = 2;
                }
                break;
        }
    }
    public void onPlayInClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonStandard:
                if (checked){
                    standardMode = true;
                }
                    break;
            case R.id.radioButtonFree:
                if (checked){
                    standardMode = false;
                }
                    break;
        }
    }
    public void onBoardSizeClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton10x10:
                if (checked){
                    boardSize = 11;
                    Log.i("in radioButton switch", Integer.toString(boardSize));
                }
                    break;
            case R.id.radioButton15x15:
                if (checked){
                    boardSize = 16;
                }
                    break;
            case R.id.radioButton20x20:
                if (checked){
                    boardSize = 21;
                }
                    break;
        }
    }
}

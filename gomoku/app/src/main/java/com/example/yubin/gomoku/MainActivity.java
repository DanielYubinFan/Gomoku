package com.example.yubin.gomoku;
import com.example.yubin.gomoku.Timer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);


        Button buttonEnterGame = (Button) findViewById(R.id.buttonEnterGame);
        buttonEnterGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OptionPage.class);
                startActivity(i);
            }
        });

        // Modify here to direct to Instruction Page
        Button buttonHowToPlay = (Button) findViewById(R.id.buttonHowToPlay);
        buttonHowToPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InstructionPage.class);
                startActivity(i);
            }
        });

        // Modify here to direct to Record Page
        /*Button buttonPlayRecord = (Button) findViewById(R.id.buttonPlayRecord);
        buttonPlayRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("record on click", " ");
                Intent i = new Intent(MainActivity.this, RecordPage.class);
                startActivity(i);
            }
        });*/
    }
}


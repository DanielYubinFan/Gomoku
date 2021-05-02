package com.example.yubin.gomoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by jialuwang on 2/20/17.
 */

public class StartPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        Button buttonEnterGame = (Button) findViewById(R.id.buttonEnterGame);
        buttonEnterGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(StartPage.this, OptionPage.class);
                startActivity(i);
            }
        });
    }
}

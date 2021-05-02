package com.example.yubin.gomoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by jialuwang on 2/23/17.
 */

public class InstructionPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction);

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(InstructionPage.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
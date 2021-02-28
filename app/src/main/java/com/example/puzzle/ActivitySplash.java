package com.example.puzzle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ActivitySplash extends AppCompatActivity {
    private MaterialButton splash_BTN_start;
    private MediaPlayer mp_open_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mp_open_game = MediaPlayer.create(this, R.raw.putin);
        mp_open_game.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash_BTN_start = findViewById(R.id.splash_BTN_start);
        splash_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                mp_open_game.stop();
                finish();
            }
        });
    }
}
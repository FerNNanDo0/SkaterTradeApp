package com.droid.app.skaterTrader.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.droid.app.skaterTrader.R;
public class LogoInicialActivity extends AppCompatActivity {
    private final int TIME = 3500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_inicial);

        assert getSupportActionBar() != null;
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {startActivity( new Intent(getApplicationContext(), ActivityMain.class));

            }
        },TIME);
    }
}
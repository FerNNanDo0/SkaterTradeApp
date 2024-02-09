package com.droid.app.skaterTrader.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.droid.app.skaterTrader.R;

public class ConfigDadosLojaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_dados_loja);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configurações de dados");

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
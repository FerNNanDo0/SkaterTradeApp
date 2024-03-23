package com.droid.app.skaterTrader.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.droid.app.skaterTrader.databinding.ActivityRedefinirSenhaBinding;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.service.InitSdkAdmob;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
public class RedefinirSenhaActivity extends AppCompatActivity {
    EditText editRedefinir;
    TemplateView template;
    NativeAd Ad;
    View view;
    ActivityRedefinirSenhaBinding binding;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.Ad = InitSdkAdmob.getAd();
        if(Ad != null){
            Ad.destroy();
            template.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_redefinir_senha);
        binding = ActivityRedefinirSenhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // config ToolBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Redefinir senha");

        editRedefinir = binding.editRedefinir;//findViewById(R.id.editRedefinir);

        // init SDK Ads
        MobileAds.initialize(this, initializationStatus -> {});

        // init SDK Ads
        template = binding.myTemplate;//findViewById(R.id.my_template);
        InitSdkAdmob.initSdkAdmob(this, template);
    }

    public void btnRedefinir(View view){
        this.view = view;
        String email = editRedefinir.getText().toString().trim();
        if( !email.isEmpty() ){
            redefinirSenha(email);
        }else{
            Snackbar.make(view, "Digite seu E-mail", Snackbar.LENGTH_LONG).show();
        }
    }
    public void redefinirSenha(String emailAddress){

        FirebaseAuth auth = FirebaseRef.getAuth();
        auth.sendPasswordResetEmail( emailAddress )
                .addOnCompleteListener(task -> {
                    if (task.isCanceled()) {
                        Log.e("IsCanceled","SendPasswordResetEmailAsync was canceled.");
                        return;
                    }
                    if (task.isSuccessful()) {
                        Log.i("Sucesso","Password reset email sent successfully.");

                        String message = "Foi enviado um link para redefinição de senha no email "+emailAddress;
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

                        startActivity(new Intent(getApplicationContext(), AcessoActivity.class));
                        finish();
                    }else{
                        String erroMsg = task.getException().getMessage();
                        Snackbar.make(view, erroMsg, Snackbar.LENGTH_LONG).show();
                    }

                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, AcessoActivity.class));
        finish();
        return super.onSupportNavigateUp();
    }
}
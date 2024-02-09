package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.R;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
public class RedefinirSenhaActivity extends AppCompatActivity {
    EditText editRedefinir;
    TemplateView template;
    NativeAd Ad;
    View view;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Ad != null){
            Ad.destroy();
            template.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinir_senha);

        // config ToolBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Redefinir senha");

        editRedefinir = findViewById(R.id.editRedefinir);

        // init SDK Ads
        MobileAds.initialize(this, initializationStatus -> {});

        initSdkAdmob();
    }
    //admob
    private void initSdkAdmob(){
        // ca-app-pub-4810475836852520/5974368133 -> id anuncio nativo
        // ca-app-pub-3940256099942544/2247696110 -> teste
        final AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-4810475836852520/5974368133")
                .forNativeAd( nativeAd -> {
                    Ad = nativeAd;

                    // Show the ad.
                    template = findViewById(R.id.my_template);
                    template.setVisibility(View.VISIBLE);
                    template.setNativeAd(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        template.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded(){
                        // metodo chamado quando o anúncio é carregado
                    }

                    @Override
                    public void onAdOpened(){
                        // metodo chamado quando o anúncio éaberto
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // usar ess metodo para alterar a interface ou apenas registrar a falha
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        // Este método envia uma solicitação para um único anúncio.
        adLoader.loadAd(new AdRequest.Builder().build());

        // Este método envia uma solicitação para vários anúncios (até cinco):
//        adLoader.loadAds(new AdRequest.Builder().build(), 3);
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

                        String message = "Um email com redefinição de senha foi enviado para seu email";
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

                        startActivity(new Intent(getApplicationContext(), AcessoActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AcessoActivity.class));
    }
}
package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.helper.ConnectWhatsApp;
import com.droid.app.skaterTrader.model.Anuncio;
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
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
public class DetalhesProdutosActivity extends AppCompatActivity
        implements View.OnClickListener {

    String numero = "";//"5551984601729/?text=";
    String str = "Olá encontrei seu anúncio no App SkaterTrade e gostaria de mais informações.\nTítulo do anúncio: %s";
    ConnectWhatsApp connectWhatsApp;
    CarouselView carouselView;
    TextView titulo, valor, estado, descricao;
    Button btnConversa;
    Anuncio anuncioSelected;
    TemplateView template;
    NativeAd Ad;

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
        setContentView(R.layout.activity_detalhes_anuncios);

        // config ToolBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhes do anúncio");

        // iniciar componentes
        initComponentes();

        // Recuperar anúncio para exibição
        anuncioSelected = (Anuncio) getIntent().getParcelableExtra("anuncioSelected");
        if(anuncioSelected != null){
            titulo.setText( anuncioSelected.getTitulo() );
            valor.setText( anuncioSelected.getValor() );
            estado.setText( anuncioSelected.getEstado() );
            descricao.setText( anuncioSelected.getDesc() );

            ImageListener imageListener = (position, imageView) -> {
                imageView.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
                String urlString = anuncioSelected.getFotos().get(position);
                //Picasso.get().load(urlString).into(imageView);
                Glide.with(this).load(urlString).into(imageView);

            };
            carouselView.setPageCount( anuncioSelected.getFotos().size() );
            carouselView.setImageListener( imageListener );
        }

        // init SDK Ads
        MobileAds.initialize(this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {}
                });

        initSdkAdmob();
    }
    //admob
    private void initSdkAdmob(){
        // ca-app-pub-4810475836852520/5974368133 -> id anuncio nativo
        // ca-app-pub-3940256099942544/2247696110 -> teste
        final AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-4810475836852520/5974368133")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        Ad = nativeAd;

                        // Show the ad.
                        template = findViewById(R.id.myTemplate);
                        template.setVisibility(View.VISIBLE);
                        template.setNativeAd(nativeAd);
                    }
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
    private void initComponentes() {
        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textViewTituloD);
        valor = findViewById(R.id.textViewValorD);
        estado = findViewById(R.id.textViewEstadoD);
        descricao = findViewById(R.id.textViewDesc);
        btnConversa = findViewById(R.id.btnWhats);
        btnConversa.setOnClickListener(this);
        connectWhatsApp = new ConnectWhatsApp(this);
    }
    // click do buttonWhats
    @Override
    public void onClick(View v) {
        if(anuncioSelected != null){
            String DDD = anuncioSelected.getDDD();
            String celular = anuncioSelected.getPhone();
            celular = celular.replace("(","");
            celular = celular.replace(")","");
            celular = celular.replace("-","");

            String titulo = anuncioSelected.getTitulo().toUpperCase();
            String msg = String.format(str, titulo);
            // msg = msg+anuncioSelected.getTitulo().toUpperCase();

            if(DDD.isEmpty()){
                numero = String.format("55%s",celular.trim());
            }else{
                numero = String.format(DDD+"%s",celular.trim());
            }

            connectWhatsApp.initMsg(numero,msg);
        }
    }
}
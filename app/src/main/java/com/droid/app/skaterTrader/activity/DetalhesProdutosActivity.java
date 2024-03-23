package com.droid.app.skaterTrader.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.databinding.ActivityDetalhesAnunciosBinding;
import com.droid.app.skaterTrader.helper.ConnectWhatsApp;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.service.InitSdkAdmob;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesProdutosActivity extends AppCompatActivity
        implements View.OnClickListener {

    String numero = "";
    String str = "Olá encontrei seu anúncio no App SkaterTrade e gostaria de mais informações.\nTítulo do anúncio: %s";
    ConnectWhatsApp connectWhatsApp;
    CarouselView carouselView;
    TextView titulo, valor, estado, descricao;
    Button btnConversa;
    Anuncio anuncioSelected;
    TemplateView template;
    NativeAd Ad;
    ActivityDetalhesAnunciosBinding binding;

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
        //setContentView(R.layout.activity_detalhes_produtos);
        binding = ActivityDetalhesAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // config ToolBar
        configActionBar();

        // iniciar componentes
        initComponentes();

        // Recuperar anúncio para exibição
        recuperarAnucio();

        // init SDK Ads
        MobileAds.initialize(this,
                initializationStatus -> {}
        );

        template = binding.myTemplate;//findViewById(R.id.my_template);
        InitSdkAdmob.initSdkAdmob(this, template);
    }
    private void initComponentes() {
        carouselView = binding.carouselView;//findViewById(R.id.carouselView);
        titulo = binding.textViewTituloD;//findViewById(R.id.textViewTituloD);
        valor = binding.textViewValorD;//findViewById(R.id.textViewValorD);
        estado = binding.textViewEstadoD;//findViewById(R.id.textViewEstadoD);
        descricao = binding.textViewDesc;//findViewById(R.id.textViewDesc);
        btnConversa = binding.btnWhats;//findViewById(R.id.btnWhats);
        btnConversa.setOnClickListener(this);
        connectWhatsApp = new ConnectWhatsApp(this);
    }
    // click do buttonWhats
    @Override
    public void onClick(View v) {
        if(anuncioSelected != null){
            String celular = anuncioSelected.getPhone();
            celular = celular.replace("(","");
            celular = celular.replace(")","");
            celular = celular.replace("-","");

            String titulo = anuncioSelected.getTitulo().toUpperCase();
            String msg = String.format(str, titulo);

           // msg = msg+anuncioSelected.getTitulo().toUpperCase();
            numero = String.format("55%s",celular.trim());
            connectWhatsApp.initMsg(numero,msg);
        }
    }

    private void configActionBar(){
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhes do anúncio");
    }

    private void recuperarAnucio(){
        anuncioSelected = getIntent().getParcelableExtra("anuncioSelected");
        if(anuncioSelected != null){
            titulo.setText( anuncioSelected.getTitulo() );
            valor.setText( anuncioSelected.getValor() );

            if(anuncioSelected.getCidade() != null){
                estado.setText(
                    String.format(anuncioSelected.getEstado()+"    "+anuncioSelected.getCidade())
                );
            }else{
                estado.setText( anuncioSelected.getEstado() );
            }

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
    }

}
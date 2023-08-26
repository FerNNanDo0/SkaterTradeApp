package com.droid.app.olx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.droid.app.olx.R;
import com.droid.app.olx.helper.ConnectWhatsApp;
import com.droid.app.olx.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesProdutosActivity extends AppCompatActivity implements View.OnClickListener {
    String numero = "";//"5551984601729/?text=";
    String msg = "Olá encontrei seu anúncio no App SkaterTrade e gostaria de mais informações.\nTítulo do anúncio: ";
    ConnectWhatsApp connectWhatsApp;
    CarouselView carouselView;
    TextView titulo, valor, estado, descricao;
    Button btnConversa;
    Anuncio anuncioSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produtos);

        // config ToolBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                Picasso.get().load(urlString).into(imageView);

            };
            carouselView.setPageCount( anuncioSelected.getFotos().size() );
            carouselView.setImageListener( imageListener );
        }
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
            String celular = anuncioSelected.getPhone();
            celular = celular.replace("(","");
            celular = celular.replace(")","");
            celular = celular.replace("-","");

            msg = msg+anuncioSelected.getTitulo().toUpperCase();
            numero = String.format("55%s",celular.trim());
            connectWhatsApp.initMsg(numero,msg);
        }
    }
}
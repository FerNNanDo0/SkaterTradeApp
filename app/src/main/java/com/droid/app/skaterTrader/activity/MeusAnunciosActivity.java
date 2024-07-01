package com.droid.app.skaterTrader.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.droid.app.skaterTrader.databinding.ActivityAnunciosBinding;
import com.droid.app.skaterTrader.helper.ClickRecyclerView;
import com.droid.app.skaterTrader.helper.Informe;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.adapter.AdapterAnuncios;
import com.droid.app.skaterTrader.viewModel.ViewModelAnuncios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dmax.dialog.SpotsDialog;
@SuppressLint("NotifyDataSetChanged")
public class MeusAnunciosActivity extends AppCompatActivity {
    FloatingActionButton floatBtn;
    private RecyclerView recyclerViewAnuncios;
    private AdapterAnuncios adapterAnuncios;
    private AlertDialog alertDialog;

//    Anuncio anuncioSelected;
    ClickRecyclerView clickRecyclerView;
    TextView textViewInforme;
    ConstraintLayout layoutRoot;
    ViewModelAnuncios viewModel;
    ActivityAnunciosBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_anuncios);
        binding = ActivityAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(getString(R.string.meus_an_ncios));

        iniciarComponentes();
        clickBtnFloat();

        // busca anuncios do usuario
        recuperarAnunciosUser();
    }

    // iniciar componentes
    public void iniciarComponentes() {
        recyclerViewAnuncios = binding.recyclerMeusAnuncio;//findViewById(R.id.recyclerMeusAnuncio);
        textViewInforme = binding.textViewInforme;//findViewById(R.id.textViewInforme);
        layoutRoot = binding.layoutRoot;//findViewById(R.id.layoutRoot);

        // configurar recyclerView
        recyclerViewAnuncios.setLayoutManager( new LinearLayoutManager(this));
        recyclerViewAnuncios.setHasFixedSize(true);

    }

    private void recuperarAnunciosUser() {
        // --------> ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModelAnuncios.class);

        observerViewModel();

        viewModel.recuperarAnunciosUser(this);
    }

    private void observerViewModel() {

        // observe msg carregando anuncios
        viewModel.getShowMsg().observe( this, this::alertDialogCustom );

        // observe recuperar anuncios
        viewModel.getList().observe(this,
            anuncios -> {

                if(anuncios != null){

                    if(anuncios.size() == 0){
                        recyclerViewAnuncios.setVisibility(View.GONE);
                        layoutRoot.setBackgroundColor(getColor(R.color.white));
                        textViewInforme.setVisibility(View.VISIBLE);
                    }else{

                        adapterAnuncios = new AdapterAnuncios(anuncios,this);
                        recyclerViewAnuncios.setAdapter( adapterAnuncios );
                        adapterAnuncios.notifyDataSetChanged();

                        clickRecyclerView = new ClickRecyclerView(
                                recyclerViewAnuncios,anuncios,
                                adapterAnuncios,this
                        );
                        clickRecyclerView.clickRecyclerView();

                    }
                }
                alertDialog.dismiss();

                informeDicaUser();
        });

        // observe msg erro ao carregar anuncios
        viewModel.getErroMsg().observe(this,
            msgErro -> {
            alertDialog.dismiss();
            alertInformation(getString(R.string.erro2), msgErro, false);
        });

    }

    private void informeDicaUser(){
        if(Informe.recuperarCodePermission(getApplicationContext()).equals("0")){
            alertInformation(getString(R.string.dica),
                    getString(R.string.para_editar_ou_excluir_o_an_ncio_basta_clicar_nele),
                    true);
        }
    }

    private void clickBtnFloat(){
        floatBtn = binding.floatingActionButton;//findViewById(R.id.floatingActionButton);
        floatBtn.setOnClickListener(v -> startActivity( new Intent(getApplicationContext(), CadastrarOuEditarAnunciosActivity.class)));
    }
    public void alertDialogCustom(String txt) {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(txt)
                .setCancelable(true)
                .build();
        alertDialog.show();
    }
    private void alertInformation(String titulo, String msg, boolean aBoolean){
        AlertDialog.Builder builder = new AlertDialog.Builder(MeusAnunciosActivity.this);
        builder.setTitle(titulo);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            if(aBoolean){
                Informe.salvarCodePermission("1", getApplicationContext());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
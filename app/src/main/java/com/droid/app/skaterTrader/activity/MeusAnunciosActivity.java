package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
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

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.Informe;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.adapter.AdapterAnuncios;
import com.droid.app.skaterTrader.viewModel.ViewModelAnuncio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
@SuppressLint("NotifyDataSetChanged")
public class MeusAnunciosActivity extends AppCompatActivity {
    FloatingActionButton floatBtn;
    private RecyclerView recyclerViewAnuncios;
    private final List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference databaseRef;
    DatabaseReference meusAnunciosRef;
    private User user;
    private AlertDialog alertDialog;
    ValueEventListener valueEventListener;
//    Anuncio anuncioSelected;
    ClickRecyclerView clickRecyclerView;
    TextView textViewInforme;
    ConstraintLayout layoutRoot;
    ViewModelAnuncio viewModel;

    @Override
    protected void onStop() {
        super.onStop();
        meusAnunciosRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarAnunciosUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Meus anúncios");

        iniciarComponentes();
        clickBtnFloat();

    }

    // iniciar componentes
    public void iniciarComponentes() {
        user = new User();
        databaseRef = FirebaseRef.getDatabase();
        recyclerViewAnuncios = findViewById(R.id.recyclerMeusAnuncio);
        textViewInforme = findViewById(R.id.textViewInforme);
        layoutRoot = findViewById(R.id.layoutRoot);

        // configurar recyclerView
        recyclerViewAnuncios.setLayoutManager( new LinearLayoutManager(this));
        recyclerViewAnuncios.setHasFixedSize(true);

        observerViewModel();

        /*adapterAnuncios = new AdapterAnuncios(anuncioList,this);
        recyclerViewAnuncios.setAdapter( adapterAnuncios );

        //clickRecyclerView();
        clickRecyclerView = new ClickRecyclerView(recyclerViewAnuncios,anuncioList,adapterAnuncios,this);
        clickRecyclerView.clickRecyclerView();*/
    }
    private void recuperarAnunciosLoja() {
        if(anuncioList.size() == 0){
            alertDialogCustom("Buscando Anúncios disponíveis");
        }

        meusAnunciosRef = databaseRef.child("lojas").child(user.getIdUser());
        valueEventListener = meusAnunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    System.out.println("YES");
                }else{
                    System.out.println("NÂo");
                }

                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot ds : snapshot.getChildren() ){
                    anuncioList.add( ds.getValue(Anuncio.class) );
                }
//                Collections.reverse( anuncioList );
//                adapterAnuncios.notifyDataSetChanged();
//                alertDialog.dismiss();

                // set ViewModel
                viewModel.setListModel(anuncioList);

                if(anuncioList.size() == 0){
                    recyclerViewAnuncios.setVisibility(View.GONE);
                    layoutRoot.setBackgroundColor(getColor(R.color.white));
                    textViewInforme.setVisibility(View.VISIBLE);
                }else{
                    if(Informe.recuperarCode(getApplicationContext()).equals("0")){
                        alertInformation("Dica para excluir",
                                "Para excluir anúncios basta pressionar o anúncio que deseja excluir e comfirmar.",
                                true);
                    }
                }

                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();
                alertInformation("Erro!", "Erro ao carregar anúncios.", false);
            }
        });
    }

    private void recuperarAnunciosUser() {
        if(anuncioList.size() == 0){
            alertDialogCustom("Buscando Anúncios disponíveis");
        }

        meusAnunciosRef = databaseRef.child("meus_anuncios").child(user.getIdUser());
        valueEventListener = meusAnunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    System.out.println("YES");
                }else{
                    System.out.println("NÂo");
                }

                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot ds : snapshot.getChildren() ){
                    anuncioList.add( ds.getValue(Anuncio.class) );
                }
//                Collections.reverse( anuncioList );
//                adapterAnuncios.notifyDataSetChanged();
//                alertDialog.dismiss();

                // set ViewModel
                viewModel.setListModel(anuncioList);

                if(anuncioList.size() == 0){
                    recyclerViewAnuncios.setVisibility(View.GONE);
                    layoutRoot.setBackgroundColor(getColor(R.color.white));
                    textViewInforme.setVisibility(View.VISIBLE);
                }else{
                    if(Informe.recuperarCode(getApplicationContext()).equals("0")){
                        alertInformation("Dica para excluir",
                                "Para excluir anúncios basta pressionar o anúncio que deseja excluir e comfirmar.",
                                true);
                    }
                }

                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();
                alertInformation("Erro!", "Erro ao carregar anúncios.", false);
            }
        });
    }

    private void observerViewModel() {
        // -------- ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModelAnuncio.class);
        // model recuperar anuncios
        viewModel.getList().observe(MeusAnunciosActivity.this, anuncios -> {

            if(anuncios != null){
                Collections.reverse( anuncios );

                adapterAnuncios = new AdapterAnuncios(anuncios,this);
                recyclerViewAnuncios.setAdapter( adapterAnuncios );
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();

                clickRecyclerView = new ClickRecyclerView(recyclerViewAnuncios,anuncios,adapterAnuncios,this);
                clickRecyclerView.clickRecyclerView();
            }
        });
    }
    private void clickBtnFloat(){
        floatBtn = findViewById(R.id.floatingActionButton);
        floatBtn.setOnClickListener(v -> startActivity( new Intent(getApplicationContext(), CadastrarAnunciosActivity.class)));
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
        builder.setPositiveButton("Ok", (dialog, which) -> {
            if(aBoolean){
                Informe.salvarCode("1", getApplicationContext());
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
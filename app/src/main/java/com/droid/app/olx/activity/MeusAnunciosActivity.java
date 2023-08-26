package com.droid.app.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.droid.app.olx.R;
import com.droid.app.olx.adapter.AdapterAnuncios;
import com.droid.app.olx.firebaseRefs.FirebaseRef;
import com.droid.app.olx.helper.RecyclerItemClickListener;
import com.droid.app.olx.model.Anuncio;
import com.droid.app.olx.model.User;
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
    private List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference databaseRef;
    DatabaseReference meusAnunciosRef;
    private User user;
    private AlertDialog alertDialog;
    ValueEventListener valueEventListener;
//    Anuncio anuncioSelected;
    ClickRecyclerView clickRecyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        recuperarAnuncios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        meusAnunciosRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        iniciarComponentes();
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        clickBtnFloat();

        // configurar recyclerView
        recyclerViewAnuncios.setLayoutManager( new LinearLayoutManager(this));
        recyclerViewAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList,this);
        recyclerViewAnuncios.setAdapter( adapterAnuncios );

        //clickRecyclerView();
        clickRecyclerView = new ClickRecyclerView(recyclerViewAnuncios,anuncioList,adapterAnuncios,this);
        clickRecyclerView.clickRecyclerView();
    }

    // iniciar componentes
    public void iniciarComponentes(){
        user = new User();
        databaseRef = FirebaseRef.getDatabase();
        recyclerViewAnuncios = findViewById(R.id.recyclerMeusAnuncio);
    }
    private void recuperarAnuncios() {
        if(anuncioList.size() == 0){
            alertDialogCustom("Buscando Anúncios disponíveis");
        }

        meusAnunciosRef = databaseRef.child("meus_anuncios").child(user.getIdUser());
        valueEventListener = meusAnunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot ds : snapshot.getChildren() ){
                    anuncioList.add( ds.getValue(Anuncio.class) );
                }
                Collections.reverse( anuncioList );
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(MeusAnunciosActivity.this);
                builder.setTitle("Erro!");
                builder.setMessage("Erro ao carregar anúncios");
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", (dialog, which) -> {

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    private void clickBtnFloat(){
        floatBtn = findViewById(R.id.floatingActionButton);
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getApplicationContext(), CadastrarAnunciosActivity.class));
            }
        });
    }
    public void alertDialogCustom(String txt) {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(txt)
                .setCancelable(true)
                .build();
        alertDialog.show();
    }
/*
    private void clickRecyclerView(){
        recyclerViewAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerViewAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                anuncioSelected = anuncioList.get(position);
                                System.out.println("id: "+anuncioSelected.getIdAnuncio());

                                AlertDialog.Builder builder = new AlertDialog.Builder(MeusAnunciosActivity.this);
                                builder.setTitle("Excluir anúncio");
                                builder.setMessage("Tem certeza que deseja excluir esse anúncio?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Sim", (dialog, which) -> {
                                    anuncioSelected.removerAnuncio();
                                    adapterAnuncios.notifyDataSetChanged();
                                });
                                builder.setNegativeButton("Não", (dialog, which) -> {

                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }
*/
}
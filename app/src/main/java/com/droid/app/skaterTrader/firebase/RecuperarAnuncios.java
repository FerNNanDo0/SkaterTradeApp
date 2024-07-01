package com.droid.app.skaterTrader.firebase;

import android.content.Context;
import androidx.annotation.NonNull;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.viewModel.ViewModelAnuncios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecuperarAnuncios {

    final List<Anuncio> anuncioList = new ArrayList<>();
    DatabaseReference databaseRef;
    DatabaseReference anunciosFiltroEstadoRef, anunciosFiltroCategoriaRef;
    User user;
    ValueEventListener valueEventListener1, valueEventListener2, valueEventListener3, valueEventListener4;
    DatabaseReference meusAnunciosRef, anunciosRef;

    public void removeEventListener(){
        if(anunciosRef != null){
            anunciosRef.removeEventListener(valueEventListener2);
        }

        if(meusAnunciosRef != null){
            meusAnunciosRef.removeEventListener(valueEventListener1);
        }

        if(anunciosFiltroEstadoRef != null){
            anunciosFiltroEstadoRef.removeEventListener(valueEventListener3);
        }

        if(anunciosFiltroCategoriaRef != null){
            anunciosFiltroCategoriaRef.removeEventListener(valueEventListener4);
        }
    }

    // constructor
    public RecuperarAnuncios() {
        user = new User();
        databaseRef = FirebaseRef.getDatabase();
    }

    // recuperar anuncios privado para o usuario cadastrado
    public void recuperarAnunciosUser(ViewModelAnuncios viewModel, Context context) {
        if(anuncioList.size() == 0){
           viewModel.setShowMsg(context.getString(R.string.buscando_an_ncios_dispon_veis));
        }

        meusAnunciosRef = databaseRef.child("meus_anuncios").child(user.getIdUser());
        valueEventListener1 = meusAnunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot ds : snapshot.getChildren() ){
                    anuncioList.add( ds.getValue(Anuncio.class) );
                }

                Collections.reverse( anuncioList );
                // set ViewModel
                viewModel.setListModel(anuncioList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                viewModel.setErroMsg(context.getString(R.string.erro_ao_carregar_an_ncios));
            }
        });
    }

    // recuperar anuncios publico para o usuario
    public void recuperarAnuncios(ViewModelAnuncios viewModel, Context context) {
        if(anuncioList.size() == 0){
            viewModel.setShowMsg(context.getString(R.string.buscando_an_ncios_dispon_veis));
        }
        anunciosRef = databaseRef.child("anuncios");
        valueEventListener2 = anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot estados : snapshot.getChildren() ){
                    for(DataSnapshot categorias : estados.getChildren()){
                        for(DataSnapshot anuncios : categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            anuncioList.add( anuncio );
                        }
                    }
                }

                Collections.reverse( anuncioList );
                // set ViewModel
                viewModel.setListModel(anuncioList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void filtarAnunciosEstado(ViewModelAnuncios viewModel, String estado, String categoriaSelected, Context context){
        if(anuncioList.size() == 0){
            viewModel.setShowMsg(context.getString(R.string.buscando_an_ncios_dispon_veis));
        }
        //estadosSelected = estado;
        anunciosFiltroEstadoRef = databaseRef.child("anuncios").child(estado);
        valueEventListener3 = anunciosFiltroEstadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anuncioList.clear();
                //alertDialogCustom("Buscando anúncios");
                for(DataSnapshot categorias : snapshot.getChildren()){
                    for(DataSnapshot anuncios : categorias.getChildren()){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        if(anuncio != null){
                            if( !categoriaSelected.isEmpty() &&
                                    anuncio.getCategoria().equals(categoriaSelected)
                            ){
                                anuncioList.add( anuncio );
                            }

                            if( categoriaSelected.isEmpty() ){
                                anuncioList.add( anuncio );
                            }
                        }
                    }
                }

                // set ViewModel 1
                viewModel.setListModel(anuncioList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void filtarAnunciosCategoria(ViewModelAnuncios viewModel, String categoria, String estadosSelected, Context context){
        if(anuncioList.size() == 0){
            viewModel.setShowMsg(context.getString(R.string.buscando_an_ncios_dispon_veis));
        }
        anunciosFiltroCategoriaRef = databaseRef.child("anuncios");
        valueEventListener4 = anunciosFiltroCategoriaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot estados : snapshot.getChildren() ){
                    for(DataSnapshot categorias : estados.getChildren()){
                        for(DataSnapshot anuncios : categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            if(anuncio != null){
                                if ( !estadosSelected.isEmpty() &&
                                        anuncio.getEstado().equals(estadosSelected) &&
                                        anuncio.getCategoria().equals(categoria)
                                ){
                                    anuncioList.add( anuncio );
                                    System.out.println("estado: "+estadosSelected);
                                }

                                if( estadosSelected.isEmpty() &&
                                        anuncio.getCategoria().equals( categoria )
                                ){
                                    anuncioList.add( anuncio );
                                }
                            }
                        }
                    }
                }

                // set ViewModel
                viewModel.setListModel(anuncioList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.droid.app.skaterTrader.firebase;


import android.app.Activity;

import androidx.annotation.NonNull;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.viewModel.ViewModelAnuncios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecuperarAnuncios extends Activity {

    final List<Anuncio> anuncioList = new ArrayList<>();
    DatabaseReference databaseRef;
    User user;
    ValueEventListener valueEventListener1, valueEventListener2;
    DatabaseReference meusAnunciosRef, anunciosRef;

    @Override
    protected void onStop() {
        super.onStop();
        meusAnunciosRef.removeEventListener(valueEventListener1);
        anunciosRef.removeEventListener(valueEventListener2);
    }

    public RecuperarAnuncios() {
        user = new User();
        databaseRef = FirebaseRef.getDatabase();
    }

    public void recuperarAnunciosUser(ViewModelAnuncios viewModel) {
        if(anuncioList.size() == 0){
           viewModel.setShowMsg("Buscando Anúncios disponíveis");
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
                viewModel.setErroMsg("Erro ao carregar anúncios.");
            }
        });
    }

    public void recuperarAnuncios(ViewModelAnuncios viewModel) {
        if(anuncioList.size() == 0){
            viewModel.setShowMsg("Buscando Anúncios disponíveis");
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

    public void filtarAnuncios(String estado){
        //estadosSelected = estado;
        anunciosFiltroRef = databaseRef.child("anuncios").child(estado);
        valueEventListener1 = anunciosFiltroRef.addValueEventListener(new ValueEventListener() {
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

                menuRedefinir.setVisible(true);

                // defnir nome btn
                String btnTxt =  String.format("REGIÃO-%s",filtro);
                btn_regiao.setText(btnTxt);

                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
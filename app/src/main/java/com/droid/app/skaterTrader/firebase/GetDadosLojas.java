package com.droid.app.skaterTrader.firebase;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.viewModel.ViewModelConfigDadosLoja;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GetDadosLojas {

    DatabaseReference lojaRef, database;
    ValueEventListener eventListener;
    Loja loja;


    public void removeEventListener(){
        lojaRef.removeEventListener(eventListener);
    }

    public void getDadosDb(ViewModelConfigDadosLoja viewModel) {
        loja = new Loja();

        //obter dados no Firebase
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(loja.getIdLoja());
        eventListener = lojaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loja = snapshot.getValue(Loja.class);

                    viewModel.setLoja(loja);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                viewModel.setError(error);
            }
        });
    }

}

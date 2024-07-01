package com.droid.app.skaterTrader.firebase;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.viewModel.ViewModelDadosConsulta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GetDadosConsultas {
    public void getDados(ViewModelDadosConsulta viewModel) {

        DatabaseReference database = FirebaseRef.getDatabase();
        DatabaseReference consultasRef = database.child("consultas");
        consultasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewModel.setDataSnapshot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

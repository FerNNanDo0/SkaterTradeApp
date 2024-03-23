package com.droid.app.skaterTrader.firebase;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.activity.MapaPicosSkate;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.MarkerPico;
import com.droid.app.skaterTrader.viewModel.ViewModelMapaPicos;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GetPicosDb extends MapaPicosSkate {

    static DatabaseReference database;
    static DatabaseReference picoRef;
    static ValueEventListener valueEventListener;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        picoRef.removeEventListener(valueEventListener);
    }


    public static void recuperarPontosDePico(ViewModelMapaPicos viewModel) {

        // ref do db
        database = FirebaseRef.getDatabase();

        picoRef = database.child("picos");
        valueEventListener = picoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                viewModel.setSnapShot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

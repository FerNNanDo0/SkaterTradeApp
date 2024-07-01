package com.droid.app.skaterTrader.firebase;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class RemoverAnuncio {


    public static void remover(@NonNull User user, @NonNull DatabaseReference database,
                               String id, String estado, String categoria, int totalFotos
    ){
        // remover meus anuncios
        DatabaseReference meuAnuncio = database.child("meus_anuncios")
                .child(user.getIdUser())
                .child(id);
        meuAnuncio.removeValue();

        // remover anuncio Publico
        DatabaseReference anuncioPublicoRef = database.child("anuncios")
                .child(estado)
                .child(categoria)
                .child(id);
        anuncioPublicoRef.removeValue();

        // remover imagens do anuncio
        StorageReference storage = FirebaseRef.getStorage();
        for (int i=0; i < totalFotos; i++){
            StorageReference imgAnuncio = storage.child("imagens")
                    .child("anuncios")
                    .child(id)
                    .child("imagem"+i);
            imgAnuncio.delete().addOnSuccessListener( unused -> {

            }).addOnFailureListener(e -> {

            });
            System.out.println("index "+i);
        }
    }
}
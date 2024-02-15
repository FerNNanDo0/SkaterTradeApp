package com.droid.app.skaterTrader.firebaseRefs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.activity.ActivityMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class FirebaseRef {
    @NonNull
    public static FirebaseAuth getAuth(){
        return FirebaseAuth.getInstance();
    }
    @NonNull
    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference();
    }
    @NonNull
    public static StorageReference getStorage(){
        return FirebaseStorage.getInstance().getReference();
    }



    public static void upDateImgPerfil(Activity activity, String urlImgStorage){
        // updatePerfil com novo img
        FirebaseUser user = FirebaseRef.getAuth().getCurrentUser();
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(urlImgStorage))
                .build();

        assert user != null;
        user.updateProfile( profile )
            .addOnCompleteListener(task1 -> {
                if(!task1.isSuccessful()){
                    Toast.makeText(activity,
                            "Erro ao atualizar foto de perfil", Toast.LENGTH_SHORT).show();
                }else{
                    if(activity != null){
                        activity.startActivity(activity.getIntent());
                    }
                }
            });
    }
}

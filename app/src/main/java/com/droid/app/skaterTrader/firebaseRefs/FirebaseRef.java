package com.droid.app.skaterTrader.firebaseRefs;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.droid.app.skaterTrader.R;
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
                            R.string.erro_ao_atualizar_foto_de_perfil,
                            Toast.LENGTH_SHORT).show();
                }else{
                    activity.recreate();
                    Log.i("imgPerfil >", "Imagem atualizada!");
                }
            });
    }
}
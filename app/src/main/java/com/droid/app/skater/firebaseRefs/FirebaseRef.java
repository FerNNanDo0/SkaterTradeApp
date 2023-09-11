package com.droid.app.skater.firebaseRefs;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
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
}

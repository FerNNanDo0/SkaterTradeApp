package com.droid.app.olx.firebaseRefs;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class FirebaseRef {
    public static FirebaseAuth getAuth(){
        return FirebaseAuth.getInstance();
    }
    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference();
    }
    public static StorageReference getStorage(){
        return FirebaseStorage.getInstance().getReference();
    }
}

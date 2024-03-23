package com.droid.app.skaterTrader.firebase;

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;

public class UserFirebase {


    public static boolean UserLogado(){
        return FirebaseRef.getAuth().getCurrentUser() != null;
    }
}

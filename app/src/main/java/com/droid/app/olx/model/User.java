package com.droid.app.olx.model;

import com.droid.app.olx.firebaseRefs.FirebaseRef;

public class User {
    private String email;
    private String senha;
    private String id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getIdUser() {
        if( FirebaseRef.getAuth().getCurrentUser() != null ){
            this.id = FirebaseRef.getAuth().getCurrentUser().getUid();
        }
        return id;
    }
    public static boolean UserLogado(){
        return FirebaseRef.getAuth().getCurrentUser() != null;
    }
}

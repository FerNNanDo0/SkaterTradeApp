package com.droid.app.olx.model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.droid.app.olx.firebaseRefs.FirebaseRef;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
public class User {

    private String nome;
    private String email;
    private String senha;
    private String id;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
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
    public void atulizarNome(Context context){
        try {
            if(UserLogado()){
                FirebaseUser user = FirebaseRef.getAuth().getCurrentUser();
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(getNome())
                        .build();

                assert user != null;
                user.updateProfile( profile )
                    .addOnCompleteListener( (@NonNull Task<Void> task) -> {
                        if(!task.isSuccessful()){
                            Toast.makeText(context,
                                    "Erro ao atualizar nome de perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

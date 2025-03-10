package com.droid.app.skaterTrader.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.activity.ActivityMain;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class User {
    private String nome;
    private String email;
    private String senha;
    private String id;
    private byte[] foto;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
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
                                    context.getString(R.string.erro_ao_atualizar_nome_de_perfil),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void salvarFotoPerfil(Activity activity){
        StorageReference storage = FirebaseRef.getStorage();
        try{
            StorageReference imgUser = storage.child("imagens")
                    .child("usuario")
                    .child(getIdUser())
                    .child("imagem_perfil");

            // fazer upload da imagem
            UploadTask uploadTask = imgUser.putBytes(getFoto());
            uploadTask.addOnSuccessListener( taskSnapshot -> {
                // Faz download dos Urls das fotos
                imgUser.getDownloadUrl().addOnCompleteListener( task -> {

                    System.out.println("Link foto " + task.getResult().toString());

                    String urlImgStorage = task.getResult().toString();

                    // update img perfil
                    FirebaseRef.upDateImgPerfil(activity, urlImgStorage);

                });
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static FirebaseUser user(){
        return FirebaseRef.getAuth().getCurrentUser();
    }
}

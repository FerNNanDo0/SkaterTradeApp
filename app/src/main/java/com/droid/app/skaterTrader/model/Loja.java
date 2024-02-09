package com.droid.app.skaterTrader.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.activity.AcessoActivity;
import com.droid.app.skaterTrader.activity.ActivityMainLoja;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class Loja {
    Activity activity;
    StorageReference storage, imgAnuncio;
    DatabaseReference database, lojaRef;
    private String nomeLoja;
    private String nomeUser;
    private String cpfOrCnpj;
    private String email;
    private String senha;
    private String imagemLogo;
    private String id;
    private ModelCnpj endereço;
    private String telefone;
    private final String tipoUser = "L";

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public ModelCnpj getModelCnpj() {
        return endereço;
    }

    public void setModelCnpj(ModelCnpj modelCnpj) {
        this.endereço = modelCnpj;
    }

    public String getNomeLoja() {
        return nomeLoja;
    }

    public void setNomeLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public String getCpfOrCnpj() {
        return cpfOrCnpj;
    }

    public void setCpfOrCnpj(String cpfOrCnpj) {
        this.cpfOrCnpj = cpfOrCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getImagemLogo() {
        return imagemLogo;
    }

    public void setImagemLogo(String imagemLogo) {
        this.imagemLogo = imagemLogo;
    }


    // gerar ID para o usuario
    public String getIdLoja() {
        if( FirebaseRef.getAuth().getCurrentUser() != null ){
            this.id = FirebaseRef.getAuth().getCurrentUser().getUid();
        }
        return id;
    }

    public static boolean UserLogado(){
        return FirebaseRef.getAuth().getCurrentUser() != null;
    }

    public void salvarDados() {
        //salvar dados no Firebase
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(getIdLoja());

        lojaRef.setValue(this);
    }

    // salvar imagens no storage
    public void salvarImgLogoLoja( byte[] img, Activity activityB ) {
        this.activity = activityB;

        // iniciar referencias do firebase
        storage = FirebaseRef.getStorage();
        imgAnuncio = storage.child("imagens")
                .child("logo_lojas")
                .child(getIdLoja())
                .child("imagem" + 1);

        // fazer upload da imagem
        UploadTask uploadTask = imgAnuncio.putBytes(img);

        uploadTask.addOnSuccessListener( taskSnapshot ->
                        fazerDownloadUrlFoto()
                )
        .addOnFailureListener(
            erroUpload -> Toast.makeText(activity, "Falha ao fazer upload!", Toast.LENGTH_SHORT).show()
        );
    }

    private void fazerDownloadUrlFoto(){
        // Faz download da Url da foto
        imgAnuncio.getDownloadUrl().addOnCompleteListener( task -> {

            System.out.println("Link foto " + task.getResult().toString());

            String urlImgStorage = task.getResult().toString();
            setImagemLogo(urlImgStorage);

            // update img perfil
            FirebaseRef.upDateImgPerfil(activity, urlImgStorage);

            atualizarDadosDB(urlImgStorage);
        });
    }

    // atualizar dados no DB
    private void atualizarDadosDB(String linkUrl){
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(getIdLoja());

        Map<String, Object> updateDB = new HashMap<>();
        updateDB.put("urlLogo",linkUrl);

        lojaRef.updateChildren(updateDB);
    }

    // salvar tipo de user
    public void atulizarTipoDeUser(){
        try {
            if(UserLogado()){
                FirebaseUser user = FirebaseRef.getAuth().getCurrentUser();
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(tipoUser)
                        .build();

                assert user != null;
                user.updateProfile( profile )
                        .addOnCompleteListener( (@NonNull Task<Void> task) -> {
                            if(!task.isSuccessful()){
                                Toast.makeText(activity,
                                        "Erro ao definir tipo de perfil", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

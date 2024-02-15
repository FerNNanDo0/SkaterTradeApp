package com.droid.app.skaterTrader.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.activity.ActivityMainLoja;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
public class Loja {
    boolean bool;
    Activity activity;
    StorageReference storage, imgAnuncio;
    DatabaseReference database, lojaRef;
    private String nomeLoja;
    private String nomeUser;
    private String cpfOrCnpj;
    private String email;
    private String senha;
    private String urlLogo;
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

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
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

    public void salvarDados(Activity activityC, byte[] dadosImg) {
        this.activity = activityC;
        //salvar dados no Firebase
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(getIdLoja());

        atulizarTipoDeUser();
        lojaRef.setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    salvarImgLogoLoja(dadosImg);

                    activity.startActivity(new Intent(activity, ActivityMainLoja.class));
                    activity.finish();
                }
            }
        });
    }

    // salvar imagens no storage
    public void salvarImgLogoLoja( byte[] img) {
        try{
            // iniciar referencias do firebase
            storage = FirebaseRef.getStorage();
            imgAnuncio = storage.child("imagens")
                    .child("logo_lojas")
                    .child(getIdLoja())
                    .child("imagem" + 1);

            // fazer upload da imagem
            UploadTask uploadTask = imgAnuncio.putBytes(img);
            uploadTask.addOnSuccessListener( taskSnapshot -> {
                fazerDownloadUrlFoto();
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void fazerDownloadUrlFoto(){
        // Faz download da Url da foto
        imgAnuncio.getDownloadUrl().addOnCompleteListener( task -> {

            System.out.println("Link foto " + task.getResult().toString());

            String urlImgStorage = task.getResult().toString();
            setUrlLogo(urlImgStorage);

            // update img perfil
            FirebaseRef.upDateImgPerfil(null, urlImgStorage);
            atualizarDadosDB(null, null);
        });
    }

    // atualizar dados no DB
    public void atualizarDadosDB(ProgressBar progress, Button btn){
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(getIdLoja());

        Map<String, Object> updateDB = new HashMap<>();
        if(getUrlLogo() != null && !getUrlLogo().isEmpty()){
            updateDB.put("urlLogo",getUrlLogo());
        }
        if(getNomeLoja() != null && !getNomeLoja().isEmpty()){
            updateDB.put("nomeLoja",getNomeLoja());
        }
        if(getNomeUser() != null && !getNomeUser().isEmpty()){
            updateDB.put("nomeUser",getNomeUser());
        }
        if(getTelefone() != null && !getTelefone().isEmpty()){
            updateDB.put("telefone",getTelefone());
        }

        lojaRef.updateChildren(updateDB).addOnCompleteListener(task -> {
            if(task.isSuccessful() && progress != null && btn != null){
                progress.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
            }
        });
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

    public void getNameLojaDb(TextView editNameLoja){
        //obter dados no Firebase
        DatabaseReference database = FirebaseRef.getDatabase();
        DatabaseReference lojaRef = database.child("lojas").child(getIdLoja());

        lojaRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Loja loja = task.getResult().getValue(Loja.class);
                if(loja != null) {
                    editNameLoja.setText( loja.getNomeLoja() );
                }
            }
        });
    }
}

package com.droid.app.skaterTrader.model;

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Loja {
    StorageReference storage;
    private String nomeLoja;
    private String nomeUser;
    private String cpfOrCnpj;
    private String email;
    private String imagemLogo;
    private ModelCep modelCep;
    private String id;


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

    public String getImagemLogo() {
        return imagemLogo;
    }

    public void setImagemLogo(String imagemLogo) {
        this.imagemLogo = imagemLogo;
    }

    public ModelCep getModelCep() {
        return modelCep;
    }
    public void setModelCep(ModelCep modelCep) {
        this.modelCep = modelCep;
    }

    public String getIdLoja() {
        if( FirebaseRef.getAuth().getCurrentUser() != null ){
            this.id = FirebaseRef.getAuth().getCurrentUser().getUid();
        }
        return id;
    }

    public void salvar() {

    }

    // salvar imagens no storage
    public void salvarImgLogoLoja( byte[] urlImg, int index) {
        // iniciar referencias do firebase
        storage = FirebaseRef.getStorage();
        StorageReference imgAnuncio = storage.child("imagens")
                .child("logo_lojas")
                .child(getIdLoja())
                .child("imagem" + index);

        // fazer upload da imagem
        UploadTask uploadTask = imgAnuncio.putBytes(urlImg);
        //UploadTask uploadTask = imgAnuncio.putFile(Uri.parse(urlImg));
    }
}

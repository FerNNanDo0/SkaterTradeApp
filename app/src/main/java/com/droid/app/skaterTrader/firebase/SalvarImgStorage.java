package com.droid.app.skaterTrader.firebase;

import androidx.annotation.NonNull;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.viewModel.ViewModelCadastroAnuncio;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class SalvarImgStorage {

    StorageReference storage;
    ViewModelCadastroAnuncio viewModelCadastroAnuncio;

    public SalvarImgStorage(ViewModelCadastroAnuncio viewModel) {
        this.viewModelCadastroAnuncio = viewModel;

        // iniciar referencias do firebase
        storage = FirebaseRef.getStorage();
    }

    public void interarImg(@NonNull Anuncio anuncio, @NonNull List<byte[]> listaFotosRecuperadas){
        int totalFotos = listaFotosRecuperadas.size();

        for (int i = 0; i < totalFotos; i++) {
            byte[] img = listaFotosRecuperadas.get(i);
            salvarImgsAnuncio(anuncio, img, i);
        }
    }


    // salvar imagens no storage
    public void salvarImgsAnuncio(@NonNull Anuncio anuncio, byte[] urlImg, int index) {

        StorageReference imgAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + index);

        // fazer upload da imagem
        UploadTask uploadTask = imgAnuncio.putBytes(urlImg);
        //UploadTask uploadTask = imgAnuncio.putFile(Uri.parse(urlImg));

        uploadTask.addOnSuccessListener( taskSnapshot -> {
                    // Faz download dos Urls das fotos
                    imgAnuncio.getDownloadUrl().addOnCompleteListener( task -> {

                        System.out.println("Link foto " + task.getResult().toString());

                        String urlImgStorage = task.getResult().toString();

                        viewModelCadastroAnuncio.setUrlImgStorage(urlImgStorage);

                    });
                })
                .addOnFailureListener(erroUpload -> {}/*exibirMsgToast("Falha ao fazer upload!")*/);
    }

}
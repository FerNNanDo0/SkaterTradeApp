package com.droid.app.skaterTrader.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.firebase.SalvarImgStorage;
import com.droid.app.skaterTrader.helper.DownloadImage;
import com.droid.app.skaterTrader.model.Anuncio;

import java.util.List;

public class ViewModelCadastroAnuncio extends ViewModel {

    MutableLiveData<String> urlImgStorage;
    MutableLiveData<byte[]> byteDadosImg;
    SalvarImgStorage salvarImgStorage;

    public ViewModelCadastroAnuncio() {
        this.urlImgStorage = new MutableLiveData<>();
        this.byteDadosImg = new MutableLiveData<>();
        salvarImgStorage = new SalvarImgStorage(this);
    }

    // download img para edit anuncio
    public void downloadImage(String url) {
        DownloadImage.download(url, this);
    }

     public void setByteImg(byte[] dadosImg){
        this.byteDadosImg.postValue(dadosImg);
     }

     public MutableLiveData<byte[]> getByteDadosImg(){
        return byteDadosImg;
     }



    // salvar anúncio para o usuário
    public void salvarAnuncio(@NonNull Anuncio anuncio, List<byte[]> listaFotosRecuperadas){
        salvarImgStorage.interarImg(anuncio, listaFotosRecuperadas);
    }

    // get and set
    public MutableLiveData<String> getUrlImgStorage() {
        return urlImgStorage;
    }

    public void setUrlImgStorage(String urlImgStorage) {
        this.urlImgStorage.postValue( urlImgStorage );
    }



    // salvar anúncio para loja
    public void salvarAnuncioLoja(@NonNull Anuncio anuncio, List<byte[]> listaFotosRecuperadas){
        salvarImgStorage.interarImg(anuncio, listaFotosRecuperadas);
    }

}

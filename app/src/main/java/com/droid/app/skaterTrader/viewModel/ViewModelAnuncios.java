package com.droid.app.skaterTrader.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.firebase.RecuperarAnuncios;
import com.droid.app.skaterTrader.model.Anuncio;
import java.util.List;
public class ViewModelAnuncios extends ViewModel {
    MutableLiveData<List<Anuncio>> listModel;
    MutableLiveData<String> showMsg;
    MutableLiveData<String> showErro;

    public ViewModelAnuncios() {
        this.listModel = new MutableLiveData<>();
        this.showMsg = new MutableLiveData<>();
        this.showErro = new MutableLiveData<>();
    }
    public void recuperarAnunciosUser() {
        RecuperarAnuncios recuperarAnuncios = new RecuperarAnuncios();
        recuperarAnuncios.recuperarAnunciosUser(this);
    }
    public void recuperarAnuncios() {
        RecuperarAnuncios recuperarAnuncios = new RecuperarAnuncios();
        recuperarAnuncios.recuperarAnuncios(this);
    }

    // get and set
    public LiveData<List<Anuncio>> getList() {
        return listModel;
    }

    public void setListModel(List<Anuncio> list){
        this.listModel.postValue(list);
    }

    public MutableLiveData<String> getShowMsg() {
        return showMsg;
    }

    public void setShowMsg(String showMsg) {
        this.showMsg.postValue( showMsg );
    }

    public MutableLiveData<String> getErroMsg() {
        return showErro;
    }

    public void setErroMsg(String showErroMsg) {
        this.showErro.postValue( showErroMsg );
    }
}

package com.droid.app.skaterTrader.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;

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
    @SuppressLint("StaticFieldLeak")
    RecuperarAnuncios recuperarAnuncios;

    public ViewModelAnuncios() {
        this.listModel = new MutableLiveData<>();
        this.showMsg = new MutableLiveData<>();
        this.showErro = new MutableLiveData<>();
        recuperarAnuncios = new RecuperarAnuncios();
    }

    public void recuperarAnunciosUser(Context context) {
        recuperarAnuncios.recuperarAnunciosUser(this, context);
    }

    public void recuperarAnuncios(Context context) {
        recuperarAnuncios.recuperarAnuncios(this, context);
    }

    public void filtrarEstadoAnuncios(String estado, String categoriaSelected, Context context){
        recuperarAnuncios.filtarAnunciosEstado(this, estado, categoriaSelected, context);
    }

    public void filtrarCategoriaAnuncios(String categoria, String categoriaSelected, Context context){
        recuperarAnuncios.filtarAnunciosCategoria(this, categoria, categoriaSelected, context);
    }

    public void removeEventListener(){
        recuperarAnuncios.removeEventListener();
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
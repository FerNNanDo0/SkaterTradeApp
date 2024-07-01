package com.droid.app.skaterTrader.viewModel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.model.ModelCnpj;
import com.droid.app.skaterTrader.service.ResultCnpj;
import com.droid.app.skaterTrader.service.VerificarCNPJ;

public class ViewModelRequestCNPJ extends ViewModel {
    MutableLiveData<ModelCnpj> liveDataRequestDadosCnpj;
    MutableLiveData<String> liveDataErroRequestCnpj;
    Handler handler;

    //constructor
    public ViewModelRequestCNPJ(){
        this.liveDataRequestDadosCnpj = new MutableLiveData<>();
        this.liveDataErroRequestCnpj = new MutableLiveData<>();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void getDadosCNPJ(String cnpj){
        new VerificarCNPJ( cnpj , new ResultCnpj() {
            @Override
            public void onRequestDadosCnpj(ModelCnpj modelCnpj) {
                handler.post( () ->
                        setLiveDataRequestDadosCnpj(modelCnpj)
                );
            }

            @Override
            public void onErroRequestCnpj(String erro) {
                handler.post(() ->
                        setLiveDataErroRequestCnpj(erro)
                );
            }
        });
    }
    // getter setter

    // request CNPJ
    private void setLiveDataRequestDadosCnpj(ModelCnpj cnpj){
        this.liveDataRequestDadosCnpj.postValue(cnpj);
    }
    private void setLiveDataErroRequestCnpj(String erro){
        this.liveDataErroRequestCnpj.postValue(erro);
    }
    public MutableLiveData<ModelCnpj> getLiveDataRequestDadosCnpj() {
        return liveDataRequestDadosCnpj;
    }
    public MutableLiveData<String> getLiveDataErroRequestCnpj() {
        return liveDataErroRequestCnpj;
    }
}
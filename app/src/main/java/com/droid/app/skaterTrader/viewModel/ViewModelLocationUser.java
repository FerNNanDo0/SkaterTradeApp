package com.droid.app.skaterTrader.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// recupera anúncios com base na localização
public class ViewModelLocationUser extends ViewModel {
    MutableLiveData<String> estado;

    public ViewModelLocationUser() {
        this.estado = new MutableLiveData<>();
    }

    public MutableLiveData<String> getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado.postValue( estado );
    }
}

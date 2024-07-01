package com.droid.app.skaterTrader.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.firebase.GetDadosConsultas;
import com.google.firebase.database.DataSnapshot;

public class ViewModelDadosConsulta extends ViewModel {

    MutableLiveData<DataSnapshot> dataSnapshot;

    public ViewModelDadosConsulta() {
        this.dataSnapshot = new MutableLiveData<>();
    }

    public void getDadosConsulta(){
        GetDadosConsultas getDadosConsultas = new GetDadosConsultas();
        getDadosConsultas.getDados(this);
    }


    // get and set
    public MutableLiveData<DataSnapshot> getDataSnapshot() {
        return dataSnapshot;
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot) {
        this.dataSnapshot.postValue(dataSnapshot);
    }
}
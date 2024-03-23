package com.droid.app.skaterTrader.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.firebase.GetPicosDb;
import com.google.firebase.database.DataSnapshot;

public class ViewModelMapaPicos extends ViewModel {

    MutableLiveData<DataSnapshot> snapShot;

    public ViewModelMapaPicos() {
        this.snapShot = new MutableLiveData<>();
    }

    public void recuperarPontosDePico(){
        GetPicosDb.recuperarPontosDePico(this);
    }

    public void setSnapShot(DataSnapshot snapShot) {
        this.snapShot.postValue(snapShot);
    }

    public MutableLiveData<DataSnapshot> getSnapShot(){
        return snapShot;
    }
}

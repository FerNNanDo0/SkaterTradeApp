package com.droid.app.skaterTrader.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelCNPJ extends ViewModel {
    MutableLiveData<String> liveData = new MutableLiveData<>();

    public void setLiveData(String message) {
        this.liveData.postValue(message);
    }

    public MutableLiveData<String> getLiveData() {
        return liveData;
    }
}

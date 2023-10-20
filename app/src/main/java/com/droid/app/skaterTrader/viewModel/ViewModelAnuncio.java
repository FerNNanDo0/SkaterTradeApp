package com.droid.app.skaterTrader.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.droid.app.skaterTrader.model.Anuncio;
import java.util.List;
public class ViewModelAnuncio extends androidx.lifecycle.ViewModel {
    MutableLiveData<List<Anuncio>> listModel;

    public ViewModelAnuncio() {
        this.listModel = new MutableLiveData<>();
    }

    public LiveData<List<Anuncio>> getList() {
        return listModel;
    }
    public void setListModel(List<Anuncio> list){
        this.listModel.setValue(list);
    }

}

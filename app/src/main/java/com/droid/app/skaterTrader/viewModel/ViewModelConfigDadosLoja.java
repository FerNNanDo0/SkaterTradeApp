package com.droid.app.skaterTrader.viewModel;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.firebase.GetDadosLojas;
import com.droid.app.skaterTrader.model.Loja;
import com.google.firebase.database.DatabaseError;

public class ViewModelConfigDadosLoja extends ViewModel {
    MutableLiveData<Boolean> bol;
    MutableLiveData<Loja> loja;
    MutableLiveData<DatabaseError> error;
    GetDadosLojas getDadosLojas;

    public ViewModelConfigDadosLoja() {
        this.bol = new MutableLiveData<>();
        this.loja = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
        getDadosLojas = new GetDadosLojas();
    }

    public void removeEventListener(){
        getDadosLojas.removeEventListener();
    }

    // Get dados da loja
    public void getDadosDB(@NonNull ProgressBar progress){
        progress.setVisibility(View.VISIBLE);
        getDadosLojas.getDadosDb(this);
    }


    // get and set
    public void setLoja(Loja loja){
        this.loja.postValue(loja);
    }

    public MutableLiveData<Loja> getDadosLoja(){
        return loja;
    }

    public void setError(DatabaseError error){
        this.error.postValue(error);
    }
    public MutableLiveData<DatabaseError> getError(){
        return error;
    }




    // Salvar imagem logo da loja
    public void salvarImgLogoLoja(byte[] dadosImg) {
        Loja loja = new Loja(this);
        loja.salvarImgLogoLoja(dadosImg, "atualizar");
    }

    // Atualiza dados da loja ao DB
    public void atualizarDadosDB(@NonNull Loja loja){
        loja.atualizarDadosDB();
    }


    // get and set
    public MutableLiveData<Boolean> getBol() {
        return bol;
    }

    public void setBol(boolean bol) {
        this.bol.postValue( bol );
    }
}
package com.droid.app.skaterTrader.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelFirebase extends ViewModel {

    MutableLiveData<Boolean> cadastrado;
    MutableLiveData<String> login;
    MutableLiveData<String> erroCadastrado, erroLogin;
    MutableLiveData<String> liveDataShowToast;

    // Constructor
    public ViewModelFirebase() {
        this.cadastrado = new MutableLiveData<>();
        this.erroCadastrado = new MutableLiveData<>();
        this.liveDataShowToast = new MutableLiveData<>();
        this.login = new MutableLiveData<>();
        this.erroLogin = new MutableLiveData<>();
    }

    // getter setter

    // show Toast de Email de comfirmação de cadastro
    public void setTxtToast(String msg) {
        this.liveDataShowToast.postValue( msg );
    }
    public MutableLiveData<String> getLiveDataShowToast() {
        return liveDataShowToast;
    }



    // Login \\
    public void setLogado(String typeUser) {
        this.login.postValue( typeUser );
    }

    public void setErroLogin(String erroCadastrado) {
        this.erroLogin.postValue( erroCadastrado );
    }

    public MutableLiveData<String> getResultLogin() {
        return login;
    }
    public MutableLiveData<String> getErroLogin() {
        return erroLogin;
    }

    // Cadastro \\
    public void setCadastrado(boolean cadastrado) {
        this.cadastrado.postValue( cadastrado );
    }

    public void setErroCadastro(String erroCadastrado) {
        this.erroCadastrado.postValue( erroCadastrado );
    }

    public MutableLiveData<Boolean> getResultCadastro() {
        return cadastrado;
    }

    public MutableLiveData<String> getErroCadastro() {
        return erroCadastrado;
    }

}

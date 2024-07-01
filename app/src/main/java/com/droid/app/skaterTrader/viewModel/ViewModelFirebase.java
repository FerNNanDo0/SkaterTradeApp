package com.droid.app.skaterTrader.viewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.droid.app.skaterTrader.firebase.CadastrarUsers;
import com.droid.app.skaterTrader.firebase.LoginUsers;
import com.droid.app.skaterTrader.model.Loja;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

public class ViewModelFirebase extends ViewModel {

    MutableLiveData<Boolean> cadastrado;
    MutableLiveData<String> login;
    MutableLiveData<String> erroCadastrado, erroLogin;
    MutableLiveData<String> liveDataShowToast;

    MutableLiveData<FirebaseAuthUIAuthenticationResult> result;
    CadastrarUsers cadastrarUsers;
    LoginUsers loginUsers;

    // Constructor
    public ViewModelFirebase() {
        this.cadastrado = new MutableLiveData<>();
        this.erroCadastrado = new MutableLiveData<>();
        this.liveDataShowToast = new MutableLiveData<>();
        this.login = new MutableLiveData<>();
        this.erroLogin = new MutableLiveData<>();
        this.result = new MutableLiveData<>();
    }

    public void cadastrarUsuario(String email, String senha, Context context){
        cadastrarUsers = new CadastrarUsers(this, "USER");
        cadastrarUsers.cadastrarUser(email, senha, context);
    }

    public void cadastrarLoja(byte[] dadosImg, String TYPE, String emailLoja,
                              String senhaLoja, @NonNull Loja loja, Context context){
        loja.setViewModel(this);
        cadastrarUsers = new CadastrarUsers(this, dadosImg, TYPE, loja);
        cadastrarUsers.cadastrarUser( emailLoja, senhaLoja, context);

    }

    public void logar(String email, String senha, Context context){
        loginUsers = new LoginUsers(this);
        loginUsers.logar(email, senha, context);
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

package com.droid.app.skaterTrader.firebase;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.activity.ActivityMainLoja;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.viewModel.ViewModelFirebase;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginUsers {

    ViewModelFirebase viewModel;
    public LoginUsers(ViewModelFirebase viewModel) {
        this.viewModel = viewModel;
    }

    public void logar(String email, String senha, Context context){
        FirebaseRef.getAuth().signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener( task -> {
                    if ( task.isSuccessful() ){

                        String tipoUser = UserFirebase.getTipoUser();//User.user().getDisplayName();

                        if(tipoUser.equals("L")){
                            viewModel.setLogado(tipoUser);
                        }else{
                            viewModel.setLogado("true");
                        }

                    }else{

                        String execao;
                        try {
                            throw Objects.requireNonNull(task.getException());

                        }catch (FirebaseAuthInvalidCredentialsException e){
                            execao = context.getString(R.string.nao_correspondem_a_um_usuario_cadastrado);

                        }catch (FirebaseAuthInvalidUserException e){
                            execao = context.getString(R.string.usuario_n_o_est_cadastrado);

                        }catch ( Exception e ){
                            execao = context.getString(R.string.erro_ao_cadastrar_usuario)+ e.getMessage();

                            e.printStackTrace();
                        }
                        viewModel.setErroLogin(execao);
                    }
                });
    }

}

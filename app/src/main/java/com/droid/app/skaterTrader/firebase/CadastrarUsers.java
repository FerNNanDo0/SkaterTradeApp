package com.droid.app.skaterTrader.firebase;

import android.content.Context;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.viewModel.ViewModelFirebase;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import java.util.Objects;

public class CadastrarUsers {
    ViewModelFirebase viewModel;
    byte[] dadosImg;
    String type;
    Loja loja;

    // constrautor - 1
    public CadastrarUsers(ViewModelFirebase viewModel, byte[] dadosImg, String tipo, Loja loja) {
        this.viewModel = viewModel;
        this.dadosImg = dadosImg;
        this.type = tipo;
        this.loja = loja;
    }

    // constrautor - 2
    public CadastrarUsers(ViewModelFirebase viewModel, String tipo) {
        this.viewModel = viewModel;
        this.type = tipo;
    }

    public void cadastrarUser(String email, String senha, Context context){

        FirebaseRef.getAuth().createUserWithEmailAndPassword( email,senha )
                .addOnCompleteListener(task -> {
                    if( task.isSuccessful() ){

                        sendEmailComfirm(context);

                    }else{
                        String execao;

                        try{
                            throw Objects.requireNonNull(task.getException());

                        } catch (FirebaseAuthWeakPasswordException passwordException){
                            execao = context.getString(R.string.digite_uma_senha_mais_forte);

                        } catch (FirebaseAuthInvalidCredentialsException invalidCredentials){
                            execao = context.getString(R.string.digite_um_e_mail_v_lido);

                        } catch (FirebaseAuthUserCollisionException collision ){
                            execao = context.getString(R.string.ja_foi_cadastrada_no_sistema);
                        } catch (Exception e){
                            execao = context.getString(R.string.usuario_n_o_est_cadastrado)+ e.getMessage();

                            throw new RuntimeException(e);
                        }

                        viewModel.setErroCadastro(execao);
                    }
                });
    }

    private void sendEmailComfirm(Context context){
        // enviar email de comfirmação
        if( FirebaseRef.getAuth().getCurrentUser() != null){
            FirebaseRef.getAuth().getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){

                            // show toast msg
                            String msg = context.getString(R.string.enviado_para_seu_email);
                            viewModel.setTxtToast(msg);


                            // verificar o tipo de usuario
                            verificarType(context);
                        }
                    });
        }
    }

    private void verificarType(Context context){
        if(type.equals("LOJA")){
            loja.salvarDados( dadosImg, context );
        }else {
            viewModel.setCadastrado(true);
        }
    }

}

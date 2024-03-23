package com.droid.app.skaterTrader.firebase;

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

    // constrautor - 1
    public CadastrarUsers(ViewModelFirebase viewModel, byte[] dadosImg, String tipo) {
        this.viewModel = viewModel;
        this.dadosImg = dadosImg;
        this.type = tipo;
    }

    // constrautor - 2
    public CadastrarUsers(ViewModelFirebase viewModel, String tipo) {
        this.viewModel = viewModel;
        this.type = tipo;
    }

    public void cadastrarUser(String email, String senha){

        FirebaseRef.getAuth().createUserWithEmailAndPassword( email,senha )
                .addOnCompleteListener(task -> {
                    if( task.isSuccessful() ){

                        sendEmailComfirm();

                    }else{
                        String execao;

                        try{
                            throw Objects.requireNonNull(task.getException());

                        } catch (FirebaseAuthWeakPasswordException passwordException){
                            execao = "Digite uma senha mais forte!";

                        } catch (FirebaseAuthInvalidCredentialsException invalidCredentials){
                            execao = "Digite um e-mail válido!";

                        } catch (FirebaseAuthUserCollisionException collision ){
                            execao = "Uma conta com esse E-mail já foi cadastrada no sistema!\nFaça o login";
                        } catch (Exception e){
                            execao = "Erro ao cadastrar usuario: "+ e.getMessage() ;

                            throw new RuntimeException(e);
                        }

                        viewModel.setErroCadastro(execao);
                    }
                });
    }

    private void sendEmailComfirm(){
        // enviar email de comfirmação
        if( FirebaseRef.getAuth().getCurrentUser() != null){
            FirebaseRef.getAuth().getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){

                            // show toast msg
                            String msg = "Um email de confirmação foi enviado para seu email.";
                            viewModel.setTxtToast(msg);


                            // verificar o tipo de usuario
                            verificarType();
                        }
                    });
        }
    }

    private void verificarType(){
        if(type.equals("LOJA")){
            Loja loja = new Loja(viewModel);
            loja.salvarDados( dadosImg );
        }else {
            viewModel.setCadastrado(true);
        }
    }

}

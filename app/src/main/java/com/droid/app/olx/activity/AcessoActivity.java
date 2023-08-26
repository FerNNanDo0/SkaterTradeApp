package com.droid.app.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.Toast;

import com.droid.app.olx.R;
import com.droid.app.olx.firebaseRefs.FirebaseRef;
import com.droid.app.olx.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineContextKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.DispatchersKt;

public class AcessoActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAcesso;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAcess;
    EditText editTextEmail, editTextSenha;
    ProgressBar progressBar;
    User user;
    Intent activity2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_activity);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initAtributos();
        switchAcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchAcess.isChecked()) {
                    btnAcesso.setText(R.string.btn_cadastrar);
                } else {
                    btnAcesso.setText(R.string.btn_logar);
                }
            }
        });
        btnAcesso.setOnClickListener(this);
    }
    private void initAtributos(){
        btnAcesso = findViewById(R.id.btnAcessar);
        switchAcess = findViewById(R.id.switch1);
        editTextEmail = findViewById(R.id.editEmail);
        editTextSenha = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);

//        reff do user
        user = new User();

        // ref activity 2
        activity2 = new Intent(this, ActivityMain.class);
    }
    private void cadastrarUser(String email, String senha){
        FirebaseRef.getAuth().createUserWithEmailAndPassword( email,senha )
                .addOnCompleteListener(task -> {
                    if( task.isSuccessful() ){
                        btnAcesso.setOnClickListener(null);
                        if (activity2 != null){
                            startActivity(activity2);
                            finish();
                        }
                        Toast.makeText(AcessoActivity.this, "Usuario cadastrado com sucesso!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        String execao;
                        progressBar.setVisibility( View.GONE );
                        try{
                            throw Objects.requireNonNull(task.getException());

                        } catch (FirebaseAuthWeakPasswordException passwordException){
                            execao = "Digite uma senha mais forte!";

                        } catch (FirebaseAuthInvalidCredentialsException invalidCredentials){
                            execao = "Digite um e-mail válido!";

                        } catch (FirebaseAuthUserCollisionException collision){
                            execao = "Uma conta com esse E-mail já foi cadastrada no sistema!";

                        } catch (Exception e){
                            execao = "Erro ao cadastrar usuario: "+ e.getMessage() ;
                            e.printStackTrace();
                        }
                        Toast.makeText(AcessoActivity.this, "Erro: "+execao,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void logarUser(String email, String senha){
        FirebaseRef.getAuth().signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener( task -> {
                    if ( task.isSuccessful() ){
                        btnAcesso.setOnClickListener(null);
                        if (activity2 != null){
                            startActivity(activity2);
                            finish();
                        }
                    }else{
                        progressBar.setVisibility( View.GONE );
                        assert task.getException() != null;
                        Toast.makeText(AcessoActivity.this, "Erro: "+task.getException().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // closed keyBoard
    private void closedKeyBoard(){
        View view = getWindow().getCurrentFocus();
        if(view != null){
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(),0);
        }
    }

    @Override
    public void onClick(View v) {

        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();

        // verificar se há email e senha digitado
        if ( !email.isEmpty() ){
            if ( !senha.isEmpty() ){
                user.setEmail(email);
                user.setSenha(senha);

                // ocultar teclado
                closedKeyBoard();

                // Verificar o estado do switch
                if ( switchAcess.isChecked() ){  // cadastro

                    progressBar.setVisibility(View.VISIBLE);
                    cadastrarUser( user.getEmail(), user.getSenha() );
                }else{  // loguin

                    progressBar.setVisibility(View.VISIBLE);
                    logarUser( user.getEmail(), user.getSenha() );
                }

            }else{
                Toast.makeText(AcessoActivity.this, "Informe uma senha",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(AcessoActivity.this, "Informe um e-mail",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

package com.droid.app.skaterTrader.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class AcessoActivity extends AppCompatActivity
        implements View.OnClickListener {
    Button btnAcesso, btnGoogle, btnLoja;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAcess;
    EditText editTextEmail, editTextSenha, editTextNome;
    TextView textViewRedefinir, textViewPoliticasPrivacidade;
    ProgressBar progressBar;
    User user;
    Intent activity2;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent(getApplicationContext(), ActivityMain.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_activity);

        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        initAtributos();

        btnAcesso.setPadding(95, 0, 0, 0);
        switchAcess.setOnClickListener(v -> {
            if (switchAcess.isChecked()) {
                btnAcesso.setText(R.string.btn_cadastrar);
                btnAcesso.setPadding(80, 0, 0, 0);
                editTextNome.setVisibility(View.VISIBLE);
                textViewRedefinir.setVisibility(View.GONE);
                btnLoja.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Cadastrar");
            } else {
                btnAcesso.setText(R.string.btn_logar);
                btnAcesso.setPadding(95, 0, 0, 0);
                editTextNome.setVisibility(View.GONE);
                btnLoja.setVisibility(View.GONE);
                textViewRedefinir.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Login");
            }
        });
        btnAcesso.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
    }
    private void initAtributos(){
        btnLoja = findViewById(R.id.buttonLojas);
        btnLoja.setVisibility(View.GONE);
        btnAcesso = findViewById(R.id.btnAcessar);
        btnGoogle= findViewById(R.id.btnGoogle);
        switchAcess = findViewById(R.id.switch1);
        editTextEmail = findViewById(R.id.editEmail);
        editTextSenha = findViewById(R.id.editTextPassword);
        editTextNome = findViewById(R.id.editNome);
        progressBar = findViewById(R.id.progressBar);
        textViewRedefinir = findViewById(R.id.textViewRedefinir);
        textViewPoliticasPrivacidade =  findViewById(R.id.textViewPoliticas);
        textViewPoliticasPrivacidade.setVisibility(View.VISIBLE);

//        reff do user
        user = new User();

        // ref activity 2
        activity2 = new Intent(this, ActivityMain.class);
    }
    private void cadastrarUser(String email, String senha){
        FirebaseRef.getAuth().createUserWithEmailAndPassword( email,senha )
            .addOnCompleteListener(task -> {
                if( task.isSuccessful() ){

                    //atualizar nome
                    user.atulizarNome(this);

                    // enviar email de comfirmação
                    if( FirebaseRef.getAuth().getCurrentUser() != null){
                        FirebaseRef.getAuth().getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener( task1 -> {
                                if(task1.isSuccessful()){
                                    Toast.makeText(AcessoActivity.this,
                                            "Um email de confirmação foi enviado para seu email.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                    startActivity(new Intent(getIntent()));

                    /*if (activity2 != null){
                        startActivity(activity2);
                        finish();
                    }*/
                }else{
                    String execao;
                    progressBar.setVisibility( View.GONE );
                    btnAcesso.setVisibility(View.VISIBLE);
                    btnGoogle.setVisibility(View.VISIBLE);
                    btnLoja.setVisibility(View.VISIBLE);

                    try{
                        throw Objects.requireNonNull(task.getException());

                    } catch (FirebaseAuthWeakPasswordException passwordException){
                        execao = "Digite uma senha mais forte!";

                    } catch (FirebaseAuthInvalidCredentialsException invalidCredentials){
                        execao = "Digite um e-mail válido!";

                    } catch (FirebaseAuthUserCollisionException collision){
                        execao = "Uma conta com esse E-mail já foi cadastrada no sistema!";
                        Toast.makeText(this, "Faça o login", Toast.LENGTH_LONG).show();
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
        btnAcesso.setEnabled(false);
        btnGoogle.setEnabled(false);
        btnLoja.setEnabled(false);
        FirebaseRef.getAuth().signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener( task -> {
                    if ( task.isSuccessful() ){

                        if (activity2 != null){
                            startActivity(activity2);
                            finish();
                        }
                    }else{
                        btnAcesso.setVisibility(View.VISIBLE);
                        btnGoogle.setVisibility(View.VISIBLE);

                        btnAcesso.setEnabled(true);
                        btnGoogle.setEnabled(true);
                        btnLoja.setEnabled(true);

                        textViewRedefinir.setVisibility(View.VISIBLE);
                        progressBar.setVisibility( View.GONE );

                        String execao;
                        try {
                            throw Objects.requireNonNull(task.getException());

                        }catch (FirebaseAuthInvalidCredentialsException e){
                            execao = "E-mail ou senha não correspondem a um usuário cadastrado!";

                        }catch (FirebaseAuthInvalidUserException e){
                            execao = "Usuario não está cadastrado.";

                        }catch ( Exception e ){
                            execao = "Usuario não está cadastrado."+ e.getMessage();
                            e.printStackTrace();
                        }

                        exibirToast(execao);
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

    //exibir msgs para o usuario
    private void exibirToast(@NonNull String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View v) {

        switch(v.getId()){
            case R.id.btnAcessar:
                verificarDados();
                break;

            case R.id.btnGoogle:
                logarComGoogle();
                break;
        }
    }
    private void verificarDados(){
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

                    String nome = editTextNome.getText().toString().trim();
                    if(!nome.isEmpty()){
                        user.setNome(nome);

                        btnAcesso.setVisibility(View.INVISIBLE);
                        btnGoogle.setVisibility(View.INVISIBLE);
                        btnLoja.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        cadastrarUser( user.getEmail(), user.getSenha() );
                    }else{
                        Toast.makeText(AcessoActivity.this, "Informe seu nome",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{  // loguin
                    btnAcesso.setVisibility(View.INVISIBLE);
                    btnGoogle.setVisibility(View.INVISIBLE);
                    textViewRedefinir.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    logarUser( user.getEmail(), user.getSenha() );
                }

            }else{
                Toast.makeText(AcessoActivity.this, "Para a maior segurança informe uma Senha com no mínimo 10 caracters.",
                        Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(AcessoActivity.this, "Informe um e-mail",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void logarComGoogle(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onSignInResult(@NonNull FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if(response != null){
            if (result.getResultCode() == RESULT_OK) {
                // Successfully signed in
                FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();

                btnAcesso.setOnClickListener(null);
                btnGoogle.setOnClickListener(null);

                if (activity2 != null){
                    if(userF != null){
                        startActivity(activity2);
                        finish();
                    }
                }
            }else{
                // A entrada falhou. Se a resposta for nula, o usuário cancelou o
                // fluxo de login usando o botão Voltar. Caso contrário, verifique
                //response.getError().getErrorCode() e trate o erro.

                assert response.getError() != null;
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                    Toast.makeText(this,
                            "Problemas com acesso a internet", Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.PROVIDER_ERROR) {

                    Toast.makeText(this,
                            "Erro: "+response.getError(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public void btnRedefinirSenha(View view){
        startActivity(new Intent(this, RedefinirSenhaActivity.class));
        finish();
    }

    // politicas de privacidade
    public void politicasPrivacidade(View view){
        browseTo();
    }
    private void browseTo(){
        try {

            String url = "https://skatertrade.web.app/política-de-privacidade.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cadastrarLojas(View view){
        startActivity(new Intent(this, CadastrarLojasActivity.class));
    }

}

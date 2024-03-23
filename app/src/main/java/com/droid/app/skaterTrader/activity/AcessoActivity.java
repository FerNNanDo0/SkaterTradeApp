package com.droid.app.skaterTrader.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droid.app.skaterTrader.databinding.AcessoActivityBinding;
import com.droid.app.skaterTrader.firebase.CadastrarUsers;
import com.droid.app.skaterTrader.firebase.LoginUsers;
import com.droid.app.skaterTrader.helper.FecharTecladoSys;
import com.droid.app.skaterTrader.helper.IntentActionView;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.viewModel.ViewModelFirebase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
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
    LinearLayout linearLayout3;
    AcessoActivityBinding binding;
    ViewModelFirebase viewModelFirebase;


    @Override
    public boolean onSupportNavigateUp() {
        startActivity( new Intent(getApplicationContext(), ActivityMain.class));
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.acesso_activity);
        binding = AcessoActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //configurar ActionBar
        configActionBar();

        // iciar conponentes da tela
        startComponents();

        // config Switch
        switchAcess.setOnClickListener(v -> {
            if (switchAcess.isChecked()) {
                configSwitch(R.string.cadastrar,80, View.VISIBLE, View.GONE, R.string.cadastrar );

            } else {
                configSwitch(R.string.btn_logar, 95, View.GONE, View.VISIBLE, R.string.login);

            }
        });

        btnAcesso.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
    }
    private void startComponents(){
        btnLoja = binding.buttonLojas;//findViewById(R.id.buttonLojas);
        btnLoja.setVisibility(View.GONE);
        btnAcesso = binding.btnAcessar;//findViewById(R.id.btnAcessar);
        btnAcesso.setPadding(95, 0, 0, 0);
        btnGoogle = binding.btnGoogle;//findViewById(R.id.btnGoogle);
        switchAcess = binding.switch1;//findViewById(R.id.switch1);
        editTextEmail = binding.editEmail;//findViewById(R.id.editEmail);
        editTextSenha = binding.editTextPassword;//findViewById(R.id.editTextPassword);
        editTextNome = binding.editNome;//findViewById(R.id.editNome);
        progressBar = binding.progressBar;//findViewById(R.id.progressBar);
        textViewRedefinir = binding.textViewRedefinir;//findViewById(R.id.textViewRedefinir);
        textViewPoliticasPrivacidade = binding.textViewPoliticas;//findViewById(R.id.textViewPoliticas);
        textViewPoliticasPrivacidade.setVisibility(View.VISIBLE);

        linearLayout3 = binding.linearLayout3;//findViewById(R.id.linearLayout3);

//        reff do user
        user = new User();

        // ref activity 2
        activity2 = new Intent(this, ActivityMain.class);

        //instance ViewModelFirebase
        viewModelFirebase = new ViewModelProvider(this).get(ViewModelFirebase.class);
    }

    private void configActionBar(){
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
    }

    private void cadastrarUser(String email, String senha){

        // observe msg Toast email de comfirm
        viewModelFirebase.getLiveDataShowToast().observe(this,
                this::exibirToast
        );

        // observe cadastro
        viewModelFirebase.getResultCadastro().observe(this,
                bol -> {
                    if(bol){
                        //atualizar nome
                        user.atulizarNome(this);
                        startActivity(activity2);
                    }
                }
        );

        // observe Erro de cadastro
        viewModelFirebase.getErroCadastro().observe(this,
                erro -> {
                    exibirToast("ERRO: "+erro);
                    configBtnAndProgressUI(View.VISIBLE, View.GONE, View.VISIBLE);
                }
        );

        // Cadastrar User
        CadastrarUsers cadastrarUsers = new CadastrarUsers(viewModelFirebase, "USER");
        cadastrarUsers.cadastrarUser(email, senha);
    }
    private void logarUser(String email, String senha){
        // config btns da ui
        configdBtns(false);

        viewModelFirebase.getResultLogin().observe(this,
            bol -> {
                if(bol.equals("L")){ // LOJA
                    startActivity(new Intent(this, ActivityMainLoja.class));
                    finish();

                }else{// USER
                    if (activity2 != null){
                        startActivity(activity2);
                        finish();
                    }
                }
            }
        );

        viewModelFirebase.getErroLogin().observe(this,
            erro -> {
                // config visibility views da ui
                textViewRedefinir.setVisibility(View.VISIBLE);
                configBtnAndProgressUI( View.VISIBLE, View.GONE, View.VISIBLE );

                // config btns da ui
                configdBtns(true);

                exibirToast(erro);
            }
        );

        // logar User
        LoginUsers loginUsers = new LoginUsers(viewModelFirebase);
        loginUsers.logar(email, senha);

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
                FecharTecladoSys.closedKeyBoard(this);

                // Verificar o estado do switch
                if ( switchAcess.isChecked() ){  // cadastro

                    String nome = editTextNome.getText().toString().trim();
                    if(!nome.isEmpty()){
                        user.setNome(nome);

                        // config visibility das view da ui
                        configBtnAndProgressUI(View.INVISIBLE, View.VISIBLE, View.GONE);
                        // cadastrar users
                        cadastrarUser( user.getEmail(), user.getSenha() );

                    }else{
                        exibirToast("Informe seu nome");
                    }

                }else{  // loguin

                    // config visibility das view da ui
                    textViewRedefinir.setVisibility(View.GONE);
                    configBtnAndProgressUI(View.INVISIBLE, View.VISIBLE, View.GONE);

                    // logar users
                    logarUser( user.getEmail(), user.getSenha() );
                }

            }else{
                exibirToast("Para a maior segurança informe uma Senha com no mínimo 10 caracters.");
            }
        }else{
            exibirToast("Informe um e-mail");
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

                    exibirToast("Problemas com acesso a internet");
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.PROVIDER_ERROR) {
                    exibirToast("Erro: "+response.getError());
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
        IntentActionView.browseTo(this);
    }


    public void cadastrarLojas(View view){
        startActivity(new Intent(this, CadastrarLojasActivity.class));
    }

    private void configBtnAndProgressUI(int btnConfig, int progressConfig, int layConfig){
        progressBar.setVisibility( progressConfig );
        btnAcesso.setVisibility(btnConfig);
        btnGoogle.setVisibility(btnConfig);
        //btnLoja.setVisibility(btnConfig);
        linearLayout3.setVisibility(layConfig);
    }

    private void configdBtns(boolean bol){
        btnAcesso.setEnabled(bol);
        btnGoogle.setEnabled(bol);
        //btnLoja.setEnabled(bol);
    }

    private void configSwitch(int txt, int pd, int visib1, int visib2, int title ){
        btnAcesso.setText(txt);
        btnAcesso.setPadding(pd, 0, 0, 0);
        editTextNome.setVisibility(visib1);
        //btnLoja.setVisibility(visib1);
        textViewRedefinir.setVisibility(visib2);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}
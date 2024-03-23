package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.droid.app.skaterTrader.databinding.ActivityCadastrarLojasBinding;
import com.droid.app.skaterTrader.firebase.CadastrarUsers;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.droid.app.skaterTrader.helper.MaskEditCpfCNPJ;
import com.droid.app.skaterTrader.helper.Permissions;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Consulta;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.viewModel.ViewModelRequestCNPJ;
import com.droid.app.skaterTrader.viewModel.ViewModelFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import de.hdodenhof.circleimageview.CircleImageView;

public class CadastrarLojasActivity extends AppCompatActivity {
    EditText editNomeLoja, editNomeUser, editCpfOrCnpj, editEmail, editSenha, editNumero;
    Button btnCadastrarLoja;
    CircleImageView imageLoja;
    String emailLoja, senhaLoja, nomeLoja, nomeUser, cpfOuCnpj, telefone;
    Loja loja;
    ProgressBar progressBarLoja;
    byte[] dadosImg;
    List<byte[]> listaFotoLogo = new ArrayList<>(1);

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    List<Consulta> listConsulta = new ArrayList<>();

    ActivityCadastrarLojasBinding binding;
    final String TYPE = "LOJA";

    ViewModelRequestCNPJ viewModelCNPJ;
    ViewModelFirebase viewModelFirebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cadastrar_lojas);
        binding = ActivityCadastrarLojasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // iniciar ViewModel e buscar dados para consultas
        getDadosConsultas();

        // validar permissions
        Permissions.validatePermissions(permissions, this, 1);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastrar loja");

        startComponents();
    }

    private void startComponents() {
        btnCadastrarLoja = binding.buttonCadastrarLoja;//findViewById(R.id.buttonCadastrarLoja);
        editCpfOrCnpj = binding.editTextCpfLoja;//findViewById(R.id.editTextCpfLoja);
        editCpfOrCnpj.addTextChangedListener(MaskEditCpfCNPJ.insert(editCpfOrCnpj));

        editNomeLoja = binding.editTextNomeLoja;//findViewById(R.id.editTextNomeLoja);
        editNomeUser = binding.editTextNomeUserLoja;//findViewById(R.id.editTextNomeUserLoja);
        editEmail = binding.editTextEmailLoja;//findViewById(R.id.editTextEmailLoja);
        editSenha = binding.editTextPasswordLoja;//findViewById(R.id.editTextPasswordLoja);

        editNumero = binding.editTextTelefoneLoja;//findViewById(R.id.editTextTelefoneLoja);
        editNumero.addTextChangedListener(MaskEditUtil.mask(editNumero,MaskEditUtil.FORMAT_FONE));

        progressBarLoja = binding.progressBarLoja;//findViewById(R.id.progressBarLoja);

        imageLoja = binding.imageLoja;//findViewById(R.id.imageLoja);

        // referencia da loja
        loja = new Loja();

        // intancia do View Model
        viewModelCNPJ = new ViewModelProvider(this).get(ViewModelRequestCNPJ.class);
    }

    // btn Cadastro da loja
    public void btnCadastrarLoja(View view) {
        validarCampos();
    }

    // validar campos
    private void validarCampos() {
        getDadosEditTexts();

        // fazer validação
        if( !nomeLoja.isEmpty() ) {
            if( !nomeUser.trim().isEmpty() ) {
                if( !cpfOuCnpj.isEmpty() && cpfOuCnpj.length() >= 14  ) {
                    if( !telefone.isEmpty() && telefone.length() >= 10 ){
                        if( !emailLoja.isEmpty() && emailLoja.length() >= 10 ) {
                            if( !senhaLoja.isEmpty() && senhaLoja.length() >= 10 ) {
                                if( listaFotoLogo.size() > 0){

                                    // ocultar teclado do sistema
                                    closedKeyBoard();
                                    configBtnAndProgress(false,View.VISIBLE);


                                    //verificar dados na lista de consulta de lojas
                                    if( listConsulta.size() > 0){
                                        verificarDadosListConsulta();
                                    }else{
                                        //Iniciar validação de dados antes do cadastro
                                        validarCPFeCNPJ();
                                    }

                                }else{
                                    exibirToast("Escolha uma imagem como \"logo\" para sua loja");
                                }
                            }else{
                                exibirToast("Para a maior segurança informe uma Senha com no mínimo 10 caracters.");
                            }
                        }else{
                            exibirToast("Informe um E-mail válido para sua loja!");
                        }
                    }else{
                        exibirToast("Informe um telefone válido!");
                    }
                }else{
                    exibirToast("Informe um CPF ou CNPJ válido!");
                }
            }else{
                exibirToast("Informe o seu NOME!");
            }
        }else{
            exibirToast("Informe o nome da sua loja!");
        }
    }

    private void getDadosEditTexts(){
        emailLoja = editEmail.getText().toString();
        senhaLoja = editSenha.getText().toString();
        nomeLoja = editNomeLoja.getText().toString();
        nomeUser = editNomeUser.getText().toString();
        cpfOuCnpj = editCpfOrCnpj.getText().toString();
        telefone = editNumero.getText().toString();
    }

    private void verificarDadosListConsulta(){
        for(Consulta consult : listConsulta){
            if( consult.getNomeLoja().equals(nomeLoja) ){

                configBtnAndProgress(true, View.GONE);
                configEditText(editNomeLoja);
                exibirToast("Esse nome já foi cadastrados no sistema!");

            }else{
                if( consult.getTelefone().equals(telefone) ){

                    configBtnAndProgress(true, View.GONE);
                    configEditText(editNumero);
                    exibirToast("Esse número de telefone já foi cadastrados no sistema!");

                }else{
                    if( consult.getCpfOrCnpj().equals(cpfOuCnpj) ){

                        configBtnAndProgress(true, View.GONE);
                        configEditText(editCpfOrCnpj);

                        if(cpfOuCnpj.length() > 14) { // se > 14 então é CNPJ
                            exibirToast("Esse CNPJ já foi cadastrados no sistema!");
                        }else{
                            exibirToast("Esse CPF já foi cadastrados no sistema!");
                        }
                    }else{
                        //Iniciar validação de dados antes do cadastro
                        validarCPFeCNPJ();
                    }
                }
            }
        }
    }
    private void getDadosConsultas() {

        DatabaseReference database = FirebaseRef.getDatabase();
        DatabaseReference consultasRef = database.child("consultas");
        consultasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snap : snapshot.getChildren()){
                    Consulta consulta = snap.getValue(Consulta.class);

                    //add dados de lista
                    listConsulta.add(consulta);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validarCPFeCNPJ(){
        if(cpfOuCnpj.length() > 14) { // se > 14 então é CNPJ

            // definir dados da loja e ecadastrar
            loja.setNomeLoja(nomeLoja);
            loja.setNomeUser(nomeUser);
            loja.setTelefone(telefone);
            loja.setEmail(emailLoja);
            loja.setSenha(senhaLoja);

            // requisição de dados do CNPJ
            /*new VerificarCNPJ( cpfOuCnpj , new ResultCnpj() {
                @Override
                public void onRequestDadosCnpj(ModelCnpj modelCnpj) {
                    loja.setCpfOrCnpj(cpfOuCnpj);
                    loja.setModelCnpj(modelCnpj);
                    cadastrarLoja();
                }

                @Override
                public void onErroRequestCnpj(String erro) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() ->
                        erroRequestCnpj(erro)
                    );
                }
            });*/

            //observe dos dados do cnpj
            viewModelCNPJ.getLiveDataRequestDadosCnpj().observe(this,
                modelCnpj -> {
                    loja.setCpfOrCnpj(cpfOuCnpj);
                    loja.setModelCnpj(modelCnpj);
                    //cadastrarLoja();
                    firebaseCadastrarLoja();
                });

            //observe de erros
            viewModelCNPJ.getLiveDataErroRequestCnpj().observe(this,
                    this::erroRequestCnpj
                );

            // obter dados
            viewModelCNPJ.getDadosCNPJ(cpfOuCnpj);


        }else{ // se não é CPF

            // verificar CPF Válido
            boolean valido = verificarCPF(cpfOuCnpj);
            if(valido) { // CPF Válido

                // definir dados da loja e ecadastrar
                loja.setNomeLoja(nomeLoja);
                loja.setNomeUser(nomeUser);
                loja.setCpfOrCnpj(cpfOuCnpj);
                loja.setTelefone(telefone);
                loja.setEmail(emailLoja);
                loja.setSenha(senhaLoja);
                //cadastrarLoja();
                firebaseCadastrarLoja();

            }else{ // CPF Inválido

                configBtnAndProgress(true, View.GONE);
                exibirToast("CPF Inválido");
                configEditText(editCpfOrCnpj);
            }
        }
    }
    private boolean verificarCPF(String cpf) {
        CPFValidator cpfValidator = new CPFValidator();
        List<ValidationMessage> erros = cpfValidator.invalidMessagesFor(cpf);
        if(erros.size() > 0){
            System.out.println(erros);
            return false;
        }
        return true;
    }

    // inicia o ViewModelCNPJ
    private void erroRequestCnpj(@NonNull String erro){

        switch (erro){
            case "ERRO 1":
                exibirToast("Houve uma falha na comunicação ao tentar verificar o CNPJ, tente mais tarde!");
                configEditText(editCpfOrCnpj);
                configBtnAndProgress(true, View.GONE);
                break;

            case "ERRO 2":
                exibirToast("CNPJ Inválido");
                configEditText(editCpfOrCnpj);
                configBtnAndProgress(true, View.GONE);
                break;

            case "ERRO 3":
                configBtnAndProgress(true, View.GONE);
                break;
        }
    }

    // salvar no firebase
    private void firebaseCadastrarLoja(){
        //instance ViewModelFirebase
        viewModelFirebase = new ViewModelProvider(this).get(ViewModelFirebase.class);

        // observe msg Toast email de comfirm
        viewModelFirebase.getLiveDataShowToast().observe(this,
                this::exibirToast
        );

        // observe cadastro
        viewModelFirebase.getResultCadastro().observe(this,
            bol -> {
                if(bol){
                    startActivity(new Intent( this, ActivityMainLoja.class));
                }
            }
        );

        // observe Erro de cadastro
        viewModelFirebase.getErroCadastro().observe(this,
            erro -> {
                exibirToast("ERRO: "+erro);
                configBtnAndProgress(true,View.GONE);
            }
        );

        // Cadastrar LOJA
        CadastrarUsers cadastrarUsers = new CadastrarUsers(viewModelFirebase, dadosImg, TYPE);
        cadastrarUsers.cadastrarUser(emailLoja, senhaLoja );
    }

    private void exibirToast(@NonNull String msg) {
        Toast.makeText(CadastrarLojasActivity.this, msg, Toast.LENGTH_LONG).show();
    }
    public void launchGallery(View view){
        Gallery.open(this, 0);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void recuperaImagemDaGaleria(Intent data ){
        try{
            if(data != null) {
                // recuperar img selecionada da galeria
                Uri imgSelected = data.getData();

                // transformar em bitMap
                Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelected);

                // cortar imagem
                //Bitmap imgBitmapCortada = Bitmap.createScaledBitmap(imgBitmap, 760, 760, true);

                // rotacionar a img que foi cortada
                Bitmap imgBitmapRotate = RotacionarImgs.rotacionarIMG(imgBitmap, imgSelected, this);
                if(imgBitmapRotate != null){
                    //reuperar dados da img para o firebase
                    imageLoja.setImageBitmap(imgBitmapRotate);
                    dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapRotate);

                    listaFotoLogo.add(dadosImg);

                }else{
                    //reuperar dados da img para o firebase
                    imageLoja.setImageBitmap(imgBitmap);
                    dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmap);

                    listaFotoLogo.add(dadosImg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK ){
            recuperaImagemDaGaleria( data );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for( int permissionResult : grantResults ){
            if ( permissionResult == PackageManager.PERMISSION_DENIED){
                alertPermission();
            }
        }
    }
    private void alertPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas!");
        builder.setMessage("Para postar imagens voçê precisa permitir o acesso a galeria.");
        builder.setCancelable(false);
        builder.setPositiveButton("Comfirmar", (dialog, which)-> finish());
        AlertDialog alert = builder.create();
        alert.show();
    }

    // methodo que oculta o teclado do sistema
    private void closedKeyBoard(){
        View view = getWindow().getCurrentFocus();
        if(view != null){
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(),0);
        }
    }

    private void configBtnAndProgress(boolean bol, int param){
        btnCadastrarLoja.setEnabled(bol);
        progressBarLoja.setVisibility(param);
    }

    private void configEditText(@NonNull EditText editText ){
        editText.setTextColor(Color.RED);
        editText.requestFocus();
    }
}
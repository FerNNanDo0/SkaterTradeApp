package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.droid.app.skaterTrader.helper.MaskaraEditTextCpfCNPJ;
import com.droid.app.skaterTrader.helper.Permissions;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.ModelCnpj;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CadastrarLojasActivity extends AppCompatActivity {

    EditText editNomeLoja, editNomeUser, editCpfOrCnpj, editEmail, editSenha, editNumero;
    Button btnCadastrarLoja;
    CircleImageView imageLoja;
    //ViewModelCNPJ viewModelCNPJ;
    Request request;
    OkHttpClient client;

    String emailLoja, senhaLoja;
    Loja loja;
    ProgressBar progressBarLoja;
    byte[] dadosImg;
    ModelCnpj cnpjModel;
    List<byte[]> listaFotoLogo = new ArrayList<>(1);

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_lojas);

        // validar permissions
        Permissions.validatePermissions(permissions, this, 1);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastrar loja");

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        btnCadastrarLoja = findViewById(R.id.buttonCadastrarLoja);
        editCpfOrCnpj = findViewById(R.id.editTextCpfLoja);
        editCpfOrCnpj.addTextChangedListener(MaskaraEditTextCpfCNPJ.insert(editCpfOrCnpj));

        editNomeLoja = findViewById(R.id.editTextNomeLoja);
        editNomeUser = findViewById(R.id.editTextNomeUserLoja);
        editEmail = findViewById(R.id.editTextEmailLoja);
        editSenha = findViewById(R.id.editTextPasswordLoja);

        editNumero = findViewById(R.id.editTextTelefoneLoja);
        editNumero.addTextChangedListener(MaskEditUtil.mask(editNumero,MaskEditUtil.FORMAT_FONE));

        progressBarLoja = findViewById(R.id.progressBarLoja);

        imageLoja = findViewById(R.id.imageLoja);

        // referencia da loja
        loja = new Loja();

        // iniciar client
        client = new OkHttpClient().newBuilder().build();
    }

    // btn Cadastro da loja
    public void btnCadastrarLoja(View view) {
        validarCampos();
    }

    // validar campos
    private void validarCampos() {
        emailLoja = editEmail.getText().toString();
        senhaLoja = editSenha.getText().toString();
        String nomeLoja = editNomeLoja.getText().toString();
        String nomeUser = editNomeUser.getText().toString();
        String cpfOuCnpj = editCpfOrCnpj.getText().toString();
        String telefone = editNumero.getText().toString();

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
                                    btnCadastrarLoja.setEnabled(false);

                                    if(cpfOuCnpj.length() > 14) { // se > 14 então é CNPJ

                                        progressBarLoja.setVisibility( View.VISIBLE );

                                        // definir dados da loja e ecadastrar
                                        loja.setNomeLoja(nomeLoja);
                                        loja.setNomeUser(nomeUser);
                                        loja.setTelefone(telefone);
                                        loja.setEmail(emailLoja);
                                        loja.setSenha(senhaLoja);
                                        validateCNPJ(cpfOuCnpj);

                                    }else{ // se não é CPF

                                        // verificar CPF Válido
                                        boolean valido = verificarCPF(cpfOuCnpj);
                                        if(valido) { // CPF Válido

                                            progressBarLoja.setVisibility( View.VISIBLE );

                                            // definir dados da loja e ecadastrar
                                            loja.setNomeLoja(nomeLoja);
                                            loja.setNomeUser(nomeUser);
                                            loja.setCpfOrCnpj(cpfOuCnpj);
                                            loja.setTelefone(telefone);
                                            loja.setEmail(emailLoja);
                                            loja.setSenha(senhaLoja);
                                            cadastrarLoja();

                                        }else{ // CPF Inválido

                                            btnCadastrarLoja.setEnabled(true);
                                            exibirToast("CPF Inválido");
                                            editCpfOrCnpj.setTextColor(Color.RED);
                                            editCpfOrCnpj.requestFocus();
                                        }
                                    }

                                }else{
                                    exibirToast("Escolha uma imagem como \"logo\" para sua loja");
                                }
                            }else{
                                exibirToast(
                                        "Para a maior segurança informe uma Senha com no mínimo 10 caracters.");
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

    private boolean verificarCPF(String cpfOuCnpj) {
        CPFValidator cpfValidator = new CPFValidator();
        List<ValidationMessage> erros = cpfValidator.invalidMessagesFor(cpfOuCnpj);
        if(erros.size() > 0){
            System.out.println(erros);
            return false;
        }
        return true;
    }

    private void validateCNPJ(String cpfOuCnpj) {
        //remover caracters especiais
        String newCpfOuCnpj = removerCharacters(cpfOuCnpj);
        String TOKEN = "f7fc0266-e337-4158-b957-4f01b9e8bfbc-b6aa315b-2629-41c2-9f00-ce227deb61a3";

        String url = "https://api.cnpja.com/office/"+newCpfOuCnpj+"?simples=true&simplesHistory=true";
        request = new Request.Builder()
                .url(url).method("GET", null)
                .addHeader("Authorization", TOKEN).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                exibirToast("Houve uma falha na comunicação ao tentar verificar o CNPJ, tente mais tarde!");
                editCpfOrCnpj.requestFocus();
                editCpfOrCnpj.setTextColor(Color.RED);
                btnCadastrarLoja.setEnabled(true);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                if(response.body() != null){
                    String body = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(body);

                        // verificar se CNPJ e valido e se ha codigo de erro
                        if(jsonObject.has("code")){

                            System.out.println(">> "+jsonObject.getString("code"));
                            exibirToast("CNPJ Inválido");
                            editCpfOrCnpj.requestFocus();
                            editCpfOrCnpj.setTextColor(Color.RED);
                            btnCadastrarLoja.setEnabled(true);
                        }else{
                            System.out.println(">> "+jsonObject);
                            setDadosConsultaCNPJ(jsonObject,cpfOuCnpj);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    exibirToast("CNPJ Inválido");
                    editCpfOrCnpj.requestFocus();
                    editCpfOrCnpj.setTextColor(Color.RED);
                }
            }
        });
    }
    private void setDadosConsultaCNPJ(@NonNull JSONObject jsonObject, String cnpj) {
        cnpjModel = new ModelCnpj();
        try {

            // recupera o endereço
            String address = jsonObject.getString("address");
            JSONObject jsonAdress = new JSONObject(address);
            String cidade = jsonAdress.getString("city");
            String estado = jsonAdress.getString("state");
            String rua = jsonAdress.getString("street");
            String numero = jsonAdress.getString("number");
            String cep = jsonAdress.getString("zip");

            // recupera o pais
            String country = jsonAdress.getString("country");
            JSONObject jsonCoutry = new JSONObject(country);
            String pais = jsonCoutry.getString("name");

            /// recupera info da empresa
            String company = jsonObject.getString("company");
            JSONObject jsonCompany = new JSONObject(company);
            String companyBody = jsonCompany.getString("name");

            // definir dados do cnpj
            cnpjModel.setCidade(cidade);
            cnpjModel.setEstado(estado);
            cnpjModel.setRua(rua);
            cnpjModel.setNumero(numero);
            cnpjModel.setCep(cep);
            cnpjModel.setPais(pais);
            cnpjModel.setCompanyBody(companyBody);
            loja.setCpfOrCnpj(cnpj);
            loja.setModelCnpj(cnpjModel);
            cadastrarLoja();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    // salvar no firebase
    public void cadastrarLoja() {

        //cadastrar no Firebase
        FirebaseRef.getAuth().createUserWithEmailAndPassword(emailLoja, senhaLoja)
                .addOnCompleteListener(task -> {
                    if( task.isSuccessful() ){

                        loja.salvarImgLogoLoja(dadosImg, this);
                        loja.atulizarTipoDeUser();

                        // enviar email de comfirmação
                        if( FirebaseRef.getAuth().getCurrentUser() != null){
                            FirebaseRef.getAuth().getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            Toast.makeText(this,
                                                    "Um email de confirmação foi enviado para seu email.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        loja.salvarDados();

                        finish();
                        startActivity(new Intent(this, ActivityMainLoja.class));

                    }else{
                        progressBarLoja.setVisibility( View.GONE );
                        btnCadastrarLoja.setEnabled(true);

                        String execao;
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
                        Toast.makeText(this, "Erro: "+execao,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NonNull
    private String removerCharacters(@NonNull String str) {
        String newStr = str.replace(".","");
        newStr = newStr.replace("/", "");
        newStr = newStr.replace("-", "");
        return newStr;
    }
    private void exibirToast(@NonNull String msg) {
        Toast.makeText(CadastrarLojasActivity.this, msg, Toast.LENGTH_LONG).show();
    }
    public void launchGallery(View view){
        try{
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            startActivityIfNeeded(i, 1);
            // startActivityForResult(i, requestCode);
        }catch (Exception e){
            e.printStackTrace();
        }
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
                    dadosImg = recuperarDadosIMG(imgBitmapRotate);

                    listaFotoLogo.add(dadosImg);

                }else{
                    //reuperar dados da img para o firebase
                    imageLoja.setImageBitmap(imgBitmap);
                    dadosImg = recuperarDadosIMG(imgBitmap);

                    listaFotoLogo.add(dadosImg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //reuperar dados da img para o firebase
    @NonNull
    private byte[] recuperarDadosIMG(@NonNull Bitmap imgBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
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
}
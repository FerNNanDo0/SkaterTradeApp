package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.EditText;
import android.widget.Toast;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.api.CEPService;
import com.droid.app.skaterTrader.api.CNPJService;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.droid.app.skaterTrader.helper.MaskaraEditTextCpfCNPJ;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.ModelCep;
import com.droid.app.skaterTrader.model.cnpj.ModelCnpj;
import com.droid.app.skaterTrader.viewModel.ViewModelCNPJ;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastrarLojasActivity extends AppCompatActivity {

    EditText editNomeLoja, editNomeUser, editCpfOrCnpj, editCep, editEmail, editSenha;
    CircleImageView imageLoja;
    ViewModelCNPJ viewModelCNPJ;
    Request request;
    OkHttpClient client;
    Retrofit retrofit;
    ModelCep _cep;
    Loja loja;
    byte[] dadosImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_lojas);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastrar loja");

        iniciarComponentes();

        viewModelCNPJ = new ViewModelProvider(this).get(ViewModelCNPJ.class);
        viewModelCNPJ.getLiveData().observe(this, txt -> {
            String[] listStr = txt.split("\n");
            System.out.println(Arrays.toString(listStr));
        });

    }

    private void iniciarComponentes() {
        editCpfOrCnpj = findViewById(R.id.editTextCpfLoja);
        editCpfOrCnpj.addTextChangedListener(MaskaraEditTextCpfCNPJ.insert(editCpfOrCnpj));

        editCep = findViewById(R.id.editTextCepLoja);
        editCep.addTextChangedListener(MaskEditUtil.mask(editCep,MaskEditUtil.FORMAT_CEP));

        editNomeLoja = findViewById(R.id.editTextNomeLoja);
        editNomeUser = findViewById(R.id.editTextNomeUserLoja);
        editEmail = findViewById(R.id.editTextEmailLoja);
        editSenha = findViewById(R.id.editTextPasswordLoja);

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
        String emailLoja = editEmail.getText().toString();
        String senhaLoja = editSenha.getText().toString();
        String nomeLoja = editNomeLoja.getText().toString();
        String nomeUser = editNomeUser.getText().toString();
        String cpfOuCnpj = editCpfOrCnpj.getText().toString();
        String cep = editCep.getText().toString();

        // fazer validação
        if( !nomeLoja.isEmpty() ) {
            if( !nomeUser.trim().isEmpty() ) {
                if( !cpfOuCnpj.isEmpty() && cpfOuCnpj.length() >= 14  ) {
                    if( !cep.isEmpty() && cep.length() >= 8 ) {
                        if( !emailLoja.isEmpty() && emailLoja.length() >= 10 ) {
                            if( !senhaLoja.isEmpty() && senhaLoja.length() >= 10 ) {
                                // ocultar teclado do sistema
                                closedKeyBoard();

                                if(cpfOuCnpj.length() > 14) { // se > 14 então é CNPJ
                                    // verificar CNPJ Válido
                                    //verificarCNPJ(cpfOuCnpj);
                                    validateCNPJ(cpfOuCnpj);

                                }else{ // se não é CPF

                                    // verificar CPF Válido
                                    boolean valido = verificarCPF(cpfOuCnpj);
                                    if(valido) { // CPF Válido

                                        // validar o cep
                                        validarCep(cep);

                                        // cadastrar Loja
                                        cadastrarLoja(nomeLoja, nomeUser, cpfOuCnpj, emailLoja, senhaLoja);

                                    }else{ // CPF Inválido
                                        exibirToast("CPF Inválido");
                                        editCpfOrCnpj.setTextColor(Color.RED);
                                        editCpfOrCnpj.requestFocus();
                                    }
                                }

                            }else{
                                exibirToast("Informe um Senha válida para sua loja com no mínimo 10 caracters.");
                            }
                        }else{
                            exibirToast("Informe um E-mail válido para sua loja!");
                        }
                    }else{
                        exibirToast("Informe um CEP válido");
                    }
                }else{
                    exibirToast("Informe um CPF ou CNPJ válido");
                }
            }else{
                exibirToast("Informe o seu NOME!");
            }
        }else{
            exibirToast("Informe o nome da sua loja!");
        }
    }
    private void validarCep(String Cep) {
        String urlBase = "https://viacep.com.br/ws/";
        retrofit = new Retrofit.Builder().baseUrl( urlBase )
                .addConverterFactory( GsonConverterFactory.create() ).build();

        CEPService cepService = retrofit.create( CEPService.class );
        retrofit2.Call<ModelCep> call = cepService.getService( Cep );

        call.enqueue(new retrofit2.Callback<ModelCep>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ModelCep> call, @NonNull retrofit2.Response<ModelCep> response) {
                if( response.isSuccessful() ){
                    _cep = response.body();
                    assert _cep != null;
                    if(_cep.getCep() != null){
                        // definir cep para loja
                        loja.setModelCep(_cep);

                        System.out.println("Cep "+_cep.getCep()+"\nBairro "+_cep.getBairro());
                    }else{
                        exibirToast("CEP Inválido");
                        editCep.requestFocus();
                        editCep.setTextColor(Color.RED);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<ModelCep> call, @NonNull Throwable t) {
                exibirToast("CEP Inválido");
                editCep.requestFocus();
                editCep.setTextColor(Color.RED);
            }
        });
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


    }

    private void verificarCNPJ(String cpfOuCnpj) {
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
                        }else{
                            System.out.println(">> "+jsonObject);
                            setDadosConsultaCNPJ(jsonObject);
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
    private void setDadosConsultaCNPJ(@NonNull JSONObject jsonObject) {
        try {

            // recupera o endereço
            String address = jsonObject.getString("address");
            JSONObject jsonAdress = new JSONObject(address);
            String cidade = jsonAdress.getString("city");
            String estado = jsonAdress.getString("state");
            String rua = jsonAdress.getString("street");
            String numero = jsonAdress.getString("number");
            String cep = jsonAdress.getString("zip");
            if(cep != null && !cep.isEmpty()){
                editCep.setText(cep);
                System.out.println(">>new cep "+cep);
            }


            // recupera o pais
            String country = jsonAdress.getString("country");
            JSONObject jsonCoutry = new JSONObject(country);
            String pais = jsonCoutry.getString("name");

            // telefone e email
            /*String fone = jsonObject.getString("phones");
            JSONObject telefone = new JSONObject(fone);
            String email = jsonObject.getString("emails");
            JSONObject emails = new JSONObject(email);

            System.out.println(">> "+telefone);
            System.out.println(">> "+emails);*/


            /// recupera info da empresa
            String company = jsonObject.getString("company");
            JSONObject jsonCompany = new JSONObject(company);
            String companyBody = jsonCompany.getString("name");

            String dadosEndereco = String
                    .format("Pais: %s\nCidade: %s\nEstado: %s\nRua: %s\nNúmero: %s\n%s",
                            pais, cidade, estado, rua, numero, companyBody
                    );

            viewModelCNPJ.setLiveData(dadosEndereco);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void cadastrarLoja(String nomeLoja, String nomeUser,
                               String cpfOuCnpj, String emailLoja, String senhaLoja) {
        // definir dados da loja
        loja.setNomeLoja(nomeLoja);
        loja.setNomeUser(nomeUser);
        loja.setCpfOrCnpj(cpfOuCnpj);
        loja.setEmail(emailLoja);


    }
    @NonNull
    private String removerCharacters(@NonNull String str) {
        String newStr = str.replace(".","");
        newStr = newStr.replace("/", "");
        newStr = newStr.replace("-", "");
        return newStr;
    }
    private void exibirToast(@NonNull String msg) {
        Toast.makeText(CadastrarLojasActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                }else{
                    //reuperar dados da img para o firebase
                    imageLoja.setImageBitmap(imgBitmap);
                    dadosImg = recuperarDadosIMG(imgBitmap);
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
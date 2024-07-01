package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.databinding.ActivityConfigDadosLojaBinding;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.droid.app.skaterTrader.helper.MaskEditCpfCNPJ;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.viewModel.ViewModelConfigDadosLoja;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigDadosLojaActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher {

    ActivityConfigDadosLojaBinding binding;
    EditText nomeLoja, nomeUser, telefone, cpfOrCnpj, email;
    CircleImageView imgLojoLoja;
    Button btnAtualizar;
    ProgressBar progressBar;
    byte[] dadosImg;
    Loja loja;

    String oldNomeL,oldNomeU, oldPhone, nomeL, nomeU, phone;

    ViewModelConfigDadosLoja viewModel;

    @Override
    protected void onStart() {
        super.onStart();
        getDadosDb();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(viewModel != null){
            viewModel.removeEventListener();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_config_dados_loja);

        binding = ActivityConfigDadosLojaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.dados_do_perfil));

        iniciarComponentes();

    }

    private void iniciarComponentes(){
        progressBar = binding.progressBarConfigLoja;

        telefone = binding.editNumero;
        telefone.addTextChangedListener(this);
        telefone.addTextChangedListener(MaskEditUtil.mask(telefone,MaskEditUtil.FORMAT_FONE));

        nomeLoja = binding.editNomeLoja;
        nomeLoja.addTextChangedListener(this);

        nomeUser = binding.editName;
        nomeUser.addTextChangedListener(this);

        cpfOrCnpj = binding.editCpfOrCnpj;
        cpfOrCnpj.setFocusableInTouchMode(false);
        cpfOrCnpj.addTextChangedListener(MaskEditCpfCNPJ.insert(cpfOrCnpj));

        btnAtualizar = binding.btnUpdate;
        btnAtualizar.setOnClickListener(this);
        btnAtualizar.setVisibility(View.GONE);

        imgLojoLoja = binding.imageLogo;
        imgLojoLoja.setOnClickListener(this);

        email = binding.editEmail;
        email.setFocusableInTouchMode(false);

        // init viewModel
        viewModel = new ViewModelProvider(this).get(ViewModelConfigDadosLoja.class);

        loja = new Loja(viewModel);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        int idItem = view.getId();

        switch (idItem){
            case R.id.imageLogo:
                Gallery.open(this, 0);
                break;

            case R.id.btn_update:
                clickBtn();
                break;
        }
    }


    private void clickBtn(){

        getText();

        if( !nomeL.isEmpty()){
            if(!nomeU.isEmpty()){
                if(!phone.isEmpty()){

                    if(!oldNomeL.equals(nomeL)){
                        loja.setNomeLoja(nomeL);

                        progressBar.setVisibility(View.VISIBLE);
                        nomeLoja.setCursorVisible(false);
                    }
                    if(!oldNomeU.equals(nomeU)){
                        loja.setNomeUser(nomeU);

                        progressBar.setVisibility(View.VISIBLE);
                        nomeUser.setCursorVisible(false);
                    }
                    if(!oldPhone.equals(phone)){
                        loja.setTelefone(phone);

                        progressBar.setVisibility(View.VISIBLE);
                        telefone.setCursorVisible(false);
                    }

                    // add observe viewModel aqui
                    viewModel.getBol().observe(this,
                        bol -> {
                            if(bol){
                                progressBar.setVisibility(View.GONE);
                                btnAtualizar.setVisibility(View.GONE);
                            }
                        }
                    );

                    // chama method atulizar dados db
                    viewModel.atualizarDadosDB(loja);

                }else{
                    sendMsgToast(getString(R.string.informe_o_n_mero_de_celular_ou_whatsapp_da_loja));
                }
            }else{
                sendMsgToast(getString(R.string.informe_o_nome_do_responsavel_pela_loja));
            }
        }else{
            sendMsgToast(getString(R.string.informe_o_nome_da_loja));
        }

    }

    private void sendMsgToast(String txt){
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // set img
        if( requestCode == 0 && resultCode == RESULT_OK ){
            if(data != null) {
                // recupera img selecionada da galeria
                Uri imgSelected = data.getData();
                imgLojoLoja.setImageURI(imgSelected);
                definirImagemPerfi(imgSelected);
            }
        }

    }
    private void definirImagemPerfi(@Nullable Uri imgSelected){
        try {
            // transformar em bitMap
            Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelected);

            // cortar imagem
            //Bitmap imgBitmapCortada = Bitmap.createScaledBitmap(imgBitmap, 720, 720, true);

            // rotacionar a img que foi cortada
            Bitmap imgBitmapRotate = RotacionarImgs.rotacionarIMG(imgBitmap, imgSelected, this);
            if(imgBitmapRotate != null){
                //reuperar dados da img para o firebase
                dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapRotate);
                imgLojoLoja.setImageBitmap(imgBitmapRotate);

            }else{
                //reuperar dados da img para o firebase
                dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmap);
                imgLojoLoja.setImageBitmap(imgBitmap);
            }

            // salvar img do usuario
            viewModel.salvarImgLogoLoja(dadosImg);

            Glide.with(this).load(imgSelected).into(imgLojoLoja);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //add viewmodel
    public void getDadosDb() {

        // Observe viewModel
        viewModel.getDadosLoja().observe(this,
            loja -> {
                progressBar.setVisibility(View.GONE);
                if(loja != null) {

                    Glide.with(getApplicationContext())
                            .load(User.user().getPhotoUrl()).into(imgLojoLoja);

                    nomeLoja.setText(loja.getNomeLoja());
                    nomeUser.setText(loja.getNomeUser());
                    telefone.setText(loja.getTelefone());
                    cpfOrCnpj.setText(loja.getCpfOrCnpj());
                    email.setText(loja.getEmail());

                    // get old dados
                    oldNomeL = nomeLoja.getText().toString();
                    oldNomeU = nomeUser.getText().toString();
                    oldPhone = telefone.getText().toString();
                }
            }
        );

        // Observe Error
        viewModel.getError().observe(this,
            error -> progressBar.setVisibility(View.GONE)
        );


        //get Dados db
        viewModel.getDadosDB(progressBar);
    }

    private void getText(){
        nomeL = nomeLoja.getText().toString();
        nomeU = nomeUser.getText().toString();
        phone = telefone.getText().toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(i > 0){
            btnAtualizar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {}
}
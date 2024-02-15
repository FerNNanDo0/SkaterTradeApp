package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.databinding.ActivityConfigDadosLojaBinding;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.droid.app.skaterTrader.helper.MaskaraEditTextCpfCNPJ;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    Loja loja, loja_db;
    ValueEventListener eventListener;
    DatabaseReference lojaRef, database;

    String oldNomeL,oldNomeU, oldPhone, nomeL, nomeU, phone;

    @Override
    protected void onStart() {
        super.onStart();
        getDadosDb();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        lojaRef.removeEventListener(eventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_config_dados_loja);

        binding = ActivityConfigDadosLojaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configurações de perfil");

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
        cpfOrCnpj.addTextChangedListener(MaskaraEditTextCpfCNPJ.insert(cpfOrCnpj));

        btnAtualizar = binding.btnUpdate;
        btnAtualizar.setOnClickListener(this);
        btnAtualizar.setVisibility(View.GONE);

        imgLojoLoja = binding.imageLogo;
        imgLojoLoja.setOnClickListener(this);

        email = binding.editEmail;
        email.setFocusableInTouchMode(false);

        loja = new Loja();
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

                    loja.atualizarDadosDB(progressBar, btnAtualizar);

                }else{
                    sendMsgToast("Informe o Número de celular ou whatsApp da loja");
                }
            }else{
                sendMsgToast("Informe o nome do responsavel pela loja");
            }
        }else{
            sendMsgToast("Informe o nome da loja");
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

            }else{
                //reuperar dados da img para o firebase
                dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmap);
            }

            // salvar img do usuario
            Loja loja = new Loja();
            loja.salvarImgLogoLoja(dadosImg);
            Glide.with(this).load(imgSelected).into(imgLojoLoja);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getDadosDb() {
        //obter dados no Firebase
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(loja.getIdLoja());
        eventListener = lojaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loja_db = snapshot.getValue(Loja.class);

                    if(loja_db != null) {

                        Glide.with(getApplicationContext())
                                .load(User.user().getPhotoUrl()).into(imgLojoLoja);

                        nomeLoja.setText(loja_db.getNomeLoja());
                        nomeUser.setText(loja_db.getNomeUser());
                        telefone.setText(loja_db.getTelefone());
                        cpfOrCnpj.setText(loja_db.getCpfOrCnpj());
                        email.setText(loja_db.getEmail());

                        // get old dados
                        oldNomeL = nomeLoja.getText().toString();
                        oldNomeU = nomeUser.getText().toString();
                        oldPhone = telefone.getText().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
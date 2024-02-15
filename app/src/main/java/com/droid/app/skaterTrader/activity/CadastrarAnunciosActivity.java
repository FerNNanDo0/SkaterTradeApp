package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.droid.app.skaterTrader.databinding.ActivityCadastrarAnunciosBinding;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import me.abhinay.input.CurrencyEditText;
public class CadastrarAnunciosActivity extends AppCompatActivity
        implements View.OnClickListener {
    CurrencyEditText editValor;
    EditText editNumero;
    EditText editTitulo,editDescricao;
    Spinner spinnerEstado,spinnerCategoria;
    Button btnSalvar;
    Bitmap imagemComplet;
    AlertDialog alertDialog;
    StorageReference storage;
    ImageView imageView1, imageView2, imageView3;

    ActivityCadastrarAnunciosBinding binding;

//    ActivityResultLauncher<Intent> galeria_StartActivityForResult;

    byte[] dadosImg;
    List<byte[]> listaFotosRecuperadas = new ArrayList<>(3);
    List<String> listUrlFotos = new ArrayList<>(3);
    List<String> listEstados = new ArrayList<>();
    List<String> listCategorias = new ArrayList<>();
    Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cadastrar_anuncios);

        binding = ActivityCadastrarAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastrar anúncio");

        // iniciar atributos e configurações
        configIniciais();
    }
    private void configIniciais(){
     // config editTexts
        editNumero = binding.editTextNumeroFone;//findViewById(R.id.editTextNumeroFone);
        editNumero.addTextChangedListener(MaskEditUtil.mask(editNumero,MaskEditUtil.FORMAT_FONE));
        editValor = binding.editTextValor;//findViewById(R.id.editTextValor);
        editValor.setCurrency("$");
        editValor.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        editValor.setSeparator(".");

        editTitulo = binding.editTextTitulo;//findViewById(R.id.editTextTitulo);
        editTitulo.setFocusable(true);
        editTitulo.requestFocus();
        editDescricao = binding.editTextDescricao;//findViewById(R.id.editTextDescricao);
     // config imageViews
        imageView1 = binding.imageView1;//findViewById(R.id.imageView1);
        imageView1.setOnClickListener(this);
        imageView2 = binding.imageView2;//findViewById(R.id.imageView2);
        imageView2.setOnClickListener(this);
        imageView3 = binding.imageView3;//findViewById(R.id.imageView3);
        imageView3.setOnClickListener(this);

        imageView2.setClickable(false);
        imageView3.setClickable(false);

     // config button
        btnSalvar = binding.buttonSalvarAnuncio;//findViewById(R.id.buttonSalvarAnuncio);
        btnSalvar.setOnClickListener(this);
     // config spinners
        spinnerEstado = binding.spinnerLeft;//findViewById(R.id.spinnerLeft);
        spinnerEstado.getBackground()
                .setColorFilter(Color.parseColor("#3A8C0E"), PorterDuff.Mode.SRC_ATOP);

        spinnerCategoria = binding.spinnerRight;//findViewById(R.id.spinnerRight);
        spinnerCategoria.getBackground()
                .setColorFilter(Color.parseColor("#3A8C0E"), PorterDuff.Mode.SRC_ATOP);

        dadosSpinner();

     // iniciar referencias do firebase
        storage = FirebaseRef.getStorage();
    }
    private void dadosSpinner(){
        String[] estados = getResources().getStringArray(R.array.estados);
        listEstados.add(0, "Estados");
        listEstados.addAll(Arrays.asList(estados));

        String[] categorias = getResources().getStringArray(R.array.categorias);
        listCategorias.add(0, "Categorias");
        listCategorias.addAll(Arrays.asList(categorias));


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,listEstados
        );
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,listCategorias
        );
        adapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        adapter2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        spinnerEstado.setAdapter(adapter1);
        spinnerCategoria.setAdapter(adapter2);
    }
    private boolean
    verificarCampos(@NonNull String titulo, String valor, String phone, String desc, String estado, String categoria){
        boolean verif = false;
        valor = valor.replace("$","");
        if( !titulo.isEmpty()){
            if( !valor.isEmpty() && !valor.equals("0")){
                if(phone.length() == 14){
                    if( !desc.isEmpty()){
                        if( !estado.equals("Estados")){
                            if( !categoria.equals("Categorias")){
                                if (listaFotosRecuperadas.size() != 0){
                                    closedKeyBoard();
                                    verif = true;

                                }else{
                                    exibirMsgErro("Escolha ao menos uma imagem");
                                }
                            }else{
                                exibirMsgErro("Escolha uma categoria");
                            }
                        }else{
                            exibirMsgErro("Escolha um estado");
                        }
                    }else{
                        exibirMsgErro("Preencha uma descrição");
                    }
                }else{
                    exibirMsgErro("Preencha um número de celular válido");
                }
            }else{
                exibirMsgErro("Preencha um valor R$");
            }
        }else{
            exibirMsgErro("Preencha um título");
        }
        return verif;
    }
    // Menssagens de erro toast
    private void exibirMsgErro(String menssagem){
        Toast.makeText(this,
                menssagem, Toast.LENGTH_SHORT).show();
    }
    // clicks das imagens e do button
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()){
//            click image1
            case R.id.imageView1:
                launchGallery(1);
                break;

//            click image2
            case R.id.imageView2:
                launchGallery(2);
                break;

//            click image3
            case R.id.imageView3:
                launchGallery(3);
                break;

//            click button salvar anuncios
            case R.id.buttonSalvarAnuncio:
                String titulo = editTitulo.getText().toString();
                String valor = Objects.requireNonNull(editValor.getText()).toString();
                String phone = editNumero.getText().toString();
                String desc = editDescricao.getText().toString();
                String estado = spinnerEstado.getSelectedItem().toString();
                String categoria = spinnerCategoria.getSelectedItem().toString();

                boolean verif = verificarCampos(titulo,valor,phone,desc,estado,categoria);
                // System.out.println("Verificado? => "+verif);
                if(verif){
                    //salvar no banco de dados
                    valor = "R"+valor;
                    editValor.setText(valor);
                    configAnuncio(titulo,valor,phone,desc,estado,categoria);
                }
                break;
        }
    }
    // configurar anuncio
    public void configAnuncio(
            String titulo, String valor, String phone, String desc, String estado, String categoria
    ) {
        anuncio = new Anuncio();
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setPhone(phone);
        anuncio.setDesc(desc);
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);

        int totalFotos = listaFotosRecuperadas.size();

        alertDialog();

        for (int i = 0; i < totalFotos; i++) {
//            String urlImg = listaFotosRecuperadas.get(i);
            byte[] urlImg = listaFotosRecuperadas.get(i);
            salvarImgsAnuncio(anuncio, urlImg, totalFotos, i);
        }
    }
    // salvar imagens no storage
    public void salvarImgsAnuncio(@NonNull Anuncio anuncio, byte[] urlImg, int totalFotos, int index) {
        StorageReference imgAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + index);

        // fazer upload da imagem
        UploadTask uploadTask = imgAnuncio.putBytes(urlImg);
        //UploadTask uploadTask = imgAnuncio.putFile(Uri.parse(urlImg));

        uploadTask.addOnSuccessListener( taskSnapshot -> {
                    // Faz download dos Urls das fotos
                    imgAnuncio.getDownloadUrl().addOnCompleteListener( task -> {

                        System.out.println("Link foto " + task.getResult().toString());

                        String urlImgStorage = task.getResult().toString();
                        listUrlFotos.add(urlImgStorage);

                        if (totalFotos == listUrlFotos.size()) {
                            anuncio.setFotos(listUrlFotos);
                            anuncio.salvarAnuncioNoDB(this);
                            alertDialog.dismiss();

                            startActivity( new Intent(getApplicationContext(), ActivityMain.class));
                            finish();
                        }

//                Toast.makeText(context, "Sucesso ao fazer download!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(erroUpload -> exibirMsgErro("Falha ao fazer upload!"));
    }
    public void alertDialog() {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio...")
                .setCancelable(false)
                .build();
        alertDialog.show();
    }
    private void launchGallery(int requestCode){
        Gallery.open(this, requestCode);
        // startActivityForResult(i, requestCode);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void recuperaImagemDaGaleria(Intent data, int codeImageView){
        try {
            if(data != null){
                // recupera img selecionada da galeria
                Uri imgSelected = data.getData();
                Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelected);

                // cortar imagem
                /*Bitmap imgBitmapEdit = Bitmap.createBitmap(
                        imgBitmap.copy(imgBitmap.getConfig(), false),
                        0, imgBitmap.getHeight()/4, imgBitmap.getWidth(), imgBitmap.getWidth());*/
                Bitmap imgBitmapCortada = Bitmap.createScaledBitmap(imgBitmap,860, 860, true);

                // rotacionar a img que foi cortada
                Bitmap imgBitmapRotate = RotacionarImgs.rotacionarIMG(imgBitmapCortada, imgSelected, this);
                if(imgBitmapRotate != null){
                    //reuperar dados da img para o firebase
                    dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapRotate);

                    imagemComplet = imgBitmapRotate;
                }else{
                    //reuperar dados da img para o firebase
                    dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapCortada);

                    imagemComplet = imgBitmapCortada;
                }

                // add no maximo 3 itens
                if(listaFotosRecuperadas.size() < 3){
                    listaFotosRecuperadas.add( dadosImg );
                }

                // configura img na tela se houver uma img
                // define imagem no ImageView
                if(codeImageView == 1){

                    imageView1.setImageBitmap( imagemComplet );
                    imageView2.setClickable(true);
                    imageView2.setImageDrawable(getDrawable(R.drawable.padrao_ativo_2));
                    listaFotosRecuperadas.set(0, dadosImg);

                } else if (codeImageView == 2) {

                    imageView2.setImageBitmap( imagemComplet );
                    imageView3.setClickable(true);
                    imageView3.setImageDrawable(getDrawable(R.drawable.padrao_ativo_3));
                    listaFotosRecuperadas.set(1, dadosImg);

                } else if (codeImageView == 3) {

                    imageView3.setImageBitmap( imagemComplet );
                    listaFotosRecuperadas.set(2, dadosImg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Erro ao recuperar imagem, tente outra imagem ou tire outra foto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK ){
            recuperaImagemDaGaleria( data, requestCode );
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

    private void closedKeyBoard(){
        View view = getWindow().getCurrentFocus();
        if(view != null){
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(),0);
        }
    }
    // ActivityResul da GALERIA
    /*private void galeria_getStartActivityForResult() {
        galeria_StartActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                recuperaImagemDaGaleria(result.getData());
            }
        });
        galeria_StartActivityForResult.launch( new Intent(
                 Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
         );
    }*/
}
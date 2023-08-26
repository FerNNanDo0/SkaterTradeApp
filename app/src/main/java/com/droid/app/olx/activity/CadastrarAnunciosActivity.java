package com.droid.app.olx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.droid.app.olx.R;
import com.droid.app.olx.firebaseRefs.FirebaseRef;
import com.droid.app.olx.helper.MaskEditUtil;
import com.droid.app.olx.helper.Permissions;
import com.droid.app.olx.model.Anuncio;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import dmax.dialog.SpotsDialog;
import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;

public class CadastrarAnunciosActivity extends AppCompatActivity implements View.OnClickListener {
    CurrencyEditText editValor;
    EditText editNumero;
    EditText editTitulo,editDescricao;
    Spinner spinnerEstado,spinnerCategoria;
    Button btnSalvar;
    AlertDialog alertDialog;
    StorageReference storage;
    ImageView imageView1, imageView2, imageView3;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
//    ActivityResultLauncher<Intent> galeria_StartActivityForResult;
    List<String> listaFotosRecuperadas = new ArrayList<>(3);
    List<String> listUrlFotos = new ArrayList<>(3);
    Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncios);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

// validar permissions
        Permissions.validatePermissions( permissions, this, 1 );
// iniciar atributos e configurações
        configIniciais();
    }
    private void configIniciais(){
     // config editTexts
        editNumero = findViewById(R.id.editTextNumeroFone);
        editNumero.addTextChangedListener(MaskEditUtil.mask(editNumero,MaskEditUtil.FORMAT_FONE));
        editValor = findViewById(R.id.editTextValor);
        editValor.setCurrency("$");
        editValor.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        editValor.setSeparator(".");

        editTitulo = findViewById(R.id.editTextTitulo);
        editDescricao = findViewById(R.id.editTextDescricao);
     // config imageViews
        imageView1 = findViewById(R.id.imageView1);
        imageView1.setOnClickListener(this);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setOnClickListener(this);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(this);
     // config button
        btnSalvar = findViewById(R.id.buttonSalvarAnuncio);
        btnSalvar.setOnClickListener(this);
     // config spinners
        spinnerEstado = findViewById(R.id.spinnerLeft);
        spinnerCategoria = findViewById(R.id.spinnerRight);
        dadosSpinner();

     // iniciar referencias do firebase
        storage = FirebaseRef.getStorage();
    }
    private void dadosSpinner(){

        String[] estados = getResources().getStringArray(R.array.estados);
        String[] categorias = getResources().getStringArray(R.array.categorias);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,estados
        );
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,categorias
        );
        adapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        adapter2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        spinnerEstado.setAdapter(adapter1);
        spinnerCategoria.setAdapter(adapter2);
    }
    private boolean
    verificarCampos(String titulo, String valor, String phone, String desc, String estado, String categoria){
        boolean verif = false;
        valor = valor.replace("$","");
        if( !titulo.isEmpty()){
            if( !valor.isEmpty() && !valor.equals("0")){
                if(phone.length() == 14){
                    if( !desc.isEmpty()){
                        if( !estado.isEmpty()){
                            if( !categoria.isEmpty()){
                                if (listaFotosRecuperadas.size() != 0){
                                    verif = true;
                                    /*System.out.println(
                                       String.format("titulo: %s, valor: %s, tel: %s, desc: %s, estado: %s categ: %s",
                                                    titulo,valor,phone,descricao,estado,categoria));*/
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
    public void onClick(View v) {
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
                String valor = editValor.getText().toString();
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
            String urlImg = listaFotosRecuperadas.get(i);
            salvarImgsAnuncio(anuncio, urlImg, totalFotos, i);
        }
    }
    // salvar imagens no storage
    public void salvarImgsAnuncio(@NonNull Anuncio anuncio, String urlImg, int totalFotos, int index) {
        StorageReference imgAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + index);

        // fazer upload da imagem
        UploadTask uploadTask = imgAnuncio.putFile(Uri.parse(urlImg));
        uploadTask.addOnSuccessListener( taskSnapshot -> {
                    // Faz download dos Urls das fotos
                    imgAnuncio.getDownloadUrl().addOnCompleteListener( task -> {

                        System.out.println("Link foto " + task.getResult().toString());

                        String urlImgStorage = task.getResult().toString();
                        listUrlFotos.add(urlImgStorage);

                        if (totalFotos == listUrlFotos.size()) {
                            anuncio.setFotos(listUrlFotos);
                            anuncio.salvarAnuncioNoDB();
                            alertDialog.dismiss();
                            finish();
                        }

//                Toast.makeText(context, "Sucesso ao fazer download!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(erroUpload -> {
                    exibirMsgErro("Falha ao fazer upload!");
                });
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
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
        startActivityForResult(i, requestCode);
    }
    private void recuperaImagemDaGaleria(Intent data, int codeImageView){
        try {
            assert data != null;
        // recupera img selecionada da galeria
            Uri imgSelected = data.getData();
            Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelected);

        // configura img na tela se houver uma img
            if(listaFotosRecuperadas.size() < 3){
                listaFotosRecuperadas.add( imgSelected.toString() );
            }

            if (imgBitmap != null) {
              // define imagem no ImageView
                if(codeImageView == 1){
                    imageView1.setImageBitmap( imgBitmap );
                    listaFotosRecuperadas.set(0, imgSelected.toString());
                } else if (codeImageView == 2) {
                    imageView2.setImageBitmap( imgBitmap );
                    listaFotosRecuperadas.set(1, imgSelected.toString());
                } else if (codeImageView == 3) {
                    imageView3.setImageBitmap( imgBitmap );
                    listaFotosRecuperadas.set(2,imgSelected.toString());
                }

                //reuperar dados da img para o firebase
                //byte[] dadosImg = recuperarDadosIMG(imgBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //reuperar dados da img para o firebase
    /*
    private byte[] recuperarDadosIMG(Bitmap imgBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }
    */
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
        builder.setPositiveButton("Comfirmar", (dialog, which)-> {
            finish();
        });
        AlertDialog alert = builder.create();
        alert.show();
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
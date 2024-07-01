package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.databinding.ActivityCadastrarAnunciosBinding;
import com.droid.app.skaterTrader.firebase.UserFirebase;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.FecharTecladoSys;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.Permissions;
import com.droid.app.skaterTrader.helper.RequestsPermission;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.helper.MaskEditUtil;
import com.droid.app.skaterTrader.viewModel.ViewModelCadastroAnuncio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import me.abhinay.input.CurrencyEditText;

@SuppressLint("UseCompatLoadingForDrawables")
public class CadastrarOuEditarAnunciosActivity extends AppCompatActivity
        implements View.OnClickListener {
    CurrencyEditText editValor;
    Anuncio anuncioSelected;
    EditText editNumero, editTitulo, editDescricao, editCidade, editEstado, editDDD;
    String cidade, titulo, valor, phone, DDD ,desc ,estado, categoria;
    Spinner spinnerEstado,spinnerCategoria;
    Button btnSalvar;
    Bitmap imagemComplet;
    AlertDialog alertDialog;
    ImageView imageView1, imageView2, imageView3;
    ActivityCadastrarAnunciosBinding binding;
    ViewModelCadastroAnuncio viewModel;

    List<String> listEstados = new ArrayList<>(32);
    List<String> listCategorias = new ArrayList<>(11);

//    ActivityResultLauncher<Intent> galeria_StartActivityForResult;

    byte[] dadosImg;
    List<byte[]> listaFotosRecuperadas = new ArrayList<>(3);
    List<String> listUrlFotos = new ArrayList<>(3);
    Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cadastrar_anuncios);

        binding = ActivityCadastrarAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //solicitar permissao
        Permissions.validatePermissions(RequestsPermission.permissions, this, 1);

        // iniciar atributos e configurações
        configActionBar();
        configIniciais();

        // iniciar viewModel
        viewModel = new ViewModelProvider(this).get(ViewModelCadastroAnuncio.class);
        viewModelObserve();

        // Recuperar anúncio para editar e atualizar
        configEditarAnuncio();

        configDadosSpinner();
    }

    private void alertExcluir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.excluir_an_ncio));
        builder.setMessage(getString(R.string.deseja_excluir_esse_an_ncio));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.sim), (dialog, which) -> {
            anuncioSelected.removerAnuncio();
            finish();
            //adapterAnuncios.notifyDataSetChanged();
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.nao), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(anuncioSelected != null){
            getMenuInflater().inflate(R.menu.menu_excluir_anuncio, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.excluir_anuncio){
            alertExcluir();
        }
        return super.onOptionsItemSelected(item);
    }


    private void configEditarAnuncio(){
        anuncioSelected = getIntent().getParcelableExtra("anuncioSelected");
        if(anuncioSelected != null){

            // mudar nome do button e definir evento de click
            btnSalvar.setText(getString(R.string.btn_atualizar_anuncio));
            // atulizar anuncio
            btnSalvar.setOnClickListener(v -> {
                btnEditarAnuncio();
            });

            // definir dados do anuncio
            editCidade.setText(anuncioSelected.getCidade());
            editTitulo.setText(anuncioSelected.getTitulo());
            editValor.setText(anuncioSelected.getValor());
            editDescricao.setText(anuncioSelected.getDesc());
            editNumero.setText(anuncioSelected.getPhone());

            // observe viewModel
            viewModel.getByteDadosImg().observe(this,
                byteDadosImg -> {
                    if(byteDadosImg != null){
                        listaFotosRecuperadas.add(byteDadosImg);
                    }else{
                        imageView1.setImageDrawable(getDrawable(R.drawable.padrao_ativo_1));
                        imageView2.setImageDrawable(getDrawable(R.drawable.padrao_2));
                        imageView3.setImageDrawable(getDrawable(R.drawable.padrao_3));
                    }
                }
            );

            // definir img anuncio
            int listLen = anuncioSelected.getFotos().size();
            if(listLen > 0){
                // config image 1
                String urlImage1 = anuncioSelected.getFotos().get(0);
                definirImageEditAnuncio(urlImage1, imageView1);

                // config image 2
                String urlImage2 = anuncioSelected.getFotos().get(1);
                definirImageEditAnuncio(urlImage2, imageView2);

                // config image 3
                if(listLen > 2){
                    String urlImage3 = anuncioSelected.getFotos().get(2);
                    definirImageEditAnuncio(urlImage3, imageView3);
                }
            }


        }else{
            btnSalvar.setOnClickListener(this);
        }
    }

    private void definirImageEditAnuncio(String url, ImageView imageView) {
        Glide.with(this).load(url).into(imageView);
        downloadImage(url);
    }

    private void downloadImage(String url) {
        viewModel.downloadImage(url);
    }

    // methodo para editar anuncio
    private void btnEditarAnuncio(){
        getDadosEditText();

        boolean verif = verificarCampos(cidade,titulo,valor,DDD,phone,desc,estado,categoria);
        // System.out.println("Verificado? => "+verif);

        if(verif){
            //configurar anuncio
            if( !valor.contains("R") ){
                valor = "R"+valor;
            }
            editValor.setText(valor);
            configAnuncio("editar");
        }
    }

    private void configActionBar(){
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.cadastrar_anuncio_titleActionBar));
    }

    private void configIniciais(){
     // config editTexts
        editNumero = binding.editTextNumeroFone;//findViewById(R.id.editTextNumeroFone);
        editNumero.addTextChangedListener(MaskEditUtil.mask(editNumero,MaskEditUtil.FORMAT_FONE_OTHER));
        editDDD = binding.editTextNumeroDDD;

        editValor = binding.editTextValor;//findViewById(R.id.editTextValor);
        editValor.setCurrency("$");
        editValor.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        editValor.setSeparator(".");

        editEstado = binding.editTextEstado;

        editCidade = binding.editTextCidade;
        editCidade.setFocusable(true);
        editCidade.requestFocus();

        editTitulo = binding.editTextTitulo;//findViewById(R.id.editTextTitulo);
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

     // config spinners
        spinnerEstado = binding.spinnerLeft;//findViewById(R.id.spinnerLeft);
        spinnerEstado.getBackground()
                .setColorFilter(Color.parseColor("#3A8C0E"), PorterDuff.Mode.SRC_ATOP);

        spinnerCategoria = binding.spinnerRight;//findViewById(R.id.spinnerRight);
        spinnerCategoria.getBackground()
                .setColorFilter(Color.parseColor("#3A8C0E"), PorterDuff.Mode.SRC_ATOP);


        // define o click do spinner Estado
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("TAG", ">> "+position+" >> "+parent.getSelectedItem().toString());
                if(parent.getSelectedItem().toString().equals("Outro") ){
                    editEstado.setVisibility(View.VISIBLE);
                    editEstado.setFocusable(true);
                    editEstado.requestFocus();
                }else{
                    editEstado.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configDadosSpinner(){

        String[] estados = getResources().getStringArray(R.array.estados);
        listEstados.add(0, getString(R.string.estados_info));
        listEstados.addAll(Arrays.asList(estados));

        String[] categorias = getResources().getStringArray(R.array.categorias);
        listCategorias.add(0, getString(R.string.categorias_info));
        listCategorias.addAll(Arrays.asList(categorias));


        // config spinner para editar anuncio
        if(anuncioSelected != null){
            listEstados.remove( anuncioSelected.getEstado() );
            listCategorias.remove( anuncioSelected.getCategoria() );

            listEstados.set(0, anuncioSelected.getEstado());
            listCategorias.set(0, anuncioSelected.getCategoria());
        }


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

    // Menssagens de erro toast
    private void exibirMsgToast(String menssagem){
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
                btnSalvarAnuncio();
                break;
        }
    }

    private void getDadosEditText(){
        cidade = editCidade.getText().toString();
        titulo = editTitulo.getText().toString();
        valor = Objects.requireNonNull(editValor.getText()).toString();
        // pegar o dd do pais
        DDD = editDDD.getText().toString();
        phone = editNumero.getText().toString();
        desc = editDescricao.getText().toString();
        estado = spinnerEstado.getSelectedItem().toString();
        categoria = spinnerCategoria.getSelectedItem().toString();
    }

    // click Button salvarAnuncios
    private void btnSalvarAnuncio(){
        getDadosEditText();

        boolean verif = verificarCampos(cidade,titulo,valor,DDD,phone,desc,estado,categoria);
        // System.out.println("Verificado? => "+verif);

        if(verif){

            //configurar anuncio
            valor = "R"+valor;
            editValor.setText(valor);
            configAnuncio("salvar");
        }
    }

    private boolean
    verificarCampos(@NonNull String cidade, String titulo, String valor, String DDD,
                    String phone, String desc, String strEstado, String categoria
    ) {
        boolean verif = false;
        valor = valor.replace("$","");

        if( !cidade.isEmpty() ){
            if( !titulo.isEmpty()){
                if( !valor.isEmpty() ){
                    if( DDD.length() > 0 ){
                        if(phone.length() > 5){
                            if( !desc.isEmpty()){
                                if( !categoria.equals("Categorias")){
                                    if( !strEstado.equals("Estados")){
                                        if (listaFotosRecuperadas.size() != 0){

                                            if(strEstado.equals("Outro")){
                                                estado = editEstado.getText().toString();
                                                if(estado.isEmpty()){
                                                    exibirMsgToast(getString(R.string.preencha_um_estado));
                                                }else{
                                                    verif = true;
                                                }

                                                FecharTecladoSys.closedKeyBoard(this);
                                            }else{
                                                FecharTecladoSys.closedKeyBoard(this);
                                                verif = true;
                                            }

                                        }else{
                                            exibirMsgToast(getString(R.string.escolha_ao_menos_uma_imagem));
                                        }
                                    }else{
                                        exibirMsgToast(getString(R.string.escolha_um_estado));
                                    }
                                }else{
                                    exibirMsgToast(getString(R.string.escolha_uma_categoria));
                                }
                            }else{
                                exibirMsgToast(getString(R.string.preencha_uma_descri_o));
                            }
                        }else{
                            exibirMsgToast(getString(R.string.preencha_um_n_mero_de_celular_v_lido));
                        }
                    }else{
                        exibirMsgToast(getString(R.string.preencha_um_c_digo_de_rea_v_lido));
                    }
                }else{
                    exibirMsgToast(getString(R.string.preencha_um_valor_r));
                }
            }else{
                exibirMsgToast(getString(R.string.preencha_um_t_tulo));
            }
        }else{
            exibirMsgToast(getString(R.string.preencha_uma_cidade));
        }

        return verif;
    }

    // configurar anuncio
    public void configAnuncio(@NonNull String modo) {

        anuncio = new Anuncio();
        anuncio.setCidade(cidade);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setDDD(DDD);
        anuncio.setPhone(phone);
        anuncio.setDesc(desc);
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);


        if(modo.equals("salvar")){ // criar e salvar

            // gerar ID para anúncios
            anuncio.gerarId();

            String tipoUser = UserFirebase.getTipoUser();
            if(tipoUser.equals("L")){ // Salvar para loja

                salvarAnuncioDeLoja();

            }else{ // Salvar para o usuario

                // chamar o method que salva
                viewModel.salvarAnuncio(anuncio, listaFotosRecuperadas);
            }

            alertDialog(getString(R.string.salvando_an_ncio_aguarde));

        }else{// editar e atualizar
            anuncio.setIdAnuncio(anuncioSelected.getIdAnuncio());
            alertDialog(getString(R.string.atualizando_an_ncio_aguarde));

            // chamar o method que salva
            viewModel.salvarAnuncio(anuncio, listaFotosRecuperadas);
        }
    }

    // para salvar o anúncio dos usuários
    private void viewModelObserve(){
        // Observe url de img
        viewModel.getUrlImgStorage().observe(this,
                url -> {
                    int totalFotos = listaFotosRecuperadas.size();

                    listUrlFotos.add(url);

                    if (totalFotos == listUrlFotos.size()) {
                        anuncio.setFotos(listUrlFotos);
                        anuncio.salvarAnuncioNoDB(this);
                        alertDialog.dismiss();

                        startActivity( new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                        finish();
                    }
                }
        );
    }



    // para salvar o anúncio das lojas -> falta configurar essa parte! 
    private void salvarAnuncioDeLoja(){
        // salvar
        viewModel.salvarAnuncioLoja(anuncio, listaFotosRecuperadas);
    }

    public void alertDialog(String txt) {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(txt)
                .setCancelable(false)
                .build();
        alertDialog.show();
    }
    private void launchGallery(int requestCode){
        Gallery.open(this, requestCode);
        // startActivityForResult(i, requestCode);
    }

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
                Bitmap imgBitmapCortada = cortarImg(imgBitmap);


                //verificar o tamanho da imagem -> largra e altura
                if(imgBitmapCortada != null){// definir img cortada

                    // rotacionar a img
                    Bitmap imgBitmapRotate = RotacionarImgs.rotacionarIMG(imgBitmapCortada, imgSelected, this);

                    if(imgBitmapRotate != null){
                        //reuperar dados da img para o firebase
                        dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapRotate);

                        imagemComplet = imgBitmapRotate;
                    }


                }else{ // definir img sem corte

                    // rotacionar a img
                    Bitmap imgBitmapRotate = RotacionarImgs.rotacionarIMG(imgBitmap, imgSelected, this);

                    //reuperar dados da img para o firebase
                    dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapRotate);

                    imagemComplet = imgBitmapRotate;
                }

                // add no maximo 3 itens
                if(listaFotosRecuperadas.size() < 3){
                    listaFotosRecuperadas.add( dadosImg );
                }

                // configura img na tela se houver uma img
                // define imagem no ImageView
                definirImg(codeImageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exibirMsgToast(getString(R.string.erro_outra_foto));
        }
    }

    private Bitmap cortarImg(@NonNull Bitmap imgBitmap){
        Bitmap imgBitmapCortada = null;
        boolean bol = false;
        int altura = 900;
        int largura = 900;

        if(imgBitmap.getWidth() > largura && imgBitmap.getHeight() > altura ){
            imgBitmapCortada = Bitmap.createScaledBitmap(
                    imgBitmap,
                    largura,
                    altura,
                    bol
            );
        }else{

            if(imgBitmap.getWidth() > largura){// cortar a largura
                imgBitmapCortada = Bitmap.createScaledBitmap(
                        imgBitmap,
                        largura,
                        imgBitmap.getHeight(),
                        bol
                );
            }
            if(imgBitmap.getHeight() > altura){ // cortar a altura
                imgBitmapCortada = Bitmap.createScaledBitmap(
                        imgBitmap,
                        imgBitmap.getWidth(),
                        altura,
                        bol
                );
            }

        }
        return imgBitmapCortada;
    }


    private void definirImg(int codeImageView){
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
        builder.setTitle(getString(R.string.permiss_es_negadas));
        builder.setMessage(R.string.permitir_o_acesso_a_galeria);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.comfirmar), (dialog, which)-> finish());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.adapter.AdapterAnuncios;
import com.droid.app.skaterTrader.firebase.NotificationAnuncio;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.ClickRecyclerView;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.IntentActionView;
import com.droid.app.skaterTrader.service.LocationService;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.service.InitSdkAdmob;
import com.droid.app.skaterTrader.viewModel.ViewModelAnuncios;
import com.droid.app.skaterTrader.viewModel.ViewModelLocationUser;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
@SuppressLint("NotifyDataSetChanged")
public class ActivityMain extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerViewMain;
    private AdapterAnuncios adapterAnuncios;
    private AlertDialog alertDialog;
    TemplateView template;
    Anuncio anuncio;
    byte[] dadosImg;
    ViewModelAnuncios viewModel;
    ViewModelLocationUser viewModelLocation;
    String filtro = "";
    String estadosSelected = "";
    String categoriaSelected = "";
    MenuItem menuRedefinir;
    ClickRecyclerView clickRecyclerView;
    Button btn_regiao, btn_categoria;
    NativeAd Ad;
    DrawerLayout drawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    CircleImageView circleImageViewUser;
    ImageButton btnEditImgLogo;
    TextView nameUser, emailUser;
    TextView textViewInforme;
    int codeImg = 0;
    Uri img;
    String name;
    String email;
    final int MY_REQUEST_CODE = 188;
    View viewEditTextFiltro;
    TextView textViewFiltro;
    LocationService location;
    String str;

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.removeEventListener();

        this.Ad = InitSdkAdmob.getAd();
        if (Ad != null) {
            Ad.destroy();
            template.setVisibility(View.GONE);
            Log.i("tag",">> ad.destroy() <");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*this.Ad = InitSdkAdmob.getAd();
        if (Ad != null) {
            Ad.destroy();
            template.setVisibility(View.GONE);
            Log.i("tag",">> ad.destroy() <");
        }
        Log.i("tag",">> ad.destroy()1 <");*/
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        // verificar atulização do App no google Play
        verificarUpdateGooglePlay();

        if(codeImg == 1){
            nameUser.setText(name);
            emailUser.setText(email);
            Glide.with(this).load(img).into(circleImageViewUser);
        }

        location.getLastLocation();
    }

    private void verificarUpdateGooglePlay(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if ( appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) ) {

                alertAtulizacao(appUpdateManager, appUpdateInfo);
            }
        });
    }
    private void alertAtulizacao(AppUpdateManager appUpdateManager, AppUpdateInfo appUpdateInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.atualiza_o_dispon_vel));
        builder.setMessage(getString(R.string.dica_atualizacao_google_play));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.msg_btn_atualizar), (dialog, which) -> {

            try {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                );
            } catch (IntentSender.SendIntentException e) {
                iniciarAppPlayStore();
                finish();
                throw new RuntimeException(e);
            }

            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.msg_btn_cancelar), (dialog, which) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //////// ======= \\\\\\\
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.layout_main);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        // WakeLook
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // config da toolBar
        configActionBar();

        // iniciar componentes da tela
        iniciarComponentes();

        // ativar notificação
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.novo_anuncio));

        // --------> init ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModelAnuncios.class);
        viewModelLocation = new ViewModelProvider(this).get(ViewModelLocationUser.class);
        location = new LocationService(this, viewModelLocation, viewModel);

        // init observer
        observerViewModel();

        // se usuário tiver logado
        if (User.UserLogado()) {

            String tipoUser = User.user().getDisplayName();

            if(tipoUser != null && tipoUser.equals("L")){
                startActivity(new Intent(this, ActivityMainLoja.class));
                finish();
            }else{
                // navigation Drawer // config menu drawer
                configNavigationDrawer();
            }
        }

        // permissão de notificação
        new NotificationAnuncio().askNotificationPermission(this);

        // configurar recyclerView
        configRecycler();

        // buscar anuncios do db
        //recuperarAnuncios();
        // buscar anuncios do db com base na localização do usuário
        //location.getLastLocation();

        // init SDK Ads
        MobileAds.initialize(this, initializationStatus -> {
        });

        // Init sdk adMob
        template = findViewById(R.id.my_template);
        InitSdkAdmob.initSdkAdmob(this, template);
    }
    private void configActionBar(){
        // config da toolBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setElevation(0);
    }
    private void configNavigationDrawer(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new
                ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
    }
    // click do navigationDrawer
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_store:
                Toast.makeText(this,
                        getString(R.string.lojas_em_breve_estar_dipon_vel),
                        Toast.LENGTH_LONG).show();
                break;

            case R.id.picosRole:
                startActivity( new Intent(this, MapaPicosSkate.class) );
                break;

            case R.id.nav_shareApp:
                IntentActionView.sharedApp(this);
                break;

            case R.id.avaliar:
                iniciarAppPlayStore();
                break;

            case R.id.nav_info:
                startActivity(new Intent(this, InfoActivity.class));
                break;

            case R.id.sair:
                if(FirebaseRef.getAuth().getCurrentUser() != null){
                    FirebaseRef.getAuth().signOut();
                    startActivity( new Intent(this, AcessoActivity.class) );
                    finish();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void launchGallery(){
        Gallery.open(this, 0);
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
            User user = new User();
            user.setFoto(dadosImg);
            user.salvarFotoPerfil( this);
            Glide.with(this).load(imgSelected).into(circleImageViewUser);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // set img
        if( requestCode == 0 && resultCode == RESULT_OK ){
            if(data != null) {
                // recupera img selecionada da galeria
                Uri imgSelected = data.getData();
                circleImageViewUser.setImageURI(imgSelected);
                definirImagemPerfi(imgSelected);
            }
        }

        // atualização
        if(requestCode == MY_REQUEST_CODE){
            if(resultCode != RESULT_OK){
                iniciarAppPlayStore();
                finish();
            }
        }
    }

    private void iniciarComponentes() {
        anuncio = new Anuncio();
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        btn_regiao = findViewById(R.id.regiao);
        btn_regiao.setOnClickListener(this);
        btn_categoria = findViewById(R.id.categoria);
        btn_categoria.setOnClickListener(this);
        textViewInforme = findViewById(R.id.textViewInforme);

        viewEditTextFiltro = getLayoutInflater().inflate(R.layout.edit_text_filtro, null);
        textViewFiltro = viewEditTextFiltro.findViewById(R.id.editTextFiltro);
    }
    private void configRecycler() {
        recyclerViewMain.setLayoutManager( new LinearLayoutManager(this));
        recyclerViewMain.setHasFixedSize(true);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if( User.UserLogado() ){
            menu.setGroupVisible(R.id.menuLogado, true);
        }else{
            menu.setGroupVisible(R.id.menuDeslogado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuRedefinir = menu.findItem(R.id.redefinir);
        menuRedefinir.setVisible(false);

        // definir Informações de perfil do usuario
        definirInfoPerfilUser();

        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint({"NonConstantResourceId", "IntentReset"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(User.UserLogado()){
            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        switch ( item.getItemId() ){
            case R.id.acessoUsers:
                startActivity( new Intent(this, AcessoActivity.class) );
                break;

            case R.id.suport:
                sendEmailSupport();
                break;

            case R.id.anuncios:
                startActivity( new Intent(this, MeusAnunciosActivity.class) );
                break;

            case R.id.redefinir:
                resetFiltroAnuncios();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void definirInfoPerfilUser(){
        if(User.UserLogado() && User.user() != null){
            img = User.user().getPhotoUrl();
            name = getString(R.string.ola)+User.user().getDisplayName();
            email = User.user().getEmail();

            nameUser = findViewById(R.id.nameUser);
            emailUser = findViewById(R.id.emailUser);

            circleImageViewUser = findViewById(R.id.imageUser);
            btnEditImgLogo = findViewById(R.id.btnEditImgLogo);
            if(circleImageViewUser != null){
                circleImageViewUser.setOnClickListener(this);
                btnEditImgLogo.setOnClickListener(this);
            }else{
                circleImageViewUser = findViewById(R.id.imageUser);
                circleImageViewUser.setOnClickListener(this);
                btnEditImgLogo = findViewById(R.id.btnEditImgLogo);
                btnEditImgLogo.setOnClickListener(this);
            }


            if(name != null && nameUser != null){
                nameUser.setText(name);
                if(email != null && emailUser != null)
                    emailUser.setText(email);
            }

            try{
                if(img != null) {
                    Glide.with(this).load(img).into(circleImageViewUser);
                    codeImg = 1;
                }else{
                    //String caminhoFoto = "android.resource://"+getPackageName()+"/"+R.drawable.logo_inicial_1;
                    circleImageViewUser.setImageResource(R.drawable.logo_inicial_1);
                }
            }catch (Exception e){
                circleImageViewUser.setImageResource(R.drawable.logo_inicial_1);
                e.printStackTrace();
            }
        }
    }

    private void sendEmailSupport(){
        Uri email = Uri.parse("mailto:agenciamagnus2023@gmail.com");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(email); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.suporte_skatertrade));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.suporte_skatertrade));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void iniciarAppPlayStore() {
        // getPackageName() from Context or Activity object
        final String appPackageName = getPackageName();
        try {
            startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));

        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))
            );
        }
    }
    private void alertDialogCustom(String txt) {
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(txt)
                .setCancelable(true)
                .build();
        alertDialog.show();
    }
    //click dos buttons
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()){
            case R.id.regiao:
                filtrarEstados();
                break;

            case R.id.categoria:
                filtrarCategorias();
                break;
        }
        int idItem = v.getId();
        if (idItem == R.id.imageUser || idItem == R.id.btnEditImgLogo){
            launchGallery();
        }
    }

    private void setAlertDialog(String titulo, Spinner spinner, View viewSpinner, String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setView(viewSpinner);

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            filtro = spinner.getSelectedItem().toString();
            if( code.equals("estado") ){

                /*if( filtro.equals("Outro") ){
                    OutroFiltro(titulo);
                    dialog.dismiss();
                }*/
                recuperarAnunnciosPorEstado(filtro);
                dialog.cancel();

            } else if ( code.equals("categoria") ) {
                recuperarAnunnciosPorCategoria(filtro);
                dialog.cancel();
            }
        });

        builder.setNegativeButton(getString(R.string.msg_btn_cancelar), (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void OutroFiltro(String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setView(viewEditTextFiltro);

        builder.setPositiveButton(getString(R.string.ok_btn),
            (dialog, which) -> {
                if(!textViewFiltro.getText().toString().isEmpty()){
                    filtro = textViewFiltro.getText().toString().toUpperCase();
                    recuperarAnunnciosPorEstado(filtro);
                }
                dialog.cancel();
            });

        AlertDialog dialogg = builder.create();
        dialogg.show();
    }

    // add viewModel
    private void recuperarAnunnciosPorEstado(String estado) {
        estadosSelected = estado;

        viewModel.filtrarEstadoAnuncios(estado, categoriaSelected, this);

        if(menuRedefinir != null){
            menuRedefinir.setVisible(true);
        }

        // defnir nome btn
        String btnTxt =  String.format(getString(R.string.msg_regiao)+"-%s",estado);
        btn_regiao.setText(btnTxt);
    }
    // add viewModel
    public void recuperarAnunnciosPorCategoria(String categoria) {
        categoriaSelected = categoria;

        viewModel.filtrarCategoriaAnuncios(categoria, estadosSelected, this);

        menuRedefinir.setVisible(true);
    }

    public void recuperarAnuncios() {
        viewModel.recuperarAnuncios(this);
    }

    private void resetFiltroAnuncios(){
        if(menuRedefinir != null){
            menuRedefinir.setVisible(false);

            estadosSelected = "";
            categoriaSelected = "";
            // defnir nome btn
            btn_regiao.setText(R.string.regiao);
            recuperarAnuncios();
        }
    }

    private void observerViewModel() {
        viewModel.getShowMsg().observe(this, this::alertDialogCustom );

        // model recuperar anuncios
        viewModel.getList().observe(this,
            anuncios -> {
                if(anuncios != null){
                    if(anuncios.isEmpty()){
                        recyclerViewMain.setVisibility(View.GONE);
                        textViewInforme.setVisibility(View.VISIBLE);
                        textViewInforme.setText(getString(R.string.n_o_h_an_ncios_para_esse_filtro));
                        alertDialog.cancel();
                    }else{
                        recyclerViewMain.setVisibility(View.VISIBLE);
                        textViewInforme.setVisibility(View.GONE);

                        adapterAnuncios = new AdapterAnuncios(anuncios,this);
                        recyclerViewMain.setAdapter( adapterAnuncios );
                        adapterAnuncios.notifyDataSetChanged();

                        clickRecyclerView = new ClickRecyclerView(recyclerViewMain,anuncios,adapterAnuncios,this);
                        clickRecyclerView.clickExibirRecyclerView();
                        alertDialog.cancel();
                    }
                }
            }
        );

        // filtrar com base na localização do usuário
        viewModelLocation.getEstado().observe(this,
                estado -> {

                    ExecutorService executor = Executors.newFixedThreadPool(1);
                    executor.execute(() -> {

                        Handler handler = new Handler(Looper.getMainLooper());
                        String[] estados = getResources().getStringArray(R.array.estados);

                        for (int i=0; i<estados.length; i++){
                            String str2 = estados[i];
                            if(estado.contains(str2)){
                                this.str = str2.trim();
                                break;
                            }
                        }

                        handler.post(()->{
                            recuperarAnunnciosPorEstado(str);
                            Log.i("TAG", ">> "+str);
                            if(alertDialog != null){
                                alertDialog.cancel();
                            }
                        });

                    });
                }
        );
    }
    @SuppressLint("InflateParams")
    public void filtrarEstados() {
        String code = getString(R.string.estado_code);
        String titulo = getString(R.string.selecione_o_estado);
        //config spinner
        View viewSpinner1 = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinnerEstado = viewSpinner1.findViewById(R.id.spinnerFiltro);
        spinnerEstado.getBackground()
                .setColorFilter(Color.parseColor("#3A8C0E"), PorterDuff.Mode.SRC_ATOP);

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterSpinner1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,estados
        );
        adapterSpinner1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerEstado.setAdapter(adapterSpinner1);

        setAlertDialog(titulo, spinnerEstado, viewSpinner1, code);

        // define o click do spinner Estado
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("TAG", ">> "+position+" >> "+parent.getSelectedItem().toString());
                if(parent.getSelectedItem().toString().equals("Outro") ){
                    OutroFiltro(titulo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @SuppressLint("InflateParams")
    public void filtrarCategorias() {
        String code = getString(R.string.categoria_code);
        //config spinner
        View viewSpinner2 = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinnerCategorias = viewSpinner2.findViewById(R.id.spinnerFiltro);
        spinnerCategorias.getBackground()
                .setColorFilter(Color.parseColor("#3A8C0E"), PorterDuff.Mode.SRC_ATOP);

        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterSpinner2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,categorias
        );
        adapterSpinner2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCategorias.setAdapter(adapterSpinner2);

        setAlertDialog(getString(R.string.selecione_a_categoria), spinnerCategorias, viewSpinner2, code);
    }
}
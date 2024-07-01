package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.databinding.ActivityMainLojaBinding;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.helper.ConfigDadosImgBitmap;
import com.droid.app.skaterTrader.helper.Gallery;
import com.droid.app.skaterTrader.helper.IntentActionView;
import com.droid.app.skaterTrader.helper.Permissions;
import com.droid.app.skaterTrader.helper.RotacionarImgs;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.User;
import com.droid.app.skaterTrader.viewModel.ViewModelConfigDadosLoja;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityMainLoja extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    CircleImageView circleImageViewUser;
    ImageButton btnEditImgLogo;
    TextView nameLoja, emailUser;
    Uri img;
    String email;
    final int MY_REQUEST_CODE = 188;
    byte[] dadosImg;
    Loja loja;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    ActivityMainLojaBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        img = User.user().getPhotoUrl();
        email = User.user().getEmail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(com.droid.app.skaterTrader.R.layout.activity_main_loja);

        binding = ActivityMainLojaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // WakeLook
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // validar permissions
        Permissions.validatePermissions(permissions, this, 1);

        // navigation Drawer // config menu drawer
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniciarComponentes();
    }

    private void iniciarComponentes(){
//        drawer = findViewById(R.id.drawerLoja);
//        navigationView = findViewById(R.id.nav_viewLoja);
        drawer = binding.drawerLoja;
        navigationView = binding.navViewLoja;

        actionBarDrawerToggle = new
                ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();

        // obter ref e dados da loja
        loja = new Loja();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_meusDados:
                startActivity( new Intent(this, ConfigDadosLojaActivity.class) );
                break;

            case R.id.nav_produtos:
                startActivity( new Intent(this, MeusAnunciosActivity.class) );
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // definir Informações de perfil do usuario
        if(User.UserLogado()){

            img = User.user().getPhotoUrl();
            email = User.user().getEmail();

            nameLoja = findViewById(R.id.nameUser);
            emailUser = findViewById(R.id.emailUser);
            circleImageViewUser = findViewById(R.id.imageUser);
            btnEditImgLogo = findViewById(R.id.btnEditImgLogo);

            if(circleImageViewUser != null){
                circleImageViewUser.setOnClickListener(this);
                btnEditImgLogo.setOnClickListener(this);
            }

            try{

                if(img != null) {
                    Glide.with(this).load(img).into(circleImageViewUser);
                }

                emailUser.setText(email);
                loja.getNameLojaDb(nameLoja);

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            FirebaseRef.getAuth().signOut();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
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

    //click dos buttons
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View v) {
        int idItem = v.getId();
        if (idItem == R.id.imageUser || idItem == R.id.btnEditImgLogo){
            Gallery.open(this, 0);
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
    private void definirImagemPerfi(@Nullable Uri imgSelected){
        try {
            // transformar em bitMap
            Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelected);

            // cortar imagem
            //Bitmap imgBitmapCortada = Bitmap.createScaledBitmap(imgBitmap, 720, 720, true);

            // rotacionar a img
            Bitmap imgBitmapRotate = RotacionarImgs.rotacionarIMG(imgBitmap, imgSelected, this);
            if(imgBitmapRotate != null){
                //reuperar dados da img para o firebase
                dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmapRotate);

            }else{
                //reuperar dados da img para o firebase
                dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmap);
            }

            // salvar img do usuario
            //Loja loja = new Loja();
            new ViewModelConfigDadosLoja().salvarImgLogoLoja(dadosImg);
            //loja.salvarImgLogoLoja(dadosImg);
            Glide.with(this).load(imgSelected).into(circleImageViewUser);

            recreate();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
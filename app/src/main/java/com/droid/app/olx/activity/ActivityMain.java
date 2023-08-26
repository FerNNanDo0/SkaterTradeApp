package com.droid.app.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.droid.app.olx.R;
import com.droid.app.olx.adapter.AdapterAnuncios;
import com.droid.app.olx.firebaseRefs.FirebaseRef;
import com.droid.app.olx.model.Anuncio;
import com.droid.app.olx.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import dmax.dialog.SpotsDialog;
@SuppressLint("NotifyDataSetChanged")
public class ActivityMain extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerViewMain;
    private List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference databaseRef;
    DatabaseReference anunciosRef;
    DatabaseReference anunciosFiltroRef;
    private AlertDialog alertDialog;
    Anuncio anuncio;
    String filtro = "";
    String estadosSelected = "";
    String categoriaSelected = "";
    MenuItem menuRedefinir;
    ClickRecyclerView clickRecyclerView;
    ValueEventListener valueEventListener,valueEventListener1,valueEventListener2;
    Button btn_regiao, btn_categoria;
    @Override
    protected void onStart() {
        super.onStart();
        recuperarAnuncios();
    }
    @Override
    protected void onStop() {
        super.onStop();
        anunciosRef.removeEventListener(valueEventListener);
        if(valueEventListener1 != null){
            anunciosFiltroRef.removeEventListener(valueEventListener1);
        }
        if(valueEventListener2 != null){
            anunciosFiltroRef.removeEventListener(valueEventListener2);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assert getSupportActionBar() != null;
        getSupportActionBar().setElevation(0);
        iniciarComponentes();

        // configurar recyclerView
        recyclerViewMain.setLayoutManager( new LinearLayoutManager(this));
        recyclerViewMain.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList,this);
        recyclerViewMain.setAdapter( adapterAnuncios );

        clickRecyclerView = new ClickRecyclerView(recyclerViewMain,anuncioList,adapterAnuncios,this);
        clickRecyclerView.clickExibirRecyclerView();
    }
    public void iniciarComponentes(){
        databaseRef = FirebaseRef.getDatabase();
        anuncio = new Anuncio();
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        btn_regiao = findViewById(R.id.regiao);
        btn_regiao.setOnClickListener(this);
        btn_categoria = findViewById(R.id.categoria);
        btn_categoria.setOnClickListener(this);
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
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId() ){

            case R.id.acessoUsers:
                startActivity( new Intent(this, AcessoActivity.class) );
                break;

            case R.id.sair:
                FirebaseRef.getAuth().signOut();
                invalidateOptionsMenu();
                finish();
                break;

            case R.id.anuncios:
                startActivity( new Intent(this, MeusAnunciosActivity.class) );
                break;

            case R.id.redefinir:
                recuperarAnuncios();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // acao de btn voltar do sistema-android
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void alertDialogCustom(String txt) {
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.regiao:
                filtrarEstados();
                break;

            case R.id.categoria:
                filtrarCategorias();
                break;
        }
    }
    private void setAlertDialog(String titulo, Spinner spinner, View viewSpinner, String code){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setView(viewSpinner);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            filtro = spinner.getSelectedItem().toString();
            if( code.equals("estado") ){
                recuperarAnunnciosPorEstado(filtro);
            } else if ( code.equals("categoria") ) {
                recuperarAnunnciosPorCategoria(filtro);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void recuperarAnunnciosPorEstado(String estado) {
        estadosSelected = estado;
        anunciosFiltroRef = databaseRef.child("anuncios").child(estado);
        valueEventListener1 = anunciosFiltroRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anuncioList.clear();
                alertDialogCustom("Buscando anúncios");
                for(DataSnapshot categorias : snapshot.getChildren()){
                    for(DataSnapshot anuncios : categorias.getChildren()){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        if(anuncio != null){
                            if( !categoriaSelected.isEmpty() &&
                                anuncio.getCategoria().equals(categoriaSelected)
                            ){
                                anuncioList.add( anuncio );
                            }

                            if( categoriaSelected.isEmpty() ){
                                anuncioList.add( anuncio );
                            }
                        }
                    }
                }
                Collections.reverse(anuncioList);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
                menuRedefinir.setVisible(true);

                // defnir nome btn
                String btnTxt =  String.format("REGIÃO-%s",filtro);
                btn_regiao.setText(btnTxt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void recuperarAnunnciosPorCategoria(String categoria){
        categoriaSelected = categoria;
        anunciosFiltroRef = databaseRef.child("anuncios");
        valueEventListener2 = anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot estados : snapshot.getChildren() ){
                    for(DataSnapshot categorias : estados.getChildren()){
                        for(DataSnapshot anuncios : categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            if(anuncio != null){
                                if ( !estadosSelected.isEmpty() &&
                                      anuncio.getEstado().equals(estadosSelected) &&
                                      anuncio.getCategoria().equals(categoria)
                                ){
                                    anuncioList.add( anuncio );
                                    System.out.println("estado: "+estadosSelected);
                                }

                                if( estadosSelected.isEmpty() &&
                                    anuncio.getCategoria().equals( categoria )
                                ){
                                    anuncioList.add( anuncio );
                                }
                            }
                        }
                    }
                }
                Collections.reverse( anuncioList );
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
                menuRedefinir.setVisible(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void recuperarAnuncios() {
        if(anuncioList.size() == 0){
            alertDialogCustom("Buscando Anúncios disponíveis");
        }
        anunciosRef = databaseRef.child("anuncios");
        valueEventListener = anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // recuperar os dados
                anuncioList.clear();
                for( DataSnapshot estados : snapshot.getChildren() ){
                    for(DataSnapshot categorias : estados.getChildren()){
                        for(DataSnapshot anuncios : categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            anuncioList.add( anuncio );
                        }
                    }
                }
                Collections.reverse( anuncioList );
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();

                if(menuRedefinir != null)
                    menuRedefinir.setVisible(false);

                estadosSelected = "";
                categoriaSelected = "";
                // defnir nome btn
                btn_regiao.setText(R.string.regiao);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void filtrarEstados(){
        String code = "estado";
        //config spinner
        View viewSpinner1 = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinnerEstado = viewSpinner1.findViewById(R.id.spinnerFiltro);

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterSpinner1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,estados
        );
        adapterSpinner1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerEstado.setAdapter(adapterSpinner1);

        setAlertDialog("Selecione o estado", spinnerEstado, viewSpinner1, code);
    }
    public void filtrarCategorias(){
        String code = "categoria";
        //config spinner
        View viewSpinner2 = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinnerCategorias = viewSpinner2.findViewById(R.id.spinnerFiltro);

        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterSpinner2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item ,categorias
        );
        adapterSpinner2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCategorias.setAdapter(adapterSpinner2);

        setAlertDialog("Selecione a categoria", spinnerCategorias, viewSpinner2, code);
    }
}
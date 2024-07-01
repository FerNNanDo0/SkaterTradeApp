package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.databinding.ActivityMapaPicosSkateBinding;

import com.droid.app.skaterTrader.helper.Informe;
import com.droid.app.skaterTrader.service.LocationService;
import com.droid.app.skaterTrader.model.MarkerPico;
import com.droid.app.skaterTrader.viewModel.ViewModelMapaPicos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;

public class MapaPicosSkate extends AppCompatActivity
        implements OnMapReadyCallback {
    GoogleMap mMap;
    Marker markerPico;
    EditText EditTitulo;
    LatLng myLocation;
    int drawableMarker;
    RadioButton radioButton1, radioButton2;
    LocationService location;
    ActivityMapaPicosSkateBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        recuperarPontosDePico();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mapa_picos_skate);
        binding = ActivityMapaPicosSkateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //config toolbar
        configActionBar();

        location = new LocationService(this, null, null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // mostrar uma vez só o imforme de status
        if(Informe.recuperarCodeStatusPico(this).equals("0")){
            infoMarkerStatusPico();
        }

    }

    private void configActionBar(){
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        getSupportActionBar().setTitle(R.string.mapa_de_picos);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // pegar localização
        myLocation = location.locationUpdate(mMap);

        mMap.clear();
        mMap.setOnMapLongClickListener(this::configurarPicoEscolhido);
    }
    private void configurarPicoEscolhido(LatLng latLng) {
        View view = getLayoutInflater().inflate(R.layout.view_add_marker_alert, null);
        EditTitulo = view.findViewById(R.id.editTextNomeMarker);
        EditTitulo.setFocusable(true);
        
        radioButton1 =  view.findViewById(R.id.radioButton1);
        radioButton2 =  view.findViewById(R.id.radioButton2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.marcar_pico_neste_local));
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.marcar), null);
        builder.setNegativeButton(getString(R.string.cancelar), (dialog, wish) -> dialog.cancel());

        final AlertDialog mDialog = builder.create();
        mDialog.setOnShowListener( dialog -> {

            Button positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener( v -> {

                if(EditTitulo.getText().toString().isEmpty()){
                    Toast.makeText(
                            getApplicationContext(), getString(R.string.defina_um_nome_pro_pico), Toast.LENGTH_SHORT).show();
                    EditTitulo.setError(getString(R.string.campo_obrigat_rio));
                    EditTitulo.setFocusable(true);
                    EditTitulo.requestFocus();
                }else{
                    String nome = EditTitulo.getText().toString();

                    if(radioButton1.isChecked()){
                        drawableMarker = R.drawable.marker_green_skate_40;
                        // Adicione um marcador onde vc clicar
                        addMarkerPico(nome, latLng, drawableMarker, "green");

                        dialog.dismiss();

                    } else if (radioButton2.isChecked()) {
                        drawableMarker = R.drawable.marker_red_skate_40;
                        // Adicione um marcador onde vc clicar
                        addMarkerPico(nome, latLng, drawableMarker, "red");

                        dialog.dismiss();

                    }else{
                        Toast.makeText(this, getString(R.string.escolha_o_status_do_pico), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        mDialog.show();
    }

    // btn floating
    public void focusMyLocation(View view) {
        if ( myLocation != null ){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }else{
            //locationUpdate();
            myLocation = location.locationUpdate(mMap);
        }
    }

    public void infoMarkerStatusPico(){
        View viewInfoMarker = getLayoutInflater().inflate(R.layout.info_status_marker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.info_status_do_marcador));
        builder.setCancelable(true);
        builder.setView(viewInfoMarker);
        builder.setNegativeButton(getString(R.string.entendi), (dialog, wish) ->
            dialog.cancel()
        );
        AlertDialog dialog = builder.create();
        dialog.show();

        Informe.salvarCodeStatusPico("1",this);
    }
    private void salvarPicoNoDB(@NonNull LatLng latLng, String nome, String corMarker) {
        MarkerPico markerPico = new MarkerPico();
        markerPico.setNome(nome);
        markerPico.setCorMarker(corMarker);
        markerPico.setLatitude(latLng.latitude);
        markerPico.setLongitude(latLng.longitude);
        markerPico.salvar();
    }
    private void recuperarPontosDePico() {
        // init viewModel
        ViewModelMapaPicos viewModel = new ViewModelProvider(this).get(ViewModelMapaPicos.class);

        // Observe snapShot
        viewModel.getSnapShot().observe(this,
                this::showMarkersOfDB
        );

        // recurar picos
        viewModel.recuperarPontosDePico();
    }

    private void showMarkersOfDB(@NonNull DataSnapshot snapshot){
        for(DataSnapshot snap : snapshot.getChildren()){
            MarkerPico markerPico1 = snap.getValue(MarkerPico.class);

            if(markerPico1 != null){

                String nome = markerPico1.getNome();
                String corMarker = markerPico1.getCorMarker();
                LatLng latLng = new LatLng(markerPico1.getLatitude(), markerPico1.getLongitude());

                if(corMarker.equals("green")){
                    markerPico = mMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(nome)
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green_skate_40))
                    );
                }else if(corMarker.equals("red")){
                    markerPico = mMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(nome)
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red_skate_40))
                    );
                }

            }

        }
    }

    private void addMarkerPico(String nome, LatLng latLng, int drawableMarker, String corMarker){
        // Adicione um marcador onde vc clicar
        markerPico = mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(nome)
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .icon(BitmapDescriptorFactory.fromResource(drawableMarker))
        );

        // salvar no db
        salvarPicoNoDB(latLng, nome, corMarker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_info){
            infoMarkerStatusPico();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
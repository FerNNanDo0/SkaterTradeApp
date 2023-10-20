package com.droid.app.skaterTrader.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.model.MarkerPico;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MapaPicosSkate extends AppCompatActivity
        implements OnMapReadyCallback {
    GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationClient;
    LocationSettingsRequest mLocationSettingsRequest;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    Marker markerPico, markerMeuLocal;
    EditText EditTitulo;
    LatLng myLocation;
    DatabaseReference database;
    DatabaseReference picoRef;
    ValueEventListener valueEventListener;
    int codeL = 0;

    int drawableMarker;

    RadioButton radioButton1, radioButton2;
    @Override
    protected void onStart() {
        super.onStart();
        recuperarPontosDePico();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        picoRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_picos_skate);

        //config toolbar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.mapa_de_picos);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // ref do db
        database = FirebaseRef.getDatabase();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // pegar localização
        getLastLocation();
        mMap.clear();
        mMap.setOnMapClickListener(this::configurarPicoEscolhido);
    }
    private void configurarPicoEscolhido(LatLng latLng) {
        View view = getLayoutInflater().inflate(R.layout.view_add_marker_alert, null);
        EditTitulo = view.findViewById(R.id.editTextNomeMarker);
        EditTitulo.setFocusable(true);
        
        radioButton1 =  view.findViewById(R.id.radioButton1);
        radioButton2 =  view.findViewById(R.id.radioButton2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Marcar pico neste local ?");
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("Marcar", null);
        builder.setNegativeButton("Cancelar", (dialog, wish) -> dialog.cancel());

        final AlertDialog mDialog = builder.create();
        mDialog.setOnShowListener( dialog -> {

            Button positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener( v -> {

                if(EditTitulo.getText().toString().isEmpty()){
                    Toast.makeText(
                            getApplicationContext(), "Defina um nome pro pico", Toast.LENGTH_SHORT).show();
                    EditTitulo.setError("Campo obrigatório!");
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
                        Toast.makeText(this, "Escolha o status do pico", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        mDialog.show();
    }
    public void focusMyLocation(View view) {
        if ( myLocation != null ){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
        }else{
            locationUpdate();
        }
    }
    public void infoMarkerStatusPico(){
        View viewInfoMarker = getLayoutInflater().inflate(R.layout.info_status_marker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info: Status do marcador");
        builder.setCancelable(true);
        builder.setView(viewInfoMarker);
        builder.setNegativeButton("Entendi", (dialog, wish) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
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
        picoRef = database.child("picos");
        valueEventListener = picoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//              System.out.println("nome pico: "+markerPico.getNome()+" LatLong: "+markerPico.getLatLng());
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // getLastLocation
    @SuppressWarnings("MissingPermission")
    public void getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {

                        //obtém a última localização conhecida
                        Location mLastLocation = task.getResult();

                        // definir a Lati e Longi para marcar o local
                        double lati = mLastLocation.getLatitude();
                        double longi = mLastLocation.getLongitude();
                        myLocation = new LatLng(lati, longi);
                        addMyMarker(myLocation);
                        locationUpdate();
                    }
                });
    }
    public void locationUpdate() {
    // LocationResquest com as definições requeridas
        //noinspection deprecation
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)        // 1 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        // Construção dum LocationSettingsRequest com as definições requeridas
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        // Callback a ser chamado quando houver alterações na localização
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location currentLocation = locationResult.getLastLocation();
                assert currentLocation != null;

                // definir a Lati e Longi para marcar o local
                myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                addMyMarker(myLocation);
                startLocationUpdates();
            }
        };
    }
    //Inicia o processo de pedido de actualizações de localização
    public void startLocationUpdates() {
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
        // Verifica se as definições do dispositivo estão configuradas para satisfazer
        // as requeridas pelo LocationSettingsRequest.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    // Todas as definições do dispositivo estão configuradas para satisfazer as requeridas.
                    // Inicia o pedido de actualizações de localização

                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(e -> {

                });
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
    private void addMyMarker(LatLng myLocation){
        if (markerMeuLocal != null) {
            markerMeuLocal.remove();
        }
        markerMeuLocal = mMap.addMarker(
                new MarkerOptions().position(myLocation)
                        .title("Seu Local")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_skate35))
        );
        if (codeL == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            codeL = 1;
        }
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
}
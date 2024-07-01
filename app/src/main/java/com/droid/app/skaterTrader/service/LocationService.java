package com.droid.app.skaterTrader.service;

import static android.content.Context.LOCATION_SERVICE;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.helper.Permissions;
import com.droid.app.skaterTrader.helper.RequestsPermission;
import com.droid.app.skaterTrader.viewModel.ViewModelAnuncios;
import com.droid.app.skaterTrader.viewModel.ViewModelLocationUser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class LocationService {
    Activity activity;
    FusedLocationProviderClient mFusedLocationClient;
    LocationSettingsRequest mLocationSettingsRequest;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    LatLng myLocation;
    Marker markerMeuLocal;
    int codeL = 0;
    int cod;
    List<Address> listEnderecos;
    ViewModelLocationUser viewModelLocationUser;
    ViewModelAnuncios viewModelAnuncios;

    public LocationService(@NonNull Activity activity, ViewModelLocationUser viewModel,
                           ViewModelAnuncios viewModelAnuncios) {

        String tituloMain = activity.getString(R.string.ative_o_gps_para_ver_an_ncios_mais_pr_ximos_a_voc);
        String tituloMap = activity.getString(R.string.ative_o_gps_para_ver_picos_mais_pr_ximos_a_voc);

        this.activity = activity;
        this.viewModelLocationUser = viewModel;
        this.viewModelAnuncios = viewModelAnuncios;

        if (viewModelAnuncios != null){
            cod=0;
            enabledGPS(tituloMain);
        }else{
            cod=1;
            enabledGPS(tituloMap);
        }

    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Permissions.validatePermissions(RequestsPermission.permissionLocation, activity, 1);

            return;
        }
    }

    @SuppressWarnings("MissingPermission")
    public void getLastLocation() {
        // validar permissions
        Permissions.validatePermissions(RequestsPermission.permissionLocation, activity, 1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.getLastLocation()
            .addOnCompleteListener(activity, task -> {
                if (task.isSuccessful() && task.getResult() != null) {

                    //obtém a última localização conhecida
                    Location mLastLocation = task.getResult();

                    // definir a Lati e Longi para marcar o local
                    double lati = mLastLocation.getLatitude();
                    double longi = mLastLocation.getLongitude();
                    myLocation = new LatLng(lati, longi);

                    getLocationUser(lati, longi);
                }
            });
    }

    public LatLng locationUpdate(GoogleMap mMap) {
        checkPermission();

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

                // definir a Lati e Longi para marcar o local
                if (currentLocation != null) {
                    myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    if (mMap != null) {
                        addMyMarker(myLocation, mMap);
                    }
                }
            }
        };
        startLocationUpdates();
        return myLocation;
    }

    @SuppressLint("MissingPermission")
    //Inicia o processo de pedido de actualizações de localização
    private void startLocationUpdates() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(activity);
        // Verifica se as definições do dispositivo estão configuradas para satisfazer
        // as requeridas pelo LocationSettingsRequest.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    // Todas as definições do dispositivo estão configuradas para satisfazer as requeridas.
                    // Inicia o pedido de actualizações de localização
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(e -> {

                });
    }
    public void addMyMarker(LatLng myLocation, GoogleMap mMap){
        //this.markerMeuLocal = markerMeuLoc;
        if (markerMeuLocal != null) {
            markerMeuLocal.remove();
        }
        markerMeuLocal = mMap.addMarker(
                new MarkerOptions().position(myLocation)
                        .title(activity.getString(R.string.seu_local))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_skate35))
        );
        if (codeL == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
            codeL = 1;
        }
    }

    private void getLocationUser(double latitude, double longitude) {
        //Log.d("TAG-1", ">> MyLocation >> "+myLocation);
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            listEnderecos = geocoder.getFromLocation(latitude, longitude, 1);
            if (listEnderecos != null) {
                String strEstado = listEnderecos.get(0).getAddressLine(0);
                viewModelLocationUser.setEstado(strEstado);
            }
        } catch (Exception e) {
            Log.d("ERRO", ">> "+e.getMessage());
        }
    }
    private void enabledGPS(String titulo){
        LocationManager service = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        // Verifica se o GPS está ativo
        if(service != null){
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Caso não esteja ativo abre um novo diálogo com as configurações para
            // realizar se ativamento
            if (!enabled) {
                setAlertDialog(titulo);
            }
        }
    }

    private void setAlertDialog(String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(titulo);

        builder.setPositiveButton(activity.getString(R.string.sim), (dialog, which) -> {
            dialog.dismiss();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        });

        builder.setNegativeButton(activity.getString(R.string.nao), (dialog, which) -> {
            dialog.dismiss();

            if (viewModelAnuncios != null){
                viewModelAnuncios.recuperarAnuncios(activity);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
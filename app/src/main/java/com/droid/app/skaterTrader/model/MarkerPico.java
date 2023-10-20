package com.droid.app.skaterTrader.model;

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class MarkerPico {
    double latitude;
    double longitude;
    String nome;
    String corMarker;
    DatabaseReference database;
    String idPico;
    public MarkerPico(){
        database = FirebaseRef.getDatabase();
        setIdPico(database.child("picos").push().getKey());
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getCorMarker() {
        return corMarker;
    }

    public void setCorMarker(String corMarker) {
        this.corMarker = corMarker;
    }

    public String getIdPico() {
        return idPico;
    }

    public void setIdPico(String idPico) {
        this.idPico = idPico;
    }

    public void salvar(){
        // salvar pico no db
        DatabaseReference picoRef = database.child("picos");
        picoRef.child(getIdPico())
                .setValue(this);
    }
}

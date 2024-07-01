package com.droid.app.skaterTrader.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.firebase.NotificationAnuncio;
import com.droid.app.skaterTrader.firebase.RemoverAnuncio;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Anuncio implements Parcelable{
    private String idAnuncio;

    private String cidade;
    private String titulo;
    private String valor;
    private String DDD;
    private String phone;
    private String desc;
    private String estado;
    private String categoria;
    private List<String> fotos;
    private User user;
    private DatabaseReference database;


    public Anuncio() {
        database = FirebaseRef.getDatabase();
        user = new User();
    }

    public void gerarId(){
        String idAnuncio = database.child("meus_anuncios").push().getKey();
        setIdAnuncio( idAnuncio );
    }

    protected Anuncio(@NonNull Parcel in) {
        idAnuncio = in.readString();
        cidade = in.readString();
        titulo = in.readString();
        valor = in.readString();
        DDD = in.readString();
        phone = in.readString();
        desc = in.readString();
        estado = in.readString();
        categoria = in.readString();
        fotos = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(idAnuncio);
        dest.writeString(cidade);
        dest.writeString(titulo);
        dest.writeString(valor);
        dest.writeString(DDD);
        dest.writeString(phone);
        dest.writeString(desc);
        dest.writeString(estado);
        dest.writeString(categoria);
        dest.writeStringList(fotos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Anuncio> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Anuncio createFromParcel(Parcel in) {
            return new Anuncio(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Anuncio[] newArray(int size) {
            return new Anuncio[size];
        }
    };



    // salvar ou editar anúncio
    public void salvarAnuncioNoDB(Context context) {
        // salvar anuncio privado para o usuario
        DatabaseReference anuncioRef = database.child("meus_anuncios");
        anuncioRef.child(user.getIdUser())
                .child(getIdAnuncio())
                .setValue(this);

        // salvar anuncio Publico para todos usuario ver
        DatabaseReference anuncioPublicoRef = database.child("anuncios");
        anuncioPublicoRef.child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);

        NotificationAnuncio.notification(context, getTitulo());
    }

    // remover anúncio
    public void removerAnuncio() {
        database = FirebaseRef.getDatabase();
        user = new User();
        // remover meus anuncios
        RemoverAnuncio.remover(
                user, database,
                getIdAnuncio(),
                getEstado(),
                getCategoria(),
                fotos.size()
        );
    }


    // getters and setters
    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public void setDDD(String ddd) {
        this.DDD = ddd;
    }

    public String getDDD(){
        return DDD;
    }
}

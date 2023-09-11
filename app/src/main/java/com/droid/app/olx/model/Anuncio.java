package com.droid.app.olx.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.droid.app.olx.firebaseRefs.FirebaseRef;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.List;
public class Anuncio implements Parcelable {
    private String idAnuncio;
    private String titulo;
    private String valor;
    private String phone;
    private String desc;
    private String estado;
    private String categoria;
    private List<String> fotos;
    private User user;
    private DatabaseReference database;
    //DatabaseReference meusAnuncioRef;
    public Anuncio() {
        database = FirebaseRef.getDatabase();
        String idAnuncio = database.child("meus_anuncios").push().getKey();
        setIdAnuncio( idAnuncio );
        user = new User();
    }

    protected Anuncio(@NonNull Parcel in) {
        idAnuncio = in.readString();
        titulo = in.readString();
        valor = in.readString();
        phone = in.readString();
        desc = in.readString();
        estado = in.readString();
        categoria = in.readString();
        fotos = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(idAnuncio);
        dest.writeString(titulo);
        dest.writeString(valor);
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

    public static final Creator<Anuncio> CREATOR = new Creator<Anuncio>() {
        @Override
        public Anuncio createFromParcel(Parcel in) {
            return new Anuncio(in);
        }

        @Override
        public Anuncio[] newArray(int size) {
            return new Anuncio[size];
        }
    };

    public void salvarAnuncioNoDB() {
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
    }

    public void removerAnuncio(){
        // remover meus anuncios
        DatabaseReference meuAnuncio = database.child("meus_anuncios")
                                                .child(user.getIdUser())
                                                .child(getIdAnuncio());
        meuAnuncio.removeValue();

        // remover anuncio Publico
        DatabaseReference anuncioPublicoRef = database.child("anuncios")
                                                        .child(getEstado())
                                                        .child(getCategoria())
                                                        .child(getIdAnuncio());
        anuncioPublicoRef.removeValue();

        // remover imagens do anuncio
        StorageReference storage = FirebaseRef.getStorage();
        for (int i=0; i < fotos.size(); i++){
            StorageReference imgAnuncio = storage.child("imagens")
                                                .child("anuncios")
                                                .child(getIdAnuncio())
                                                .child("imagem"+i);
            imgAnuncio.delete().addOnSuccessListener( unused -> {

            }).addOnFailureListener(e -> {

            });
            System.out.println("index "+i);
        }
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
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
}

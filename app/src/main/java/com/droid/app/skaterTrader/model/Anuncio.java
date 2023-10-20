package com.droid.app.skaterTrader.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Contract;

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

        notification(context);
    }

    private void notification(@NonNull Context context) {
        FirebaseMessaging.getInstance().subscribeToTopic("Novo anúncio");

        // criar notificação
        String canal = context.getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, canal)
                .setContentTitle("Novo anúncio.")
                .setContentText("Um novo anúncio foi publicado.")
                .setSmallIcon(R.drawable.ic_notification_)
                .setSound(uriSom)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, notification.build());
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

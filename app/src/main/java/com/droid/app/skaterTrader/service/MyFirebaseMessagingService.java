package com.droid.app.skaterTrader.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.model.Loja;
import com.droid.app.skaterTrader.model.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationCompat.Builder notification;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String titulo = Objects.requireNonNull(message.getNotification()).getTitle();
        String corpoMsg = message.getNotification().getBody();

        assert titulo != null;
        enviarNotificacao(titulo, corpoMsg);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void enviarNotificacao(@NonNull String titulo, String msg){
        //Config para notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(titulo.contains("Atualização disponível") || msg.contains("playstore")){
            final String appPackageName = getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName));

            PendingIntent pendingIntent = PendingIntent
                    .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            // criar notificação
            notification = new NotificationCompat.Builder(this, canal)
                    .setContentTitle( titulo )
                    .setContentText( msg )
                    .setSmallIcon( R.drawable.ic_notification_ )
                    .setSound( uriSom )
                    .setAutoCancel(true)
                    .setContentIntent( pendingIntent );
        }else{
            // criar notificação
            notification = new NotificationCompat.Builder(this, canal)
                    .setContentTitle( titulo )
                    .setContentText( msg )
                    .setSmallIcon( R.drawable.ic_notification_ )
                    .setSound( uriSom )
                    .setAutoCancel(true);
        }


        // recuperar notificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // verificar Versão do Android, a partir do android Oreo para configurar canal de notificação.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel( channel );
        }

        // Eviar a notificação
        notificationManager.notify(0,notification.build() );
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // refencia de loja e User
        Loja loja = new Loja();
        User user = new User();

        // definir token para lojas e usuários
        loja.setToken(token);
        user.setToken(token);

        Log.i("TOKEN >", token );
    }

}

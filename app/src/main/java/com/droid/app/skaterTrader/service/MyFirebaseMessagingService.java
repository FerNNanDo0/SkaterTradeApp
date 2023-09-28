package com.droid.app.skaterTrader.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.droid.app.skaterTrader.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if( message != null){
            String titulo = message.getNotification().getTitle();
            String corpoMsg = message.getNotification().getBody();

            enviarNotificacao(titulo, corpoMsg);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void enviarNotificacao(String titulo, String msg){
        //Config para notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        /*Intent intent = new Intent(this, ActivityMain.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);*/

        // criar notificação
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, canal)
                .setContentTitle( titulo )
                .setContentText( msg )
                .setSmallIcon( R.drawable.ic_notification_ )
                .setSound( uriSom )
                .setAutoCancel(true);
//                .setContentIntent( pendingIntent );

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
    }

}

package com.droid.app.skaterTrader.firebase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.droid.app.skaterTrader.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationAnuncio extends AppCompatActivity {
    public static void notification(@NonNull Context context) {
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

    // permission of notification
    public void askNotificationPermission(Context context) {

        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                Log.i("success", "O usúario deu permissão para notificação");
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // FCM SDK (and your app) can post notifications.
                // Inform user that that your app will not show notifications.

            });
}

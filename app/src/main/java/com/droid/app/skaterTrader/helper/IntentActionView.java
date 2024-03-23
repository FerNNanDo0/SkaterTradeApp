package com.droid.app.skaterTrader.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

public class IntentActionView {

    public static void browseTo(Activity activity){
        try {
            String url = "https://skatertrade.web.app/política-de-privacidade.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sharedApp(@NonNull Activity activity){
        String urlApp = "https://play.google.com/store/apps/details?id=com.droid.app.skaterTrader";
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, urlApp);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Compartilhar App");
        activity.startActivity(shareIntent);
    }
}

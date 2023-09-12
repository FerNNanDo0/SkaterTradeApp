package com.droid.app.skaterTrader.helper;

import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;

public class ConnectWhatsApp implements ConnectWhats {
    final String urlBase = "https://wa.me/";
    Context context;

    public ConnectWhatsApp(Context context){
        this.context = context;
    }

    @Override
    public void initMsg(String numero, String msg) {
        String chatMsgWhats = (urlBase + numero+"/?text=" + msg);
        context.startActivity(
                new Intent( Intent.ACTION_VIEW, Uri.parse( chatMsgWhats.trim() ))
        );
    }
}

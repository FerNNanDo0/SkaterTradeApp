package com.droid.app.skaterTrader.service;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.droid.app.skaterTrader.R;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class InitSdkAdmob {
    public static NativeAd Ad;

    public static NativeAd getAd() {
        return Ad;
    }
    public static void initSdkAdmob(Activity activity, TemplateView template) {

        // ca-app-pub-4810475836852520/5974368133 -> id anuncio nativo
        // ca-app-pub-3940256099942544/2247696110 -> teste
        final AdLoader adLoader = new AdLoader.Builder(activity, "ca-app-pub-4810475836852520/5974368133")
                .forNativeAd(nativeAd -> {
                    Ad = nativeAd;

                    // Show the ad.
                    template.setVisibility(View.VISIBLE);
                    template.setNativeAd(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        template.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded() {
                        // metodo chamado quando o anúncio é carregado
                    }

                    @Override
                    public void onAdOpened() {
                        // metodo chamado quando o anúncio éaberto
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // usar ess metodo para alterar a interface ou apenas registrar a falha
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        // Este método envia uma solicitação para um único anúncio.
        adLoader.loadAd(new AdRequest.Builder().build());

        // Este método envia uma solicitação para vários anúncios (até cinco):
        //adLoader.loadAds(new AdRequest.Builder().build(), 5);
    }

}

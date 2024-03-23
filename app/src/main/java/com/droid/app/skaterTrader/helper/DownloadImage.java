package com.droid.app.skaterTrader.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.droid.app.skaterTrader.viewModel.ViewModelCadastroAnuncio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadImage {

    static  byte[] dadosImg;
    public static void download(String url, ViewModelCadastroAnuncio viewModel) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute( () -> {

            try {
                URL urlObj = new URL(url);

                HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection();

                httpConnection.setRequestMethod("GET");
                InputStream inputStream = httpConnection.getInputStream();

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                dadosImg = ConfigDadosImgBitmap.recuperarDadosIMG(imgBitmap);

                Log.i("TAG",">>> imgBitmap >>> "+imgBitmap+"\n>>> dadosImg >>> "+dadosImg);

                handler.post(() -> {

                    if(dadosImg != null){
                        viewModel.setByteImg(dadosImg);
                    }

                });
            } catch (IOException ex) {
                ex.getMessage();
            }

        });
    }
}

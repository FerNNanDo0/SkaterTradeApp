package com.droid.app.skaterTrader.helper;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class ConfigDadosImgBitmap {

    //reuperar dados da img para o firebase
    @NonNull
    public static byte[] recuperarDadosIMG(@NonNull Bitmap imgBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();
    }
}

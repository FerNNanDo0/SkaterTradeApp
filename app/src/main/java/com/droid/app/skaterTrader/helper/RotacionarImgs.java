package com.droid.app.skaterTrader.helper;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;
public class RotacionarImgs {
    static Bitmap imgEdit;
    public static Bitmap rotacionarIMG(@NonNull Bitmap imgBitmap, Uri imgSelected, Activity activity) throws IOException {
    // verificar orientação da img
        int rotate = 0;
        String path = getImagePath(imgSelected, activity);

        if(path != null){
            ExifInterface exif = new ExifInterface(path);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270-180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180/2;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.preRotate(rotate);
            imgEdit = Bitmap.createBitmap(imgBitmap, 0, 0,
                    imgBitmap.getWidth(),
                    imgBitmap.getHeight(),
                    matrix, true
            );
        }
        return imgEdit;
    }
    private static String getImagePath(Uri contentUri, @NonNull Activity activity) {
        String[] campos = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, campos, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }
}
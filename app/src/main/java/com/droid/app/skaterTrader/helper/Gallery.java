package com.droid.app.skaterTrader.helper;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

public class Gallery {
    public static void open(Activity activity, int requestCode){
        try{
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            activity.startActivityIfNeeded(i, requestCode);
            // startActivityForResult(i, requestCode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

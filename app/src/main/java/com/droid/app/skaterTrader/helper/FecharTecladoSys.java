package com.droid.app.skaterTrader.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public class FecharTecladoSys {
    public static void closedKeyBoard(@NonNull Activity activity){
        View view = activity.getWindow().getCurrentFocus();
        if(view != null){
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(),0);
        }
    }
}

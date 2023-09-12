package com.droid.app.skaterTrader.helper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

public class Informe {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String NOME_ARQ = "anotações.preferencias";
    private static final String CHAVE_NOME = "informe";

    public static void salvarCode(String code,Context context){
        config(context);

        editor.putString(CHAVE_NOME, code);
        editor.commit();
    }
    public static String recuperarCode(Context context){
        config(context);

        return sharedPreferences.getString(CHAVE_NOME, "");
    }
    private static void config(@NonNull Context context){
        sharedPreferences = context.getSharedPreferences( NOME_ARQ , 0 );
        editor = sharedPreferences.edit();
    }

}

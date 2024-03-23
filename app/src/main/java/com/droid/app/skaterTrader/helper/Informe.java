package com.droid.app.skaterTrader.helper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

public class Informe {
    private static SharedPreferences sharedPreferences, sharedPreferences2;
    private static SharedPreferences.Editor editor, editor2;
    private static final String NOME_ARQ = "anotações.preferencias";
    private static final String CHAVE_NOME = "informe";


    // salvar code para informe de permissões
    public static void salvarCodePermission(String code, Context context){
        config(context);

        editor.putString(CHAVE_NOME, code);
        editor.commit();
    }
    public static String recuperarCodePermission(Context context){
        config(context);

        return sharedPreferences.getString(CHAVE_NOME, "");
    }

    private static void config(@NonNull Context context){
        sharedPreferences = context.getSharedPreferences( NOME_ARQ , 0 );
        editor = sharedPreferences.edit();
    }


    // salvar code para informe de Status de pico no mapa
    public static void salvarCodeStatusPico(String code,Context context){
        config2(context);

        editor2.putString(CHAVE_NOME, code);
        editor2.commit();
    }

    public static String recuperarCodeStatusPico(Context context){
        config2(context);

        return sharedPreferences2.getString(CHAVE_NOME, "");
    }

    private static void config2(@NonNull Context context){
        sharedPreferences2 = context.getSharedPreferences( NOME_ARQ , 0 );
        editor2 = sharedPreferences2.edit();
    }
}
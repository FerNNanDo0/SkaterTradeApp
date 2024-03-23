package com.droid.app.skaterTrader.helper;

import androidx.annotation.NonNull;

public class RemoveChars {

    @NonNull
    public static String removerCharacters(@NonNull String str) {
        String newStr = str.replace(".","");
        newStr = newStr.replace("/", "");
        newStr = newStr.replace("-", "");
        return newStr;
    }
}
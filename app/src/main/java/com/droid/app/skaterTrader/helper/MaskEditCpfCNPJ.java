package com.droid.app.skaterTrader.helper;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public abstract class MaskEditCpfCNPJ {
    private static final String FORMAT_CNPJ = "##.###.###/####-##";
    private static final String FORMAT_CPF = "###.###.###-##";

    @NonNull
    public static String unmask(@NonNull String s) {
        return s.replaceAll("\\D*", "");
    }

    @NonNull
    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0){
                    editText.setTextColor(Color.BLACK);
                }
                String str = MaskEditCpfCNPJ.unmask(s.toString());
                String mask;
                String defaultMask = getDefaultMask(str);
                switch (str.length()) {
                    case 14:
                        mask = FORMAT_CNPJ;
                        break;

                    case 11:
                        mask = FORMAT_CPF;
                        break;

                    default:
                        mask = defaultMask;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ( (m != '#' && str.length() > old.length()) ||
                         (m != '#' && str.length() < old.length() && str.length() != i)
                    ) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    @Contract(pure = true)
    private static String getDefaultMask(@NonNull String str) {
        String defaultMask = FORMAT_CPF;
        if (str.length() > 11){
            defaultMask = FORMAT_CNPJ;
        }
        return defaultMask;
    }
}
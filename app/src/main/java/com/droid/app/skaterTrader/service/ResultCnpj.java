package com.droid.app.skaterTrader.service;

import com.droid.app.skaterTrader.model.ModelCnpj;

public interface ResultCnpj {
    void onRequestDadosCnpj(ModelCnpj modelCnpj);
    void onErroRequestCnpj(String erro);
}
package com.droid.app.skaterTrader.api;

import com.droid.app.skaterTrader.model.ModelCep;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CEPService {
    @GET("{cep}/json/")
    Call<ModelCep> getService(@Path("cep") String cep);
}

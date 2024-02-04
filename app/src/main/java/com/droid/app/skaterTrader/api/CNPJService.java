package com.droid.app.skaterTrader.api;

import com.droid.app.skaterTrader.model.cnpj.ModelCnpj;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface CNPJService {
    String Authorization = "f7fc0266-e337-4158-b957-4f01b9e8bfbc-b6aa315b-2629-41c2-9f00-ce227deb61a3";
    @GET("{cnpj}/json/")
    Call<ModelCnpj> getService(@Path("cnpj") String cnpj );
}

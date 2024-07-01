package com.droid.app.skaterTrader.service;

import static com.droid.app.skaterTrader.helper.RemoveChars.removerCharacters;
import androidx.annotation.NonNull;
import com.droid.app.skaterTrader.model.ModelCnpj;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class VerificarCNPJ {
    private final String TOKEN = "f7fc0266-e337-4158-b957-4f01b9e8bfbc-b6aa315b-2629-41c2-9f00-ce227deb61a3";
    static Request request;
    static OkHttpClient client;
    ModelCnpj cnpjModel;
    ResultCnpj resultCnpj;

    public VerificarCNPJ( String cnpj , ResultCnpj resultCnpj ) {
        this.resultCnpj = resultCnpj;
        getServiceCNPJ(cnpj);
    }

    private void getServiceCNPJ(String cnpj) {
        // iniciar client
        client = new OkHttpClient().newBuilder().build();

        //remover caracters especiais
        String newCnpj = removerCharacters(cnpj);

        String url = "https://api.cnpja.com/office/"+newCnpj+"?simples=true&simplesHistory=true";
        request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", TOKEN)
                .build();

        client.newCall(request).enqueue( new RequestCnpj() );

    }

    private void setDadosConsultaCNPJ(@NonNull JSONObject jsonObject) {
        cnpjModel = new ModelCnpj();
        try {

            // recupera o endereço
            String address = jsonObject.getString("address");
            JSONObject jsonAdress = new JSONObject(address);
            String cidade = jsonAdress.getString("city");
            String estado = jsonAdress.getString("state");
            String rua = jsonAdress.getString("street");
            String numero = jsonAdress.getString("number");
            String cep = jsonAdress.getString("zip");

            // recupera o pais
            String country = jsonAdress.getString("country");
            JSONObject jsonCoutry = new JSONObject(country);
            String pais = jsonCoutry.getString("name");

            /// recupera info da empresa
            String company = jsonObject.getString("company");
            JSONObject jsonCompany = new JSONObject(company);
            String companyBody = jsonCompany.getString("name");

            // definir dados loja e cnpj
            cnpjModel.setCidade(cidade);
            cnpjModel.setEstado(estado);
            cnpjModel.setRua(rua);
            cnpjModel.setNumero(numero);
            cnpjModel.setCep(cep);
            cnpjModel.setPais(pais);
            cnpjModel.setCompanyBody(companyBody);

            resultCnpj.onRequestDadosCnpj(cnpjModel);

        } catch (JSONException e) {
            resultCnpj.onErroRequestCnpj("ERRO 3");
            //configBtnAndProgress(true, View.GONE);
            throw new RuntimeException(e);
        }
    }


    // class Callback para requisição CNPJ
    private class RequestCnpj implements Callback {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            /*exibirToast("Houve uma falha na comunicação ao tentar verificar o CNPJ, tente mais tarde!");
            editCpfOrCnpj.requestFocus();
            editCpfOrCnpj.setTextColor(Color.RED);
            configBtnAndProgress(true, View.GONE);*/
            resultCnpj.onErroRequestCnpj("ERRO 1");
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

            if(response.body() != null){

                String body = Objects.requireNonNull(response.body()).string();

                try {
                    JSONObject jsonObject = new JSONObject(body);

                    // verificar se CNPJ e valido e se ha codigo de erro
                    if(jsonObject.has("code")){

                        System.out.println(">> "+jsonObject.getString("code"));
                        /*exibirToast("CNPJ Inválido");
                        editCpfOrCnpj.requestFocus();
                        editCpfOrCnpj.setTextColor(Color.RED);
                        configBtnAndProgress(true, View.GONE);*/

                        resultCnpj.onErroRequestCnpj("ERRO 2");

                    }else{
                        // tratar os dados json
                        System.out.println(">> "+jsonObject);
                        setDadosConsultaCNPJ(jsonObject);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
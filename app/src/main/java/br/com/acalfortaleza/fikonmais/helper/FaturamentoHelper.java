package br.com.acalfortaleza.fikonmais.helper;

import android.util.Log;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.FaturamentoLinha;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import br.com.acalfortaleza.fikonmais.models.Faturamento;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class FaturamentoHelper {


    private OkHttpClient client;
    private Retrofit retrofit;
    private volatile Call<Faturamento> consultaFaturamentoAtual;


    public FaturamentoHelper() {


        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


       public Faturamento GetFaturamento(String dtinicial, String dtfinal, int codvendr, String filial) throws IOException, XmlPullParserException, ParseException {
        final ApiGemco service = retrofit.create(ApiGemco.class);
        final String filialNormalizada = filial == null ? "" : filial.trim();
        Response<Faturamento> response = executarGetFaturamento(service, dtinicial, dtfinal, codvendr, filialNormalizada);
        Faturamento body = response.body();

        if (body == null && !filialNormalizada.isEmpty()) {
            Log.w("FaturamentoDebug", "Consulta sem retorno com filial. Reprocessando sem filial."
                    + " | codvendr=" + codvendr
                    + " | filial=" + filialNormalizada);
            response = executarGetFaturamento(service, dtinicial, dtfinal, codvendr, "");
            body = response.body();
        }

        return body;

    }

    public List<FaturamentoLinha> GetFaturamentoLinha(String dtinicial, String dtfinal, int codvendr) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<FaturamentoLinha>> request= service.GetFaturamentoLinha( dtinicial, dtfinal, codvendr, "Bearer " + service.token);

        return request.execute().body();

    }

    public void cancelarConsultaFaturamentoAtual() {
        final Call<Faturamento> request = consultaFaturamentoAtual;
        if (request != null && !request.isCanceled()) {
            request.cancel();
            Log.w("FaturamentoDebug", "GetFaturamento cancelado pelo usuario");
        }
    }



    private Response<Faturamento> executarGetFaturamento(ApiGemco service, String dtinicial, String dtfinal, int codvendr, String filial) throws IOException {
        final String endpoint = "api/v1/Faturamento";
        final String url = ApiGemco.BASE_URL + endpoint;

        Log.d("FaturamentoDebug",
                "GetFaturamento envio"
                        + " | endpoint=" + endpoint
                        + " | url=" + url
                        + " | dtinicial=" + dtinicial
                        + " | dtfinal=" + dtfinal
                        + " | codvendr=" + codvendr
                        + " | filial=" + filial
                        + " | authPrefix=Bearer");

        final Call<Faturamento> request = service.GetFaturamento(dtinicial, dtfinal, codvendr, filial, "Bearer " + service.token);
        consultaFaturamentoAtual = request;
        try {
            final Response<Faturamento> response = request.execute();
            String errorBody = null;
            String bodyJson = response.body() != null ? new Gson().toJson(response.body()) : "null";

            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }

            Log.d("FaturamentoDebug",
                    "GetFaturamento retorno"
                            + " | endpoint=" + endpoint
                            + " | url=" + url
                            + " | dtinicial=" + dtinicial
                            + " | dtfinal=" + dtfinal
                            + " | codvendr=" + codvendr
                            + " | filial=" + filial
                            + " | http=" + response.code()
                            + " | sucesso=" + response.isSuccessful()
                            + " | body=" + bodyJson
                            + " | errorBody=" + String.valueOf(errorBody));

            return response;
        } finally {
            if (consultaFaturamentoAtual == request) {
                consultaFaturamentoAtual = null;
            }
        }
    }
}

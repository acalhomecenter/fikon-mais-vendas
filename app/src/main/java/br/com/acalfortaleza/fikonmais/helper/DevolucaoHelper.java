package br.com.acalfortaleza.fikonmais.helper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import br.com.acalfortaleza.fikonmais.models.Devolucao;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DevolucaoHelper {



    private OkHttpClient client;
    private Retrofit retrofit;


    public DevolucaoHelper() {


        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


    public List<Devolucao> GetDevolucoes(int codvendr) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Devolucao>> request= service.GetDevolucoes(  codvendr,  "Bearer " + service.token);

        return request.execute().body();

    }


}

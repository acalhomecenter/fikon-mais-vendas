package br.com.acalfortaleza.fikonmais.helper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaHelper {
    private Retrofit retrofit;
    private List<ProdutoGemco> produtos;
    private OkHttpClient client;


    public  ConsultaHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    public List<ProdutoGemco> ConsultarProduto(String descricao, String codFilPreco) throws IOException, XmlPullParserException, ParseException {


            final ApiGemco service = retrofit.create(ApiGemco.class);


            final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemco("TEXTO", descricao, codFilPreco, "Bearer " + service.token);

                        produtos = requestProduto.execute().body();


                        return produtos;

    }

}

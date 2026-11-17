package br.com.acalfortaleza.fikonmais.helper;

import android.util.Log;

import com.google.gson.JsonObject;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Campanha;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.IndicadorCampanha;
import br.com.acalfortaleza.fikonmais.models.Rank;
import br.com.acalfortaleza.fikonmais.models.Regulamento;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CampanhaHelper {

    private Retrofit retrofit;
    private OkHttpClient client;


    public  CampanhaHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    public List<Campanha> ConsultaPontosDetalhado(int codvendr, int codfil){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<List<Campanha>> request = service.GetPontosDetalhado( codvendr, codfil, "Bearer " + service.token);

            Response<List<Campanha>> response = request.execute();

            if(response.isSuccessful()){
                return response.body();
            }

            return  new ArrayList<>();

        }
        catch (Exception ex){

        }

        return null;

    }

    public List<Rank> ConsultaRank(int codvendr, int codfil){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<List<Rank>> request = service.GetRank( codvendr, codfil, "Bearer " + service.token);

            Response<List<Rank>> response = request.execute();

            if(response.isSuccessful()){
                return response.body();
            }

            return new ArrayList<>();

        }
        catch (Exception ex){

        }

        return null;

    }

    public List<Regulamento> ConsultaRegulamento() throws ParseException, XmlPullParserException, IOException {

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<List<Regulamento>> request = service.GetRegulamento("Bearer " + service.token);

            List<Regulamento> listaRegulamento = request.execute().body();

            return listaRegulamento;

        }
        catch (Exception ex){

        }

        return null;

    }

    public String ConsultaAtualizacao(int codigoConsultor){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<JsonObject> request = service.GetAtualizacaoCampanha(codigoConsultor, "Bearer " + service.token);

            JsonObject retorno = request.execute().body();

            return retorno.get("Result").toString();

        }
        catch (Exception ex){

        }

        return null;

    }


}

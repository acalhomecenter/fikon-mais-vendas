package br.com.acalfortaleza.fikonmais.helper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Campanha;
import br.com.acalfortaleza.fikonmais.models.Encomenda;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EncomendaHelper {


   private Retrofit retrofit;
   private OkHttpClient client;


   public  EncomendaHelper(){

      client = new OkHttpClient.Builder()
              .connectTimeout(60, TimeUnit.SECONDS)
              .readTimeout(60,TimeUnit.SECONDS).build();
      retrofit = new Retrofit.Builder()
              .baseUrl(ApiGemco.BASE_URL)
              .client(client)
              .addConverterFactory(GsonConverterFactory.create())
              .build();

   }



   public List<Encomenda> ConsultaEncomenda(int codvendr, int codfil) throws IOException, XmlPullParserException, ParseException {

      try {

         ApiGemco service = retrofit.create(ApiGemco.class);

         Call<List<Encomenda>> request = service.GetEncomendas( codvendr, codfil, "Bearer " + service.token);

         Response<List<Encomenda>> response = request.execute();

         if(response.isSuccessful()){
            return response.body();
         }

         return  new ArrayList<>();

      }
      catch (Exception ex){

      }

      return null;

   }

}

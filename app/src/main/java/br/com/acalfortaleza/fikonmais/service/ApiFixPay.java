package br.com.acalfortaleza.fikonmais.service;

import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.PixRequest;
import br.com.acalfortaleza.fikonmais.models.PixResponse;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Query;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiFixPay {

    public static final String BASE_URL = "https://pix.fixpay.com.br:3467/v1/";


    @POST("generate_pix")
    Call<PixResponse> gerarPix(@Body PixRequest pixrequest, @Header("Authorization") String auth);

}

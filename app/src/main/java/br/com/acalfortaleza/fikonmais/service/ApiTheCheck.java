package br.com.acalfortaleza.fikonmais.service;

import java.util.List;


import br.com.acalfortaleza.fikonmais.models.TheChecker;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiTheCheck {


    public static final String BASE_URL = "https://api.thechecker.co";
    public static final String KEY = "eed9324955b76ec9496cc24cbf108a9a23c004404bce3c313e8d3e3d62f7cccd";


    @GET("/v2/verify")
    Call<TheChecker> validarEmail(@Query("email") String email, @Query("api_key") String auth);

}

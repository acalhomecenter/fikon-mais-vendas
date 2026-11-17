package br.com.acalfortaleza.fikonmais.service;

import br.com.acalfortaleza.fikonmais.models.UserFluig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiFluig {

    public static final String BASE_URL = "http://fluig.acalhomecenter.com.br/api/public/";
    public static final String KEY = "t1t2t3";
    public static final String TOKEN = "66386ca3-24f1-4066-b896-63a55f638934";
    public static final String SECRET = "abcdefghi";
    public static final String TOKENSECRET = "50248b51-2439-4a21-868d-48593478d62a4a469cef-e9a6-401d-9481-6fc599c7588e";




    @GET("2.0/users/getUser/{cpf}")
    Call<UserFluig> GetUserFluig(@Path("cpf") String cpf);


    @GET("ecm/document/activeDocumentFile/{Id}")
    Call<ResponseBody> GetImagemNoticia(@Path("Id") String Id);



    @GET("social/image/profile/{Id}/MEDIUM_PICTURE")
    Call<ResponseBody> GetImagemProfile(@Path("Id") String id);


}

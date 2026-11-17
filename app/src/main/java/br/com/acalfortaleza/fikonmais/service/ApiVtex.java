package br.com.acalfortaleza.fikonmais.service;

import java.util.List;

import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;
import br.com.acalfortaleza.fikonmais.models.SugestaoProdutoVtex;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiVtex {

    public static final String BASE_URL = "http://lojaacal.vtexcommercestable.com.br/";
    public static final String header_appkey = "x-vtex-api-appkey";
    public static final String appkey = "vtexappkey-lojaacal-YIMPVD";
    public static final String header_token = "x-vtex-api-apptoken";
    public static final String token = "MGZYAWTCRCYOOSZPZHLREODMGUGFKSOYPIPWBGAHWJWXHXMUEGDVPQQXCNSFPNRYOZYFCDOTGDVUSGFIIMADPSJYSSTNOVRIRXIHYHRBRKPLVVFWIJXDNNJWPZMKJAEK";



    @GET("api/catalog_system/pvt/sku/stockkeepingunitbyid/{skuid}")
    Call<ProdutoVtex> getProdutoVtex(@Path("skuid") String skuid , @Header(header_appkey) String appkey, @Header(header_token) String token);

    @GET("api/catalog_system/pub/products/crossselling/suggestions/{productId}")
    Call<List<SugestaoProdutoVtex>> getSugestoesProdutoVtex(@Path("productId") String productId, @Header(header_appkey) String appkey, @Header(header_token) String token);


}

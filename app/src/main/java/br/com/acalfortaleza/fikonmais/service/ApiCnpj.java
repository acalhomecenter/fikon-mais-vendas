package br.com.acalfortaleza.fikonmais.service;

import br.com.acalfortaleza.fikonmais.models.PessoaJuridica;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiCnpj {

    public static final String BASE_URL = "https://www.receitaws.com.br/";

    @GET("v1/cnpj/{cnpj}")
    Call<PessoaJuridica> ConsultaCnpj(@Path("cnpj") String cnpj);

}

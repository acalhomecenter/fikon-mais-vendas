package br.com.acalfortaleza.fikonmais.helper;

import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Credz.Invitation;
import br.com.acalfortaleza.fikonmais.models.Credz.Plan;
import br.com.acalfortaleza.fikonmais.models.Credz.PlanSimulation;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CredzHelper {

    private Retrofit retrofit;
    private OkHttpClient client;

    public CredzHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public Invitation EnviarInvitation(Cliente cliente, String tipo, int codfil){

        try{

            final ApiGemco.Credz service = retrofit.create(ApiGemco.Credz.class);

            final Call<Invitation> invitationCall = service.EnviarInvitation( cliente, tipo, codfil, "Bearer " + ApiGemco.token);

            Response<Invitation> response = invitationCall.execute();

            if(response.isSuccessful()) {
                return response.body();
            }

            return new Gson().fromJson(new Gson().toJson(response.errorBody()), Invitation.class);

        }
        catch (Exception ex){
            Log.e(this.getClass().toString(),"Erro na função EnviarInvitation",ex);
        }

        return null;

    }

    public PlanSimulation RealizarSimulacao(String cpf, String numpedven, float vltotal, float vlentrada, int codfil){

        try{

            final ApiGemco.Credz service = retrofit.create(ApiGemco.Credz.class);

            final Call<PlanSimulation> simulationCall = service.RealizarSimulacaoPlanos(cpf, numpedven, vltotal, vlentrada, codfil, "Bearer " + ApiGemco.token);

            Response<PlanSimulation> response = simulationCall.execute();

            if(response.isSuccessful()) {
                return response.body();
            }

            return new Gson().fromJson(new Gson().toJson(response.errorBody()), PlanSimulation.class);

        }
        catch (Exception ex){
            Log.e(this.getClass().toString(),"Erro na função RealizarSimulacao",ex);
        }

        return null;

    }

    public String GravarPlanId(String planid, String numpedeven){

        try{

            final ApiGemco.Credz service = retrofit.create(ApiGemco.Credz.class);

            final Call<String> planCall = service.GravarPlanId(planid, numpedeven, "Bearer " + ApiGemco.token);

            Response<String> response = planCall.execute();

            if(response.isSuccessful()){
                return response.body();
            }

        }
        catch (Exception ex){
            Log.e(this.getClass().toString(),"Erro na função GravarPlanId", ex);
        }

        return null;

    }

}

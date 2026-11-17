package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.FreteServicoInfoAdapter;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.FreteServico;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupInfoFreteServicos extends Activity {

    private RecyclerView mRecyclerView;
    private FreteServicoInfoAdapter adapter;
    private List<FreteServico> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_info_frete_servicos);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .7));

        mRecyclerView = findViewById(R.id.pop_info_frete_lista);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PopupInfoFreteServicos.this));
        adapter = new FreteServicoInfoAdapter(PopupInfoFreteServicos.this, mList);
        mRecyclerView.setAdapter(adapter);

        android.widget.Button btnClose = findViewById(R.id.pop_info_frete_btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        carregarServicos();
    }

    private void carregarServicos(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiGemco api = retrofit.create(ApiGemco.class);
        String token = "Bearer " + ApiGemco.token;
        Call<List<FreteServico>> call = api.GetFreteServico(token);
        call.enqueue(new Callback<List<FreteServico>>() {
            @Override
            public void onResponse(Call<List<FreteServico>> call, Response<List<FreteServico>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    List<FreteServico> data = response.body();
                    List<FreteServico> filtrados = new ArrayList<>();
                    for(FreteServico fs : data){
                        if (fs.getStatus() != null && fs.getStatus().equals("1")){
                            filtrados.add(fs);
                        }
                    }
                    mList = filtrados;
                    adapter.setData(filtrados);
                }
            }

            @Override
            public void onFailure(Call<List<FreteServico>> call, Throwable t) {
            }
        });
    }
}

package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.MainActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Usuario;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.ImageView.ScaleType.FIT_XY;

public class MainHelper {

    private CircleImageView profile;
    private Bitmap bitmap;
    private OkHttpClient client;
    private Retrofit retrofit;




    public MainHelper(View view) {


        profile =  view.findViewById(R.id.nav_profile);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


    public void carregarImagem(String caminhoFoto) {
        if(caminhoFoto != null) {

            bitmap = BitmapFactory.decodeFile(caminhoFoto);

            if(bitmap != null){
                profile.invalidate();
                profile.setImageBitmap(bitmap);


            }

        }
    }




    public String UploadAvatar (MultipartBody.Part file, RequestBody filename) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<String> upload = service.UploadAvatar(file,filename,"Bearer " + service.token);
        upload.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



        return "ok";

    }





}

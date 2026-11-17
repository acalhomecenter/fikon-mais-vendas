package br.com.acalfortaleza.fikonmais.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.ImagemCiashop;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;


public class ImageDialogFragment extends DialogFragment {

    private int COUNT_PLUS = 1;
    private int COUNT_LESS = -1 ;
    public static String KEY = "image_dialog";
    private View layout;
    private ImageView iv_close;
    private ImageView iv_arrow_left;
    private ImageView iv_arrow_right;
    private PhotoView iv_image;
    private int imagePosition =0;
    private List<ProdutoVtex.Images> imagens;
    private String img_url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(
        DialogFragment.STYLE_NORMAL,
        android.R.style.Theme_Black_NoTitleBar_Fullscreen);


        if (getArguments() != null) {


            imagens = (List<ProdutoVtex.Images>) getArguments().getSerializable("imagens");
            img_url = getArguments().getString("img_url");



            }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_imagedialog,
                container, false);

        iv_close = layout.findViewById(R.id.iv_close);
        iv_arrow_left = layout.findViewById(R.id.iv_arrow_left);
        iv_arrow_right = layout.findViewById(R.id.iv_arrow_right);
        iv_image = layout.findViewById(R.id.iv_image);


        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        iv_arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextImage();
            }
        });

        iv_arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevImage();
            }
        });

        //iv_image.setMinimumScale(1.0f);
        //iv_image.setMediumScale(1.75f);
        iv_image.setMaximumScale(5.0f);
        //iv_image.setScale(1.55f,true);

        setImage(false,0);

    }

    private void prevImage() {
        setImage( true, COUNT_LESS );
    }

    private void nextImage() {
        setImage( true, COUNT_PLUS );
    }



    private void setImage( Boolean applyCount, int typeCount ){

        if(applyCount ) {

                imagePosition = imagePosition + typeCount;

        }

        if(img_url != null && !img_url.equals("")){
            Picasso
                    .get()
                    .load( img_url )
                    .into( iv_image );

        }else{
            Picasso
                    .get()
                    .load( imagens.get(imagePosition).ImageUrl )
                    .into( iv_image );
            verifyButtons();

        }




    }


    private void verifyButtons() {

        if( imagePosition == 0 )
           iv_arrow_left.setVisibility(View.GONE);
        else
           iv_arrow_left.setVisibility(View.VISIBLE);



        if( imagePosition == imagens.size() - 1)
            iv_arrow_right.setVisibility(View.GONE);
        else
            iv_arrow_right.setVisibility(View.VISIBLE);
    }

    private void close() {
        dismiss();
    }
}

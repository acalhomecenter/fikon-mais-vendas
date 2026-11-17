package br.com.acalfortaleza.fikonmais.adapters;



import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.fragments.ImageDialogFragment;
import br.com.acalfortaleza.fikonmais.models.ImagemCiashop;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;

public class SlideAdapter extends PagerAdapter {


    private Context context;
    LayoutInflater inflater;
    private List<ProdutoVtex.Images> mList;
    private ImageView img;
    private Bundle bundle;


    public SlideAdapter(Context context, List<ProdutoVtex.Images> mList) {
        this.context = context;
        this.mList = mList == null ? new ArrayList<>() : new ArrayList<>(mList);
        Collections.sort(this.mList, new Comparator<ProdutoVtex.Images>() {
            @Override
            public int compare(ProdutoVtex.Images left, ProdutoVtex.Images right) {
                if (left == null || right == null || left.Principal == right.Principal) {
                    return 0;
                }
                return left.Principal ? -1 : 1;
            }
        });
        bundle = new Bundle();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout)object);


    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slidshow_layout,container,false);

        img = view.findViewById(R.id.imageView_id);

        Picasso.get().load(mList.get(position).ImageUrl).placeholder(R.drawable.semimagem)
                .fit()
                .centerInside()
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        img.setImageResource(R.drawable.semimagem);

                    }


                });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // if ( !mList.get(0).getImagePath().equals("Sem foto")) {

                    bundle.putSerializable("imagens", (Serializable) mList);
                    ImageDialogFragment fragment = new ImageDialogFragment();
                    fragment.setArguments(bundle);
                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    fragment.show(transaction, ImageDialogFragment.KEY);
              //  }

            }
        });

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);


    }
}

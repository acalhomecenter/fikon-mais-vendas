package br.com.acalfortaleza.fikonmais.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.RankAdapter;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Campanha;
import br.com.acalfortaleza.fikonmais.models.Rank;

public class RankFragments extends Fragment {

    public RankFragments(){

    }


    private View layout;
    private List<Rank> listRank;
    private RecyclerView recyclerRanking;
    private RankAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            listRank = (List<Rank>) getArguments().getSerializable("rank");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.fragment_rank,
                container, false);

        recyclerRanking = layout.findViewById(R.id.camp_lista_rank);
        recyclerRanking.setHasFixedSize(true);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new RankAdapter(getContext(), listRank);
        recyclerRanking.setAdapter(adapter);

        return layout;

    }

}

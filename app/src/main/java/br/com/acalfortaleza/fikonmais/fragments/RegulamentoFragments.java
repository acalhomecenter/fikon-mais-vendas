package br.com.acalfortaleza.fikonmais.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.RegulamentoAdapter;
import br.com.acalfortaleza.fikonmais.models.Regulamento;

public class RegulamentoFragments extends Fragment {

    public RegulamentoFragments(){

    }


    private View layout;
    private List<Regulamento> regulamentos;

    private RecyclerView listaRegulamento;
    private RegulamentoAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            regulamentos = (List<Regulamento>) getArguments().getSerializable("regulamento");


        }
    }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {


            layout = inflater.inflate(R.layout.fragment_regulamento,
                    container, false);

            listaRegulamento = layout.findViewById(R.id.listaRegulamento);
            listaRegulamento.setHasFixedSize(true);
            listaRegulamento.setLayoutManager(new LinearLayoutManager(getActivity()));

            adapter = new RegulamentoAdapter(getContext(), regulamentos);
            listaRegulamento.setAdapter(adapter);

            return layout;

        }






}

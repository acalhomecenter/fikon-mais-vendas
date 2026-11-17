package br.com.acalfortaleza.fikonmais.fragments;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Cliente;


    public class ClientesHomeFragments extends Fragment {

        private View layout;
        private HashMap<String, String> usuarioLogado;
        private Handler handler;
        private List<Cliente> listClientes;
        private LoginHelper loginHelper;




        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            loginHelper = new LoginHelper(getActivity());
            usuarioLogado = loginHelper.GetUsuarioSalvo();
            handler = new Handler();

        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


            layout = inflater.inflate(R.layout.fragment_home_clientes,
                    container, false);


            return layout;

        }

}

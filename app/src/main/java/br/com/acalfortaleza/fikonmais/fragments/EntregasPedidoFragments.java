package br.com.acalfortaleza.fikonmais.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.EntregasConsultaAdapter;
import br.com.acalfortaleza.fikonmais.helper.EntregasHelper;
import br.com.acalfortaleza.fikonmais.models.Entregas;

public class EntregasPedidoFragments extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private View layout;
    private ListView listaEntregas;
    private String pedido;
    private EntregasConsultaAdapter adapterConsulta;
    private List<Entregas> listEntregas;
    private Handler handler;
    private ProgressDialog dialog;
    private EntregasHelper helper;
    private AlertDialog alertaDialog;
    private Entregas entrega;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        pedido = getArguments().getString("pedido");
        handler = new Handler();
        helper = new EntregasHelper();

        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_entregas_pedido,
                container, false);

        listaEntregas = layout.findViewById(R.id.listaEntregasPedido);

        this.listaEntregas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long l) {
                entrega = (Entregas) parent.getItemAtPosition(position);
                showMenuImagem(v);
            }
        });

        if(pedido != null) {
            ConsultarEntregasPedido();
        }

        return layout;
    }




    public void ConsultarEntregasPedido(){

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listEntregas = helper.ConsultarEntregasPedido(Integer.parseInt(pedido));
                    if (listEntregas == null || listEntregas.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                        Toast.makeText(getActivity(),"Nenhuma Entrega encontrada para o pedido: "+String.valueOf(pedido),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapterConsulta = new EntregasConsultaAdapter(getActivity(), listEntregas);
                                listaEntregas.setAdapter(adapterConsulta);

                            }
                        });
                    }




                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Verifique a sua conexão com a internet e tente novamente!",Toast.LENGTH_SHORT).show();


                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Ocorreu uma falha na consulta, por gentileza tente novamente!",Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }


                }
            }

        }.start();
    }

    @SuppressLint("RestrictedApi")
    public void showMenuImagem(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_entregas, popup.getMenu());


        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();


    }


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapa:
                Intent intentMapa = new Intent(Intent.ACTION_VIEW);
                intentMapa.setData(Uri.parse("geo:0,0?q=" + Uri.encode(entrega.getEndereco()+" - "+entrega.getBairro()+"  "+entrega.getCidade()+"-"+entrega.getUf() )));
                item.setIntent(intentMapa);

                break;
            case R.id.ligar:

                if(entrega.getCelular().equals("")){
                    Toast.makeText(getActivity(),"Celular do cliente não, está cadastrado!",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE}, 1);

                }else{
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + entrega.getCelular()));

                    startActivity(intentLigar);
                    break;
                }

                break;

            case R.id.ocorrencia:
                if(entrega.getOcorrencia().equals("")){
                    Toast.makeText(getActivity(),"Entrega não possui ocorrências!",Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                    alerta.setIcon(R.drawable.ic_menu_ocorrencia);
                    alerta.setCancelable(false);
                    alerta.setTitle("Ocorrência ");
                    alerta.setMessage(Html.fromHtml(entrega.getOcorrencia()+ "<br />" +
                            "<br />" +"<b>" + "Tratativa: " + "</b>"+
                            "<br />" +entrega.getTratativa()));
                    alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    alertaDialog = alerta.create();
                    alertaDialog.show();
                    break;
                }

            default:
                break;
        }
        return false;
    }


}

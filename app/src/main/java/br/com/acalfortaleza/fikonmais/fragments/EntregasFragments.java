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
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.EntregasAdapter;
import br.com.acalfortaleza.fikonmais.helper.EntregasHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Entregas;
import mehdi.sakout.fancybuttons.FancyButton;

public class EntregasFragments extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private View layout;
    private TextView texto;
    private ListView listaEntregas;
    private FancyButton btEntregas;
    private FancyButton btAtraso;
    private ProgressDialog dialog;
    private EntregasHelper helper;
    private LoginHelper loginHelper;
    private List<Entregas> listEntregas;
    private HashMap<String, String> usuarioLogado;
    private Handler handler;
    private EntregasAdapter adapter;
    private AlertDialog alertaDialog;
    private Entregas entrega;
    private TextView qtdEntregas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new EntregasHelper();
        loginHelper = new LoginHelper(getActivity());

        usuarioLogado = loginHelper.GetUsuarioSalvo();
        handler = new Handler();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.fragment_entregas,
                container, false);
        texto = layout.findViewById(R.id.textoEntrega);
        btEntregas = layout.findViewById(R.id.btn_entregadia);
        btAtraso = layout.findViewById(R.id.btn_entregaatraso);
        listaEntregas = layout.findViewById(R.id.listaEntregas);
        qtdEntregas = layout.findViewById(R.id.adp_ent_qtd);


        this.listaEntregas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long l) {
                entrega = (Entregas) parent.getItemAtPosition(position);
                showMenuImagem(v);
            }
        });

        btEntregas.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        btEntregas.setTextColor(getResources().getColor(R.color.icons));
        btEntregas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btEntregas.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btEntregas.setTextColor(getResources().getColor(R.color.icons));
                btAtraso.setBackgroundColor(getResources().getColor(R.color.icons));
                btAtraso.setTextColor(getResources().getColor(R.color.primary));
                ConsultarEntregasDia();

            }
        });

        btAtraso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btEntregas.setBackgroundColor(getResources().getColor(R.color.icons));
                btEntregas.setTextColor(getResources().getColor(R.color.primary));
                btAtraso.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btAtraso.setTextColor(getResources().getColor(R.color.icons));
                ConsultarEntregasMes();

            }
        });

        ConsultarEntregasDia();


        return layout;
    }

    public void ConsultarEntregasDia() {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listEntregas = helper.ConsultarEntregasDia(Integer.parseInt((usuarioLogado.get("codigo"))));
                    if (listEntregas == null || listEntregas.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Nenhuma Entrega encontrada para hoje.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new EntregasAdapter(getActivity(), listEntregas);
                                listaEntregas.setAdapter(adapter);

                                if (adapter.getCount() > 0) {
                                    qtdEntregas.setText("Qtd. de entregas: " + String.valueOf(adapter.getCount()));

                                } else {
                                    qtdEntregas.setText("");
                                }

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(getActivity(), "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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

    public void ConsultarEntregasMes() {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listEntregas = helper.ConsultarEntregasMes(Integer.parseInt((usuarioLogado.get("codigo"))));

                    if (listEntregas == null || listEntregas.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Nenhuma Entrega encontrada no período.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new EntregasAdapter(getActivity(), listEntregas);
                                listaEntregas.setAdapter(adapter);

                                if (adapter.getCount() > 0) {
                                    qtdEntregas.setText("Qtd. de entregas: " + String.valueOf(adapter.getCount()));

                                } else {
                                    qtdEntregas.setText("");
                                }

                            }
                        });

                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(getActivity(), "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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
                intentMapa.setData(Uri.parse("geo:0,0?q=" + Uri.encode(entrega.getEndereco() + " - " + entrega.getBairro() + "  " + entrega.getCidade() + "-" + entrega.getUf())));
                item.setIntent(intentMapa);

                break;
            case R.id.ligar:

                if (entrega.getCelular().equals("")) {
                    Toast.makeText(getActivity(), "Celular do cliente não, está cadastrado!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE}, 1);

                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + entrega.getCelular()));

                    startActivity(intentLigar);
                    break;
                }

                break;

            case R.id.ocorrencia:
                if (entrega.getOcorrencia().equals("")) {
                    Toast.makeText(getActivity(), "Entrega não possui ocorrências!", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                    alerta.setIcon(R.drawable.ic_menu_ocorrencia);
                    alerta.setCancelable(false);
                    alerta.setTitle("Ocorrência ");
                    alerta.setMessage(Html.fromHtml(entrega.getOcorrencia() + "<br />" +
                            "<br />" + "<b>" + "Tratativa: " + "</b>" +
                            "<br />" + entrega.getTratativa()));
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

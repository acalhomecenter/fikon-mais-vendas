package br.com.acalfortaleza.fikonmais.fragments;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.ClientesActivity;
import br.com.acalfortaleza.fikonmais.ClientesCadastroActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.ClientesAdapter;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.popup.PopupConsultaCliente;
import mehdi.sakout.fancybuttons.FancyButton;

public class ClientesFragments extends Fragment {

    private View layout;
    private HashMap<String, String> usuarioLogado;
    private RecyclerView listaClientes;
    private Handler handler;
    private ClientesAdapter adapter;
    private List<Cliente> listClientes;
    private LoginHelper loginHelper;
    private ProgressDialog dialog;
    private ClienteHelper helper;
    private Cliente cliente;
    private String cgccpf = "0";
    private String nomeCli = "nome";
    private boolean isOrcamento = false;
    private boolean isPedido = false;
    private CardView btn_novo_cadastro;
    public static String situacao = "N";
    public static String natureza ;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cgccpf = getArguments().getString("cgccpf");
            nomeCli = getArguments().getString("nome");
            isOrcamento = getArguments().getBoolean("orcamento");
            isPedido = getArguments().getBoolean("pedido");
            context =  getContext();
        }
        loginHelper = new LoginHelper(getContext());
        helper = new ClienteHelper(getContext());
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        handler = new Handler();


    }

    @Override
    public void onResume() {
        super.onResume();

        if(!situacao.equals("N")){
            ConsultarClienteCodvendr(Integer.parseInt(usuarioLogado.get("codigo")),natureza,situacao);
            natureza = null;
            situacao = "N";
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.fragment_clientes,
                container, false);

        FloatingActionButton fab = layout.findViewById(R.id.frm_cli_btnCliente);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itent = new Intent(getContext(), ClientesCadastroActivity.class);
                itent.putExtra("orcamento",isOrcamento);
                startActivity(itent);
            }
        });

        FloatingActionButton fabAvancado = layout.findViewById(R.id.frm_cli_btnAvancado);
        fabAvancado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itent = new Intent(getContext(), PopupConsultaCliente.class);
                startActivity(itent);
            }
        });

        listaClientes = layout.findViewById(R.id.listaClientes);
        listaClientes.setHasFixedSize(true);
        listaClientes.setLayoutManager(new LinearLayoutManager(getActivity()));



        if (cgccpf!=null && cgccpf.length() >=2) {
            ConsultarClienteCgccpf(cgccpf);
        }

        if (nomeCli!=null && nomeCli.length() >= 5) {
            ConsultarClienteNome(nomeCli);
        }


        return layout;

    }


    public void ConsultarClienteCgccpf(final String cgccpf) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listClientes = helper.ConsultarClientesCgccpf(cgccpf, Integer.parseInt(usuarioLogado.get("codfil")), usuarioLogado.get("cpf"));
                    if (listClientes == null || listClientes.size()==0 ) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (!ValidarCpf(cgccpf)) {

                                } else {


                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setTitle("Atenção :|");
                                    builder.setMessage("Cgccpf: " + String.valueOf(cgccpf) + " não encontrado, deseja cadastrar?");
                                    builder
                                            .setCancelable(true)
                                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(getContext(), ClientesCadastroActivity.class);
                                                    intent.putExtra("cgccpf", cgccpf);
                                                    intent.putExtra("orcamento", isOrcamento);
                                                    intent.putExtra("pedido", isPedido);
                                                    startActivity(intent);


                                                }

                                            });

                                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();


                                        }
                                    });


                                    builder.create().show();

                                }
                            }
                        });
                    }


                        /*if (listClientes == null || listClientes.size() == 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"Código de cliente nao encontado!",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }*/


                        if(listClientes != null && listClientes.size() > 0){


                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    adapter = new ClientesAdapter(context, listClientes, isOrcamento, isPedido, Integer.parseInt(usuarioLogado.get("codfil")));
                                    listaClientes.setAdapter(adapter);

                                }
                            });
                        }






                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(context, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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


    public void ConsultarClienteNome(final String nomeCli) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listClientes = helper.ConsultarClientesNome(nomeCli);
                    if (listClientes == null || listClientes.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Nenhum cliente encontrado, para essa consulta!" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                adapter = new ClientesAdapter(context, listClientes,isOrcamento, isPedido, Integer.parseInt(usuarioLogado.get("codfil")));
                                listaClientes.setAdapter(adapter);

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(context, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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


    public void ConsultarClienteCodvendr(final int codvendr, final String natjur, String situacao) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listClientes = helper.ConsultarClientesCodvendr(codvendr, natjur, situacao);
                    if (listClientes == null || listClientes.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Nenhum cliente encontrado, para essa consulta!" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                adapter = new ClientesAdapter(context, listClientes,isOrcamento, isPedido, Integer.parseInt(usuarioLogado.get("codfil")));
                                listaClientes.setAdapter(adapter);

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(context, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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




    public boolean ValidarCpf(String cgccpf){


        if (cgccpf.length() == 11) {

            if (helper.ValidadorCpf(cgccpf.toString().trim())) {


                return true;
            } else {
                Toast.makeText(context,"Você consultou um CPF inválido?",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (cgccpf.length() == 14) {

            if (helper.ValidarCnpj(cgccpf.toString().trim())) {


                return true;
            } else {

                Toast.makeText(context,"Você consultou um CNPJ inválido?",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (cgccpf.length() >11 && cgccpf.length() <14 ) {



                Toast.makeText(context,"Você digitou um CNPJ inválido?",Toast.LENGTH_SHORT).show();
                return false;

        }
        return true;
    }




}

package br.com.acalfortaleza.fikonmais.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.CupomActivity;
import br.com.acalfortaleza.fikonmais.DevolucoesActivity;
import br.com.acalfortaleza.fikonmais.MainActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.ContatoAdapter;
import br.com.acalfortaleza.fikonmais.adapters.PontosAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.helper.FikonApiClient;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.models.Cargo;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.Segmento;
import br.com.acalfortaleza.fikonmais.popup.PopupCadastroContato;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ClientesComplementoFragments extends Fragment {


    public ClientesComplementoFragments() {


    }

    private View layout;
    private LoginHelper loginHelper;
    private HashMap<String, String> usuarioLogado;
    private TextInputLayout layout_identidate;
    public static TextInputEditText edt_identidate;
    private TextInputLayout layout_emissor;
    public static TextInputEditText edt_emissor;
    private TextInputLayout layout_ufemissor;
    public static TextInputEditText edt_ufemissor;
    public static TextInputEditText edt_profissao;
    public static Cliente clienteAlteracao;
    public static TextInputEditText edt_sitcred;
    public static TextInputEditText consultor;
    public static TextInputEditText edt_obs;
    public static  TextInputLayout layout_segmento;
    public static TextInputEditText edt_segmento;
    public static int codConsultor;
    private Button btn_gravar_contato;
    private TextInputLayout layout_profissao;
    private TextInputLayout layout_consultor;
    private List<Contato> mList;
    private ArrayList<String> listaContato = new ArrayList<String>();
    private List<Consultor> mListConsultor;
    private ArrayList<String> listaConsultor= new ArrayList<String>();
    private RecyclerView mRecycleView;
    private ContatoAdapter adapter;
    private SpinnerDialog spinnerDialog;
    private SpinnerDialog spinnerDialogSegmento;
    private SpinnerDialog spinnerDialogConsultor;
    private List<Cargo> mListCargo;
    private ArrayList<String> listaCargo= new ArrayList<>();

    private List<Segmento> mListSegmento;
    private ArrayList<String> listaSegmento= new ArrayList<>();
    private ProgressDialog dialog;
    private ClienteHelper helper;
    private OrcamentoHelper helperOrc;
    private Handler handler;
    private String retorno;


    public static void limparClienteNoFormulario() {

        edt_identidate.setText("");
        edt_emissor.setText("");
        edt_ufemissor.setText("");
        edt_sitcred.setText("");
        edt_profissao.setText("");
        consultor.setText("");
        edt_obs.setText("");
        codConsultor = 0;
        edt_segmento.setText("");


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginHelper = new LoginHelper(getActivity());
        helper = new ClienteHelper(getActivity());
        helperOrc = new OrcamentoHelper();
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        handler = new Handler();

        if (getArguments() != null) {

            clienteAlteracao = (Cliente) getArguments().getSerializable("cliente");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_clientes_complemento,
                container, false);


        layout_identidate = layout.findViewById(R.id.layout_cli_edtIdentidade);
        edt_identidate = layout.findViewById(R.id.cli_edtIdentidade);
        layout_emissor = layout.findViewById(R.id.layout_cli_edtEmissor);
        edt_emissor = layout.findViewById(R.id.cli_edtEmissor);
        layout_ufemissor = layout.findViewById(R.id.layout_cli_edtUfEmissor);
        edt_ufemissor = layout.findViewById(R.id.cli_edtUfEmissor);
        edt_sitcred = layout.findViewById(R.id.cli_edtSitCredito);
        layout_profissao= layout.findViewById(R.id.cli_layout_edtProfissao);
        edt_profissao = layout.findViewById(R.id.cli_edtProfissao);
        edt_segmento = layout.findViewById(R.id.cli_edtSegmento);
        layout_segmento = layout.findViewById(R.id.cli_layout_edtSegmento);
        layout_consultor = layout.findViewById(R.id.cli_layout_layoutConsultor);
        consultor = layout.findViewById(R.id.cli_edtConsultor);
        edt_obs = layout.findViewById(R.id.cli_edtObs);
        btn_gravar_contato = layout.findViewById(R.id.cli_btn_gravarcontato);
        mRecycleView = layout.findViewById(R.id.cli_listaContatos);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));



        ConsultarCombobox();

        if(usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS CORPORATIVO FX U") || usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS FX U") || usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS MULTICANAIS FX U") || usuarioLogado.get("isgerente").equals("SIM") ){

            consultor.setEnabled(true);
        }else{
            consultor.setEnabled(false);
        }


        if (clienteAlteracao != null) {
            carregaClienteNoFormulario(clienteAlteracao);
            if(clienteAlteracao.getNatjur().equals("J")){
                btn_gravar_contato.setEnabled(true);
                btn_gravar_contato.setBackgroundResource(R.drawable.backgroudbuttom);
                layout_segmento.setVisibility(View.VISIBLE);

                adapter = new ContatoAdapter(getContext(), clienteAlteracao.getContatos());
                mList = clienteAlteracao.getContatos();
                mRecycleView.setAdapter(adapter);

            }else{
                btn_gravar_contato.setEnabled(false);
                btn_gravar_contato.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                btn_gravar_contato.setEnabled(false);
                btn_gravar_contato.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                layout_segmento.setVisibility(View.INVISIBLE);

            }


        } else {
            consultor.setText("");

            btn_gravar_contato.setEnabled(false);
            btn_gravar_contato.setBackgroundResource(R.drawable.backgroundbuttondisabled);
            layout_segmento.setVisibility(View.INVISIBLE);
        }


        btn_gravar_contato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PopupCadastroContato.class);
                intent.putExtra("codcli",clienteAlteracao.getCodcli());
                intent.putExtra(PopupCadastroContato.EXTRA_CODIGO_CLIENTE_FIKON,
                        FikonApiClient.codigoClienteFikon(clienteAlteracao));
                startActivity(intent);
            }
        });

        spinnerDialog = new SpinnerDialog(getActivity(), listaCargo, "selecione a profissão");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edt_profissao.setText(item);
                if(clienteAlteracao != null){
                    clienteAlteracao.setCargo(mListCargo.get(position).getDescricao());
                    clienteAlteracao.setCodCargo(mListCargo.get(position).getCodcargo());
                }
                else {
                    clienteAlteracao = new Cliente();
                    clienteAlteracao.setCargo(mListCargo.get(position).getDescricao());
                    clienteAlteracao.setCodCargo(mListCargo.get(position).getCodcargo());
                }



            }
        });



        spinnerDialogSegmento = new SpinnerDialog(getActivity(), listaSegmento, "selecione o segmento");
        spinnerDialogSegmento.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edt_segmento.setText(item);
                if(clienteAlteracao != null){

                    clienteAlteracao.setSegmento(mListSegmento.get(position));
                }
                else {
                    clienteAlteracao = new Cliente();

                    clienteAlteracao.setSegmento(mListSegmento.get(position));
                }



            }
        });


        spinnerDialogConsultor = new SpinnerDialog(getActivity(), listaConsultor, "selecione o consultor");
        spinnerDialogConsultor.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                consultor.setText(item);
                if(clienteAlteracao != null){
                    clienteAlteracao.setCodvendr(mListConsultor.get(position).CodigoConsultor);
                    clienteAlteracao.setConsultor(mListConsultor.get(position).nomeconsultor);
                    codConsultor = mListConsultor.get(position).CodigoConsultor;
                }
                else {
                    clienteAlteracao = new Cliente();
                    clienteAlteracao.setCodvendr(mListConsultor.get(position).CodigoConsultor);
                    clienteAlteracao.setConsultor(mListConsultor.get(position).nomeconsultor);
                    codConsultor = mListConsultor.get(position).CodigoConsultor;
                }



            }
        });


        edt_profissao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_profissao.getRight() - edt_profissao.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edt_segmento.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_segmento.getRight() - edt_segmento.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogSegmento.showSpinerDialog();

                    }
                }
                return false;
            }
        });

        consultor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (consultor.getRight() - consultor.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                        if(consultor.getText().toString().length() > 0 ){
                            if(usuarioLogado.get("isgerente").equals("SIM")){
                                spinnerDialogConsultor.showSpinerDialog();
                            }
                        }else{
                            spinnerDialogConsultor.showSpinerDialog();
                        }


                    }
                }
                return false;
            }
        });

        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecycleView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(clienteAlteracao !=null && clienteAlteracao.getContatos().size()>0){

                            Intent intent = new Intent(getActivity(),PopupCadastroContato.class);
                            intent.putExtra("contato",clienteAlteracao.getContatos().get(position));
                            startActivity(intent);
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        if (helper.isFikon()) {
                            Toast.makeText(getActivity(),
                                    FikonApiClient.MENSAGEM_CONTATO_SO_INCLUSAO,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Atenção :|");
                        builder.setMessage("Deseja realizar a exclusão do contato?");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        deletarContato(mList.get(position),"DELETAR");

                                        adapter.deleteListitem(mList.get(position),position);


                                    }

                                });


                        builder.setNegativeButton("Nao", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {



                            }
                        });

                        builder.create().show();

                    }
                })
        );



        return layout;
    }

    public void carregaClienteNoFormulario(Cliente cliente) {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
        edt_identidate.setText(cliente.getNumdoc());
        edt_emissor.setText(cliente.getEmissor());
        edt_ufemissor.setText(cliente.getUfEmissor());
        this.edt_sitcred.setText(cliente.getSitcred());

        if (!cliente.getConsultor().equals("")) {
            this.consultor.setText(cliente.getConsultor());

        }

        edt_obs.setText(cliente.getObs());


        if (!cliente.getCargo().equals("")) {
            edt_profissao.setText(cliente.getCargo());
        }

        if (cliente.getSegmento() != null && cliente.getSegmento().getId() > 0) {
            edt_segmento.setText(cliente.getSegmento().getDescricao());
        }

    }

    public void ConsultarCombobox() {

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {


                    mListCargo = helper.ConsultarCargos();

                    mListSegmento = helper.ConsultarSegmentos();

                    mListConsultor = helperOrc.GetConsultor();



                    handler.post(new Runnable() {
                        @Override
                        public void run() {



                            if (mListCargo != null & mListCargo.size() > 0) {
                                for (Cargo c : mListCargo) {
                                    listaCargo.add(c.getDescricao());
                                }
                            }


                            if (mListSegmento != null & mListSegmento.size() > 0) {
                                for (Segmento s : mListSegmento) {
                                    listaSegmento.add(s.getDescricao());
                                }
                            }



                            if (mListConsultor != null & mListConsultor.size() > 0) {
                                for (Consultor v : mListConsultor) {
                                    listaConsultor.add(v.nomeconsultor);
                                }
                            }



                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getActivity()).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
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
                            new AlertDialog.Builder(getActivity()).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } finally {
                    dialog.dismiss();


                }
            }


        }.start();


    }


    public void deletarContato(final Contato contato , final String tipo) {


        dialog = new ProgressDialog(getActivity());

            dialog.setMessage("Excluindo...");


        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {


                try {

                   retorno =  helper.SetContato(contato, tipo);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity(), retorno, Toast.LENGTH_LONG).show();
                            if (retorno != null && retorno.equals("Contato deletado com sucesso!")) {


                                Toast.makeText(getActivity(),retorno,Toast.LENGTH_SHORT).show();


                            }


                        }
                    });
                }

            }
        }.start();

    }



}

package br.com.acalfortaleza.fikonmais.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.ClientesCadastroActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.EnderecosAdapter;
import br.com.acalfortaleza.fikonmais.applications.Mask;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.TheChecker;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEmail;

public class ClientesGeralFragments extends Fragment {


    public ClientesGeralFragments() {

        retornoemail = "";
    }

    private View layout;
    public static TextInputEditText edt_cgccpf;
    private TextInputLayout layout_cgccpf;
    private TextInputEditText edt_nomecli;
    private TextInputLayout layout_nomeCli;
    private TextInputLayout layout_fantasia;
    public static TextInputEditText edt_celular;
    public static TextInputEditText edt_telefone;
    public static TextInputEditText edt_wpp;
    public static TextInputEditText edt_email;
    private TextInputLayout layout_celular;
    private TextInputLayout layout_telefone;
    private TextInputLayout layout_wpp;
    private TextInputLayout layout_email;
    private Button btn_gravar;
    private ClienteHelper helper;
    private FrameLayout container;
    private Context context;
    private TextInputEditText edt_data;
    private TextInputLayout layout_data;
    private RadioButton radio_masculino;
    private RadioButton radio_feminino;
    private LinearLayout linearPf;
    private LinearLayout linearPj;
    private TextInputEditText edt_fantasia;
    public static TextInputEditText edt_inscricao;
    private List<Cliente> listClientes;
    private ProgressDialog dialog;
    private Handler handler;
    private Cliente clienteAlteracao;
    private String retorno;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private String cgccpf;
    private Boolean isOrcamento = false;
    private  Cliente clienteFormulario;
    public static String retornoemail = "";
    TheChecker checker = new TheChecker();
    private ValidacaoEmail validacaoEmail = new ValidacaoEmail();
    private Switch wppFixo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new ClienteHelper(getActivity());
        loginHelper = new LoginHelper(getActivity());
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        handler = new Handler();

        if (getArguments() != null) {

            clienteAlteracao = (Cliente) getArguments().getSerializable("cliente");
            cgccpf = getArguments().getString("cgccpf");
            isOrcamento = getArguments().getBoolean("isOrcamento");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_clientes_geral,
                container, false);

        edt_cgccpf = layout.findViewById(R.id.cli_edtCgccpf);
        layout_cgccpf = layout.findViewById(R.id.layout_cli_edtCgccpf);
        edt_nomecli = layout.findViewById(R.id.cli_edtNomcli);
        layout_nomeCli = layout.findViewById(R.id.layout_cli_edtNomcli);
        layout_fantasia = layout.findViewById(R.id.layout_cli_edtFantasia);
        edt_celular = layout.findViewById(R.id.cli_edtCelular);
        edt_telefone = layout.findViewById(R.id.cli_edtTelefone);
        edt_wpp = layout.findViewById(R.id.cli_edtWpp);
        edt_email = layout.findViewById(R.id.cli_edtEmail);
        layout_celular = layout.findViewById(R.id.layout_cli_edtCelular);
        layout_telefone = layout.findViewById(R.id.layout_cli_edtTelefone);
        layout_wpp = layout.findViewById(R.id.layout_cli_edtWpp);
        layout_email = layout.findViewById(R.id.layout_cli_edtEmail);
        edt_data = layout.findViewById(R.id.cli_edtNascimento);
        layout_data = layout.findViewById(R.id.layout_cli_edtNascimento);
        radio_masculino = layout.findViewById(R.id.cli_radioMasculino);
        radio_feminino = layout.findViewById(R.id.cli_radionFeminino);
        btn_gravar = layout.findViewById(R.id.cli_btn_gravar);
        linearPf = layout.findViewById(R.id.linearPf);
        linearPj = layout.findViewById(R.id.linearPj);
        edt_fantasia = layout.findViewById(R.id.cli_edtfantasia);
        edt_inscricao = layout.findViewById(R.id.cli_edtInscricao);
        wppFixo = layout.findViewById(R.id.cli_fixo);


        wppFixo.setVisibility(View.INVISIBLE);



        edt_nomecli.setEnabled(false);
        edt_data.setEnabled(false);
        edt_fantasia.setEnabled(false);
        edt_inscricao.setEnabled(false);
        edt_celular.setEnabled(false);
        edt_email.setEnabled(false);
        edt_telefone.setEnabled(false);
        edt_wpp.setEnabled(false);

        if (clienteAlteracao != null) {

            carregaClienteNoFormulario(clienteAlteracao);
            edt_cgccpf.setEnabled(false);
            edt_nomecli.setEnabled(true);
            edt_data.setEnabled(true);
            edt_fantasia.setEnabled(true);
            edt_inscricao.setEnabled(true);
            edt_celular.setEnabled(true);
            edt_email.setEnabled(true);
            edt_telefone.setEnabled(true);
            edt_wpp.setEnabled(true);



        }else{
            edt_cgccpf.setEnabled(true);
            edt_nomecli.setEnabled(false);
            edt_data.setEnabled(false);
            edt_fantasia.setEnabled(false);
            edt_inscricao.setEnabled(false);
            edt_celular.setEnabled(false);
            edt_email.setEnabled(false);
            edt_telefone.setEnabled(false);
            edt_wpp.setEnabled(false);
        }

        if(cgccpf!= null && !cgccpf.equals("") && cgccpf.length()>11){
            edt_cgccpf.setText(cgccpf);
            edt_cgccpf.setEnabled(false);
            linearPf.setVisibility(View.INVISIBLE);
            linearPj.setVisibility(View.VISIBLE);
            edt_nomecli.setEnabled(true);
            edt_fantasia.setEnabled(true);
            edt_inscricao.setEnabled(true);
            edt_data.setEnabled(true);
            edt_celular.setEnabled(true);
            edt_email.setEnabled(true);
            edt_telefone.setEnabled(true);
            edt_wpp.setEnabled(true);


        }else{
             if(cgccpf!= null && !cgccpf.equals("") && cgccpf.length() >1 && cgccpf.length() <=11 ){
                 edt_cgccpf.setText(cgccpf);
                 edt_cgccpf.setEnabled(false);
                 linearPf.setVisibility(View.VISIBLE);
                 linearPj.setVisibility(View.INVISIBLE);
                 edt_nomecli.setEnabled(true);
                 edt_data.setEnabled(true);
                 edt_celular.setEnabled(true);
                 edt_email.setEnabled(true);
                 edt_telefone.setEnabled(true);
                 edt_wpp.setEnabled(true);


             }

        }


        edt_cgccpf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (validarCgccpf()) {

                        if (edt_cgccpf.getText().toString().trim().length() > 11) {
                            linearPf.setVisibility(View.INVISIBLE);
                            linearPj.setVisibility(View.VISIBLE);
                            edt_nomecli.setEnabled(true);
                            edt_fantasia.setEnabled(true);
                            edt_inscricao.setEnabled(true);
                            edt_data.setEnabled(true);
                            edt_celular.setEnabled(true);
                            edt_email.setEnabled(true);
                            edt_telefone.setEnabled(true);
                            edt_wpp.setEnabled(true);
                            ClientesComplementoFragments.layout_segmento.setVisibility(View.VISIBLE);

                            if (clienteAlteracao == null) {
                                ConsultarClienteCgccpf(edt_cgccpf.getText().toString().trim());

                            }


                        } else {
                            linearPf.setVisibility(View.VISIBLE);
                            linearPj.setVisibility(View.INVISIBLE);
                            edt_nomecli.setEnabled(true);
                            edt_data.setEnabled(true);
                            edt_celular.setEnabled(true);
                            edt_email.setEnabled(true);
                            edt_telefone.setEnabled(true);
                            edt_wpp.setEnabled(true);
                            ClientesComplementoFragments.layout_segmento.setVisibility(View.INVISIBLE);
                            if (clienteAlteracao == null) {
                                ConsultarClienteCgccpf(edt_cgccpf.getText().toString().trim());
                            }
                        }
                    }


                }
            }
        });


        edt_cgccpf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validarCgccpf()) {

                        if (edt_cgccpf.getText().toString().trim().length() > 11) {
                            linearPf.setVisibility(View.INVISIBLE);
                            linearPj.setVisibility(View.VISIBLE);
                            edt_nomecli.setEnabled(true);
                            edt_fantasia.setEnabled(true);
                            edt_inscricao.setEnabled(true);
                            edt_data.setEnabled(true);
                            edt_celular.setEnabled(true);
                            edt_email.setEnabled(true);
                            edt_telefone.setEnabled(true);
                            edt_wpp.setEnabled(true);
                            ClientesComplementoFragments.layout_segmento.setVisibility(View.VISIBLE);

                            if (clienteAlteracao == null) {
                                ConsultarClienteCgccpf(edt_cgccpf.getText().toString().trim());

                            }


                        } else {
                            linearPf.setVisibility(View.VISIBLE);
                            linearPj.setVisibility(View.INVISIBLE);
                            edt_nomecli.setEnabled(true);
                            edt_data.setEnabled(true);
                            edt_celular.setEnabled(true);
                            edt_email.setEnabled(true);
                            edt_telefone.setEnabled(true);
                            edt_wpp.setEnabled(true);
                            ClientesComplementoFragments.layout_segmento.setVisibility(View.INVISIBLE);
                            if (clienteAlteracao == null) {
                                ConsultarClienteCgccpf(edt_cgccpf.getText().toString().trim());
                            }
                        }
                    }


                }
                return false;
            }
        });


        edt_nomecli.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (validarNome()) {

                        if (edt_cgccpf.getText().toString().trim().length() > 11) {
                            ClientesComplementoFragments.layout_segmento.setVisibility(View.VISIBLE);
                        }else{
                            ClientesComplementoFragments.layout_segmento.setVisibility(View.INVISIBLE);
                        }

                    }
                }
            }
        });

       /*edt_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if(!edt_email.getText().toString().trim().equals("") && retornoemail.equals("")){
                        ConsultarEmail(edt_email.getText().toString().trim());
                    }

                }
            }
        });*/

        edt_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {
                if((event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()) || i == EditorInfo.IME_ACTION_DONE) {
                    if(!edt_email.getText().toString().trim().equals("")){
                        ConsultarEmail(edt_email.getText().toString().trim());
                    }
                }
                return false;
            }
        });



        edt_data.addTextChangedListener(Mask.insert("##/##/####", edt_data));


        edt_telefone.addTextChangedListener(Mask.insert("(##)####-####", edt_telefone));


        edt_celular.addTextChangedListener(Mask.insert("(##)#####-####", edt_celular));

        edt_wpp.addTextChangedListener(Mask.insert("(##)#####-####", edt_wpp));









        edt_celular.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!validarCelular()) {

                    } else {

                    }
                }
            }
        });


        btn_gravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (!validarCgccpf() || !validarEmail() || !validarNome() || !validarCelular() || !validarData() || !validarTelefone() || !validarWpp() || !validarFantasia() || !validarConsultor() || !validarSegmento()) {

                    } else {

                        if (btn_gravar.getText().equals("Alterar")) {

                            if (ClientesEnderecosFragments.mList.size() == 0) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Você realmente deseja alterar os dados do Cliente, sem informar o endereço?");
                                builder
                                        .setCancelable(false)
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                salvarCliente("ALTERAR");

                                            }

                                        });

                                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.create().show();


                            } else {
                                if (clienteAlteracao.getTipo().equals("CADASTRADO")) {
                                    salvarCliente("GEMCO_ALTERAR");

                                } else {
                                    salvarCliente("GEMCO_CADASTRAR");

                                }
                            }

                        }


                        if (btn_gravar.getText().equals("Cadastrar")) {

                            if (ClientesEnderecosFragments.mList.size() == 0 && clienteAlteracao == null) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Você realmente deseja cadastrar o Cliente, sem informar o endereço?");
                                builder
                                        .setCancelable(false)
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                salvarCliente("ORCAMENTO");

                                            }

                                        });


                                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.create().show();

                            }

                            if (ClientesEnderecosFragments.mList.size() > 0 && clienteAlteracao == null) {


                                salvarCliente("GEMCO_CADASTRAR");


                            }


                        }



                }

            }
        });

        return layout;
    }


    public void IncluirNoOrcamento(){
        if(isOrcamento){

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage("Você deseja incluir esse cliente no orçamento?");
            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                            ConsultarClienteParaOrcamento(clienteFormulario.getCgccpf());


                        }

                    });


            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();


        }else{

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage("Você deseja iniciar um orçamento para esse cliente?");
            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            ConsultarClienteParaOrcamento(clienteFormulario.getCgccpf());

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


    public void salvarCliente(final String tipo) {


        dialog = new ProgressDialog(getActivity());
        if (tipo.equals("ORCAMENTO") || tipo.equals("GEMCO_CADASTRAR")) {
            dialog.setMessage("Salvando...");
        }
        if (tipo.equals("ALTERAR") || tipo.equals("GEMCO_ALTERAR")) {
            dialog.setMessage("Alterando...");
        }


        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                clienteFormulario = getClienteDoFormulario();
                try {
                    int qtdEnderecos = clienteFormulario != null && clienteFormulario.getEnderecos() != null
                            ? clienteFormulario.getEnderecos().size() : 0;
                    Log.d("ClienteCadastroDebug",
                            "Iniciando salvarCliente"
                                    + " | tipo=" + tipo
                                    + " | cgccpf=" + (clienteFormulario != null ? clienteFormulario.getCgccpf() : "null")
                                    + " | nome=" + (clienteFormulario != null ? clienteFormulario.getNomcli() : "null")
                                    + " | natjur=" + (clienteFormulario != null ? clienteFormulario.getNatjur() : "null")
                                    + " | email=" + (clienteFormulario != null ? clienteFormulario.getEmail() : "null")
                                    + " | celular=" + (clienteFormulario != null ? clienteFormulario.getCelular() : "null")
                                    + " | codcli=" + (clienteFormulario != null ? clienteFormulario.getCodcli() : 0)
                                    + " | qtdEnderecos=" + qtdEnderecos
                                    + " | botao=" + btn_gravar.getText());
                } catch (Exception logEx) {
                    Log.e("ClienteCadastroDebug", "Falha ao montar log antes de salvar cliente", logEx);
                }

                try {

                    retorno = helper.SetCliente(clienteFormulario, tipo);
                    Log.d("ClienteCadastroDebug", "Retorno SetCliente=" + retorno);

                } catch (IOException e) {
                    Log.e("ClienteCadastroDebug", "IOException ao salvar cliente", e);
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    Log.e("ClienteCadastroDebug", "XmlPullParserException ao salvar cliente", e);
                    e.printStackTrace();
                } catch (ParseException e) {
                    Log.e("ClienteCadastroDebug", "ParseException ao salvar cliente", e);
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity(), retorno, Toast.LENGTH_LONG).show();
                            if (retorno != null && (retorno.equals("Cliente cadastrado com sucesso!") || retorno.equals("Cliente alterado com sucesso!"))) {
                                limparClienteNoFormulario();
                                ClientesComplementoFragments.limparClienteNoFormulario();
                                IncluirNoOrcamento();
                            }


                        }
                    });
                }

            }
        }.start();

    }


    public void carregaClienteNoFormulario(Cliente cliente) {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
        this.edt_cgccpf.setText(cliente.getCgccpf());
        this.edt_nomecli.setText(cliente.getNomcli());
        if (cliente.getNatjur().equals("F")) {
            this.linearPj.setVisibility(View.INVISIBLE);
            this.linearPf.setVisibility(View.VISIBLE);
            try {
                if (!cliente.getDtnasc().equals("")) {
                    Date date = entrada.parse(cliente.getDtnasc());
                    this.edt_data.setText(formato.format(date));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (cliente.getSexo().equals("M")) {
                this.radio_masculino.setChecked(true);

            } else {
                this.radio_feminino.setChecked(true);
            }
        } else {
            this.linearPf.setVisibility(View.INVISIBLE);
            this.linearPj.setVisibility(View.VISIBLE);
            this.edt_fantasia.setText(cliente.getFantasia());
            this.edt_inscricao.setText(cliente.getInscr());

        }
        if (!cliente.getFone1().equals("") && cliente.getFone1().length() >= 8) {
            this.edt_telefone.setText("(" + cliente.getDddFone1() + ")" + cliente.getFone1().substring(0, 4) + "-" + cliente.getFone1().substring(4, 8));
        } else {
            this.edt_telefone.setText(cliente.getDddFone1() + cliente.getFone1());
        }
        if (!cliente.getFone2().equals("") && cliente.getFone2().length() >= 9) {
            this.edt_celular.setText("(" + cliente.getDddFone2() + ")" + cliente.getFone2().substring(0, 5) + "-" + cliente.getFone2().substring(5, 9));
        } else {
            this.edt_celular.setText(cliente.getDddFone2() + cliente.getFone2());
        }
        if (!cliente.getCelular().equals("") && cliente.getCelular().length() >= 9) {
            this.edt_wpp.setText("(" + cliente.getDddCelular() + ")" + cliente.getCelular().substring(0, 5) + "-" + cliente.getCelular().substring(5, 9));
        } else {
            this.edt_wpp.setText(cliente.getDddCelular() + cliente.getCelular());
        }
        this.edt_email.setText(cliente.getEmail());

        if (ClientesComplementoFragments.edt_identidate != null) {
            DateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat entrada2 = new SimpleDateFormat("yyyy-MM-dd");
            ClientesComplementoFragments.edt_identidate.setText(cliente.getNumdoc());
            ClientesComplementoFragments.edt_emissor.setText(cliente.getEmissor());
            ClientesComplementoFragments.edt_ufemissor.setText(cliente.getUfEmissor());
            ClientesComplementoFragments.edt_sitcred.setText(cliente.getSitcred());


            if (!cliente.getConsultor().equals("")) {
                ClientesComplementoFragments.consultor.setText(cliente.getConsultor());

            }else{
                ClientesComplementoFragments.consultor.setText("");
            }


            ClientesComplementoFragments.edt_obs.setText(cliente.getObs());

        }
        this.btn_gravar.setText("Alterar");


        if(!edt_email.getText().toString().trim().equals("")){
            ConsultarEmail(edt_email.getText().toString().trim());
        }


    }


    public void limparClienteNoFormulario() {


        this.edt_cgccpf.setText("");
        this.edt_nomecli.setText("");
        this.linearPj.setVisibility(View.INVISIBLE);
        this.linearPf.setVisibility(View.VISIBLE);
        this.edt_data.setText("");
        this.radio_masculino.setChecked(false);
        this.radio_feminino.setChecked(false);
        this.edt_fantasia.setText("");
        this.edt_inscricao.setText("");
        this.edt_telefone.setText("");
        this.edt_celular.setText("");
        this.edt_wpp.setText("");
        this.edt_email.setText("");
        this.btn_gravar.setText("Cadastrar");
        this.edt_cgccpf.setEnabled(true);
        ClientesComplementoFragments.limparClienteNoFormulario();

        EnderecosAdapter adapterDelete = (EnderecosAdapter) ClientesEnderecosFragments.mRecycleView.getAdapter();
        for (int i = 0; i < ClientesEnderecosFragments.mList.size(); i++) {
            adapterDelete.removeListItem(adapterDelete.getItemCount()-1);
        }
        ClientesEnderecosFragments.limparEnderecoNoFormulario();
        clienteAlteracao = null;


    }

    public Cliente getClienteDoFormulario() {

        Cliente cliente = new Cliente();
        if (clienteAlteracao != null) {
            cliente.setFikonMetadata(clienteAlteracao.getFikonMetadata());
        }


        String cgccpf = edt_cgccpf.getText().toString();
        String natjur = "";
        if (cgccpf.length() == 11) {
            natjur = "F";
        } else {
            natjur = "J";
        }
        String nome = edt_nomecli.getText().toString().replaceAll("'"," ");
        String fantasia = edt_fantasia.getText().toString().replaceAll("'"," ");
        String inscricao = edt_inscricao.getText().toString();
        Date date = null;

        String dtnascimento = edt_data.getText().toString();

        String sexo = "";
        if (radio_masculino.isChecked()) {
            sexo = "M";
        }
        if (radio_feminino.isChecked()) {
            sexo = "F";
        }
        String email = edt_email.getText().toString();
        String dddfone1 = "";
        String telefone = "";
        if (edt_telefone.length() > 0) {
            dddfone1 = edt_telefone.getText().toString().substring(1, 3);
            telefone = edt_telefone.getText().toString().substring(4, 8) + edt_telefone.getText().toString().substring(9);
        }
        String dddcelular = "";
        String celular = "";
        if (edt_celular.length() > 0) {

            try{
                dddcelular = edt_celular.getText().toString().substring(1, 3);
                celular = edt_celular.getText().toString().substring(4, 9) + edt_celular.getText().toString().substring(10);
            }catch (Exception e){
                dddcelular = "85";
                try {
                    celular = edt_celular.getText().toString().substring(4, 9);
                }catch (Exception ee){
                    dddcelular ="";
                    celular ="";
                }

            }

        }
        String dddwpp = "";
        String wpp = "";
        if (edt_wpp.length() > 0) {
            if (wppFixo.isChecked()) {
                dddwpp = edt_wpp.getText().toString().substring(1, 3);
                try {
                    wpp = edt_wpp.getText().toString().substring(4, 8) + edt_wpp.getText().toString().substring(9);
                } catch (Exception e) {
                    wpp = edt_wpp.getText().toString().substring(4, 9);
                }

            } else {


            dddwpp = edt_wpp.getText().toString().substring(1, 3);
            try {
                wpp = edt_wpp.getText().toString().substring(4, 9) + edt_wpp.getText().toString().substring(10);
            } catch (Exception e) {
                wpp = edt_wpp.getText().toString().substring(4, 9);
            }
        }

        }





        cliente.setCgccpf(cgccpf);
        cliente.setNatjur(natjur);
        cliente.setNomcli(nome);
        cliente.setFantasia(fantasia);
        cliente.setInscr(inscricao);
        cliente.setEmail(email);
        cliente.setDddFone1(dddfone1);
        cliente.setFone1(telefone);
        cliente.setDddCelular(dddwpp);
        cliente.setCelular(wpp);
        cliente.setDddFone2(dddcelular);
        cliente.setFone2(celular);
        cliente.setSexo(sexo);
        cliente.setDtnasc(dtnascimento);

        if(ClientesComplementoFragments.clienteAlteracao != null && ClientesComplementoFragments.clienteAlteracao.getCodCargo() > 0){
            cliente.setCodCargo(ClientesComplementoFragments.clienteAlteracao.getCodCargo());
        }

        if(ClientesComplementoFragments.clienteAlteracao != null && ClientesComplementoFragments.clienteAlteracao.getSegmento() != null  && ClientesComplementoFragments.clienteAlteracao.getSegmento().getId() > 0){
            cliente.setSegmento(ClientesComplementoFragments.clienteAlteracao.getSegmento());
        }

        cliente.setFilialCadastro(usuarioLogado.get("codfil"));

        if(usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS CORPORATIVO FX U") || usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS FX U") || usuarioLogado.get("isgerente").equals("SIM") ){

            cliente.setConsultor(String.valueOf(ClientesComplementoFragments.codConsultor));
        }else{
            cliente.setConsultor(usuarioLogado.get("codigo"));
        }

        cliente.setNumdoc(ClientesComplementoFragments.edt_identidate.getText().toString());
        cliente.setObs(ClientesComplementoFragments.edt_obs.getText().toString());

        cliente.setEmissor(ClientesComplementoFragments.edt_emissor.getText().toString());
        cliente.setUfEmissor(ClientesComplementoFragments.edt_ufemissor.getText().toString());
        if(ClientesEnderecosFragments.mList.size() >0) {
            for(int i = 0; i < ClientesEnderecosFragments.mList.size(); i++ ) {
                ClientesEnderecosFragments.mList.get(i).setDddFone1(dddfone1);
                ClientesEnderecosFragments.mList.get(i).setFone1(telefone);
                ClientesEnderecosFragments.mList.get(i).setDddFone2(dddcelular);
                ClientesEnderecosFragments.mList.get(i).setFone2(celular);
                ClientesEnderecosFragments.mList.get(i).setDddCelular(dddwpp);
                ClientesEnderecosFragments.mList.get(i).setCelular(wpp);
                ClientesEnderecosFragments.mList.get(i).setEmail(email);
            }
            cliente.setEnderecos(ClientesEnderecosFragments.mList);

            if(cliente.getEnderecos().size()>0){
                if(cliente.getEnderecos().get(0).getCodcli()>0){
                    cliente.setCodcli(cliente.getEnderecos().get(0).getCodcli());
                }
            }
        }


        return cliente;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public boolean validarCgccpf() {

        String cgccpf = edt_cgccpf.getText().toString().trim();

        if (cgccpf.isEmpty()) {
            layout_cgccpf.setErrorEnabled(true);
            layout_cgccpf.setError("Informe um Cpf/Cnpj");
            return false;

        }

        if (cgccpf.length() < 11) {
            layout_cgccpf.setErrorEnabled(true);
            layout_cgccpf.setError("Erro no Cpf/Cnpj");
            edt_cgccpf.setError("cpf/cnpj menor que 11 numeros");
            return false;


        }

        if (edt_cgccpf.getText().length() == 11) {

            if (helper.ValidadorCpf(edt_cgccpf.getText().toString().trim())) {

                layout_cgccpf.setErrorEnabled(false);

                return true;
            } else {
                edt_cgccpf.setError("Cpf inválido");
                edt_cgccpf.setText("");
                return false;
            }
        }

        if (edt_cgccpf.getText().length() == 14) {

            if (helper.ValidarCnpj(edt_cgccpf.getText().toString().trim())) {

                layout_cgccpf.setErrorEnabled(false);
                return true;
            } else {

                edt_cgccpf.setError("Cnpj inválido");
                edt_cgccpf.setText("");
                return false;
            }
        }

        return false;

    }



    public boolean validarEmail() {


        String email = edt_email.getText().toString().trim();

        if(email.isEmpty()){
            layout_email.setErrorEnabled(false);

            return true;
        }
        if (!isValidEmail(email)) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("Erro no e-mail");
            edt_email.setError("E-mail inválido");
            return false;

        } else {

            if(retornoemail.equals("")){
                layout_email.setErrorEnabled(true);
                layout_email.setError("Erro no e-mail");
                edt_email.setError("E-mail inválido");
                return false;
            }
            if(retornoemail.equals("N")){
                layout_email.setErrorEnabled(true);
                layout_email.setError("Erro no e-mail");
                edt_email.setError("E-mail inválido");
                return false;
            }
            if(retornoemail.equals("S")){
                layout_email.setErrorEnabled(false);
                return true;
            }

        }

        layout_email.setErrorEnabled(false);

        return true;

    }





    public boolean validarFantasia() {

        String fantasia = edt_fantasia.getText().toString().trim();

        if (fantasia.isEmpty() && edt_cgccpf.getText().length() > 11) {
            layout_fantasia.setErrorEnabled(true);
            layout_fantasia.setError("Informe o nome de fantasia");

            return false;

        } else {
            layout_fantasia.setErrorEnabled(false);
            return true;
        }

    }


    public boolean validarData() {

        String data = edt_data.getText().toString().trim();

        if (!data.isEmpty() && data.length() < 10) {
            layout_data.setErrorEnabled(true);
            layout_data.setError("Erro na data");
            edt_data.setError("Informa a data dd/mm/aaaa");
            return false;

        } else {
            layout_data.setErrorEnabled(false);
            return true;
        }

    }

    public boolean validarTelefone() {

        String fone = edt_telefone.getText().toString().trim();

        if (!fone.isEmpty() && fone.length() < 13) {
            layout_telefone.setErrorEnabled(true);
            layout_telefone.setError("Erro no telefone");
            edt_telefone.setError("Telefone com formato inválido");
            return false;

        } else {
            layout_telefone.setErrorEnabled(false);
            return true;
        }

    }


    public boolean validarWpp() {

        String wpp = edt_celular.getText().toString().trim();

        if (!wpp.isEmpty() && wpp.length() < 13) {
            layout_celular.setErrorEnabled(true);
            layout_celular.setError("Erro no celular");
            edt_celular.setError("celular com formato inválido");
            return false;

        } else {
            layout_celular.setErrorEnabled(false);
            return true;
        }

    }


    public boolean validarNome() {

        String nome = edt_nomecli.getText().toString().trim();

        if (nome.isEmpty()) {
            layout_nomeCli.setErrorEnabled(true);
            layout_nomeCli.setError("Informe o nome do cliente");

            return false;


        } else {
            layout_nomeCli.setErrorEnabled(false);
            return true;
        }

    }

    public boolean validarConsultor() {



        if(usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS CORPORATIVO FX U") || usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS FX U")){

          if(!ClientesComplementoFragments.consultor.equals("")){
              return true;

          }else{
              Toast.makeText(context,"Para finalizar o cadastro informe o consultor!",Toast.LENGTH_SHORT).show();
              return false;
          }



        } else {

            return true;
        }

    }


    public boolean validarSegmento() {



        if(edt_cgccpf.getText().toString().trim().length() > 11){

            if(!ClientesComplementoFragments.edt_segmento.getText().toString().equals("")){
                return true;

            }else{
                Toast.makeText(context,"Para finalizar o cadastro informe o segmento da empresa!",Toast.LENGTH_SHORT).show();
                return false;
            }



        } else {

            return true;
        }

    }



    public boolean validarCelular() {

        String celular = edt_wpp.getText().toString().trim();

        if (celular.isEmpty()) {
            layout_wpp.setErrorEnabled(true);
            layout_wpp.setError("Informe o nº de WhatsApp");

            return false;
        }
        if (!celular.isEmpty() && celular.length() < 13) {
            layout_wpp.setErrorEnabled(true);
            layout_wpp.setError("Erro no WhatsApp");
            edt_wpp.setError("WhatsApp com formato inválido");
            return false;

        } else {
            layout_wpp.setErrorEnabled(false);
            return true;
        }

    }

    private static boolean isValidEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void ConsultarClienteCgccpf(final String cgccpf) {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listClientes = helper.ConsultarClientesCgccpf(cgccpf, Integer.parseInt(usuarioLogado.get("codfil")), usuarioLogado.get("cpf"));
                    if (listClientes == null || listClientes.size()==0) {

                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                clienteAlteracao = listClientes.get(0);
                                carregaClienteNoFormulario(listClientes.get(0));
                                ClientesEnderecosFragments.clienteAlteracao = listClientes.get(0);
                                if(listClientes.get(0).getEnderecos()!=null) {
                                    ClientesEnderecosFragments.mList = listClientes.get(0).getEnderecos();
                                    EnderecosAdapter adapter = (EnderecosAdapter) ClientesEnderecosFragments.mRecycleView.getAdapter();
                                    for(int i = 0; i < ClientesEnderecosFragments.mList.size(); i++){
                                        adapter.addListitem(listClientes.get(0).getEnderecos().get(i),i);
                                    }
                                }

                                edt_cgccpf.setEnabled(false);

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

                    dialog.dismiss();


                }
            }

        }.start();
    }




    public void ConsultarClienteParaOrcamento(final String cgccpf) {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listClientes = helper.ConsultarClientesCgccpf(cgccpf, Integer.parseInt(usuarioLogado.get("codfil")), usuarioLogado.get("cpf"));
                    if (listClientes == null || listClientes.size()==0) {

                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(isOrcamento) {
                                     Orcamento_LancamentoActivity.carregaDadosCliente(listClientes.get(0));
                                    ((ClientesCadastroActivity) context).finish();

                                }else{
                                    Intent intent = new Intent(context,Orcamento_LancamentoActivity.class);
                                    intent.putExtra("cliente",listClientes.get(0));
                                    context.startActivity(intent);
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

                    dialog.dismiss();


                }
            }

        }.start();
    }

    public void ConsultarEmail(final String email) {


        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Validando e-mail...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {
                try {

                    validacaoEmail = helper.validarEmail(email);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (validacaoEmail == null || validacaoEmail.Validacao == null) {
                                layout_email.setErrorEnabled(false);
                                retornoemail = "S";
                            } else {

                                switch (validacaoEmail.Validacao.toUpperCase()) {

                                    case "INVALIDO":

                                        layout_email.setErrorEnabled(true);
                                        layout_email.setError("Erro no e-mail");
                                        edt_email.setError("E-mail inválido");
                                        retornoemail =  "N";
                                        break;


                                    default:
                                        layout_email.setErrorEnabled(false);
                                        retornoemail =  "S";
                                        break;

                                }

                            }


                        }

                    });

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

                    dialog.dismiss();


                }
            }

        }.start();



    }


}

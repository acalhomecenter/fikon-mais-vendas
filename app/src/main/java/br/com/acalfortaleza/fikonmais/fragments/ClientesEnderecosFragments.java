
package br.com.acalfortaleza.fikonmais.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
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
import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.EnderecosAdapter;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Enderecos;

public class ClientesEnderecosFragments extends Fragment implements RecyclerViewOnClickListenerHack {


    public ClientesEnderecosFragments() {


    }

    private View layout;
    private TextInputLayout layout_cep;
    private TextInputLayout layout_end;
    private TextInputLayout layout_bairro;
    private static TextInputEditText edt_cep;
    private static TextInputEditText edt_logradouro;
    private static TextInputEditText edt_endereco;
    private static TextInputEditText edt_numero;
    private static TextInputEditText edt_bairro;
    private static TextInputEditText edt_cidade;
    private static TextInputEditText edt_complemento;
    private static TextInputEditText edt_uf;
    private static TextInputEditText edt_referencia;
    public static RecyclerView mRecycleView;
    public static List<Enderecos> mList;
    private static Button btn_Adcionar;
    public static Cliente clienteAlteracao;
    public static EnderecosAdapter adapter;
    private ProgressDialog dialog;
    private static Enderecos enderecoConsultado;
    private ClienteHelper helper;
    private Handler handler;
    private String retorno;
    private int position;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null ) {

            clienteAlteracao = (Cliente) getArguments().getSerializable("cliente");

        }

        helper = new ClienteHelper(getContext());
        handler = new Handler();

    }

    public static void limparEnderecoNoFormulario() {


        edt_cep.setText("");
        edt_logradouro.setText("");
        edt_endereco.setText("");
        edt_numero.setText("");
        edt_bairro.setText("");
        edt_cidade.setText("");
        edt_uf.setText("");
        edt_complemento.setText("");
        edt_referencia.setText("");
        btn_Adcionar.setText("Adicionar");

        enderecoConsultado = null;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void carregarLista(List<Enderecos> mList, Cliente clienteAlteracao) {
        this.mList = mList;
        adapter = new EnderecosAdapter(getActivity(), mList);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecycleView.setAdapter(adapter);


        if (clienteAlteracao != null && clienteAlteracao.getEnderecos() != null) {

            this.mList = clienteAlteracao.getEnderecos();
            EnderecosAdapter adapterAdd = (EnderecosAdapter) mRecycleView.getAdapter();
            for (int i = 0; i < this.mList.size(); i++) {

                adapterAdd.addListitem(this.mList.get(i), i);

            }
        }
    }

    public void carregaEnderecoNoFormulario(Enderecos endereco) {


        this.edt_cep.setText(endereco.getCep());
        this.edt_logradouro.setText(endereco.getCodlograd());
        this.edt_endereco.setText(endereco.getEnder());
        this.edt_numero.setText(endereco.getNumero());
        this.edt_bairro.setText(endereco.getBairro());
        this.edt_cidade.setText(endereco.getCidade());
        this.edt_uf.setText(endereco.getUf());
        this.edt_complemento.setText(endereco.getComplemento());
        this.edt_referencia.setText(endereco.getReferencia());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragments_clientes_endereco,
                container, false);


        layout_cep = layout.findViewById(R.id.layout_cli_edtCep);
        layout_end = layout.findViewById(R.id.layout_cli_edtEndereco);
        layout_bairro = layout.findViewById(R.id.layout_cli_edtBairro);
        edt_cep = layout.findViewById(R.id.cli_edtCep);
        edt_logradouro = layout.findViewById(R.id.cli_edtLogradouro);
        edt_endereco = layout.findViewById(R.id.cli_edtEndereco);
        edt_numero = layout.findViewById(R.id.cli_edtNumero);
        edt_bairro = layout.findViewById(R.id.cli_edtBairro);
        edt_cidade = layout.findViewById(R.id.cli_edtCidade);
        edt_uf = layout.findViewById(R.id.cli_edtUf);
        edt_complemento = layout.findViewById(R.id.cli_edtComplemento);
        edt_referencia = layout.findViewById(R.id.cli_edtReferencia);
        mRecycleView = layout.findViewById(R.id.cli_listaEnderecos);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        btn_Adcionar = layout.findViewById(R.id.cli_end_btnAdcionar);


        carregarLista(new ArrayList<Enderecos>(), clienteAlteracao);


        edt_cep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (validarCep()) {
                        if (enderecoConsultado == null) {
                            ConsultarEndereco(edt_cep.getText().toString().trim());
                        }
                        if (enderecoConsultado != null && !enderecoConsultado.getCep().equals(edt_cep.getText().toString().trim())) {
                            ConsultarEndereco(edt_cep.getText().toString().trim());
                        }
                    }
                }
            }
        });

        btn_Adcionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCep() && validarEndereco() && validarBairro()) {

                    if (btn_Adcionar.getText().equals("Adicionar") && clienteAlteracao == null) {

                        Enderecos enderecoFormulario = getEnderecoDoFormulario(mList.size());
                        adapter.addListitem(enderecoFormulario, mList.size());
                        Toast.makeText(getActivity(), "Endereço adcionado!", Toast.LENGTH_SHORT).show();
                        limparEnderecoNoFormulario();


                    } else {
                        if (btn_Adcionar.getText().equals("Adicionar") && clienteAlteracao.getTipo().equals("CADASTRADO")) {

                                inserirEndereco(getEnderecoDoFormulario(mList.size()),"ADICIONAR");

                            } else {
                            if(btn_Adcionar.getText().equals("Adicionar") && !clienteAlteracao.getTipo().equals("CADASTRADO") ){
                                Enderecos enderecoFormulario = getEnderecoDoFormulario(0);
                                adapter.addListitem(enderecoFormulario, mList.size());
                                Toast.makeText(getActivity(), "Endereço adcionado!", Toast.LENGTH_SHORT).show();
                                limparEnderecoNoFormulario();
                            }

                            }
                        }


                    if (btn_Adcionar.getText().equals("Alterar") && clienteAlteracao == null) {
                        Toast.makeText(getActivity(), "Endereço alterado!", Toast.LENGTH_SHORT).show();
                        Enderecos enderecoFormulario = getEnderecoDoFormulario(position);
                        adapter.updateListitem(enderecoFormulario, position);
                        limparEnderecoNoFormulario();

                    }
                    if (btn_Adcionar.getText().equals("Alterar") && clienteAlteracao.getTipo().equals("CADASTRADO")) {

                        inserirEndereco(getEnderecoDoFormulario(position),"ALTERAR");

                    }
                    if (btn_Adcionar.getText().equals("Alterar") && !clienteAlteracao.getTipo().equals("CADASTRADO")) {

                        Toast.makeText(getActivity(), "Endereço alterado!", Toast.LENGTH_SHORT).show();
                        Enderecos enderecoFormulario = getEnderecoDoFormulario(position);
                        adapter.updateListitem(enderecoFormulario, position);
                        mList.set(position,enderecoFormulario);
                        limparEnderecoNoFormulario();
                    }


                }

            }


        });


        return layout;
    }

    public Enderecos getEnderecoDoFormulario(int position) {

        Enderecos endereco = new Enderecos();

        int codcli = 0;
        if (clienteAlteracao != null) {
            codcli = clienteAlteracao.getCodcli();
        }
        String cep = edt_cep.getText().toString();
        String logradouro = edt_logradouro.getText().toString();
        String ender = edt_endereco.getText().toString().replace("'","");
        String numero = edt_numero.getText().toString();
        String bairro = edt_bairro.getText().toString().replace("'","");
        String cidade = edt_cidade.getText().toString().replace("'","");
        String complemento = edt_complemento.getText().toString().replace("'","");
        String uf = edt_uf.getText().toString();
        String referencia = edt_referencia.getText().toString();
        String inscricao = ClientesGeralFragments.edt_inscricao.getText().toString();
        String email = ClientesGeralFragments.edt_email.getText().toString();
        String dddfone1 = "";
        String telefone = "";
        if (ClientesGeralFragments.edt_telefone.length() > 0) {
            dddfone1 = ClientesGeralFragments.edt_telefone.getText().toString().substring(1, 3);
            try{
                telefone = ClientesGeralFragments.edt_telefone.getText().toString().substring(4, 8) + ClientesGeralFragments.edt_telefone.getText().toString().substring(9);
            }catch (Exception e){
                telefone = ClientesGeralFragments.edt_telefone.getText().toString().substring(4, 8);
            }

        }
        String dddcelular = "";
        String celular = "";
        if (ClientesGeralFragments.edt_celular.length() > 0) {
            dddcelular = ClientesGeralFragments.edt_celular.getText().toString().substring(1, 3);
            try{
                celular = ClientesGeralFragments.edt_celular.getText().toString().substring(4, 9) + ClientesGeralFragments.edt_celular.getText().toString().substring(10);
            }catch (Exception e){
                celular = ClientesGeralFragments.edt_celular.getText().toString().substring(4, 9);
            }

        }
        String dddwpp = "";
        String wpp = "";
        if (ClientesGeralFragments.edt_wpp.length() > 0) {
            dddwpp = ClientesGeralFragments.edt_wpp.getText().toString().substring(1, 3);
            try{
                wpp = ClientesGeralFragments.edt_wpp.getText().toString().substring(4, 9) + ClientesGeralFragments.edt_wpp.getText().toString().substring(10);
            }catch (Exception e){
                wpp = ClientesGeralFragments.edt_wpp.getText().toString().substring(4, 9);
            }

        }
        String Natjur = "";
        if (ClientesGeralFragments.edt_cgccpf.getText().toString().length() == 11) {
            Natjur = "F";
        }

        if (ClientesGeralFragments.edt_cgccpf.getText().toString().length() > 11) {
            Natjur = "J";
        }

        if (ClientesGeralFragments.edt_cgccpf.getText().toString().length() < 11) {
            Natjur = "F";
        }

        if (ClientesGeralFragments.edt_cgccpf.getText().toString().equals("")) {
            Natjur = "F";
        }


        endereco.setCodcli(codcli);
        endereco.setCgccpf(ClientesGeralFragments.edt_cgccpf.getText().toString());
        endereco.setCep(cep);
        endereco.setCodend(position);
        endereco.setCodlograd(logradouro);
        endereco.setEnder(ender);
        endereco.setNumero(numero);
        endereco.setBairro(bairro);
        endereco.setCidade(cidade);
        endereco.setComplemento(complemento);
        endereco.setUf(uf);
        endereco.setReferencia(referencia);
        endereco.setInscricao(inscricao);
        if (position == 0) {
            endereco.setDddFone1(dddfone1);
            endereco.setFone1(telefone);
            endereco.setDddFone2(dddcelular);
            endereco.setFone2(celular);
            endereco.setDddCelular(dddwpp);
            endereco.setCelular(wpp);
            endereco.setEmail(email);
            if (Natjur.equals("F")) {
                endereco.setTpender("R");
            } else {
                endereco.setTpender("C");
            }
        } else {
            endereco.setDddFone1(dddfone1);
            endereco.setFone1(telefone);
            endereco.setDddFone2(dddcelular);
            endereco.setFone2(celular);
            endereco.setDddCelular(dddwpp);
            endereco.setCelular(wpp);
            endereco.setEmail(email);
            endereco.setTpender("E");
        }
        if(enderecoConsultado != null){
            endereco.setPraca(enderecoConsultado.getPraca());
            //endereco.setVlfrete(enderecoConsultado.getVlfrete());
            enderecoConsultado.setFldomingo(enderecoConsultado.getFldomingo());
            enderecoConsultado.setFlsegunda(enderecoConsultado.getFlsegunda());
            enderecoConsultado.setFlterca(enderecoConsultado.getFlterca());
            enderecoConsultado.setFlquarta(enderecoConsultado.getFlquarta());
            enderecoConsultado.setFlquinta(enderecoConsultado.getFlquinta());
            enderecoConsultado.setFlsexta(enderecoConsultado.getFlsexta());
            enderecoConsultado.setFlsabado(enderecoConsultado.getFlsabado());

        }

        return endereco;
    }


    @Override
    public void onClickListener(View view, int position) {

        this.position = position;
        carregaEnderecoNoFormulario(mList.get(position));
        enderecoConsultado=mList.get(position);
        btn_Adcionar.setText("Alterar");



    }

    public void ConsultarEndereco(final String cep) {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    enderecoConsultado = helper.ConsultarEndereco(cep);
                    if (enderecoConsultado == null || enderecoConsultado.getCep() == null) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                limparEnderecoNoFormulario();
                                Toast.makeText(getActivity(), "Cep não cadastrado, solicite o cadastro do cep.", Toast.LENGTH_LONG).show();
                                edt_cep.requestFocus();
                            }
                        });


                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                carregaEnderecoNoFormulario(enderecoConsultado);

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

    public void inserirEndereco(final Enderecos endereco, final String tipo) {

        dialog = new ProgressDialog(getActivity());
        if(tipo.equals("ADICIONAR")) {
            dialog.setMessage("Salvando...");
        }else{
            dialog.setMessage("Alterando...");
        }
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    retorno = helper.SetEndereco(endereco, tipo);

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

                            if (retorno.equals("Endereco cadastrado com sucesso!")) {
                                EnderecosAdapter enderecosAdapter = (EnderecosAdapter) mRecycleView.getAdapter();
                                enderecosAdapter.addListitem(getEnderecoDoFormulario(mList.size()), mList.size());
                                mList.add(getEnderecoDoFormulario(mList.size()));
                                limparEnderecoNoFormulario();
                            }

                            if(retorno.equals("Endereco alterado com sucesso!")){
                                Enderecos enderecoFormulario = getEnderecoDoFormulario(position);
                                adapter.updateListitem(enderecoFormulario, position);
                                limparEnderecoNoFormulario();
                            }


                        }
                    });
                }
            }

        }.start();
    }

    public boolean validarCep() {

        String cep = edt_cep.getText().toString().trim();

        if (cep.isEmpty()) {
            layout_cep.setErrorEnabled(true);
            layout_cep.setError("Informe um cep");
            return false;

        } else {

            if (cep.length() < 8) {
                layout_cep.setErrorEnabled(true);
                layout_cep.setError("Erro no cep");
                edt_cep.setError("Cep informado, menor que 8 digitos");
                return false;


            } else {

                layout_cep.setErrorEnabled(false);
                return true;

            }


        }

    }


    public boolean validarEndereco() {

        String end = edt_endereco.getText().toString().trim();

        if (end.isEmpty()) {
            layout_end.setErrorEnabled(true);
            layout_end.setError("Informe um endereço");
            return false;

        } else {

            layout_end.setErrorEnabled(false);
                return true;

            }


        }


    public boolean validarBairro() {

        String end = edt_bairro.getText().toString().trim();

        if (end.isEmpty()) {
            layout_bairro.setErrorEnabled(true);
            layout_bairro.setError("Informe o bairro");
            return false;

        } else {

            layout_bairro.setErrorEnabled(false);
            return true;

        }


    }




}

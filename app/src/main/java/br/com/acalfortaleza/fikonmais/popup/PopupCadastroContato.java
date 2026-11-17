package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.applications.Mask;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.helper.FikonApiClient;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Cargo;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Setor;
import br.com.acalfortaleza.fikonmais.models.TheChecker;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEmail;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupCadastroContato extends Activity {

    /** Código (ou CPF/CNPJ) do cliente na Fikon, dono do contato. */
    public static final String EXTRA_CODIGO_CLIENTE_FIKON = "codigoClienteFikon";

    public static TextInputEditText edt_cgccpf;
    public static TextInputEditText edt_celular;
    public static TextInputEditText edt_telefone;
    public static TextInputEditText edt_wpp;
    public static TextInputEditText edt_email;
    public static TextInputEditText edt_setor;
    public static TextInputEditText edt_cargo;
    public static String retornoemail = "";
    public Contato contatoFormulario;
    TheChecker checker = new TheChecker();
    private TextInputLayout layout_cgccpf;
    private TextInputEditText edt_nomecli;
    private TextInputEditText edt_nascimennto;
    private TextInputLayout layout_nascimento;
    private TextInputLayout layout_nomeCli;
    private TextInputLayout layout_celular;
    private TextInputLayout layout_telefone;
    private TextInputLayout layout_wpp;
    private TextInputLayout layout_email;
    private TextInputLayout layout_setor;
    private TextInputLayout layout_cargo;
    private Button btn_fechar;
    private Button btn_gravar;
    private ProgressDialog dialog;
    private Handler handler;
    private Contato contatoAlteracao;
    private String retorno;
    private List<Contato> listContato;
    private ClienteHelper helper;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private List<Setor> mListSetor;
    private List<Cargo> mListCargo;
    private Setor setorSelecionado;
    private Cargo cargoSelecionado;
    private ArrayList<String> listaSetor = new ArrayList<>();
    private ArrayList<String> listacargo = new ArrayList<>();
    private SpinnerDialog spinnerDialogSetor;
    private SpinnerDialog spinnerDialogCargo;
    private ValidacaoEmail validacaoEmail = new ValidacaoEmail();

    private static boolean isValidEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_cadastrocontato);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 1.0), (int) (height * .9));

        helper = new ClienteHelper(PopupCadastroContato.this);
        loginHelper = new LoginHelper(PopupCadastroContato.this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        handler = new Handler();


        edt_cgccpf = findViewById(R.id.cont_edtCpf);
        edt_nomecli = findViewById(R.id.cont_edtNomeContato);
        edt_nascimennto = findViewById(R.id.cont_edtNascimento);
        edt_setor = findViewById(R.id.cont_edtSetor);
        edt_cargo = findViewById(R.id.cont_edtCargo);
        edt_wpp = findViewById(R.id.cli_edtWpp);
        edt_email = findViewById(R.id.cli_edtEmail);
        edt_telefone = findViewById(R.id.cli_edtTelefone);
        edt_celular = findViewById(R.id.cli_edtCelular);
        btn_fechar = findViewById(R.id.pop_cont_btnFechar);
        btn_gravar = findViewById(R.id.cont_btn_gravar);
        layout_cgccpf = findViewById(R.id.layout_cont_cpf);
        layout_nascimento = findViewById(R.id.layout_cont_edtNascimento);
        layout_nomeCli = findViewById(R.id.layout_cont_nomeContato);
        layout_email = findViewById(R.id.layout_cli_edtEmail);
        layout_cargo = findViewById(R.id.layout_cont_cargo);
        layout_setor = findViewById(R.id.layout_cont_setor);
        layout_celular = findViewById(R.id.layout_cli_edtCelular);
        layout_wpp = findViewById(R.id.layout_cli_edtWpp);
        layout_telefone = findViewById(R.id.layout_cli_edtTelefone);




        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        edt_cgccpf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (validarCgccpf()) {


                        if (contatoAlteracao == null) {
                            ConsultarContatoCgccpf(edt_cgccpf.getText().toString().trim());

                        }

                    }
                }

            }
        });


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

                if ( !validarEmail() || !validarNome() || !validarCelular() || !validarData() || !validarTelefone() || !validarWpp()) {

                } else {

                    if (btn_gravar.getText().equals("Alterar")) {

                        salvarContato("ALTERAR");

                    }


                    if (btn_gravar.getText().equals("Cadastrar")) {

                        if (contatoAlteracao == null) {

                            salvarContato("ADICIONAR");

                        }


                    }


                }


            }
        });


        if(getIntent().getSerializableExtra("contato") != null){
            contatoAlteracao = (Contato) getIntent().getSerializableExtra("contato");
            carregaContatoNoFormulario(contatoAlteracao);
        }

        if (helper.isFikon()) {
            // Contato na Fikon não tem CPF nem data de nascimento.
            layout_cgccpf.setVisibility(View.GONE);
            layout_nascimento.setVisibility(View.GONE);
            if (contatoAlteracao != null) {
                // A API só inclui contatos: o já cadastrado abre só para consulta.
                btn_gravar.setVisibility(View.GONE);
                Toast.makeText(this, FikonApiClient.MENSAGEM_CONTATO_SO_INCLUSAO,
                        Toast.LENGTH_LONG).show();
            }
        }

        ConsultarCombox();


        spinnerDialogSetor = new SpinnerDialog(PopupCadastroContato.this, listaSetor, "Setor");
        spinnerDialogSetor.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edt_setor.setText(item);

                if(contatoAlteracao != null){
                    contatoAlteracao.setSetor(mListSetor.get(position));
                }
                setorSelecionado = mListSetor.get(position);



            }
        });


        spinnerDialogCargo = new SpinnerDialog(PopupCadastroContato.this, listacargo, "Cargo");
        spinnerDialogCargo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edt_cargo.setText(item);

                if(contatoAlteracao != null){
                    contatoAlteracao.setCargo(mListCargo.get(position));
                }
                cargoSelecionado = mListCargo.get(position);



            }
        });



        edt_setor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_setor.getRight() - edt_setor.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogSetor.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edt_cargo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_cargo.getRight() - edt_cargo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogCargo.showSpinerDialog();

                    }
                }
                return false;
            }
        });

        edt_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {
                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                    if(!edt_email.getText().toString().trim().equals("")){
                        ConsultarEmail(edt_email.getText().toString().trim());
                    }
                }
                return false;
            }
        });



    }

    public void ConsultarCombox(){
        dialog = new ProgressDialog(PopupCadastroContato.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {


                    mListSetor = helper.ConsultarSetor();
                    mListCargo = helper.ConsultarCargosContatos();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {




                            if (mListSetor != null && mListSetor.size() > 0) {
                                for (Setor s : mListSetor) {
                                    listaSetor.add(s.getDescricao());
                                }
                            }



                            if (mListCargo != null && mListCargo.size() > 0) {
                                for (Cargo c : mListCargo) {
                                    listacargo.add(c.getDescricao());
                                }


                            }

                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new androidx.appcompat.app.AlertDialog.Builder(PopupCadastroContato.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupCadastroContato.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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


        edt_nascimennto.addTextChangedListener(Mask.insert("##/##/####", edt_nascimennto));


    }

    public void salvarContato(final String tipo) {


        dialog = new ProgressDialog(this);
        if (tipo.equals("ADICIONAR") ) {
            dialog.setMessage("Salvando...");
        }
        if (tipo.equals("ALTERAR") ) {
            dialog.setMessage("Alterando...");
        }


        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                contatoFormulario = getContatoDoFormulario();

                try {

                    retorno = helper.SetContato(contatoFormulario, tipo);

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

                            Toast.makeText(PopupCadastroContato.this, retorno, Toast.LENGTH_LONG).show();
                            if (retorno != null && (retorno.equals("Contato cadastrado com sucesso!") || retorno.equals("Contato alterado com sucesso!"))) {
                                limparClienteNoFormulario();


                            }


                        }
                    });
                }

            }
        }.start();

    }

    public Contato getContatoDoFormulario() {

        Contato contato = new Contato();


        String cgccpf = edt_cgccpf.getText().toString();
        String natjur = "";

        String nome = edt_nomecli.getText().toString().replaceAll("'", " ");

        Date date = null;

        String dtnascimento = edt_nascimennto.getText().toString();

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
            dddcelular = edt_celular.getText().toString().substring(1, 3);
            try {
                celular = edt_celular.getText().toString().substring(4, 9) + edt_celular.getText().toString().substring(10);
            } catch (Exception e) {
                celular = edt_celular.getText().toString().substring(4, 9);
            }

        }
        String dddwpp = "";
        String wpp = "";
        if (edt_wpp.length() > 0) {
            dddwpp = edt_wpp.getText().toString().substring(1, 3);
            try {
                wpp = edt_wpp.getText().toString().substring(4, 9) + edt_wpp.getText().toString().substring(10);
            } catch (Exception e) {
                wpp = edt_wpp.getText().toString().substring(4, 9);
            }

        }
        int codcli = 0;
        if(contatoAlteracao != null && contatoAlteracao.getCodcli()>0){
            codcli = contatoAlteracao.getCodcli();
        }else{
             codcli = getIntent().getIntExtra("codcli",0);
        }



        contato.setCodcli(codcli);
        contato.setCodigoClienteFikon(contatoAlteracao != null
                && contatoAlteracao.getCodigoClienteFikon() != null
                ? contatoAlteracao.getCodigoClienteFikon()
                : getIntent().getStringExtra(EXTRA_CODIGO_CLIENTE_FIKON));
        contato.setCgccpf(cgccpf);
        contato.setNomeContato(nome);
        contato.setEmail(email);
        contato.setDddFone1(dddfone1);
        contato.setFone1(telefone);
        contato.setDddCelular(dddwpp);
        contato.setCelular(wpp);
        contato.setDddFone2(dddcelular);
        contato.setFone2(celular);
        contato.setDtnascimento(dtnascimento);

        if(contatoAlteracao != null){
            contato.setSetor(contatoAlteracao.getSetor());
            contato.setCargo(contatoAlteracao.getCargo());
            contato.setFilialCadastro(contatoAlteracao.getFilialCadastro());
            contato.setConsultor(contatoAlteracao.getConsultor());
        }else {
            contato.setSetor(setorSelecionado);
            contato.setCargo(cargoSelecionado);
            contato.setFilialCadastro(Integer.parseInt(usuarioLogado.get("codfil")));
            contato.setConsultor(Integer.parseInt(usuarioLogado.get("codigo")));
        }



        return contato;
    }

    public void limparClienteNoFormulario() {


        this.edt_cgccpf.setText("");
        this.edt_nomecli.setText("");
        this.edt_telefone.setText("");
        this.edt_celular.setText("");
        this.edt_wpp.setText("");
        this.edt_email.setText("");
        this.btn_gravar.setText("Cadastrar");
        this.edt_cgccpf.setEnabled(true);
        this.edt_nascimennto.setText("");
        this.edt_setor.setText("");
        this.edt_cargo.setText("");

        contatoAlteracao = null;


        finish();


    }

    public boolean validarCgccpf() {

        String cgccpf = edt_cgccpf.getText().toString().trim();

        if (!cgccpf.isEmpty()) {


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
        }

            return false;



    }

    public boolean validarEmail() {


        String email = edt_email.getText().toString().trim();


        if (email.isEmpty()) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("Erro no e-mail");
            edt_email.setError("E-mail é obrigatório");
            return false;
        }
        if (!isValidEmail(email)) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("Erro no e-mail");
            edt_email.setError("E-mail inválido");
            return false;

        } else {


            if (retornoemail.equals("")) {
                layout_email.setErrorEnabled(true);
                layout_email.setError("Erro no e-mail");
                edt_email.setError("E-mail inválido");
                return false;
            }
            if (retornoemail.equals("N")) {
                layout_email.setErrorEnabled(true);
                layout_email.setError("Erro no e-mail");
                edt_email.setError("E-mail inválido");
                return false;
            }
            if (email.equals("") || retornoemail.equals("S")) {
                layout_email.setErrorEnabled(false);

                return true;
            }


        }
        layout_email.setErrorEnabled(false);

        return true;

    }

    public boolean validarData() {

        String data = edt_nascimennto.getText().toString().trim();

        if (!data.isEmpty() && data.length() < 10) {
            layout_nascimento.setErrorEnabled(true);
            layout_nascimento.setError("Erro na data");
            edt_nascimennto.setError("Informa a data dd/mm/aaaa");
            return false;

        } else {
            layout_nascimento.setErrorEnabled(false);
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

    public boolean validarCelular() {

        String celular = edt_wpp.getText().toString().trim();

        if (celular.isEmpty()) {
            layout_wpp.setErrorEnabled(true);
            layout_wpp.setError("Informe o nº de celular");

            return false;
        }
        if (!celular.isEmpty() && celular.length() < 14) {
            layout_wpp.setErrorEnabled(true);
            layout_wpp.setError("Erro no celular");
            edt_wpp.setError("Celular com formato inválido");
            return false;

        } else {
            layout_wpp.setErrorEnabled(false);
            return true;
        }

    }

    public void ConsultarContatoCgccpf(final String cgccpf) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    listContato = helper.ConsultarContatoCgccpf((cgccpf));
                    if (listContato == null || listContato.size() == 0) {

                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                contatoAlteracao = listContato.get(0);
                                carregaContatoNoFormulario(contatoAlteracao);

                                edt_cgccpf.setEnabled(false);

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PopupCadastroContato.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PopupCadastroContato.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {

                    dialog.dismiss();


                }
            }

        }.start();
    }


    public void ConsultarEmail(final String email) {


        dialog = new ProgressDialog(this);
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
                                        retornoemail = "N";
                                        break;

                                    default:
                                        layout_email.setErrorEnabled(false);
                                        retornoemail = "S";
                                        break;

                                }

                            }


                        }

                    });

                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PopupCadastroContato.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PopupCadastroContato.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {

                    dialog.dismiss();


                }
            }

        }.start();


    }

    public void carregaContatoNoFormulario(Contato contato) {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
        this.edt_cgccpf.setText(contato.getCgccpf());
        this.edt_nomecli.setText(contato.getNomeContato());

        try {
            if (!contato.getDtnascimento().equals("")) {
                Date date = entrada.parse(contato.getDtnascimento());
                this.edt_nascimennto.setText(formato.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!contato.getFone1().equals("") && contato.getFone1().length() >= 8) {
            this.edt_telefone.setText("(" + contato.getDddFone1() + ")" + contato.getFone1().substring(0, 4) + "-" + contato.getFone1().substring(4, 8));
        } else {
            this.edt_telefone.setText(contato.getDddFone1() + contato.getFone1());
        }
        if (!contato.getFone2().equals("") && contato.getFone2().length() >= 9) {
            this.edt_celular.setText("(" + contato.getDddFone2() + ")" + contato.getFone2().substring(0, 5) + "-" + contato.getFone2().substring(5, 9));
        } else {
            this.edt_celular.setText(contato.getDddFone2() + contato.getFone2());
        }
        if (!contato.getCelular().equals("") && contato.getCelular().length() >= 9) {
            this.edt_wpp.setText("(" + contato.getDddCelular() + ")" + contato.getCelular().substring(0, 5) + "-" + contato.getCelular().substring(5, 9));
        } else {
            this.edt_wpp.setText(contato.getDddCelular() + contato.getCelular());
        }
        this.edt_email.setText(contato.getEmail());

        this.edt_setor.setText(contato.getSetor().getDescricao());

        this.edt_cargo.setText(contato.getCargo().getDescricao());

        this.btn_gravar.setText("Alterar");


    }


}

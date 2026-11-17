package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.textfield.TextInputEditText;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.Produto.FiltrosProdutoResponse;
import br.com.acalfortaleza.fikonmais.models.ProdutoDetalhe;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupConsultaProduto extends Activity  {

    private Button btn_consultar;
    private Button btn_fechar;
    private List<ProdutoDetalhe> mListF;
    private List<ProdutoDetalhe> mListL;
    private List<ProdutoDetalhe> mListG;
    private List<ProdutoDetalhe> mListSB;
    private List<ProdutoDetalhe> mListFAM;
    private List<ProdutoDetalhe> mListE;
    private List<ProdutoDetalhe> mListS;
    private ArrayList<String> mListFornecedor = new ArrayList<>();
    private ArrayList<String> mListLinha = new ArrayList<>();
    private ArrayList<String> mListFamilia = new ArrayList<>();
    private ArrayList<String> mListGrupo = new ArrayList<>();
    private ArrayList<String> mListSubGrupo = new ArrayList<>();
    private ArrayList<String> mListEspecific = new ArrayList<>();
    private ArrayList<String> mListSituacao = new ArrayList<>();
    private TextInputEditText edtFornecedor;
    private TextInputEditText edtLinha;
    private TextInputEditText edtFamilia;
    private TextInputEditText edtGrupo;
    private TextInputEditText edtSubGrupo;
    private TextInputEditText edtEspecific;
    private TextInputEditText edtSituacao;
    private TextInputEditText edtCodForne;
    private Switch sPonta;
    private SpinnerDialog spinnerDialogForne;
    private SpinnerDialog spinnerDialogLinha;
    private SpinnerDialog spinnerDialogFamilia;
    private SpinnerDialog spinnerDialogGrupo;
    private SpinnerDialog spinnerDialogSubGrupo;
    private SpinnerDialog spinnerDialogEspecific;
    private SpinnerDialog spinnerDialogSituacao;
    private ProgressDialog dialog;
    private ProdutoHelper helperproduto;
    private Handler handler;
    private static HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private FiltrosProdutoResponse mFiltrosFikon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_consulta_produto);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .9));


        helperproduto = new ProdutoHelper(this);
        handler = new Handler();

        loginHelper = new LoginHelper(this);

        usuarioLogado = loginHelper.GetUsuarioSalvo();

        edtFornecedor = findViewById(R.id.pop_prod_edtForne);
        edtLinha = findViewById(R.id.pop_prod_edtLinha);
        edtFamilia = findViewById(R.id.pop_prod_edtFamilia);
        edtGrupo = findViewById(R.id.pop_prod_edtGrupo);
        edtSubGrupo = findViewById(R.id.pop_prod_edtSubGrupo);
        edtEspecific = findViewById(R.id.pop_prod_edtEspecific);
        edtSituacao = findViewById(R.id.pop_prod_edtSituacao);
        edtCodForne = findViewById(R.id.pop_prod_edtcodforne);
        sPonta = findViewById(R.id.pop_prod_ponta);



        spinnerDialogForne = new SpinnerDialog(PopupConsultaProduto.this, mListFornecedor, "Selecione um Fornecedor");
        spinnerDialogForne.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtFornecedor.setText(item);
                spinnerDialogForne.closeSpinerDialog();

            }
        });


        spinnerDialogLinha = new SpinnerDialog(PopupConsultaProduto.this, mListLinha, "Selecione uma Linha");
        spinnerDialogLinha.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtLinha.setText(item);
                spinnerDialogLinha.closeSpinerDialog();
            }
        });


        spinnerDialogFamilia = new SpinnerDialog(PopupConsultaProduto.this, mListFamilia, "Selecione uma Família");
        spinnerDialogFamilia.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtFamilia.setText(item);
                spinnerDialogFamilia.closeSpinerDialog();
            }
        });

        spinnerDialogGrupo = new SpinnerDialog(PopupConsultaProduto.this, mListGrupo, "Selecione um Grupo");
        spinnerDialogGrupo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtGrupo.setText(item);
                spinnerDialogGrupo.closeSpinerDialog();
            }
        });

        spinnerDialogSubGrupo = new SpinnerDialog(PopupConsultaProduto.this, mListSubGrupo, "Selecione um SubGrupo");
        spinnerDialogSubGrupo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtSubGrupo.setText(item);
                spinnerDialogSubGrupo.closeSpinerDialog();
            }
        });


        spinnerDialogEspecific = new SpinnerDialog(PopupConsultaProduto.this, mListEspecific, "Selecione uma Especificação");
        spinnerDialogEspecific.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtEspecific.setText(item);
                spinnerDialogEspecific.closeSpinerDialog();
            }
        });


        spinnerDialogSituacao = new SpinnerDialog(PopupConsultaProduto.this, mListSituacao, "Selecione uma Situação");
        spinnerDialogSituacao.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtSituacao.setText(item);
                spinnerDialogSituacao.closeSpinerDialog();
            }
        });


        edtFornecedor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;



                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtFornecedor.getRight() - edtFornecedor.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("F");



                    }
                }
                return false;
            }
        });


        edtLinha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtLinha.getRight() - edtLinha.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("L");


                    }
                }
                return false;
            }
        });


        edtFamilia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtFamilia.getRight() - edtFamilia.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("FAM");


                    }
                }
                return false;
            }
        });


        edtGrupo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtGrupo.getRight() - edtGrupo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("G");


                    }
                }
                return false;
            }
        });


        edtSubGrupo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtSubGrupo.getRight() - edtSubGrupo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("SB");


                    }
                }
                return false;
            }
        });

        edtEspecific.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtEspecific.getRight() - edtEspecific.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("E");


                    }
                }
                return false;
            }
        });

        edtSituacao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtSituacao.getRight() - edtSituacao.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        ConsultarCombobox("S");


                    }
                }
                return false;
            }
        });



        btn_consultar = findViewById(R.id.pop_prod_btnconsulta);
        btn_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int codfil;

                String cargo = usuarioLogado == null ? null : usuarioLogado.get("cargo");
                if ("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO".equals(cargo)
                        || "CONSULTOR(A) DE VENDAS CORPORATIVO PLENO JH".equals(cargo)
                        || "CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR".equals(cargo)
                        || "CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR JH".equals(cargo)) {
                    codfil = 150;
                } else {
                    String codfilStr = usuarioLogado == null ? null : usuarioLogado.get("codfil");
                    codfil = Integer.parseInt(codfilStr == null ? "0" : codfilStr);
                }


                if(sPonta.isChecked() || edtFornecedor.getText().length()>1 || edtLinha.getText().length()>1 || edtFamilia.getText().length()>1
                        || edtGrupo.getText().length()>1 || edtSubGrupo.getText().length()>1 || edtEspecific.getText().length()>1 || edtSituacao.getText().length()>1 || edtCodForne.getText().length()>=1) {

                           ConsultaProdutos(edtFornecedor.getText().toString(),
                            edtLinha.getText().toString(),
                            edtFamilia.getText().toString(),
                            edtGrupo.getText().toString(),
                            edtSubGrupo.getText().toString(),
                            edtEspecific.getText().toString(),
                            edtSituacao.getText().toString(),
                            edtCodForne.getText().toString(),
                            codfil
                           );

                }else{
                    Toast.makeText(PopupConsultaProduto.this,"Para consultar escolha algum filtro!",Toast.LENGTH_SHORT).show();
                }



            }
        });

        btn_fechar = findViewById(R.id.pop_prod_btnFechar);
        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                finish();
            }
        });





    }


    @Override
    protected void onStop() {
        super.onStop();
        ConsultaActivity.floatConsultaAvancada.setClickable(true);
    }



    public void ConsultarCombobox(final String tipo) {

        dialog = new ProgressDialog(PopupConsultaProduto.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                   switch (tipo){
                       case "F":
                           carregarFiltrosFikon();
                           adicionarNomes(mFiltrosFikon.getDados().getFornecedores(), mListFornecedor);
                           mostrarSpinner(spinnerDialogForne, mListFornecedor);
                           break;

                       case "L":
                           carregarFiltrosFikon();
                           adicionarNomes(mFiltrosFikon.getDados().getLinhas(), mListLinha);
                           mostrarSpinner(spinnerDialogLinha, mListLinha);
                           break;

                       case "FAM":
                           carregarFiltrosFikon();
                           adicionarNomes(mFiltrosFikon.getDados().getFamilias(), mListFamilia);
                           mostrarSpinner(spinnerDialogFamilia, mListFamilia);
                           break;

                       case "G":
                           carregarFiltrosFikon();
                           adicionarNomes(mFiltrosFikon.getDados().getGrupos(), mListGrupo);
                           mostrarSpinner(spinnerDialogGrupo, mListGrupo);
                           break;

                       case "SB":
                           mListSB = helperproduto.GetProdutosDetalhesSbGrupo();
                           if(mListSB  != null && mListSB.size()>0) {
                               for (ProdutoDetalhe msg : mListSB) {
                                   mListSubGrupo.add(msg.getSubgrupo());
                               }
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       spinnerDialogSubGrupo.showSpinerDialog();
                                   }
                               });
                           }
                           break;

                       case "E":
                           mListE = helperproduto.GetProdutosDetalhesEspecific();
                           if(mListE  != null && mListE.size()>0) {
                               for (ProdutoDetalhe me : mListE) {
                                   mListEspecific.add(me.getEspecificacao());
                               }
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       spinnerDialogEspecific.showSpinerDialog();
                                   }
                               });
                           }
                           break;

                       case "S":
                           mListS = helperproduto.GetProdutosDetalhesSituacao();
                           for (ProdutoDetalhe ms : mListS) {
                               mListSituacao.add(ms.getSituacao());
                           }
                           handler.post(new Runnable() {
                               @Override
                               public void run() {
                                   spinnerDialogSituacao.showSpinerDialog();
                               }
                           });
                   }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupConsultaProduto.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                    final String mensagem = e.getMessage();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupConsultaProduto.this).setTitle("Falha na consulta de filtros").setMessage(
                                    mensagem == null || mensagem.trim().isEmpty()
                                            ? "Não foi possível carregar os filtros de produtos."
                                            : mensagem
                            ).setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    private void carregarFiltrosFikon() throws IOException {
        if (mFiltrosFikon == null) {
            mFiltrosFikon = helperproduto.GetFiltrosProdutos();
        }
        if (mFiltrosFikon == null || mFiltrosFikon.getDados() == null) {
            throw new IOException("A API Fikon não retornou os filtros de produtos.");
        }
    }

    private void adicionarNomes(List<FiltrosProdutoResponse.ItemFiltro> filtros,
                                ArrayList<String> opcoes) {
        opcoes.clear();
        if (filtros == null) {
            return;
        }
        for (FiltrosProdutoResponse.ItemFiltro filtro : filtros) {
            if (filtro != null && filtro.getNome() != null && !filtro.getNome().trim().isEmpty()) {
                opcoes.add(filtro.getNome());
            }
        }
    }

    private void mostrarSpinner(final SpinnerDialog spinner, final ArrayList<String> opcoes) {
        if (opcoes.isEmpty()) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                spinner.showSpinerDialog();
            }
        });
    }


    public void ConsultaProdutos(final String fornecedor, final String linha, final String familia, final String grupo, final String subgrupo, final String especificacao, final String situacao, final String codforne, final int codfil) {


        dialog = new ProgressDialog(PopupConsultaProduto.this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread() {

            public void run() {

                try {
                    if(sPonta.isChecked()){
                        ConsultaActivity.lista = helperproduto.ConsultarProdutoPonta(fornecedor,linha,familia,grupo,subgrupo,especificacao,situacao, codfil);
                    }else{
                        ConsultaActivity.lista = helperproduto.ConsultarProdutoAvancado(fornecedor,linha,familia,grupo,subgrupo,especificacao,situacao,"",codforne, codfil);
                    }

                    ConsultaActivity._isConsultaTexto = true;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(ConsultaActivity.lista == null){
                                Toast.makeText(PopupConsultaProduto.this,"Erro na consulta de produtos", Toast.LENGTH_LONG).show();
                            }
                            else if(ConsultaActivity.lista.size() == 0){
                                Toast.makeText(PopupConsultaProduto.this,"Nenhum produto encontrado", Toast.LENGTH_LONG).show();
                            }
                            else{
                                ConsultaActivity.carregarLista();
                            }

                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PopupConsultaProduto.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PopupConsultaProduto.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        finish();

                    }


                }

            }


        }.start();


    }





    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}

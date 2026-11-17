package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.OrcamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Device;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupRegistrarCelular extends Activity {

    private SpinnerDialog spinnerDialog;
    private SpinnerDialog spinnerDialogApr;
    private TextInputEditText edtTipo;
    private TextInputEditText edtAprovador;
    private Button btn_registrar;
    private LoginHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    private Button btn_fechar;
    private Button btn_copiar;
    private List<String> mList;
    private ArrayList<String> listaTipo = new ArrayList<>();
    private ArrayList<String> listaAprovador = new ArrayList<>();
    private HashMap<String, String> usuarioLogado;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_registrarcelular);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .5));



        helper = new LoginHelper(this);

        usuarioLogado = helper.GetUsuarioSalvo();

        handler = new Handler();
        edtTipo = findViewById(R.id.pop_reg_edtTipoAparelho);
        edtAprovador = findViewById(R.id.pop_reg_edtAprovador);
        btn_registrar = findViewById(R.id.pop_reg_btn_registrar);
        btn_fechar = findViewById(R.id.pop_reg_btnFechar);
        btn_copiar = findViewById(R.id.pop_reg_btn_copy);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ConsultarCombobox();



        spinnerDialog = new SpinnerDialog(PopupRegistrarCelular.this, listaTipo, "Informe o Tipo de aparelho");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtTipo.setText(item);


            }
        });


        edtTipo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtTipo.getRight() - edtTipo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        spinnerDialogApr = new SpinnerDialog(PopupRegistrarCelular.this, listaAprovador, "Informe o aprovador");
        spinnerDialogApr.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtAprovador.setText(item);


            }
        });


        edtAprovador.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtAprovador.getRight() - edtAprovador.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogApr.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        btn_copiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(OrcamentoActivity.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("simple text", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(PopupRegistrarCelular.this,"O ID: "+Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)+" foi copiado!",Toast.LENGTH_LONG).show();
            }
        });


        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtTipo.getText().toString().equals("")){

                    if(!edtTipo.getText().toString().equals("")){
                        Device device = new Device();
                        device.setColaborador(usuarioLogado.get("nome"));
                        device.setCpf(usuarioLogado.get("cpf"));
                        device.setTipo(edtTipo.getText().toString());
                        device.setAndroid_id(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                        device.setMarca(Build.MANUFACTURER);
                        device.setModelo(Build.MODEL);
                        device.setStatus("ATIVO");
                        device.setAprovador(edtAprovador.getText().toString());

                        RegistrarAparelho(device);
                    }else{
                        Toast.makeText(PopupRegistrarCelular.this,"Informe o aprovador!", Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(PopupRegistrarCelular.this,"Informe o tipo de aparelho!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void ConsultarCombobox(){


        listaTipo.add("CORPORATIVO");
        listaTipo.add("PESSOAL");


        dialog = new ProgressDialog(PopupRegistrarCelular.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {


                    mList =   helper.ConsultaAprovador();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {



                            if(mList != null && mList.size()>0){

                                for (int i = 1 ; i < mList.size(); i++) {
                                    listaAprovador.add(mList.get(i));
                                }


                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupRegistrarCelular.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupRegistrarCelular.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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


    public void RegistrarAparelho (Device device) {

        dialog = new ProgressDialog(PopupRegistrarCelular.this);
        dialog.setTitle("Registrando...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {


                    retorno = helper.SetDevice(device);


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            if (retorno.equals("CADASTRADO")) {

                                Toast.makeText(PopupRegistrarCelular.this, "Aparelho foi registrado!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PopupRegistrarCelular.this, "Aparelho não foi registrado!", Toast.LENGTH_SHORT).show();
                                finish();
                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupRegistrarCelular.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupRegistrarCelular.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

}

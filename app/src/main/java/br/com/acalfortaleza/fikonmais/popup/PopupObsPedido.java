package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;

import br.com.acalfortaleza.fikonmais.PedidosActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;

public class PopupObsPedido extends Activity {

    private String obs;
    private String pedido;
    private int status;
    private Button btn_alterar;
    private EditText edtObs;
    private TextView txtPedido;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    private PedidosHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_obs_pedido);


        pedido =  getIntent().getStringExtra("numpedido");
        obs =  getIntent().getStringExtra("obs");
        status = getIntent().getIntExtra("status",0);
        helper = new PedidosHelper();
        handler = new Handler();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .5));


        btn_alterar = findViewById(R.id.pop_obs_btn_Alterar);
        edtObs = findViewById(R.id.pop_obs_edtObs);
        txtPedido = findViewById(R.id.pop_obs_pedido);

        edtObs.setText(obs);
        txtPedido.setText("Pedido: "+pedido);

        if(status>5){
            btn_alterar.setEnabled(false);
            btn_alterar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
        }else{
            btn_alterar.setEnabled(true);
            btn_alterar.setBackgroundResource(R.drawable.backgroudbuttom);
        }

        btn_alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!edtObs.getText().toString().equals(obs)) {
                    String novaObs = edtObs.getText().toString().replace("'", "");
                    PedidosActivity._isAlteracao = true;
                    dialog = new ProgressDialog(PopupObsPedido.this);
                    dialog.setMessage("Alterando...");
                    dialog.setCancelable(false);
                    dialog.show();


                    new Thread() {

                        public void run() {

                            try {

                                retorno = helper.SetObs(pedido, novaObs);

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

                                        if (!retorno.toString().equals("Pedido alterado!")) {
                                            Toast.makeText(PopupObsPedido.this, retorno, Toast.LENGTH_LONG).show();

                                            finish();


                                        } else {
                                            Toast.makeText(PopupObsPedido.this, retorno, Toast.LENGTH_SHORT).show();

                                            finish();
                                        }

                                    }

                                });
                            }

                        }
                    }.start();
                }else{
                    finish();
                }


            }
        });






    }
}

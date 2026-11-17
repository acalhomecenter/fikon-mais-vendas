package br.com.acalfortaleza.fikonmais;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.adapters.AgendamentoRetiraAdapter;
import br.com.acalfortaleza.fikonmais.adapters.OrcamentoAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.modal.ModalBottomSheetOrc;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.HorarioRetira;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;

public class AgendamentoRetiraActivity extends BaseAcalActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener, ModalBottomSheetOrc.ActionListener{
    public static final String EXTRA_RETORNAR_RESULTADO = "retornarResultadoAoSelecionar";
    public static final String EXTRA_AGENDAMENTO_DATA = "agendamentoData";
    public static final String EXTRA_AGENDAMENTO_HORA = "agendamentoHora";
    public static final String EXTRA_AGENDAMENTO_TIPO = "agendamentoTipo";
    public static final String EXTRA_AGENDAMENTO_PALETIZADO = "agendamentoPaletizado";
    public static final String EXTRA_AGENDAMENTO_DESCRICAO = "agendamentoDescricao";

    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private TextView txt_Numpedido;
    private TextView txt_Dtpedido;
    private TextView txt_Peso;
    private TextView txt_Cliente;
    private TextView txt_Situacao;
    private TextView txt_Cpf;
    private ImageView img_cliente;
    private ImageView img_indicador;
    private ImageView img_entrega;
    private ImageView img_transf;
    private ImageView img_encomenda;
    private TextView txt_consultor;
    private TextView txt_lote;
    private Switch sPaletizado;
    private CalendarView calendarView;
    private Pedidos pedido;
    private RecyclerView mRecyclerView;
    private AgendamentoRetiraAdapter adapter;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private  List<HorarioRetira> lista = new ArrayList<>();
    private List<HorarioRetira> mList;
    private ProgressDialog dialog;
    private Handler handler;
    private PedidosHelper helper;
    private int year, month, day, hour, minute;
    private String dataconsulta;
    private String dataagendada;
    private String tipo;
    private String paletizado;
    private boolean retornarResultadoAoSelecionar;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.agendamento_retira_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_agendamento_retira);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.agendamento_retira_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Agendamento");
        getSupportActionBar().setElevation(0);


        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        retornarResultadoAoSelecionar = getIntent().getBooleanExtra(EXTRA_RETORNAR_RESULTADO, false);

        lista = new ArrayList<>();

        handler = new Handler();

        helper = new PedidosHelper();

        adapter = new AgendamentoRetiraAdapter(this, lista);

        helperLogin = new LoginHelper(this);

        usuarioLogado = helperLogin.GetUsuarioSalvo();

        txt_Numpedido= findViewById(R.id.agen_adp_numped);
        txt_Dtpedido = findViewById(R.id.agen_adp_txtvalidade);
        txt_Cliente = findViewById(R.id.agen_adp_txtcliente);
        txt_Situacao = findViewById(R.id.agen_adp_txtSituacao);
        txt_Cpf = findViewById(R.id.agen_adp_txtcpf);
        img_indicador = findViewById(R.id.agen_adp_indicador);
        txt_Peso = findViewById(R.id.agen_adp_txtPeso);
        img_cliente = findViewById(R.id.agen_adp_imgCliente);
        txt_consultor = findViewById(R.id.agen_adp_txtconsultor);
        txt_lote = findViewById(R.id.agen_adp_txtlote);
        img_entrega= findViewById(R.id.agen_adp_indicadorEntrega);
        img_transf = findViewById(R.id.agen_adp_indicadorTransf);
        img_encomenda = findViewById(R.id.agen_adp_indicadorEncomenda);
        calendarView = findViewById(R.id.agen_calendario);
        sPaletizado = findViewById(R.id.agen_swit_paletizado);
        mRecyclerView = findViewById(R.id.agen_list_horario);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AgendamentoRetiraActivity.this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if(!mList.get(position).getVagas().equals("Não há vagas")){

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AgendamentoRetiraActivity.this);
                            builder.setTitle("Atenção :|");
                            builder.setMessage("Realizar o agendamento do pedido: "+dfInteiro.format(pedido.getNumpedven())+", para o dia  "+dataagendada+" ás  "+mList.get(position).getHorario()+"?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Agendar", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            if (retornarResultadoAoSelecionar) {
                                                retornarAgendamentoSelecionado(mList.get(position).getHorario());
                                                return;
                                            }

                                            AgendarHorario(String.valueOf(pedido.getNumpedven()), String.valueOf(pedido.getCliente().getCodcli()),dataconsulta, mList.get(position).getHorario(), String.valueOf(pedido.getPeso()),"",tipo, pedido.getCodfil(),pedido.getCodvendr(),"AGENDADO",pedido.getCliente(),dataagendada+" ás  "+mList.get(position).getHorario());


                                        }

                                    });

                            builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


                            builder.create().show();


                        }else{
                            Toast.makeText(AgendamentoRetiraActivity.this,"Não é possivel agendar!",Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        txt_Peso.setText(df.format(getPeso(pedido.getItens()))+"Kg");
        pedido.setPeso(getPeso(pedido.getItens()));
        txt_Numpedido.setText("Nº Ped.: " + (dfInteiro.format(pedido.getNumpedven())));
        txt_Cliente.setText(pedido.getCliente().getNomcli());
        txt_consultor.setText("Consultor: " + pedido.getConsultor());

        if (pedido.getCliente().getNatjur().equals("F")) {
            img_cliente.setImageResource(R.drawable.ic_pessoaf);
            txt_Cpf.setText("Cpf: " + pedido.getCliente().getCgccpf().substring(0, 3) + "." + pedido.getCliente().getCgccpf().substring(3, 6) + "." + pedido.getCliente().getCgccpf().substring(6, 9) + "-" + pedido.getCliente().getCgccpf().substring(9, 11));
        } else {
            img_cliente.setImageResource(R.drawable.ic_pessoaj);
            txt_Cpf.setText("Cnpj: " + pedido.getCliente().getCgccpf().substring(0, 2) + "." + pedido.getCliente().getCgccpf().substring(2, 5) + "." + pedido.getCliente().getCgccpf().substring(5, 8) + "/" + pedido.getCliente().getCgccpf().substring(8, 12) + "-" + pedido.getCliente().getCgccpf().substring(12, 14));
        }

        if (pedido.getCliente().getSexo().equals("F")) {
            img_cliente.setImageResource(R.drawable.ic_pessoam);
        }


        if(pedido.getAgendamento() != null && !pedido.getAgendamento().equals("")){
            txt_Situacao.setText(pedido.getAgendamento());
        }else{
            txt_Situacao.setText("Sem Agendamento");
        }



        if (pedido.getCodStatus() == 5) {

            img_indicador.setImageResource(R.drawable.ic_indicador_pendentes);

        }

        if (pedido.getCodStatus() == 8) {

            img_indicador.setImageResource(R.drawable.ic_parcial);

        }

        if (pedido.getCodStatus() == 6) {

            img_indicador.setImageResource(R.drawable.ic_indicador_concluidos);

        }

        if (pedido.getCodStatus() == 1 || pedido.getCodStatus() == 2) {

            img_indicador.setImageResource(R.drawable.ic_faltalib);

        }

        if (pedido.getCodStatus() == 3) {

            img_indicador.setImageResource(R.drawable.ic_faltacxa);
        }
       if(pedido.getDtpedido() != null){
           try {

               Date date = entrada.parse(pedido.getDtpedido());
               txt_Dtpedido.setText(("Data " + formato.format(date)));
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }else{
           txt_Dtpedido.setText("");
       }


        if(pedido.getLote().equals("")){
            txt_lote.setText("");
        }else{
            txt_lote.setText("Lote: "+pedido.getLote());
        }


        if(pedido.getVlFrete()>0){
            img_entrega.setVisibility(View.VISIBLE);
        }else{
            img_entrega.setVisibility(View.INVISIBLE);
        }

        if(pedido.getTransferencia()>0){
            img_transf.setVisibility(View.VISIBLE);
        }else{
            img_transf.setVisibility(View.INVISIBLE);
        }

        if(pedido.getEncomenda().equals("E")){
            img_encomenda.setVisibility(View.VISIBLE);
        }else{
            img_encomenda.setVisibility(View.INVISIBLE);
        }




        Calendar cMin = Calendar.getInstance();
        Calendar cMax = Calendar.getInstance();

        cMax.set(cMax.get(Calendar.YEAR)+1,11,31);

        calendarView.setMinDate( cMin.getTimeInMillis() );
        calendarView.setMaxDate( cMax.getTimeInMillis() );

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int y, int m, int d) {
                year = y;
                month = m;
                day = d;

                dataconsulta = "";
                dataconsulta = year+"-"+(month+1 < 10 ? "0"+(month+1) : month+1 )+"-"+
                        ( +day < 10 ? "0"+day : day);

                ConsultaHorario( dataconsulta);

                dataagendada = (day < 10 ? "0"+day : day)+"/"+
                        (month+1 < 10 ? "0"+(month+1) : month+1 )+"/"+
                        year;


                //Toast.makeText(AgendamentoRetiraActivity.this,dataagendada,Toast.LENGTH_SHORT).show();

            }
        });






        }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();


        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        year = month = day = hour = minute = 0;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar tDefault = Calendar.getInstance();
        tDefault.set(this.year,month,day,hour,minute);

        this.year = year;
        month = monthOfYear;
        day = dayOfMonth;


        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                tDefault.get(Calendar.HOUR_OF_DAY),
                tDefault.get(Calendar.MINUTE),
                true

        );

        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getSupportFragmentManager(), "timePickerDialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        if( hourOfDay < 8 || hourOfDay > 17){
            onDateSet(null,year, month,day);
            Toast.makeText(this,"Somente entre 8h e 17h",Toast.LENGTH_SHORT).show();
            return;
        }

        hour = hourOfDay;
        this.minute = minute;

        dataagendada = "";



        dataagendada = (day < 10 ? "0"+day : day)+"/"+
                (month+1 < 10 ? "0"+(month+1) : month+1 )+"/"+
                year+" às "+
                (hour < 10 ? "0"+hour : hour)+"h"+
                (this.minute < 10 ? "0"+this.minute : this.minute);

        //Toast.makeText(this,dataagendada,Toast.LENGTH_SHORT).show();



    }

    private void initDateTimeData(){
        if(year == 0){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
    }


    public void ConsultaHorario(final String data) {

        dialog = new ProgressDialog(AgendamentoRetiraActivity.this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    mList = helper.GetHorarios(data, tipo);

                    if (mList == null || mList.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AgendamentoRetiraActivity.this, "Não foram encontrados horarios, para a data informada!", Toast.LENGTH_SHORT).show();
                                mList = new ArrayList<>();
                                adapter = new AgendamentoRetiraAdapter(AgendamentoRetiraActivity.this, mList);
                                mRecyclerView.setAdapter(adapter);

                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                adapter = new AgendamentoRetiraAdapter(AgendamentoRetiraActivity.this, mList);
                                mRecyclerView.setAdapter(adapter);

                                dialog.dismiss();

                            }
                        });
                    }







                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AgendamentoRetiraActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(AgendamentoRetiraActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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


    public void AgendarHorario(String numpedven, String codcli, String data, String hora, String peso, String placa, String tipo, int codfil, int codvendr, String status, Cliente cliente, String agendamento) {

        dialog = new ProgressDialog(AgendamentoRetiraActivity.this);
        dialog.setMessage("Agendando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {
                    String retorno = "";

                    if(sPaletizado.isChecked()){
                        paletizado = "S";
                    }else{
                        paletizado = "N";
                    }

                    retorno = helper.SetAgendamentoRetira(numpedven,codcli,data,hora,peso,placa,tipo,codfil,codvendr,status,usuarioLogado.get("nome"),cliente,agendamento,paletizado);

                    if (retorno != null && retorno.toString().equals("AGENDADO")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(AgendamentoRetiraActivity.this, "Há retirada foi agendada com sucesso!", Toast.LENGTH_SHORT).show();
                                txt_Situacao.setText("Agendado: " + dataagendada+ " ás "+hora);
                                if(Pedidos_LancamentoActivity.pedido != null){
                                    Pedidos_LancamentoActivity.pedido.setAgendamento("Agendado: " + dataagendada+ " ás "+hora);
                                }
                                ConsultaHorario(dataconsulta);


                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                dialog.dismiss();
                                Toast.makeText(AgendamentoRetiraActivity.this, "Há retirada não foi agendada!", Toast.LENGTH_SHORT).show();



                            }
                        });
                    }







                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AgendamentoRetiraActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(AgendamentoRetiraActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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

    private void retornarAgendamentoSelecionado(String horaSelecionada) {
        paletizado = sPaletizado.isChecked() ? "S" : "N";

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_AGENDAMENTO_DATA, dataconsulta);
        resultIntent.putExtra(EXTRA_AGENDAMENTO_HORA, horaSelecionada);
        resultIntent.putExtra(EXTRA_AGENDAMENTO_TIPO, tipo);
        resultIntent.putExtra(EXTRA_AGENDAMENTO_PALETIZADO, paletizado);
        resultIntent.putExtra(EXTRA_AGENDAMENTO_DESCRICAO, dataagendada + " ás " + horaSelecionada);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    @Override
    public void onButtonClick(int id) {


    }

    public double getPeso(List<ItemPedidos> itens) {
        double peso = 0.00;

        for (ItemPedidos p : itens) {
            if(p.getCodFilOrig() == 5 && p.getTpped().equals("P") ){
                peso = peso + (p.getPeso()*p.getQtcomp());
            }


            if(peso < 500){

                tipo = "PEQUENO";
            }

            if(peso >= 500 && peso <1500){
                 tipo = "MEDIO";
            }

            if(peso >= 1500){
                tipo = "GRANDE";
            }

        }
        return peso;
    }




    public void PerguntarEdicao(final String cgccpf){





    }
}

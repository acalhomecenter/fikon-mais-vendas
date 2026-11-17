package br.com.acalfortaleza.fikonmais.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;

import com.google.gson.Gson;

import br.com.acalfortaleza.fikonmais.ClientesActivity;
import br.com.acalfortaleza.fikonmais.ClientesCadastroActivity;
import br.com.acalfortaleza.fikonmais.OrcamentoActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.PedidosActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.helper.CredzHelper;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Credz.Invitation;
import br.com.acalfortaleza.fikonmais.models.Credz.Qualification;
import br.com.acalfortaleza.fikonmais.models.Enderecos;


public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ViewHolder> {

    private Context context;
    private final List<Cliente> clientes;
    private Cliente cliente;
    private AlertDialog alertaDialog;
    private Boolean isOrcamento;
    private Boolean isPedido;
    private AlertDialog.Builder builder;
    private Handler handler;
    private int codfilConsultor;

    private String CgccpfConsultado ="";

    public ClientesAdapter(Context context, List<Cliente> clientes, Boolean isOrcamento, Boolean isPedido, int codfilConsultor ) {

        this.context = context;
        this.clientes = clientes;
        this.isOrcamento = isOrcamento;
        this.isPedido = isPedido;
        this.handler = new Handler(Looper.getMainLooper());
        this.codfilConsultor = codfilConsultor;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_clientes_consulta, parent,false);
        ViewHolder holder = new ViewHolder(view);
        builder = new AlertDialog.Builder(context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        cliente = clientes.get(position);
        DecimalFormat df = new DecimalFormat("0.00");

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");

        if(cliente.getConsultor().equals("")){
            holder.consultor.setText("");
        }else{
            holder.consultor.setText("Responsável: "+cliente.getConsultor());
        }


        if (cliente.getTipo().equals("CADASTRADO")) {
            holder.corCliente.setBackgroundColor(Color.parseColor("#1976D2"));
        } else {
            holder.corCliente.setBackgroundColor(Color.parseColor("#C8671E"));
        }

        holder.cliente.setText(String.valueOf(cliente.getCodcli()) + String.valueOf(cliente.getDigcli()) + " - " + cliente.getNomcli());

        if (cliente.getNatjur().equals("F")) {
            holder.cgccpf.setText("Cpf: " + cliente.getCgccpf().substring(0, 3) + "." + cliente.getCgccpf().substring(3, 6) + "." + cliente.getCgccpf().substring(6, 9) + "-" + cliente.getCgccpf().substring(9, 11));
        } else {
            holder.fantasia.setText("Fantasia: " + cliente.getFantasia());
            holder.inscricao.setText("IE: " + cliente.getInscr());
            holder.cgccpf.setText("Cnpj: " + cliente.getCgccpf().substring(0, 2) + "." + cliente.getCgccpf().substring(2, 5) + "." + cliente.getCgccpf().substring(5, 8) + "/" + cliente.getCgccpf().substring(8, 12) + "-" + cliente.getCgccpf().substring(12, 14));
        }


        if (cliente.getCampanha() != null) {
            holder.campanha.setVisibility(View.VISIBLE);
            holder.campanha.setText(df.format(cliente.getCampanha().getVendaLiquida()) + " Pontos");
        } else{
            holder.campanha.setVisibility(View.INVISIBLE);
            holder.campanha.setText("0 Pontos");
        }


        if (!cliente.getDtAtuCad().equals("") && !cliente.getDtAtuCad().equals("0001-01-01T00:00:00")) {

            try {
                Date date = entrada.parse(cliente.getDtAtuCad());
                holder.dtalteracao.setText(("Data de alteração:  " + formato.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else {
            holder.dtalteracao.setText((""));
        }

        if(cliente.getDadosAdcionais() != null && cliente.getDadosAdcionais().getStatus() != null && !cliente.getDadosAdcionais().getStatus().equals("")){
            holder.situacao.setText("Situação: "+cliente.getDadosAdcionais().getStatus().trim());
        }else{
            holder.situacao.setText("");
        }




        if (!cliente.getDtCadast().equals("") && !cliente.getDtCadast().equals("0001-01-01T00:00:00")) {
            try {
                Date date = entrada.parse(cliente.getDtCadast());
                holder.dtcadastro.setText(("Data de cadastro:  " + formato.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else {
            holder.dtcadastro.setText((""));
        }

        if (!cliente.getFone1().equals("")) {

            holder.fone.setText("Fone: (" + cliente.getDddFone1() + ") " + cliente.getFone1());
            holder.fone.setTextColor(Color.parseColor("#757575"));
            holder.imgFone.setImageResource(R.drawable.phone);
            holder.imgFone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!clientes.get(position).getFone1().equals("") ) {

                        if (ActivityCompat.checkSelfPermission(((Activity) context),
                                Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(((Activity) context),
                                    new String[]{Manifest.permission.CALL_PHONE}, 1);

                        } else {
                            Intent intentLigar = new Intent(Intent.ACTION_CALL);
                            intentLigar.setData(Uri.parse("tel:" +clientes.get(position).getDddFone1() + clientes.get(position).getFone1()));
                            context.startActivity(intentLigar);

                        }

                    }else{
                        Toast.makeText(context, "Telefone do cliente não, está cadastrado!", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }else{

            holder.fone.setText("Telefone não cadastrado");
            holder.fone.setTextColor(Color.parseColor("#A61553"));
            holder.imgFone.setImageResource(0);

        }

        if (!cliente.getCelular().equals("")) {

            holder.celular.setText("Celular: (" + cliente.getDddCelular() + ") " + cliente.getCelular());
            holder.celular.setTextColor(Color.parseColor("#757575"));
            holder.imgCelular.setImageResource(R.drawable.celular);

            holder.imgCelular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!clientes.get(position).getCelular().equals("") ) {


                        if (ActivityCompat.checkSelfPermission(((Activity) context),
                                Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(((Activity) context),
                                    new String[]{Manifest.permission.CALL_PHONE}, 1);

                        } else {
                            Intent intentLigar = new Intent(Intent.ACTION_CALL);
                            intentLigar.setData(Uri.parse("tel:" + clientes.get(position).getDddCelular() + clientes.get(position).getCelular()));
                            context.startActivity(intentLigar);

                        }

                    }else{
                        Toast.makeText(context, "Celular do cliente não, está cadastrado!", Toast.LENGTH_SHORT).show();
                    }

                }
            });



        }else{

            holder.celular.setText("Celular não cadastrado");
            holder.celular.setTextColor(Color.parseColor("#A61553"));
            holder.imgCelular.setImageResource(0);
            holder.imgWpp.setImageResource(0);

        }

        if (!cliente.getCelular().equals("")){
            holder.imgWpp.setImageResource(R.drawable.whats);
            holder.imgWpp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri uri = Uri.parse("https://wa.me/"+"55"+clientes.get(position).getDddCelular()+clientes.get(position).getCelular());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);


                }
            });
        }else{
            holder.imgWpp.setImageResource(0);
        }


        if (!cliente.getEmail().equals("") && cliente.getEmail().length()>1 ) {

            holder.email.setText("Email: " + cliente.getEmail());
            holder.email.setTextColor(Color.parseColor("#757575"));
            holder.imgEmail.setImageResource(R.drawable.email);
            holder.imgEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(clientes.get(position).getEmail()).matches()) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + clientes.get(position).getEmail()));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        context.startActivity(Intent.createChooser(emailIntent, ""));

                    } else {

                        Toast.makeText(context, "E-mail inválido!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }else{

            holder.email.setText("E-mail não cadastrado");
            holder.email.setTextColor(Color.parseColor("#A61553"));
            holder.imgEmail.setImageResource(0);

        }
        if(cliente.getEnderecos()!=null && cliente.getEnderecos().size()>0) {
            holder.endereco.setText(cliente.getEnderecos().get(0).getCodlograd()+ " "+cliente.getEnderecos().get(0).getEnder()+", "+cliente.getEnderecos().get(0).getNumero()
                    + " " + cliente.getEnderecos().get(0).getBairro() + ", " + cliente.getEnderecos().get(0).getCidade() + " " + cliente.getEnderecos().get(0).getUf());

            if (!cliente.getEnderecos().get(0).getComplemento().equals("")) {

                holder.complemento.setText("Complemento: " + (cliente.getEnderecos().get(0).getComplemento()));
            }
        }


        if (!cliente.getDtUltmComp().equals("") && !cliente.getDtUltmComp().equals("0001-01-01T00:00:00") ) {
            try {
                Date date = entrada.parse(cliente.getDtUltmComp());
                holder.dtultimacompra.setText(("Ultima compra:  " + formato.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            holder.dtultimacompra.setText((""));
        }

        if (cliente.getQtCompras() > 0) {

            holder.qtdpedidos.setText("Qtd de pedidos: " + String.valueOf(cliente.getQtCompras()));

        } else {
            holder.qtdpedidos.setText("Qtd de pedidos: 0");
        }


        if (!cliente.getVlMaiorCompra().equals("")) {

            holder.maiorcompra.setText("Maior compra: " + real.format(cliente.getVlMaiorCompra()));

        }


        holder.saldo.setText("Crédito disponível: "+  real.format(cliente.getSaldo()));

        if(cliente.getQtdendereco()>1){
            holder.imgEnderecos.setImageResource(R.drawable.enderecos);
            holder.imgEnderecos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String resultado = "";
                    for (Enderecos e : clientes.get(position).getEnderecos()) {
                        if(e.getCodend()>0) {
                            resultado = resultado + ("<b>" + "Tipo de endereco: " + "</b>" + e.getTpender() + "<br />" +
                                    "<b>" + "Cep: " + "</b>" + e.getCep() + "" + " <br />" +
                                    "<b>" + "Endereço: " + "</b>" + e.getCodlograd() + " " + e.getEnder() + ", " + e.getNumero() + " <br />" +
                                    "<b>" + "Bairro: " + "</b>" + e.getBairro() + "" + " <br />" +
                                    "<b>" + "Cidade: " + "</b>" + e.getCidade() + "-" + e.getUf() + "<br />" +
                                    "<b>" + "Complemento: " + "</b>" + e.getComplemento() + "<br />" +
                                    "<br/>");
                        }
                    }


                    AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                    alerta.setIcon(R.drawable.localizacao);
                    alerta.setCancelable(false);
                    alerta.setTitle("Endereços do cliente");
                    alerta.setMessage(Html.fromHtml(resultado));
                    alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    alertaDialog = alerta.create();
                    alertaDialog.show();


                }
            });
        }else {

            holder.imgEnderecos.setImageResource(0);
        }
        holder.imgMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cliente.getEnderecos()!= null) {
                    Intent intentMapa = new Intent(Intent.ACTION_VIEW);
                    intentMapa.setData(Uri.parse("geo:0,0?q=" + Uri.encode(clientes.get(position).getEnderecos().get(0).getCodlograd()+ " "+clientes.get(position).getEnderecos().get(0).getEnder()+", "+clientes.get(position).getEnderecos().get(0).getNumero()
                            + " " + clientes.get(position).getEnderecos().get(0).getBairro() + ", " + clientes.get(position).getEnderecos().get(0).getCidade() + " " + clientes.get(position).getEnderecos().get(0).getUf())));
                    context.startActivity(intentMapa);
                }

            }
        });

        holder.btnalterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ClientesCadastroActivity.class);
                intent.putExtra("cliente",clientes.get(position));
                intent.putExtra("cgccpf",clientes.get(position).getCgccpf());
                intent.putExtra("orcamento",isOrcamento);
                intent.putExtra("pedido",isPedido);
                ClientesActivity.alterado="sim";
                context.startActivity(intent);


            }
        });


        holder.btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenuConsulta(view);
                CgccpfConsultado = clientes.get(position).getCgccpf();



            }
        });

        switch (cliente.getSitcred()){

            case "Cobranca":{
                holder.corCliente.setBackgroundColor(Color.parseColor("#ffb300"));
                break;
            }

            case "Suspenso":{
                holder.corCliente.setBackgroundColor(Color.parseColor("#757575"));
                break;
            }

            case "Ch Devol":{
                holder.corCliente.setBackgroundColor(Color.parseColor("#A61553"));
                break;
            }
            case "Bloqueia Venda":{
                holder.corCliente.setBackgroundColor(Color.parseColor("#A61553"));
                break;
            }


            default:{

                break;
            }

        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {







                if(cliente.getMeses() <= 3) {

                    if(cliente.getEmail() == null || cliente.getEmail().equals("") || cliente.getDtnasc() == null || cliente.getDtnasc().equals("") || cliente.getCelular() == null || cliente.getCelular().equals("")  ) {

                        builder.setTitle("Dados incompletos :|");
                        builder.setMessage("Dados cadastrais do cliente incompletos, por gentileza completar o cadastro!");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        if (isOrcamento && !isPedido) {
                                            builder.setTitle("Orçamento");
                                            builder.setMessage("Você deseja incluir esse cliente no orçamento?");
                                            builder
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                            Orcamento_LancamentoActivity.carregaDadosCliente(clientes.get(position));
                                                            ((ClientesActivity) context).finish();
                                                        }

                                                    });


                                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            builder.create().show();


                                        }


                                        if (!isOrcamento && isPedido) {

                                            builder.setTitle("Pedido");
                                            builder.setMessage("Você deseja incluir esse cliente no pedido?");
                                            builder
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                            Pedidos_LancamentoActivity.carregaDadosCliente(clientes.get(position));
                                                            ((ClientesActivity) context).finish();
                                                        }

                                                    });


                                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            builder.create().show();


                                        }


                                        if (!isOrcamento && !isPedido) {

                                            builder.setTitle("Orçamento");
                                            builder.setMessage("Você deseja iniciar um orçamento para esse cliente?");
                                            builder
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                            Intent intent = new Intent(context, Orcamento_LancamentoActivity.class);
                                                            intent.putExtra("cliente", clientes.get(position));
                                                            context.startActivity(intent);
                                                            ((ClientesActivity) context).finish();
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
                        builder.create().show();

                    }else{

                        if (isOrcamento && !isPedido) {
                            builder.setTitle("Orçamento");
                            builder.setMessage("Você deseja incluir esse cliente no orçamento?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            Orcamento_LancamentoActivity.carregaDadosCliente(clientes.get(position));
                                            ((ClientesActivity) context).finish();
                                        }

                                    });


                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.create().show();
                            return true;

                        }


                        if (!isOrcamento && isPedido) {

                            builder.setTitle("Pedido");
                            builder.setMessage("Você deseja incluir esse cliente no pedido?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            Pedidos_LancamentoActivity.carregaDadosCliente(clientes.get(position));
                                            ((ClientesActivity) context).finish();
                                        }

                                    });


                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.create().show();
                            return true;

                        }


                        if (!isOrcamento && !isPedido) {

                            builder.setTitle("Orçamento");
                            builder.setMessage("Você deseja iniciar um orçamento para esse cliente?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            Intent intent = new Intent(context, Orcamento_LancamentoActivity.class);
                                            intent.putExtra("cliente", clientes.get(position));
                                            context.startActivity(intent);
                                            ((ClientesActivity) context).finish();
                                        }

                                    });


                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();


                                }
                            });


                            builder.create().show();
                            return true;

                        }


                    }






                }else{

                    builder.setTitle("Cadastro desatualizado :(");
                    builder.setMessage("Para continuar, atualize os dados cadastrais do cliente!");
                    builder
                            .setCancelable(false)
                            .setPositiveButton("Alterar", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    Intent intent = new Intent(context, ClientesCadastroActivity.class);
                                    intent.putExtra("cliente", clientes.get(position));
                                    intent.putExtra("cgccpf", clientes.get(position).getCgccpf());
                                    intent.putExtra("orcamento", isOrcamento);
                                    intent.putExtra("pedido", isPedido);
                                    ClientesActivity.alterado = "sim";
                                    context.startActivity(intent);
                                }

                            });

                        builder.create().show();

                }


                 return  true;
            }
        });

        System.out.println("Qualification:" + new Gson().toJson(cliente.getQualification()));

        if(cliente.isTemCartaoAcal()!=null && cliente.isTemCartaoAcal()){

            holder.imgCredzQualification.setImageResource(R.drawable.ic_cartao_acal);
            holder.btnCredzInvitation.setText("CLIENTE POSSUI CARTÃO ACAL");
            holder.btnCredzInvitation.setTextColor(Color.BLACK);
            holder.btnCredzInvitation.setBackgroundColor(Color.TRANSPARENT);
            holder.btnCredzInvitation.setClickable(false);
            holder.btnCredzInvitation.setFocusable(false);

        }
        else if(cliente!=null && cliente.getQualification()!=null && cliente.getQualification().getState()!=null && cliente.getQualification().getState().equals("APPROVED")){

            if(cliente.isInvitationEnviado()){

                holder.imgCredzQualification.setImageResource(R.drawable.ic_convite_cartao_enviado);
                holder.btnCredzInvitation.setText("CONVITE CARTÃO ACAL JÁ ENVIADO");
                holder.btnCredzInvitation.setTextColor(Color.BLACK);
                holder.btnCredzInvitation.setBackgroundColor(Color.TRANSPARENT);
                holder.btnCredzInvitation.setClickable(false);
                holder.btnCredzInvitation.setFocusable(false);

            }
            else {

                holder.btnCredzInvitation.setVisibility(View.VISIBLE);
                holder.imgCredzQualification.setVisibility(View.VISIBLE);
                holder.btnCredzInvitation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CredzHelper credzHelper = new CredzHelper();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                                .setTitle("Enviar Convite")
                                .setMessage("Como deseja enviar o convite para o cliente fazer o cadastro para o cartão Acal?")
                                .setCancelable(false);

                        final CountDownLatch countDown = new CountDownLatch(1);
                        String tipoEnvio = null;

                        if (cliente.getEmail() != null && !cliente.getEmail().equals("")) {

                            alertDialog.setPositiveButton("Email", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            ProgressDialog progressDialog = new ProgressDialog(context);
                                            progressDialog.setTitle("Enviando Convite");
                                            progressDialog.setMessage("Por favor aguarde");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Invitation invitation = credzHelper.EnviarInvitation(cliente, "Email", codfilConsultor);

                                                    String tituloAlerta = "";
                                                    String mensagemAlerta = "";

                                                    if (invitation != null && invitation.getState().equals("COMPLETED")) {

                                                        tituloAlerta = "Convite Enviado";
                                                        mensagemAlerta = "Convite enviado com sucesso para o email " + cliente.getEmail();

                                                        if (invitation.getInvitationRequest() != null) {
                                                            if (invitation.getInvitationRequest().getCard() != null && invitation.getInvitationRequest().getCard().getProponent() != null) {

                                                                if (invitation.getInvitationRequest().getCard().getProponent().getEmail() != null) {
                                                                    tituloAlerta = "Convite Já Enviado";
                                                                    mensagemAlerta = "Convite já foi enviado anteriormente para o email " +
                                                                            invitation.getInvitationRequest().getCard().getProponent().getEmail();
                                                                } else {
                                                                    tituloAlerta = "Convite Já Enviado";
                                                                    mensagemAlerta = "Convite já foi enviado anteriormente para o telefone " +
                                                                            invitation.getInvitationRequest().getCard().getProponent().getPhone().getAreaCallingCode() +
                                                                            invitation.getInvitationRequest().getCard().getProponent().getPhone().getNumber();
                                                                }

                                                            }
                                                        }

                                                    } else if (invitation != null && invitation.getState().equals("FAILURE")) {
                                                        if (invitation.getErrorResponse().getResponseCode().equals("DUPLICATE_TRANSACTION")) {
                                                            tituloAlerta = "Convite Já Enviado";
                                                            mensagemAlerta = "Convite já foi enviado anteriormente para o cliente";
                                                        } else {
                                                            tituloAlerta = "Erro ao Enviar Convite";
                                                            mensagemAlerta = invitation.getErrorResponse().getErrors()[0].getDisplayMessage();
                                                        }
                                                    } else {
                                                        tituloAlerta = "Erro ao Enviar Convite";
                                                        mensagemAlerta = "";
                                                    }

                                                    final String tituloAlerta2 = tituloAlerta;
                                                    final String mensagemAlerta2 = mensagemAlerta;

                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            new AlertDialog.Builder(context)
                                                                    .setTitle(tituloAlerta2)
                                                                    .setMessage(mensagemAlerta2)
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .create().show();

                                                        }
                                                    });

                                                    progressDialog.dismiss();

                                                }
                                            }).start();

                                        }
                                    });

                                    dialog.dismiss();

                                }
                            });

                        }
                        if (
                            cliente.getDddCelular() != null && !cliente.getDddCelular().equals("") &&
                            cliente.getCelular() != null && !cliente.getCelular().equals("")
                        ) {

                            alertDialog.setNegativeButton("Telefone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            ProgressDialog progressDialog = new ProgressDialog(context);
                                            progressDialog.setTitle("Enviando Convite");
                                            progressDialog.setMessage("Por favor aguarde");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Invitation invitation = credzHelper.EnviarInvitation(cliente, "Telefone", codfilConsultor);

                                                    String tituloAlerta = "";
                                                    String mensagemAlerta = "";

                                                    if (invitation != null && invitation.getState().equals("COMPLETED")) {

                                                        tituloAlerta = "Convite Enviado";
                                                        mensagemAlerta = "Convite enviado com sucesso para o telefone " + cliente.getDddCelular() + cliente.getCelular();

                                                        if (invitation.getInvitationRequest() != null) {
                                                            if (invitation.getInvitationRequest().getCard() != null && invitation.getInvitationRequest().getCard().getProponent() != null) {

                                                                if (invitation.getInvitationRequest().getCard().getProponent().getEmail() != null) {
                                                                    tituloAlerta = "Convite Já Enviado";
                                                                    mensagemAlerta = "Convite já foi enviado anteriormente para o email " +
                                                                            invitation.getInvitationRequest().getCard().getProponent().getEmail();
                                                                } else {
                                                                    tituloAlerta = "Convite Já Enviado";
                                                                    mensagemAlerta = "Convite já foi enviado anteriormente para o telefone " +
                                                                            invitation.getInvitationRequest().getCard().getProponent().getPhone().getAreaCallingCode() +
                                                                            invitation.getInvitationRequest().getCard().getProponent().getPhone().getNumber();
                                                                }

                                                            }
                                                        }

                                                    } else if (invitation != null && invitation.getState().equals("FAILURE")) {
                                                        if (invitation.getErrorResponse().getResponseCode().equals("DUPLICATE_TRANSACTION")) {
                                                            tituloAlerta = "Convite Já Enviado";
                                                            mensagemAlerta = "Convite já foi enviado anteriormente para o cliente";
                                                        } else {
                                                            tituloAlerta = "Erro ao Enviar Convite";
                                                            mensagemAlerta = invitation.getErrorResponse().getErrors()[0].getDisplayMessage();
                                                        }
                                                    } else {
                                                        tituloAlerta = "Erro ao Enviar Convite";
                                                        mensagemAlerta = "";
                                                    }

                                                    final String tituloAlerta2 = tituloAlerta;
                                                    final String mensagemAlerta2 = mensagemAlerta;

                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            new AlertDialog.Builder(context)
                                                                    .setTitle(tituloAlerta2)
                                                                    .setMessage(mensagemAlerta2)
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .create().show();

                                                        }
                                                    });

                                                    progressDialog.dismiss();

                                                }
                                            }).start();

                                        }
                                    });

                                    dialog.dismiss();

                                }
                            });

                        }


                        alertDialog.create().show();

                    }
                });
            }

        }
        else {
            ConstraintLayout.LayoutParams layoutInvisible = new ConstraintLayout.LayoutParams(0,0);
            layoutInvisible.setMargins(0,0,0,0);
            holder.btnCredzInvitation.setLayoutParams(layoutInvisible);
            holder.btnCredzInvitation.setVisibility(View.INVISIBLE);
            holder.imgCredzQualification.setLayoutParams(layoutInvisible);
            holder.imgCredzQualification.setVisibility(View.INVISIBLE);
        }

    }

    /*public boolean ValidarCadastro(){


        //define datas
        DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date date = entrada.parse(cliente.getDtAtuCad());
            LocalDate dataAlteracao = LocalDate.of(date.toInstant().atZone( ZoneId.systemDefault() ).toLocalDate());
            LocalDate hoje = LocalDate.now();

//calcula diferença
            long meses = dataAlteracao.until(hoje, ChronoUnit.MONTHS);
        } catch (ParseException e) {
            e.printStackTrace();
        }




        if (meses >= 6) {

            return false;

        } else {

            return true;
        }
    }*/

    @Override
    public long getItemId(int position) {
        return clientes.get(position).getCodcli();
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }



    @SuppressLint("RestrictedApi")
    public void showMenuConsulta(View v) {
        androidx.appcompat.widget.PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_consulta, popup.getMenu());


        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();


    }


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.orcamento:

                Intent intento = new Intent(context, OrcamentoActivity.class);
                intento.putExtra("cgccpf",CgccpfConsultado);
                intento.putExtra("isalteracao",true);
                context.startActivity(intento);

                break;
            case R.id.pedidos:

                Intent intent = new Intent(context, PedidosActivity.class);
                intent.putExtra("cgccpf",CgccpfConsultado);
                intent.putExtra("isalteracao",true);
                context.startActivity(intent);



                break;
            default:
                break;
        }
        return false;
    }




    class ViewHolder extends RecyclerView.ViewHolder  {

        TextView cgccpf;
        TextView cliente;
        TextView endereco;
        TextView complemento;
        TextView fantasia;
        TextView campanha;
        TextView inscricao;
        TextView dtalteracao;
        TextView situacao;
        TextView consultor;
        TextView dtcadastro;
        TextView dtultimacompra;
        TextView qtdpedidos;
        TextView maiorcompra;
        TextView fone;
        TextView celular;
        TextView email;
        Button btnalterar;
        Button btnPedidos;
        ImageView imgFone;
        ImageView imgCelular;
        ImageView imgWpp;
        ImageView imgEmail;
        ImageView imgMapa;
        ImageView imgEnderecos;
        ConstraintLayout corCliente;
        CardView cardView;
        TextView saldo;
        Button btnCredzInvitation;
        ImageView imgCredzQualification;


        public ViewHolder(View itemView) {
            super(itemView);
            cgccpf = itemView.findViewById(R.id.adp_cli_cgccpf);
            cliente = itemView.findViewById(R.id.adp_cli_nome);
            endereco = itemView.findViewById(R.id.adp_cli_endereco);
            complemento = itemView.findViewById(R.id.adp_cli_complemento);
            campanha = itemView.findViewById(R.id.adp_cli_campanha);
            fantasia = itemView.findViewById(R.id.adp_cli_fantasia);
            inscricao = itemView.findViewById(R.id.adp_cli_ie);
            dtalteracao = itemView.findViewById(R.id.adp_cli_dtalteracao);
            situacao = itemView.findViewById(R.id.adp_cli_situacao);
            consultor = itemView.findViewById(R.id.adp_cli_consultor);
            dtcadastro = itemView.findViewById(R.id.adp_cli_dtcadastro);
            dtultimacompra = itemView.findViewById(R.id.adp_cli_dtcompras);
            qtdpedidos = itemView.findViewById(R.id.adp_cli_pedidos);
            maiorcompra = itemView.findViewById(R.id.adp_cli_maiorcompra);
            fone = itemView.findViewById(R.id.adp_cli_fone);
            celular = itemView.findViewById(R.id.adp_cli_celular);
            email = itemView.findViewById(R.id.adp_cli_email);
            imgEmail = itemView.findViewById(R.id.adp_cli_imgemail);
            btnalterar = itemView.findViewById(R.id.adp_cli_btn_alterar);
            btnPedidos = itemView.findViewById(R.id.adp_cli_btn_verpedidos);
            imgFone = itemView.findViewById(R.id.adp_cli_imgfone);
            imgCelular = itemView.findViewById(R.id.adp_cli_imgcelular);
            imgWpp = itemView.findViewById(R.id.adp_cli_imgwpp);
            imgMapa = itemView.findViewById(R.id.adp_cli_imgMapa);
            imgEnderecos = itemView.findViewById(R.id.adp_cli_imgEnderecos);
            corCliente = itemView.findViewById(R.id.constraintLayout);
            cardView = itemView.findViewById(R.id.adp_cli_Card);
            saldo = itemView.findViewById(R.id.adp_cli_saldo);
            btnCredzInvitation = itemView.findViewById(R.id.adp_cli_btn_CredzInvitation);
            imgCredzQualification = itemView.findViewById(R.id.adp_cli_imgCredzQualificationApproved);
        }



    }


}

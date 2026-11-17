package br.com.acalfortaleza.fikonmais.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.EncomendaHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Encomenda;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Pedidos;

public class EncomendaAdapter extends RecyclerView.Adapter<EncomendaAdapter.ViewHolder>  {

   public static List<Encomenda> mList;
   public static List<Encomenda> mListFull;
   final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
   final DecimalFormat df = new DecimalFormat("0.00");
   final DecimalFormat dfInteiro = new DecimalFormat("0");
   String PedidoTeste = "0";
   DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
   DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
   private Context context;
   private Encomenda encomenda;
   private AlertDialog alertaDialog;
   private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
   private int qtdLiberado = 0;
   private int qtdParcial = 0;
   private int qtdConcluido = 0;
   private int qtdFaltaCxa = 0;
   private int qtdFaltaLib = 0;
   private EncomendaHelper helper;
   private ProgressDialog dialog;
   private Handler handler;
   private String retorno;
   private HashMap<String, String> usuarioLogado;
   private LoginHelper loginHelper;
   private boolean isGerente;

   public EncomendaAdapter(Context context, List<Encomenda> mList) {
      this.context = context;
      this.mList = mList;
      this.mListFull = new ArrayList<>(mList);
   }


   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      handler = new Handler();
      helper = new EncomendaHelper();
      loginHelper = new LoginHelper(context);
      usuarioLogado = loginHelper.GetUsuarioSalvo();
      LayoutInflater inflater = LayoutInflater.from(context);
      View view = inflater.inflate(R.layout.adapter_encomenda, parent, false);
      ViewHolder holder = new ViewHolder(view);

      if(usuarioLogado.get("isgerente").equals("SIM")){
         isGerente = true;
      }else{
         isGerente = false;
      }

      return holder;
   }


   @Override
   public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


      encomenda = mList.get(position);


      holder.txt_Numorc.setText("Nº Ped.: " + (dfInteiro.format(encomenda.getNumpedven())));
      holder.txt_Cliente.setText(encomenda.getCliente());
      holder.txt_consultor.setText("Consultor: " + encomenda.getConsultor());

      if (encomenda.getNatjur().equals("") ||   encomenda.getNatjur().equals("F")) {
         holder.img_cliente.setImageResource(R.drawable.ic_pessoaf);
         holder.txt_Cpf.setText("Cpf: " + encomenda.getCgccpf().substring(0, 3) + "." + encomenda.getCgccpf().substring(3, 6) + "." + encomenda.getCgccpf().substring(6, 9) + "-" + encomenda.getCgccpf().substring(9, 11));
      } else {
         holder.img_cliente.setImageResource(R.drawable.ic_pessoaj);
         holder.txt_Cpf.setText("Cnpj: " + encomenda.getCgccpf().substring(0, 2) + "." + encomenda.getCgccpf().substring(2, 5) + "." + encomenda.getCgccpf().substring(5, 8) + "/" + encomenda.getCgccpf().substring(8, 12) + "-" + encomenda.getCgccpf().substring(12, 14));
      }


      holder.txt_Status.setText("Status: "+ encomenda.getStatusPedido());
      holder.txt_Produto.setText(encomenda.getProduto() + " Qde : "+ df.format(encomenda.getQtcomp()));

      holder.txt_Total.setText("Total: " + real.format(encomenda.getVlTotItem()));


      if (encomenda.getStatusPedido().equals("S/PEDIDO")) {

         holder.img_indicador.setImageResource(R.drawable.ic_faltacxa);

      }

      if (encomenda.getStatusPedido().equals("PEDIDO REALIZADO")) {

         holder.img_indicador.setImageResource(R.drawable.ic_indicador_concluidos);

      }


      if(!encomenda.getDataConfirmacao().toString().equals("0001-01-01T00:00:00")){
         holder.img_indicador.setImageResource(R.drawable.ic_indicador_pendentes);

      }






      try {
         Date date = entrada.parse(encomenda.getDtPrevisao());

         if(encomenda.getDtPrevisao().toString().equals("0001-01-01T00:00:00")){
            holder.txt_validade.setText("Sem previsão");
         }else{
            holder.txt_validade.setText(("Previsão:  " + formato.format(date)));
         }

      } catch (ParseException e) {
         e.printStackTrace();
      }




   }



   @Override
   public long getItemId(int position) {
      return (long) mList.get(position).getNumpedven();
   }

   @Override
   public int getItemCount() {
      return mList.size();
   }

   public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
      mRecyclerViewOnClickListenerHack = r;
   }


   public void addListitem(Encomenda e, int position) {
      mList.add(e);
      notifyItemInserted(position);

   }

   public void updateListitem(Encomenda e, int position) {
      mList.set(position, e);
      notifyItemChanged(position);

   }





   class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      TextView txt_Numorc;
      TextView txt_validade;
      TextView txt_Produto;
      TextView txt_Status;
      TextView txt_Cliente;
      TextView txt_Total;
      TextView txt_Cpf;
      ImageView img_cliente;
      ImageView img_indicador;
      TextView txt_consultor;



      public ViewHolder(View itemView) {
         super(itemView);

         txt_Numorc = itemView.findViewById(R.id.enc_adp_numped);
         txt_validade = itemView.findViewById(R.id.enc_adp_txtvalidade);
         txt_Cliente = itemView.findViewById(R.id.enc_adp_txtcliente);
         txt_Total = itemView.findViewById(R.id.enc_adp_txttotal);
         txt_Cpf = itemView.findViewById(R.id.enc_adp_txtcpf);
         img_indicador = itemView.findViewById(R.id.enc_adp_indicador);
         txt_Produto= itemView.findViewById(R.id.enc_adp_txtProduto);
         txt_Status= itemView.findViewById(R.id.enc_adp_txtstatus);
         img_cliente = itemView.findViewById(R.id.enc_adp_imgCliente);
         txt_consultor = itemView.findViewById(R.id.enc_adp_txtconsultor);



         itemView.setOnClickListener(this);

      }


      @Override
      public void onClick(View view) {
         if (mRecyclerViewOnClickListenerHack != null) {
            mRecyclerViewOnClickListenerHack.onClickListener(view, getPosition());

         }

      }
   }
}
package br.com.acalfortaleza.fikonmais.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.VendaPerfeita;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> implements Filterable {

    private Context context;
    public static List<Pedidos> mList;
    public static List<Pedidos> mListFull;
    private Pedidos pedido;
    private AlertDialog alertaDialog;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private int qtdLiberado = 0;
    private int qtdParcial = 0;
    private int qtdConcluido = 0;
    private int qtdFaltaCxa = 0;
    private int qtdFaltaLib = 0;
    private PedidosHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private boolean isGerente;


    public PedidosAdapter(Context context, List<Pedidos> mList) {
        this.context = context;
        this.mList = mList;
        this.mListFull = new ArrayList<>(mList);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        handler = new Handler();
        helper = new PedidosHelper();
        loginHelper = new LoginHelper(context);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_pedidos, parent, false);
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


        pedido = mList.get(position);

        calculaMargem(pedido.getItens());

        holder.txt_Numorc.setText("Nº Ped.: " + (dfInteiro.format(pedido.getNumpedven())));
        holder.txt_Cliente.setText(pedido.getCliente().getNomcli());
        holder.txt_consultor.setText("Consultor: " + pedido.getConsultor());

        if (pedido.getCliente().getNatjur().equals("F")) {
            holder.img_cliente.setImageResource(R.drawable.ic_pessoaf);
            holder.txt_Cpf.setText("Cpf: " + pedido.getCliente().getCgccpf().substring(0, 3) + "." + pedido.getCliente().getCgccpf().substring(3, 6) + "." + pedido.getCliente().getCgccpf().substring(6, 9) + "-" + pedido.getCliente().getCgccpf().substring(9, 11));
        } else {
            holder.img_cliente.setImageResource(R.drawable.ic_pessoaj);
            holder.txt_Cpf.setText("Cnpj: " + pedido.getCliente().getCgccpf().substring(0, 2) + "." + pedido.getCliente().getCgccpf().substring(2, 5) + "." + pedido.getCliente().getCgccpf().substring(5, 8) + "/" + pedido.getCliente().getCgccpf().substring(8, 12) + "-" + pedido.getCliente().getCgccpf().substring(12, 14));
        }

        if (pedido.getCliente().getSexo().equals("F")) {
            holder.img_cliente.setImageResource(R.drawable.ic_pessoam);
        }


        // Calcula o frete exibido: frete base (VlFrete ou VlFreteCalculado) + serviços em cache (populated by PopupPedFechamento)
        double servicosCache = 0.0d;
        try {
            if (pedido != null && pedido.getNumpedven() > 0) {
                int numPed = (int) pedido.getNumpedven();
                servicosCache = br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento.servicosFretePorPedido.getOrDefault(numPed, 0.0d);
            }
        } catch (Exception ex) {
            servicosCache = 0.0d;
        }

        double vlFreteBase = (pedido.getVlFreteCalculado() > 0.00d) ? pedido.getVlFreteCalculado() : pedido.getVlFrete();
        double displayedFrete = vlFreteBase + servicosCache;

        // Log detalhado para acompanhar valores de frete/serviços por pedido
        try {
            int numPedLog = (int) pedido.getNumpedven();
            double vlTotalBase = pedido.getVlTotal();
            java.util.List<Integer> idsCache = br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento.idsServicosFretePorPedido.get(numPedLog);
            android.util.Log.d("FretePedList", "Adapter bind pedido=" + numPedLog + ": VlTotalBase=" + vlTotalBase + ", VlFreteBase=" + vlFreteBase + " (INCLUÍDO em VlTotal), ServicosCache=" + servicosCache + ", IdsCache=" + (idsCache==null?"[]":idsCache.toString()) + ", VlFreteSelecionado=" + pedido.getVlFreteSelecionado() + ", FreteTotalExibido=" + displayedFrete + ", DisplayedTotal=" + vlTotalBase);
        } catch (Exception e) {
            android.util.Log.w("FretePedList", "Erro ao logar valores do pedido: " + pedido.getNumpedven(), e);
        }

        holder.txt_Total.setText("Total: " + real.format(pedido.getVlTotal()));


        if (pedido.getCodStatus() == 5) {

            holder.img_indicador.setImageResource(R.drawable.ic_indicador_pendentes);

        }

        if (pedido.getCodStatus() == 8) {

            holder.img_indicador.setImageResource(R.drawable.ic_parcial);

        }

        if (pedido.getCodStatus() == 6) {

            holder.img_indicador.setImageResource(R.drawable.ic_indicador_concluidos);

        }

        if (pedido.getCodStatus() == 1 || pedido.getCodStatus() == 2) {

            holder.img_indicador.setImageResource(R.drawable.ic_faltalib);

        }

        if (pedido.getCodStatus() == 3) {

            holder.img_indicador.setImageResource(R.drawable.ic_faltacxa);

        }


        if (pedido.getPmargRent() > 0) {
            holder.txt_margem.setText("Margem: " + df.format(pedido.getPmargRent()) + "%");
        } else {
            holder.txt_margem.setText("Margem: 0%");
        }

        if (pedido.getPmargRent() >= 34.5) {
            holder.txt_margem.setTextColor(Color.parseColor("#00bfa5"));
        }
        if (pedido.getPmargRent() >= 33.5 && pedido.getPmargRent() < 34.5) {
            holder.txt_margem.setTextColor(Color.parseColor("#C8671E"));
        }

        if (pedido.getPmargRent() < 33.5) {
            holder.txt_margem.setTextColor(Color.parseColor("#A61553"));
        }



        try {
            Date date = entrada.parse(pedido.getDtpedido());
            holder.txt_validade.setText(("Data " + formato.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(pedido.getLote().equals("")){
            holder.txt_lote.setText("");
        }else{
            holder.txt_lote.setText("Lote: "+pedido.getLote());
        }


        if(pedido.getVlFrete()>0){
            holder.img_entrega.setVisibility(View.VISIBLE);
        }else{
            holder.img_entrega.setVisibility(View.INVISIBLE);
        }

        if(pedido.getTransferencia()>0){
            holder.img_transf.setVisibility(View.VISIBLE);
        }else{
            holder.img_transf.setVisibility(View.INVISIBLE);
        }

        if(pedido.getEncomenda().equals("E")){
            holder.img_encomenda.setVisibility(View.VISIBLE);
        }else{
            holder.img_encomenda.setVisibility(View.INVISIBLE);
        }

        if(pedido.getCodArq()>0){
            holder.img_espec.setVisibility(View.VISIBLE);
        }else{
            holder.img_espec.setVisibility(View.INVISIBLE);
        }

        if(pedido.getTipoMotor() == null || pedido.getTipoMotor().equals("") ){

            holder.img_motor.setVisibility(View.INVISIBLE);

        }else{

            holder.img_motor.setVisibility(View.VISIBLE);
        }

        if(usuarioLogado.get("isgerente").equals("SIM") && !usuarioLogado.get("codfil").equals("501")){
            holder.txt_margem.setVisibility(View.VISIBLE);
        }else{
            holder.txt_margem.setVisibility(View.INVISIBLE);
        }

        pedido.setIndicadoresVendaPerfeita( PedidosHelper.ObterIndicadoresVendaPerfeita(pedido, displayedFrete) );

        PedidosHelper.AtualizarLayoutVendaPerfeita(
            pedido.getIndicadoresVendaPerfeita(), pedido.getMetasVendaPerfeita(),
            holder.VendaPerfeitaConversao, holder.VendaPerfeitaSKU, holder.VendaPerfeitaTicket,
            holder.VendaPerfeitaDesconto, holder.VendaPerfeitaEntrega
        );

    }


    public void calculaMargem (List<ItemPedidos> listaItens){


        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");

        BigDecimal valorTotal =  new BigDecimal("0.00");
        BigDecimal valorTotalItem =  new BigDecimal("0.00");
        BigDecimal valorTotalMarg = new BigDecimal("0.00");
        BigDecimal valorCusto = new BigDecimal("0.00");
        BigDecimal valorFrete = new BigDecimal("0.00");
        double valorEncargos = 0.00d;
        BigDecimal valorIcms = new BigDecimal("0.00");
        double valorMargem = 0.00d;
        BigDecimal pesoTotal = new BigDecimal("0.00");
        BigDecimal valorIcmRed = new BigDecimal("0.00");
        BigDecimal valorDesconto = new BigDecimal("0.00");
        BigDecimal calcDesconto = new BigDecimal("0.00");




        for (ItemPedidos item : listaItens) {

            valorTotalItem = (BigDecimal.valueOf(item.getPrecoUnit()).multiply(BigDecimal.valueOf(item.getQtcomp())));
            valorTotalItem = valorTotalItem.setScale(2, RoundingMode.FLOOR);
            valorTotal = valorTotal.add(valorTotalItem);
            valorFrete = valorFrete.add(BigDecimal.valueOf(item.getVlFrete()).setScale(7,RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(item.getQtcomp())));
            //valorTotalMarg = valorTotalMarg.add( BigDecimal.valueOf((item.getPrecoUnit() - item.getVlDescItem())* item.getQtcomp()).setScale(2, RoundingMode.DOWN));
            valorCusto = valorCusto.add(BigDecimal.valueOf((item.getCusto()) * item.getQtcomp()));
            calcDesconto = BigDecimal.valueOf(item.getVlDescItem()).multiply(BigDecimal.valueOf(item.getQtcomp()));
            calcDesconto = calcDesconto.setScale(3,RoundingMode.HALF_UP);
            valorDesconto = valorDesconto.add(calcDesconto);

            pesoTotal = pesoTotal.add(BigDecimal.valueOf(item.getQtcomp()  * item.getPeso()));

            if (item.getIcmsRed() == 0) {
                valorIcms = valorIcms.add(BigDecimal.valueOf((((item.getPrecoUnit() - item.getVlDescItem() ) * item.getQtcomp()) * item.getIcms()) / 100));

            } else {
                valorIcms = valorIcms.add(BigDecimal.valueOf(((((item.getPrecoUnit() - item.getVlDescItem() ) * item.getQtcomp()) - (((item.getPrecoUnit() - item.getVlDescItem() ) * item.getQtcomp()) * item.getIcmsRed()) / 100)
                        * item.getIcms()) / 100));

            }



        }

        valorTotal = valorTotal.subtract(valorDesconto);

        if (listaItens.size() == 0) {
            valorEncargos = 0.00d;
            valorMargem = 0.00d;
        } else {

            valorEncargos = ((valorTotal).doubleValue() * listaItens.get(0).getEncargos()) / 100;

            //calculo anterior
            //valorMargem = ((valorTotalMarg - valorEncargos - valorIcms - valorCusto) / (valorTotalMarg - valorEncargos - valorIcms)) * 100;

            valorMargem = ((valorTotal.doubleValue() - valorEncargos - valorIcms.doubleValue() - valorCusto.doubleValue()) / (valorTotal.doubleValue())) * 100;

        }



        pedido.setPmargRent(valorMargem);


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

    public int QtdLiberado() {
        for (Pedidos p : mList) {
            if (p.getCodStatus() == 5) {
                qtdLiberado = qtdLiberado + 1;
            }
        }
        return qtdLiberado;
    }

    public int QtdConcluido() {
        for (Pedidos p : mList) {
            if (p.getCodStatus() == 6) {
                qtdConcluido = qtdConcluido + 1;
            }
        }
        return qtdConcluido;
    }

    public int QtdParcial() {
        for (Pedidos p : mList) {
            if (p.getCodStatus() == 8) {
                qtdParcial = qtdParcial + 1;
            }
        }
        return qtdParcial;
    }


    public int QtdFaltaCaixa() {
        for (Pedidos p : mList) {
            if (p.getCodStatus() == 3) {
                qtdFaltaCxa = qtdFaltaCxa + 1;
            }
        }
        return qtdFaltaCxa;
    }


    public int QtdFaltaLib() {
        for (Pedidos p : mList) {
            if (p.getCodStatus() == 1 || p.getCodStatus() == 2) {
                qtdFaltaLib = qtdFaltaLib + 1;
            }
        }
        return qtdFaltaLib;
    }


    public void addListitem(Pedidos p, int position) {
        mList.add(p);
        notifyItemInserted(position);

    }

    public void updateListitem(Pedidos p, int position) {
        mList.set(position, p);
        notifyItemChanged(position);

    }

    @Override
    public Filter getFilter() {
        return pedidosFilter;
    }

    private Filter pedidosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pedidos> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mListFull);

            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Pedidos p : mListFull) {
                    if (String.valueOf(p.getCodStatus()).toLowerCase().contains(filterPattern)) {
                        filteredList.add(p);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mList.clear();
            mList.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public void Order_Az() {
        Collections.sort(mList, Pedidos.ORDER_AZ);
        notifyDataSetChanged();
    }

    public void Order_Za() {
        Collections.sort(mList, Pedidos.ORDER_ZA);
        notifyDataSetChanged();
    }

    public void Order_Valor() {
        Collections.sort(mList, Pedidos.ORDER_VALOR);
        notifyDataSetChanged();
    }

    public void Order_ValorDesc() {
        Collections.sort(mList, Pedidos.ORDER_VALORDESC);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_Numorc;
        TextView txt_validade;
        TextView txt_margem;
        TextView txt_Cliente;
        TextView txt_Total;
        TextView txt_Cpf;
        ImageView img_cliente;
        ImageView img_indicador;
        ImageView img_motor;
        ImageView img_entrega;
        ImageView img_transf;
        ImageView img_encomenda;
        ImageView img_espec;
        TextView txt_consultor;
        TextView txt_lote;
        TextView VendaPerfeitaConversao;
        TextView VendaPerfeitaSKU;
        TextView VendaPerfeitaTicket;
        TextView VendaPerfeitaDesconto;
        TextView VendaPerfeitaEntrega;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_Numorc = itemView.findViewById(R.id.ped_adp_numped);
            txt_validade = itemView.findViewById(R.id.ped_adp_txtvalidade);
            txt_Cliente = itemView.findViewById(R.id.ped_adp_txtcliente);
            txt_Total = itemView.findViewById(R.id.ped_adp_txttotal);
            txt_Cpf = itemView.findViewById(R.id.ped_adp_txtcpf);
            img_indicador = itemView.findViewById(R.id.ped_adp_indicador);
            txt_margem = itemView.findViewById(R.id.ped_adp_txtmargem);
            img_cliente = itemView.findViewById(R.id.ped_adp_imgCliente);
            txt_consultor = itemView.findViewById(R.id.ped_adp_txtconsultor);
            txt_lote = itemView.findViewById(R.id.ped_adp_txtlote);
            img_entrega= itemView.findViewById(R.id.ped_adp_indicadorEntrega);
            img_transf = itemView.findViewById(R.id.ped_adp_indicadorTransf);
            img_encomenda = itemView.findViewById(R.id.ped_adp_indicadorEncomenda);
            img_espec = itemView.findViewById(R.id.ped_adp_indicadorEsp);
            img_motor = itemView.findViewById(R.id.ped_adp_imgmotor);

            VendaPerfeitaConversao = itemView.findViewById(R.id.AdapterPedidos_VendaPerfeitaConversao);
            VendaPerfeitaSKU = itemView.findViewById(R.id.AdapterPedidos_VendaPerfeitaSKU);
            VendaPerfeitaTicket = itemView.findViewById(R.id.AdapterPedidos_VendaPerfeitaTicket);
            VendaPerfeitaDesconto = itemView.findViewById(R.id.AdapterPedidos_VendaPerfeitaDesconto);
            VendaPerfeitaEntrega = itemView.findViewById(R.id.AdapterPedidos_VendaPerfeitaEntrega);

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

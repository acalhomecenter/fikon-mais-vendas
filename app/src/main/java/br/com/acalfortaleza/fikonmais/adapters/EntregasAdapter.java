package br.com.acalfortaleza.fikonmais.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Entregas;


public class EntregasAdapter extends BaseAdapter {

    private Context context;
    private final List<Entregas> entregas;


    public EntregasAdapter(Context context, List<Entregas> entregas) {

        this.context = context;
        this.entregas = entregas;

    }


    @Override
    public int getCount() {
        return entregas.size();
    }

    @Override
    public Object getItem(int position) {
        return entregas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return entregas.get(position).getNumped();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = convertView;
        EntregasAdapter.ViewHolder holder;

        DecimalFormat df = new DecimalFormat("0.00");

        if (view == null) {
            holder = new EntregasAdapter.ViewHolder();
            view = ((Activity) context).getLayoutInflater().inflate(R.layout.adapter_entregas_dia, parent, false);
            holder.natjur = view.findViewById(R.id.adp_ent_natjur);
            holder.cgccpf = view.findViewById(R.id.adp_ent_cgccpf);
            holder.cliente = view.findViewById(R.id.adp_ent_cliente);
            holder.fantasia = view.findViewById(R.id.adp_ent_fantasia);
            holder.pedido = view.findViewById(R.id.adp_ent_pedido);
            holder.endereco = view.findViewById(R.id.adp_ent_endereco);
            holder.complemento = view.findViewById(R.id.adp_ent_complemento);
            holder.dtpedido = view.findViewById(R.id.adp_ent_dtpedido);
            holder.lote = view.findViewById(R.id.adp_ent_imglote);
            holder.statusLote = view.findViewById(R.id.adp_ent_statuslote);
            holder.textoLote = view.findViewById(R.id.adp_ent_lote);
            holder.lotePeso = view.findViewById(R.id.adp_ent_lotepeso);
            holder.separacao = view.findViewById(R.id.adp_ent_imgseparacao);
            holder.statusSeparacao = view.findViewById(R.id.adp_ent_statusseparacao);
            holder.textoSeparacao = view.findViewById(R.id.adp_ent_separacao);
            holder.faturamento = view.findViewById(R.id.adp_ent_imgfaturamento);
            holder.statusFaturamento = view.findViewById(R.id.adp_ent_statusfaturado);
            holder.textoFaturamento = view.findViewById(R.id.adp_ent_faturamento);
            holder.transporte = view.findViewById(R.id.adp_ent_imgtransporte);
            holder.statustTansporte = view.findViewById(R.id.adp_ent_statustransporte);
            holder.textoTransporte = view.findViewById(R.id.adp_ent_transporte);
            holder.transporteRomaneio = view.findViewById(R.id.adp_ent_transporteromaneio);
            holder.entrega = view.findViewById(R.id.adp_ent_imgentrega);
            holder.statustEntrega = view.findViewById(R.id.adp_ent_statusentrega);
            holder.textoEntrega = view.findViewById(R.id.adp_ent_entrega);
            holder.fone = view.findViewById(R.id.adp_ent_fone);
            holder.textoFatStatus = view.findViewById(R.id.adp_ent_faturamentoStatus);


            view.setTag(holder);
        } else {
            holder = (EntregasAdapter.ViewHolder) view.getTag();
        }

        Entregas entrega = entregas.get(position);

        if (entrega.getNatjur().equals("F")) {
            holder.natjur.setImageResource(R.drawable.ic_pessoaf);
            holder.cgccpf.setText("Cpf: "+entrega.getCgccpf().substring(0,3)+"."+entrega.getCgccpf().substring(3,6)+"."+entrega.getCgccpf().substring(6,9)+"-"+entrega.getCgccpf().substring(9,11));
            holder.fantasia.setText("");

        } else {
            holder.natjur.setImageResource(R.drawable.ic_pessoaj);
            holder.fantasia.setText("Fantasia: " + entrega.getFantasia());
            holder.cgccpf.setText("Cnpj: " +entrega.getCgccpf().substring(0,2)+"."+entrega.getCgccpf().substring(2,5)+"."+entrega.getCgccpf().substring(5,8)+"/"+entrega.getCgccpf().substring(8,12)+"-"+entrega.getCgccpf().substring(12,14));;
        }

        if(entrega.getCelular().equals("")){
           holder.fone.setText("");
        }else
        {
            holder.fone.setText("Fone: "+(entrega.getCelular()));
        }


        holder.cliente.setText((entrega.getCliente()));

        holder.pedido.setText(String.valueOf(entrega.getNumped()));

        holder.dtpedido.setText(entrega.getDtpedido());

        holder.endereco.setText(entrega.getEndereco() + " " + entrega.getBairro() + ", " + entrega.getCidade() + " " + entrega.getUf());

        if (entrega.getComplemento().equals("")) {
            holder.complemento.setText("");
        } else {
            holder.complemento.setText("Complemento: " + (entrega.getComplemento()));
        }

        if (entrega.getStatuslote() > 0) {

            holder.textoLote.setText(String.valueOf(entrega.getLote()));
            holder.lote.setImageResource(R.drawable.loteok);
            holder.statusLote.setImageResource(R.drawable.statusok);
            holder.lotePeso.setText(df.format(entrega.getPeso()) + " Kg");

            holder.textoSeparacao.setText("Pedido não separado");
            holder.separacao.setImageResource(R.drawable.separacao);
            holder.statusSeparacao.setImageResource(R.drawable.statusinativo);

            holder.textoFaturamento.setText("Pedido não faturado");
            holder.faturamento.setImageResource(R.drawable.faturado);
            holder.statusFaturamento.setImageResource(R.drawable.statusinativo);

            holder.textoTransporte.setText("previsão :" + entrega.getDtentrega());
            holder.transporteRomaneio.setText("");
            holder.transporte.setImageResource(R.drawable.saiuentrega);
            holder.statustTansporte.setImageResource(R.drawable.statusinativo);

            holder.textoEntrega.setText("Aguardando");
            holder.entrega.setImageResource(R.drawable.entregue);
            holder.statustEntrega.setImageResource(R.drawable.statusinativo);


        } else {
            holder.textoLote.setText("Pedido sem lote");
            holder.lote.setImageResource(R.drawable.lote);
            holder.statusLote.setImageResource(R.drawable.statusinativo);
            holder.lotePeso.setText("");

            holder.textoSeparacao.setText("Pedido não separado");
            holder.separacao.setImageResource(R.drawable.separacao);
            holder.statusSeparacao.setImageResource(R.drawable.statusinativo);

            holder.textoFaturamento.setText("Pedido não faturado");
            holder.textoFatStatus.setText("");
            holder.faturamento.setImageResource(R.drawable.faturado);
            holder.statusFaturamento.setImageResource(R.drawable.statusinativo);

            holder.textoTransporte.setText("previsão :" + entrega.getDtentrega());
            holder.transporteRomaneio.setText("");
            holder.transporte.setImageResource(R.drawable.saiuentrega);
            holder.statustTansporte.setImageResource(R.drawable.statusinativo);

            holder.textoEntrega.setText("Aguardando");
            holder.entrega.setImageResource(R.drawable.entregue);
            holder.statustEntrega.setImageResource(R.drawable.statusinativo);


        }


        if (entrega.getStatuslote() > 0 && entrega.getStatuslote() < 3) {

            holder.textoSeparacao.setText("Em separação");
            holder.separacao.setImageResource(R.drawable.separacaook);
            holder.statusSeparacao.setImageResource(R.drawable.statusok);

        }

        if (entrega.getStatuslote() == 3 && entrega.getStatusnota()==2) {

            holder.textoFaturamento.setText("NF: " + String.valueOf(entrega.getNumnota()));
            holder.textoFatStatus.setText("");
            holder.faturamento.setImageResource(R.drawable.faturadook);
            holder.statusFaturamento.setImageResource(R.drawable.statusok);

            holder.textoSeparacao.setText("Pedido separado");
            holder.separacao.setImageResource(R.drawable.separacaook);
            holder.statusSeparacao.setImageResource(R.drawable.statusok);

        }else{
            if (entrega.getStatuslote() == 3 && entrega.getStatusnota()!=2) {
                holder.textoFaturamento.setText("NF: " + String.valueOf(entrega.getNumnota()));
                holder.textoFatStatus.setText("NF devolvida");
                holder.faturamento.setImageResource(R.drawable.faturadook);
                holder.statusFaturamento.setImageResource(R.drawable.statuserro);

                holder.textoSeparacao.setText("Pedido separado");
                holder.separacao.setImageResource(R.drawable.separacaook);
                holder.statusSeparacao.setImageResource(R.drawable.statusok);
            }

        }


        if (entrega.getStatuslote() >= 4) {

            if (entrega.getDtexp().equals("")) {

                holder.textoTransporte.setText("Pedido pronto para entrega");
                holder.transporteRomaneio.setText("RM: " + String.valueOf(entrega.getNromaneio()));
                holder.transporte.setImageResource(R.drawable.saiuentrega);
                holder.statustTansporte.setImageResource(R.drawable.statusinativo);

                holder.textoFaturamento.setText("NF: " + String.valueOf(entrega.getNumnota()));
                holder.textoFatStatus.setText("");
                holder.faturamento.setImageResource(R.drawable.faturadook);
                holder.statusFaturamento.setImageResource(R.drawable.statusok);

                holder.textoSeparacao.setText("Pedido separado");
                holder.separacao.setImageResource(R.drawable.separacaook);
                holder.statusSeparacao.setImageResource(R.drawable.statusok);


            } else {

                holder.textoTransporte.setText("Pedido saiu para entrega");
                holder.transporteRomaneio.setText("RM: " + String.valueOf(entrega.getNromaneio()));
                holder.transporte.setImageResource(R.drawable.saiuentregaok);
                holder.statustTansporte.setImageResource(R.drawable.statusok);

                holder.textoFaturamento.setText("NF: " + String.valueOf(entrega.getNumnota()));
                holder.textoFatStatus.setText("");
                holder.faturamento.setImageResource(R.drawable.faturadook);
                holder.statusFaturamento.setImageResource(R.drawable.statusok);

                holder.textoSeparacao.setText("Pedido separado");
                holder.separacao.setImageResource(R.drawable.separacaook);
                holder.statusSeparacao.setImageResource(R.drawable.statusok);

            }


        } else {

            holder.textoTransporte.setText("previsão :" + entrega.getDtentrega());
            holder.transporteRomaneio.setText("");
            holder.transporte.setImageResource(R.drawable.saiuentrega);
            holder.statustTansporte.setImageResource(R.drawable.statusinativo);


        }


        if (entrega.getStatuslote() == 5) {


            if (entrega.getDtexp().equals("")) {
                holder.textoEntrega.setText("Pedido entregue");
                holder.entrega.setImageResource(R.drawable.entregueok);
                holder.statustEntrega.setImageResource(R.drawable.statusok);

                holder.textoTransporte.setText("Pedido saiu para entrega");
                holder.transporteRomaneio.setText("RM: " + String.valueOf(entrega.getNromaneio()));
                holder.transporte.setImageResource(R.drawable.saiuentregaok);
                holder.statustTansporte.setImageResource(R.drawable.statusok);

            } else {
                holder.textoEntrega.setText("Pedido entregue :" + (entrega.getDtexp()));
                holder.entrega.setImageResource(R.drawable.entregueok);
                holder.statustEntrega.setImageResource(R.drawable.statusok);
            }

        }

        if (entrega.getOcorrencia().equals("")) {

        } else {


            holder.textoEntrega.setText("Ocorrência de entrega");
            holder.entrega.setImageResource(R.drawable.entregue);
            holder.statustEntrega.setImageResource(R.drawable.statuserro);

        }


        return view;

    }

    private class ViewHolder {

        private ImageView natjur;
        private TextView cgccpf;
        private TextView cliente;
        private TextView pedido;
        private TextView endereco;
        private TextView complemento;
        private TextView fantasia;
        private TextView dtpedido;
        private ImageView lote;
        private ImageView statusLote;
        private TextView textoLote;
        private TextView lotePeso;
        private ImageView separacao;
        private ImageView statusSeparacao;
        private TextView textoSeparacao;
        private ImageView faturamento;
        private ImageView statusFaturamento;
        private TextView textoFaturamento;
        private ImageView transporte;
        private ImageView statustTansporte;
        private TextView textoTransporte;
        private TextView transporteRomaneio;
        private ImageView entrega;
        private ImageView statustEntrega;
        private TextView textoEntrega;
        private TextView fone;
        private TextView textoFatStatus;


    }
}

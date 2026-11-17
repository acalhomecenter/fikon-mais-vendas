package br.com.acalfortaleza.fikonmais.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.popup.PopupFormaPgto;
import br.com.acalfortaleza.fikonmais.fragments.AvistaFragment;

public class FinanceiroAdapter extends BaseExpandableListAdapter {


    private Context context;
    public static List<Financeiro> mList;
    public static HashMap<String, Financeiro> mListItens;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private boolean showRemove = true;

    public  FinanceiroAdapter(Context context, List<Financeiro> listaFinanceiro, HashMap<String, Financeiro> listaitens){

        this.context = context;
        this.mList = listaFinanceiro;
        this.mListItens = listaitens;

    }

    public  FinanceiroAdapter(Context context, List<Financeiro> listaFinanceiro, HashMap<String, Financeiro> listaitens, boolean showRemove){
        this.context = context;
        this.mList = listaFinanceiro;
        this.mListItens = listaitens;
        this.showRemove = showRemove;
    }


    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListItens.get(mList.get(groupPosition).toString());
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup holder;

        Financeiro financeiro = (Financeiro) getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.adapter_forma_pgto, null);

            holder = new ViewHolderGroup();
            convertView.setTag(holder);

            holder.tvEvento = convertView.findViewById(R.id.adap_forma_evento);
            holder.tvValor = convertView.findViewById(R.id.adap_forma_valor);
            holder.tvVencimento = convertView.findViewById(R.id.adap_forma_vencimento);
            holder.btnRemove = convertView.findViewById(R.id.adap_forma_remove);

        }else{
            holder = (ViewHolderGroup) convertView.getTag();
        }


        holder.tvEvento.setText(financeiro.getNumprc()+ " - "+financeiro.getEvento());
        holder.tvValor.setText(real.format(financeiro.getValor()));
        try {
            Date date = entrada.parse(financeiro.getDtvcto());
            holder.tvVencimento.setText(("vencendo em " + formato.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (holder.btnRemove != null) {
            if (showRemove && financeiro.getCodEve() == 29) {
                holder.btnRemove.setVisibility(View.VISIBLE);
                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mList != null && groupPosition >= 0 && groupPosition < mList.size()) {
                                Financeiro fin = mList.get(groupPosition);
                                mList.remove(groupPosition);
                                if (PopupFormaPgto.adapter != null && PopupFormaPgto.adapter.mList != null) {
                                    PopupFormaPgto.adapter.mList.remove(fin);
                                    PopupFormaPgto.adapter.notifyDataSetChanged();
                                }
                                notifyDataSetChanged();
                                if (PopupFormaPgto.btn_gravar != null) {
                                    PopupFormaPgto.btn_gravar.setEnabled(false);
                                    PopupFormaPgto.btn_gravar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                                }
                                try { AvistaFragment.devolverReciboParaSelecao(fin); } catch (Exception ignore) {}
                            }
                        } catch (Exception ignore) {}
                    }
                });
            } else {
                holder.btnRemove.setVisibility(View.GONE);
            }
        }


        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


          LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          convertView = layoutInflater.inflate(R.layout.adapter_forma_pgto_detalhe, null);


            Financeiro fin = (Financeiro) getChild(groupPosition, childPosition);

            LinearLayout linear = convertView.findViewById(R.id.adap_forma_linear);


            if (!fin.getConveniada().equals("")) {


                if(fin.getTid() != null && fin.getTid().equals("")) {


                    TextView tvDesc1 = new TextView(context);
                    TextView tvDesc2 = new TextView(context);
                    TextView tvDesc3 = new TextView(context);
                    TextView tvDesc4 = new TextView(context);


                    tvDesc1.setText("Conveniada: " + fin.getConveniada());
                    tvDesc2.setText("Qtde Parcelas: " + fin.getQtprc());
                    tvDesc3.setText("Nº Documento: " + fin.getNumdoc());
                    tvDesc4.setText("Autorização: " + fin.getNumaut());

                    tvDesc1.setTextColor(Color.parseColor("#000000"));
                    tvDesc2.setTextColor(Color.parseColor("#000000"));
                    tvDesc3.setTextColor(Color.parseColor("#000000"));
                    tvDesc4.setTextColor(Color.parseColor("#000000"));

                    linear.addView(tvDesc1);
                    linear.addView(tvDesc2);
                    linear.addView(tvDesc3);
                    linear.addView(tvDesc4);

                }else{
                    TextView tvDesc1 = new TextView(context);
                    TextView tvDesc2 = new TextView(context);
                    TextView tvDesc3 = new TextView(context);
                    TextView tvDesc4 = new TextView(context);
                    TextView tvDesc5 = new TextView(context);

                    tvDesc1.setText("Conveniada: " + fin.getConveniada());
                    tvDesc2.setText("Qtde Parcelas: " + fin.getQtprc());
                    tvDesc3.setText("Nº Documento: " + fin.getNumdoc());
                    tvDesc4.setText("Autorização: " + fin.getNumaut());
                    tvDesc5.setText("TID: " + fin.getTid());

                    tvDesc1.setTextColor(Color.parseColor("#000000"));
                    tvDesc2.setTextColor(Color.parseColor("#000000"));
                    tvDesc3.setTextColor(Color.parseColor("#000000"));
                    tvDesc4.setTextColor(Color.parseColor("#000000"));
                    tvDesc5.setTextColor(Color.parseColor("#000000"));

                    linear.addView(tvDesc1);
                    linear.addView(tvDesc2);
                    linear.addView(tvDesc3);
                    linear.addView(tvDesc4);
                    linear.addView(tvDesc5);
                }

            } else if(fin.getForma() == 1) {


            }else{
                TextView tvDesc1 = new TextView(context);
                tvDesc1.setText("Qtde Parcelas: " + fin.getQtprc());
                tvDesc1.setTextColor(Color.parseColor("#000000"));
                linear.addView(tvDesc1);
            }


        return convertView;

    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    class ViewHolderGroup {
        TextView tvEvento;
        TextView tvValor;
        TextView tvVencimento;
        Button btnRemove;
    }


}

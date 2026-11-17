package br.com.acalfortaleza.fikonmais.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.DevolucoesActivity;
import br.com.acalfortaleza.fikonmais.IndicadoresActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.popup.PopupIndicadorSecundario;


public class IndicadoresConsultorFragment extends Fragment {

    private static HashMap<String, String> usuarioLogado;
    private View layout;
    private int pStatus;
    private Handler handler;
    private TextView card1Titulo;
    private TextView card2Titulo;
    private TextView card3Titulo;
    private TextView card4Titulo;
    private TextView card5Titulo;
    private TextView card6Titulo;
    private TextView card7Titulo;
    private TextView card8Titulo;
    private TextView card1Valor;
    private TextView card2Valor;
    private TextView card3Valor;
    private TextView card4Valor;
    private TextView card5Valor;
    private TextView card6Valor;
    private TextView card7Valor;
    private TextView card8Valor;
    private TextView card1Perc;
    private TextView card2Perc;
    private TextView card3Perc;
    private TextView card4Perc;
    private TextView card5Perc;
    private TextView card6Perc;
    private TextView card7Perc;
    private TextView card8Perc;
    private TextView card1Meta;
    private TextView card2Meta;
    private TextView card3Meta;
    private TextView card4Meta;
    private TextView card5Meta;
    private TextView card6Meta;
    private TextView card7Meta;
    private TextView card8Meta;
    private CardView card1;
    private CardView card2;
    private CardView card3;
    private CardView card4;
    private CardView card5;
    private CardView card6;
    private CardView card7;
    private CardView card8;
    private Consultor consultor;
    private Drawable drawable;
    private LoginHelper loginHelper;
    private String tipoIndicador;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            consultor =  (Consultor)getArguments().get("indicadores");
            
            if (consultor != null) {
                if (consultor.VendaLiquida == null) consultor.VendaLiquida = 0.0;
                if (consultor.Margem == null) consultor.Margem = 0.0;
                if (consultor.Projecao == null) consultor.Projecao = 0.0;
                if (consultor.ProjecaoEmValor == null) consultor.ProjecaoEmValor = 0.0;
                if (consultor.Meta == null) consultor.Meta = 0.0;
                if (consultor.MetaDoDia == null) consultor.MetaDoDia = 0.0;
            }

            tipoIndicador = getArguments().getString("tipo");

        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_indicadores_consultor,
                container, false);

        handler = new Handler();

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");
        final DecimalFormat intf = new DecimalFormat("0");

        loginHelper = new LoginHelper(getContext());
        usuarioLogado = loginHelper.GetUsuarioSalvo();


        card1 = layout.findViewById(R.id.btn_card1);
        card2 = layout.findViewById(R.id.btn_card2);
        card3 = layout.findViewById(R.id.btn_card3);
        card4= layout.findViewById(R.id.btn_card4);
        card5 = layout.findViewById(R.id.btn_card5);
        card6 = layout.findViewById(R.id.btn_card6);
        card7 = layout.findViewById(R.id.btn_card7);
        card8 = layout.findViewById(R.id.btn_card8);


        card1Titulo = layout.findViewById(R.id.card1titulo);
        card2Titulo = layout.findViewById(R.id.card2titulo);
        card3Titulo = layout.findViewById(R.id.card3titulo);
        card4Titulo = layout.findViewById(R.id.card4titulo);
        card5Titulo = layout.findViewById(R.id.card5titulo);
        card6Titulo = layout.findViewById(R.id.card6titulo);
        card7Titulo = layout.findViewById(R.id.card7titulo);
        card8Titulo = layout.findViewById(R.id.card8titulo);


        card1Perc= layout.findViewById(R.id.card1Perc);
        card2Perc = layout.findViewById(R.id.card2Perc);
        card3Perc = layout.findViewById(R.id.card3Perc);
        card4Perc = layout.findViewById(R.id.card4Perc);
        card5Perc = layout.findViewById(R.id.card5Perc);
        card6Perc = layout.findViewById(R.id.card6Perc);
        card7Perc = layout.findViewById(R.id.card7Perc);
        card8Perc = layout.findViewById(R.id.card8Perc);



        card1Valor= layout.findViewById(R.id.card1Valor);
        card2Valor = layout.findViewById(R.id.card2Valor);
        card3Valor = layout.findViewById(R.id.card3Valor);
        card4Valor = layout.findViewById(R.id.card4Valor);
        card5Valor = layout.findViewById(R.id.card5Valor);
        card6Valor = layout.findViewById(R.id.card6Valor);
        card7Valor = layout.findViewById(R.id.card7Valor);
        card8Valor = layout.findViewById(R.id.card8Valor);



        card1Meta= layout.findViewById(R.id.card1Meta);
        card2Meta = layout.findViewById(R.id.card2Meta);
        card3Meta = layout.findViewById(R.id.card3Meta);
        card4Meta = layout.findViewById(R.id.card4Meta);
        card5Meta = layout.findViewById(R.id.card5Meta);
        card6Meta = layout.findViewById(R.id.card6Meta);
        card7Meta = layout.findViewById(R.id.card7Meta);
        card8Meta = layout.findViewById(R.id.card8Meta);




        if(tipoIndicador.equals("acumulado")) {


            card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PopupIndicadorSecundario.class);
                    intent.putExtra("consultor", consultor);
                    intent.putExtra("tipo","faturamento");
                    startActivity(intent);
                }
            });

            card1Valor.setText(real.format(consultor.VendaLiquida));

            //Lojas Fisica

            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida < 115000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 115 mil até 170 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 115000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida >= 115000.00 && consultor.VendaLiquida < 170000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 170 mil até 250 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 170000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida >= 170000.00 && consultor.VendaLiquida < 250000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: Acima de 250 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 250000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida >= 250000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            //Corporativo

            if (consultor.VendaLiquida < 150000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 150 mil até 270 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 150000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.VendaLiquida >= 150000.00 && consultor.VendaLiquida < 270000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 270 mi até 400 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 270000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 270000.00 && consultor.VendaLiquida < 400000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: Acima de 250 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 400000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 400000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            //Conceito

            if (consultor.VendaLiquida < 130000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("Meta: 130 mil até 280 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 130000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.VendaLiquida >= 130000.00 && consultor.VendaLiquida < 280000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("Meta: 280 mil até 370 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 280000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 280000.00 && consultor.VendaLiquida < 370000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("Meta: Acima de 370 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 370000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 370000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            //Canais Digitais

            if (consultor.VendaLiquida < 60000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("Meta: 60 mil até 80 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 60000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.VendaLiquida >= 60000.00 && consultor.VendaLiquida < 80000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("Meta: 80 mil até 105 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 80000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 80000.00 && consultor.VendaLiquida < 105000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("Meta: Acima de 105 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 105000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 105000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }

            card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PopupIndicadorSecundario.class);
                    intent.putExtra("consultor", consultor);
                    intent.putExtra("tipo","margem");
                    startActivity(intent);
                }
            });
            card2Titulo.setText("Minha Margem");
            card2Perc.setText("");
            card2Valor.setText(df.format(consultor.Margem) + "%");
            if (consultor.Margem < consultor.MetaMargem1) {
                card2Meta.setText("Minha Meta: " + df.format(consultor.MetaMargem1));
                card2.setBackgroundColor(getResources().getColor(R.color.roxotrans));


            }
            if (consultor.Margem >= consultor.MetaMargem1 && consultor.PercMargem < consultor.MetaMargem2) {
                card2Meta.setText("Minha Meta: " + df.format(consultor.MetaMargem2));
                card2.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            }
            if (consultor.Margem >= consultor.MetaMargem2) {
                card2Meta.setText("");
                card2.setBackgroundColor(getResources().getColor(R.color.green_trans));


            }


            card3Titulo.setText("Orçamentos Ganhos");
            card3Perc.setText("% Ganhos: " + df.format((consultor.QtdGanhos / consultor.QtdOrcamentos) * 100) + "%");
            card3Valor.setText(intf.format(consultor.QtdGanhos));
            if (consultor.QtdGanhos < consultor.QtdOrcamentos) {
                card3Meta.setText("Minha Meta: " + intf.format(consultor.QtdOrcamentos));
                card3.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card3Meta.setText("Minha Meta: " + intf.format(consultor.QtdOrcamentos));
                card3.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }


            card4Titulo.setText("Abordagens");
            card4Perc.setText("% Atingido: " + df.format((consultor.Abordagem / consultor.MetaAbordagem) * 100) + "%");
            card4Valor.setText(intf.format(consultor.Abordagem));
            if (consultor.Abordagem < consultor.MetaAbordagem) {
                card4Meta.setText("Minha Meta: " + intf.format(consultor.MetaAbordagem));
                card4.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card4Meta.setText("Minha Meta: " + intf.format(consultor.MetaAbordagem));
                card4.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }

            card5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PopupIndicadorSecundario.class);
                    intent.putExtra("consultor", consultor);
                    intent.putExtra("tipo","atendimento");
                    startActivity(intent);
                }
            });

            card5Titulo.setText("Atendimentos");
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150) {
                card5Perc.setText("% Atingido: " + df.format((consultor.Atendimentos / (consultor.Metapf / consultor.QtdConsultores)) * 100) + "%");

                if (consultor.Atendimentos < (consultor.Metapf / consultor.QtdConsultores)) {
                    card5Meta.setText("Minha Meta: " + intf.format((consultor.Metapf / consultor.QtdConsultores)));
                    card5.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card5Meta.setText("");
                    card5.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }

            } else {

                card5Perc.setText("% Atingido: " + df.format((consultor.Atendimentos / (consultor.Metapj / consultor.QtdConsultores)) * 100) + "%");

                if (consultor.Atendimentos < (consultor.Metapj / consultor.QtdConsultores)) {
                    card5Meta.setText("Minha Meta: " + intf.format((consultor.Metapj / consultor.QtdConsultores)));
                    card5.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card5Meta.setText("");
                    card5.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }
            }

            card5Valor.setText(intf.format(consultor.Atendimentos));

            card6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PopupIndicadorSecundario.class);
                    intent.putExtra("consultor", consultor);
                    intent.putExtra("tipo","ticket");
                    startActivity(intent);
                }
            });

            card6Titulo.setText("Ticket");
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150) {
                card6Perc.setText("% Atingido: " + df.format((consultor.Ticket / consultor.Ticketpf) * 100) + "%");

                if (consultor.Ticket < consultor.Ticketpf) {
                    card6Meta.setText("Minha Meta: " + real.format(consultor.Ticketpf));
                    card6.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card6Meta.setText("");
                    card6.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }

            } else {

                card6Perc.setText("% Atingido: " + df.format((consultor.Ticket / consultor.Ticketpj) * 100) + "%");

                if (consultor.Ticket < consultor.Ticketpj) {
                    card6Meta.setText("Minha Meta: " + real.format(consultor.Ticketpj));
                    card6.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card6Meta.setText("");
                    card6.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }
            }

            card6Valor.setText(real.format(consultor.Ticket));


            card7Titulo.setText("Conversão");
            card7Perc.setText("");
            card7Valor.setText(df.format((consultor.Atendimentos / consultor.Abordagem) * 100) + "%");
            if (consultor.Atendimentos < consultor.Abordagem) {
                card7Meta.setText("Minha Meta: " + intf.format(consultor.Abordagem));
                card7.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card7Meta.setText("Minha Meta: " + intf.format(consultor.Abordagem));
                card7.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }

            card8.setVisibility(View.VISIBLE);
            card8Titulo.setText("Minha Projeção");
            card8Perc.setText("% Atingido: " + df.format(consultor.Projecao) + "%");
            card8Valor.setText(real.format(consultor.ProjecaoEmValor));
            if (consultor.Projecao < 100.00) {
                card8Meta.setText("Minha Meta: " + real.format(consultor.Meta));
                card8.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card8Meta.setText("Minha Meta: " + intf.format(consultor.Meta));
                card8.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }
        }

        if(tipoIndicador.equals("dia")){

            card1Valor.setText(real.format(consultor.VendaLiquida));


                card1Meta.setText("Meta: "+real.format(consultor.MetaDoDia));
                card1Titulo.setText("Faturamento do dia");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida /consultor.MetaDoDia) * 100) + "%");

                if(consultor.VendaLiquida< consultor.MetaDoDia){
                    card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));
                }else{
                    card1.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }


            card2Titulo.setText("Minha Margem");
            card2Perc.setText("");
            card2Valor.setText(df.format(consultor.Margem) + "%");
            if (consultor.Margem < consultor.MetaMargem1) {
                card2Meta.setText("Minha Meta: " + df.format(consultor.MetaMargem1));
                card2.setBackgroundColor(getResources().getColor(R.color.roxotrans));


            }
            if (consultor.Margem >= consultor.MetaMargem1 && consultor.PercMargem < consultor.MetaMargem2) {
                card2Meta.setText("Minha Meta: " + df.format(consultor.MetaMargem2));
                card2.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            }
            if (consultor.Margem >= consultor.MetaMargem2) {
                card2Meta.setText("");
                card2.setBackgroundColor(getResources().getColor(R.color.green_trans));


            }


            card3Titulo.setText("Orçamentos Ganhos");
            card3Perc.setText("% Ganhos: " + df.format((consultor.QtdGanhos / consultor.QtdOrcamentos) * 100) + "%");
            card3Valor.setText(intf.format(consultor.QtdGanhos));
            if (consultor.QtdGanhos < consultor.QtdOrcamentos) {
                card3Meta.setText("Minha Meta: " + intf.format(consultor.QtdOrcamentos));
                card3.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card3Meta.setText("Minha Meta: " + intf.format(consultor.QtdOrcamentos));
                card3.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }


            card4Titulo.setText("Abordagens");
            card4Perc.setText("% Atingido: " + df.format((consultor.Abordagem / consultor.MetaAbordagem) * 100) + "%");
            card4Valor.setText(intf.format(consultor.Abordagem));
            if (consultor.Abordagem < consultor.MetaAbordagem) {
                card4Meta.setText("Minha Meta: " + intf.format(consultor.MetaAbordagem));
                card4.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card4Meta.setText("Minha Meta: " + intf.format(consultor.MetaAbordagem));
                card4.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }


            card5Titulo.setText("Atendimentos");
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150) {
                card5Perc.setText("% Atingido: " + df.format((consultor.Atendimentos / (consultor.Metapf / consultor.QtdConsultores)) * 100) + "%");

                if (consultor.Atendimentos < (consultor.Metapf / consultor.QtdConsultores)) {
                    card5Meta.setText("Minha Meta: " + intf.format((consultor.Metapf / consultor.QtdConsultores)));
                    card5.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card5Meta.setText("");
                    card5.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }

            } else {

                card5Perc.setText("% Atingido: " + df.format((consultor.Atendimentos / (consultor.Metapj / consultor.QtdConsultores)) * 100) + "%");

                if (consultor.Atendimentos < (consultor.Metapj / consultor.QtdConsultores)) {
                    card5Meta.setText("Minha Meta: " + intf.format((consultor.Metapj / consultor.QtdConsultores)));
                    card5.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card5Meta.setText("");
                    card5.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }
            }

            card5Valor.setText(intf.format(consultor.Atendimentos));


            card6Titulo.setText("Ticket");
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150) {
                card6Perc.setText("% Atingido: " + df.format((consultor.Ticket / consultor.Ticketpf) * 100) + "%");

                if (consultor.Ticket < consultor.Ticketpf) {
                    card6Meta.setText("Minha Meta: " + real.format(consultor.Ticketpf));
                    card6.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card6Meta.setText("");
                    card6.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }

            } else {

                card6Perc.setText("% Atingido: " + df.format((consultor.Ticket / consultor.Ticketpj) * 100) + "%");

                if (consultor.Ticket < consultor.Ticketpj) {
                    card6Meta.setText("Minha Meta: " + real.format(consultor.Ticketpj));
                    card6.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card6Meta.setText("");
                    card6.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }
            }

            card6Valor.setText(real.format(consultor.Ticket));


            card7Titulo.setText("Conversão");
            card7Perc.setText("");
            card7Valor.setText(df.format((consultor.Atendimentos / consultor.Abordagem) * 100) + "%");
            if (consultor.Atendimentos < consultor.Abordagem) {
                card7Meta.setText("Minha Meta: " + intf.format(consultor.Abordagem));
                card7.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card7Meta.setText("Minha Meta: " + intf.format(consultor.Abordagem));
                card7.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }


             card8.setVisibility(View.INVISIBLE);

        }



        if(tipoIndicador.equals("ontem")){

            card1Valor.setText(real.format(consultor.VendaLiquida));

            //Lojas Fisica

            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida < 100000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 100 mil até 170 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 100000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida >= 100000.00 && consultor.VendaLiquida < 170000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 170 mil até 250 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 170000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida >= 170000.00 && consultor.VendaLiquida < 250000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: Acima de 250 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 250000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150 && consultor.VendaLiquida >= 250000.00 && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") && !usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            //Corporativo

            if (consultor.VendaLiquida < 150000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 150 mil até 270 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 150000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.VendaLiquida >= 150000.00 && consultor.VendaLiquida < 270000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: 270 mil até 400 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 270000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 270000.00 && consultor.VendaLiquida < 400000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("Meta: Acima de 250 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 400000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 400000.00 && usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO PLENO") || usuarioLogado.get("cargo").toString().equals("CONSULTOR(A) DE VENDAS CORPORATIVO JUNIOR")) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            //Conceito

            if (consultor.VendaLiquida < 130000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("Meta: 130 mil até 280 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 130000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.VendaLiquida >= 130000.00 && consultor.VendaLiquida < 280000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("Meta: 280 mil até 370 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 280000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 280000.00 && consultor.VendaLiquida < 370000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("Meta: Acima de 370 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 370000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 370000.00 && consultor.CodigoFilialConsultor == 3) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            //Canais Digitais

            if (consultor.VendaLiquida < 60000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("Meta: 60 mil até 80 mil");
                card1Titulo.setText("Faturamento");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 60000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.roxotrans));

            }
            if (consultor.VendaLiquida >= 60000.00 && consultor.VendaLiquida < 80000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("Meta: 80 mil até 105 mil");
                card1Titulo.setText("Faturamento Bronze");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 80000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 80000.00 && consultor.VendaLiquida < 105000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("Meta: Acima de 105 mil");
                card1Titulo.setText("Faturamento Prata");
                card1Perc.setText("% Atingido: " + df.format((consultor.VendaLiquida / 105000.00) * 100) + "%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }
            if (consultor.VendaLiquida >= 105000.00 && consultor.CodigoFilialConsultor == 900) {
                card1Meta.setText("");
                card1Titulo.setText("Faturamento Ouro");
                card1Perc.setText("% Atingido:  100%");
                card1.setBackgroundColor(getResources().getColor(R.color.green_trans));

            }


            card2Titulo.setText("Minha Margem");
            card2Perc.setText("");
            card2Valor.setText(df.format(consultor.Margem) + "%");
            if (consultor.Margem < consultor.MetaMargem1) {
                card2Meta.setText("Minha Meta: " + df.format(consultor.MetaMargem1));
                card2.setBackgroundColor(getResources().getColor(R.color.roxotrans));


            }
            if (consultor.Margem >= consultor.MetaMargem1 && consultor.PercMargem < consultor.MetaMargem2) {
                card2Meta.setText("Minha Meta: " + df.format(consultor.MetaMargem2));
                card2.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            }
            if (consultor.Margem >= consultor.MetaMargem2) {
                card2Meta.setText("");
                card2.setBackgroundColor(getResources().getColor(R.color.green_trans));


            }


            card3Titulo.setText("Orçamentos Ganhos");
            card3Perc.setText("% Ganhos: " + df.format((consultor.QtdGanhos / consultor.QtdOrcamentos) * 100) + "%");
            card3Valor.setText(intf.format(consultor.QtdGanhos));
            if (consultor.QtdGanhos < consultor.QtdOrcamentos) {
                card3Meta.setText("Minha Meta: " + intf.format(consultor.QtdOrcamentos));
                card3.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card3Meta.setText("Minha Meta: " + intf.format(consultor.QtdOrcamentos));
                card3.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }


            card4Titulo.setText("Abordagens");
            card4Perc.setText("% Atingido: " + df.format((consultor.Abordagem / consultor.MetaAbordagem) * 100) + "%");
            card4Valor.setText(intf.format(consultor.Abordagem));
            if (consultor.Abordagem < consultor.MetaAbordagem) {
                card4Meta.setText("Minha Meta: " + intf.format(consultor.MetaAbordagem));
                card4.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card4Meta.setText("Minha Meta: " + intf.format(consultor.MetaAbordagem));
                card4.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }


            card5Titulo.setText("Atendimentos");
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150) {
                card5Perc.setText("% Atingido: " + df.format((consultor.Atendimentos / (consultor.Metapf / consultor.QtdConsultores)) * 100) + "%");

                if (consultor.Atendimentos < (consultor.Metapf / consultor.QtdConsultores)) {
                    card5Meta.setText("Minha Meta: " + intf.format((consultor.Metapf / consultor.QtdConsultores)));
                    card5.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card5Meta.setText("");
                    card5.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }

            } else {

                card5Perc.setText("% Atingido: " + df.format((consultor.Atendimentos / (consultor.Metapj / consultor.QtdConsultores)) * 100) + "%");

                if (consultor.Atendimentos < (consultor.Metapj / consultor.QtdConsultores)) {
                    card5Meta.setText("Minha Meta: " + intf.format((consultor.Metapj / consultor.QtdConsultores)));
                    card5.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card5Meta.setText("");
                    card5.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }
            }

            card5Valor.setText(intf.format(consultor.Atendimentos));


            card6Titulo.setText("Ticket");
            if (consultor.CodigoFilialConsultor != 3 && consultor.CodigoFilialConsultor != 900 && consultor.CodigoFilialConsultor != 150) {
                card6Perc.setText("% Atingido: " + df.format((consultor.Ticket / consultor.Ticketpf) * 100) + "%");

                if (consultor.Ticket < consultor.Ticketpf) {
                    card6Meta.setText("Minha Meta: " + real.format(consultor.Ticketpf));
                    card6.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card6Meta.setText("");
                    card6.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }

            } else {

                card6Perc.setText("% Atingido: " + df.format((consultor.Ticket / consultor.Ticketpj) * 100) + "%");

                if (consultor.Ticket < consultor.Ticketpj) {
                    card6Meta.setText("Minha Meta: " + real.format(consultor.Ticketpj));
                    card6.setBackgroundColor(getResources().getColor(R.color.roxotrans));


                } else {
                    card6Meta.setText("");
                    card6.setBackgroundColor(getResources().getColor(R.color.green_trans));
                }
            }

            card6Valor.setText(real.format(consultor.Ticket));


            card7Titulo.setText("Conversão");
            card7Perc.setText("");
            card7Valor.setText(df.format((consultor.Atendimentos / consultor.Abordagem) * 100) + "%");
            if (consultor.Atendimentos < consultor.Abordagem) {
                card7Meta.setText("Minha Meta: " + intf.format(consultor.Abordagem));
                card7.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card7Meta.setText("Minha Meta: " + intf.format(consultor.Abordagem));
                card7.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }

            card8.setVisibility(View.VISIBLE);
            card8Titulo.setText("Minha Projeção");
            card8Perc.setText("% Atingido: " + df.format(consultor.Projecao) + "%");
            card8Valor.setText(real.format(consultor.ProjecaoEmValor));
            if (consultor.Projecao < 100.00) {
                card8Meta.setText("Minha Meta: " + real.format(consultor.Meta));
                card8.setBackgroundColor(getResources().getColor(R.color.laranja_trans));


            } else {
                card8Meta.setText("Minha Meta: " + real.format(consultor.Meta));
                card8.setBackgroundColor(getResources().getColor(R.color.green_trans));
            }

        }



    /*
        vlDevolucao = layout.findViewById(R.id.frm_valorDev);
        vlDevolucao.setText(real.format(valorDevolucao));
        vlDevolucao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DevolucoesActivity.class);
                startActivity(intent);
            }
        });
        vlDesconto = layout.findViewById(R.id.frm_valorDesc);
        vlDesconto.setText(real.format(valorDesconto));
        devolucao = layout.findViewById(R.id.frm_perc_dev);
        devolucao.setText(percentualDev + "%");
        if(Double.parseDouble(percentualDev)<3){
            devolucao.setTextColor(Color.parseColor("#4F8651"));
        }else{

            devolucao.setTextColor(Color.parseColor("#B91A1F"));
        }

        desconto = layout.findViewById(R.id.frm_perc_desc);
        desconto.setText(percentualDesc + "%");
        if(Double.parseDouble(percentualDesc)<3){
            desconto.setTextColor(Color.parseColor("#4F8651"));
        }else{

            desconto.setTextColor(Color.parseColor("#B91A1F"));
        }
        ticket = layout.findViewById(R.id.frm_ticket);
        ticket.setText(real.format(ticketMes));
        atdpf = layout.findViewById(R.id.frm_atdpf);
        atdpf.setText(String.valueOf(qtdpf));
        atdpj = layout.findViewById(R.id.frm_atdpj);
        atdpj.setText(String.valueOf(qtdpj));
        atdtotal = layout.findViewById(R.id.frm_totalatd);
        atdtotal.setText(String.valueOf(qtdpf+qtdpj));

        vlfrete = layout.findViewById(R.id.frm_vlfrete);
        freteOrig = layout.findViewById(R.id.frm_freteorig);
        percFrete = layout.findViewById(R.id.frm_percfrete);
        pedidosRetira = layout.findViewById(R.id.frm_pedidosRetira);
        pedidoAgendados = layout.findViewById(R.id.frm_pedidosAgendados);
        percAgendados = layout.findViewById(R.id.frm_percAgendado);


        vlfrete.setText(real.format(valorFrete));
        freteOrig.setText(real.format(valorFreteorig));
        pedidosRetira.setText(String.valueOf(qtdePedidosRetira));
        pedidoAgendados.setText(String.valueOf(qtdePedidosAgendados));
        percFrete.setText(percentualFrete + "%");
        percAgendados.setText(percentualAgendamento + "%");


        pStatus = 0;
        Resources res = getResources();
        if (Double.parseDouble(percentualMeta) > 100) {
            drawable = res.getDrawable(R.drawable.circularmetabatida);
        } else {
            drawable = res.getDrawable(R.drawable.circular);
        }

        final ProgressBar mProgress = layout.findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus <= (Double.parseDouble(percentualMeta) - 1)) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start(); */

        return layout;
    }






}

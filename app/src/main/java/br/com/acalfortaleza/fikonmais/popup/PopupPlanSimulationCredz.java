package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.CredzPlanAdapter;
import br.com.acalfortaleza.fikonmais.helper.CredzHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Credz.Plan;
import br.com.acalfortaleza.fikonmais.models.Credz.PlanSimulation;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;

public class PopupPlanSimulationCredz extends Activity {

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private Handler handler;
    private LoginHelper loginHelper;
    private Pedidos pedido;
    private Orcamento orcamento;
    private TextView txtTitulo;
    private Float valorCredz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_plan_simulation_credz);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .9));

        handler = new Handler();

        recyclerView = findViewById(R.id.popup_planSimulationCredz_recycler);
        txtTitulo = findViewById(R.id.popup_planSimulationCredz_txtTitulo);

        PopupFormaPgto.planoEscolhidoCredz = null;

        loginHelper = new LoginHelper(PopupPlanSimulationCredz.this);

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        valorCredz = getIntent().getFloatExtra("valor",0);
        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");

        handler.post(new Runnable() {
            @Override
            public void run() {

                if(pedido!=null) {

                    txtTitulo.setText("Escolha um plano de pagamento");
                    ConsultarPlanos(
                            pedido.getCliente().getCgccpf(),
                            String.valueOf((long) pedido.getNumpedven()),
                            valorCredz,
                            (float) pedido.getVlEntrada(),
                            pedido.getCodfil()
                    );

                }
                else if(orcamento!=null){

                    txtTitulo.setText("Planos de pagamento disponíveis");
                    ConsultarPlanos(
                            orcamento.getCliente().getCgccpf(),
                            "0",
                            (float) orcamento.getVlTotal() + (float) orcamento.getVlFrete(),
                            (float) orcamento.getVlEntrada(),
                            orcamento.getCodfil()
                    );

                }

            }
        });

    }

    public void ConsultarPlanos(String cpf, String numpedven, float vltotal, float vlentrada, int codfil){

        progressDialog = new ProgressDialog(PopupPlanSimulationCredz.this);
        progressDialog.setTitle("Consultando Planos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    CredzHelper credzHelper = new CredzHelper();
                    PlanSimulation planSimulation = credzHelper.RealizarSimulacao(cpf, numpedven, vltotal, vlentrada, codfil);

                    System.out.println("planos Credz:" + new Gson().toJson(planSimulation));

                    List<Plan> listPlan = new ArrayList<>();

                    if(planSimulation!=null && planSimulation.getState().equals("COMPLETED")){

                        if(planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getResponseCode().equals("APPROVED")) {

                            if (
                                planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getStraight() != null
                                && planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getStraight().length > 0
                            ) {

                                for (PlanSimulation.Straight straight : planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getStraight()) {

                                    Plan plan = new Gson().fromJson(new Gson().toJson(straight), Plan.class);
                                    plan.setType("À VISTA");
                                    listPlan.add(plan);

                                }

                            }

                            if (
                                planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getMerchant() != null
                                && planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getMerchant().length > 0
                            ) {

                                for (PlanSimulation.Merchant1 merchant1 : planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getMerchant()) {

                                    Plan plan = new Gson().fromJson(new Gson().toJson(merchant1), Plan.class);
                                    plan.setType("SEM JUROS");
                                    listPlan.add(plan);

                                }

                            }

                            if (
                                planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getIssuer() != null
                                && planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getIssuer().length > 0
                            ) {

                                for (PlanSimulation.Issuer issuer : planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getPlans().getIssuer()) {

                                    Plan plan = new Gson().fromJson(new Gson().toJson(issuer), Plan.class);
                                    plan.setType("COM JUROS");
                                    listPlan.add(plan);

                                }

                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    PopularRecyclerView(listPlan);
                                    progressDialog.dismiss();
                                }
                            });

                            return;

                        }
                        else if(planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getResponseCode().equals("DECLINED")){

                            progressDialog.dismiss();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    AlertDialog.Builder alert = new AlertDialog.Builder(PopupPlanSimulationCredz.this)
                                            .setTitle("Não há planos disponíveis")
                                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });

                                    if(orcamento!=null){

                                       if(orcamento.getVlTotal()<10){
                                           alert.setMessage(
                                               planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getDisplayMessage() + ". " +
                                               "Valor do orçamento é muito baixo para o Cartão Acal. " +
                                               "Adicione mais produtos para aumentar o valor do orçamento ou escolha outra forma de pagamento."
                                           );
                                       }
                                       else {
                                           alert.setMessage(
                                               planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getDisplayMessage() + ". " +
                                               "Cliente não possui crédito suficiente no Cartão Acal para este orcamento em nenhum dos planos. " +
                                               "Escolha outra forma de pagamento."
                                           );
                                       }

                                    }
                                    else if(pedido!=null){

                                        if(pedido.getVlTotal()<10){
                                            alert.setMessage(
                                                planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getDisplayMessage() + ". " +
                                                "Valor do pedido é muito baixo para ser pago com o Cartão Acal. " +
                                                "Adicione mais produtos para aumentar o valor do pedido ou escolha outra forma de pagamento."
                                            );
                                        }
                                        else {
                                            alert.setMessage(
                                                planSimulation.getPaymentPlanSimulationResponse().getCredzInstallment().getDisplayMessage() + ". " +
                                                "Cliente não possui crédito suficiente no Cartão Acal para este pedido em nenhum dos planos. " +
                                                "Escolha outra forma de pagamento."
                                            );
                                        }

                                    }



                                    alert.create().show();

                                }
                            });

                            return;

                        }

                    }
                    else if(
                        planSimulation!=null
                        && planSimulation.getState().equals("FAILURE")
                        && planSimulation.getErrorResponse()!=null
                        && planSimulation.getErrorResponse().getErrors()!=null
                        && planSimulation.getErrorResponse().getErrors().length>0
                    ){

                        if(
                            planSimulation.getErrorResponse().getResponseCode().equals("RULE_MISMATCH") &&
                            planSimulation.getErrorResponse().getErrors()[0].getTechnicalMessage().contains("PROPOSTA_NAO_ENCONTRADA")
                        ){

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PopupPlanSimulationCredz.this)
                                        .setTitle("Não há planos disponíveis")
                                        .setMessage("Cliente não possui o cartão Acal. " +
                                                "Verifique se o cliente está aprovado para fazer o Cartão Acal " +
                                                "e se o mesmo realizou o cadastro completo para o Cartão Acal, " +
                                                "ou escolha outra forma de pagamento.")
                                        .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).create().show();
                                }
                            });

                        }
                        else{

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PopupPlanSimulationCredz.this)
                                        .setTitle("Erro na consulta de planos")
                                        .setMessage(planSimulation.getErrorResponse().getErrors()[0].getDisplayMessage())
                                        .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).create().show();
                                }
                            });

                        }

                        return;

                    }

                }
                catch (Exception ex){
                    ex.printStackTrace();
                }

                progressDialog.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PopupPlanSimulationCredz.this)
                            .setTitle("Erro na consulta de planos")
                            .setMessage("Não foi possível realizar a consulta de planos. Tente novamente mais tarde.")
                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create().show();
                    }
                });


            }
        }).start();

    }

    public void PopularRecyclerView(List<Plan> listPlans){

        try{

            LinearLayoutManager linearLayout = new LinearLayoutManager(PopupPlanSimulationCredz.this);
            CredzPlanAdapter adapter = new CredzPlanAdapter(PopupPlanSimulationCredz.this, listPlans, pedido, orcamento);
            recyclerView.setLayoutManager(linearLayout);
            recyclerView.setAdapter(adapter);

        }
        catch (Exception ex){

        }

    }

}

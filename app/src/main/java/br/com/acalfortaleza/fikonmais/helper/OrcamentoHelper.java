package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.models.BeneficioPromocao;
import br.com.acalfortaleza.fikonmais.models.CodigoEncomenda;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.DadosBancarios;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.popup.PopupCodigoConsentimentoEncomenda;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrcamentoHelper {


    private Retrofit retrofit;
    private OkHttpClient client;
    private double qtemb;
    private List<CondPagamento> listaCondicoes;

    public  OrcamentoHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }




    public String SetOrcamento(Orcamento orcamento) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            return FikonVendasClient.salvarOrcamento(orcamento);
        }




        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> postOrcamento = service.saveOrcamento( orcamento, "Bearer " + service.token);

        return postOrcamento.execute().body();

    }



    public String IntegraOrcamento(String numorc) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            // Na Fikon o orçamento já nasce no ERP: não há integração separada.
            return numorc;
        }




        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> integraOrcamento = service.IntegraOrcamento( numorc, "Bearer " + service.token);

        return integraOrcamento.execute().body();

    }

    public ItemOrcamento setItemOrcamento(double qtd, ProdutoGemco gemco, String descricao, String imagem){

        ItemOrcamento itemorcamento = new ItemOrcamento();
        if(!descricao.equals("")) {
            itemorcamento.setDescricao(descricao);
        }else {
            if(gemco!=null){
                itemorcamento.setDescricao(gemco.Descricao);
            }

        }

        if(gemco.Qtemb==0){
            qtemb=1;
        }else {
            qtemb=gemco.Qtemb;
        }

        itemorcamento.setCoditprod(gemco.Codigo);
        itemorcamento.setPrecoUnit(gemco.Preco);
        itemorcamento.setCodbarras(gemco.CodigoBarra.get(0).CodBarra);
        itemorcamento.setQtcomp(qtd);
        itemorcamento.setQtemb(qtemb);
        itemorcamento.setEncargos(gemco.Encargos);
        itemorcamento.setIcms(gemco.Icms);
        itemorcamento.setIcmsRed(gemco.IcmsRed);
        itemorcamento.setCusto(gemco.Custo);
        itemorcamento.setNcm(gemco.Ncm);
        itemorcamento.setPeso(gemco.Peso);
        itemorcamento.setFornecedor(gemco.Fornecedor);
        itemorcamento.setImagem(imagem);
        android.util.Log.d("ORC_SET_ITEM", "COD " + gemco.Codigo + " imagem " + String.valueOf(imagem));
        itemorcamento.setFllotevencto(gemco.Lotevencto);
        itemorcamento.setLinha(gemco.Linha);
        itemorcamento.setSituacao(gemco.Situacao);
        itemorcamento.setPrecoConc(gemco.PrecoConc);
        itemorcamento.setBloqueioDesc(gemco.BloqueioDesc);
        itemorcamento.setPercentualComissao(gemco.PercentualComissao);
        itemorcamento.setPercIncentivo(gemco.PercIncentivo);




        return itemorcamento;
    }


    public List<Orcamento> GetOrcamentos(String dtinicial, String dtfinal, int codvendr, int codfil, int codarq) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            return FikonVendasClient.listarOrcamentos(dtinicial, dtfinal);
        }




        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Orcamento>> requestOrcamento = service.GetOrcamentos( dtinicial, dtfinal, codvendr, codfil, codarq,"Bearer " + service.token);

        return requestOrcamento.execute().body();

    }



    public List<Orcamento> GetOrcamentoConsulta(String consulta, String tipo) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            return FikonVendasClient.pesquisarOrcamentos(consulta, tipo);
        }




        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Orcamento>> requestOrcamento = service.GetOrcamentoConsulta( consulta, tipo, "Bearer " + service.token);

        return requestOrcamento.execute().body();

    }


    public List<Orcamento> GetOrcamentoConsultaIndividual(String consulta, String tipo) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            return FikonVendasClient.consultarOrcamento(consulta);
        }




        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Orcamento>> requestOrcamento = service.GetOrcamentoConsultaIndividual( consulta, tipo, "Bearer " + service.token);

        Response<List<Orcamento>> response = requestOrcamento.execute();

        if (response.isSuccessful()) {
            try {
                Log.d("API_ORCAMENTO_IND", "Response Body: " + new Gson().toJson(response.body()));
            } catch (Exception e) {
                Log.e("API_ORCAMENTO_IND", "Error logging response", e);
            }
            return response.body();
        } else {
            Log.e("API_ORCAMENTO_IND", "Error Code: " + response.code() + " Msg: " + response.message());
            if(response.errorBody() != null) {
                try {
                    Log.e("API_ORCAMENTO_IND", "Error Body: " + response.errorBody().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }



    public List<DadosBancarios> GetDadosBancarios() throws IOException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<DadosBancarios>> requestDados = service.GetDadosBancarios( "Bearer " + service.token);

        return requestDados.execute().body();

    }



    public List<Consultor> GetConsultor() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Consultor>> requestConsultor = service.GetConsultor("Bearer " + service.token);

        return requestConsultor.execute().body();

    }


    public String GerarCarrinho(Orcamento orcamento) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        Orcamento orcamentoCarrinho = prepararOrcamentoParaCarrinho(orcamento);

        final Call<String> postOrcamento = service.gerarCarrinho( orcamentoCarrinho, "Bearer " + service.token);

        String ret = postOrcamento.execute().body().toString();
        return ret;

    }

    private Orcamento prepararOrcamentoParaCarrinho(Orcamento orcamentoOriginal) {
        if (orcamentoOriginal == null) {
            return null;
        }

        Orcamento orcamentoCarrinho = new Gson().fromJson(new Gson().toJson(orcamentoOriginal), Orcamento.class);
        if (orcamentoCarrinho == null || orcamentoCarrinho.getItens() == null) {
            return orcamentoOriginal;
        }

        if (!isCondicaoAPrazo(orcamentoCarrinho)) {
            return orcamentoCarrinho;
        }

        for (ItemOrcamento item : orcamentoCarrinho.getItens()) {
            if (isItemPromocional(item)) {
                item.setPrecoUnit(reverterPrecoUnitarioBrutoPromocionalPrazo(item));
            }
        }

        return orcamentoCarrinho;
    }

    private boolean isCondicaoAPrazo(Orcamento orcamento) {
        if (orcamento == null || orcamento.getCondPagamento() == null) {
            return false;
        }

        if (orcamento.getCondPagamento().getDescricao() != null
                && orcamento.getCondPagamento().getDescricao().toUpperCase().contains("A PRAZO")) {
            return true;
        }

        if (orcamento.getCondPagamento().getCondpgto() != null
                && !orcamento.getCondPagamento().getCondpgto().trim().isEmpty()) {
            return !"01".equals(orcamento.getCondPagamento().getCondpgto().trim());
        }

        return false;
    }

    private boolean isItemPromocional(ItemOrcamento item) {
        return item != null && item.getSituacao() != null && "PR".equalsIgnoreCase(item.getSituacao().trim());
    }

    private double reverterPrecoUnitarioBrutoPromocionalPrazo(ItemOrcamento item) {
        double precoBrutoAtual = item.getPrecoUnit();
        double descontoUnitario = item.getVlDescItem();
        return ((precoBrutoAtual - descontoUnitario) * 0.95d) + descontoUnitario;
    }

    public List<BeneficioPromocao> CalcularPromocao(String codigoPromo , Orcamento orcamento) throws IOException, XmlPullParserException, ParseException {

        List<BeneficioPromocao> retorno = new ArrayList<>();

        final ApiGemco service = retrofit.create(ApiGemco.class);

        List<ItemPedidos> itensPedido = new ArrayList<>();

        for (ItemOrcamento itemOrcamento: orcamento.getItens()) {

            ItemPedidos itemPedidos = new ItemPedidos();
            itemPedidos.setCodbarras(itemOrcamento.getCodbarras());
            itemPedidos.setCoditprod(itemOrcamento.getCoditprod());
            itemPedidos.setQtcomp(itemOrcamento.getQtcomp());
            itemPedidos.setPrecoUnit(itemOrcamento.getPrecoUnit());
            itemPedidos.setVlDescItem(itemOrcamento.getVlDescItem());
            itemPedidos.setPercentualComissao(itemOrcamento.getPercentualComissao());
            itemPedidos.setPercIncentivo(itemOrcamento.getPercIncentivo());

            itensPedido.add(itemPedidos);

        }

        Pedidos pedido = new Pedidos();
        pedido.setUrlMotor(orcamento.getUrlMotor());

        pedido.setNumpedven(123456);
        pedido.setFilial(orcamento.getFilial());
        pedido.setCodfil(orcamento.getCodfil());
        pedido.setItens(itensPedido);

        final Call<List<BeneficioPromocao>> post = service.consultarPromocao(codigoPromo,pedido,"Bearer " + service.token);


        retorno = post.execute().body();

        if(retorno != null  && retorno.size()>0){


            return retorno    ;

        }else {
            return null;
        }



    }

    public String EnviarAnexoMoskit(String urlAnexo, String numOrc, String nomeArquivo){

        try{
            Log.d("MoskitIntegration", "Enviando anexo Moskit - NumOrc: " + numOrc + ", NomeArquivo: " + nomeArquivo + ", URL: " + urlAnexo);

            OkHttpClient client2 = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15,TimeUnit.SECONDS).build();
            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(ApiGemco.BASE_URL)
                    .client(client2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final ApiGemco service = retrofit2.create(ApiGemco.class);

            final Call<String> request = service.enviarAnexoMoskit(urlAnexo, numOrc, nomeArquivo, "Bearer " + service.token);

            Response<String> response = request.execute();

            if(response.isSuccessful()){
                String body = response.body();
                Log.d("MoskitIntegration", "Sucesso ao enviar anexo Moskit: " + body);
                return body;
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "unknown error";
                Log.e("MoskitIntegration", "Erro ao enviar anexo Moskit. Code: " + response.code() + ", Error: " + errorBody);
            }

        }
        catch (Exception ex){
            Log.e("MoskitIntegration", "Exceção ao enviar anexo Moskit", ex);
            ex.printStackTrace();
        }

        return null;

    }

    public boolean NecessarioCodigoEncomenda(Orcamento orcamento){

        if(orcamento!=null && orcamento.getItens()!=null && orcamento.getItens().size()>0){

            for (ItemOrcamento item: orcamento.getItens()) {

                if(item.getSituacao()!=null && item.getSituacao().equals("EN")){
                    return true;
                }

            }

        }

        return false;

    }

    public CodigoEncomenda SalvarCodigoEncomenda(CodigoEncomenda codigoEncomenda){

        try{

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<CodigoEncomenda> request = service.SalvarCodigoEncomenda(
                    codigoEncomenda,
                    "Bearer " + service.token
            );

            Response<CodigoEncomenda> response = request.execute();

            if(response.isSuccessful()) {
                return response.body();
            } else if (response.code() == 400) {
                return new CodigoEncomenda();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public CodigoEncomenda ConsultarCodigoEncomenda(long numorc){

        try{

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<CodigoEncomenda> request = service.ConsultarCodigoEncomenda(
                    numorc,
                    "Bearer " + service.token
            );

            Response<CodigoEncomenda> response = request.execute();

            if(response.isSuccessful()) {
                return response.body();
            } else if (response.code() == 404) {
                return new CodigoEncomenda();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public String EnviarCodigoEncomenda(Orcamento orcamento){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<String> request = service.EnviarCodigoEncomenda(
                    orcamento,
                    "Bearer " + service.token
            );

            Response<String> response = request.execute();

            if(response.isSuccessful()) {
                return response.body();
            } else if (response.code() == 400) {
                return response.errorBody().string()
                        .replaceAll("\"","")
                        .replace("{","")
                        .replace("}","")
                        .replace("Message:","");
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public String ConsultarPrevisaoEncomenda(long fluig, int coditprod){

        try{

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<String> request = service.ConsultarPrevisaoEncomenda(
                    fluig,
                    coditprod,
                    "Bearer " + service.token
            );

            Response<String> response = request.execute();

            if(response.isSuccessful()) {
                return response.body();
            } else if (response.code() == 404) {
                return "";
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public String ValidarCodigoEncomenda(Orcamento orcamento, Context context){

        try {

            if (
                orcamento == null || orcamento.getNumorc() <= 0
                || orcamento.getCliente() == null || orcamento.getCliente().getCodcli() <= 0
                || orcamento.getItens() == null || orcamento.getItens().size() <= 0
            ) {
                new AlertDialog.Builder(context)
                        .setTitle("Salve o orçamento")
                        .setMessage("É necessário salvar o orçamento antes de realizar esta operação.")
                        .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return "salvar";
            }

            CountDownLatch count = new CountDownLatch(1);
            String[] acao = {null};
            CodigoEncomenda[] codigoEncomenda = {null};

            new Thread(new Runnable() {
                @Override
                public void run() {

                    codigoEncomenda[0] = ConsultarCodigoEncomenda((long) orcamento.getNumorc());

                    long hashAtual = 0;
                    for (ItemOrcamento item: orcamento.getItens()) {
                        if(item.getSituacao()!=null && item.getSituacao().equals("EN")){
                            hashAtual += item.getCoditprod();
                        }
                    }

                    if (codigoEncomenda[0] != null) {

                        if(
                            codigoEncomenda[0].Numorc <= 0
                            || codigoEncomenda[0].CodigoGerado == null || codigoEncomenda[0].CodigoGerado.trim().equals("")
                            || codigoEncomenda[0].Hash == null || codigoEncomenda[0].Hash <= 0 || codigoEncomenda[0].Hash!=hashAtual
                        ){
                            acao[0] = "salvar";
                        }
                        else if (codigoEncomenda[0].CodigoGerado.equals(codigoEncomenda[0].CodigoDigitado)) {
                            acao[0] = "validado";
                        }
                        else {
                            acao[0] = "popup";
                        }

                        count.countDown();

                    }
                }
            }).start();

            try {
                count.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            switch (acao[0]){

                case "salvar":
                    new AlertDialog.Builder(context)
                            .setTitle("Salve o orçamento")
                            .setMessage("É necessário salvar o orçamento antes de realizar esta operação.")
                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                    return acao[0];

                case "popup":
                    Intent intent = new Intent(context, PopupCodigoConsentimentoEncomenda.class);
                    intent.putExtra("orcamento", orcamento);
                    intent.putExtra("codigoAnterior", codigoEncomenda[0]);
                    context.startActivity(intent);
                    return acao[0];

                case "validado":
                    return acao[0];

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        Toast.makeText(context, "Erro ao validar o código de encomenda", Toast.LENGTH_LONG).show();
        return "erro";

    }

}

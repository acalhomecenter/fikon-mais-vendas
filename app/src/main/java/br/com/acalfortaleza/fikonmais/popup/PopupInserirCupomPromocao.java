package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.FikonVendasClient;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.CupomPromocionalRequest;
import br.com.acalfortaleza.fikonmais.models.CupomPromocionalResponse;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupInserirCupomPromocao extends Activity {
    private static final String COD_MOTIVO_CUPOM = "CD";
    private static final String DESCRICAO_MOTIVO_CUPOM = "CUPOM DESCONTO";
    public static final String EXTRA_PEDIDO_RESULTADO = "pedido_resultado";
    public static final String EXTRA_CUPOM_CODIGO = "cupom_codigo";
    public static final String EXTRA_CUPOM_ID_RESERVA = "cupom_id_reserva";

    private final Gson gson = new Gson();
    private EditText inputCupom;
    private TextView mensagemErro;
    private Button btnAplicar;
    private Button btnPular;
    private Pedidos pedido;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_inserir_cupom_promocao);

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        inputCupom = findViewById(R.id.popup_cupom_input);
        mensagemErro = findViewById(R.id.popup_cupom_erro);
        btnAplicar = findViewById(R.id.popup_cupom_aplicar);
        btnPular = findViewById(R.id.popup_cupom_pular);

        btnAplicar.setOnClickListener(view -> aplicarCupom());
        btnPular.setOnClickListener(view -> finishSemCupom());
        ajustarTamanhoPopup();
    }

    private void ajustarTamanhoPopup() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getWindow().setLayout((int) (metrics.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void aplicarCupom() {
        String codigoCupom = inputCupom.getText().toString().trim();
        if (codigoCupom.isEmpty()) {
            mensagemErro.setText("Informe o cupom de promoção.");
            return;
        }
        if (pedido == null || pedido.getItens() == null || pedido.getItens().isEmpty()) {
            mensagemErro.setText("Não há itens no pedido para aplicar o cupom.");
            return;
        }
        if (pedidoPossuiCupomAplicado()) {
            new AlertDialog.Builder(this)
                    .setMessage("Já existe um cupom de desconto inserido no pedido.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        mensagemErro.setText("");
        btnAplicar.setEnabled(false);
        btnPular.setEnabled(false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando cupom...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            CupomPromocionalResponse cupomResponse = null;
            String erro = null;
            String descontoFormatado = null;
            boolean cupomAplicado = false;
            try {
                if (pedido.getCliente() == null || pedido.getCliente().getCodcli() <= 0) {
                    throw new IllegalStateException("Não foi possível identificar o cliente do pedido.");
                }

                boolean cupomJaUtilizado = false;
                // Na Fikon o limite de uso é conferido pela própria reserva do cupom.
                if (!FikonVendasClient.ativo()) try {
                    Retrofit retrofitApiGemco = new Retrofit.Builder()
                            .baseUrl(ApiGemco.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Response<Boolean> verificacaoCupom = retrofitApiGemco.create(ApiGemco.class)
                            .VerificarCupomJaUtilizado(
                                    pedido.getCliente().getCodcli(),
                                    codigoCupom,
                                    "Bearer " + ApiGemco.token)
                            .execute();
                    if (verificacaoCupom.isSuccessful() && verificacaoCupom.body() != null) {
                        cupomJaUtilizado = verificacaoCupom.body();
                    } else {
                        android.util.Log.w(
                                "CupomPromocional",
                                "Consulta de utilização do cupom falhou (HTTP "
                                        + verificacaoCupom.code()
                                        + "); prosseguindo com a aplicação do cupom.");
                    }
                } catch (Exception exception) {
                    android.util.Log.w(
                            "CupomPromocional",
                            "Falha na consulta de utilização do cupom; prosseguindo com a aplicação.",
                            exception);
                }

                if (cupomJaUtilizado) {
                    throw new IllegalStateException("Este cupom já foi utilizado para este cliente.");
                }

                CupomPromocionalRequest request = criarRequisicao(codigoCupom);
                Gson gsonComNulos = new GsonBuilder().serializeNulls().create();
                android.util.Log.d(
                        "CupomPromocional",
                        "POST preco/cupom request: " + gsonComNulos.toJson(request));
                Response<CupomPromocionalResponse> response =
                        FikonVendasClient.aplicarCupom(request).execute();
                if (response.body() != null) {
                    cupomResponse = response.body();
                    android.util.Log.d(
                            "CupomPromocional",
                            "POST preco/cupom response HTTP " + response.code()
                                    + ": " + gsonComNulos.toJson(cupomResponse));
                } else if (response.errorBody() != null) {
                    String corpoErro = response.errorBody().string();
                    android.util.Log.w(
                            "CupomPromocional",
                            "POST preco/cupom response HTTP " + response.code()
                                    + ": " + corpoErro);
                    cupomResponse = gson.fromJson(corpoErro, CupomPromocionalResponse.class);
                } else {
                    android.util.Log.w(
                            "CupomPromocional",
                            "POST preco/cupom response HTTP " + response.code() + " sem corpo.");
                }
                if (!response.isSuccessful()) {
                    erro = cupomResponse != null && cupomResponse.mensagem != null
                            ? cupomResponse.mensagem
                            : "Não foi possível validar o cupom. Tente novamente.";
                } else if (cupomResponse == null || !cupomResponse.sucesso || cupomResponse.dados == null) {
                    erro = cupomResponse != null && cupomResponse.mensagem != null
                            ? cupomResponse.mensagem
                            : "O cupom não foi aplicado. Verifique o código e tente novamente.";
                } else if (cupomResponse.dados.cupom == null
                        || cupomResponse.dados.cupom.codigo == null
                        || cupomResponse.dados.identificadorReserva <= 0) {
                    // Cupom sem promoção volta 200 com identificadorReserva null e o motivo
                    // em dados.mensagem.
                    erro = cupomResponse.dados.mensagem != null && !cupomResponse.dados.mensagem.trim().isEmpty()
                            ? cupomResponse.dados.mensagem
                            : "O cupom não foi aplicado. Verifique o código e tente novamente.";
                } else {
                    BigDecimal descontoTotal = aplicarDescontos(cupomResponse.dados.descontosAplicados);
                    cupomAplicado = descontoTotal.signum() > 0;
                    descontoFormatado = java.text.NumberFormat
                            .getCurrencyInstance(new Locale("pt", "BR"))
                            .format(descontoTotal);
                }
            } catch (Exception exception) {
                android.util.Log.e("CupomPromocional", "Falha ao validar cupom promocional", exception);
                erro = exception instanceof IllegalStateException
                        ? exception.getMessage()
                        : "Não foi possível validar o cupom. Verifique sua conexão e tente novamente.";
            }

            final CupomPromocionalResponse resultado = cupomResponse;
            final String mensagem = erro;
            final String totalDescontoFormatado = descontoFormatado;
            final boolean aplicouCupom = cupomAplicado;
            runOnUiThread(() -> {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (mensagem != null) {
                    mensagemErro.setText(mensagem);
                    btnAplicar.setEnabled(true);
                    btnPular.setEnabled(true);
                    ajustarTamanhoPopup();
                    return;
                }
                exibirSucesso(resultadoCupomCodigo(resultado), resultado, totalDescontoFormatado, aplicouCupom);
            });
        }).start();
    }

    private String resultadoCupomCodigo(CupomPromocionalResponse response) {
        return response.dados.cupom.codigo;
    }

    private boolean pedidoPossuiCupomAplicado() {
        for (ItemPedidos item : pedido.getItens()) {
            if (item != null && item.getCodMotivo() != null
                    && COD_MOTIVO_CUPOM.equalsIgnoreCase(item.getCodMotivo().trim())) {
                return true;
            }
        }
        return false;
    }

    private void exibirSucesso(String codigoCupom, CupomPromocionalResponse response, String descontoFormatado,
                               boolean cupomAplicado) {
        if (isFinishing()) {
            return;
        }
        String mensagem = "Cupom aplicado com sucesso! Desconto total de: " + descontoFormatado;
        if (!cupomAplicado) {
            mensagem = "Cupom validado, mas não aplicado. Os descontos existentes nos itens são iguais ou maiores.";
        }
        new AlertDialog.Builder(this)
                .setMessage(mensagem)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_PEDIDO_RESULTADO, pedido);
                    data.putExtra(EXTRA_CUPOM_CODIGO, codigoCupom);
                    data.putExtra(EXTRA_CUPOM_ID_RESERVA, response.dados.identificadorReserva);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                })
                .show();
    }

    private CupomPromocionalRequest criarRequisicao(String codigoCupom) {
        Integer estabelecimento = FikonVendasClient.ativo()
                ? FikonVendasClient.estabelecimentoDaSessao()
                : new ProdutoHelper().resolverEstabelecimentoPedido(pedido.getCodfil());
        if (estabelecimento == null || estabelecimento <= 0) {
            throw new IllegalStateException("Estabelecimento promocional não encontrado.");
        }

        CupomPromocionalRequest request = new CupomPromocionalRequest();
        request.cupom = codigoCupom;
        // "pessoa" é o código do cliente na Fikon; código inexistente volta 400.
        request.pessoa = FikonVendasClient.ativo()
                ? FikonVendasClient.codigoPessoa(pedido.getCliente())
                : "C" + pedido.getCliente().getCodcli();
        request.chcriacao = null;
        request.estabelecimento = estabelecimento;
        request.itens = new ArrayList<>();
        for (ItemPedidos item : pedido.getItens()) {
            if (item == null || item.getCoditprod() <= 0 || item.getQtcomp() <= 0) {
                continue;
            }
            CupomPromocionalRequest.Item requestItem = new CupomPromocionalRequest.Item();
            requestItem.recurso = null;
            requestItem.codigo = item.getCoditprod();
            requestItem.quantidade = item.getQtcomp();
            requestItem.unitario = item.getPrecoUnit();
            request.itens.add(requestItem);
        }
        if (request.itens.isEmpty()) {
            throw new IllegalStateException("O pedido não possui itens válidos.");
        }
        return request;
    }

    private BigDecimal aplicarDescontos(List<CupomPromocionalResponse.DescontoAplicado> descontos) {
        if (descontos == null || descontos.isEmpty()) {
            throw new IllegalArgumentException("O cupom não retornou descontos para aplicar aos itens.");
        }
        boolean[] itemUtilizado = new boolean[pedido.getItens().size()];
        List<ItemPedidos> itensVencidos = new ArrayList<>();
        List<BigDecimal> descontosUnitariosVencedores = new ArrayList<>();
        BigDecimal descontoTotal = BigDecimal.ZERO;
        boolean encontrouItem = false;
        for (CupomPromocionalResponse.DescontoAplicado desconto : descontos) {
            if (desconto == null
                    || !isFinite(desconto.valorDesconto)
                    || !isFinite(desconto.quantidade)
                    || desconto.valorDesconto <= 0
                    || desconto.quantidade <= 0) {
                continue;
            }
            for (int i = 0; i < pedido.getItens().size(); i++) {
                ItemPedidos item = pedido.getItens().get(i);
                if (itemUtilizado[i] || item == null || item.getCoditprod() != desconto.codigo) {
                    continue;
                }
                encontrouItem = true;
                itemUtilizado[i] = true;
                BigDecimal descontoUnitario = BigDecimal.valueOf(desconto.valorDesconto)
                        .divide(BigDecimal.valueOf(desconto.quantidade), 6, RoundingMode.HALF_UP);
                double descontoAtualValor = item.getVlDescItem();
                BigDecimal descontoExistente = BigDecimal.valueOf(
                        isFinite(descontoAtualValor) ? Math.max(0d, descontoAtualValor) : 0d);
                if ("S".equalsIgnoreCase(item.getIsMotor()) || "SIM".equalsIgnoreCase(item.getIsMotor())) {
                    double descontoPromoValor = item.getVldescPromo();
                    if (isFinite(descontoPromoValor)) {
                        descontoExistente = descontoExistente.max(
                                BigDecimal.valueOf(Math.max(0d, descontoPromoValor)));
                    }
                }
                if (descontoUnitario.compareTo(descontoExistente) > 0) {
                    itensVencidos.add(item);
                    descontosUnitariosVencedores.add(descontoUnitario);
                    descontoTotal = descontoTotal.add(BigDecimal.valueOf(desconto.valorDesconto));
                }
                break;
            }
        }
        if (!encontrouItem) {
            throw new IllegalArgumentException("Nenhum item do pedido recebeu desconto do cupom.");
        }
        for (int i = 0; i < itensVencidos.size(); i++) {
            ItemPedidos item = itensVencidos.get(i);
            BigDecimal descontoAplicado = descontosUnitariosVencedores.get(i);
            item.setVlDescItem(descontoAplicado.doubleValue());
            item.setCodMotivo(COD_MOTIVO_CUPOM);
            item.setMotivoDesc(DESCRICAO_MOTIVO_CUPOM);
            if ("S".equalsIgnoreCase(item.getIsMotor()) || "SIM".equalsIgnoreCase(item.getIsMotor())) {
                item.setVldescPromo(descontoAplicado.doubleValue());
            }
            double precoBase = item.getPrecoPrimario() > 0
                    ? item.getPrecoPrimario()
                    : item.getPrecoUnit();
            if (isFinite(precoBase) && precoBase > 0) {
                item.setDpi(descontoAplicado
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(precoBase), 6, RoundingMode.HALF_UP)
                        .doubleValue());
            }
        }
        return descontoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isFinite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private void finishSemCupom() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (btnAplicar != null && btnAplicar.isEnabled()) {
            finishSemCupom();
        }
    }
}

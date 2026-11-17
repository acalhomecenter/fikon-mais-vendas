package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoProduto;

public class PopupVerificaPromoFikon extends Activity {

    public static final String EXTRA_PEDIDO_RESULTADO = "pedido_resultado";
    public static final String EXTRA_ORCAMENTO_RESULTADO = "orcamento_resultado";

    private final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final List<PromocaoPrecoProduto> promocoesEncontradas = new ArrayList<>();

    private Pedidos pedido;
    private Orcamento orcamento;
    private ProdutoHelper produtoHelper;
    private LoginHelper loginHelper;
    private HashMap<String, String> usuarioLogado;
    private Button btnFechar;
    private Button btnVerificar;
    private Button btnOk;
    private TextView txtResultado;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_verificapromofikon);

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");
        produtoHelper = new ProdutoHelper();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        ajustarTamanhoPopup();

        btnFechar = findViewById(R.id.popup_verifica_promo_fikon_btn_fechar);
        btnVerificar = findViewById(R.id.popup_verifica_promo_fikon_btn_verificar);
        btnOk = findViewById(R.id.popup_verifica_promo_fikon_btn_ok);
        txtResultado = findViewById(R.id.popup_verifica_promo_fikon_txt_resultado);

        btnOk.setEnabled(false);
        btnOk.setVisibility(View.GONE);

        btnFechar.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
        btnVerificar.setOnClickListener(v -> consultarPromocoesAtivas());
        btnOk.setOnClickListener(v -> confirmarAplicacaoPromocional());

        txtResultado.setText("Clique em \"VERIFICAR PROMOÇÕES ATIVAS\" para consultar os itens "
                + (isFluxoOrcamento() ? "do orçamento." : "do pedido."));
        txtResultado.post(this::ajustarTamanhoPopup);
    }

    private void consultarPromocoesAtivas() {
        List<ItemPedidos> itensConsulta = obterItensParaConsultaPromocional();
        if (itensConsulta.isEmpty()) {
            Toast.makeText(this,
                    isFluxoOrcamento() ? "Orçamento sem itens para consultar promoção." : "Pedido sem itens para consultar promoção.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        btnVerificar.setEnabled(false);
        btnOk.setEnabled(false);
        btnOk.setVisibility(View.GONE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Consultando promoções ativas...");
        dialog.show();

        new Thread(() -> {
            List<PromocaoPrecoProduto> promocoes = produtoHelper.consultarPromocoesFikonPedido(
                    itensConsulta,
                    obterCodfilConsulta());

            runOnUiThread(() -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                promocoesEncontradas.clear();
                if (promocoes != null) {
                    promocoesEncontradas.addAll(promocoes);
                }

                txtResultado.setText(montarResumoPromocoes(promocoesEncontradas));
                btnVerificar.setEnabled(true);
                boolean possuiPromocoes = !promocoesEncontradas.isEmpty();
                btnOk.setEnabled(possuiPromocoes);
                btnOk.setVisibility(possuiPromocoes ? View.VISIBLE : View.GONE);
                txtResultado.post(this::ajustarTamanhoPopup);
            });
        }).start();
    }

    private void ajustarTamanhoPopup() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels * .9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void confirmarAplicacaoPromocional() {
        if (isFluxoOrcamento()) {
            aplicarPrecosPromocionaisNoOrcamento();
        } else {
            aplicarPrecosPromocionaisNoPedido();
        }
        Intent data = new Intent();
        if (isFluxoOrcamento()) {
            data.putExtra(EXTRA_ORCAMENTO_RESULTADO, orcamento);
        } else {
            data.putExtra(EXTRA_PEDIDO_RESULTADO, pedido);
        }
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void aplicarPrecosPromocionaisNoPedido() {
        if (pedido == null || pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return;
        }

        boolean promocaoAplicada = false;
        for (PromocaoPrecoProduto promocao : promocoesEncontradas) {
            ItemPedidos item = localizarItemPedido(promocao);
            if (item == null || promocao == null || promocao.precoAtual <= 0) {
                continue;
            }

            aplicarDescontoPromocionalNoItem(item, promocao);
            promocaoAplicada = true;
        }

        if (promocaoAplicada) {
            pedido.setIsMotor("S");
        }
    }

    private void aplicarPrecosPromocionaisNoOrcamento() {
        if (orcamento == null || orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            return;
        }

        boolean promocaoAplicada = false;
        for (PromocaoPrecoProduto promocao : promocoesEncontradas) {
            ItemOrcamento item = localizarItemOrcamento(promocao);
            if (item == null || promocao == null || promocao.precoAtual <= 0) {
                continue;
            }

            aplicarDescontoPromocionalNoItem(item, promocao);
            promocaoAplicada = true;
        }

        if (promocaoAplicada) {
            orcamento.setIsMotor("S");
        }
    }

    private void aplicarDescontoPromocionalNoItem(ItemPedidos item, PromocaoPrecoProduto promocao) {
        BigDecimal precoPromocional = BigDecimal.valueOf(promocao.precoAtual).setScale(2, RoundingMode.HALF_UP);
        BigDecimal precoBase = resolverPrecoBasePedidoParaMotor(item, precoPromocional);
        item.setPrecoUnit(precoBase.doubleValue());
        item.setPrecoPrimario(precoBase.doubleValue());

        if (precoPromocional.compareTo(precoBase) >= 0) {
            item.setVlDescItem(0d);
            item.setDpi(0d);
            item.setVldescPromo(0d);
        } else {
            BigDecimal descontoUnitario = precoBase.subtract(precoPromocional).setScale(2, RoundingMode.HALF_UP);
            BigDecimal percentual = BigDecimal.ZERO;

            if (precoBase.compareTo(BigDecimal.ZERO) > 0) {
                percentual = descontoUnitario
                        .divide(precoBase, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100d))
                        .setScale(4, RoundingMode.HALF_UP);
            }

            item.setVlDescItem(descontoUnitario.doubleValue());
            item.setDpi(percentual.doubleValue());
            item.setVldescPromo(descontoUnitario.doubleValue());
        }

        item.setNomePromo(promocao.nomePromocao);
        item.setMensagemPromo(obterMensagemPromocional(promocao));
        item.setCodigoPromo(obterCodigoPromocional(promocao));
        item.setIsMotor("S");
        item.setCodMotivo("");
        item.setMotivoDesc("");
    }

    private void aplicarDescontoPromocionalNoItem(ItemOrcamento item, PromocaoPrecoProduto promocao) {
        BigDecimal precoPromocional = BigDecimal.valueOf(promocao.precoAtual).setScale(2, RoundingMode.HALF_UP);
        BigDecimal precoBase = resolverPrecoBaseOrcamentoParaMotor(item, precoPromocional);
        item.setPrecoUnit(precoBase.doubleValue());

        if (precoPromocional.compareTo(precoBase) >= 0) {
            item.setVlDescItem(0d);
            item.setDpi(0d);
            item.setVldescPromo(0d);
        } else {
            BigDecimal descontoUnitario = precoBase.subtract(precoPromocional).setScale(2, RoundingMode.HALF_UP);
            BigDecimal percentual = BigDecimal.ZERO;

            if (precoBase.compareTo(BigDecimal.ZERO) > 0) {
                percentual = descontoUnitario
                        .divide(precoBase, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100d))
                        .setScale(4, RoundingMode.HALF_UP);
            }

            item.setVlDescItem(descontoUnitario.doubleValue());
            item.setDpi(percentual.doubleValue());
            item.setVldescPromo(descontoUnitario.doubleValue());
        }

        item.setNomePromo(promocao.nomePromocao);
        item.setMensagemPromo(obterMensagemPromocional(promocao));
        item.setCodigoPromo(obterCodigoPromocional(promocao));
        item.setIsMotor("S");
        item.setCodMotivo("");
        item.setMotivoDesc("");
    }

    private BigDecimal resolverPrecoBasePedidoParaMotor(ItemPedidos item, BigDecimal precoPromocional) {
        BigDecimal precoPrimario = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getPrecoPrimario())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal precoUnit = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getPrecoUnit())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descontoAtual = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descontoPromoOriginal = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getVldescPromo())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal precoBase = precoPrimario.max(precoUnit);
        precoBase = precoBase.max(precoPromocional.add(descontoAtual));
        precoBase = precoBase.max(precoPromocional.add(descontoPromoOriginal));
        precoBase = precoBase.max(precoPromocional);
        return precoBase.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolverPrecoBaseOrcamentoParaMotor(ItemOrcamento item, BigDecimal precoPromocional) {
        BigDecimal precoUnit = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getPrecoUnit())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descontoAtual = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descontoPromoOriginal = item != null
                ? BigDecimal.valueOf(Math.max(0.0d, item.getVldescPromo())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal precoBase = precoUnit.max(precoPromocional.add(descontoAtual));
        precoBase = precoBase.max(precoPromocional.add(descontoPromoOriginal));
        precoBase = precoBase.max(precoPromocional);
        return precoBase.setScale(2, RoundingMode.HALF_UP);
    }

    private String obterMensagemPromocional(PromocaoPrecoProduto promocao) {
        if (promocao != null && promocao.mensagemPromocional != null
                && !promocao.mensagemPromocional.trim().isEmpty()) {
            String mensagem = promocao.mensagemPromocional.trim();
            if (!"Consulta das promoções realizada com sucesso.".equalsIgnoreCase(mensagem)) {
                return mensagem;
            }
        }

        return null;
    }

    private String obterCodigoPromocional(PromocaoPrecoProduto promocao) {
        if (promocao != null && promocao.codigoPromocao != null
                && !promocao.codigoPromocao.trim().isEmpty()) {
            return promocao.codigoPromocao.trim();
        }

        return null;
    }

    private String montarResumoPromocoes(List<PromocaoPrecoProduto> promocoes) {
        if (promocoes == null || promocoes.isEmpty()) {
            return "Nenhuma promoção ativa encontrada para os itens deste "
                    + (isFluxoOrcamento() ? "orçamento" : "pedido") + ".";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Promoções ativas encontradas:\n\n");

        for (PromocaoPrecoProduto promocao : promocoes) {
            String descricao = obterDescricaoItem(promocao);
            builder.append(descricao).append("\n");
            builder.append("Preço promocional: ")
                    .append(real.format(promocao.precoAtual))
                    .append("\n");
            builder.append("Promoção: ")
                    .append(promocao.nomePromocao == null || promocao.nomePromocao.trim().isEmpty()
                            ? "Sem nome informado"
                            : promocao.nomePromocao.trim())
                    .append("\n");

            String vigenciaFormatada = formatarVigencia(promocao.vigencia);
            if (vigenciaFormatada != null) {
                builder.append("Vigência: ")
                        .append(vigenciaFormatada)
                        .append("\n");
            }

            builder.append("\n");
        }

        builder.append("Ao tocar em OK, o preço promocional será aplicado aos itens listados.");
        return builder.toString();
    }

    private String obterDescricaoItem(PromocaoPrecoProduto promocao) {
        if (isFluxoOrcamento()) {
            ItemOrcamento item = localizarItemOrcamento(promocao);
            if (item != null && item.getDescricao() != null && !item.getDescricao().trim().isEmpty()) {
                return item.getDescricao().trim();
            }
        } else {
            ItemPedidos item = localizarItemPedido(promocao);
            if (item != null && item.getDescricao() != null && !item.getDescricao().trim().isEmpty()) {
                return item.getDescricao().trim();
            }
        }

        return "Item " + (promocao != null ? promocao.codigo : "");
    }

    private ItemOrcamento localizarItemOrcamento(PromocaoPrecoProduto promocao) {
        if (promocao == null || orcamento == null || orcamento.getItens() == null) {
            return null;
        }

        for (int i = 0; i < orcamento.getItens().size(); i++) {
            ItemOrcamento item = orcamento.getItens().get(i);
            if (item == null) {
                continue;
            }

            String codigoItem = String.valueOf(item.getCoditprod()).trim();
            int posicaoItem = item.getItem() > 0 ? item.getItem() : (i + 1);

            if (promocao.codigo != null && codigoItem.equals(promocao.codigo.trim()) && promocao.posicaoItem == posicaoItem) {
                return item;
            }
        }

        for (ItemOrcamento item : orcamento.getItens()) {
            if (item == null || promocao.codigo == null) {
                continue;
            }

            if (String.valueOf(item.getCoditprod()).trim().equals(promocao.codigo.trim())) {
                return item;
            }
        }

        return null;
    }

    private List<ItemPedidos> obterItensParaConsultaPromocional() {
        if (pedido != null && pedido.getItens() != null) {
            return pedido.getItens();
        }

        List<ItemPedidos> itensConsulta = new ArrayList<>();
        if (orcamento == null || orcamento.getItens() == null) {
            return itensConsulta;
        }

        for (int i = 0; i < orcamento.getItens().size(); i++) {
            ItemOrcamento itemOrcamento = orcamento.getItens().get(i);
            if (itemOrcamento == null) {
                continue;
            }

            ItemPedidos itemPedido = new ItemPedidos();
            itemPedido.setCoditprod(itemOrcamento.getCoditprod());
            itemPedido.setDescricao(itemOrcamento.getDescricao());
            itemPedido.setPrecoUnit(itemOrcamento.getPrecoUnit());
            itemPedido.setQtcomp(itemOrcamento.getQtcomp());
            itemPedido.setItem(itemOrcamento.getItem() > 0 ? itemOrcamento.getItem() : (i + 1));
            itensConsulta.add(itemPedido);
        }

        return itensConsulta;
    }

    private boolean isFluxoOrcamento() {
        return orcamento != null;
    }

    private String formatarVigencia(String vigencia) {
        if (vigencia == null || vigencia.trim().isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat formatoApi = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            formatoApi.setLenient(false);
            Date dataVigencia = formatoApi.parse(vigencia.trim());
            return new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(dataVigencia);
        } catch (Exception ex) {
            return vigencia.trim();
        }
    }

    private ItemPedidos localizarItemPedido(PromocaoPrecoProduto promocao) {
        if (promocao == null || pedido == null || pedido.getItens() == null) {
            return null;
        }

        for (int i = 0; i < pedido.getItens().size(); i++) {
            ItemPedidos item = pedido.getItens().get(i);
            if (item == null) {
                continue;
            }

            String codigoItem = String.valueOf(item.getCoditprod()).trim();
            int posicaoItem = item.getItem() > 0 ? item.getItem() : (i + 1);

            if (promocao.codigo != null && codigoItem.equals(promocao.codigo.trim()) && promocao.posicaoItem == posicaoItem) {
                return item;
            }
        }

        for (ItemPedidos item : pedido.getItens()) {
            if (item == null || promocao.codigo == null) {
                continue;
            }

            if (String.valueOf(item.getCoditprod()).trim().equals(promocao.codigo.trim())) {
                return item;
            }
        }

        return null;
    }

    private String obterCodfilConsulta() {
        if (usuarioLogado != null && usuarioLogado.get("codfil") != null && !usuarioLogado.get("codfil").trim().isEmpty()) {
            return usuarioLogado.get("codfil").trim();
        }

        if (pedido != null && pedido.getCodfil() > 0) {
            return String.valueOf(pedido.getCodfil());
        }

        if (orcamento != null && orcamento.getCodfil() > 0) {
            return String.valueOf(orcamento.getCodfil());
        }

        return "0";
    }
}

package br.com.acalfortaleza.fikonmais.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonCondicao;

/**
 * Substitui o PopupFormaPgto quando a sessão é Fikon: a condição escolhida já define o
 * tipo de documento, e aqui o consultor só escolhe o número de parcelas permitido.
 * O resultado é uma linha de Financeiro com Condpgto = chave da condição Fikon.
 */
public final class FikonPagamentoDialog {

    public interface Callback {
        void onConfirmado(List<Financeiro> financeiro);
    }

    private FikonPagamentoDialog() {
    }

    public static void mostrar(final Activity activity, Pedidos pedido, final double total,
                               String chaveCondicao, final Callback callback) {
        final FikonCondicao condicao = FikonVendasClient.condicao(chaveCondicao);
        if (condicao == null) {
            Toast.makeText(activity,
                    "Condição de pagamento não encontrada na Fikon. Escolha outra condição.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (total <= 0) {
            Toast.makeText(activity, "Total do pedido inválido.", Toast.LENGTH_LONG).show();
            return;
        }

        final int minimo = condicao.parcelasMinimo();
        int maximo = condicao.parcelasMaximo();
        // Respeita o valor mínimo da parcela da condição.
        if (condicao.valorMinimoDaParcela > 0) {
            int porValor = (int) Math.floor(total / condicao.valorMinimoDaParcela);
            maximo = Math.max(minimo, Math.min(maximo, porValor));
        }

        if (maximo <= minimo) {
            callback.onConfirmado(financeiro(condicao, total, minimo));
            Toast.makeText(activity, descricao(condicao, total, minimo), Toast.LENGTH_SHORT).show();
            return;
        }

        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        List<String> opcoes = new ArrayList<>();
        for (int n = minimo; n <= maximo; n++) {
            opcoes.add(n + "x de " + real.format(total / n));
        }

        int padding = (int) (16 * activity.getResources().getDisplayMetrics().density);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(padding, padding / 2, padding, 0);

        TextView info = new TextView(activity);
        String tipo = condicao.nomeTipoDocumento();
        info.setText((tipo == null ? "" : tipo + "\n") + "Total: " + real.format(total)
                + (condicao.tipoParcelamento == null ? "" : "\n" + condicao.tipoParcelamento));
        layout.addView(info);

        final Spinner spinner = new Spinner(activity);
        spinner.setAdapter(new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_dropdown_item, opcoes));
        layout.addView(spinner);

        new AlertDialog.Builder(activity)
                .setTitle(condicao.nome)
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    int parcelas = minimo + spinner.getSelectedItemPosition();
                    callback.onConfirmado(financeiro(condicao, total, parcelas));
                })
                .show();
    }

    private static List<Financeiro> financeiro(FikonCondicao condicao, double total, int parcelas) {
        Financeiro fin = new Financeiro();
        fin.setCondpgto(String.valueOf(condicao.chave));
        fin.setCondicao(condicao.nome);
        fin.setEvento(condicao.nomeTipoDocumento());
        fin.setValor(Math.round(total * 100d) / 100d);
        fin.setQtprc(parcelas);
        fin.setNumprc(parcelas);
        List<Financeiro> lista = new ArrayList<>();
        lista.add(fin);
        return lista;
    }

    private static String descricao(FikonCondicao condicao, double total, int parcelas) {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return condicao.nome + ": " + parcelas + "x de " + real.format(total / parcelas);
    }
}

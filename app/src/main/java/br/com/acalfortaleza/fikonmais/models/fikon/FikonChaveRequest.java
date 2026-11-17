package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Corpo { chcriacao, pagamentos? } de transformar, gravar, aprovar e desaprovar. */
public class FikonChaveRequest {
    @SerializedName("chcriacao")
    public final long chcriacao;

    @SerializedName("pagamentos")
    public final List<FikonPagamento> pagamentos;

    public FikonChaveRequest(long chcriacao) {
        this(chcriacao, null);
    }

    public FikonChaveRequest(long chcriacao, List<FikonPagamento> pagamentos) {
        this.chcriacao = chcriacao;
        this.pagamentos = pagamentos;
    }
}

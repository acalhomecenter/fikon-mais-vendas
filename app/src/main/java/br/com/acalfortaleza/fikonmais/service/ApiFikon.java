package br.com.acalfortaleza.fikonmais.service;

import br.com.acalfortaleza.fikonmais.models.fikon.FikonCliente;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonClienteCadastroResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonClienteResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonCepResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContatoCadastroResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContatoRequest;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContatoResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonFiltroBuscaRequest;
import br.com.acalfortaleza.fikonmais.models.Produto.FiltrosProdutoResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.DadosLogin;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonProdutoResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.UsuarioResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonChaveRequest;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonDocumentoRequest;
import br.com.acalfortaleza.fikonmais.models.CupomPromocionalRequest;
import br.com.acalfortaleza.fikonmais.models.CupomPromocionalResponse;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoRequest;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoResponse;
import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiFikon {
    @POST("/api-app/v1/login")
    Call<UsuarioResponse> efetuarLogin(@Body DadosLogin body);

    @GET("/api-app/v1/cliente")
    Call<FikonClienteResponse> consultarCliente(
            @Query("cpfCnpj") String cpfCnpj,
            @Header("Authorization") String authorization);

    @GET("/api-app/v1/frete/consultarCep")
    Call<FikonCepResponse> consultarCep(
            @Query("cep") String cep,
            @Header("Authorization") String authorization);

    @POST("/api-app/v1/cliente")
    Call<FikonClienteCadastroResponse> inserirCliente(
            @Body FikonCliente cliente,
            @Header("Authorization") String authorization);

    @PUT("/api-app/v1/cliente")
    Call<FikonClienteCadastroResponse> atualizarCliente(
            @Query("codigo") String codigo,
            @Body FikonCliente cliente,
            @Header("Authorization") String authorization);

    @GET("/api-app/v1/cliente/contatoAssociado")
    Call<FikonContatoResponse> consultarContatos(
            @Query("consulta") String consulta,
            @Query("tipo") String tipo,
            @Header("Authorization") String authorization);

    @POST("/api-app/v1/cliente/cadastrarContatoAssociado")
    Call<FikonContatoCadastroResponse> cadastrarContatos(
            @Body FikonContatoRequest contatos,
            @Header("Authorization") String authorization);

    @GET("/api-app/v1/recurso")
    Call<FikonProdutoResponse> consultarProdutoPorCodigo(
            @Query("codigo") String codigo,
            @Query("tipoBusca") String tipoBusca,
            @Query("estabelecimento") String estabelecimento,
            @Query("trazerFiscal") Boolean trazerFiscal,
            @Header("Authorization") String authorization);

    @POST("/api-app/v1/recurso/pesquisar")
    Call<FikonProdutoResponse> consultarProdutosPorFiltro(
            @Body FikonFiltroBuscaRequest filtro,
            @Header("Authorization") String authorization);

    // ---------- Preço e cupom ----------

    /** Resposta sem envelope: { codigoPromocao, descontoValorTotal, produtos, ... }. */
    @POST("/api-app/v1/preco/promocao")
    Call<PromocaoPrecoResponse> consultarPromocoes(
            @Body List<PromocaoPrecoRequest> itens,
            @Header("Authorization") String authorization);

    /** Cupom inválido volta 200 com dados.identificadorReserva = null. */
    @POST("/api-app/v1/preco/cupom")
    Call<CupomPromocionalResponse> aplicarCupom(
            @Body CupomPromocionalRequest cupom,
            @Header("Authorization") String authorization);

    /** Corpo { identificadorReserva }; já confirmado volta 400. */
    @POST("/api-app/v1/preco/cupom/confirmar")
    Call<JsonObject> confirmarCupom(
            @Body JsonObject reserva,
            @Header("Authorization") String authorization);

    // ---------- Orçamentos e pedidos (respostas em { data }, objeto ou lista) ----------

    @GET("/api-app/v1/comercial/orcamentos")
    Call<JsonObject> consultarOrcamento(
            @Query("chcriacao") Long chcriacao,
            @Header("Authorization") String authorization);

    @GET("/api-app/v1/comercial/orcamentos")
    Call<JsonObject> listarOrcamentos(
            @Query("dataInicial") String dataInicial,
            @Query("dataFinal") String dataFinal,
            @Query("pessoa") String pessoa,
            @Query("representante") String representante,
            @Query("estabelecimento") String estabelecimento,
            @Header("Authorization") String authorization);

    @POST("/api-app/v1/comercial/orcamentos")
    Call<JsonObject> criarOrcamento(
            @Body FikonDocumentoRequest orcamento,
            @Header("Authorization") String authorization);

    @PUT("/api-app/v1/comercial/editar")
    Call<JsonObject> editarDocumento(
            @Body FikonDocumentoRequest documento,
            @Header("Authorization") String authorization);

    @POST("/api-app/v1/comercial/orcamentos/pedido")
    Call<JsonObject> transformarEmPedido(
            @Body FikonChaveRequest orcamento,
            @Header("Authorization") String authorization);

    @PUT("/api-app/v1/comercial/pedidos/gravar")
    Call<JsonObject> gravarPedido(
            @Body FikonChaveRequest pedido,
            @Header("Authorization") String authorization);

    @PUT("/api-app/v1/comercial/pedidos/aprovar")
    Call<JsonObject> aprovarPedido(
            @Body FikonChaveRequest pedido,
            @Header("Authorization") String authorization);

    @PUT("/api-app/v1/comercial/pedidos/desaprovar")
    Call<JsonObject> desaprovarPedido(
            @Body FikonChaveRequest pedido,
            @Header("Authorization") String authorization);

    /** Resposta { name, dadosVenda }. */
    @GET("/api-app/v1/pedidos")
    Call<JsonObject> consultarPedido(
            @Query("idPedido") long idPedido,
            @Header("Authorization") String authorization);

    @DELETE("/api-app/v1/pedidos")
    Call<JsonObject> excluirPedido(
            @Query("idPedido") long idPedido,
            @Header("Authorization") String authorization);

    /** Resposta { condicoes, erros }. */
    @GET("/api-app/v1/pedidos/pesquisarCondicoes")
    Call<JsonObject> pesquisarCondicoes(
            @Header("Authorization") String authorization);

    @GET("/api-app/v1/recurso/filtros")
    Call<FiltrosProdutoResponse> consultarFiltrosProdutos(
            @Header("Authorization") String authorization);
}

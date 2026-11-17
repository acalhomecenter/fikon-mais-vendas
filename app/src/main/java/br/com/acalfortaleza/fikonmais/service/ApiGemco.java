package br.com.acalfortaleza.fikonmais.service;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.model.FreteServicoOrcamento;
import br.com.acalfortaleza.fikonmais.model.FreteServicoPost;
import br.com.acalfortaleza.fikonmais.model.FreteServicoResposta;
import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.Arquiteto;
import br.com.acalfortaleza.fikonmais.models.BeneficioPromocao;
import br.com.acalfortaleza.fikonmais.models.Campanha;
import br.com.acalfortaleza.fikonmais.models.Cargo;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.CodigoEncomenda;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.Conveniada;
import br.com.acalfortaleza.fikonmais.models.Credito;
import br.com.acalfortaleza.fikonmais.models.Credz.Invitation;
import br.com.acalfortaleza.fikonmais.models.Credz.PlanSimulation;
import br.com.acalfortaleza.fikonmais.models.Cupom;
import br.com.acalfortaleza.fikonmais.models.CupomPromocionalRequest;
import br.com.acalfortaleza.fikonmais.models.CupomPromocionalResponse;
import br.com.acalfortaleza.fikonmais.models.DadosBancarios;
import br.com.acalfortaleza.fikonmais.models.Device;
import br.com.acalfortaleza.fikonmais.models.Devolucao;
import br.com.acalfortaleza.fikonmais.models.Encomenda;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Entregas;
import br.com.acalfortaleza.fikonmais.models.FilialFikonGenco;
import br.com.acalfortaleza.fikonmais.models.FasesObra;
import br.com.acalfortaleza.fikonmais.models.Faturamento;
import br.com.acalfortaleza.fikonmais.models.FaturamentoLinha;
import br.com.acalfortaleza.fikonmais.models.Filial;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.FormaPagamento;
import br.com.acalfortaleza.fikonmais.models.HorarioRetira;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.Midias;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import br.com.acalfortaleza.fikonmais.models.NomeAmbiente;
import br.com.acalfortaleza.fikonmais.models.Notas;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.PedTransferencia;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PoliticaDesconto;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoRequest;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoResponse;
import br.com.acalfortaleza.fikonmais.models.PrecoConc;
import br.com.acalfortaleza.fikonmais.models.PrecoDeProduto;
import br.com.acalfortaleza.fikonmais.models.ProdutoDetalhe;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.Rank;
import br.com.acalfortaleza.fikonmais.models.Recibo;
import br.com.acalfortaleza.fikonmais.models.Regulamento;
import br.com.acalfortaleza.fikonmais.models.RetonoBeneficio;
import br.com.acalfortaleza.fikonmais.models.Segmento;
import br.com.acalfortaleza.fikonmais.models.Senha;
import br.com.acalfortaleza.fikonmais.models.Setor;
import br.com.acalfortaleza.fikonmais.models.Sugestoes;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import br.com.acalfortaleza.fikonmais.models.Usuario;
import br.com.acalfortaleza.fikonmais.models.UtilPedido;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEmail;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEstoque;
import br.com.acalfortaleza.fikonmais.model.SalvarPixRequest;
import br.com.acalfortaleza.fikonmais.model.AtualizarStatusPixRequest;
import br.com.acalfortaleza.fikonmais.model.AtualizarStatusPixResponse;
import br.com.acalfortaleza.fikonmais.models.CupomPedidoRequest;
import br.com.acalfortaleza.fikonmais.models.VendaPerfeita;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import okhttp3.ResponseBody;

// --- FIM DA CLASSE DE MODELO ---

public interface ApiGemco {

    //Producao - Api

      static final String BASE_URL = "http://api.acalhomecenter.com.br:8086/ApiAcal/";
      static final String PROMO_BASE_URL = "https://acal.fikon.com.br";

    //Homologacao - Api
      //  public static final String BASE_URL = "http://apihomol.acalhomecenter.com.br:8087/ApiAcal/";
     // static final String PROMO_BASE_URL = "https://dacal.fikon.com.br";


    //Producao - Token

        public static final String token = "mO1zXrxpsNesVkiSACigxRBLEdWMccu_-4zjZNuVWLQNJGntPzd_KRGN4Wfh27WB6WV07kuz11WJGiCft64Q_lNO2PTAS_tS3t2_g77uKsoCqBVSBAeNPUbrp8RhQjsEmJi5H8nuOenz8MvGfOhPq4U8hn8-O6nv5YXoCQhxBRC_sD12ehCTOAuesk5lVQdqIpy86ovau1vrxQnQiTbp0Visu007duMWrYmOq2raIIQ";
      public static final String PROMO_TOKEN = "01nwXhX8CgJwGhwHTNb7dlX8Jzs54pwD60FZqRGBWm5rSAEzGvoumP4vcc*k2v*NHu";

     // Homologacao - Token
      //public static final String token =  "oc0KOwHua_G_L7WWOp6VAkyZOESlFbUauiI1pnQnplhP26N4uqs_jLTSFdyrQ3eLt6i5NbmshNUPJZxgU25x_rvP7ETBdb-jp2SO4XUew4SuU5JMyEpbFpuJIVQjBfy5lp5OWWzrgSENRKFs2YDrN9RFiQs0uQfQcoV6wLz-8YPKrVDa4PR2j-6nwBZr5daGnXgpNK4jDB5PUuZ_l6_XvX9o1-at_61gdy8rLDY2oWI";
     // public static final String PROMO_TOKEN = "019i5zNyUUiR*BNbxwV_HhcSVrfOTZL0goKxlvNekeW3SP_XmoG*YuGV7XW21Evm3Y";

    @GET("api/v1/Produtos")
    Call<List<ProdutoGemco>> listProdutoGemco(@Query("tipo") String tipo,@Query("valor") String valor, @Query("codFilPreco") String codFilPreco, @Header("Authorization") String auth);

    @GET("api/v1/Produtos/Pesquisa")
    Call<List<PrecoConc>> listPrecoConc(@Query("codigo") String codigo,  @Header("Authorization") String auth);

    @GET("api/v1/Produtos/PrecoDePromocao")
    Call<PrecoDeProduto> getPrecoDePromocao(@Query("coditprod") int coditprod, @Query("codfil") int codfil, @Header("Authorization") String auth);

    @GET("api/v1/Produtos/FilialFikon")
    Call<FilialFikonGenco> consultarFilialFikon(@Query("codfil") int codfil, @Header("Authorization") String auth);

    @POST("api-acal/api/v1/preco/promocao")
    Call<PromocaoPrecoResponse> consultarPrecosPromocionais(@Body List<PromocaoPrecoRequest> itens, @Header("Authorization") String auth);

    @POST("api-app/v1/preco/cupom")
    Call<CupomPromocionalResponse> aplicarCupomPromocional(@Body CupomPromocionalRequest request, @Header("Authorization") String auth);

    @GET("api/v1/Produtos")
    Call<List<ProdutoDetalhe>> listProdutoDetalhados(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/Fornecedor")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosForn(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/Linha")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosLinha(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/Familia")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosFamilia(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/Grupo")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosGrupo(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/SbGrupo")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosSbgrupo(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/Especific")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosEspecific(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/Situacao")
    Call<List<ProdutoDetalhe>> listProdutoDetalhadosSituacao(@Header("Authorization") String auth);

    @GET("api/v1/Produtos/ConsultaPonta")
    Call<List<ProdutoGemco>> listProdutoPonta(@Query("fornecedor") String fornecedor,@Query("linha") String linha,@Query("familia") String familia, @Query("grupo") String grupo, @Query("subgrupo") String subgrupo, @Query("especificacao") String especificacao, @Query("situacao") String situacao, @Query("codfil") int codfil, @Header("Authorization") String auth);

    @GET("api/v1/Produtos/Consulta")
    Call<List<ProdutoGemco>> listProdutoGemcoAvancado(@Query("fornecedor") String fornecedor,@Query("linha") String linha,@Query("familia") String familia, @Query("grupo") String grupo, @Query("subgrupo") String subgrupo, @Query("especificacao") String especificacao,  @Query("situacao") String situacao, @Query("descricao") String descricao, @Query("codforne") String codforne, @Query("codfil") int codfil, @Header("Authorization") String auth);

    @GET("api/v1/Consultores")
    Call<List<Consultor>> GetConsultor(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);


    @GET("api/v1/Consultores/Acumulado")
    Call<List<Consultor>> GetConsultorAcumulado(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);

    @GET("api/v1/Consultores/AcumuladoGestor")
    Call<List<Consultor>> GetGestorAcumulado(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);


    @GET("api/v1/Consultores/Dia")
    Call<List<Consultor>> GetConsultorDia(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);

    @GET("api/v1/Consultores/DiaGestor")
    Call<List<Consultor>> GetGestorDia(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);


    @GET("api/v1/Consultores/Ontem")
    Call<List<Consultor>> GetConsultorOntem(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);

    @GET("api/v1/Consultores/OntemGestor")
    Call<List<Consultor>> GetGestorOntem(@Query("Cpf") String cpf, @Query("Filial") String filial, @Header("Authorization") String auth);


    @GET("api/v1/ReenvioSenha")
    Call<List<Senha>> GetSenha(@Query("Cpf") String Cpf, @Header("Authorization") String auth);

    @Multipart
    @POST("api/v1/UploadAvatar")
    Call<String> UploadAvatar(@Part MultipartBody.Part file, @Part("file") RequestBody name,@Header("Authorization") String auth);

    @GET("api/v1/Entregas")
    Call<List<Entregas>> GetEntregasDia(@Query("Codigo") int Codigo, @Header("Authorization") String auth);

    @GET("api/v1/EntregasMes")
    Call<List<Entregas>> GetEntregasMes(@Query("Codigo") int Codigo, @Header("Authorization") String auth);

    @GET("api/v1/EntregasCgccpf")
    Call<List<Entregas>> GetEntregasCgccpf(@Query("Cgccpf") double Cgccpf, @Header("Authorization") String auth);

    @GET("api/v1/EntregasPedido")
    Call<List<Entregas>> GetEntregasPedido(@Query("Pedido") int Pedido, @Header("Authorization") String auth);

    @GET("api/v1/Cliente")
    Call<List<Cliente>> GetClienteCgccpf(@Query("Cgccpf") String Cgccpf,@Query("codfil") int codfil,@Query("cpfConsultor") String cpfconsultor,   @Header("Authorization") String auth);

    @GET("api/v1/Cliente/ContatoCadastrado")
    Call<List<Contato>> GetContatoCadastrado(@Query("cpf") String Cgccpf, @Header("Authorization") String auth);

    @GET("api/v1/Cliente")
    Call<List<Cliente>> GetClienteNome(@Query("Nome") String Nome, @Header("Authorization") String auth);

    @GET("api/v1/Cliente")
    Call<List<Cliente>> GetEspecificador(@Query("Nome") String Nome, @Query("Codvendr") String Codvendr, @Header("Authorization") String auth);


    @GET("api/v1/Cliente")
    Call<List<Cliente>> GetClienteCodvendr(@Query("codvendr") int codvendr, @Query("natjur") String natjur, @Query("situacao") String situacao, @Header("Authorization") String auth);


    @POST("api/v1/Cliente")
    Call<String> saveClienteOrcamento(@Body Cliente cliente, @Query("tipo") String tipo, @Header("Authorization") String auth);

    @GET("api/v1/Cliente/Cargo")
    Call<List<Cargo>> GetCargos(@Header("Authorization") String auth);

    @GET("api/v1/Cliente/Segmento")
    Call<List<Segmento>> GetSegmentos(@Header("Authorization") String auth);

    @GET("api/v1/Cliente/CargoContatos")
    Call<List<Cargo>> GetCargosContatos(@Header("Authorization") String auth);

    @GET("api/v1/Cliente/Setor")
    Call<List<Setor>> GetSetor(@Header("Authorization") String auth);

    @GET("api/v1/Cliente")
    Call<Enderecos> GetEnderecoConsultado(@Query("Cep") String Cep, @Header("Authorization") String auth);

    @POST("api/v1/Cliente/Endereco")
    Call<String> saveEndereco(@Body Enderecos endereco, @Query("tipo") String tipo, @Header("Authorization") String auth);

    @GET("api/v1/Orcamento/Condicoes")
    Call<List<CondPagamento>> GetCondPagamento(@Query("codfil") int codfil, @Header("Authorization") String auth);

    @POST("api/v1/Orcamento")
    Call<String> saveOrcamento(@Body Orcamento orcamento,  @Header("Authorization") String auth);

    @GET("api/v1/Orcamento/Individual")
    Call<List<Orcamento>> GetOrcamentoConsultaIndividual(@Query("consulta") String consulta, @Query("tipo") String tipo ,@Header("Authorization") String auth);

    @POST("api/v1/Orcamento/Atualizar")
    Call<String> atualizarOrc(@Query("numorc") String numorc, @Query("numped") String numped,  @Header("Authorization") String auth);

    @GET("api/v1/Orcamento")
    Call<List<Orcamento>> GetOrcamentos(@Query("dtinicial") String dtinicial, @Query("dtfinal") String dtfinal, @Query("codvendr") int codvendr, @Query("codfil") int codfil ,@Query("codarq") int codarq,@Header("Authorization") String auth);


    @GET("api/v1/Orcamento")
    Call<String> IntegraOrcamento(@Query("numorc") String numorc, @Header("Authorization") String auth);

    @GET("api/v1/Orcamento")
    Call<List<Orcamento>> GetOrcamentoConsulta(@Query("consulta") String consulta, @Query("tipo") String tipo ,@Header("Authorization") String auth);

    @POST("api/v1/FreteServico/OrcSelecionado")
    Call<String> PostFreteServicoOrcamento(@Header("Authorization") String auth, @Body FreteServicoPost body);

    @GET("api/v1/FreteServico/OrcSelecionado")
    Call<FreteServicoResposta> GetFreteServicoOrcamento(@Header("Authorization") String auth, @Query("orcamento") int orcamento);

    @POST("api/v1/Sugestoes/Vendas")
    Call<String> saveSugestao(@Body Sugestoes sugestao, @Header("Authorization") String auth);

    @GET("api/v1/Combobox/Motivo")
    Call<ArrayList<String>> GetMotivos(@Query("tipo") String tipo, @Header("Authorization") String auth);

    @GET("api/v1/Combobox/FormaPgto")
    Call<List<FormaPagamento>> GetFormasPgto(@Query("codfil") int codfil, @Query("condpgto") String condpgto, @Header("Authorization") String auth);

    @GET("api/v1/Combobox/MotivoDesconto")
    Call<List<MotivoDesconto>> GetMotivosDesconto(@Header("Authorization") String auth);

    @GET("api/v1/Combobox/FilialPedido")
    Call<List<Filial>> GetFilial(@Header("Authorization") String auth);

    @GET("api/v1/Combobox/Midia")
    Call<List<Midias>> GetMidias(@Header("Authorization") String auth);

    @GET("api/v1/Combobox/Conveniada")
    Call<List<Conveniada>> GetConveniadas(@Header("Authorization") String auth);

    @GET("api/v1/Pedidos/Condicoes")
    Call<List<CondPagamento>> GetCondPagamentoPedidos(@Header("Authorization") String auth);

    @GET("api/v1/Orcamento/Fases")
    Call<List<FasesObra>> GetFasesOrcamento(@Header("Authorization") String auth);


    @GET("api/v1/Orcamento/Arquitetos")
    Call<List<Arquiteto>> GetArquitetosOrcamento(@Header("Authorization") String auth);


    @POST("api/v1/Pedidos")
    Call<String> savePedido(@Body Pedidos pedido,  @Header("Authorization") String auth);

    @POST("api/v1/Pedidos/Cupom")
    Call<String> SalvarCupomPedido(@Body CupomPedidoRequest request, @Header("Authorization") String auth);

    @GET("api/v1/Pedidos/Cupom/JaUtilizado")
    Call<Boolean> VerificarCupomJaUtilizado(@Query("codcli") int codcli,
                                            @Query("codigoCupom") String codigoCupom,
                                            @Header("Authorization") String auth);

    @DELETE("api/v1/Pedidos/Cupom/{numpedven}")
    Call<String> ExcluirCupomPedido(@Path("numpedven") long numpedven, @Header("Authorization") String auth);

    @GET("api/v1/Pedidos")
    Call<List<Pedidos>> GetPedidos(@Query("dtinicial") String dtinicial, @Query("dtfinal") String dtfinal, @Query("codvendr") int codvendr, @Query("codfil") int codfil , @Query("codarq") int codarq,@Header("Authorization") String auth);

    @GET("api/v1/Pedidos/ListaPedidos")
    Call<List<Pedidos>> GetPedidosList(@Query("dtinicial") String dtinicial, @Query("dtfinal") String dtfinal, @Query("codvendr") int codvendr, @Query("codfil") int codfil , @Query("codarq") int codarq ,@Query("consulta") String consulta, @Query("tipo") String tipo ,@Header("Authorization") String auth);


    @GET("api/v1/Pedidos")
    Call<String> IntegraPedido(@Query("numpedido") String numpedido, @Header("Authorization") String auth);


    @GET("api/v1/Pedidos/Notas")
    Call<List<Notas>> GetNotasPedido(@Query("numpedven") double numpedido, @Header("Authorization") String auth);
    @GET("api/v1/Pedidos/Transferencias")
    Call<List<PedTransferencia>> GetTransfPedido(@Query("numpedven") double numpedido, @Header("Authorization") String auth);

    @GET("api/v1/Pedidos/Finnanceiro")
    Call<List<Financeiro>> GetFinanceiroPedido(@Query("numpedven") double numpedido, @Query("codfil") int codfil, @Header("Authorization") String auth);


    @GET("api/v1/Pedidos")
    Call<List<Pedidos>> GetPedidosConsulta(@Query("consulta") String consulta, @Query("tipo") String tipo ,@Header("Authorization") String auth);

    @GET("api/v1/Pedidos/Tpnota")
    Call<List<TpNotas>> GetTpNotas( @Header("Authorization") String auth);

    @POST("api/v1/Pedidos/Obs")
    Call<String> setObs(@Query("pedido") String pedido, @Query("obs") String obs , @Header("Authorization") String auth);


    @POST("api/v1/Pedidos/Inicializar")
    Call<List<String>> InicializarPedido(@Query("codvendr") int codvendr, @Query("codfil") int codfil , @Query("tpnota") int tpnota , @Query("coditprod") String coditprod , @Query("qtcomp") double qtcomp , @Query("precounit") double precounit , @Header("Authorization") String auth);

    @POST("api/v1/Pedidos/Estoque")
    Call<List<ItemPedidos>> ConsultaEstoque(@Body List<ItemPedidos> itemPedidos, @Header("Authorization") String auth);


    @GET("api/v1/Faturamento")
    Call<Faturamento> GetFaturamento(@Query("dtinicial") String dtinicial, @Query("dtfinal") String dtfinal, @Query("codvendr") int codvendr ,@Query("filial") String filial, @Header("Authorization") String auth);

    @GET("api/v1/Faturamento/Linha")
    Call<List<FaturamentoLinha>> GetFaturamentoLinha(@Query("dtinicial") String dtinicial, @Query("dtfinal") String dtfinal, @Query("codvendr") int codvendr , @Header("Authorization") String auth);


    @GET("api/v1/Devolucao")
    Call<List<Devolucao>> GetDevolucoes(@Query("codvendr") int codvendr , @Header("Authorization") String auth);

    @GET("api/v1/Cupom")
    Call<List<Cupom>> GetCupons(@Query("codvendr") int codvendr , @Header("Authorization") String auth);

    @GET("api/v1/Campanha")
    Call<List<Campanha>> GetPontosDetalhado(@Query("CodConsultor") int codvendr ,@Query("CodFilial") int codfil , @Header("Authorization") String auth);

    @GET("api/v1/Campanha/Rank")
    Call<List<Rank>> GetRank(@Query("CodConsultor") int codvendr , @Query("CodFilial") int codfil , @Header("Authorization") String auth);

    @GET("api/v1/Campanha/Regulamento")
    Call<List<Regulamento>> GetRegulamento( @Header("Authorization") String auth);

    @GET("api/v1/Campanha/Atualizacao")
    Call<JsonObject> GetAtualizacaoCampanha(@Query("CodConsultor") int codigoConsultor, @Header("Authorization") String auth);

    @GET("api/v1/Orcamento/Bancos")
    Call<List<DadosBancarios>> GetDadosBancarios(@Header("Authorization") String auth);

    @POST("api/v1/Cliente/Contato")
    Call<String> saveContato(@Body Contato contato, @Query("tipo") String tipo, @Header("Authorization") String auth);


    @GET("api/v1/Pedidos/Hora")
    Call<List<HorarioRetira>> GetHorarios(@Query("data") String data,@Query("tipo") String tipo,@Header("Authorization") String auth);

    @POST("api/v1/Pedidos/Agendar")
    Call<String> SetAgendamentoRetira(@Query("numpedven") String numpedven,@Query("codcli") String codcli,@Query("data") String data,@Query("hora") String hora,@Query("peso") String peso,@Query("placa") String placa,@Query("tipo") String tipo,@Query("codfil") int codfil,@Query("codvendr") int codvendr,@Query("status") String status,@Query("usuario") String usuario,@Body Cliente cliente,@Query("agendamento") String agendamento,@Query("paletizado") String paletizado  ,@Header("Authorization") String auth);

    @GET("api/v1/Pedidos/Recibos")
    Call<List<Recibo>> GetRecibos(@Query("codcli") String codcli, @Header("Authorization") String auth);

    @POST("api/v1/Promocao")
    Call<List<BeneficioPromocao>> consultarPromocao(@Query("codigoPromo") String codigoPromo, @Body Pedidos pedidos, @Header("Authorization") String auth);

    @GET("api/v1/Pedidos/LiberacaoComercial")
    Call<String> LiberacaoComercial(@Query("numpedven") String numpedven, @Query("codlib") int codlib, @Query("codfil") int codfil, @Query("temcredi") String temcredi, @Header("Authorization") String auth);


    @GET("api/v1/Pedidos/LiberacaoCredito")
    Call<String> LiberacaoCredito(@Query("numpedven") String numpedven, @Query("codlib") int codlib, @Query("codfil") int codfil, @Query("liberador") String liberador, @Query("codcli") String codcli, @Header("Authorization") String auth);


    @GET("api/v1/Produtos/ValidarEstoque")
    Call<String> setValidacaoEstoque(@Query("filialEstoque") int filialEstoque,@Query("filped") int filped, @Query("codvendr") int codvendr, @Query("coditprod") int coditprod, @Query("qtdsolicitada") double qtdsolicitada, @Query("lotevencto") String lotvencto, @Header("Authorization") String auth);


    @GET("api/v1/Produtos/ConsultaValidacaoEstoque")
    Call<List<ValidacaoEstoque>> getValidacaoEstoque(@Query("codvendr") int codvendr , @Header("Authorization") String auth);


    @GET("api/v1/Pedidos/VerificarCredito")
    Call<Credito> getVerificarCredito(@Query("codcli") String codcli , @Query("valorPedido") double valorPedido, @Header("Authorization") String auth);


    @GET("api/v1/Orcamento/Consultores")
    Call<List<Consultor>> GetConsultor(@Header("Authorization") String auth);



    @GET("api/v1/Pedidos/LoteProduto")
    Call<List<Lotevencto>> GetLotevenctoProduto(@Query("coditprod") String coditprod, @Query("filorig") String filorig ,  @Query("qtde") Double qtde, @Header("Authorization") String auth);



    @GET("api/v1/Colaboradores/Verificar")
    Call<String> GetVerificarSenha(@Query("senha") String senha,  @Header("Authorization") String auth);

    @GET("api/v1/Colaboradores/Aprovador")
    Call<List<String>> getAprovador( @Header("Authorization") String auth);


    @POST("api/v1/Colaboradores/Device")
    Call<String> setDevice(@Body Device device, @Header("Authorization") String auth);


    @POST("api/v1/Orcamento/GerarCarrinho")
    Call<String> gerarCarrinho(@Body Orcamento orcamento,  @Header("Authorization") String auth);


    @POST("api/v1/Email/ValidarEmail")
    Call<ValidacaoEmail> validarEmail(@Query("email") String email , @Header("Authorization") String auth);

    @GET("api/v1/Pedidos/Status")
    Call<UtilPedido> getStatusPedido(@Query("numpedven") String numpedven , @Header("Authorization") String auth);

    @POST("api/v1/Orcamento/EnviarAnexoMoskit")
    Call<String> enviarAnexoMoskit(@Query("urlAnexo") String urlAnexo, @Query("numOrc") String numOrc, @Query("nomeArquivo") String nomeArquivo ,@Header("Authorization") String auth);

    @GET("api/v1/Produtos/ConsultarNomesAmbientes")
    Call<List<NomeAmbiente>> consultarNomesAmbientes(@Header("Authorization") String auth);

    @POST("api/v1/Produtos/AtualizarImagemVtex")
    Call<String> atualizarImagemVtex(@Query("codigo") int codigo, @Query("imagem") String imagem, @Header("Authorization") String auth);

    @GET("api/v1/Produtos/AtualizarImagemVtex/{codigo}")
    Call<String> atualizarImagemVtexGet(@Path("codigo") int codigo, @Query("imagem") String imagem, @Header("Authorization") String auth);

    @POST("api/v1/Pedidos/PoliticaDesconto")
    Call<PoliticaDesconto> GetPoliticaDesconto(@Body Pedidos pedido, @Header("Authorization") String auth);

    @GET("api/v1/Pedidos/MetasVendaPerfeita")
    Call<VendaPerfeita> GetMetasVendaPerfeita(@Query("codfil") int codfil, @Query("codvendr") int codvendr, @Query("natjurcli") String natjurCliente, @Query("dataPedido") String dataPedido, @Header("Authorization") String auth);

    @POST("api/v1/Orcamento/CodigoEncomenda")
    Call<CodigoEncomenda> SalvarCodigoEncomenda(@Body CodigoEncomenda codigoEncomenda, @Header("Authorization") String auth);

    @GET("api/v1/Orcamento/CodigoEncomenda")
    Call<CodigoEncomenda> ConsultarCodigoEncomenda(@Query("numorc") long numorc, @Header("Authorization") String auth);

    @POST("api/v1/Orcamento/CodigoEncomenda/Enviar")
    Call<String> EnviarCodigoEncomenda(@Body Orcamento orcamento, @Header("Authorization") String auth);

    @GET("api/v1/Orcamento/PrevisaoEncomenda")
    Call<String> ConsultarPrevisaoEncomenda(@Query("fluig") long fluig, @Query("coditprod") int coditprod, @Header("Authorization") String auth);

    @GET("api/v1/FixPay")
    Call<ResponseBody> GetLinksPagamento(@Query("pedido") long pedido, @Header("Authorization") String auth);

    @GET("api/v1/FixPay/ConsultarLinksCartao")
    Call<ResponseBody> GetLinksCartao(@Query("pedido") long pedido, @Header("Authorization") String auth);

    @GET("api/v1/FixPay")
    Call<String> consultaPix(@Query("pedido") long pedido, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/EnviarLinks")
    Call<String> EnviarLinksPagamento(@Body Pedidos pedido, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/EnviarLinksPix")
    Call<String> EnviarLinksPagamentoPix(@Body Pedidos pedido, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/ReenviarPixWhatsapp")
    Call<String> ReenviarPixWhatsapp(@Body Pedidos pedido, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/EnviarWhatsappLinks")
    Call<String> EnviarWhatsappLinks(@Body Pedidos pedido, @Header("Authorization") String auth);

    @GET("api/v1/Encomenda")
    Call<List<Encomenda>> GetEncomendas(@Query("Codvendr") int codvendr , @Query("Codfil") int codfil , @Header("Authorization") String auth);

    @GET("api/v1/FixPay/TokenFilial")
    Call<String> consultarTokenFilial(@Query("codfil") int codfil, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/ProcessarCobrancaPix")
    Call<String> processarCobrancaPix(@Query("txid") String txid, @Query("pixCopiaCola") String pixCopiaCola, @Query("valor") double valor, @Query("numpedven") String numpedven, @Query("codcli") String codcli, @Query("telefone") String telefone, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/SalvarPixGerado")
    Call<String> salvarPixGerado(@Body SalvarPixRequest request, @Header("Authorization") String auth);

    @POST("api/v1/FixPay/AtualizarStatusTokenLink")
    Call<AtualizarStatusPixResponse> atualizarStatusPix(@Body AtualizarStatusPixRequest request, @Header("Authorization") String auth);

    @GET("api/v1/FreteServico")
    Call<List<br.com.acalfortaleza.fikonmais.models.FreteServico>> GetFreteServico(@Header("Authorization") String auth);

    @POST("api/v1/FreteServico/Selecionado")
    Call<String> SalvarFreteServicoSelecionado(@Body br.com.acalfortaleza.fikonmais.model.FreteServicoSelecionadoRequest request, @Header("Authorization") String auth);

    // --- MÉTODO ATUALIZADO ---
    @GET("api/v1/FreteServico/Selecionado")
    Call<FreteServicoResponse> ConsultarFreteServicoSelecionado(@Query("pedido") long pedido, @Header("Authorization") String auth);

    @GET("api/v1/Metas/BonusVendaAvista")
    Call<List<br.com.acalfortaleza.fikonmais.models.Metas.BonusVendaAvista>> GetBonusVendaAvista(@Query("dataIni") String dataIni, @Query("dataFim") String dataFim, @Query("consultor") Integer consultor, @Header("Authorization") String auth);

    @GET("api/v1/Metas/MetasConsultor")
    Call<br.com.acalfortaleza.fikonmais.models.Metas.MetasConsultor> GetMetasConsultor(@Header("Authorization") String auth);

    @GET("api/v1/Metas/BonusArrecadeFrete")
    Call<List<br.com.acalfortaleza.fikonmais.models.Metas.BonusArrecadeFrete>> GetBonusArrecadeFrete(@Query("dataIni") String dataIni, @Query("dataFim") String dataFim, @Query("consultor") Integer consultor, @Header("Authorization") String auth);

    @GET("api/v1/Metas/BonusLinhasPositivadas")
    Call<List<br.com.acalfortaleza.fikonmais.models.Metas.BonusLinhasPositivadas>> GetBonusLinhasPositivadas(@Query("dataIni") String dataIni, @Query("dataFim") String dataFim, @Query("consultor") Integer consultor, @Header("Authorization") String auth);

    @GET("api/v1/Metas/ComissaoVariavel")
    Call<List<br.com.acalfortaleza.fikonmais.models.Metas.ComissaoVariavelProduto>> GetComissaoVariavel(@Query("dataIni") String dataIni, @Query("dataFim") String dataFim, @Query("codigo") String codigo, @Query("filial") String filial, @Header("Authorization") String auth);


    public interface Credz{

        @POST("api/v1/Credz/Invitation")
        Call<Invitation> EnviarInvitation(@Body Cliente cliente, @Query("tipo") String tipo, @Query("codfil") int codfil , @Header("Authorization") String auth);

        @POST("api/v1/Credz/Simulation")
        Call<PlanSimulation> RealizarSimulacaoPlanos(@Query("cpf") String cpf, @Query("numpedven") String numpedven, @Query("vltotal") float vltotal, @Query("vlentrada") float vlentrada, @Query("codfil") int codfil, @Header("Authorization") String auth);

        @POST("api/v1/Credz/Plan")
        Call<String> GravarPlanId(@Query("planid") String planid, @Query("numpedven") String numpedven, @Header("Authorization") String auth);

    }



}

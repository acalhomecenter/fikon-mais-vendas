package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.CodigoBarra;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.Lotes;
import br.com.acalfortaleza.fikonmais.models.Produto.FiltrosProdutoResponse;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;
import br.com.acalfortaleza.fikonmais.models.Segmento;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonCliente;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonClienteCadastroResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonClienteResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonCepResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContato;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContatoCadastroResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContatoRequest;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonContatoResponse;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonEndereco;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonFiltroBuscaRequest;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonProduto;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonProdutoResponse;
import br.com.acalfortaleza.fikonmais.service.ApiFikon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class FikonApiClient {
    public static final String DEFAULT_BASE_URL = "https://dfikon.fikon.com.br/";
    private static final String PREFERENCES_NAME = "usuario.salvo";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_BASE_URL = "base";
    private static final String KEY_ESTABLISHMENT = "chaveEstabelecimento";
    private static final String TAG = "FikonApiClient";

    private FikonApiClient() {
    }

    public static String getEstabelecimento(Context context) {
        int estabelecimento = preferences(context).getInt(KEY_ESTABLISHMENT, 0);
        return estabelecimento == 0 ? "" : String.valueOf(estabelecimento);
    }

    public static ApiFikon api(Context context) {
        return createApi(getBaseUrl(context));
    }

    public static String authorization(Context context) {
        return getAuthorization(context);
    }

    public static String baseUrl(Context context) {
        return getBaseUrl(context);
    }

    public static FiltrosProdutoResponse consultarFiltrosProdutos(Context context)
            throws IOException {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            throw new IOException("Fikon product filters require an authenticated session");
        }

        Response<FiltrosProdutoResponse> response = api(context)
                .consultarFiltrosProdutos(authorization)
                .execute();
        FiltrosProdutoResponse body = response.body();
        if (!response.isSuccessful() || body == null || !body.isSucesso()
                || body.getDados() == null) {
            String message = body == null ? null : body.getMensagem();
            throw new IOException(message == null || message.trim().isEmpty()
                    ? "Fikon product filters request failed (HTTP " + response.code() + ")"
                    : message);
        }
        return body;
    }

    public static List<br.com.acalfortaleza.fikonmais.models.ProdutoGemco> consultarProdutosPorCodigo(
            Context context, String codigo, String tipoBusca, String estabelecimento) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            Log.e(TAG, "Fikon product lookup cannot run without an authenticated session");
            return null;
        }

        try {
            Response<FikonProdutoResponse> response = api(context)
                    // trazerFiscal=false sempre: nem toda base tem custo e tributos
                    // cadastrados, e a simulação fiscal deixa a busca lenta.
                    .consultarProdutoPorCodigo(codigo, tipoBusca, estabelecimento,
                            Boolean.FALSE, authorization)
                    .execute();
            FikonProdutoResponse body = response.body();
            if (!response.isSuccessful() || body == null || !body.isSucesso()
                    || body.getDados() == null) {
                Log.e(TAG, "Fikon product lookup failed (HTTP " + response.code() + ")");
                return null;
            }

            List<br.com.acalfortaleza.fikonmais.models.ProdutoGemco> produtos = new ArrayList<>();
            for (FikonProduto produto : body.getDados()) {
                if (produto != null) {
                    ProdutoGemco mapped = mapProduto(context, produto);
                    if (mapped != null) {
                        produtos.add(mapped);
                    }
                }
            }
            return produtos;
        } catch (IOException e) {
            Log.e(TAG, "Fikon product lookup request failed", e);
            return null;
        }
    }

    public static List<br.com.acalfortaleza.fikonmais.models.ProdutoGemco> consultarProdutosPorFiltro(
            Context context, FikonFiltroBuscaRequest filtro) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            Log.e(TAG, "Fikon product search cannot run without an authenticated session");
            return null;
        }

        try {
            Response<FikonProdutoResponse> response = api(context)
                    .consultarProdutosPorFiltro(filtro, authorization)
                    .execute();
            FikonProdutoResponse body = response.body();
            if (!response.isSuccessful() || body == null || !body.isSucesso()
                    || body.getDados() == null) {
                Log.e(TAG, "Fikon product search failed (HTTP " + response.code() + ")");
                return null;
            }

            List<br.com.acalfortaleza.fikonmais.models.ProdutoGemco> produtos = new ArrayList<>();
            for (FikonProduto produto : body.getDados()) {
                if (produto != null) {
                    ProdutoGemco mapped = mapProduto(context, produto);
                    if (mapped != null) {
                        produtos.add(mapped);
                    }
                }
            }
            return produtos;
        } catch (IOException e) {
            Log.e(TAG, "Fikon product search request failed", e);
            return null;
        }
    }

    public static List<Cliente> consultarClientes(Context context, String cpfCnpj) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            Log.e(TAG, "Fikon customer lookup cannot run without an authenticated session");
            return null;
        }

        try {
            Response<FikonClienteResponse> response = createApi(getBaseUrl(context))
                    .consultarCliente(cpfCnpj, authorization)
                    .execute();
            FikonClienteResponse body = response.body();
            if (!response.isSuccessful() || body == null || !body.isSucesso()
                    || body.getDados() == null) {
                Log.e(TAG, "Fikon customer lookup failed (HTTP " + response.code() + ")");
                return null;
            }

            List<Cliente> clientes = new ArrayList<>();
            for (FikonCliente fikonCliente : body.getDados()) {
                if (fikonCliente != null) {
                    Cliente cliente = mapCliente(fikonCliente);
                    // A tela de complemento lista os contatos só de pessoa jurídica.
                    if ("J".equals(cliente.getNatjur())) {
                        cliente.setContatos(consultarContatosDoCliente(context, fikonCliente));
                    }
                    clientes.add(cliente);
                }
            }
            return clientes;
        } catch (IOException e) {
            Log.e(TAG, "Fikon customer lookup request failed", e);
            return null;
        }
    }

    public static Enderecos consultarEndereco(Context context, String cep) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            Log.e(TAG, "Fikon CEP lookup cannot run without an authenticated session");
            return null;
        }

        String cepDigits = cep == null ? "" : cep.replaceAll("\\D", "");
        try {
            Response<FikonCepResponse> response = createApi(getBaseUrl(context))
                    .consultarCep(cepDigits, authorization)
                    .execute();
            FikonCepResponse body = response.body();
            if (!response.isSuccessful() || body == null || body.getRetorno() == null) {
                Log.e(TAG, "Fikon CEP lookup failed (HTTP " + response.code() + "): "
                        + errorMessage(response, null));
                return null;
            }

            FikonCepResponse.Endereco source = body.getRetorno();
            Enderecos endereco = new Enderecos();
            endereco.setCep(cepDigits);
            // logradouro já vem com o tipo (ex.: "RUA DAS FLORES")
            endereco.setEnder(source.getLogradouro());
            endereco.setBairro(source.getBairro());
            endereco.setCidade(source.getLocalidade());
            endereco.setUf(UfHelper.sigla(source.getUf()));
            return endereco;
        } catch (IOException e) {
            Log.e(TAG, "Fikon CEP lookup request failed", e);
            return null;
        }
    }

    public static String salvarCliente(Context context, Cliente cliente) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            Log.e(TAG, "Fikon customer save cannot run without an authenticated session");
            return "Sessão Fikon expirada. Faça login novamente.";
        }
        if (cliente == null) {
            throw new IllegalArgumentException("A customer is required");
        }

        try {
            ApiFikon api = api(context);
            FikonCliente payload = toFikonCliente(cliente);

            // POST cliente com CPF/CNPJ já cadastrado vira atualização e exige 'codigo';
            // pior, atualiza o cliente do 'codigo' enviado, mesmo que seja outro.
            // Por isso o app decide: tem código (ou o CPF já existe) -> PUT; senão -> POST.
            String codigo = payload.getCodigo();
            if (isBlank(codigo)) {
                codigo = buscarCodigoPorDocumento(api, authorization, payload.getCpfcnpj());
            }
            boolean atualizacao = !isBlank(codigo);

            Response<FikonClienteCadastroResponse> response;
            if (atualizacao) {
                payload.setCodigo(codigo);
                response = api.atualizarCliente(codigo, payload, authorization).execute();
            } else {
                response = api.inserirCliente(payload, authorization).execute();
            }
            FikonClienteCadastroResponse body = response.body();
            if (!response.isSuccessful() || body == null || !body.isSucesso()) {
                String mensagem = errorMessage(response, body == null ? null : body.getMensagem());
                Log.e(TAG, "Fikon customer save failed (HTTP " + response.code() + "): " + mensagem);
                if (!isBlank(mensagem)) {
                    return mensagem;
                }
                return atualizacao
                        ? "Não foi possível atualizar o cliente."
                        : "Não foi possível cadastrar o cliente.";
            }
            // As telas comparam com estes textos para limpar o formulário e seguir
            // para o orçamento; a Fikon responde "...com sucesso." com outro verbo.
            return atualizacao
                    ? "Cliente alterado com sucesso!"
                    : "Cliente cadastrado com sucesso!";
        } catch (IOException e) {
            Log.e(TAG, "Fikon customer save request failed", e);
            return "Falha de comunicação ao salvar o cliente.";
        }
    }

    /**
     * Procura o cliente pelo CPF/CNPJ e devolve o código dele, ou null se não existir.
     * Só aceita o resultado se o documento bater: GET cliente também procura por código.
     */
    private static String buscarCodigoPorDocumento(ApiFikon api, String authorization,
                                                   String cpfCnpj) throws IOException {
        String documento = onlyDigits(cpfCnpj);
        if (documento.length() != 11 && documento.length() != 14) {
            return null;
        }
        Response<FikonClienteResponse> response =
                api.consultarCliente(documento, authorization).execute();
        FikonClienteResponse body = response.body();
        if (!response.isSuccessful() || body == null || body.getDados() == null) {
            if (response.code() != 404) {
                Log.e(TAG, "Fikon customer lookup before save failed (HTTP "
                        + response.code() + ")");
            }
            return null;
        }
        for (FikonCliente existente : body.getDados()) {
            if (existente != null && documento.equals(onlyDigits(existente.getCpfcnpj()))
                    && !isBlank(existente.getCodigo())) {
                return existente.getCodigo();
            }
        }
        return null;
    }

    // ---------- Contatos ----------

    public static final String MENSAGEM_CONTATO_SO_INCLUSAO =
            "A Fikon ainda não permite alterar ou excluir contatos pelo app.";

    /** Código do cliente na Fikon; sem código, o CPF/CNPJ (a rota aceita os dois). */
    public static String codigoClienteFikon(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        FikonCliente metadata = cliente.getFikonMetadata();
        if (metadata != null && !isBlank(metadata.getCodigo())) {
            return metadata.getCodigo();
        }
        String documento = onlyDigits(cliente.getCgccpf());
        return documento.isEmpty() ? null : documento;
    }

    private static List<Contato> consultarContatosDoCliente(Context context, FikonCliente cliente) {
        List<Contato> contatos = new ArrayList<>();
        String codigo = !isBlank(cliente.getCodigo())
                ? cliente.getCodigo() : onlyDigits(cliente.getCpfcnpj());
        if (isBlank(codigo)) {
            return contatos;
        }
        List<FikonContato> fikonContatos = consultarContatos(context, codigo);
        if (fikonContatos == null) {
            return contatos;
        }
        for (FikonContato fikonContato : fikonContatos) {
            if (fikonContato != null) {
                Contato contato = toContato(fikonContato);
                contato.setCodigoClienteFikon(codigo);
                contatos.add(contato);
            }
        }
        return contatos;
    }

    /** Cadastra um contato do formulário antigo no cliente informado em CodigoClienteFikon. */
    public static String cadastrarContato(Context context, Contato contato) {
        if (contato == null) {
            return "Informe o contato.";
        }
        List<FikonContato> contatos = new ArrayList<>();
        contatos.add(fromContato(contato));
        return cadastrarContatos(context, contato.getCodigoClienteFikon(), contatos);
    }

    /**
     * Converte para o modelo das telas. Os campos de texto nunca ficam null porque
     * o formulário chama equals()/length() direto neles.
     */
    private static Contato toContato(FikonContato source) {
        Contato contato = new Contato();
        contato.setNomeContato(nullToEmpty(source.getNome()));
        contato.setEmail(nullToEmpty(source.getEmail()));
        contato.setCgccpf("");
        contato.setDtnascimento("");

        String[] celular = splitPhone(source.getCelular());
        contato.setDddCelular(celular[0]);
        contato.setCelular(celular[1]);
        contato.setDddFone2("");
        contato.setFone2("");

        String[] fone = splitPhone(source.getFone());
        contato.setDddFone1(fone[0]);
        contato.setFone1(fone[1]);
        contato.setDdd("");
        contato.setFone("");

        br.com.acalfortaleza.fikonmais.models.Setor setor =
                new br.com.acalfortaleza.fikonmais.models.Setor();
        setor.setDescricao(nullToEmpty(source.getSetor()));
        contato.setSetor(setor);
        br.com.acalfortaleza.fikonmais.models.Cargo cargo =
                new br.com.acalfortaleza.fikonmais.models.Cargo();
        cargo.setDescricao(nullToEmpty(source.getCargo()));
        contato.setCargo(cargo);
        return contato;
    }

    /**
     * No formulário, o campo obrigatório "celular" (edt_wpp) grava em DddCelular/Celular
     * e o telefone fixo em DddFone1/Fone1; o celular opcional (DddFone2/Fone2) não existe
     * na Fikon e só é usado se o obrigatório vier vazio.
     */
    private static FikonContato fromContato(Contato source) {
        FikonContato contato = new FikonContato();
        contato.setNome(trimToNull(source.getNomeContato()));
        contato.setEmail(trimToNull(source.getEmail()));
        String celular = formatPhone(source.getDddCelular(), source.getCelular());
        if (celular == null) {
            celular = formatPhone(source.getDddFone2(), source.getFone2());
        }
        contato.setCelular(celular);
        contato.setFone(formatPhone(source.getDddFone1(), source.getFone1()));
        if (source.getSetor() != null) {
            contato.setSetor(trimToNull(source.getSetor().getDescricao()));
        }
        if (source.getCargo() != null) {
            contato.setCargo(trimToNull(source.getCargo().getDescricao()));
        }
        return contato;
    }

    /** "(62) 97777-0000" -> {"62", "977770000"}. */
    private static String[] splitPhone(String phone) {
        String digits = onlyDigits(phone);
        if (digits.startsWith("55") && digits.length() > 11) {
            digits = digits.substring(2);
        }
        if (digits.length() >= 10) {
            return new String[]{digits.substring(0, 2), digits.substring(2)};
        }
        return new String[]{"", digits};
    }

    /** {"62", "977770000"} -> "(62) 97777-0000", no formato do exemplo da documentação. */
    private static String formatPhone(String ddd, String number) {
        String digits = onlyDigits(ddd) + onlyDigits(number);
        if (digits.length() == 11) {
            return "(" + digits.substring(0, 2) + ") " + digits.substring(2, 7)
                    + "-" + digits.substring(7);
        }
        if (digits.length() == 10) {
            return "(" + digits.substring(0, 2) + ") " + digits.substring(2, 6)
                    + "-" + digits.substring(6);
        }
        return digits.isEmpty() ? null : digits;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    /**
     * Lista as pessoas de contato de um cliente.
     *
     * @param consulta código, chave, nome ou CPF/CNPJ do cliente
     * @return lista (vazia se o cliente não tem contatos) ou null em caso de falha
     */
    public static List<FikonContato> consultarContatos(Context context, String consulta) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            Log.e(TAG, "Fikon contact lookup cannot run without an authenticated session");
            return null;
        }
        String documento = onlyDigits(consulta);
        String tipo = documento.length() == 11 || documento.length() == 14 ? "CPFCNPJ" : null;
        try {
            Response<FikonContatoResponse> response = api(context)
                    .consultarContatos(tipo == null ? consulta : documento, tipo, authorization)
                    .execute();
            FikonContatoResponse body = response.body();
            if (!response.isSuccessful() || body == null || !body.isSucesso()) {
                Log.e(TAG, "Fikon contact lookup failed (HTTP " + response.code() + "): "
                        + errorMessage(response, body == null ? null : body.getMensagem()));
                return null;
            }
            return body.getDados() == null ? new ArrayList<FikonContato>() : body.getDados();
        } catch (IOException e) {
            Log.e(TAG, "Fikon contact lookup request failed", e);
            return null;
        }
    }

    /**
     * Cadastra pessoas de contato de um cliente. A Fikon só inclui: não há alteração
     * nem exclusão de contato na API. O primeiro contato inválido recusa a lista toda.
     *
     * @param codigoCliente código, chave, nome ou CPF/CNPJ do cliente
     * @return mensagem para o usuário
     */
    public static String cadastrarContatos(Context context, String codigoCliente,
                                           List<FikonContato> contatos) {
        String authorization = getAuthorization(context);
        if (authorization == null) {
            return "Sessão Fikon expirada. Faça login novamente.";
        }
        if (isBlank(codigoCliente)) {
            return "Informe o cliente do contato.";
        }
        if (contatos == null || contatos.isEmpty()) {
            return "Informe ao menos um contato.";
        }
        for (FikonContato contato : contatos) {
            if (contato == null || isBlank(contato.getCelular())) {
                return "O celular do contato é obrigatório.";
            }
        }
        try {
            Response<FikonContatoCadastroResponse> response = api(context)
                    .cadastrarContatos(new FikonContatoRequest(codigoCliente, contatos),
                            authorization)
                    .execute();
            FikonContatoCadastroResponse body = response.body();
            if (!response.isSuccessful() || body == null || !body.isSucesso()) {
                String mensagem = errorMessage(response, body == null ? null : body.getMensagem());
                Log.e(TAG, "Fikon contact save failed (HTTP " + response.code() + "): " + mensagem);
                return isBlank(mensagem) ? "Não foi possível cadastrar o contato." : mensagem;
            }
            return "Contato cadastrado com sucesso!";
        } catch (IOException e) {
            Log.e(TAG, "Fikon contact save request failed", e);
            return "Falha de comunicação ao salvar o contato.";
        }
    }

    /**
     * Mensagem de erro de uma resposta Fikon. Nos 400/404 o corpo vem no errorBody,
     * em formato que varia ({ mensagem }, { message }, { erros: [...] } ou texto puro).
     */
    static String errorMessage(Response<?> response, String fallback) {
        if (!isBlank(fallback)) {
            return fallback;
        }
        if (response == null || response.errorBody() == null) {
            return null;
        }
        String raw;
        try {
            raw = response.errorBody().string();
        } catch (IOException e) {
            return null;
        }
        if (isBlank(raw)) {
            return null;
        }
        try {
            com.google.gson.JsonElement element = new com.google.gson.JsonParser().parse(raw);
            if (element.isJsonObject()) {
                com.google.gson.JsonObject json = element.getAsJsonObject();
                for (String key : new String[]{"mensagem", "message", "erro", "error"}) {
                    if (json.has(key) && json.get(key).isJsonPrimitive()) {
                        return json.get(key).getAsString();
                    }
                }
                if (json.has("erros") && json.get("erros").isJsonArray()
                        && json.getAsJsonArray("erros").size() > 0) {
                    return json.getAsJsonArray("erros").get(0).getAsString();
                }
                return null;
            }
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            }
        } catch (RuntimeException ignored) {
            // não é JSON: usa o texto como veio
        }
        return raw.length() > 300 ? raw.substring(0, 300) : raw;
    }

    static String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private static Cliente mapCliente(FikonCliente source) {
        Cliente cliente = new Cliente();
        cliente.setFikonMetadata(source);
        // As telas tratam Codcli <= 0 como "cliente não cadastrado". Código Fikon com letras
        // (ex.: C1043) não vira número, então usa a chave do cliente. O código original
        // continua em getFikonMetadata().getCodigo() para as chamadas à API.
        String codigoNumerico = onlyDigits(source.getCodigo());
        int codcli = source.getCodcli() > 0 ? source.getCodcli()
                : codigoNumerico.equals(source.getCodigo()) && codigoNumerico.length() <= 9
                        && !codigoNumerico.isEmpty()
                        ? Integer.parseInt(codigoNumerico) : source.getChave();
        cliente.setCodcli(codcli);
        cliente.setSitcred("");
        cliente.setDigcli(source.getDigcli());
        cliente.setCgccpf(source.getCpfcnpj());
        cliente.setNomcli(source.getNome());
        // A Fikon não manda natureza jurídica; as telas fazem getNatjur().equals(...).
        String natjur = source.getNatjur();
        if (isBlank(natjur)) {
            natjur = onlyDigits(source.getCpfcnpj()).length() == 14 ? "J" : "F";
        }
        cliente.setNatjur(natjur);
        cliente.setContatos(new ArrayList<Contato>());
        cliente.setTpinsc(source.getTpinsc());
        cliente.setInscr(source.getInscr());
        cliente.setFantasia(source.getFantasia());
        cliente.setEmail(source.getEmail());
        cliente.setDdd(source.getDdd());
        cliente.setFone(source.getFone());
        cliente.setDddFone1(source.getDddFone1());
        cliente.setFone1(source.getFone1());
        cliente.setDddFone2(source.getDddFone2());
        cliente.setFone2(source.getFone2());
        cliente.setDddCelular(source.getDddCelular());
        cliente.setCelular(source.getCelular());
        cliente.setObs(source.getObservacao());
        cliente.setDtnasc(source.getDtnasc());
        cliente.setSexo(source.getSexo());
        cliente.setFilialCadastro(source.getFilialCadastro());
        cliente.setConsultor(source.getConsultor());
        cliente.setTipo(firstNonEmpty(source.getTipo(), "CADASTRADO"));
        if (!isBlank(source.getSegmento())) {
            Segmento segmento = new Segmento();
            try {
                segmento.setId(Integer.parseInt(source.getSegmento()));
            } catch (NumberFormatException e) {
                segmento.setDescricao(source.getSegmento());
            }
            cliente.setSegmento(segmento);
        }
        List<Enderecos> addresses = new ArrayList<>();
        Enderecos mainAddress = mapEndereco(source.getEnderecoPrincipal());
        if (mainAddress != null) {
            addresses.add(mainAddress);
        }
        if (source.getEnderecosAdicionais() != null) {
            for (FikonEndereco address : source.getEnderecosAdicionais()) {
                Enderecos mappedAddress = mapEndereco(address);
                if (mappedAddress != null) {
                    addresses.add(mappedAddress);
                }
            }
        }
        if (!addresses.isEmpty()) {
            cliente.setEnderecos(addresses);
        }
        return cliente;
    }

    private static Enderecos mapEndereco(FikonEndereco source) {
        if (source == null) {
            return null;
        }
        Enderecos endereco = new Enderecos();
        endereco.setFikonMetadata(source);
        endereco.setCep(source.getCep());
        endereco.setEnder(source.getLogradouro());
        endereco.setNumero(source.getNumero());
        endereco.setBairro(source.getBairro());
        endereco.setCidade(source.getCidade());
        endereco.setUf(source.getUf());
        endereco.setComplemento(source.getComplemento());
        endereco.setReferencia(source.getPontoRefer());
        return endereco;
    }

    private static FikonCliente toFikonCliente(Cliente source) {
        FikonCliente target = source.getFikonMetadata();
        if (target == null) {
            target = new FikonCliente();
        }
        target.setCpfcnpj(source.getCgccpf());
        target.setNome(source.getNomcli());
        target.setNatjur(source.getNatjur());
        target.setTpinsc(source.getTpinsc());
        target.setInscr(source.getInscr());
        target.setFantasia(source.getFantasia());
        target.setEmail(source.getEmail());
        target.setFone(firstNonEmpty(source.getFone(), source.getFone1()));
        target.setCelular(firstNonEmpty(source.getCelular(), source.getFone2()));
        target.setObservacao(source.getObs());
        target.setDdd(source.getDdd());
        target.setDddFone1(source.getDddFone1());
        target.setFone1(source.getFone1());
        target.setDddFone2(source.getDddFone2());
        target.setFone2(source.getFone2());
        target.setDddCelular(source.getDddCelular());
        target.setDtnasc(source.getDtnasc());
        target.setSexo(source.getSexo());
        target.setFilialCadastro(source.getFilialCadastro());
        target.setConsultor(source.getConsultor());
        target.setTipo(source.getTipo());
        target.setVendedor(source.getConsultor());
        if (source.getSegmento() != null && source.getSegmento().getId() > 0) {
            target.setSegmento(String.valueOf(source.getSegmento().getId()));
        }

        List<Enderecos> addresses = source.getEnderecos();
        if (addresses != null && !addresses.isEmpty()) {
            target.setEnderecoPrincipal(toFikonEndereco(
                    addresses.get(0), target.getEnderecoPrincipal()));
            List<FikonEndereco> additionalAddresses = new ArrayList<>();
            for (int i = 1; i < addresses.size(); i++) {
                FikonEndereco previous = target.getEnderecosAdicionais() != null
                        && target.getEnderecosAdicionais().size() >= i
                        ? target.getEnderecosAdicionais().get(i - 1) : null;
                additionalAddresses.add(toFikonEndereco(addresses.get(i), previous));
            }
            target.setEnderecosAdicionais(additionalAddresses);
        }
        return target;
    }

    private static FikonEndereco toFikonEndereco(Enderecos source, FikonEndereco previous) {
        FikonEndereco target = source.getFikonMetadata() == null
                ? previous : source.getFikonMetadata();
        if (target == null) {
            target = new FikonEndereco();
        }
        target.setCep(source.getCep());
        target.setLogradouro(source.getEnder());
        target.setNumero(source.getNumero());
        target.setBairro(source.getBairro());
        target.setCidade(source.getCidade());
        target.setUf(source.getUf());
        target.setComplemento(source.getComplemento());
        target.setPontoRefer(source.getReferencia());
        return target;
    }

    private static ProdutoGemco mapProduto(Context context, FikonProduto source) {
        int codigo = parseInt(source.codigo);
        if (codigo <= 0) {
            Log.e(TAG, "Skipping Fikon product without a valid numeric code: " + source.codigo);
            return null;
        }

        ProdutoGemco produto = new ProdutoGemco();
        produto.Codigo = codigo;
        produto.Descricao = firstNonEmpty(source.Descricao, source.nome);
        produto.Preco = source.precoVigente > 0 ? source.precoVigente : source.Preco;
        produto.Fornecedor = source.Fornecedor;
        produto.Linha = firstNonEmpty(source.Linha, source.classeNome);
        produto.Situacao = firstNonEmpty(source.Situacao, source.inativo);
        produto.Unidade = source.Unidade;
        produto.Comprador = source.Comprador;
        produto.Qtemb = source.Qtemb;
        produto.Encargos = source.custosETributos != null
                ? source.custosETributos.encargos : source.Encargos;
        produto.Icms = source.custosETributos != null
                ? source.custosETributos.icms : source.Icms;
        produto.IcmsRed = source.IcmsRed;
        produto.Custo = source.custosETributos != null
                ? source.custosETributos.custo : source.Custo;
        produto.Ncm = firstNonEmpty(source.Ncm, source.ncm);
        produto.Peso = source.Peso;
        produto.Imagem = firstNonEmpty(source.Imagem, "");
        produto.Caracteristicas = source.Caracteristicas;
        produto.Selecao = source.Selecao;
        produto.BloqueioDesc = source.BloqueioDesc;
        produto.Incentivado = source.Incentivado;
        produto.Lotevencto = source.Lotevencto;
        produto.Skuid = firstNonEmpty(source.Skuid, findSku(source));
        produto.PrecoConc = source.PrecoConc;
        produto.AtualizacaoPreco = source.AtualizacaoPreco;
        produto.PrecoPor = source.PrecoPor;
        produto.PercentualComissao = source.PercentualComissao;
        produto.PercIncentivo = source.PercIncentivo;
        produto.CodigoBarra = source.CodigoBarra == null
                ? new ArrayList<CodigoBarra>() : new ArrayList<>(source.CodigoBarra);

        mapImages(context, source, produto);
        mapStock(source, produto);
        ensureBarcode(source, produto);
        if (source.Upsell != null) {
            produto.Upsell = new ArrayList<>();
            for (FikonProduto upsell : source.Upsell) {
                if (upsell != null) {
                    ProdutoGemco mappedUpsell = mapProduto(context, upsell);
                    if (mappedUpsell != null) {
                        produto.Upsell.add(mappedUpsell);
                    }
                }
            }
        }
        return produto;
    }

    private static void mapImages(Context context, FikonProduto source, ProdutoGemco target) {
        List<ProdutoVtex.Images> images = new ArrayList<>();
        if (source.imagens != null) {
            for (FikonProduto.ImagemProduto image : source.imagens) {
                if (image == null || isBlank(image.url)) {
                    continue;
                }
                ProdutoVtex.Images mapped = new ProdutoVtex.Images();
                mapped.ImageUrl = resolveImageUrl(context, image.url);
                mapped.Principal = image.principal;
                images.add(mapped);
            }
        }
        if (source.Imagens != null) {
            for (ProdutoVtex.Images image : source.Imagens) {
                if (image != null && !isBlank(image.ImageUrl)
                        && !containsImage(images, image.ImageUrl)) {
                    images.add(image);
                }
            }
        }
        target.Imagens = images;
        if (!images.isEmpty()) {
            for (ProdutoVtex.Images image : images) {
                if (image.Principal) {
                    target.Imagem = image.ImageUrl;
                    return;
                }
            }
            if (isBlank(target.Imagem)) {
                target.Imagem = images.get(0).ImageUrl;
            }
        }
    }

    private static void mapStock(FikonProduto source, ProdutoGemco target) {
        if (source.estoquesPorLoja == null || source.estoquesPorLoja.isEmpty()) {
            return;
        }
        List<Estoque> stocks = new ArrayList<>();
        for (FikonProduto.EstoquePorLoja store : source.estoquesPorLoja) {
            if (store == null || store.itensEstoque == null || store.itensEstoque.isEmpty()) {
                continue;
            }
            Estoque stock = new Estoque();
            stock.FikonEstabelecimentoChave = store.estabelecimentoChave;
            stock.EstoqueFikon = true;
            stock.Filial = store.nomeEstabelecimento;
            stock.Disponivel = "0";
            stock.Fisico = "0";
            stock.Reserva = "0";
            stock.Transito = "0";
            stock.Futuro = "0";
            stock.Mostruario = "0";
            stock.Previsao = firstNonEmpty(store.Previsao, "");
            stock.Lotes = new ArrayList<>();
            double total = 0d;
            for (FikonProduto.ItemEstoque item : store.itensEstoque) {
                if (item == null) {
                    continue;
                }
                total += item.saldoTotal;
                if (item.lotes == null) {
                    continue;
                }
                for (FikonProduto.Lote sourceLote : item.lotes) {
                    if (sourceLote == null) {
                        continue;
                    }
                    Lotes lote = new Lotes();
                    lote.Lote = firstNonEmpty(sourceLote.codigoLote, sourceLote.nomeLote);
                    lote.Disponivel = String.valueOf(sourceLote.quantidade);
                    lote.Qtde = lote.Disponivel;
                    lote.Reserva = "0";
                    lote.Bloqueado = "0";
                    stock.Lotes.add(lote);
                }
            }
            stock.Disponivel = String.valueOf(total);
            stock.Fisico = stock.Disponivel;
            if (stock.Lotes.isEmpty()) {
                stock.Lotes.add(new Lotes());
                stock.Lotes.get(0).Lote = "";
                stock.Lotes.get(0).Disponivel = "0";
                stock.Lotes.get(0).Qtde = "0";
                stock.Lotes.get(0).Reserva = "0";
                stock.Lotes.get(0).Bloqueado = "0";
            }
            stocks.add(stock);
        }

        if (stocks.isEmpty()) {
            return;
        }
        if (target.CodigoBarra == null) {
            target.CodigoBarra = new ArrayList<>();
        }
        CodigoBarra barcode;
        if (target.CodigoBarra.isEmpty()) {
            barcode = new CodigoBarra();
            barcode.CodBarra = findBarcode(source);
            target.CodigoBarra.add(barcode);
        } else {
            barcode = target.CodigoBarra.get(0);
            if (barcode == null) {
                barcode = new CodigoBarra();
                target.CodigoBarra.set(0, barcode);
            }
        }
        barcode.Estoque = stocks;
    }

    private static void ensureBarcode(FikonProduto source, ProdutoGemco target) {
        if (target.CodigoBarra == null) {
            target.CodigoBarra = new ArrayList<>();
        }
        if (target.CodigoBarra.isEmpty()) {
            CodigoBarra barcode = new CodigoBarra();
            barcode.CodBarra = findBarcode(source);
            target.CodigoBarra.add(barcode);
        } else if (target.CodigoBarra.get(0) == null) {
            CodigoBarra barcode = new CodigoBarra();
            barcode.CodBarra = findBarcode(source);
            target.CodigoBarra.set(0, barcode);
        } else if (isBlank(target.CodigoBarra.get(0).CodBarra)) {
            target.CodigoBarra.get(0).CodBarra = findBarcode(source);
        }
    }

    private static String findBarcode(FikonProduto source) {
        if (source.codigos != null) {
            for (FikonProduto.CodigoProduto code : source.codigos) {
                if (code != null && !isBlank(code.codigo) && isBarcodeType(code.tipo)) {
                    return code.codigo;
                }
            }
        }
        return source.codigo;
    }

    /** A Fikon manda o tipo com variante, ex.: "Gtin 13", "Gtin 14", "EAN 13". */
    private static boolean isBarcodeType(String tipo) {
        if (isBlank(tipo)) {
            return false;
        }
        String normalized = tipo.trim().toUpperCase(java.util.Locale.ROOT);
        return normalized.startsWith("GTIN")
                || normalized.startsWith("EAN")
                || normalized.contains("BARCODE")
                || normalized.contains("BARRAS");
    }

    private static String findSku(FikonProduto source) {
        if (source.codigos != null) {
            for (FikonProduto.CodigoProduto code : source.codigos) {
                if (code != null && !isBlank(code.codigo)
                        && ("SKU".equalsIgnoreCase(code.tipo)
                        || "SKUID".equalsIgnoreCase(code.tipo)
                        || "VTEX".equalsIgnoreCase(code.tipo))) {
                    return code.codigo;
                }
            }
        }
        return "";
    }

    private static String resolveImageUrl(Context context, String url) {
        if (url.regionMatches(true, 0, "http://", 0, 7)
                || url.regionMatches(true, 0, "https://", 0, 8)) {
            return url;
        }
        String baseUrl = getBaseUrl(context);
        return baseUrl + (url.startsWith("/") ? url.substring(1) : url);
    }

    private static boolean containsImage(List<ProdutoVtex.Images> images, String url) {
        for (ProdutoVtex.Images image : images) {
            if (url.equals(image.ImageUrl)) {
                return true;
            }
        }
        return false;
    }

    private static int parseInt(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Fikon product code is not numeric: " + value, e);
            return 0;
        }
    }

    private static int parseInt(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            return 0;
        }
        return (int) value;
    }

    private static ApiFikon createApi(String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(normalizeBaseUrl(baseUrl))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiFikon.class);
    }

    private static String getAuthorization(Context context) {
        String token = preferences(context).getString(KEY_TOKEN, null);
        if (isBlank(token)) {
            return null;
        }
        return token.regionMatches(true, 0, "Bearer ", 0, 7)
                ? token
                : "Bearer " + token;
    }

    private static String getBaseUrl(Context context) {
        return normalizeBaseUrl(
                preferences(context).getString(KEY_BASE_URL, DEFAULT_BASE_URL));
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (isBlank(baseUrl)) {
            throw new IllegalArgumentException("Fikon base URL must not be empty");
        }
        String normalized = baseUrl.trim();
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String firstNonEmpty(String preferred, String fallback) {
        return isBlank(preferred) ? fallback : preferred;
    }
}

package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PixRequest;
import br.com.acalfortaleza.fikonmais.models.PixResponse;
import br.com.acalfortaleza.fikonmais.service.ApiFixPay;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import br.com.acalfortaleza.fikonmais.model.AtualizarStatusPixRequest;
import br.com.acalfortaleza.fikonmais.model.AtualizarStatusPixResponse;
import br.com.acalfortaleza.fikonmais.helper.LogHelper;
import br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento;
import retrofit2.Response;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class FixPayHelper {

    private Retrofit retrofit;
    private OkHttpClient client;
    private String tokenPixMaisAtual; // Armazena o token PIX mais atual extraído da consulta


    public  FixPayHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiFixPay.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    /**
     * Extrai apenas o token PIX mais atual (último) do tipo 'P' da resposta da consulta inicial
     */
    public List<String> extrairTokensPixTipoP(String responseBody) {
        List<String> tokensP = new ArrayList<>();
        List<String> todosTokensP = new ArrayList<>(); // Lista temporária para coletar todos os tokens
        
        try {
            LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_INICIO", "[EXTRAIR_TOKENS_P] Iniciando extração do token mais atual tipo P da resposta");
            
            if (responseBody == null || responseBody.trim().isEmpty()) {
                LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_VAZIO", "[EXTRAIR_TOKENS_P] Resposta vazia ou nula");
                return tokensP;
            }
            
            // Verificar se é JSON ou texto simples
            String trimmedResponse = responseBody.trim();
            if (trimmedResponse.startsWith("[") || trimmedResponse.startsWith("{")) {
                // Processar como JSON (código original)
                LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_JSON", "[EXTRAIR_TOKENS_P] Detectado formato JSON, processando como array/objeto");
                
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(responseBody);
                
                if (jsonElement.isJsonArray()) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_ARRAY", "[EXTRAIR_TOKENS_P] Processando array com " + jsonArray.size() + " elementos");
                    
                    for (JsonElement element : jsonArray) {
                        if (element.isJsonObject()) {
                            JsonObject obj = element.getAsJsonObject();
                            
                            // Verificar se tem os campos necessários
                            if (obj.has("tipo") && obj.has("token")) {
                                String tipo = obj.get("tipo").getAsString();
                                String token = obj.get("token").getAsString();

                                if (br.com.acalfortaleza.fikonmais.constants.PaymentTypeCodes.PIX.equals(tipo)) {
                                    todosTokensP.add(token);
                                    LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_ENCONTRADO", "[EXTRAIR_TOKENS_P] Token tipo P encontrado: " + token.substring(0, Math.min(10, token.length())) + "...");
                                }
                            }
                        }
                    }
                }
            } else {
                // Processar como texto simples
                LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_TEXTO", "[EXTRAIR_TOKENS_P] Detectado formato de texto simples, processando com regex");

                // Usar regex para extrair tokens do tipo P
                // Padrão: "- Tipo: P - Token: [valor_do_token]"
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("- Tipo: " + br.com.acalfortaleza.fikonmais.constants.PaymentTypeCodes.PIX + " - Token: ([A-Za-z0-9]+)");
                java.util.regex.Matcher matcher = pattern.matcher(responseBody);
                
                int tokenCount = 0;
                while (matcher.find()) {
                    String token = matcher.group(1);
                    if (token != null && !token.trim().isEmpty()) {
                        todosTokensP.add(token.trim());
                        tokenCount++;
                        LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_ENCONTRADO", "[EXTRAIR_TOKENS_P] Token tipo P encontrado (" + tokenCount + "): " + token.substring(0, Math.min(10, token.length())) + "...");
                    }
                }
                
                LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_REGEX_RESULTADO", "[EXTRAIR_TOKENS_P] Regex processou " + tokenCount + " matches para tokens tipo P");
            }
            
            // Retornar apenas o token mais atual (último da lista)
            if (!todosTokensP.isEmpty()) {
                String tokenMaisAtual = todosTokensP.get(todosTokensP.size() - 1);
                tokensP.add(tokenMaisAtual);
                
                // Armazenar o token mais atual na variável de instância
                this.tokenPixMaisAtual = tokenMaisAtual;
                LogHelper.writePixLog(null, "TOKEN_ARMAZENADO", "[TOKEN_STORAGE] Token PIX mais atual armazenado na instância: " + tokenMaisAtual.substring(0, Math.min(10, tokenMaisAtual.length())) + "...");
                
                LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_MAIS_ATUAL", "[EXTRAIR_TOKENS_P] Token mais atual selecionado: " + tokenMaisAtual.substring(0, Math.min(10, tokenMaisAtual.length())) + "... (" + todosTokensP.size() + " tokens encontrados no total)");
            } else {
                // Limpar o token armazenado se nenhum token foi encontrado
                this.tokenPixMaisAtual = null;
                LogHelper.writePixLog(null, "TOKEN_LIMPO", "[TOKEN_STORAGE] Token PIX limpo - nenhum token tipo P encontrado");
            }
            
            LogHelper.writePixLog(null, "EXTRAIR_TOKENS_P_RESULTADO", "[EXTRAIR_TOKENS_P] Retornando apenas o token mais atual. Total encontrados: " + todosTokensP.size() + ", Retornados: " + tokensP.size());
            
        } catch (Exception e) {
            LogHelper.writePixError(null, "[EXTRAIR_TOKENS_P] Erro ao extrair tokens tipo P: " + e.getMessage());
            Log.e("FixPayHelper", "Erro ao extrair tokens tipo P", e);
        }
        
        return tokensP;
    }
    
    /**
     * Consulta o status de um token PIX individual no endpoint FixPay
     */
    public void consultarStatusTokenPixIndividual(Context context, String token, String tokenFilial, int codfil) {
        LogHelper.writePixLog(context, "CONSULTA_TOKEN_INDIVIDUAL_INICIO", "[CONSULTA_TOKEN_INDIVIDUAL] Iniciando consulta para token: " + token.substring(0, Math.min(10, token.length())) + "...");
        
        try {
            // Criar cliente OkHttp para requisição ao FixPay
            OkHttpClient clientFixPay = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Construir URL do endpoint FixPay
            String url = "https://pix.fixpay.com.br:3467/v1/consult_pix/" + token;
            LogHelper.writePixLog(context, "CONSULTA_TOKEN_INDIVIDUAL_URL", "[CONSULTA_TOKEN_INDIVIDUAL] URL: " + url);
            
            // Criar requisição HTTP GET com autorização
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + tokenFilial)
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();
            
            LogHelper.writePixLog(context, "CONSULTA_TOKEN_INDIVIDUAL_AUTH", "[CONSULTA_TOKEN_INDIVIDUAL] Usando token de autorização: " + tokenFilial.substring(0, Math.min(10, tokenFilial.length())) + "...");
            
            // Executar requisição de forma assíncrona
            clientFixPay.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    LogHelper.writePixLog(context, "CONSULTA_TOKEN_INDIVIDUAL_RESPOSTA", "[CONSULTA_TOKEN_INDIVIDUAL] Status: " + response.code() + " - " + response.message());
                    
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        LogHelper.writePixLog(context, "CONSULTA_TOKEN_INDIVIDUAL_SUCESSO", "[CONSULTA_TOKEN_INDIVIDUAL] Consulta realizada com sucesso para token: " + token.substring(0, Math.min(10, token.length())) + "...");
                        LogHelper.writePixResponse(context, "[CONSULTA_TOKEN_INDIVIDUAL] Resposta: " + responseBody);
                        
                        Log.d("FixPayHelper", "=== CONSULTA TOKEN INDIVIDUAL SUCESSO ===");
                        Log.d("FixPayHelper", "Token: " + token.substring(0, Math.min(10, token.length())) + "...");
                        Log.d("FixPayHelper", "Status: " + response.code());
                        Log.d("FixPayHelper", "Resposta: " + responseBody);
                        
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sem corpo de erro";
                        LogHelper.writePixError(context, "[CONSULTA_TOKEN_INDIVIDUAL] Erro na consulta. Status: " + response.code() + ", Erro: " + errorBody);
                        
                        Log.e("FixPayHelper", "=== ERRO CONSULTA TOKEN INDIVIDUAL ===");
                        Log.e("FixPayHelper", "Token: " + token.substring(0, Math.min(10, token.length())) + "...");
                        Log.e("FixPayHelper", "Status: " + response.code());
                        Log.e("FixPayHelper", "Erro: " + errorBody);
                    }
                    
                    response.close();
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LogHelper.writePixError(context, "[CONSULTA_TOKEN_INDIVIDUAL] Falha na conexão: " + e.getMessage());
                    Log.e("FixPayHelper", "=== FALHA CONSULTA TOKEN INDIVIDUAL ===");
                    Log.e("FixPayHelper", "Token: " + token.substring(0, Math.min(10, token.length())) + "...");
                    Log.e("FixPayHelper", "Erro: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            LogHelper.writePixError(context, "[CONSULTA_TOKEN_INDIVIDUAL] Exceção: " + e.getMessage());
            Log.e("FixPayHelper", "Erro na consulta individual do token PIX", e);
        }
    }
    
    /**
     * Processa a consulta de status de todos os tokens PIX tipo 'P' encontrados
     */
    public void processarConsultaTokensPixExistentes(Context context, String responseBodyConsultaInicial, int codfil) {
        LogHelper.writePixLog(context, "PROCESSAR_CONSULTA_TOKENS_INICIO", "[PROCESSAR_CONSULTA_TOKENS] Iniciando processamento para filial: " + codfil);
        
        try {
            // Extrair tokens tipo 'P' da resposta inicial
            List<String> tokensP = extrairTokensPixTipoP(responseBodyConsultaInicial);
            
            if (tokensP.isEmpty()) {
                LogHelper.writePixLog(context, "PROCESSAR_CONSULTA_TOKENS_VAZIO", "[PROCESSAR_CONSULTA_TOKENS] Nenhum token tipo P encontrado para consulta");
                return;
            }
            
            LogHelper.writePixLog(context, "PROCESSAR_CONSULTA_TOKENS_ENCONTRADOS", "[PROCESSAR_CONSULTA_TOKENS] " + tokensP.size() + " tokens tipo P encontrados. Obtendo token de autorização...");
            
            // Obter token de autorização da filial
            String tokenFilial = ConsultarTokenFilial(context, codfil);
            
            if (tokenFilial == null || tokenFilial.isEmpty()) {
                LogHelper.writePixError(context, "[PROCESSAR_CONSULTA_TOKENS] Token da filial não encontrado. Não é possível consultar tokens individuais.");
                return;
            }
            
            LogHelper.writePixLog(context, "PROCESSAR_CONSULTA_TOKENS_TOKEN_OK", "[PROCESSAR_CONSULTA_TOKENS] Token de autorização obtido. Iniciando consultas individuais...");
            
            // Consultar cada token individualmente
            for (int i = 0; i < tokensP.size(); i++) {
                String token = tokensP.get(i);
                LogHelper.writePixLog(context, "PROCESSAR_CONSULTA_TOKENS_INDIVIDUAL", "[PROCESSAR_CONSULTA_TOKENS] Consultando token " + (i + 1) + "/" + tokensP.size() + ": " + token.substring(0, Math.min(10, token.length())) + "...");
                
                // Fazer consulta individual (assíncrona)
                consultarStatusTokenPixIndividual(context, token, tokenFilial, codfil);
                
                // Pequeno delay entre consultas para evitar sobrecarga
                try {
                    Thread.sleep(500); // 500ms entre consultas
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            LogHelper.writePixLog(context, "PROCESSAR_CONSULTA_TOKENS_CONCLUIDO", "[PROCESSAR_CONSULTA_TOKENS] Todas as consultas individuais foram iniciadas");
            
        } catch (Exception e) {
            LogHelper.writePixError(context, "[PROCESSAR_CONSULTA_TOKENS] Erro no processamento: " + e.getMessage());
            Log.e("FixPayHelper", "Erro ao processar consulta de tokens PIX existentes", e);
        }
    }


    public String ConsultarTokenFilial(Context context, int codfil) throws IOException {
        LogHelper.writePixLog(context, "TOKEN_CONSULTA_INICIO", "[TOKEN] Iniciando consulta do token para filial: " + codfil);
        
        try {
            OkHttpClient clientGemco = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofitGemco = new Retrofit.Builder()
                    .baseUrl(ApiGemco.BASE_URL)
                    .client(clientGemco)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiGemco apiGemco = retrofitGemco.create(ApiGemco.class);
            Call<String> call = apiGemco.consultarTokenFilial(codfil, "Bearer " + ApiGemco.token);
            
            LogHelper.writePixLog(context, "TOKEN_CONSULTA_API", "[TOKEN] Fazendo requisição para API Gemco - codfil: " + codfil);
            
            Response<String> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                String token = response.body().trim();
                LogHelper.writePixLog(context, "TOKEN_CONSULTA_SUCESSO", "[TOKEN] Token obtido com sucesso: " + token.substring(0, Math.min(10, token.length())) + "...");
                return token;
            } else {
                String errorMsg = "Erro ao obter token da filial. Código: " + response.code();
                if (response.errorBody() != null) {
                    errorMsg += " - " + response.errorBody().string();
                }
                LogHelper.writePixLog(context, "TOKEN_CONSULTA_ERRO", "[TOKEN] " + errorMsg);
                throw new IOException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Erro na consulta do token: " + e.getMessage();
            LogHelper.writePixLog(context, "TOKEN_CONSULTA_EXCEPTION", "[TOKEN] " + errorMsg);
            throw new IOException(errorMsg, e);
        }
    }

    public void consultaPix(long numeroPedido, String codfil, Context context) {
        Log.d("FixPayHelper", "=== INICIANDO CONSULTA PIX ===");
        Log.d("FixPayHelper", "Pedido: " + numeroPedido);
        Log.d("FixPayHelper", "Código da filial: " + codfil);
        
        try {
            // Usar diretamente o token estático do ApiGemco
            String token = ApiGemco.token;
            Log.d("FixPayHelper", "=== USANDO TOKEN ESTÁTICO APIGEMCO ===");
            Log.d("FixPayHelper", "Token ApiGemco: " + token);
            Log.d("FixPayHelper", "Tamanho do token: " + token.length() + " caracteres");
            
            // Criar cliente OkHttp para requisição direta
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Construir URL completa
            String url = "http://apihomol.acalhomecenter.com.br:8087/ApiAcal/api/v1/FixPay?pedido=" + numeroPedido;
            Log.d("FixPayHelper", "URL da consulta PIX: " + url);
            
            // Criar requisição HTTP GET
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();
            
            Log.d("FixPayHelper", "Executando requisição GET para: " + url);
            Log.d("FixPayHelper", "Authorization Header: Bearer " + token);
            
            // Executar requisição de forma assíncrona
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    Log.d("FixPayHelper", "=== RESPOSTA HTTP DA CONSULTA PIX ===");
                    Log.d("FixPayHelper", "Código de status: " + response.code());
                    Log.d("FixPayHelper", "Mensagem de status: " + response.message());
                    Log.d("FixPayHelper", "URL da requisição: " + response.request().url());
                    
                    // Log dos headers da resposta
                    Log.d("FixPayHelper", "=== HEADERS DA RESPOSTA ===");
                    for (String headerName : response.headers().names()) {
                        Log.d("FixPayHelper", "Header: " + headerName + " = " + response.headers().get(headerName));
                    }
                    
                    // Log dos headers da requisição
                    Log.d("FixPayHelper", "=== HEADERS DA REQUISIÇÃO ===");
                    for (String headerName : response.request().headers().names()) {
                        Log.d("FixPayHelper", "Request Header: " + headerName + " = " + response.request().headers().get(headerName));
                    }
                    
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        Log.d("FixPayHelper", "=== CONSULTA PIX REALIZADA COM SUCESSO ===");
                        
                        // DEBUG: Log do responseBody completo ANTES de qualquer processamento
                        Log.d("FixPayHelper", "[DEBUG] ResponseBody completo (tamanho: " + responseBody.length() + "): " + responseBody);
                        Log.d("FixPayHelper", "[DEBUG] ResponseBody após trim: '" + responseBody.trim() + "'");
                        
                        // DEBUG: Verificações de formato
                        boolean startsWithBracket = responseBody.trim().startsWith("[");
                        boolean equalsEmptyArray = responseBody.trim().equals("[]");
                        Log.d("FixPayHelper", "[DEBUG] responseBody.trim().startsWith('[') = " + startsWithBracket);
                        Log.d("FixPayHelper", "[DEBUG] responseBody.trim().equals('[]') = " + equalsEmptyArray);
                        
                        // DEBUG: Log do valor de codfil antes da conversão
                        Log.d("FixPayHelper", "[DEBUG] Valor de codfil antes da conversão: '" + codfil + "'");
                        
                        Log.d("FixPayHelper", "Resposta completa da consulta PIX: " + responseBody);
                        
                        try {
                            // Parse do JSON para log detalhado
                            if (responseBody.trim().startsWith("[")) {
                                // É um array JSON
                                Log.d("FixPayHelper", "Tipo de resposta: Array JSON");
                                if (responseBody.trim().equals("[]")) {
                                    Log.d("FixPayHelper", "Resultado: Nenhum link de pagamento encontrado para o pedido " + numeroPedido);
                                } else {
                                    Log.d("FixPayHelper", "Resultado: Links de pagamento encontrados para o pedido " + numeroPedido);
                                    Log.d("FixPayHelper", "Dados dos links: " + responseBody);
                                    
                                    // DEBUG: Log antes da chamada processarConsultaTokensPixExistentes
                                    Log.d("FixPayHelper", "[DEBUG] ANTES de chamar processarConsultaTokensPixExistentes");
                                    Log.d("FixPayHelper", "[DEBUG] Parâmetros: context=" + context + ", responseBody.length=" + responseBody.length() + ", codfil='" + codfil + "'");
                                    
                                    // NOVA FUNCIONALIDADE: Processar consulta de tokens PIX existentes
                                    Log.d("FixPayHelper", "=== INICIANDO CONSULTA DE TOKENS PIX INDIVIDUAIS ===");
                                    try {
                                        Log.d("FixPayHelper", "[DEBUG] Tentando converter codfil '" + codfil + "' para int");
                                        int codfilInt = Integer.parseInt(codfil);
                                        Log.d("FixPayHelper", "[DEBUG] Conversão bem-sucedida: codfilInt = " + codfilInt);
                                        Log.d("FixPayHelper", "[DEBUG] Chamando processarConsultaTokensPixExistentes...");
                                        processarConsultaTokensPixExistentes(context, responseBody, codfilInt);
                                        Log.d("FixPayHelper", "[DEBUG] processarConsultaTokensPixExistentes executado com sucesso");
                                    } catch (NumberFormatException e) {
                                        Log.e("FixPayHelper", "[DEBUG] Erro ao converter codfil '" + codfil + "' para int: " + e.getMessage());
                                        Log.e("FixPayHelper", "[DEBUG] Stack trace da conversão:", e);
                                    } catch (Exception e) {
                                        Log.e("FixPayHelper", "[DEBUG] Erro inesperado ao chamar processarConsultaTokensPixExistentes: " + e.getMessage());
                                        Log.e("FixPayHelper", "[DEBUG] Stack trace:", e);
                                    }
                                }
                            } else if (responseBody.trim().startsWith("{")) {
                                // É um objeto JSON
                                Log.d("FixPayHelper", "Tipo de resposta: Objeto JSON");
                                Log.d("FixPayHelper", "Dados do objeto: " + responseBody);
                            } else {
                                // Resposta em texto simples
                                Log.d("FixPayHelper", "Tipo de resposta: Texto simples");
                                Log.d("FixPayHelper", "Conteúdo: " + responseBody);
                            }
                        } catch (Exception e) {
                            Log.e("FixPayHelper", "[DEBUG] Erro ao processar resposta da consulta PIX: " + e.getMessage());
                            Log.e("FixPayHelper", "[DEBUG] Stack trace do processamento:", e);
                            Log.d("FixPayHelper", "Resposta bruta: " + responseBody);
                        }
                    } else {
                        Log.e("FixPayHelper", "=== ERRO NA CONSULTA PIX ===");
                        Log.e("FixPayHelper", "Código de erro: " + response.code());
                        Log.e("FixPayHelper", "Mensagem de erro: " + response.message());
                        
                        try {
                            String errorBody = response.body() != null ? response.body().string() : "Sem corpo de erro";
                            Log.e("FixPayHelper", "Corpo do erro da consulta PIX: " + errorBody);
                        } catch (Exception e) {
                            Log.e("FixPayHelper", "Erro ao ler corpo da resposta de erro: " + e.getMessage());
                        }
                    }
                    
                    response.close();
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("FixPayHelper", "=== FALHA NA CHAMADA DA API DE CONSULTA PIX ===");
                    Log.e("FixPayHelper", "Erro de conexão: " + e.getMessage());
                    Log.e("FixPayHelper", "URL da requisição: " + call.request().url());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            Log.e("FixPayHelper", "Erro ao obter token para consulta PIX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public PixResponse GerarPix(Context context, PixRequest pixRequest, int codfil) throws IOException, XmlPullParserException, ParseException {
        return GerarPix(context, pixRequest, codfil, null);
    }
    
    public PixResponse GerarPix(Context context, PixRequest pixRequest, int codfil, PixLoadingCallback loadingCallback) throws IOException, XmlPullParserException, ParseException {
        return GerarPix(context, pixRequest, codfil, true, loadingCallback);
    }
    
    public PixResponse GerarPix(Context context, PixRequest pixRequest, int codfil, boolean verificarAntes, PixLoadingCallback loadingCallback) throws IOException, XmlPullParserException, ParseException {
        LogHelper.writePixLog(context, "GERAR_PIX_INICIO", "[GERAR_PIX] Iniciando geração de PIX para filial: " + codfil + " (verificarAntes: " + verificarAntes + ")");
        
        // Se verificarAntes for true, executar verificação antes de gerar
        if (verificarAntes) {
            LogHelper.writePixLog(context, "GERAR_PIX_VERIFICACAO_SOLICITADA", "[GERAR_PIX] Verificação solicitada antes da geração do PIX");
            
            // Extrair número do pedido do pixRequest (assumindo que está no campo reference ou similar)
            String numeroPedido = extrairNumeroPedidoDoRequest(context, pixRequest);
            
            if (numeroPedido != null && !numeroPedido.trim().isEmpty()) {
                LogHelper.writePixLog(context, "GERAR_PIX_VERIFICANDO_PEDIDO", "[GERAR_PIX] Verificando pedido: " + numeroPedido);
                
                // Usar um objeto para controlar o fluxo assíncrono
                final Object lock = new Object();
                final boolean[] podeGerar = {false};
                final String[] mensagemErro = {null};
                
                verificarPixAntesDeGerar(context, numeroPedido, null, new VerificacaoPixCallback() {
                    @Override
                    public void onPodeGerar() {
                        synchronized (lock) {
                            podeGerar[0] = true;
                            lock.notify();
                        }
                    }
                    
                    @Override
                    public void onJaPago(String mensagem) {
                        synchronized (lock) {
                            mensagemErro[0] = mensagem;
                            lock.notify();
                        }
                    }
                    
                    @Override
                    public void onErro(String erro) {
                        synchronized (lock) {
                            // Em caso de erro na verificação, permitir geração para não bloquear usuário
                            podeGerar[0] = true;
                            lock.notify();
                        }
                    }
                });
                
                // Aguardar resultado da verificação com timeout reduzido
                synchronized (lock) {
                    try {
                        lock.wait(5000); // Timeout reduzido para 5 segundos
                    } catch (InterruptedException e) {
                        LogHelper.writePixError(context, "[GERAR_PIX] Thread interrompida durante verificação, permitindo geração do PIX");
                        Thread.currentThread().interrupt(); // Restaurar status de interrupção
                        podeGerar[0] = true;
                    } catch (Exception e) {
                        LogHelper.writePixError(context, "[GERAR_PIX] Erro inesperado na verificação: " + e.getMessage());
                        podeGerar[0] = true; // Em caso de erro, permitir geração
                    }
                }
                
                // Se PIX já foi pago, retornar null e logar
                if (mensagemErro[0] != null) {
                    LogHelper.writePixError(context, "[GERAR_PIX] " + mensagemErro[0]);
                    return null;
                }
                
                // Se não pode gerar por algum motivo, retornar null
                if (!podeGerar[0]) {
                    LogHelper.writePixError(context, "[GERAR_PIX] Verificação impediu a geração do PIX");
                    return null;
                }
            } else {
                LogHelper.writePixLog(context, "GERAR_PIX_SEM_NUMERO_PEDIDO", "[GERAR_PIX] Número do pedido não encontrado no request, prosseguindo sem verificação");
            }
        }
        
        // Consultar token da filial dinamicamente
        String token = ConsultarTokenFilial(context, codfil);
        
        if (token == null || token.isEmpty()) {
            LogHelper.writePixError(context, "[GERAR_PIX] Token da filial " + codfil + " é nulo ou vazio. Não é possível gerar PIX.");
            return null; // Token não encontrado
        }

        LogHelper.writePixLog(context, "GERAR_PIX_TOKEN_OK", "[GERAR_PIX] Token obtido com sucesso. Iniciando chamada para FixPay API.");
        
        final ApiFixPay service = retrofit.create(ApiFixPay.class);
        try {
            final Call<PixResponse> post = service.gerarPix(pixRequest, "Bearer " + token);
            LogHelper.writePixRequest(context, "[GERAR_PIX] Enviando requisição para FixPay com valor: " + pixRequest.getValue());
            
            // Executar a chamada e obter a resposta HTTP completa
            Response<PixResponse> httpResponse = post.execute();
            
            // LOG DETALHADO: Status code da resposta HTTP
            Log.d("FixPayHelper_API", "=== RESPOSTA HTTP COMPLETA ===");
            Log.d("FixPayHelper_API", "Status Code: " + httpResponse.code());
            Log.d("FixPayHelper_API", "Status Message: " + httpResponse.message());
            Log.d("FixPayHelper_API", "Is Successful: " + httpResponse.isSuccessful());
            
            // LOG DETALHADO: Headers da resposta
            Log.d("FixPayHelper_API", "=== HEADERS DA RESPOSTA ===");
            if (httpResponse.headers() != null) {
                for (String headerName : httpResponse.headers().names()) {
                    Log.d("FixPayHelper_API", "Header: " + headerName + " = " + httpResponse.headers().get(headerName));
                }
            }
            
            // LOG DETALHADO: JSON bruto da resposta (se disponível)
            if (httpResponse.errorBody() != null) {
                String errorJson = httpResponse.errorBody().string();
                Log.d("FixPayHelper_API", "=== JSON DE ERRO ===");
                Log.d("FixPayHelper_API", "Error JSON: " + errorJson);
                LogHelper.writePixError(context, "[GERAR_PIX] JSON de erro: " + errorJson);
            }
            
            PixResponse response = httpResponse.body();
            
            // LOG DETALHADO: Resposta parseada
            if (response != null) {
                Gson gson = new Gson();
                String responseJson = gson.toJson(response);
                Log.d("FixPayHelper_API", "=== JSON DA RESPOSTA PARSEADA ===");
                Log.d("FixPayHelper_API", "Response JSON: " + responseJson);
                LogHelper.writePixResponse(context, "[GERAR_PIX] JSON completo da resposta: " + responseJson);
                LogHelper.writePixResponse(context, "[GERAR_PIX] Resposta recebida da FixPay API com sucesso.");
            } else {
                Log.d("FixPayHelper_API", "=== RESPOSTA NULA ===");
                Log.d("FixPayHelper_API", "Response body é nulo");
                LogHelper.writePixError(context, "[GERAR_PIX] Resposta nula recebida da FixPay API.");
            }
            
            return response;
        } catch (Exception e) {
            Log.e("FixPayHelper_API", "=== EXCEÇÃO NA API ===");
            Log.e("FixPayHelper_API", "Exceção: " + e.getClass().getSimpleName());
            Log.e("FixPayHelper_API", "Mensagem: " + e.getMessage());
            Log.e("FixPayHelper_API", "Stack trace: ", e);
            LogHelper.writePixError(context, "[GERAR_PIX] Exceção ao chamar FixPay API: " + e.getMessage());
            return null;
        }
    }
    
    public PixResponse GerarPix(Context context, PixRequest pixRequest, int codfil, boolean verificarAntes) throws IOException, XmlPullParserException, ParseException {
        LogHelper.writePixLog(context, "GERAR_PIX_INICIO", "[GERAR_PIX] Iniciando geração de PIX para filial: " + codfil + " (verificarAntes: " + verificarAntes + ")");
        
        // Se verificarAntes for true, executar verificação antes de gerar
        if (verificarAntes) {
            LogHelper.writePixLog(context, "GERAR_PIX_VERIFICACAO_SOLICITADA", "[GERAR_PIX] Verificação solicitada antes da geração do PIX");
            
            // Extrair número do pedido do pixRequest (assumindo que está no campo reference ou similar)
            String numeroPedido = extrairNumeroPedidoDoRequest(context, pixRequest);
            
            if (numeroPedido != null && !numeroPedido.trim().isEmpty()) {
                LogHelper.writePixLog(context, "GERAR_PIX_VERIFICANDO_PEDIDO", "[GERAR_PIX] Verificando pedido: " + numeroPedido);
                
                // Usar um objeto para controlar o fluxo assíncrono
                final Object lock = new Object();
                final boolean[] podeGerar = {false};
                final String[] mensagemErro = {null};
                
                verificarPixAntesDeGerar(context, numeroPedido, null, new VerificacaoPixCallback() {
                    @Override
                    public void onPodeGerar() {
                        synchronized (lock) {
                            podeGerar[0] = true;
                            lock.notify();
                        }
                    }
                    
                    @Override
                    public void onJaPago(String mensagem) {
                        synchronized (lock) {
                            mensagemErro[0] = mensagem;
                            lock.notify();
                        }
                    }
                    
                    @Override
                    public void onErro(String erro) {
                        synchronized (lock) {
                            // Em caso de erro na verificação, permitir geração para não bloquear usuário
                            podeGerar[0] = true;
                            lock.notify();
                        }
                    }
                });
                
                // Aguardar resultado da verificação com timeout reduzido
                synchronized (lock) {
                    try {
                        lock.wait(5000); // Timeout reduzido para 5 segundos
                    } catch (InterruptedException e) {
                        LogHelper.writePixError(context, "[GERAR_PIX] Thread interrompida durante verificação, permitindo geração do PIX");
                        Thread.currentThread().interrupt(); // Restaurar status de interrupção
                        podeGerar[0] = true;
                    } catch (Exception e) {
                        LogHelper.writePixError(context, "[GERAR_PIX] Erro inesperado na verificação: " + e.getMessage());
                        podeGerar[0] = true; // Em caso de erro, permitir geração
                    }
                }
                
                // Se PIX já foi pago, retornar null e logar
                if (mensagemErro[0] != null) {
                    LogHelper.writePixError(context, "[GERAR_PIX] " + mensagemErro[0]);
                    return null;
                }
                
                // Se não pode gerar por algum motivo, retornar null
                if (!podeGerar[0]) {
                    LogHelper.writePixError(context, "[GERAR_PIX] Verificação impediu a geração do PIX");
                    return null;
                }
            } else {
                LogHelper.writePixLog(context, "GERAR_PIX_SEM_NUMERO_PEDIDO", "[GERAR_PIX] Número do pedido não encontrado no request, prosseguindo sem verificação");
            }
        }
        
        // Consultar token da filial dinamicamente
        String token = ConsultarTokenFilial(context, codfil);
        
        if (token == null || token.isEmpty()) {
            LogHelper.writePixError(context, "[GERAR_PIX] Token da filial " + codfil + " é nulo ou vazio. Não é possível gerar PIX.");
            return null; // Token não encontrado
        }

        LogHelper.writePixLog(context, "GERAR_PIX_TOKEN_OK", "[GERAR_PIX] Token obtido com sucesso. Iniciando chamada para FixPay API.");
        
        final ApiFixPay service = retrofit.create(ApiFixPay.class);
        try {
            final Call<PixResponse> post = service.gerarPix(pixRequest, "Bearer " + token);
            LogHelper.writePixRequest(context, "[GERAR_PIX] Enviando requisição para FixPay com valor: " + pixRequest.getValue());
            
            // Executar a chamada e obter a resposta HTTP completa
            Response<PixResponse> httpResponse = post.execute();
            
            // LOG DETALHADO: Status code da resposta HTTP
            Log.d("FixPayHelper_API", "=== RESPOSTA HTTP COMPLETA ===");
            Log.d("FixPayHelper_API", "Status Code: " + httpResponse.code());
            Log.d("FixPayHelper_API", "Status Message: " + httpResponse.message());
            Log.d("FixPayHelper_API", "Is Successful: " + httpResponse.isSuccessful());
            
            // LOG DETALHADO: Headers da resposta
            Log.d("FixPayHelper_API", "=== HEADERS DA RESPOSTA ===");
            if (httpResponse.headers() != null) {
                for (String headerName : httpResponse.headers().names()) {
                    Log.d("FixPayHelper_API", "Header: " + headerName + " = " + httpResponse.headers().get(headerName));
                }
            }
            
            // LOG DETALHADO: JSON bruto da resposta (se disponível)
            if (httpResponse.errorBody() != null) {
                String errorJson = httpResponse.errorBody().string();
                Log.d("FixPayHelper_API", "=== JSON DE ERRO ===");
                Log.d("FixPayHelper_API", "Error JSON: " + errorJson);
                LogHelper.writePixError(context, "[GERAR_PIX] JSON de erro: " + errorJson);
            }
            
            PixResponse response = httpResponse.body();
            
            // LOG DETALHADO: Resposta parseada
            if (response != null) {
                Gson gson = new Gson();
                String responseJson = gson.toJson(response);
                Log.d("FixPayHelper_API", "=== JSON DA RESPOSTA PARSEADA ===");
                Log.d("FixPayHelper_API", "Response JSON: " + responseJson);
                LogHelper.writePixResponse(context, "[GERAR_PIX] JSON completo da resposta: " + responseJson);
                LogHelper.writePixResponse(context, "[GERAR_PIX] Resposta recebida da FixPay API com sucesso.");
            } else {
                Log.d("FixPayHelper_API", "=== RESPOSTA NULA ===");
                Log.d("FixPayHelper_API", "Response body é nulo");
                LogHelper.writePixError(context, "[GERAR_PIX] Resposta nula recebida da FixPay API.");
            }
            
            return response;
        } catch (Exception e) {
            Log.e("FixPayHelper_API", "=== EXCEÇÃO NA API ===");
            Log.e("FixPayHelper_API", "Exceção: " + e.getClass().getSimpleName());
            Log.e("FixPayHelper_API", "Mensagem: " + e.getMessage());
            Log.e("FixPayHelper_API", "Stack trace: ", e);
            LogHelper.writePixError(context, "[GERAR_PIX] Exceção ao chamar FixPay API: " + e.getMessage());
            return null;
        }
    }

    public PixResponse GerarPixComWhatsApp(PopupPedFechamento context, PixRequest pixRequest, int codfil, String numpedven, String codcli, String telefone) throws IOException, XmlPullParserException, ParseException {
        return GerarPixComWhatsApp(context, pixRequest, codfil, numpedven, codcli, telefone, null);
    }
    
    public PixResponse GerarPixComWhatsApp(PopupPedFechamento context, PixRequest pixRequest, int codfil, String numpedven, String codcli, String telefone, PixLoadingCallback loadingCallback) throws IOException, XmlPullParserException, ParseException {
        LogHelper.writePixLog(context, "GERAR_PIX_WHATSAPP_INICIO", "[GERAR_PIX_WHATSAPP] Iniciando geração de PIX com WhatsApp para filial: " + codfil);
        
        // Primeiro, gerar o PIX
        PixResponse pixResponse = GerarPix(context, pixRequest, codfil, true, loadingCallback);
        
        if (pixResponse != null && pixResponse.getData() != null) {
            LogHelper.writePixLog(context, "GERAR_PIX_WHATSAPP_PIX_OK", "[GERAR_PIX_WHATSAPP] PIX gerado com sucesso. Iniciando envio via WhatsApp.");
            
            // Criar objeto Pedidos para envio via WhatsApp
            Pedidos pedido = new Pedidos();
            pedido.setNumpedven(Double.parseDouble(numpedven));
            pedido.setCodfil(codfil);
            
            // Enviar via WhatsApp usando a API Gemco
             try {
                 EnviarPixWhatsApp(context, pedido, telefone, pixResponse);
                 LogHelper.writePixLog(context, "GERAR_PIX_WHATSAPP_SUCESSO", "[GERAR_PIX_WHATSAPP] PIX gerado e enviado via WhatsApp com sucesso.");
             } catch (Exception e) {
                 LogHelper.writePixError(context, "[GERAR_PIX_WHATSAPP] Erro ao enviar PIX via WhatsApp: " + e.getMessage());
                 // Mesmo com erro no WhatsApp, retorna o PIX gerado
             }
        } else {
            LogHelper.writePixError(context, "[GERAR_PIX_WHATSAPP] Falha na geração do PIX. WhatsApp não será enviado.");
        }
        
        return pixResponse;
    }

    private void EnviarPixWhatsApp(Context context, Pedidos pedido, String telefone, PixResponse pixResponse) throws IOException {
        LogHelper.writePixLog(context, "ENVIAR_PIX_WHATSAPP_INICIO", "[ENVIAR_PIX_WHATSAPP] Iniciando envio de PIX via WhatsApp para: " + telefone);
        
        try {
            OkHttpClient clientGemco = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();
            
            Retrofit retrofitGemco = new Retrofit.Builder()
                    .baseUrl(ApiGemco.BASE_URL)
                    .client(clientGemco)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            final ApiGemco serviceGemco = retrofitGemco.create(ApiGemco.class);
            
            // Usar os parâmetros corretos do método processarCobrancaPix
            String txid = pixResponse.getData().getTxid();
            String pixCopiaCola = pixResponse.getData().getPixCopiaECola();
            double valor = Double.parseDouble(pixResponse.getData().getValor().getOriginal());
            
            final Call<String> call = serviceGemco.processarCobrancaPix(
                txid, 
                pixCopiaCola, 
                valor, 
                String.valueOf(pedido.getNumpedven()), 
                String.valueOf(pedido.getCliente().getCodcli()), 
                telefone, 
                "Bearer " + ApiGemco.token
            );
            
            LogHelper.writePixLog(context, "ENVIAR_PIX_WHATSAPP_CHAMADA", "[ENVIAR_PIX_WHATSAPP] Executando chamada para ProcessarCobrancaPix - URL: " + ApiGemco.BASE_URL);
            
            Response<String> response = call.execute();
            
            if (response.isSuccessful()) {
                LogHelper.writePixLog(context, "ENVIAR_PIX_WHATSAPP_SUCESSO", "[ENVIAR_PIX_WHATSAPP] PIX enviado via WhatsApp com sucesso. Response: " + response.body());
            } else {
                LogHelper.writePixError(context, "[ENVIAR_PIX_WHATSAPP] Falha ao enviar PIX via WhatsApp. Response code: " + response.code() + ", Message: " + response.message());
            }
        } catch (Exception e) {
            LogHelper.writePixError(context, "[ENVIAR_PIX_WHATSAPP] Exceção ao enviar PIX via WhatsApp: " + e.getMessage());
            throw new IOException("Erro ao enviar PIX via WhatsApp: " + e.getMessage());
        }
    }

    /**
     * Retorna o token PIX mais atual armazenado na instância
     * @return String com o token PIX mais atual ou null se não houver token armazenado
     */
    public String getTokenPixMaisAtual() {
        if (this.tokenPixMaisAtual != null && !this.tokenPixMaisAtual.trim().isEmpty()) {
            LogHelper.writePixLog(null, "TOKEN_ACESSADO", "[TOKEN_STORAGE] Token PIX mais atual acessado: " + this.tokenPixMaisAtual.substring(0, Math.min(10, this.tokenPixMaisAtual.length())) + "...");
            return this.tokenPixMaisAtual;
        } else {
            LogHelper.writePixLog(null, "TOKEN_VAZIO_ACESSO", "[TOKEN_STORAGE] Tentativa de acesso ao token PIX, mas nenhum token está armazenado");
            return null;
        }
    }

    /**
     * Limpa o token PIX armazenado na instância
     */
    public void limparTokenPixMaisAtual() {
        if (this.tokenPixMaisAtual != null) {
            LogHelper.writePixLog(null, "TOKEN_LIMPO_MANUAL", "[TOKEN_STORAGE] Token PIX limpo manualmente: " + this.tokenPixMaisAtual.substring(0, Math.min(10, this.tokenPixMaisAtual.length())) + "...");
        }
        this.tokenPixMaisAtual = null;
        LogHelper.writePixLog(null, "TOKEN_RESETADO", "[TOKEN_STORAGE] Token PIX resetado para null");
    }

    // ===== INTERFACES DE CALLBACK PARA VERIFICAÇÃO DE PIX =====
    
    /**
     * Interface de callback para resultado da consulta à API da ACAL
     */
    public interface ConsultaPixCallback {
        void onSucesso(String tokenLink, String link, String status, String tipo);
        void onErro(String mensagemErro);
        void onTokenNaoEncontrado();
    }
    
    /**
     * Interface de callback para resultado da verificação de status na API FixPay
     */
    public interface StatusPixCallback {
        void onSucesso(String status, String resposta);
        void onErro(String mensagemErro);
    }
    
    /**
     * Interface de callback para resultado final da verificação
     */
    public interface VerificacaoPixCallback {
        void onPodeGerar(); // PIX pode ser gerado
        void onJaPago(String mensagem); // PIX já foi pago
        void onErro(String mensagemErro); // Erro na verificação
    }

    // ===== MÉTODOS AUXILIARES =====
    
    /**
     * Extrai o número do pedido do PixRequest
     * @param context Contexto da aplicação
     * @param pixRequest Request do PIX
     * @return Número do pedido ou null se não encontrado
     */
    private String extrairNumeroPedidoDoRequest(Context context, PixRequest pixRequest) {
        if (pixRequest == null) {
            LogHelper.writePixLog(context, "EXTRAIR_PEDIDO_REQUEST_NULO", "[EXTRAIR_PEDIDO] PixRequest é nulo");
            return null;
        }
        
        // Tentar extrair do campo message (campo disponível na classe PixRequest)
        if (pixRequest.getMessage() != null && !pixRequest.getMessage().trim().isEmpty()) {
            String message = pixRequest.getMessage().trim();
            // Procurar por padrões numéricos na mensagem
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
            java.util.regex.Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String numeroPedido = matcher.group();
                LogHelper.writePixLog(context, "EXTRAIR_PEDIDO_MESSAGE", "[EXTRAIR_PEDIDO] Número do pedido extraído da message: " + numeroPedido);
                return numeroPedido;
            }
        }
        
        // Se não encontrar na message, tentar usar o partner_id como fallback
        if (pixRequest.getPartner_id() > 0) {
            String numeroPedido = String.valueOf(pixRequest.getPartner_id());
            LogHelper.writePixLog(context, "EXTRAIR_PEDIDO_PARTNER_ID", "[EXTRAIR_PEDIDO] Número do pedido extraído do partner_id: " + numeroPedido);
            return numeroPedido;
        }
        
        LogHelper.writePixLog(context, "EXTRAIR_PEDIDO_NAO_ENCONTRADO", "[EXTRAIR_PEDIDO] Número do pedido não encontrado no PixRequest");
        return null;
    }
    
    // ===== MÉTODOS DE VERIFICAÇÃO DE PIX =====
    
    /**
     * Consulta o token PIX mais atual na API da ACAL
     * @param context Contexto da aplicação
     * @param numeroPedido Número do pedido para consulta
     * @param callback Callback para retorno do resultado
     */
    public void consultarTokenPixMaisAtualAcal(Context context, String numeroPedido, ConsultaPixCallback callback) {
        LogHelper.writePixLog(context, "CONSULTA_ACAL_INICIO", "[CONSULTA_ACAL] Iniciando consulta para pedido: " + numeroPedido);
        
        try {
            // Obter token de autorização
            String token = ApiGemco.token;
            LogHelper.writePixLog(context, "CONSULTA_ACAL_TOKEN", "[CONSULTA_ACAL] Usando token de autorização: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));
            
            // Criar cliente OkHttp para requisição à API da ACAL
            OkHttpClient clientAcal = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Construir URL da API da ACAL
            String url = "http://apihomol.acalhomecenter.com.br:8087/ApiAcal/api/v1/FixPay?pedido=" + numeroPedido;
            LogHelper.writePixLog(context, "CONSULTA_ACAL_URL", "[CONSULTA_ACAL] URL: " + url);
            
            // Criar requisição HTTP GET com header de autorização Bearer
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();
            
            LogHelper.writePixLog(context, "CONSULTA_ACAL_HEADERS", "[CONSULTA_ACAL] Headers: Authorization=Bearer *** , Content-Type=application/json");
            
            // Executar requisição de forma assíncrona
            clientAcal.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    LogHelper.writePixLog(context, "CONSULTA_ACAL_RESPOSTA", "[CONSULTA_ACAL] Status: " + response.code() + " - " + response.message());
                    
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        LogHelper.writePixLog(context, "CONSULTA_ACAL_SUCESSO", "[CONSULTA_ACAL] Consulta realizada com sucesso");
                        LogHelper.writePixResponse(context, "[CONSULTA_ACAL] Resposta: " + responseBody);
                        
                        try {
                            // Processar resposta JSON
                            if (responseBody != null && !responseBody.trim().isEmpty()) {
                                JsonParser parser = new JsonParser();
                                JsonElement jsonElement = parser.parse(responseBody);
                                
                                if (jsonElement.isJsonObject()) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    
                                    // Extrair campos da resposta
                                    String tokenLink = jsonObject.has("TokenLink") ? jsonObject.get("TokenLink").getAsString() : null;
                                    String link = jsonObject.has("Link") ? jsonObject.get("Link").getAsString() : null;
                                    String status = jsonObject.has("Status") ? jsonObject.get("Status").getAsString() : null;
                                    String tipo = jsonObject.has("Tipo") ? jsonObject.get("Tipo").getAsString() : null;
                                    
                                    LogHelper.writePixLog(context, "CONSULTA_ACAL_DADOS", "[CONSULTA_ACAL] TokenLink: " + (tokenLink != null ? tokenLink.substring(0, Math.min(10, tokenLink.length())) + "..." : "null") + ", Status: " + status + ", Tipo: " + tipo);
                                    
                                    if (tokenLink != null && !tokenLink.trim().isEmpty()) {
                                        // Log específico com o valor completo do TokenLink que será usado na API FixPay
                                        LogHelper.writePixLog(context, "CONSULTA_ACAL_TOKENLINK_COMPLETO", "[CONSULTA_ACAL] TokenLink que será usado na API FixPay: " + tokenLink);
                                        callback.onSucesso(tokenLink, link, status, tipo);
                                    } else {
                                        LogHelper.writePixLog(context, "CONSULTA_ACAL_TOKEN_VAZIO", "[CONSULTA_ACAL] TokenLink não encontrado ou vazio");
                                        callback.onTokenNaoEncontrado();
                                    }
                                } else {
                                    LogHelper.writePixError(context, "[CONSULTA_ACAL] Resposta não é um objeto JSON válido");
                                    callback.onErro("Resposta da API não é um objeto JSON válido");
                                }
                            } else {
                                LogHelper.writePixError(context, "[CONSULTA_ACAL] Resposta vazia ou nula");
                                callback.onTokenNaoEncontrado();
                            }
                        } catch (Exception e) {
                            LogHelper.writePixError(context, "[CONSULTA_ACAL] Erro ao processar resposta JSON: " + e.getMessage());
                            callback.onErro("Erro ao processar resposta: " + e.getMessage());
                        }
                        
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sem corpo de erro";
                        LogHelper.writePixError(context, "[CONSULTA_ACAL] Erro na consulta. Status: " + response.code() + ", Erro: " + errorBody);
                        callback.onErro("Erro na consulta à API da ACAL. Status: " + response.code());
                    }
                    
                    response.close();
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LogHelper.writePixError(context, "[CONSULTA_ACAL] Falha na conexão: " + e.getMessage());
                    callback.onErro("Falha na conexão com a API da ACAL: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            LogHelper.writePixError(context, "[CONSULTA_ACAL] Erro ao iniciar consulta: " + e.getMessage());
            callback.onErro("Erro ao iniciar consulta: " + e.getMessage());
        }
    }

    /**
     * Verifica o status de um PIX na API FixPay usando o TokenLink
     * @param context Contexto da aplicação
     * @param tokenLink Token do PIX para verificar o status
     * @param callback Callback para retornar o resultado
     */
    public void verificarStatusPixFixPay(Context context, String tokenLink, StatusPixCallback callback) {
        LogHelper.writePixLog(context, "VERIFICAR_STATUS_FIXPAY_INICIO", "[VERIFICAR_STATUS_FIXPAY] Iniciando verificação de status do PIX");
        LogHelper.writePixLog(context, "VERIFICAR_STATUS_FIXPAY_TOKEN", "[VERIFICAR_STATUS_FIXPAY] TokenLink: " + (tokenLink != null ? tokenLink.substring(0, Math.min(10, tokenLink.length())) + "..." : "null"));
        
        if (tokenLink == null || tokenLink.trim().isEmpty()) {
            LogHelper.writePixError(context, "[VERIFICAR_STATUS_FIXPAY] TokenLink está vazio ou nulo");
            callback.onErro("TokenLink não pode estar vazio");
            return;
        }
        
        // Primeiro passo: obter o codfil do usuário logado
        SharedPreferences preferences = context.getSharedPreferences("usuario.salvo", Context.MODE_PRIVATE);
        int codfil = preferences.getInt("codfil", 5); // valor padrão 5 se não encontrar
        LogHelper.writePixLog(context, "VERIFICAR_STATUS_FIXPAY_CODFIL", "[VERIFICAR_STATUS_FIXPAY] Codfil obtido: " + codfil);
        
        // Segundo passo: obter o token específico da FixPay via API TokenFilial da ACAL usando ConsultarTokenFilial
        // Executar em thread separada pois ConsultarTokenFilial é síncrono
        new Thread(() -> {
            try {
                String tokenFixPay = ConsultarTokenFilial(context, codfil);
                LogHelper.writePixLog(context, "VERIFICAR_STATUS_FIXPAY_TOKEN_FILIAL_SUCESSO", "[VERIFICAR_STATUS_FIXPAY] Token FixPay obtido com sucesso via ConsultarTokenFilial");
                
                // Terceiro passo: fazer a requisição para a API FixPay com o token correto
                consultarApiFixPayComToken(context, tokenLink, tokenFixPay, callback);
                
            } catch (Exception e) {
                LogHelper.writePixError(context, "[VERIFICAR_STATUS_FIXPAY] Erro ao obter token FixPay da filial via ConsultarTokenFilial: " + e.getMessage());
                callback.onErro("Erro ao obter token FixPay: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Interface para callback do token da filial
     */
    private interface TokenFilialCallback {
        void onSucesso(String tokenFixPay);
        void onErro(String mensagemErro);
    }
    
    /**
     * Interface pública para callback do token da filial
     */
    public interface TokenCallback {
        void onSucesso(String tokenFixPay);
        void onErro(String mensagemErro);
    }
    
    /**
     * Versão sobrecarregada do ConsultarTokenFilial que aceita TokenCallback
     * @param context Contexto da aplicação
     * @param callback Callback para retornar o token
     */
    private void ConsultarTokenFilial(Context context, TokenCallback callback) {
        LogHelper.writePixLog(context, "CONSULTAR_TOKEN_FILIAL_CALLBACK", "[CONSULTAR_TOKEN_FILIAL] Iniciando obtenção do token FixPay com callback");
        
        // Usar código da filial padrão (pode ser obtido de SharedPreferences ou configuração)
        int codfil = 1; // Valor padrão - ajustar conforme necessário
        
        obterTokenFixPayFilial(context, codfil, new TokenFilialCallback() {
            @Override
            public void onSucesso(String tokenFixPay) {
                LogHelper.writePixLog(context, "CONSULTAR_TOKEN_FILIAL_CALLBACK_SUCESSO", "[CONSULTAR_TOKEN_FILIAL] Token obtido com sucesso via callback");
                callback.onSucesso(tokenFixPay);
            }
            
            @Override
            public void onErro(String mensagemErro) {
                LogHelper.writePixError(context, "[CONSULTAR_TOKEN_FILIAL] Erro ao obter token via callback: " + mensagemErro);
                callback.onErro(mensagemErro);
            }
        });
    }
    
    /**
     * Obtém o token específico da FixPay através da API TokenFilial da ACAL
     * @param context Contexto da aplicação
     * @param codfil Código da filial
     * @param callback Callback para retornar o token
     */
    private void obterTokenFixPayFilial(Context context, int codfil, TokenFilialCallback callback) {
        LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_INICIO", "[OBTER_TOKEN_FILIAL] Iniciando obtenção do token FixPay para filial: " + codfil);
        
        try {
            // Criar cliente OkHttp para requisição à API ACAL
            OkHttpClient clientAcal = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Construir URL da API TokenFilial
            String url = "http://apihomol.acalhomecenter.com.br:8087/ApiAcal/api/v1/FixPay/TokenFilial?codfil=" + codfil;
            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_URL", "[OBTER_TOKEN_FILIAL] URL: " + url);
            
            // Fazer requisição GET com Bearer token do ApiGemco
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + ApiGemco.token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
            
            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_HEADERS", "[OBTER_TOKEN_FILIAL] Headers: Authorization=Bearer *** , Content-Type=application/json");
            
            // Executar requisição de forma assíncrona
            clientAcal.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_RESPOSTA", "[OBTER_TOKEN_FILIAL] Status: " + response.code() + " - " + response.message());
                    
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_SUCESSO", "[OBTER_TOKEN_FILIAL] Token obtido com sucesso");
                        
                        // LOG DETALHADO: Resposta completa da API TokenFilial
                        LogHelper.writePixResponse(context, "[OBTER_TOKEN_FILIAL] ===== RESPOSTA COMPLETA DA API TOKENFILIAL =====");
                        LogHelper.writePixResponse(context, "[OBTER_TOKEN_FILIAL] Status HTTP: " + response.code() + " - " + response.message());
                        LogHelper.writePixResponse(context, "[OBTER_TOKEN_FILIAL] Headers: " + response.headers().toString());
                        LogHelper.writePixResponse(context, "[OBTER_TOKEN_FILIAL] Body JSON: " + responseBody);
                        LogHelper.writePixResponse(context, "[OBTER_TOKEN_FILIAL] Tamanho do body: " + responseBody.length() + " caracteres");
                        LogHelper.writePixResponse(context, "[OBTER_TOKEN_FILIAL] ================================================");
                        
                        LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_RESPOSTA_BODY", "[OBTER_TOKEN_FILIAL] Resposta: " + responseBody);
                        
                        try {
                            // A resposta deve ser o token diretamente ou em formato JSON
                            String tokenFixPay = responseBody.trim();
                            
                            // Se a resposta for JSON, extrair o token
                            if (tokenFixPay.startsWith("{") && tokenFixPay.endsWith("}")) {
                                JsonParser parser = new JsonParser();
                                JsonElement jsonElement = parser.parse(tokenFixPay);
                                if (jsonElement.isJsonObject()) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    // Assumindo que o token está em um campo como "token" ou "TokenFilial"
                                    if (jsonObject.has("token")) {
                                        tokenFixPay = jsonObject.get("token").getAsString();
                                    } else if (jsonObject.has("TokenFilial")) {
                                        tokenFixPay = jsonObject.get("TokenFilial").getAsString();
                                    } else {
                                        // Se não encontrar campos conhecidos, usar o primeiro valor string
                                        for (String key : jsonObject.keySet()) {
                                            if (jsonObject.get(key).isJsonPrimitive() && jsonObject.get(key).getAsJsonPrimitive().isString()) {
                                                tokenFixPay = jsonObject.get(key).getAsString();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // LOG DETALHADO: Análise do token extraído
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_ANALISE_TOKEN", "[OBTER_TOKEN_FILIAL] ===== ANÁLISE DO TOKEN EXTRAÍDO =====");
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_TOKEN_COMPLETO", "[OBTER_TOKEN_FILIAL] Token completo: " + tokenFixPay);
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_TOKEN_TAMANHO", "[OBTER_TOKEN_FILIAL] Tamanho do token: " + (tokenFixPay != null ? tokenFixPay.length() : 0) + " caracteres");
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_TOKEN_TIPO", "[OBTER_TOKEN_FILIAL] Tipo do token: " + (tokenFixPay != null ? tokenFixPay.getClass().getSimpleName() : "null"));
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_TOKEN_VAZIO", "[OBTER_TOKEN_FILIAL] Token está vazio: " + (tokenFixPay == null || tokenFixPay.trim().isEmpty()));
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_TOKEN_EXTRAIDO", "[OBTER_TOKEN_FILIAL] Token extraído (mascarado): " + (tokenFixPay != null && tokenFixPay.length() > 10 ? tokenFixPay.substring(0, 10) + "..." : tokenFixPay));
                            LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_ANALISE_FIM", "[OBTER_TOKEN_FILIAL] ===========================================");
                            
                            if (tokenFixPay != null && !tokenFixPay.trim().isEmpty()) {
                                String tokenFinal = tokenFixPay.trim();
                                LogHelper.writePixLog(context, "OBTER_TOKEN_FILIAL_TOKEN_FINAL", "[OBTER_TOKEN_FILIAL] Token final enviado para callback: " + (tokenFinal.length() > 10 ? tokenFinal.substring(0, 10) + "..." : tokenFinal));
                                callback.onSucesso(tokenFinal);
                            } else {
                                LogHelper.writePixError(context, "[OBTER_TOKEN_FILIAL] Token vazio ou nulo na resposta");
                                callback.onErro("Token vazio retornado pela API TokenFilial");
                            }
                            
                        } catch (Exception e) {
                            LogHelper.writePixError(context, "[OBTER_TOKEN_FILIAL] Erro ao processar resposta: " + e.getMessage());
                            callback.onErro("Erro ao processar resposta da API TokenFilial: " + e.getMessage());
                        }
                        
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sem corpo de erro";
                        LogHelper.writePixError(context, "[OBTER_TOKEN_FILIAL] Erro na obtenção do token. Status: " + response.code() + ", Erro: " + errorBody);
                        callback.onErro("Erro na API TokenFilial. Status: " + response.code() + ", Resposta: " + errorBody);
                    }
                    
                    response.close();
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LogHelper.writePixError(context, "[OBTER_TOKEN_FILIAL] Falha na conexão: " + e.getMessage());
                    callback.onErro("Falha na conexão com a API TokenFilial: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            LogHelper.writePixError(context, "[OBTER_TOKEN_FILIAL] Erro ao iniciar obtenção do token: " + e.getMessage());
            callback.onErro("Erro ao iniciar obtenção do token: " + e.getMessage());
        }
    }
    
    /**
     * Consulta a API FixPay com o token específico obtido
     * @param context Contexto da aplicação
     * @param tokenLink Token do PIX para verificar
     * @param tokenFixPay Token específico da FixPay
     * @param callback Callback para retornar o resultado
     */
    private void consultarApiFixPayComToken(Context context, String tokenLink, String tokenFixPay, StatusPixCallback callback) {
        LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_INICIO", "[CONSULTAR_API_FIXPAY] Iniciando consulta à API FixPay");
        
        try {
            // Criar cliente OkHttp para requisição à API FixPay
            OkHttpClient clientFixPay = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Construir URL com TokenLink concatenado
            String url = "https://pix.fixpay.com.br:3467/v1/consult_pix/" + tokenLink;
            LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_URL", "[CONSULTAR_API_FIXPAY] URL: " + url);
            
            // Fazer requisição GET com o token específico da FixPay
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + tokenFixPay)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
            
            LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_HEADERS", "[CONSULTAR_API_FIXPAY] Headers: Authorization=Bearer *** , Content-Type=application/json");
            
            // Executar requisição de forma assíncrona
            clientFixPay.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_RESPOSTA", "[CONSULTAR_API_FIXPAY] Status: " + response.code() + " - " + response.message());
                    
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_SUCESSO", "[CONSULTAR_API_FIXPAY] Verificação realizada com sucesso");
                        
                        // LOG DETALHADO: Resposta completa da API FixPay
                        LogHelper.writePixResponse(context, "[CONSULTAR_API_FIXPAY] ===== RESPOSTA COMPLETA DA API FIXPAY =====");
                        LogHelper.writePixResponse(context, "[CONSULTAR_API_FIXPAY] Status HTTP: " + response.code() + " - " + response.message());
                        LogHelper.writePixResponse(context, "[CONSULTAR_API_FIXPAY] Headers: " + response.headers().toString());
                        LogHelper.writePixResponse(context, "[CONSULTAR_API_FIXPAY] Body JSON: " + responseBody);
                        LogHelper.writePixResponse(context, "[CONSULTAR_API_FIXPAY] =============================================");
                        
                        try {
                            // Processar resposta JSON
                            if (responseBody != null && !responseBody.trim().isEmpty()) {
                                JsonParser parser = new JsonParser();
                                JsonElement jsonElement = parser.parse(responseBody);
                                
                                if (jsonElement.isJsonObject()) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    
                                    // Extrair status do campo data.status (não do campo raiz)
                                    final String status;
                                    if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                                        JsonObject dataObject = jsonObject.getAsJsonObject("data");
                                        status = dataObject.has("status") ? dataObject.get("status").getAsString() : "";
                                    } else {
                                        status = "";
                                    }
                                    
                                    // LOG DETALHADO: Status extraído e análise
                                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_EXTRAIDO", "[CONSULTAR_API_FIXPAY] ===== ANÁLISE DO STATUS =====");
                                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_VALOR", "[CONSULTAR_API_FIXPAY] Status extraído de data.status: '" + status + "'");
                                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_TIPO", "[CONSULTAR_API_FIXPAY] Tipo do status: " + (status != null ? status.getClass().getSimpleName() : "null"));
                                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_VAZIO", "[CONSULTAR_API_FIXPAY] Status está vazio: " + (status == null || status.trim().isEmpty()));
                                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_CONCLUIDA", "[CONSULTAR_API_FIXPAY] Contém 'CONCLUIDA'? " + (status != null && status.toUpperCase().contains("CONCLUIDA")));
                                    LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_ANALISE", "[CONSULTAR_API_FIXPAY] ==============================");
                                    
                                    // Tornar responseBody final para uso na classe anônima
                                    final String finalResponseBody = responseBody;
                                    
                                    // FLUXO CORRETO: Verificar se precisa expirar antes de atualizar na ACAL
                                    if (status != null && status.toUpperCase().contains("CONCLUIDA")) {
                                        // Status é CONCLUIDA - atualizar diretamente na ACAL como PAGO
                                        LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_CONCLUIDA_DIRETO", "[CONSULTAR_API_FIXPAY] Status CONCLUIDA - atualizando diretamente na ACAL como PAGO");
                                        
                                        atualizarStatusPixAcal(context, tokenLink, "PAGO", new AtualizarStatusPixCallback() {
                                            @Override
                                            public void onSucesso(String resposta) {
                                                LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_ATUALIZAR_PAGO_SUCESSO", "[CONSULTAR_API_FIXPAY] Status PAGO atualizado com sucesso na API ACAL");
                                                callback.onSucesso(status, finalResponseBody);
                                            }
                                            
                                            @Override
                                            public void onErro(String mensagemErro) {
                                                LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro ao atualizar status PAGO na API ACAL: " + mensagemErro);
                                                // Mesmo com erro na atualização, continuar com o callback original
                                                callback.onSucesso(status, finalResponseBody);
                                            }
                                        });
                                    } else {
                                        // Status não é CONCLUIDA - expirar primeiro na FixPay, depois atualizar na ACAL
                                        LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_STATUS_NAO_CONCLUIDA", "[CONSULTAR_API_FIXPAY] Status não é CONCLUIDA ('" + status + "') - expirando PIX na FixPay primeiro");
                                        
                                        expiraPix(context, tokenLink, tokenFixPay, new ExpiracaoPixCallback() {
                                            @Override
                                            public void onSucesso(String respostaExpiracao) {
                                                LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_EXPIRACAO_SUCESSO", "[CONSULTAR_API_FIXPAY] PIX expirado com sucesso na FixPay - atualizando status na ACAL como EXPIRADO");
                                                
                                                // Após expirar com sucesso, atualizar na ACAL como EXPIRADO
                                                atualizarStatusPixAcal(context, tokenLink, "EXPIRADO", new AtualizarStatusPixCallback() {
                                                    @Override
                                                    public void onSucesso(String resposta) {
                                                        LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_ATUALIZAR_EXPIRADO_SUCESSO", "[CONSULTAR_API_FIXPAY] Status EXPIRADO atualizado com sucesso na API ACAL após expiração");
                                                        callback.onSucesso("EXPIRADO", finalResponseBody);
                                                    }
                                                    
                                                    @Override
                                                    public void onErro(String mensagemErro) {
                                                        LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro ao atualizar status EXPIRADO na API ACAL: " + mensagemErro);
                                                        // Mesmo com erro na atualização, continuar com o callback original
                                                        callback.onSucesso("EXPIRADO", finalResponseBody);
                                                    }
                                                });
                                            }
                                            
                                            @Override
                                            public void onErro(String mensagemErro) {
                                                LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro ao expirar PIX na FixPay: " + mensagemErro);
                                                
                                                // Mesmo com erro na expiração, atualizar na ACAL como EXPIRADO
                                                LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_EXPIRACAO_ERRO_CONTINUAR", "[CONSULTAR_API_FIXPAY] Erro na expiração, mas continuando com atualização na ACAL como EXPIRADO");
                                                
                                                atualizarStatusPixAcal(context, tokenLink, "EXPIRADO", new AtualizarStatusPixCallback() {
                                                    @Override
                                                    public void onSucesso(String resposta) {
                                                        LogHelper.writePixLog(context, "CONSULTAR_API_FIXPAY_ATUALIZAR_EXPIRADO_SUCESSO_APOS_ERRO", "[CONSULTAR_API_FIXPAY] Status EXPIRADO atualizado com sucesso na API ACAL mesmo após erro na expiração");
                                                        callback.onSucesso("EXPIRADO", finalResponseBody);
                                                    }
                                                    
                                                    @Override
                                                    public void onErro(String mensagemErroAcal) {
                                                        LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro ao atualizar status EXPIRADO na API ACAL após erro na expiração: " + mensagemErroAcal);
                                                        callback.onErro("Erro na expiração: " + mensagemErro + ". Erro na atualização ACAL: " + mensagemErroAcal);
                                                    }
                                                });
                                            }
                                        }, null); // null para loadingCallback pois não precisamos de loading aqui
                                    }
                                } else {
                                    LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Resposta não é um objeto JSON válido");
                                    LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Tipo da resposta: " + jsonElement.getClass().getSimpleName());
                                    callback.onErro("Resposta da API FixPay não é um objeto JSON válido");
                                }
                            } else {
                                LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Resposta vazia ou nula");
                                callback.onErro("Resposta vazia da API FixPay");
                            }
                        } catch (Exception e) {
                            LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro ao processar resposta JSON: " + e.getMessage());
                            LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Stack trace: " + android.util.Log.getStackTraceString(e));
                            callback.onErro("Erro ao processar resposta da API FixPay: " + e.getMessage());
                        }
                        
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sem corpo de erro";
                        LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro na verificação. Status: " + response.code() + ", Erro: " + errorBody);
                        callback.onErro("Erro na API FixPay. Status: " + response.code() + ", Resposta: " + errorBody);
                    }
                    
                    response.close();
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Falha na conexão: " + e.getMessage());
                    callback.onErro("Falha na conexão com a API FixPay: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            LogHelper.writePixError(context, "[CONSULTAR_API_FIXPAY] Erro ao iniciar verificação: " + e.getMessage());
            callback.onErro("Erro ao iniciar verificação: " + e.getMessage());
        }
    }

    /**
     * Expira um PIX existente na API FixPay
     * @param context Contexto da aplicação
     * @param tokenLink Token do PIX para expirar
     * @param tokenFixPay Token específico da FixPay para autorização
     * @param callback Callback para retornar o resultado
     * @param loadingCallback Callback para controlar o loading durante a operação
     */
    private void expiraPix(Context context, String tokenLink, String tokenFixPay, ExpiracaoPixCallback callback, PixLoadingCallback loadingCallback) {
        LogHelper.writePixLog(context, "EXPIRAR_PIX_INICIO", "[EXPIRAR_PIX] Iniciando expiração do PIX com TokenLink: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "...");
        
        // Chamar callback de início do loading
        if (loadingCallback != null) {
            loadingCallback.onInicio();
        }
        
        try {
            // Criar cliente OkHttp para requisição à API FixPay
            OkHttpClient clientFixPay = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // URL do endpoint de expiração com TokenLink como parâmetro
            String url = "https://pix.fixpay.com.br:3467/v1/expire/" + tokenLink;
            LogHelper.writePixLog(context, "EXPIRAR_PIX_URL", "[EXPIRAR_PIX] URL: " + url);
            
            // Criar RequestBody vazio (API não espera JSON no body)
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"), 
                ""
            );
            
            LogHelper.writePixLog(context, "EXPIRAR_PIX_BODY", "[EXPIRAR_PIX] Body: vazio (TokenLink na URL)");
            
            // Fazer requisição POST com o token específico da FixPay
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + tokenFixPay)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();
            
            LogHelper.writePixLog(context, "EXPIRAR_PIX_HEADERS", "[EXPIRAR_PIX] Headers: Authorization=Bearer ***, Content-Type=application/json");
            
            // Executar requisição de forma assíncrona
            clientFixPay.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    LogHelper.writePixLog(context, "EXPIRAR_PIX_RESPOSTA", "[EXPIRAR_PIX] Status: " + response.code() + " - " + response.message());
                    
                    String responseBody = response.body() != null ? response.body().string() : "";
                    
                    // LOG DETALHADO: Resposta completa da API FixPay
                    LogHelper.writePixResponse(context, "[EXPIRAR_PIX] ===== RESPOSTA COMPLETA DA API FIXPAY EXPIRAÇÃO =====");
                    LogHelper.writePixResponse(context, "[EXPIRAR_PIX] Status HTTP: " + response.code() + " - " + response.message());
                    LogHelper.writePixResponse(context, "[EXPIRAR_PIX] Headers: " + response.headers().toString());
                    LogHelper.writePixResponse(context, "[EXPIRAR_PIX] Body: " + responseBody);
                    LogHelper.writePixResponse(context, "[EXPIRAR_PIX] =================================================");
                    
                    if (response.code() == 200) {
                        LogHelper.writePixLog(context, "EXPIRAR_PIX_SUCESSO", "[EXPIRAR_PIX] PIX expirado com sucesso");
                        callback.onSucesso(responseBody);
                    } else {
                        LogHelper.writePixError(context, "[EXPIRAR_PIX] Erro na expiração. Status: " + response.code() + ", Resposta: " + responseBody);
                        callback.onErro("Erro ao expirar PIX. Status: " + response.code() + ", Resposta: " + responseBody);
                    }
                    
                    // Chamar callback de fim do loading
                    if (loadingCallback != null) {
                        loadingCallback.onFim();
                    }
                    
                    response.close();
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LogHelper.writePixError(context, "[EXPIRAR_PIX] Falha na conexão: " + e.getMessage());
                    callback.onErro("Falha na conexão com a API FixPay para expiração: " + e.getMessage());
                    
                    // Chamar callback de fim do loading
                    if (loadingCallback != null) {
                        loadingCallback.onFim();
                    }
                }
            });
            
        } catch (Exception e) {
            LogHelper.writePixError(context, "[EXPIRAR_PIX] Erro ao iniciar expiração: " + e.getMessage());
            callback.onErro("Erro ao iniciar expiração: " + e.getMessage());
            
            // Chamar callback de fim do loading
            if (loadingCallback != null) {
                loadingCallback.onFim();
            }
        }
    }

    /**
     * Verifica se um PIX pode ser gerado ou se já foi pago
     * Orquestra todo o fluxo: consulta ACAL → verifica FixPay → decide se pode gerar PIX
     * @param context Contexto da aplicação
     * @param numeroPedido Número do pedido para verificação
     * @param callback Callback para retorno do resultado final
     */
    public void verificarPixAntesDeGerar(Context context, String numeroPedido, PixLoadingCallback loadingCallback, VerificacaoPixCallback callback) {
        LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_INICIO", "[VERIFICAR_PIX_ANTES_GERAR] Iniciando verificação para pedido: " + numeroPedido);
        
        if (numeroPedido == null || numeroPedido.trim().isEmpty()) {
            LogHelper.writePixError(context, "[VERIFICAR_PIX_ANTES_GERAR] Número do pedido está vazio ou nulo");
            callback.onErro("Número do pedido não pode estar vazio");
            return;
        }
        
        // Primeiro passo: consultar a API da ACAL para obter o TokenLink
        consultarTokenPixMaisAtualAcal(context, numeroPedido, new ConsultaPixCallback() {
            @Override
            public void onSucesso(String tokenLink, String link, String status, String tipo) {
                LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_ACAL_SUCESSO", "[VERIFICAR_PIX_ANTES_GERAR] Token obtido da ACAL com sucesso");
                
                // Segundo passo: verificar o status na API FixPay
                verificarStatusPixFixPay(context, tokenLink, new StatusPixCallback() {
                    @Override
                    public void onSucesso(String statusFixPay, String resposta) {
                        LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_FIXPAY_SUCESSO", "[VERIFICAR_PIX_ANTES_GERAR] Status FixPay verificado: " + statusFixPay);
                        
                        // LOG DETALHADO: Análise da decisão final
                        LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_INICIO", "[VERIFICAR_PIX_ANTES_GERAR] ===== DECISÃO FINAL SOBRE GERAÇÃO DO PIX =====");
                        LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_STATUS", "[VERIFICAR_PIX_ANTES_GERAR] Status recebido da FixPay: '" + statusFixPay + "'");
                        LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_COMPARACAO", "[VERIFICAR_PIX_ANTES_GERAR] Comparando com 'CONCLUIDA' (ignoreCase): " + "CONCLUIDA".equalsIgnoreCase(statusFixPay));
                        LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_RESPOSTA", "[VERIFICAR_PIX_ANTES_GERAR] Resposta completa da FixPay: " + (resposta != null ? resposta.substring(0, Math.min(200, resposta.length())) + "..." : "null"));
                        
                        // Verificar se o PIX já foi concluído (status contém CONCLUIDA)
                        if (statusFixPay != null && statusFixPay.toUpperCase().contains("CONCLUIDA")) {
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_RESULTADO", "[VERIFICAR_PIX_ANTES_GERAR] *** DECISÃO: PIX JÁ FOI PAGO - NÃO GERAR NOVO PIX ***");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_MOTIVO", "[VERIFICAR_PIX_ANTES_GERAR] Motivo: Status 'CONCLUIDA' indica que o pagamento já foi realizado");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_ACAO", "[VERIFICAR_PIX_ANTES_GERAR] Ação: Bloqueando geração de novo PIX");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_FIM", "[VERIFICAR_PIX_ANTES_GERAR] ================================================");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_JA_PAGO", "[VERIFICAR_PIX_ANTES_GERAR] PIX já foi pago - Status: " + statusFixPay);
                            callback.onJaPago("Pagamento PIX para este pedido já foi efetuado!");
                        } else {
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_RESULTADO", "[VERIFICAR_PIX_ANTES_GERAR] *** DECISÃO: PIX EXISTENTE PRECISA SER EXPIRADO ANTES DE GERAR NOVO ***");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_MOTIVO", "[VERIFICAR_PIX_ANTES_GERAR] Motivo: Status '" + statusFixPay + "' não indica pagamento concluído, mas PIX existe");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_ACAO", "[VERIFICAR_PIX_ANTES_GERAR] Ação: Expirando PIX existente antes de permitir novo");
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_DECISAO_FINAL_FIM", "[VERIFICAR_PIX_ANTES_GERAR] ================================================");
                            
                            // Terceiro passo: expirar o PIX existente antes de permitir novo
                            LogHelper.writePixLog(context, "VERIFICAR_PIX_EXPIRAR_INICIO", "[VERIFICAR_PIX_ANTES_GERAR] Iniciando expiração do PIX existente");
                            
                            // Obter token FixPay usando ConsultarTokenFilial síncrono
                            try {
                                // Usar o método síncrono que funciona corretamente
                                String tokenFixPay = ConsultarTokenFilial(context, 1); // codfil padrão 1
                                LogHelper.writePixLog(context, "VERIFICAR_PIX_TOKEN_OBTIDO", "[VERIFICAR_PIX_ANTES_GERAR] Token FixPay obtido para expiração");
                                
                                // Chamar método de expiração
                                expiraPix(context, tokenLink, tokenFixPay, new ExpiracaoPixCallback() {
                                    @Override
                                    public void onSucesso(String resposta) {
                                        LogHelper.writePixLog(context, "VERIFICAR_PIX_EXPIRAR_SUCESSO", "[VERIFICAR_PIX_ANTES_GERAR] PIX expirado com sucesso, permitindo geração de novo PIX");
                                        callback.onPodeGerar();
                                    }
                                    
                                    @Override
                                    public void onErro(String mensagemErro) {
                                        LogHelper.writePixError(context, "[VERIFICAR_PIX_ANTES_GERAR] Erro ao expirar PIX: " + mensagemErro);
                                        callback.onErro("ERRO AO EXPIRAR PIX EXISTENTE!");
                                    }
                                }, loadingCallback);
                            } catch (IOException e) {
                                LogHelper.writePixError(context, "[VERIFICAR_PIX_ANTES_GERAR] Erro ao obter token FixPay: " + e.getMessage());
                                callback.onErro("ERRO AO OBTER TOKEN PARA EXPIRAÇÃO!");
                            }
                        }
                    }
                    
                    @Override
                    public void onErro(String mensagemErro) {
                        LogHelper.writePixError(context, "[VERIFICAR_PIX_ANTES_GERAR] Erro ao verificar status na FixPay: " + mensagemErro);
                        // Em caso de erro na verificação do status, permitir a geração do PIX
                        // para não bloquear o usuário
                        LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_ERRO_PERMITE", "[VERIFICAR_PIX_ANTES_GERAR] Erro na verificação, permitindo geração do PIX");
                        callback.onPodeGerar();
                    }
                });
            }
            
            @Override
            public void onErro(String mensagemErro) {
                LogHelper.writePixError(context, "[VERIFICAR_PIX_ANTES_GERAR] Erro ao consultar ACAL: " + mensagemErro);
                // Em caso de erro na consulta da ACAL, permitir a geração do PIX
                // para não bloquear o usuário
                LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_ERRO_ACAL_PERMITE", "[VERIFICAR_PIX_ANTES_GERAR] Erro na consulta ACAL, permitindo geração do PIX");
                callback.onPodeGerar();
            }
            
            @Override
            public void onTokenNaoEncontrado() {
                LogHelper.writePixLog(context, "VERIFICAR_PIX_ANTES_GERAR_TOKEN_NAO_ENCONTRADO", "[VERIFICAR_PIX_ANTES_GERAR] Token não encontrado na ACAL, permitindo geração do PIX");
                // Se não há token, significa que não há PIX anterior, pode gerar
                callback.onPodeGerar();
            }
        });
    }

    /**
     * Atualiza o status do PIX na API ACAL
     * @param context Contexto da aplicação
     * @param tokenLink Token do link PIX para atualizar
     * @param novoStatus Novo status a ser definido (PAGO ou EXPIRADO)
     * @param callback Callback para retorno do resultado
     */
    public void atualizarStatusPixAcal(Context context, String tokenLink, String novoStatus, AtualizarStatusPixCallback callback) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault()).format(new java.util.Date());
        LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_INICIO", "[ATUALIZAR_STATUS_PIX] [" + timestamp + "] Iniciando atualização de status para token: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "... Status: " + novoStatus);
        LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_OPERACAO_INICIO", "[ATUALIZAR_STATUS_PIX] ========== INÍCIO DA OPERAÇÃO DE ATUALIZAÇÃO DE STATUS ==========");
        
        try {
            // Usar token estático do ApiGemco
            String token = ApiGemco.token;
            LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_TOKEN", "[ATUALIZAR_STATUS_PIX] Usando token estático do ApiGemco");
            
            // Criar retrofit para API ACAL
            OkHttpClient clientAcal = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            Retrofit retrofitAcal = new Retrofit.Builder()
                    .baseUrl(ApiGemco.BASE_URL)
                    .client(clientAcal)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            ApiGemco apiGemco = retrofitAcal.create(ApiGemco.class);
            
            // Criar request com os dados
            AtualizarStatusPixRequest request = new AtualizarStatusPixRequest();
            request.setTokenLink(tokenLink);
            request.setStatus(novoStatus);
            
            LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_REQUEST", "[ATUALIZAR_STATUS_PIX] Request criado - TokenLink: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "..., Status: " + novoStatus);
            
            // Fazer chamada para API
            String authHeader = "Bearer " + token;
            Call<AtualizarStatusPixResponse> call = apiGemco.atualizarStatusPix(request, authHeader);
            
            String timestampReq = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault()).format(new java.util.Date());
            LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_CHAMADA", "[ATUALIZAR_STATUS_PIX] [" + timestampReq + "] Fazendo requisição HTTP POST para endpoint: api/v1/FixPay/AtualizarStatusTokenLink");
            LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_DADOS_ENVIO", "[ATUALIZAR_STATUS_PIX] Dados enviados: {tokenLink: '" + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "...', status: '" + novoStatus + "'}");
            
            call.enqueue(new Callback<AtualizarStatusPixResponse>() {
                @Override
                public void onResponse(Call<AtualizarStatusPixResponse> call, Response<AtualizarStatusPixResponse> response) {
                    String timestampResp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault()).format(new java.util.Date());
                    LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_RESPOSTA", "[ATUALIZAR_STATUS_PIX] [" + timestampResp + "] Resposta recebida - Status HTTP: " + response.code() + " - " + response.message());
                    
                    if (response.isSuccessful()) {
                        AtualizarStatusPixResponse responseBody = response.body();
                        String responseStr = responseBody != null ? responseBody.toString() : "";
                        boolean sucesso = responseBody != null ? responseBody.isSucesso() : false;
                        String mensagem = responseBody != null ? responseBody.getMensagem() : "";
                        
                        LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_SUCESSO", "[ATUALIZAR_STATUS_PIX] ✅ SUCESSO: Status atualizado com sucesso para: " + novoStatus);
                        LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_SUCESSO_DETALHES", "[ATUALIZAR_STATUS_PIX] ✅ Código HTTP: " + response.code() + ", Sucesso: " + sucesso + ", Mensagem: " + mensagem);
                        LogHelper.writePixResponse(context, "[ATUALIZAR_STATUS_PIX] Resposta completa: " + responseStr);
                        LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_OPERACAO_SUCESSO", "[ATUALIZAR_STATUS_PIX] ========== OPERAÇÃO CONCLUÍDA COM SUCESSO ==========");
                        
                        Log.d("FixPayHelper", "=== ✅ ATUALIZAÇÃO STATUS PIX SUCESSO ===");
                        Log.d("FixPayHelper", "Token: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "...");
                        Log.d("FixPayHelper", "Novo Status: " + novoStatus);
                        Log.d("FixPayHelper", "Código HTTP: " + response.code());
                        Log.d("FixPayHelper", "Sucesso: " + sucesso);
                        Log.d("FixPayHelper", "Mensagem: " + mensagem);
                        
                        callback.onSucesso(mensagem != null ? mensagem : responseStr);
                    } else {
                        String errorBody = "";
                        try {
                            errorBody = response.errorBody() != null ? response.errorBody().string() : "Sem corpo de erro";
                        } catch (IOException e) {
                            errorBody = "Erro ao ler corpo da resposta: " + e.getMessage();
                        }
                        
                        LogHelper.writePixError(context, "[ATUALIZAR_STATUS_PIX] ❌ FALHA: Erro na atualização. Status HTTP: " + response.code() + ", Erro: " + errorBody);
                        LogHelper.writePixError(context, "[ATUALIZAR_STATUS_PIX] ❌ Detalhes do erro: Código " + response.code() + " - " + response.message());
                        LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_OPERACAO_FALHA", "[ATUALIZAR_STATUS_PIX] ========== OPERAÇÃO FALHOU ==========");
                        
                        Log.e("FixPayHelper", "=== ❌ ERRO ATUALIZAÇÃO STATUS PIX ===");
                        Log.e("FixPayHelper", "Token: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "...");
                        Log.e("FixPayHelper", "Status HTTP: " + response.code());
                        Log.e("FixPayHelper", "Mensagem: " + response.message());
                        Log.e("FixPayHelper", "Corpo do erro: " + errorBody);
                        
                        callback.onErro("Erro ao atualizar status. Status: " + response.code() + ", Resposta: " + errorBody);
                    }
                }
                
                @Override
                public void onFailure(Call<AtualizarStatusPixResponse> call, Throwable t) {
                    String timestampFail = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault()).format(new java.util.Date());
                    LogHelper.writePixError(context, "[ATUALIZAR_STATUS_PIX] [" + timestampFail + "] ❌ FALHA DE CONEXÃO: " + t.getMessage());
                    LogHelper.writePixError(context, "[ATUALIZAR_STATUS_PIX] ❌ Tipo de exceção: " + t.getClass().getSimpleName());
                    LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_OPERACAO_FALHA_CONEXAO", "[ATUALIZAR_STATUS_PIX] ========== OPERAÇÃO FALHOU - ERRO DE CONEXÃO ==========");
                    
                    Log.e("FixPayHelper", "=== ❌ FALHA CONEXÃO ATUALIZAÇÃO STATUS PIX ===");
                    Log.e("FixPayHelper", "Token: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "...");
                    Log.e("FixPayHelper", "Tipo de erro: " + t.getClass().getSimpleName());
                    Log.e("FixPayHelper", "Mensagem: " + t.getMessage());
                    Log.e("FixPayHelper", "Stack trace: ", t);
                    
                    callback.onErro("Falha na conexão com a API ACAL: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            String timestampExc = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault()).format(new java.util.Date());
            LogHelper.writePixError(context, "[ATUALIZAR_STATUS_PIX] [" + timestampExc + "] ❌ EXCEÇÃO: Erro ao iniciar atualização: " + e.getMessage());
            LogHelper.writePixError(context, "[ATUALIZAR_STATUS_PIX] ❌ Tipo de exceção: " + e.getClass().getSimpleName());
            LogHelper.writePixLog(context, "ATUALIZAR_STATUS_PIX_OPERACAO_EXCECAO", "[ATUALIZAR_STATUS_PIX] ========== OPERAÇÃO FALHOU - EXCEÇÃO ==========");
            
            Log.e("FixPayHelper", "=== ❌ EXCEÇÃO ATUALIZAÇÃO STATUS PIX ===");
            Log.e("FixPayHelper", "Token: " + tokenLink.substring(0, Math.min(10, tokenLink.length())) + "...");
            Log.e("FixPayHelper", "Exceção: " + e.getClass().getSimpleName());
            Log.e("FixPayHelper", "Mensagem: " + e.getMessage());
            Log.e("FixPayHelper", "Stack trace: ", e);
            
            callback.onErro("Erro ao iniciar atualização: " + e.getMessage());
        }
    }

    /**
     * Interface para callback de atualização de status PIX
     */
    public interface AtualizarStatusPixCallback {
        void onSucesso(String resposta);
        void onErro(String mensagemErro);
    }

    /**
     * Interface para callback de expiração de PIX
     */
    public interface ExpiracaoPixCallback {
        void onSucesso(String resposta);
        void onErro(String mensagemErro);
    }

    /**
     * Interface para callback de loading durante operações PIX
     */
    public interface PixLoadingCallback {
        void onInicio();
        void onFim();
    }

}

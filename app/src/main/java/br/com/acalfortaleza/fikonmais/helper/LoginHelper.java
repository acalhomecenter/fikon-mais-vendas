package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Arquiteto;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Device;
import br.com.acalfortaleza.fikonmais.models.FasesObra;
import br.com.acalfortaleza.fikonmais.models.ProdutoDetalhe;
import br.com.acalfortaleza.fikonmais.models.UserFluig;
import br.com.acalfortaleza.fikonmais.models.Usuario;
import br.com.acalfortaleza.fikonmais.models.fikon.DadosLogin;
import br.com.acalfortaleza.fikonmais.models.fikon.UsuarioResponse;
import br.com.acalfortaleza.fikonmais.service.ApiFluig;
import br.com.acalfortaleza.fikonmais.service.ApiFikon;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import br.com.acalfortaleza.fikonmais.models.Senha;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class LoginHelper {

    private static final String NOME_ARQUIVO = "usuario.salvo";
    private static final int MODE = 0 ;
    private static final String CHAVE_CPF ="cpf" ;
    private static final String CHAVE_FILIAL ="filial" ;
    private static final String CHAVE_NASCIMENTO ="nascimento" ;
    private static final String CHAVE_NOME = "nome";
    private static final String CHAVE_CODIGO ="codigo" ;
    private static final String CHAVE_CODFIL ="codfil" ;
    private static final String CHAVE_EMAIL ="email";
    private static final String CHAVE_CELULAR ="celular";
    private static final String CHAVE_LOGIN ="login";
    private static final String CHAVE_GERENTE="isgerente";
    private static final String CHAVE_PERCDESC="descontopermitido";
    private static final String CHAVE_PERCFRETE="descontofrete";
    private static final String CHAVE_PERCMARGEM="percmargem";
    private static final String CHAVE_PRAZOENTREGA="prazoentrega";
    private static final String CHAVE_FILIALORIG ="filialorig" ;
    private static final String CHAVE_CODFILANT ="codfilant" ;
    private static final String CHAVE_FILIALORIGANT ="filialorigant" ;
    private static final String CHAVE_CODUSER ="coduser" ;
    private static final String CHAVE_URLMOTOR="urlmotor" ;
    private static final String CHAVE_CARGO="cargo" ;
    private static final String CHAVE_IMPRIMIR="imprimir";
    private static final String CHAVE_CONTINGENCIA="contingencia";
    private static final String CHAVE_TOKEN = "token";
    private static final String CHAVE_USUARIO = "usuario";
    private static final String CHAVE_CODIGO_ESTABELECIMENTO = "codigoEstabelecimento";
    private static final String CHAVE_TIPO_USUARIO = "tipoUsuario";
    private static final String CHAVE_CHAVE_ESTABELECIMENTO = "chaveEstabelecimento";
    private static final String CHAVE_BASE = "base";




    private Consultor consultor;
    private List<Consultor> retornoConsultor;
    private List<Senha> retornoSenha;
    private Senha senha;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private OkHttpClient client;
    private Retrofit retrofit;
    private OkHttpClient clientFikon;
    private Retrofit retrofitFikon;
    private Retrofit retrofitFluig;
    private OkHttpClient clientFluig;
    private List<CondPagamento> listaCondicoes;
    private String base;


    public LoginHelper (Context parametro){
        context = parametro;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,Context.MODE_PRIVATE);
        editor = preferences.edit();
        base = preferences.getString(CHAVE_BASE, FikonApiClient.DEFAULT_BASE_URL);

         client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
         retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        clientFikon = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        retrofitFikon = new Retrofit.Builder()
                .baseUrl(base)
                .client(clientFikon)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ApiFluig.KEY, ApiFluig.SECRET);
        consumer.setTokenWithSecret(ApiFluig.TOKEN, ApiFluig.TOKENSECRET);

        clientFluig = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();

        retrofitFluig = new Retrofit.Builder()
                .baseUrl(ApiFluig.BASE_URL)
                .client(clientFluig)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void SalvarUsuario(Usuario usuario, Consultor consultor){

        editor.putString(CHAVE_CPF,usuario.getCpf());
        editor.putString(CHAVE_FILIAL,usuario.getFilial());
        editor.putString(CHAVE_NASCIMENTO,usuario.getNascimento());
        editor.putString(CHAVE_NOME,consultor.nomeconsultor);
        editor.putString(CHAVE_CODIGO, usuario.getCodigo());
        editor.putInt(CHAVE_CODFIL,consultor.CodigoFilialConsultor);
        editor.putString(CHAVE_EMAIL,consultor.Email);
        editor.putString(CHAVE_CELULAR,consultor.Celular);
        editor.putString(CHAVE_LOGIN,usuario.getLoginFluig());
        editor.putString(CHAVE_GERENTE,consultor.isGerente);
        editor.putFloat(CHAVE_PERCDESC,consultor.PercDescPermitido);
        editor.putFloat(CHAVE_PERCFRETE,consultor.PercFretePermitido);
        editor.putFloat(CHAVE_PERCMARGEM,consultor.PercMargem);
        editor.putInt(CHAVE_PRAZOENTREGA,consultor.PrazoEntrega);
        editor.putString(CHAVE_FILIALORIG,consultor.FilialOrig);
        editor.putInt(CHAVE_CODFILANT,consultor.CodigoFilialConsultor);
        editor.putString(CHAVE_FILIALORIGANT,consultor.FilialOrig);
        editor.putInt(CHAVE_CODUSER,consultor.CodUser);
        editor.putString(CHAVE_URLMOTOR,consultor.urlMotor);
        editor.putString(CHAVE_CARGO,usuario.getCargo());
        editor.putString(CHAVE_IMPRIMIR,consultor.Imprimir);
        editor.putString(CHAVE_TOKEN, usuario.getToken());
        editor.putString(CHAVE_USUARIO, nomeUsuario(usuario));
        editor.putString(CHAVE_CODIGO_ESTABELECIMENTO, usuario.getCodigoEstabelecimento());
        editor.putString(CHAVE_TIPO_USUARIO, usuario.getTipoUsuario());
        editor.putInt(CHAVE_CHAVE_ESTABELECIMENTO, usuario.getChaveestabelecimento());
        editor.putString(CHAVE_BASE, base);

        editor.commit();

        logDadosUsuario();
    }

    public void SalvarUsuarioFikon(Usuario usuario) {
        String nome = usuario.getUsuario();
        if (nome == null || nome.trim().isEmpty()) {
            nome = usuario.getNome();
        }
        String filial = usuario.getEstabelecimento();
        if (filial == null || filial.trim().isEmpty()) {
            filial = usuario.getFilial();
        }

        int codigoFilial = parseInt(usuario.getCodigoEstabelecimento());
        if (codigoFilial == 0) {
            codigoFilial = 5;
        }

        editor.putString(CHAVE_CPF, usuario.getCpf());
        editor.putString(CHAVE_FILIAL, filial);
        editor.putString(CHAVE_NASCIMENTO, usuario.getNascimento());
        editor.putString(CHAVE_NOME, nome);
        // O código Fikon pode ter letras (ex.: V0042), mas ~26 telas fazem
        // Integer.parseInt(usuarioLogado.get("codigo")). O original fica em codigoFikon,
        // usado como vendedor nos orçamentos; "codigo" guarda só os dígitos.
        String codigoVendedor = usuario.getCodigo();
        editor.putString(FikonVendasClient.KEY_CODIGO_VENDEDOR, codigoVendedor);
        String codigoNumerico = codigoVendedor == null ? "" : codigoVendedor.replaceAll("\\D", "");
        if (codigoNumerico.isEmpty() || codigoNumerico.length() > 9) {
            codigoNumerico = String.valueOf(usuario.getChaveUsuario() > 0 ? usuario.getChaveUsuario() : 0);
        }
        editor.putString(CHAVE_CODIGO, codigoNumerico);
        editor.putInt(CHAVE_CODFIL, codigoFilial);
        editor.putString(CHAVE_EMAIL, usuario.getEmail());
        editor.putString(CHAVE_CELULAR, usuario.getCelular());
        if (usuario.getLoginFluig() != null && !usuario.getLoginFluig().trim().isEmpty()) {
            editor.putString(CHAVE_LOGIN, usuario.getLoginFluig());
        } else {
            editor.remove(CHAVE_LOGIN);
        }
        editor.putString(CHAVE_GERENTE, usuario.getIsgerente());
        editor.putFloat(CHAVE_PERCDESC, (float) usuario.getPercdesconto());
        editor.putFloat(CHAVE_PERCFRETE, (float) usuario.getPercfrete());
        editor.putFloat(CHAVE_PERCMARGEM, (float) usuario.getMargem());
        editor.putInt(CHAVE_PRAZOENTREGA, 2);
        editor.putString(CHAVE_FILIALORIG, filial);
        editor.putInt(CHAVE_CODFILANT, codigoFilial);
        editor.putString(CHAVE_FILIALORIGANT, filial);
        editor.putInt(CHAVE_CODUSER, usuario.getChaveUsuario());
        editor.remove(CHAVE_URLMOTOR);
        editor.putString(CHAVE_CARGO, usuario.getCargo());
        editor.putString(CHAVE_IMPRIMIR, usuario.getImprimeorc());
        editor.putString(CHAVE_TOKEN, usuario.getToken());
        editor.putString(CHAVE_USUARIO, nomeUsuario(usuario));
        editor.putString(CHAVE_CODIGO_ESTABELECIMENTO, usuario.getCodigoEstabelecimento());
        editor.putString(CHAVE_TIPO_USUARIO, usuario.getTipoUsuario());
        editor.putInt(CHAVE_CHAVE_ESTABELECIMENTO, usuario.getChaveestabelecimento());
        editor.putString(CHAVE_BASE, base);
        editor.apply();

        logDadosUsuario();
    }

    private String nomeUsuario(Usuario usuario) {
        String nome = usuario.getUsuario();
        return nome == null || nome.trim().isEmpty() ? usuario.getNome() : nome;
    }

    public void DefinirBaseFikon(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a URL da base Fikon.");
        }

        String normalizedBase = baseUrl.trim();
        if (!normalizedBase.endsWith("/")) {
            normalizedBase += "/";
        }

        Retrofit configuredRetrofit = new Retrofit.Builder()
                .baseUrl(normalizedBase)
                .client(clientFikon)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        base = normalizedBase;
        retrofitFikon = configuredRetrofit;
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.w("LoginHelper", "Fikon user code is not numeric");
            return 0;
        }
    }


    public void logDadosUsuario() {
        HashMap<String, String> u = GetUsuarioSalvo();
        Log.d("USUARIO_LOGADO", "=================== DADOS DO USUÁRIO LOGADO ===================");
        Log.d("USUARIO_LOGADO", "Nome: " + u.get(CHAVE_NOME));
        Log.d("USUARIO_LOGADO", "CPF: " + u.get(CHAVE_CPF));
        Log.d("USUARIO_LOGADO", "Cargo: " + u.get(CHAVE_CARGO));
        Log.d("USUARIO_LOGADO", "Código Consultor: " + u.get(CHAVE_CODIGO));
        Log.d("USUARIO_LOGADO", "Código Usuário (coduser): " + u.get(CHAVE_CODUSER));
        Log.d("USUARIO_LOGADO", "Filial (codfil): " + u.get(CHAVE_CODFIL) + " (" + u.get(CHAVE_FILIALORIG) + ")");
        Log.d("USUARIO_LOGADO", "Filial Usuário: " + u.get(CHAVE_FILIAL));
        Log.d("USUARIO_LOGADO", "É Gerente: " + u.get(CHAVE_GERENTE));
        Log.d("USUARIO_LOGADO", "Email: " + u.get(CHAVE_EMAIL));
        Log.d("USUARIO_LOGADO", "Celular: " + u.get(CHAVE_CELULAR));
        Log.d("USUARIO_LOGADO", "Login Fluig: " + u.get(CHAVE_LOGIN));
        Log.d("USUARIO_LOGADO", "Data Nascimento: " + u.get(CHAVE_NASCIMENTO));
        Log.d("USUARIO_LOGADO", "Perc Desc Permitido: " + u.get(CHAVE_PERCDESC));
        Log.d("USUARIO_LOGADO", "Perc Frete Permitido: " + u.get(CHAVE_PERCFRETE));
        Log.d("USUARIO_LOGADO", "Perc Margem: " + u.get(CHAVE_PERCMARGEM));
        Log.d("USUARIO_LOGADO", "Prazo Entrega: " + u.get(CHAVE_PRAZOENTREGA));
        Log.d("USUARIO_LOGADO", "URL Motor: " + u.get(CHAVE_URLMOTOR));
        Log.d("USUARIO_LOGADO", "Imprimir: " + u.get(CHAVE_IMPRIMIR));
        Log.d("USUARIO_LOGADO", "=============================================================");
    }


    public void AlterarUsuarioDelivery(boolean delivery, int codfil, String filial){

        if(delivery){
            editor.putInt(CHAVE_CODFIL,900);
            editor.putString(CHAVE_FILIALORIG,"CENTRAL DE VENDAS");
            editor.commit();
        }else{
            editor.putInt(CHAVE_CODFIL,codfil);
            editor.putString(CHAVE_FILIALORIG,filial);
            editor.commit();
        }



    }



    public void AlterarContigencia(boolean contigencia){

        if(contigencia){
            editor.putString(CHAVE_CONTINGENCIA,"S");
            editor.commit();
        }else{
            editor.putString(CHAVE_CONTINGENCIA,"N");
            editor.commit();
        }



    }

    public HashMap<String,String> GetUsuarioSalvo(){
        HashMap<String,String> dadosUsuario = new HashMap<>();
        dadosUsuario.put(CHAVE_NOME,preferences.getString(CHAVE_NOME,null));
        dadosUsuario.put(CHAVE_CODIGO, getPreferenceString(CHAVE_CODIGO, "0"));
        dadosUsuario.put(CHAVE_CPF,preferences.getString(CHAVE_CPF,null));
        dadosUsuario.put(CHAVE_FILIAL,preferences.getString(CHAVE_FILIAL,null));
        dadosUsuario.put(CHAVE_NASCIMENTO,preferences.getString(CHAVE_NASCIMENTO,null));
        dadosUsuario.put(CHAVE_CODFIL,String.valueOf(preferences.getInt(CHAVE_CODFIL,5)));
        dadosUsuario.put(CHAVE_EMAIL,preferences.getString(CHAVE_EMAIL,null));
        dadosUsuario.put(CHAVE_CELULAR,preferences.getString(CHAVE_CELULAR,null));
        dadosUsuario.put(CHAVE_LOGIN,preferences.getString(CHAVE_LOGIN,null));
        dadosUsuario.put(CHAVE_GERENTE,preferences.getString(CHAVE_GERENTE,null));
        dadosUsuario.put(CHAVE_PERCDESC,String.valueOf(preferences.getFloat(CHAVE_PERCDESC,0.00f)));
        dadosUsuario.put(CHAVE_PERCFRETE,String.valueOf(preferences.getFloat(CHAVE_PERCFRETE,0.00f)));
        dadosUsuario.put(CHAVE_PERCMARGEM,String.valueOf(preferences.getFloat(CHAVE_PERCMARGEM,0.00f)));
        dadosUsuario.put(CHAVE_PRAZOENTREGA,String.valueOf(preferences.getInt(CHAVE_PRAZOENTREGA, 2)));
        dadosUsuario.put(CHAVE_FILIALORIG,preferences.getString(CHAVE_FILIALORIG,null));
        dadosUsuario.put(CHAVE_CODFILANT,String.valueOf(preferences.getInt(CHAVE_CODFILANT,5)));
        dadosUsuario.put(CHAVE_FILIALORIGANT,preferences.getString(CHAVE_FILIALORIGANT,null));
        dadosUsuario.put(CHAVE_CODUSER,String.valueOf(preferences.getInt(CHAVE_CODUSER,0)));
        dadosUsuario.put(CHAVE_URLMOTOR,preferences.getString(CHAVE_URLMOTOR,null));
        // O login Fikon não traz cargo; várias telas fazem get("cargo").equals(...).
        dadosUsuario.put(CHAVE_CARGO,preferences.getString(CHAVE_CARGO,""));
        dadosUsuario.put(CHAVE_IMPRIMIR,preferences.getString(CHAVE_IMPRIMIR,null));
        dadosUsuario.put(CHAVE_TOKEN, preferences.getString(CHAVE_TOKEN, null));
        dadosUsuario.put(CHAVE_TIPO_USUARIO, preferences.getString(CHAVE_TIPO_USUARIO, null));
        dadosUsuario.put(CHAVE_USUARIO, preferences.getString(
                CHAVE_USUARIO, preferences.getString(CHAVE_NOME, null)));
        dadosUsuario.put(CHAVE_CODIGO_ESTABELECIMENTO, preferences.getString(
                CHAVE_CODIGO_ESTABELECIMENTO, preferences.getString(CHAVE_FILIALORIG, null)));
        dadosUsuario.put(CHAVE_CHAVE_ESTABELECIMENTO,
                String.valueOf(preferences.getInt(CHAVE_CHAVE_ESTABELECIMENTO, 0)));
        dadosUsuario.put(CHAVE_BASE, preferences.getString(CHAVE_BASE, FikonApiClient.DEFAULT_BASE_URL));


        return dadosUsuario;
    }

    private String getPreferenceString(String key, String defaultValue) {
        Object value = preferences.getAll().get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }


    public UserFluig ConsultaFluig (String cpf) throws IOException, XmlPullParserException, ParseException {

        final ApiFluig service = retrofitFluig.create(ApiFluig.class);

        final Call<UserFluig> request= service.GetUserFluig(cpf);

        UserFluig userFluig = new UserFluig();

        try {
           userFluig = request.execute().body();
            return userFluig;
        } catch (Exception e) {
            return null;
        }






    }

    public Usuario EfetuarLogin(String login, String senha, String aplicacao)
            throws IOException, XmlPullParserException, ParseException {
        final ApiFikon service = retrofitFikon.create(ApiFikon.class);
        DadosLogin dados = new DadosLogin(login, senha, aplicacao);
        Call<UsuarioResponse> requestUsuario = service.efetuarLogin(dados);

        Response<UsuarioResponse> response = requestUsuario.execute();
        if (response.isSuccessful() && response.body() != null) {
            UsuarioResponse retorno = response.body();
            if (retorno.isSucesso() && retorno.getDados() != null) {
                return retorno.getDados();
            }
        }

        return null;
    }


    public String ConsultarSenha(String senha ) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<String> requestAcesso= service.GetVerificarSenha(senha, "Bearer " + service.token);


        return  requestAcesso.execute().body();

    }


    public List<String> ConsultaAprovador( ) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<String>> request= service.getAprovador( "Bearer " + service.token);


        return  request.execute().body();

    }


    public String SetDevice(Device device ) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<String> requestAcesso= service.setDevice(device, "Bearer " + service.token);


        return  requestAcesso.execute().body();

    }


    public Consultor ConsultarConsultor (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetConsultor(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

                consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }



    public Consultor ConsultarConsultorAcumulado (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetConsultorAcumulado(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

            consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }


    public Consultor ConsultarGestorAcumulado (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetGestorAcumulado(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

            consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }


    public Consultor ConsultarConsultorDia (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetConsultorDia(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

            consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }

    public Consultor ConsultarGestorDia (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetGestorDia(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

            consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }


    public Consultor ConsultarConsultorOntem (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetConsultorOntem(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

            consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }


    public Consultor ConsultarGestorOntem (String cpf, String filial, String nome) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Consultor>> requestConsultor = service.GetGestorOntem(cpf, filial, "Bearer " + service.token);


        retornoConsultor = requestConsultor.execute().body();

        if (retornoConsultor!=null) {

            if(retornoConsultor.get(0).CodigoConsultor!=0){

                for (Consultor c : retornoConsultor) {

                    consultor = c;

                }
            }else{



                consultor = Eauxiliar(nome,cpf,filial,retornoConsultor.get(0).CodigoFilialConsultor,retornoConsultor.get(0).FilialOrig);

            }

        }else {

            consultor = NaoEConsultor(nome,cpf,filial);


        }

        return  consultor;

    }





    public Consultor NaoEConsultor(String nome, String cpf, String filial){

        consultor = new Consultor();
        consultor.CodigoConsultor = 0;
        consultor.CodigoFilialConsultor = 5;
        consultor.nomeconsultor = nome;
        consultor.Cpf = cpf;
        consultor.Meta = 0.00;
        consultor.Desconto = 0.00;
        consultor.DiasDeVenda = 0;
        consultor.Devolucao = 0.00;
        consultor.Frete = 0.00;
        consultor.DiasUteis = 0;
        consultor.PercDesconto = "0";
        consultor.PercentDevolucao = "0";
        consultor.PercentualMeta = "0";
        consultor.Projecao = 0.00;
        consultor.ProjecaoEmValor = 0.00;
        consultor.MetaDoDia = 0.00;
        consultor.VendaBruta = 0.00;
        consultor.VendaDiaAnterior = 0.00;
        consultor.VendaLiquida = 0.00;
        consultor.VendaDoDia=0.00;
        consultor.PercentualMetaDia="0";
        consultor.Filial = filial;
        consultor.FilialOrig = "";
        consultor.MargemDia= "0";
        consultor.MargemMes= "0";
        consultor.MargemDiaAnterior= "0";
        consultor.ItensPorVendaDia= 0.00;
        consultor.QuantidadeDia= 0.00;
        consultor.AtendimentosDia= 0.00;
        consultor.TicketDia= 0.00;
        consultor.PrecoDia= 0.00;
        consultor.ItensPorVendaMes= 0.00;
        consultor.QuantidadeMes= 0.00;
        consultor.TicketMes= 0.00;
        consultor.PrecoMes= 0.00;
        consultor.Atdpf = 0;
        consultor.Atdpj = 0;
        consultor.Email = "";
        consultor.Celular = "";
        consultor.isGerente="Não";
        consultor.PercDescPermitido = 0.00f;
        consultor.PercFretePermitido= 0.00f;
        consultor.PercMargem = 0.00f;
        consultor.PrazoEntrega = 2;
        consultor.CodUser=0;
        consultor.urlMotor="";
        consultor.vlFrete= 0.00;
        consultor.freteOrig=0.00;
        consultor.percFrete="0";
        consultor.qtdeRetira=0;
        consultor.qtdeAgendado=0;
        consultor.percAgendado="0";
        consultor.Imprimir="N";


        return consultor;


    }


    public Consultor Eauxiliar(String nome, String cpf, String filial, int codfil, String filorig){

        consultor = new Consultor();
        consultor.CodigoConsultor = 0;
        consultor.CodigoFilialConsultor = codfil;
        consultor.nomeconsultor = nome;
        consultor.Cpf = cpf;
        consultor.Meta = 0.00;
        consultor.Desconto = 0.00;
        consultor.DiasDeVenda = 0;
        consultor.Devolucao = 0.00;
        consultor.Frete = 0.00;
        consultor.DiasUteis = 0;
        consultor.PercDesconto = "0";
        consultor.PercentDevolucao = "0";
        consultor.PercentualMeta = "0";
        consultor.Projecao = 0.00;
        consultor.ProjecaoEmValor = 0.00;
        consultor.MetaDoDia = 0.00;
        consultor.VendaBruta = 0.00;
        consultor.VendaDiaAnterior = 0.00;
        consultor.VendaLiquida = 0.00;
        consultor.VendaDoDia=0.00;
        consultor.PercentualMetaDia="0";
        consultor.Filial = filial;
        consultor.FilialOrig = filorig;
        consultor.MargemDia= "0";
        consultor.MargemMes= "0";
        consultor.MargemDiaAnterior= "0";
        consultor.ItensPorVendaDia= 0.00;
        consultor.QuantidadeDia= 0.00;
        consultor.AtendimentosDia= 0.00;
        consultor.TicketDia= 0.00;
        consultor.PrecoDia= 0.00;
        consultor.ItensPorVendaMes= 0.00;
        consultor.QuantidadeMes= 0.00;
        consultor.TicketMes= 0.00;
        consultor.PrecoMes= 0.00;
        consultor.Atdpf = 0;
        consultor.Atdpj = 0;
        consultor.Email = "";
        consultor.Celular = "";
        consultor.isGerente="Não";
        consultor.PercDescPermitido = 0.00f;
        consultor.PercFretePermitido= 0.00f;
        consultor.PercMargem = 0.00f;
        consultor.PrazoEntrega = 2;
        consultor.CodUser=0;
        consultor.urlMotor="";
        consultor.vlFrete= 0.00;
        consultor.freteOrig=0.00;
        consultor.percFrete="0";
        consultor.qtdeRetira=0;
        consultor.qtdeAgendado=0;
        consultor.percAgendado="0";
        consultor.Imprimir="N";
        consultor.Margem = 0.00;



        return consultor;


    }


    public Senha GetSenha (String cpf) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Senha>> requestSenha = service.GetSenha(cpf,"Bearer " + service.token);


        retornoSenha = requestSenha.execute().body();

        if(requestSenha.toString().equals("[]") ){

        }else{

            for (Senha s : retornoSenha) {

                senha = s;

            }
        }

        return senha;

    }

    public List<CondPagamento> getListaCondicoes( int codfil) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            return FikonVendasClient.listarCondicoesPagamento();
        }



        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<CondPagamento>> requestCondicoes = service.GetCondPagamento( codfil, "Bearer " + service.token);

        listaCondicoes = requestCondicoes.execute().body();



        return listaCondicoes;

    }


    public List<FasesObra> GetFases() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<FasesObra>> request = service.GetFasesOrcamento(  "Bearer " + service.token);

        return request.execute().body();

    }





    public List<Arquiteto> GetArquitetos() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Arquiteto>> request = service.GetArquitetosOrcamento(  "Bearer " + service.token);

        return request.execute().body();

    }






}

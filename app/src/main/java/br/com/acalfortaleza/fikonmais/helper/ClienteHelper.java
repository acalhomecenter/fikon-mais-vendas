package br.com.acalfortaleza.fikonmais.helper;

import android.util.Log;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Cargo;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.Credz.Invitation;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Segmento;
import br.com.acalfortaleza.fikonmais.models.Setor;
import br.com.acalfortaleza.fikonmais.models.TheChecker;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEmail;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import br.com.acalfortaleza.fikonmais.service.ApiTheCheck;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;





public class ClienteHelper  {
    private android.content.Context fikonContext;

    public ClienteHelper(android.content.Context context) {
        this();
        this.fikonContext = context == null ? null : context.getApplicationContext();
    }


    private Retrofit retrofit;
    private Retrofit retrofitThechecker;
    private List<Cliente> listaCliente;
    private List<Contato> listaContato;
    private List<Enderecos> listaEnderecos;
    private OkHttpClient client;
    private String retorno;

    public  ClienteHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitThechecker = new Retrofit.Builder()
                .baseUrl(ApiTheCheck.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


    /** true quando as chamadas vão para a Fikon (helper criado com Context). */
    public boolean isFikon() {
        return fikonContext != null;
    }

    public List<Contato> ConsultarContatoCgccpf(String cgccpf) throws IOException, XmlPullParserException, ParseException {
        if (fikonContext != null) {
            // Na Fikon o contato não tem CPF próprio: não há como achá-lo pelo documento.
            return new java.util.ArrayList<>();
        }

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Contato>> requestContato = service.GetContatoCadastrado( cgccpf, "Bearer " + service.token);

        listaContato = requestContato.execute().body();

        return listaContato;

    }

    public List<Cliente> ConsultarClientesCgccpf(String cgccpf, int codfil, String cpfConsultor) throws IOException, XmlPullParserException, ParseException {
        if (fikonContext != null) {
            return FikonApiClient.consultarClientes(fikonContext, cgccpf);
        }

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cliente>> requestCliente = service.GetClienteCgccpf( cgccpf, codfil, cpfConsultor, "Bearer " + service.token);

        listaCliente = requestCliente.execute().body();

        return listaCliente;

    }


    public List<Cliente> ConsultarClientesNome(String nomeCli) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cliente>> requestCliente = service.GetClienteNome( nomeCli, "Bearer " + service.token);

        listaCliente = requestCliente.execute().body();

        return listaCliente;

    }

    public List<Cliente> ConsultarEspecificador(String nome, String codvendr) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cliente>> requestCliente = service.GetEspecificador( nome, codvendr, "Bearer " + service.token);

        listaCliente = requestCliente.execute().body();

        return listaCliente;

    }

    public List<Cliente> ConsultarClientesCodvendr(int codcli, String natjur, String situacao) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cliente>> requestCliente = service.GetClienteCodvendr( codcli, natjur, situacao,"Bearer " + service.token);

        listaCliente = requestCliente.execute().body();

        return listaCliente;

    }


    public ValidacaoEmail validarEmail(String email) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<ValidacaoEmail> request = service.validarEmail(  email  , "Bearer " + service.token);

        ValidacaoEmail validacaoEmail = request.execute().body();

        return validacaoEmail;

    }


    public Contato TransformaClienteEmContato(Cliente cliente){

        Contato contato = new Contato();

        contato.setNomeContato(cliente.getNomcli());
        contato.setDtnascimento(cliente.getDtnasc());
        contato.setCodend(0);
        contato.setDdd(cliente.getDdd());
        contato.setFone(cliente.getFone());
        contato.setDddFone1(cliente.getDddFone1());
        contato.setFone1(cliente.getFone1());
        contato.setDddFone2(cliente.getDddFone2());
        contato.setFone2(cliente.getFone2());
        contato.setDddCelular(cliente.getDddCelular());
        contato.setCelular(cliente.getCelular());
        contato.setEmail(cliente.getEmail());
        contato.setCgccpf(cliente.getCgccpf());

        return contato;
    }


    public String SetCliente(Cliente cliente, String tipo) throws IOException, XmlPullParserException, ParseException {

        if (fikonContext != null) {
            return FikonApiClient.salvarCliente(fikonContext, cliente);
        }


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> postCliente = service.saveClienteOrcamento( cliente,tipo, "Bearer " + service.token);
        try {
            Log.d("ClienteCadastroDebug",
                    "POST saveClienteOrcamento"
                            + " | tipo=" + tipo
                            + " | payload=" + new Gson().toJson(cliente));
        } catch (Exception ex) {
            Log.e("ClienteCadastroDebug", "Falha ao serializar payload do cliente", ex);
        }

        Response<String> response = postCliente.execute();
        String responseBody = response.body();
        String errorBody = null;
        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (Exception ex) {
            Log.e("ClienteCadastroDebug", "Falha ao ler errorBody do cadastro de cliente", ex);
        }

        Log.d("ClienteCadastroDebug",
                "Resposta saveClienteOrcamento"
                        + " | code=" + response.code()
                        + " | successful=" + response.isSuccessful()
                        + " | body=" + String.valueOf(responseBody)
                        + " | errorBody=" + String.valueOf(errorBody));

        return responseBody;

    }


    public String SetContato(Contato contato, String tipo) throws IOException, XmlPullParserException, ParseException {
        if (fikonContext != null) {
            if (!"ADICIONAR".equals(tipo)) {
                return FikonApiClient.MENSAGEM_CONTATO_SO_INCLUSAO;
            }
            return FikonApiClient.cadastrarContato(fikonContext, contato);
        }

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> postContato = service.saveContato( contato,tipo, "Bearer " + service.token);

        return postContato.execute().body();

    }


    public List<Cargo> ConsultarCargos() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cargo>> requestCargos = service.GetCargos(  "Bearer " + service.token);

        return requestCargos.execute().body();

    }


    public List<Segmento> ConsultarSegmentos() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Segmento>> requestSegmentos = service.GetSegmentos(  "Bearer " + service.token);

        return requestSegmentos.execute().body();

    }


    public List<Cargo> ConsultarCargosContatos() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cargo>> requestCargos = service.GetCargosContatos(  "Bearer " + service.token);

        return requestCargos.execute().body();

    }



    public List<Setor> ConsultarSetor() throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Setor>> requestSetor = service.GetSetor(  "Bearer " + service.token);

        return requestSetor.execute().body();

    }

    public Enderecos ConsultarEndereco(String cep) throws IOException, XmlPullParserException, ParseException {
        if (fikonContext != null) {
            return FikonApiClient.consultarEndereco(fikonContext, cep);
        }



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<Enderecos> requestEndereco = service.GetEnderecoConsultado(  cep ,"Bearer " + service.token);

        return requestEndereco.execute().body();

    }

    public String SetEndereco(Enderecos endereco, String tipo) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> postEndereco= service.saveEndereco( endereco,tipo, "Bearer " + service.token);

        return postEndereco.execute().body();

    }




    public boolean ValidadorCpf (String cpf) {

        int cont = 0;
        int cont1 = 10;
        int n1 = 0;
        int dig1 = 0;
        int dig2 = 0;


        while (cont < 9) {

            n1 = Integer.parseInt(cpf.substring(cont, cont + 1)) * cont1;
            dig1 = n1 + dig1;
            cont = cont + 1;
            cont1 = cont1 - 1;

        }

        if ((dig1 % 11) < 2) {
            dig1 = 0;
            if (dig1 != Integer.parseInt(cpf.substring(9, 10))) {
                return false;
            }


        }

        if ((dig1 % 11) >= 2) {
            dig1 = (11 - (dig1 % 11));
            if (dig1 != Integer.parseInt(cpf.substring(9, 10))) {
                return false;
            }


        }

        cont = 0;
        cont1 = 11;
        while (cont < 10) {

            n1 = Integer.parseInt(cpf.substring(cont, cont+1)) * cont1;
            cont = cont + 1;
            cont1 = cont1 - 1;
            dig2 = dig2 + n1;

        }

        if ((dig2 % 11) < 2) {
            dig2 = 0;
            if (dig2 != Integer.parseInt(cpf.substring(10, 11))) {
                return false;
            }


        }

        if ((dig2 % 11) >= 2) {
            dig2 = (11 - (dig2 % 11));
            if (dig2 != Integer.parseInt(cpf.substring(10, 11))) {
                return false;
            }


        }

        if (dig1 == Integer.parseInt(cpf.substring(9, 10)) && dig2 == Integer.parseInt(cpf.substring(10, 11))) {

            return true;
        } else {

            return false;


        }

    }

    public boolean ValidarCnpj (String cnpj){

        char dig13, dig14;
        int sm, i, r, num, peso;


        try {

            sm = 0;
            peso = 2;
            for (i=11; i>=0; i--) {

                num = (int)(cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else dig13 = (char)((11-r) + 48);

// Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i=12; i>=0; i--) {
                num = (int)(cnpj.charAt(i)- 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else dig14 = (char)((11-r) + 48);

// Verifica se os dígitos calculados conferem com os dígitos informados.
            if ((dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }


}

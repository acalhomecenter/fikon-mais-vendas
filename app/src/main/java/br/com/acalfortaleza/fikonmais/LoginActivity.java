package br.com.acalfortaleza.fikonmais;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;

import br.com.acalfortaleza.fikonmais.dao.UsuarioDao;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.BiometricAuthHelper;
import br.com.acalfortaleza.fikonmais.helper.SecureCredentialStore;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Senha;
import br.com.acalfortaleza.fikonmais.models.UserFluig;
import br.com.acalfortaleza.fikonmais.models.Usuario;

public class LoginActivity extends AppCompatActivity {

    //private static final String BASE_URL_FIKON = "https://dacal.fikon.com.br";
    private static final String BASE_URL_FIKON = "https://acal.fikon.com.br";

    private Button btlogar;
    private TextView esqueciSenha;
    private ImageView logo;
    private EditText edtLogin;
    private EditText edtBase;
    private EditText edtSenha;
    private RadioButton loginBiometria;
    private Handler handler;
    private ProgressDialog dialog;
    private Switch salvarSenha;
    private UsuarioDao dao;

    private Usuario usuarioBanco;
    private Consultor consultor;
    private Senha senha;
    private UserFluig userFluig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(Color.BLACK);
        dao = new UsuarioDao(LoginActivity.this);
        handler = new Handler();

        edtBase = findViewById(R.id.edtBase);
        edtBase.setText(BASE_URL_FIKON);
        edtBase.setEnabled(false);
        edtBase.setKeyListener(null);

        edtLogin = findViewById(R.id.edtLogin);
        edtSenha = findViewById(R.id.edtSenha);
        salvarSenha = findViewById(R.id.salvarSenha);
        loginBiometria = findViewById(R.id.loginBiometria);

        boolean hasBiometricCredentials = SecureCredentialStore.hasCredentials(this);
        boolean biometricAvailable = BiometricAuthHelper.isAvailable(this);
        loginBiometria.setChecked(hasBiometricCredentials && biometricAvailable);
        loginBiometria.setEnabled(biometricAvailable);
        if (hasBiometricCredentials && biometricAvailable) {
            edtLogin.setText(SecureCredentialStore.readLogin(this));
        } else if (hasBiometricCredentials) {
            SecureCredentialStore.clear(this);
        }

        loginBiometria.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked && SecureCredentialStore.hasCredentials(LoginActivity.this)) {
                SecureCredentialStore.clear(LoginActivity.this);
            }
        });

        if (hasBiometricCredentials && biometricAvailable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    solicitarBiometria();
                }
            }, 300);
        }

        logo = findViewById(R.id.logo);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logoapp_branca);
        logo.setImageBitmap(bitmap);

        esqueciSenha = findViewById(R.id.esqueciSenha);
        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar o esqueci senha
            }
        });

        btlogar = findViewById(R.id.btn_logar);
        btlogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginBiometria.isChecked()
                        && SecureCredentialStore.hasCredentials(LoginActivity.this)
                        && edtSenha.getText().toString().isEmpty()) {
                    solicitarBiometria();
                    return;
                }

                if (edtLogin.getText().toString().trim().equals("") || edtSenha.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Informe o login e senha para entrar!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Acessando...");
                dialog.setCancelable(false);
                dialog.show();
                final boolean saveBiometricLogin = loginBiometria.isChecked()
                        && BiometricAuthHelper.isAvailable(LoginActivity.this);

                new Thread() {
                    public void run() {
                        final String login = edtLogin.getText().toString().trim();
                        final String senha = edtSenha.getText().toString();
                        final String base = BASE_URL_FIKON;

                        try {
                            final LoginHelper helper = new LoginHelper(LoginActivity.this);
                            helper.DefinirBaseFikon(base);

                            usuarioBanco = helper.EfetuarLogin(login, senha, "APP");
                            if (usuarioBanco == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Foi informado login ou senha inválido!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            String tipoUsuario = usuarioBanco.getTipoUsuario();
                            String tokenUsuario = usuarioBanco.getToken();

                            // A Fikon devolve HTTP 200 e sucesso=true mesmo com senha errada
                            // ou usuário sem o escopo do App: o motivo vem em dados.mensagem.
                            if (tokenUsuario == null || tokenUsuario.trim().isEmpty()) {
                                final String motivo = usuarioBanco.getMensagem() != null
                                        && !usuarioBanco.getMensagem().trim().isEmpty()
                                        ? usuarioBanco.getMensagem()
                                        : "Foi informado login ou senha inválido!";
                                usuarioBanco = null;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, motivo, Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }

                            boolean tipoValido = tipoUsuario != null && (
                                    tipoUsuario.trim().equalsIgnoreCase("Vendedores") ||
                                            tipoUsuario.trim().equalsIgnoreCase("Consultores do Sistema")
                            );

                            boolean tokenValido = tokenUsuario != null && !tokenUsuario.trim().isEmpty();

                            if (!tipoValido || !tokenValido) {
                                final String debugTipo = tipoUsuario != null ? tipoUsuario : "null";
                                final String debugToken = (tokenUsuario != null && !tokenUsuario.isEmpty()) ? "presente" : "vazio/null";
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Sem permissão! Tipo: " + debugTipo + " | Token: " + debugToken, Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }

                            if (salvarSenha.isChecked()) {
                                usuarioBanco.setSenha(senha);
                                dao.limparBanco();
                                dao.inserir(usuarioBanco);
                                dao.close();
                            }

                            consultor = helper.ConsultarConsultor(usuarioBanco.getCpf(), usuarioBanco.getFilial(), usuarioBanco.getNome());
                            userFluig = helper.ConsultaFluig(usuarioBanco.getCpf());
                            if (userFluig != null) {
                                usuarioBanco.setLoginFluig(userFluig.getContent().getLogin());
                            }
                            helper.SalvarUsuario(usuarioBanco, consultor);
                            if (saveBiometricLogin) {
                                SecureCredentialStore.save(LoginActivity.this, login, senha, base);
                            } else {
                                SecureCredentialStore.clear(LoginActivity.this);
                            }

                            Intent irParaMain = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(irParaMain);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            finishAffinity();

                        } catch (UnknownHostException e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Informe uma URL válida para a base do Fikon!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Falha ao acessar o Fikon. Tente novamente!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } finally {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }
                }.start();
            }
        });
    }

    private void solicitarBiometria() {
        if (!loginBiometria.isChecked() || !SecureCredentialStore.hasCredentials(this)) {
            return;
        }

        BiometricAuthHelper.prompt(this, new BiometricAuthHelper.Callback() {
            @Override
            public void onSuccess() {
                String login = SecureCredentialStore.readLogin(LoginActivity.this);
                String password = SecureCredentialStore.readPassword(LoginActivity.this);
                if (login == null || password == null) {
                    SecureCredentialStore.clear(LoginActivity.this);
                    Toast.makeText(LoginActivity.this,
                            "Não foi possível recuperar as credenciais salvas. Faça login com sua senha.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                edtLogin.setText(login);
                edtSenha.setText(password);
                btlogar.performClick();
            }

            @Override
            public void onFailure() {
                Toast.makeText(LoginActivity.this,
                        "Biometria cancelada. Você pode entrar com seu usuário e senha.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

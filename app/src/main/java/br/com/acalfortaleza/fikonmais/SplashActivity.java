package br.com.acalfortaleza.fikonmais;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;

import br.com.acalfortaleza.fikonmais.dao.UsuarioDao;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.SecureCredentialStore;
import br.com.acalfortaleza.fikonmais.models.Usuario;

public class SplashActivity extends AppCompatActivity implements Runnable {

    private Handler handler;
    private UsuarioDao dao;
    private LoginHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dao = new UsuarioDao(this);
        helper = new LoginHelper(this);
        handler = new Handler();

        ImageView logo = findViewById(R.id.logoSplash);
        // Logo atual da Fikon (vetor); versão clara para o fundo escuro da abertura.
        logo.setImageResource(R.drawable.logo_fikon_branca);

        handler.postDelayed(this, 1000);
    }

    @Override
    public void run() {
        if (SecureCredentialStore.hasCredentials(this)) {
            abrirLogin();
            return;
        }

        final Usuario usuarioSalvo = dao.getUsuario();
        if (usuarioSalvo == null || usuarioSalvo.getLogin() == null || usuarioSalvo.getSenha() == null) {
            abrirLogin();
            return;
        }

        realizarLogin(usuarioSalvo.getLogin(), usuarioSalvo.getSenha());
    }

    private void realizarLogin(final String login, final String senha) {
        if (login == null || senha == null || login.trim().isEmpty() || senha.trim().isEmpty()) {
            abrirLogin();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Usuario usuario = helper.EfetuarLogin(login, senha, "APP");
                    if (!isUsuarioFikonValido(usuario)) {
                        dao.limparBanco();
                        SecureCredentialStore.clear(SplashActivity.this);
                        abrirLogin();
                        return;
                    }

                    usuario.setLogin(login);
                    usuario.setSenha(senha);
                    helper.SalvarUsuarioFikon(usuario);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                } catch (UnknownHostException e) {
                    mostrarErroConexao("Verifique a sua conexão com a internet e tente novamente!");
                } catch (ParseException | XmlPullParserException | IOException e) {
                    mostrarErroConexao("Ocorreu uma falha ao tentar se logar, tente novamente.");
                }
            }
        }).start();
    }

    private boolean isUsuarioFikonValido(Usuario usuario) {
        return usuario != null
                && "Vendedores".equals(usuario.getTipoUsuario())
                && usuario.getToken() != null
                && !usuario.getToken().trim().isEmpty();
    }

    private void abrirLogin() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void mostrarErroConexao(final String mensagem) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    new AlertDialog.Builder(SplashActivity.this)
                            .setTitle("Falha de Conexão")
                            .setMessage(mensagem)
                            .setCancelable(false)
                            .setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    finishAffinity();
                                }
                            })
                            .show();
                }
            }
        });
    }
}

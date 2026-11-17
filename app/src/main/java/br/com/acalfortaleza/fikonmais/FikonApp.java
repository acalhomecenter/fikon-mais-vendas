package br.com.acalfortaleza.fikonmais;

import android.app.Application;
import android.content.Context;

/**
 * Guarda o Context da aplicação para os helpers antigos (OrcamentoHelper, PedidosHelper),
 * que são criados sem Context, poderem ler o token e a loja da sessão Fikon.
 */
public class FikonApp extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    /** Context da aplicação, ou null antes do onCreate. */
    public static Context context() {
        return appContext;
    }
}

package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public final class SecureCredentialStore {

    private static final String PREFS_NAME = "fikon_secure_credentials";
    private static final String KEY_LOGIN = "secure_login";
    private static final String KEY_PASSWORD = "secure_password";
    private static final String KEY_BASE_URL = "secure_base_url";

    private SecureCredentialStore() {
    }

    private static SharedPreferences getPreferences(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static void save(Context context, String login, String password, String baseUrl) {
        if (context == null || login == null || password == null || baseUrl == null) {
            return;
        }
        try {
            SharedPreferences prefs = getPreferences(context);
            prefs.edit()
                    .putString(KEY_LOGIN, login)
                    .putString(KEY_PASSWORD, password)
                    .putString(KEY_BASE_URL, baseUrl)
                    .apply();
        } catch (GeneralSecurityException | IOException ignored) {
        }
    }

    public static boolean hasCredentials(Context context) {
        try {
            SharedPreferences prefs = getPreferences(context);
            return prefs.contains(KEY_LOGIN)
                    && prefs.contains(KEY_PASSWORD)
                    && prefs.contains(KEY_BASE_URL);
        } catch (GeneralSecurityException | IOException ignored) {
            return false;
        }
    }

    public static String readLogin(Context context) {
        try {
            return getPreferences(context).getString(KEY_LOGIN, null);
        } catch (GeneralSecurityException | IOException ignored) {
            return null;
        }
    }

    public static String readPassword(Context context) {
        try {
            return getPreferences(context).getString(KEY_PASSWORD, null);
        } catch (GeneralSecurityException | IOException ignored) {
            return null;
        }
    }

    public static String readBaseUrl(Context context) {
        try {
            return getPreferences(context).getString(KEY_BASE_URL, null);
        } catch (GeneralSecurityException | IOException ignored) {
            return null;
        }
    }

    public static void clear(Context context) {
        try {
            getPreferences(context).edit().clear().apply();
        } catch (GeneralSecurityException | IOException ignored) {
        }
    }
}

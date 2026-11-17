package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public final class BiometricAuthHelper {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    private BiometricAuthHelper() {
    }

    public static boolean isAvailable(Context context) {
        if (context == null) {
            return false;
        }

        return BiometricManager.from(context)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static void prompt(final FragmentActivity activity, final Callback callback) {
        if (activity == null || activity.isFinishing()) {
            if (callback != null) {
                callback.onFailure();
            }
            return;
        }

        if (!isAvailable(activity)) {
            if (callback != null) {
                callback.onFailure();
            }
            return;
        }

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acesso do Fikon")
                .setSubtitle("Use sua digital para entrar no Fikon")
                .setNegativeButtonText("Usar senha")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if (callback != null) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess();
                                }
                            });
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });

        biometricPrompt.authenticate(promptInfo);
    }
}

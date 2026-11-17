package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogHelper {
    private static final String TAG = "LogHelper";
    private static final String LOG_FILE_NAME = "pix_debug_log.txt";
    
    private static File getLogFile(Context context) {
        File logDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            logDir = new File(context.getExternalFilesDir(null), "logs");
        } else {
            logDir = new File(context.getFilesDir(), "logs");
        }
        
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        return new File(logDir, LOG_FILE_NAME);
    }
    
    public static void writeLog(Context context, String tag, String message) {
        try {
            File logFile = getLogFile(context);
            FileWriter writer = new FileWriter(logFile, true);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            
            String logEntry = String.format("[%s] %s: %s\n", timestamp, tag, message);
            writer.write(logEntry);
            writer.flush();
            writer.close();
            
            Log.d(TAG, "Log escrito: " + tag + " - " + message);
        } catch (IOException e) {
            Log.e(TAG, "Erro ao escrever log: " + e.getMessage());
        }
    }
    
    public static void writePixLog(Context context, String step, String data) {
        writeLog(context, "PIX_DEBUG", step + ": " + data);
    }
    
    public static void writePixError(Context context, String error) {
        writeLog(context, "PIX_ERROR", error);
    }
    
    public static void writePixRequest(Context context, String requestData) {
        writeLog(context, "PIX_REQUEST", requestData);
    }
    
    public static void writePixResponse(Context context, String responseData) {
        writeLog(context, "PIX_RESPONSE", responseData);
    }
    
    public static void clearLog(Context context) {
        try {
            File logFile = getLogFile(context);
            if (logFile.exists()) {
                logFile.delete();
            }
            writeLog(context, "LOG_SYSTEM", "Log limpo - Nova sessão iniciada");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao limpar log: " + e.getMessage());
        }
    }
    
    public static String getLogPath(Context context) {
        return getLogFile(context).getAbsolutePath();
    }
}
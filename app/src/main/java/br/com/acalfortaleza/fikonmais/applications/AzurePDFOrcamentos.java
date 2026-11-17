package br.com.acalfortaleza.fikonmais.applications;

import br.com.acalfortaleza.fikonmais.BuildConfig;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AzurePDFOrcamentos {

    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
            "AccountName=documentosfuncionarios;" +
            "AccountKey=" + BuildConfig.AZURE_STORAGE_KEY + ";" +
            "BlobEndpoint=https://documentosfuncionarios.blob.core.windows.net;" +
            "FileEndpoint=https://documentosfuncionarios.file.core.windows.net;";

    private static String containerAzure = "orcamentos-moskit";

    private static Boolean arquivoEnviado;
    private static String urlArquivo;

    private Handler handler = new Handler();

    public String nomeArquivo;

    public String subirArquivoPDF(File file){

        arquivoEnviado = null;
        urlArquivo = null;
        CountDownLatch count = new CountDownLatch(1);

        if(file==null || !file.exists()){
            return null;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    CloudBlobContainer container = blobClient.getContainerReference(containerAzure);
                    container.createIfNotExists();

                    BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
                    containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

                    container.uploadPermissions(containerPermissions);

                    nomeArquivo = file.getName().replace(".pdf", new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss").format(new Date()) + ".pdf");

                    CloudBlockBlob blob = container.getBlockBlobReference(nomeArquivo);
                    blob.upload(new FileInputStream(file), file.length());

                    arquivoEnviado = true;
                    urlArquivo = "https://documentosfuncionarios.blob.core.windows.net/" + containerAzure + "/" + nomeArquivo;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Arquivo " + file.getName() + " enviado. Link : " + urlArquivo);
                        }
                    });

                }
                catch (Exception ex){
                    ex.printStackTrace();
                    arquivoEnviado = false;
                    urlArquivo = null;
                }

                count.countDown();

            }
        });
        t.start();

        try {
            count.await(15, TimeUnit.SECONDS);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try{
            if(t!=null && t.isAlive()){
                t.interrupt();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return urlArquivo;

    }

}

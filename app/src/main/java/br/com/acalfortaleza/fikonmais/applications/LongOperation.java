package br.com.acalfortaleza.fikonmais.applications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import android.util.Log;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.LoginActivity;


public class LongOperation extends AsyncTask<Void, Void, String> {

    private String assunto;
    private String mensagem;
    private String destinatario;


    public LongOperation(String assunto, String mensagem , String destinatario){

    this.assunto=assunto  ;
    this.mensagem=mensagem ;
    this.destinatario=destinatario ;

    }






    @Override


    protected String doInBackground(Void... params) {


        try {

            GMailSender sender = new GMailSender("appacalhomecenter@gmail.com", "6j8a2b7u");


            sender.sendMail(assunto,


                    mensagem,"appacalhomecenter@gmail.com",


                    destinatario)                   ;





        } catch (Exception e) {


            Log.e("error", e.getMessage(), e);


            return "Email não Enviado";


        }


        return "Email Enviado";


    }





    @Override


    protected void onPostExecute(String result) {







    }





    @Override


    protected void onPreExecute() {




    }





    @Override


    protected void onProgressUpdate(Void... values) {


    }


}

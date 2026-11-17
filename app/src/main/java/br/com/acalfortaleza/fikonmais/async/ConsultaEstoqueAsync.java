package br.com.acalfortaleza.fikonmais.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.EstoqueValidacao;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento;

public class ConsultaEstoqueAsync extends AsyncTask<List<ItemPedidos> , String, List<ItemPedidos>> {

    private Context context;
    private ConsultaEstoqueInterface cei;
    private ProgressDialog progress;
    private PedidosHelper helper;
    private List<ItemPedidos> itemPedidos;

    public ConsultaEstoqueAsync(Context context, List<ItemPedidos> itemPedidos, ConsultaEstoqueInterface cei ) {
        this.context = context;
        this.itemPedidos = itemPedidos;
        this.cei = cei;
        helper = new PedidosHelper();
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage("Consultando estoque...");
        progress.setCancelable(false);
        progress.show();

    }

    @Override
    protected List<ItemPedidos> doInBackground(List<ItemPedidos>... params) {
        List<ItemPedidos> retornoEstoque = new ArrayList<>();

        try {
            for (int i = 0 ; i < itemPedidos.size(); i++){
                if(itemPedidos.get(i).getCodfilAnt() == 0 || itemPedidos.get(i).getCodFilOrig() != itemPedidos.get(i).getCodfilAnt()){
                    itemPedidos.get(i).setQuantidadeAnterior(0.00d);

                }
            }
            retornoEstoque = helper.ConsultarEstoque(itemPedidos);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
        } finally {


        }

        return retornoEstoque;
    }


    @Override
    protected void onPostExecute(List<ItemPedidos> retornoEstoque) {
        progress.dismiss();
        cei.depoisDaConsulta(retornoEstoque);
    }

}

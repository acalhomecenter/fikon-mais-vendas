package br.com.acalfortaleza.fikonmais.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Usuario;

public class CondPagamentoDao extends SQLiteOpenHelper {
    private static final String DATABASE ="Condpagamento";
    private static final int VERSAO =2;
    private static final String TABELA ="CondPagamento";
    private static final String COL_CONDPGTO="condpagto";
    private static final String COL_DESCRICAO ="descricao";
    private static final String COL_TEMENTRADA ="tementrada";



    public CondPagamentoDao(Context context) {

        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+ TABELA + "  ("
                + COL_CONDPGTO + " TEXT UNIQUE PRIMARY KEY, "
                + COL_DESCRICAO + " TEXT UNIQUE, "
                + COL_TEMENTRADA + " TEXT "
                + ");";

        db.execSQL(sql);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;

        db.execSQL(sql);
        onCreate(db);
    }


    public void inserir(CondPagamento condPagamento){

        ContentValues values = new ContentValues();
        values.put(COL_CONDPGTO, condPagamento.getCondpgto());
        values.put(COL_DESCRICAO, condPagamento.getDescricao());
        values.put(COL_TEMENTRADA, condPagamento.getTemEntrada());

        getWritableDatabase().insert(TABELA, null, values);
    }

    public List<CondPagamento> getCondicoes(){


        String sql = " SELECT * FROM " + TABELA + ";";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        List<CondPagamento> mList = new ArrayList<>();
        CondPagamento condPagamento = null;


            while (c.moveToNext()) {
                condPagamento = new CondPagamento();
                condPagamento.setCondpgto(c.getString(c.getColumnIndex(COL_CONDPGTO)));
                condPagamento.setDescricao(c.getString(c.getColumnIndex(COL_DESCRICAO)));
                condPagamento.setTemEntrada(c.getString(c.getColumnIndex(COL_TEMENTRADA)));

                mList.add(condPagamento);

            }

        return  mList;

    }

    public void  limparBanco( ){


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABELA);
        db.close();
    }

}

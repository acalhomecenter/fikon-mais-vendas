package br.com.acalfortaleza.fikonmais.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.models.Arquiteto;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;

public class ArquitetoDao extends SQLiteOpenHelper {
    private static final String DATABASE ="Arquitetos";
    private static final int VERSAO =2;
    private static final String TABELA ="Arquitetos";
    private static final String COL_CODARQ="codarq";
    private static final String COL_NOMEARQ ="nomearq";
    private static final String COL_CGCCPF ="cgccpf";



    public ArquitetoDao(Context context) {

        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+ TABELA + "  ("
                + COL_CODARQ + " TEXT UNIQUE PRIMARY KEY, "
                + COL_NOMEARQ + " TEXT UNIQUE, "
                + COL_CGCCPF + " TEXT "
                + ");";

        db.execSQL(sql);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;

        db.execSQL(sql);
        onCreate(db);
    }


    public void inserir(Arquiteto arquiteto){

        ContentValues values = new ContentValues();
        values.put(COL_CODARQ, arquiteto.getCodarq());
        values.put(COL_NOMEARQ, arquiteto.getNomearq());
        values.put(COL_CGCCPF, arquiteto.getCgccpf());

        getWritableDatabase().insert(TABELA, null, values);
    }

    public List<Arquiteto> getArquitetos(){


        String sql = " SELECT * FROM " + TABELA + ";";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        List<Arquiteto> mList = new ArrayList<>();
        Arquiteto arquiteto = null;


        while (c.moveToNext()) {
            arquiteto = new Arquiteto();
            arquiteto.setCodarq(c.getInt(c.getColumnIndex(COL_CODARQ)));
            arquiteto.setNomearq(c.getString(c.getColumnIndex(COL_NOMEARQ)));
            arquiteto.setCgccpf(c.getString(c.getColumnIndex(COL_CGCCPF)));

            mList.add(arquiteto);

        }

        return  mList;

    }

    public void  limparBanco( ){


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABELA);
        db.close();
    }

}

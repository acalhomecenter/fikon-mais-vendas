package br.com.acalfortaleza.fikonmais.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.models.FasesObra;

public class FasesObraDao extends SQLiteOpenHelper {
    private static final String DATABASE ="FasesObra";
    private static final int VERSAO =2;
    private static final String TABELA ="Fases";
    private static final String COL_ID="id";
    private static final String COL_DESCRICAO ="descricao";



    public FasesObraDao(Context context) {

        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+ TABELA + "  ("
                + COL_ID + " TEXT UNIQUE PRIMARY KEY, "
                + COL_DESCRICAO + " TEXT UNIQUE "
                + ");";

        db.execSQL(sql);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;

        db.execSQL(sql);
        onCreate(db);
    }


    public void inserir(FasesObra fasesObra){

        ContentValues values = new ContentValues();
        values.put(COL_ID, fasesObra.Id );
        values.put(COL_DESCRICAO, fasesObra.Descricao);


        getWritableDatabase().insert(TABELA, null, values);
    }

    public List<FasesObra> getFases(){


        String sql = " SELECT * FROM " + TABELA + ";";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        List<FasesObra> mList = new ArrayList<>();
        FasesObra faseObra = null;


        while (c.moveToNext()) {
            faseObra = new FasesObra();
            faseObra.Id = c.getInt(c.getColumnIndex(COL_ID));
            faseObra.Descricao =  c.getString(c.getColumnIndex(COL_DESCRICAO));


            mList.add(faseObra);

        }

        return  mList;

    }

    public void  limparBanco( ){


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABELA);
        db.close();
    }

}


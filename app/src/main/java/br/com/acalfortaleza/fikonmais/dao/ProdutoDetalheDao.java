package br.com.acalfortaleza.fikonmais.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.models.ProdutoDetalhe;


public class ProdutoDetalheDao extends SQLiteOpenHelper {
    private static final String DATABASE ="ProdutoDetalhe";
    private static final int VERSAO =1;
    private static final String TABELA ="ProdutoDetalhe";
    private static final String COL_ITEM="item";
    private static final String COL_DESCRICAO="descricao";
    private static final String COL_CODFORNE ="codforne";
    private static final String COL_FORNECEDOR ="fornecedor";
    private static final String COL_CODLINHA ="codlinha";
    private static final String COL_LINHA ="linha";
    private static final String COL_CODFAM ="codfam";
    private static final String COL_FAMILIA ="familia";
    private static final String COL_CODGRUPO ="codgrupo";
    private static final String COL_GRUPO ="grupo";
    private static final String COL_CODSUBGP ="codsubgp";
    private static final String COL_SUBGRUPO ="subgrupo";
    private static final String COL_ESPECIFIC ="especific";
    private static final String COL_ESPECIFICACAO="especificacao";
    private static final String COL_PRECO ="preco";
    private static final String COL_SITUACAO="situacao";


    public ProdutoDetalheDao(Context context) {

        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+ TABELA + "  ("
                + COL_ITEM + " TEXT UNIQUE PRIMARY KEY, "
                + COL_DESCRICAO + " TEXT , "
                + COL_CODFORNE + " TEXT , "
                + COL_FORNECEDOR + " TEXT , "
                + COL_CODLINHA + " TEXT , "
                + COL_LINHA + " TEXT , "
                + COL_CODFAM + " TEXT , "
                + COL_FAMILIA + " TEXT , "
                + COL_CODGRUPO + " TEXT , "
                + COL_GRUPO + " TEXT , "
                + COL_CODSUBGP + " TEXT , "
                + COL_SUBGRUPO + " TEXT , "
                + COL_ESPECIFIC + " TEXT , "
                + COL_ESPECIFICACAO + " TEXT , "
                + COL_PRECO + " TEXT , "
                + COL_SITUACAO + " TEXT  "
                + ");";

        db.execSQL(sql);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;

        db.execSQL(sql);
        onCreate(db);
    }


    public void inserir(ProdutoDetalhe produtoDetalhe){

        ContentValues values = new ContentValues();
        values.put(COL_ITEM, produtoDetalhe.getItem());
        values.put(COL_DESCRICAO, produtoDetalhe.getDescricao());
        values.put(COL_CODFORNE, produtoDetalhe.getCodforne());
        values.put(COL_FORNECEDOR, produtoDetalhe.getFornecedor());
        values.put(COL_CODLINHA, produtoDetalhe.getCodlinha());
        values.put(COL_LINHA, produtoDetalhe.getLinha());
        values.put(COL_CODFAM, produtoDetalhe.getCodFam());
        values.put(COL_FAMILIA, produtoDetalhe.getFamilia());
        values.put(COL_CODGRUPO, produtoDetalhe.getCodgrupo());
        values.put(COL_GRUPO, produtoDetalhe.getGrupo());
        values.put(COL_CODSUBGP, produtoDetalhe.getCodsubgp());
        values.put(COL_SUBGRUPO, produtoDetalhe.getSubgrupo());
        values.put(COL_ESPECIFIC, produtoDetalhe.getEspecific());
        values.put(COL_ESPECIFICACAO, produtoDetalhe.getEspecificacao());
        values.put(COL_PRECO, produtoDetalhe.getPreco());
        values.put(COL_SITUACAO, produtoDetalhe.getSituacao());


        getWritableDatabase().insert(TABELA, null, values);
    }

    public List<ProdutoDetalhe> getProdutoDetalhe(){


        String sql = " SELECT * FROM " + TABELA + ";";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        List<ProdutoDetalhe> mList = new ArrayList<>();
        ProdutoDetalhe produtoDetalhe = null;


        while (c.moveToNext()) {
            produtoDetalhe = new ProdutoDetalhe();
            produtoDetalhe.setItem(c.getInt(c.getColumnIndex(COL_ITEM)));
            produtoDetalhe.setDescricao(c.getString(c.getColumnIndex(COL_DESCRICAO)));
            produtoDetalhe.setCodforne(c.getInt(c.getColumnIndex(COL_CODFORNE)));
            produtoDetalhe.setFornecedor(c.getString(c.getColumnIndex(COL_FORNECEDOR)));
            produtoDetalhe.setCodlinha(c.getInt(c.getColumnIndex(COL_CODLINHA)));
            produtoDetalhe.setLinha(c.getString(c.getColumnIndex(COL_LINHA)));
            produtoDetalhe.setCodFam(c.getInt(c.getColumnIndex(COL_CODFAM)));
            produtoDetalhe.setFamilia(c.getString(c.getColumnIndex(COL_FAMILIA)));
            produtoDetalhe.setCodgrupo(c.getInt(c.getColumnIndex(COL_CODGRUPO)));
            produtoDetalhe.setGrupo(c.getString(c.getColumnIndex(COL_GRUPO)));
            produtoDetalhe.setCodsubgp(c.getInt(c.getColumnIndex(COL_CODSUBGP)));
            produtoDetalhe.setSubgrupo(c.getString(c.getColumnIndex(COL_SUBGRUPO)));
            produtoDetalhe.setEspecific(c.getInt(c.getColumnIndex(COL_ESPECIFIC)));
            produtoDetalhe.setEspecificacao(c.getString(c.getColumnIndex(COL_ESPECIFICACAO)));
            produtoDetalhe.setPreco(c.getDouble(c.getColumnIndex(COL_PRECO)));
            produtoDetalhe.setEspecificacao(c.getString(c.getColumnIndex(COL_SITUACAO)));

            mList.add(produtoDetalhe);

        }

        return  mList;

    }



    public ArrayList<String> getFornecedores(){


        String sql = " SELECT distinct fornecedor FROM " + TABELA + " Order by 1 asc; ";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();


        while (c.moveToNext()) {

            mList.add(c.getString(c.getColumnIndex(COL_FORNECEDOR)));

        }

        return  mList;

    }


    public ArrayList<String> getLinhas(){


        String sql = " SELECT distinct linha FROM " + TABELA + " Order by 1 asc; ";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();

        while (c.moveToNext()) {


            mList.add(c.getString(c.getColumnIndex(COL_LINHA)));

        }

        return  mList;

    }


    public ArrayList<String> getFamilias(){


        String sql = " SELECT distinct familia FROM " + TABELA + " Order by 1 asc;";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();



        while (c.moveToNext()) {


            mList.add(c.getString(c.getColumnIndex(COL_FAMILIA)));

        }

        return  mList;

    }


    public ArrayList<String> getGrupos(){


        String sql = " SELECT distinct grupo FROM " + TABELA + " Order by 1 asc;";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();


        while (c.moveToNext()) {


            mList.add(c.getString(c.getColumnIndex(COL_GRUPO)));

        }

        return  mList;

    }


    public ArrayList<String> getSubGrupos(){


        String sql = " SELECT distinct subgrupo FROM " + TABELA + " Order by 1 asc;";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();


        while (c.moveToNext()) {


            mList.add(c.getString(c.getColumnIndex(COL_SUBGRUPO)));

        }

        return  mList;

    }


    public ArrayList<String>getEspecificacoes(){


        String sql = " SELECT distinct especificacao FROM " + TABELA + " Order by 1 asc;";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();


        while (c.moveToNext()) {


            mList.add(c.getString(c.getColumnIndex(COL_ESPECIFICACAO)));

        }

        return  mList;

    }

    public ArrayList<String> getSituacoes(){


        String sql = " SELECT distinct situacao FROM " + TABELA + " Order by 1 asc;";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        ArrayList<String> mList = new ArrayList<>();


        while (c.moveToNext()) {


            mList.add(c.getString(c.getColumnIndex(COL_SITUACAO)));

        }

        return  mList;

    }


    public void  limparBanco( ){

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABELA);
        db.close();
    }

}

package br.com.acalfortaleza.fikonmais.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;

import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Objects;

import br.com.acalfortaleza.fikonmais.models.Orcamento;

public class OrcamentoAlteradoDao extends SQLiteOpenHelper {

    private static final String DATABASE = "OrcamentoAlterado";
    private static final int VERSAO = 4;
    private static final String TABELA = "OrcamentoAlterado";
    private static final String COL_NUMORC = "numorc";
    private static final String COL_CODCLI = "codcli";
    private static final String COL_VLTOTAL = "vltotal";
    private static final String COL_QTDITENS = "qtditens";
    private static final String COL_PESO = "peso";
    private static final String COL_ALTERADO = "alterado";
    private static final String COL_CACHE_ITENS = "cache_itens";

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public OrcamentoAlteradoDao(Context context) {

        super(context, DATABASE, null, VERSAO);

        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABELA +
                " ( " +
                    COL_NUMORC + " TEXT UNIQUE PRIMARY KEY, " +
                    COL_CODCLI + " TEXT, " +
                    COL_VLTOTAL + " TEXT, " +
                    COL_QTDITENS + " TEXT, " +
                    COL_PESO + " TEXT, " +
                    COL_ALTERADO + " TEXT, " +
                    COL_CACHE_ITENS + " TEXT " +
                " ) ";

        db.execSQL(sql);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;

        db.execSQL(sql);
        onCreate(db);

    }

    public void inserir(String numorc, Orcamento orcamento, boolean alterado){

        SQLiteDatabase db = getWritableDatabase();

        String sql = "DELETE FROM " + TABELA +
                " WHERE " + COL_NUMORC + " <> '" + numorc + "' ";

        db.execSQL(sql);

        OrcamentoBanco orcamentoBanco = new OrcamentoBanco(orcamento);

        String sql2 =
                "INSERT OR IGNORE INTO " + TABELA +
                    " ( " +
                        COL_NUMORC + " , " +
                        COL_CODCLI + " , " +
                        COL_VLTOTAL + " , " +
                        COL_QTDITENS + " , " +
                        COL_PESO + " , " +
                        COL_ALTERADO +
                    " ) " +
                " VALUES " +
                    " ( " +
                        " '" + numorc + "', " +
                        " '" + orcamentoBanco.getCodcli() + "', " +
                        " '" + orcamentoBanco.getVltotal() + "', " +
                        " '" + orcamentoBanco.getQtditens() + "', " +
                        " '" + orcamentoBanco.getPeso() + "', " +
                        " '" + String.valueOf(alterado) + "' " +
                    " ) ";

        db.execSQL(sql2);

        db.close();

    }

    public void editarAlterado(String numorc, boolean alterado){

        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE " + TABELA +
                " SET " + COL_ALTERADO + " = '" + String.valueOf(alterado) + "' " +
                " WHERE " + COL_NUMORC + " = '" + numorc + "' ";

        db.execSQL(sql);

        db.close();

    }

    public void editarOrcamento(String numorc, Orcamento orcamento){

        SQLiteDatabase db = getWritableDatabase();

        OrcamentoBanco orcamentoBanco = new OrcamentoBanco(orcamento);

        String sql = "UPDATE " + TABELA +
                " SET " +
                COL_CODCLI + " = '" + orcamentoBanco.getCodcli() + "', " +
                COL_VLTOTAL + " = '" + orcamentoBanco.getVltotal() + "', " +
                COL_QTDITENS + " = '" + orcamentoBanco.getQtditens() + "', " +
                COL_PESO + " = '" + orcamentoBanco.getPeso() + "' " +
                " WHERE " + COL_NUMORC + " = '" + numorc + "'";

        db.execSQL(sql);

        db.close();

    }

    public void deletarOrcamento(String numorc){

        SQLiteDatabase db = getWritableDatabase();

        String sql = "DELETE FROM " + TABELA +
                " WHERE " + COL_NUMORC + " = '" + numorc + "' ";

        db.execSQL(sql);

        db.close();

    }


    @SuppressLint("Range")
    public boolean orcamentoAlterado(String numorc, Orcamento orcamento){

        if(numorc==null || numorc.trim().equals("0")){
            return true;
        }

        OrcamentoBanco orcamentoBanco = new OrcamentoBanco(orcamento);
        boolean orcamentoAlterado = false;

        OrcamentoBanco orcamentoOriginal = new OrcamentoBanco(new Orcamento());

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + TABELA +
                " WHERE " + COL_NUMORC + " = '" + numorc + "' ";

        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.getCount() == 0){
            return false;
        }

        while (cursor.moveToNext()) {

            orcamentoOriginal.setCodcli( cursor.getString(cursor.getColumnIndex(COL_CODCLI)) );
            orcamentoOriginal.setVltotal( cursor.getString(cursor.getColumnIndex(COL_VLTOTAL)) );
            orcamentoOriginal.setQtditens( cursor.getString(cursor.getColumnIndex(COL_QTDITENS)) );
            orcamentoOriginal.setPeso( cursor.getString(cursor.getColumnIndex(COL_PESO)) );

            orcamentoAlterado = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(COL_ALTERADO)));

        }

        if(
            !Objects.equals(orcamentoOriginal.getCodcli(), orcamentoBanco.getCodcli())
            || !Objects.equals(orcamentoOriginal.getVltotal(), orcamentoBanco.getVltotal())
            || !Objects.equals(orcamentoOriginal.getQtditens(), orcamentoBanco.getQtditens())
            || !Objects.equals(orcamentoOriginal.getPeso(), orcamentoBanco.getPeso())
            || orcamentoAlterado
        ) {
            return true;
        }

        return false;

    }

    public void setItensEmCache(String numorc, boolean emCache) {
        if (numorc == null || numorc.trim().equals("0") || numorc.trim().isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String checkSql = "SELECT " + COL_NUMORC + " FROM " + TABELA + " WHERE " + COL_NUMORC + " = '" + numorc + "'";
        Cursor cursor = db.rawQuery(checkSql, null);
        if (cursor.getCount() > 0) {
            String sql = "UPDATE " + TABELA + " SET " + COL_CACHE_ITENS + " = '" + String.valueOf(emCache) + "' WHERE " + COL_NUMORC + " = '" + numorc + "'";
            db.execSQL(sql);
        } else {
            String sql = "INSERT OR REPLACE INTO " + TABELA + " (" + COL_NUMORC + ", " + COL_CACHE_ITENS + ", " + COL_ALTERADO + ") VALUES ('" + numorc + "', '" + String.valueOf(emCache) + "', 'false')";
            db.execSQL(sql);
        }
        cursor.close();
        db.close();
    }

    @SuppressLint("Range")
    public boolean possuiItensEmCache(String numorc) {
        if (numorc == null || numorc.trim().equals("0") || numorc.trim().isEmpty()) {
            return false;
        }
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_CACHE_ITENS + " FROM " + TABELA + " WHERE " + COL_NUMORC + " = '" + numorc + "'";
        Cursor cursor = db.rawQuery(sql, null);
        boolean emCache = false;
        while (cursor.moveToNext()) {
            String val = cursor.getString(cursor.getColumnIndex(COL_CACHE_ITENS));
            if (val != null && Boolean.parseBoolean(val)) {
                emCache = true;
            }
        }
        cursor.close();
        db.close();
        return emCache;
    }

    private class OrcamentoBanco{

        private String codcli;
        private String vltotal;
        private String qtditens;
        private String peso;

        private OrcamentoBanco(Orcamento orcamento){

            this.codcli = orcamento.getCliente() != null && orcamento.getCliente().getCodcli() > 0 ? Integer.toString(orcamento.getCliente().getCodcli()) : "0";
            this.vltotal = Double.toString(orcamento.getVlTotal());
            this.qtditens = orcamento.getItens() != null && orcamento.getItens().size() > 0 ? Integer.toString(orcamento.getItens().size()) : "0";
            this.peso = Double.toString(orcamento.getPeso());

        }

        public String getCodcli() {
            return codcli;
        }

        public void setCodcli(String codcli) {
            this.codcli = codcli;
        }

        public String getVltotal() {
            return vltotal;
        }

        public void setVltotal(String vltotal) {
            this.vltotal = vltotal;
        }

        public String getQtditens() {
            return qtditens;
        }

        public void setQtditens(String qtditens) {
            this.qtditens = qtditens;
        }

        public String getPeso() {
            return peso;
        }

        public void setPeso(String peso) {
            this.peso = peso;
        }

    }

}

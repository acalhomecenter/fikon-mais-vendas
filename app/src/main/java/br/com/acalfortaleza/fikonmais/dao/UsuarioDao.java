package br.com.acalfortaleza.fikonmais.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.acalfortaleza.fikonmais.models.Usuario;

public class UsuarioDao extends SQLiteOpenHelper {

    private static final String DATABASE ="acalVendas";
    private static final int VERSAO =2;
    private static final String TABELA ="Usuario";
    private static final String COL_ID="id";
    private static final String COL_NOME ="nome";
    private static final String COL_MATRICULA ="matricula";
    private static final String COL_NASCIMENTO ="nascimento";
    private static final String COL_CPF ="cpf";
    private static final String COL_LOGIN ="login";
    private static final String COL_SENHA ="senha";
    private static final String COL_FILIAL="filial" ;
    private static final String COL_AREA= "area";
    private static final String COL_SETOR= "setor";
    private static final String COL_CARGO= "cargo";
    private static final String COL_CODIGO ="codigo" ;


    public UsuarioDao(Context context) {

        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+ TABELA + "  ("
                + COL_ID + " TEXT UNIQUE PRIMARY KEY, "
                + COL_NOME + " TEXT UNIQUE, "
                + COL_MATRICULA + " TEXT, "
                + COL_NASCIMENTO + " TEXT, "
                + COL_CPF+ " TEXT, "
                + COL_LOGIN + " TEXT, "
                + COL_SENHA + " TEXT ,"
                + COL_FILIAL + " TEXT, "
                + COL_AREA + " TEXT, "
                + COL_SETOR + " TEXT, "
                + COL_CARGO + " TEXT,"
                + COL_CODIGO+ " TEXT "
                + ");";

        db.execSQL(sql);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;

        db.execSQL(sql);
        onCreate(db);
    }


    public void inserir(Usuario usuario){

        ContentValues values = new ContentValues();
        values.put(COL_ID, usuario.getId());
        values.put(COL_NOME, usuario.getNome());
        values.put(COL_MATRICULA, usuario.getMatricula());
        values.put(COL_NASCIMENTO, usuario.getNascimento());
        values.put(COL_CPF, usuario.getCpf());
        values.put(COL_LOGIN, usuario.getLogin());
        values.put(COL_SENHA, usuario.getSenha());
        values.put(COL_FILIAL, usuario.getFilial());
        values.put(COL_AREA, usuario.getArea());
        values.put(COL_SETOR, usuario.getSetor());
        values.put(COL_CARGO, usuario.getCargo());
        values.put(COL_CODIGO, usuario.getCodigo());


        getWritableDatabase().insert(TABELA, null, values);
    }

    public Usuario getUsuario(){


        String sql = " SELECT * FROM " + TABELA + ";";
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        Usuario usuario = new Usuario();

        if(c.getCount() != 0){
            while (c.moveToNext()) {

                usuario.setId(c.getInt(c.getColumnIndex(COL_ID)));
                usuario.setNome(c.getString(c.getColumnIndex(COL_NOME)));
                usuario.setMatricula(c.getString(c.getColumnIndex(COL_MATRICULA)));
                usuario.setNascimento(c.getString(c.getColumnIndex(COL_NASCIMENTO)));
                usuario.setCpf(c.getString(c.getColumnIndex(COL_CPF)));
                usuario.setLogin(c.getString(c.getColumnIndex(COL_LOGIN)));
                usuario.setSenha(c.getString(c.getColumnIndex(COL_SENHA)));
                usuario.setFilial(c.getString(c.getColumnIndex(COL_FILIAL)));
                usuario.setArea(c.getString(c.getColumnIndex(COL_AREA)));
                usuario.setSetor(c.getString(c.getColumnIndex(COL_SETOR)));
                usuario.setCargo(c.getString(c.getColumnIndex(COL_CARGO)));
                usuario.setCodigo(c.getString(c.getColumnIndex(COL_CODIGO)));
            }

        }else{

            usuario.setId(0);
            usuario.setNome(null);
            usuario.setMatricula(null);
            usuario.setNascimento(null);
            usuario.setCpf(null);
            usuario.setLogin(null);
            usuario.setSenha(null);
            usuario.setFilial(null);
            usuario.setArea(null);
            usuario.setSetor(null);
            usuario.setCargo(null);
            usuario.setCodigo(null);



        }

        return usuario;

    }

    public void  limparBanco( ){


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABELA);
        db.close();
    }


}

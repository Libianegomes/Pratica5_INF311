package br.ufv.inf311.checkinlocais.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.ufv.inf311.checkinlocais.model.Categoria;
import br.ufv.inf311.checkinlocais.model.Checkin;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "checkin_locais.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CATEGORIA = "Categoria";
    public static final String TABLE_CHECKIN = "Checkin";

    public static final String COL_ID_CATEGORIA = "idCategoria";
    public static final String COL_NOME = "nome";
    public static final String COL_LOCAL = "Local";
    public static final String COL_QTD_VISITAS = "qtdVisitas";
    public static final String COL_CAT = "cat";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIA + " (" +
                COL_ID_CATEGORIA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_CHECKIN + " (" +
                COL_LOCAL + " TEXT PRIMARY KEY, " +
                COL_QTD_VISITAS + " INTEGER NOT NULL, " +
                COL_CAT + " INTEGER NOT NULL, " +
                COL_LATITUDE + " TEXT NOT NULL, " +
                COL_LONGITUDE + " TEXT NOT NULL, " +
                "CONSTRAINT fkey0 FOREIGN KEY (" + COL_CAT + ") REFERENCES " + TABLE_CATEGORIA + " (" + COL_ID_CATEGORIA + ")" +
                ")");

        inserirCategoriasPadrao(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIA);
        onCreate(db);
    }

    private void inserirCategoriasPadrao(SQLiteDatabase db) {
        inserirCategoria(db, "Restaurante");
        inserirCategoria(db, "Bar");
        inserirCategoria(db, "Cinema");
        inserirCategoria(db, "Universidade");
        inserirCategoria(db, "Estádio");
        inserirCategoria(db, "Parque");
        inserirCategoria(db, "Outros");
    }

    private void inserirCategoria(SQLiteDatabase db, String nome) {
        ContentValues values = new ContentValues();
        values.put(COL_NOME, nome);
        db.insertOrThrow(TABLE_CATEGORIA, null, values);
    }

    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_CATEGORIA,
                new String[]{COL_ID_CATEGORIA, COL_NOME},
                null,
                null,
                null,
                null,
                COL_ID_CATEGORIA + " ASC")) {

            while (cursor.moveToNext()) {
                categorias.add(new Categoria(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_CATEGORIA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME))
                ));
            }
        }

        return categorias;
    }

    public List<String> listarLocaisVisitados() {
        List<String> locais = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_CHECKIN,
                new String[]{COL_LOCAL},
                null,
                null,
                null,
                null,
                COL_LOCAL + " COLLATE NOCASE ASC")) {

            while (cursor.moveToNext()) {
                locais.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCAL)));
            }
        }

        return locais;
    }

    public boolean checkinExiste(String local) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_CHECKIN,
                new String[]{COL_LOCAL},
                COL_LOCAL + " = ?",
                new String[]{local},
                null,
                null,
                null)) {

            return cursor.moveToFirst();
        }
    }

    public void inserirCheckin(String local, int categoriaId, String latitude, String longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOCAL, local);
        values.put(COL_QTD_VISITAS, 1);
        values.put(COL_CAT, categoriaId);
        values.put(COL_LATITUDE, latitude);
        values.put(COL_LONGITUDE, longitude);
        db.insertOrThrow(TABLE_CHECKIN, null, values);
    }

    public void incrementarVisita(String local) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(
                "UPDATE " + TABLE_CHECKIN + " SET " + COL_QTD_VISITAS + " = " + COL_QTD_VISITAS + " + 1 WHERE " + COL_LOCAL + " = ?",
                new Object[]{local}
        );
    }

    public void deletarCheckin(String local) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CHECKIN, COL_LOCAL + " = ?", new String[]{local});
    }

    public List<Checkin> listarCheckins() {
        return listarCheckinsOrdenados("c." + COL_LOCAL + " COLLATE NOCASE ASC");
    }

    public List<Checkin> listarRelatorio() {
        return listarCheckinsOrdenados("c." + COL_QTD_VISITAS + " DESC, c." + COL_LOCAL + " COLLATE NOCASE ASC");
    }

    private List<Checkin> listarCheckinsOrdenados(String orderBy) {
        List<Checkin> checkins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT c." + COL_LOCAL + ", c." + COL_QTD_VISITAS + ", c." + COL_CAT + ", " +
                "c." + COL_LATITUDE + ", c." + COL_LONGITUDE + ", cat." + COL_NOME + " AS categoriaNome " +
                "FROM " + TABLE_CHECKIN + " c " +
                "INNER JOIN " + TABLE_CATEGORIA + " cat ON c." + COL_CAT + " = cat." + COL_ID_CATEGORIA + " " +
                "ORDER BY " + orderBy;

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                checkins.add(new Checkin(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCAL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_QTD_VISITAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CAT)),
                        cursor.getString(cursor.getColumnIndexOrThrow("categoriaNome")),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LATITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LONGITUDE))
                ));
            }
        }

        return checkins;
    }
}

package com.example.emanu.boaviagem.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by emanu on 08/07/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "travels";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DETOUR="detour";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_DETOUR
    };

    private static final String NOME_BANCO="dbTravels";
    private static final int    VERSAO_BANCO = 1;

    public DBHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ TABLE_NAME +" ("+
                        COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COLUMN_DETOUR +" TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
    }
}
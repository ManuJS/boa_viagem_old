package com.example.emanu.boaviagem.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.emanu.boaviagem.database.DBHelper;


public class BoaViagemProvider extends ContentProvider {
    // Deve estar igual ao Manifest
    private static final String
            AUTHORITY = "com.example.emanu.boaviagem";

    private static final String BASE_PATH_TRAVELS = "travels";
    private static final String BASE_PATH_EXPENSES = "expenses";


    // Tipo de acesso que retorna todas as mensagens
    private static final int TYPE_ALL_TRAVELS = 1;
    // Tipo de acesso que retorna apenas uma mensagem
    // usando o id da mesma
    private static final int TYPE_SINGLE_TRAVEL = 2;

    // Tipo de acesso que retorna todas os gastos
    private static final int TYPE_ALL_EXPENSE = 3;

    private static final int TYPE_SINGLE_EXPENSE = 4;

    private static final int TYPE_TRAVEL_EXPENSE = 5;

    // Classe para checar se a Uri passada é valida
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // É através dessa URI que acessamos nosso provider
    public static final Uri CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + BASE_PATH_TRAVELS);

    static {

        uriMatcher.addURI(AUTHORITY,
                BASE_PATH_TRAVELS,
                TYPE_ALL_TRAVELS);

        uriMatcher.addURI(AUTHORITY,
                BASE_PATH_TRAVELS + "/#",
                TYPE_SINGLE_TRAVEL);

        uriMatcher.addURI(AUTHORITY,
                BASE_PATH_EXPENSES,
                TYPE_ALL_EXPENSE);

        uriMatcher.addURI(AUTHORITY,
                BASE_PATH_EXPENSES + "/#",
                TYPE_SINGLE_EXPENSE);

        uriMatcher.addURI(AUTHORITY,
                BASE_PATH_EXPENSES + "/"+ BASE_PATH_TRAVELS + "/#",
                TYPE_TRAVEL_EXPENSE);



        uriMatcher.addURI(AUTHORITY,
                BASE_PATH_TRAVELS + "/#", TYPE_SINGLE_TRAVEL);
    }

    private DBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        // Ao criar o Provider, inicializamos o helper
        mOpenHelper = new DBHelper(getContext());
        return true; // success
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);

        SQLiteDatabase sqlDB =
                mOpenHelper.getWritableDatabase();
        long id = 0;



        switch (uriType) {

            case TYPE_ALL_TRAVELS:
                id = sqlDB.insert(BASE_PATH_TRAVELS, null, values);
                return Uri.withAppendedPath(BoaViagemProvider.CONTENT_URI,
                        String.valueOf(id));


            case TYPE_ALL_EXPENSE:
                id = sqlDB.insert(BASE_PATH_EXPENSES, null, values);
                return Uri.withAppendedPath(BoaViagemProvider.CONTENT_URI,
                        String.valueOf(id));


            default:
                throw new IllegalArgumentException(
                        "Unknown URI: " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB =
                mOpenHelper.getWritableDatabase();

        int rowsUpdated = 0;

        switch (uriType) {
            case TYPE_ALL_TRAVELS:
                rowsUpdated = sqlDB.update(
                        DBHelper.TABLE_NAME_TRAVELS,
                        values,
                        selection,
                        selectionArgs);
                break;

            case TYPE_SINGLE_TRAVEL:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(
                            DBHelper.TABLE_NAME_TRAVELS,
                            values,
                            DBHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(
                            DBHelper.TABLE_NAME_TRAVELS,
                            values,
                            DBHelper.COLUMN_ID +"="+ id +
                                    " and "+ selection,
                            selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown URI: " + uri);
        }

        getContext().getContentResolver()
                .notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB =
                mOpenHelper.getWritableDatabase();

        int rowsDeleted = 0;
        switch (uriType) {
            case TYPE_ALL_TRAVELS:
                rowsDeleted = sqlDB.delete(
                        DBHelper.TABLE_NAME_TRAVELS,
                        selection,
                        selectionArgs);
                break;

            case TYPE_SINGLE_TRAVEL:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            DBHelper.TABLE_NAME_TRAVELS,
                            DBHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            DBHelper.TABLE_NAME_TRAVELS,
                            DBHelper.COLUMN_ID +"="+ id +
                                    " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown URI: " + uri);
        }

        getContext().getContentResolver()
                .notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder queryBuilder =
                new SQLiteQueryBuilder();

        queryBuilder.setTables(DBHelper.TABLE_NAME_TRAVELS);

        int uriType = uriMatcher.match(uri);
        Cursor cursor = null;
        SQLiteDatabase db =
                mOpenHelper.getWritableDatabase();

        switch (uriType) {
            case TYPE_ALL_TRAVELS:
                cursor = queryBuilder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TYPE_SINGLE_TRAVEL:
                queryBuilder.appendWhere(
                        DBHelper.COLUMN_ID + "= ?");

                cursor = queryBuilder.query(
                        db,
                        projection,
                        selection,
                        new String[]{ uri.getLastPathSegment() },
                        null,
                        null,
                        null);
                break;


            case TYPE_ALL_EXPENSE:
                cursor = queryBuilder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
//
//            case TYPE_SINGLE_EXPENSE:
//
//                selection = Gasto._ID + " = ?";
//                selectionArgs = new String[] {uri.getLastPathSegment()};
//                return database.query(GASTO_PATH, projection,
//                        selection, selectionArgs, null, null, sortOrder);
//
//            case TYPE_TRAVEL_EXPENSE:
//
//                selection = Gasto.VIAGEM_ID + " = ?";
//                selectionArgs = new String[] {uri.getLastPathSegment()};
//                return database.query(GASTO_PATH, projection,
//                        selection, selectionArgs, null, null, sortOrder);
//
//            default:
//                throw new IllegalArgumentException("Uri desconhecida");
//        }

            default:
                throw new IllegalArgumentException(
                        "Unknown URI: " + uri);
        }

        cursor.setNotificationUri(
                getContext().getContentResolver(), uri);

        return cursor;
    }
}
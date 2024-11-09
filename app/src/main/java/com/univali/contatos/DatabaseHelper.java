package com.univali.contatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contatos.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsuarioTable = "CREATE TABLE usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL)";
        db.execSQL(createUsuarioTable);

        String createTelefoneTable = "CREATE TABLE telefone (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER NOT NULL, " +
                "ddd TEXT NOT NULL, " +
                "numero TEXT NOT NULL, " +
                "tipo TEXT NOT NULL, " +
                "FOREIGN KEY(usuario_id) REFERENCES usuario(id))";
        db.execSQL(createTelefoneTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS telefone");
        db.execSQL("DROP TABLE IF EXISTS usuario");
        onCreate(db);
    }

    public long saveContact(SQLiteDatabase db, String fullName) {
        ContentValues values = new ContentValues();
        values.put("nome", fullName);
        return db.insert("usuario", null, values);
    }

    public void savePhone(SQLiteDatabase db, long userId, String ddd, String phoneNumber, String phoneType) {
        ContentValues values = new ContentValues();
        values.put("usuario_id", userId);
        values.put("ddd", ddd);
        values.put("numero", phoneNumber);
        values.put("tipo", phoneType);
        db.insert("telefone", null, values);
    }

    public void deleteContact(SQLiteDatabase db, long contactId) {
        db.delete("telefone", "usuario_id = ?", new String[]{String.valueOf(contactId)});
        db.delete("usuario", "id = ?", new String[]{String.valueOf(contactId)});
    }

    public void updateContact(SQLiteDatabase db, long contactId, String fullName) {
        ContentValues values = new ContentValues();
        values.put("nome", fullName);
        db.update("usuario", values, "id = ?", new String[]{String.valueOf(contactId)});
    }

    public void deletePhonesByContactId(SQLiteDatabase db, long contactId) {
        db.delete("telefone", "usuario_id = ?", new String[]{String.valueOf(contactId)});
    }

    public Contact getContactById(SQLiteDatabase db, long contactId) {
        Cursor cursor = db.query("usuario", new String[]{"id", "nome"}, "id = ?", new String[]{String.valueOf(contactId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndex("nome"));
            cursor.close();

            List<Phone> phones = new ArrayList<>();
            Cursor phoneCursor = db.query("telefone", new String[]{"ddd", "numero", "tipo"}, "usuario_id = ?", new String[]{String.valueOf(contactId)}, null, null, null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    String ddd = phoneCursor.getString(phoneCursor.getColumnIndex("ddd"));
                    String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex("numero"));
                    String phoneType = phoneCursor.getString(phoneCursor.getColumnIndex("tipo"));
                    phones.add(new Phone(ddd, phoneNumber, phoneType));
                }
                phoneCursor.close();
            }

            return new Contact(contactId, fullName, phones);
        }
        return null;
    }
}
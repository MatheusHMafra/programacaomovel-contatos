package com.univali.contatos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewContacts;
    private FloatingActionButton fabAddContact;
    private DatabaseHelper dbHelper;
    private ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        fabAddContact = findViewById(R.id.fabAddContact);
        dbHelper = new DatabaseHelper(this);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        contactsAdapter = new ContactsAdapter(getContacts());
        recyclerViewContacts.setAdapter(contactsAdapter);

        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdicaoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactsAdapter.updateContacts(getContacts());
    }

    private List<Contact> getContacts() {
        Map<Long, Contact> contactMap = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT usuario.id, usuario.nome, telefone.ddd, telefone.numero, telefone.tipo " +
                "FROM usuario LEFT JOIN telefone ON usuario.id = telefone.usuario_id";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            long userId = cursor.getLong(cursor.getColumnIndex("id"));
            String fullName = cursor.getString(cursor.getColumnIndex("nome"));
            String ddd = cursor.getString(cursor.getColumnIndex("ddd"));
            String phoneNumber = cursor.getString(cursor.getColumnIndex("numero"));
            String phoneType = cursor.getString(cursor.getColumnIndex("tipo"));

            Contact contact = contactMap.get(userId);
            if (contact == null) {
                contact = new Contact(userId, fullName);
                contactMap.put(userId, contact);
            }
            contact.addPhone(new Phone(ddd, phoneNumber, phoneType));
        }

        cursor.close();
        db.close();
        return new ArrayList<>(contactMap.values());
    }

    public void deleteContact(long contactId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.deleteContact(db, contactId);
        db.close();
        Toast.makeText(this, "Contato deletado com sucesso", Toast.LENGTH_SHORT).show();
        contactsAdapter.updateContacts(getContacts());
    }

    public void editContact(View view) {
        int position = recyclerViewContacts.getChildAdapterPosition((View) view.getParent());
        Contact contact = contactsAdapter.getContactAtPosition(position);
        Intent intent = new Intent(MainActivity.this, EdicaoActivity.class);
        intent.putExtra("CONTACT_ID", contact.getId());
        startActivity(intent);
    }
}
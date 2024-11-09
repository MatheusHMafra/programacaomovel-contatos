package com.univali.contatos;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdicaoActivity extends AppCompatActivity {

    private EditText editTextFullName;
    private LinearLayout phoneContainer;
    private Button buttonAddPhone;
    private Button buttonSaveContact;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adicao);

        editTextFullName = findViewById(R.id.editTextFullName);
        phoneContainer = findViewById(R.id.phoneContainer);
        buttonAddPhone = findViewById(R.id.buttonAddPhone);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);
        dbHelper = new DatabaseHelper(this);

        buttonAddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneContainer.getChildCount() < 3) {
                    addPhoneField();
                }
                if (phoneContainer.getChildCount() >= 3) {
                    buttonAddPhone.setVisibility(View.GONE);
                }
            }
        });

        buttonSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    private void addPhoneField() {
        View phoneField = getLayoutInflater().inflate(R.layout.item_phone, null);
        phoneContainer.addView(phoneField);

        EditText editTextDDD = phoneField.findViewById(R.id.editTextDDD);
        editTextDDD.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        EditText editTextPhoneNumber = phoneField.findViewById(R.id.editTextPhoneNumber);
        editTextPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});

        Button buttonDeletePhone = phoneField.findViewById(R.id.buttonDeletePhone);
        if (buttonDeletePhone != null) {
            buttonDeletePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneContainer.removeView(phoneField);
                    if (phoneContainer.getChildCount() < 3) {
                        buttonAddPhone.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            throw new NullPointerException("buttonDeletePhone não encontrado no layout item_phone");
        }
    }

    private void saveContact() {
        String fullName = editTextFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Por favor, insira o nome completo", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasHome = false;
        boolean hasMobile = false;
        boolean hasWork = false;

        List<Phone> phones = new ArrayList<>();

        for (int i = 0; i < phoneContainer.getChildCount(); i++) {
            View phoneField = phoneContainer.getChildAt(i);
            EditText editTextDDD = phoneField.findViewById(R.id.editTextDDD);
            EditText editTextPhoneNumber = phoneField.findViewById(R.id.editTextPhoneNumber);
            Spinner spinnerPhoneType = phoneField.findViewById(R.id.spinnerPhoneType);

            String ddd = editTextDDD.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String phoneType = spinnerPhoneType.getSelectedItem().toString();

            if (TextUtils.isEmpty(ddd) || ddd.length() != 3) {
                Toast.makeText(this, "Por favor, insira um DDD válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 9) {
                Toast.makeText(this, "Por favor, insira um número de telefone válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneType.equals("Casa") && hasHome) {
                Toast.makeText(this, "Você já adicionou um número de telefone para Casa", Toast.LENGTH_SHORT).show();
                return;
            } else if (phoneType.equals("Celular") && hasMobile) {
                Toast.makeText(this, "Você já adicionou um número de telefone para Celular", Toast.LENGTH_SHORT).show();
                return;
            } else if (phoneType.equals("Trabalho") && hasWork) {
                Toast.makeText(this, "Você já adicionou um número de telefone para Trabalho", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneType.equals("Casa")) {
                hasHome = true;
            } else if (phoneType.equals("Celular")) {
                hasMobile = true;
            } else if (phoneType.equals("Trabalho")) {
                hasWork = true;
            }

            phones.add(new Phone(ddd, phoneNumber, phoneType));
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = dbHelper.saveContact(db, fullName);

        for (Phone phone : phones) {
            dbHelper.savePhone(db, userId, phone.getDdd(), phone.getPhoneNumber(), phone.getPhoneType());
        }

        db.close();

        Toast.makeText(this, "Contato salvo com sucesso", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Métodos para salvar o contato e os telefones no banco de dados
    // private void saveContact(String fullName) { ... }
    // private void savePhone(String fullName, String ddd, String phoneNumber, String phoneType) { ... }
}

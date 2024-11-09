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
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EdicaoActivity extends AppCompatActivity {

    private EditText editTextFullName;
    private LinearLayout phoneContainer;
    private Button buttonAddPhone;
    private Button buttonSaveContact;
    private DatabaseHelper dbHelper;
    private long contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicao);

        editTextFullName = findViewById(R.id.editTextFullName);
        phoneContainer = findViewById(R.id.phoneContainer);
        buttonAddPhone = findViewById(R.id.buttonAddPhone);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);
        dbHelper = new DatabaseHelper(this);

        contactId = getIntent().getLongExtra("CONTACT_ID", -1);
        if (contactId != -1) {
            loadContactData(contactId);
        }

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

    private void loadContactData(long contactId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Contact contact = dbHelper.getContactById(db, contactId);
        if (contact != null) {
            editTextFullName.setText(contact.getFullName());
            for (Phone phone : contact.getPhones()) {
                addPhoneField(phone);
            }
        }
        db.close();
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

    private void addPhoneField(Phone phone) {
        View phoneField = getLayoutInflater().inflate(R.layout.item_phone, null);
        phoneContainer.addView(phoneField);

        EditText editTextDDD = phoneField.findViewById(R.id.editTextDDD);
        editTextDDD.setText(phone.getDdd());
        editTextDDD.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        EditText editTextPhoneNumber = phoneField.findViewById(R.id.editTextPhoneNumber);
        editTextPhoneNumber.setText(phone.getPhoneNumber());
        editTextPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});

        Spinner spinnerPhoneType = phoneField.findViewById(R.id.spinnerPhoneType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phone_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhoneType.setAdapter(adapter);

        if (phone.getPhoneType().equals("Casa")) {
            spinnerPhoneType.setSelection(adapter.getPosition("Casa"));
        } else if (phone.getPhoneType().equals("Celular")) {
            spinnerPhoneType.setSelection(adapter.getPosition("Celular"));
        } else if (phone.getPhoneType().equals("Trabalho")) {
            spinnerPhoneType.setSelection(adapter.getPosition("Trabalho"));
        }

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
        dbHelper.updateContact(db, contactId, fullName);

        dbHelper.deletePhonesByContactId(db, contactId);
        for (Phone phone : phones) {
            dbHelper.savePhone(db, contactId, phone.getDdd(), phone.getPhoneNumber(), phone.getPhoneType());
        }

        db.close();

        Toast.makeText(this, "Contato atualizado com sucesso", Toast.LENGTH_SHORT).show();
        finish();
    }
}

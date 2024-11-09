package com.univali.contatos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contacts;

    public ContactsAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.textViewFullName.setText(contact.getFullName());

        StringBuilder phones = new StringBuilder();
        for (Phone phone : contact.getPhones()) {
            phones.append(String.format("(%s) %s - %s\n", phone.getDdd(), phone.getPhoneNumber(), phone.getPhoneType()));
        }
        holder.textViewPhone.setText(phones.toString().trim());

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) holder.itemView.getContext()).deleteContact(contact.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void updateContacts(List<Contact> newContacts) {
        contacts.clear();
        contacts.addAll(newContacts);
        notifyDataSetChanged();
    }

    public Contact getContactAtPosition(int position) {
        return contacts.get(position);
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFullName;
        TextView textViewPhone;
        Button buttonDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFullName = itemView.findViewById(R.id.textViewFullName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
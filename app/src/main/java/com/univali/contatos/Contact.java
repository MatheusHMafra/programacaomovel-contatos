package com.univali.contatos;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    private long id;
    private String fullName;
    private List<Phone> phones;

    public Contact(long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.phones = new ArrayList<>();
    }

    public Contact(long id, String fullName, List<Phone> phones) {
        this.id = id;
        this.fullName = fullName;
        this.phones = phones;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void addPhone(Phone phone) {
        phones.add(phone);
    }
}
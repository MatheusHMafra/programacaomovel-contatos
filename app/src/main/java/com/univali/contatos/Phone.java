
package com.univali.contatos;

public class Phone {
    private String ddd;
    private String phoneNumber;
    private String phoneType;

    public Phone(String ddd, String phoneNumber, String phoneType) {
        this.ddd = ddd;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
    }

    public String getDdd() {
        return ddd;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }
}
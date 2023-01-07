package com.example.myapplication;

public class ContactModel {
    String contactName;
    String contactNumber;

    public ContactModel(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}

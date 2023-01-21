package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String CONTACT_TABLE = "ContactInfo";
    private static String COLUMN_CONTACT_NAME = "contact_name";
    private static String COLUMN_CONTACT_NUMBER = "contact_number";

    private static String USER_TABLE = "UserInfo";
    private static String COLUMN_USER_NAME = "user_name";
    private static String COLUMN_USER_EMAIL ="user_email";
    private static String COLUMN_USER_PASSWORD ="user_password";

    public DataBaseHelper(Context context) {
        super(context, "Emergency.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createContactTable = "Create table "+CONTACT_TABLE+"(contact_id INTEGER PRIMARY KEY AUTOINCREMENT,"+COLUMN_CONTACT_NAME+" text, "+COLUMN_CONTACT_NUMBER+" text, user_id int )";
        db.execSQL(createContactTable);

        String userCreateTable = "CREATE TABLE "+USER_TABLE+"(user_id INTEGER PRIMARY KEY AUTOINCREMENT,"+COLUMN_USER_NAME+" TEXT,"+COLUMN_USER_EMAIL+" TEXT,"+COLUMN_USER_PASSWORD+" TEXT)";
        db.execSQL(userCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public boolean addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =new ContentValues();

        // Check if email already exists in the "user" table
        String query = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = '" + user.getEmail() + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            // Email already exists, return false
            return false;
        }
        cursor.close();

        // Email does not exist, proceed with inserting the new user
        cv.put(COLUMN_USER_NAME,user.getName());
        cv.put(COLUMN_USER_EMAIL,user.getEmail());
        cv.put(COLUMN_USER_PASSWORD,user.getPassword());

        long insert = db.insert(USER_TABLE, null, cv);
        db.close();
        if(insert == -1){
            return false;
        }else return true;
    }


    public User getUser(String userEmail, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Select query to retrieve user from "user" table
        String query = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = '" + userEmail + "' AND " + COLUMN_USER_PASSWORD + " = '" + password + "'";
        System.out.println(query);
        Cursor cursor = db.rawQuery(query, null);

        User user = null;
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            String name = cursor.getString(1);
            String email = cursor.getString(2);
            user = new User(userId, name, email, password);
        }
        cursor.close();
        db.close();
        return user;
    }


    public boolean addContact(ContactModel model,String userEmail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =new ContentValues();

        int userID = getUserId(userEmail);

        // Email does not exist, proceed with inserting the new user
        cv.put(COLUMN_CONTACT_NAME,model.getContactName());
        cv.put(COLUMN_CONTACT_NUMBER,model.getContactNumber());
        cv.put("user_id",userID);

        long insert = db.insert(CONTACT_TABLE, null, cv);
        db.close();
        if(insert == -1){
            return false;
        }else return true;
    }

    public int getUserId(String userEmail){
        SQLiteDatabase db = this.getReadableDatabase();

        // Select query to retrieve user from "user" table
        String query = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = '" + userEmail + "'";
        Cursor cursor = db.rawQuery(query, null);

        int userId = 0;
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }


    public List<ContactModel> getAllContacts(String userEmail){
        List<ContactModel> contacts = new ArrayList<>();

        int userId = getUserId(userEmail);

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+CONTACT_TABLE+" WHERE user_id = "+userId;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                int contactId = cursor.getInt(0);
                String contactName = cursor.getString(1);
                String contactNumber = cursor.getString(2);
                ContactModel model = new ContactModel(contactId, contactName, contactNumber);
                contacts.add(model);
            } while ((cursor.moveToNext()));
        }
        cursor.close();
        db.close();

        return contacts;
    }

    public boolean deleteContact(String contactName,String email){
        int userId = getUserId(email);
        String query = "Delete from "+CONTACT_TABLE+" where "+COLUMN_CONTACT_NAME +"= '"+contactName+"' and user_id"+ userId;

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(CONTACT_TABLE,"user-id ? and contact_name? ",new String[] {Integer.toString(userId),contactName}) > 0;
    }

}
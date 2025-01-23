package com.example.belajar_android_sturio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
//import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "product.db";
    private static final int DATABASE_VERSION = 2;

//    obat
    private static final String TABLE_NAME = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QTY = "qty";

//    checkout
    private static final String TABLE_CHECKOUT = "checkouts";
    private static final String COLUMN_CHECKOUT_ID = "id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_CHECKOUT_DATE = "checkout_date";
    private static final String COLUMN_CHECKOUT_QTY = "quantity";

//    user
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT UNIQUE, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_QTY + " INTEGER)";

        String createCheckoutTable = "CREATE TABLE " + TABLE_CHECKOUT + " (" +
                COLUMN_CHECKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_ID + " INTEGER, " +
                COLUMN_CHECKOUT_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_CHECKOUT_QTY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " +
                TABLE_NAME + "(" + COLUMN_ID + "))";

        String createUserTable = "CREATE TABLE " + TABLE_USER + "(" + COLUMN_USER_ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE, " + COLUMN_USER_PASSWORD + " TEXT)";


//        // add user1
        String plainPassUser1 = "bagas123";
        String passwordUser1 = BCrypt.hashpw(plainPassUser1, BCrypt.gensalt(10));
        String addUser1 = "INSERT INTO USER (name , email, password) VALUES (?, ?, ?)";
//
//        // add user2
        String plainPassUser2 = "rifqi321";
        String passwordUser2 = BCrypt.hashpw(plainPassUser2, BCrypt.gensalt(10));
        String addUser2 = "INSERT INTO USER (name, email, password) VALUES (?, ?, ?)";

        db.execSQL(createTable);
        db.execSQL(createCheckoutTable);
        db.execSQL(createUserTable);
        db.execSQL(addUser1, new Object[]{"Bagas Setyo N", "bagas123@gmail.com", passwordUser1});
        db.execSQL(addUser2, new Object[]{"Muhamad Rifqi", "rifqi321@gmail.com", passwordUser2});
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void saveOrUpdateProduct(String name, double price, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QTY, qty);

        long id = db.replace(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public void addToCheckout(long productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_CHECKOUT_QTY + " FROM " + TABLE_CHECKOUT + " WHERE " + COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            int existingQty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHECKOUT_QTY));
            ContentValues values = new ContentValues();
            values.put(COLUMN_CHECKOUT_QTY, existingQty + quantity);
            db.update(TABLE_CHECKOUT, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_ID, productId);
            values.put(COLUMN_CHECKOUT_QTY, quantity);
            db.insert(TABLE_CHECKOUT, null, values);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }


    public void deleteProduct(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor getCheckoutItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT c.id, p.name, p.price, c.quantity " +
                        "FROM " + TABLE_CHECKOUT + " c " +
                        "JOIN " + TABLE_NAME + " p ON c.product_id = p.id",
                null
        );
    }

    public void clearCheckout() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHECKOUT, null, null);
        db.close();
    }

    public int getCurrentStock(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{"qty"}, // Ganti "stock" jadi "qty"
                "name = ?",
                new String[]{productName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int currentStock = cursor.getInt(cursor.getColumnIndexOrThrow("qty")); // Ganti "stock" jadi "qty"
            cursor.close();
            return currentStock;
        }
        return 0;
    }

    public void updateStock(String productName, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("qty", newStock);

        db.update(TABLE_NAME, values, "name = ?", new String[]{productName});
        db.close(); // Tambahkan close database
    }


    //    user
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isMatch = false;

        Cursor cursor = db.rawQuery("SELECT password FROM USER WHERE email = ?", new String[]{email});

        // cek
        if (cursor != null && cursor.moveToFirst()) {
            String storedHash = cursor.getString(0);
            isMatch = BCrypt.checkpw(password, storedHash);
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isMatch;
    }
}
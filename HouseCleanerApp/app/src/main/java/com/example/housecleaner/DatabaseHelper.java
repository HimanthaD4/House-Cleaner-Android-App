package com.example.housecleaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HouseCleaner.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_USER_TYPE = "user_type";

    private static final String TABLE_HOUSES = "houses";
    private static final String COL_HOUSE_ID = "house_id";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_LOCATION = "location";
    private static final String COL_ROOMS = "rooms";
    private static final String COL_BATHROOMS = "bathrooms";
    private static final String COL_FLOOR_TYPE = "floor_type";
    private static final String COL_CONTACT = "contact";
    private static final String COL_IMAGE = "image";
    private static final String COL_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT, " +
                COL_USER_TYPE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HOUSES + " (" +
                COL_HOUSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER, " +
                COL_LOCATION + " TEXT, " +
                COL_ROOMS + " INTEGER, " +
                COL_BATHROOMS + " INTEGER, " +
                COL_FLOOR_TYPE + " TEXT, " +
                COL_CONTACT + " TEXT, " +
                COL_IMAGE + " TEXT, " +
                COL_STATUS + " TEXT DEFAULT 'available')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOUSES);
        onCreate(db);
    }

    public boolean insertUser(String name, String email, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_USER_TYPE, userType);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    public boolean addHouse(House house, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_LOCATION, house.getLocation());
        values.put(COL_ROOMS, house.getRooms());
        values.put(COL_BATHROOMS, house.getBathrooms());
        values.put(COL_FLOOR_TYPE, house.getFloorType());
        values.put(COL_CONTACT, house.getContact());
        values.put(COL_IMAGE, house.getImage());
        return db.insert(TABLE_HOUSES, null, values) != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID}, COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) return cursor.getInt(0);
        cursor.close();
        return -1;
    }

    public String getUserType(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_TYPE}, COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) return cursor.getString(0);
        cursor.close();
        return null;
    }

    public List<House> getHousesByUserId(int userId) {
        List<House> houses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HOUSES + " WHERE " +
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                House house = new House(
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7));
                house.setId(cursor.getInt(0));
                house.setUserId(cursor.getInt(1));
                house.setStatus(cursor.getString(8));
                houses.add(house);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return houses;
    }

    public List<House> getAvailableHouses() {
        List<House> houses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HOUSES + " WHERE " +
                COL_STATUS + "=?", new String[]{"available"});
        if (cursor.moveToFirst()) {
            do {
                House house = new House(
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7));
                house.setId(cursor.getInt(0));
                house.setUserId(cursor.getInt(1));
                house.setStatus(cursor.getString(8));
                houses.add(house);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return houses;
    }

    public boolean acceptJob(int houseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, "accepted");
        return db.update(TABLE_HOUSES, values, COL_HOUSE_ID + "=?",
                new String[]{String.valueOf(houseId)}) > 0;
    }

    public boolean deleteHouse(int houseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HOUSES, COL_HOUSE_ID + "=?",
                new String[]{String.valueOf(houseId)}) > 0;
    }

    public House getHouseById(int houseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, COL_HOUSE_ID + "=?",
                new String[]{String.valueOf(houseId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            House house = new House(
                    cursor.getString(cursor.getColumnIndex(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COL_ROOMS)),
                    cursor.getString(cursor.getColumnIndex(COL_BATHROOMS)),
                    cursor.getString(cursor.getColumnIndex(COL_FLOOR_TYPE)),
                    cursor.getString(cursor.getColumnIndex(COL_CONTACT)),
                    cursor.getString(cursor.getColumnIndex(COL_IMAGE))
            );
            house.setId(cursor.getInt(cursor.getColumnIndex(COL_HOUSE_ID)));
            house.setStatus(cursor.getString(cursor.getColumnIndex(COL_STATUS)));
            cursor.close();
            return house;
        }
        return null;
    }
}
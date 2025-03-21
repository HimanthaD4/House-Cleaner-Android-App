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
    private static final int DATABASE_VERSION = 6; // Incremented version for schema changes

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_HOUSES = "houses";
    private static final String TABLE_COMMENTS = "comments";

    // Common column names
    private static final String COL_ID = "id";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_HOUSE_ID = "house_id";

    // Users table columns
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_USER_TYPE = "user_type";

    // Houses table columns
    private static final String COL_LOCATION = "location";
    private static final String COL_ROOMS = "rooms";
    private static final String COL_BATHROOMS = "bathrooms";
    private static final String COL_FLOOR_TYPE = "floor_type";
    private static final String COL_CONTACT = "contact";
    private static final String COL_IMAGE = "image";
    private static final String COL_STATUS = "status";
    private static final String COL_PRICE = "price"; // Added price column

    // Comments table columns
    private static final String COL_COMMENT_ID = "comment_id";
    private static final String COL_PARENT_ID = "parent_id";
    private static final String COL_COMMENT_TEXT = "comment_text";
    private static final String COL_TIMESTAMP = "timestamp";

    // Price calculation constants
    private static final int BASE_PRICE = 2000; // Base price in LKR
    private static final int ROOM_PRICE = 500; // Price per room
    private static final int BATHROOM_PRICE = 300; // Price per bathroom

    // Singleton instance
    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " TEXT,"
                + COL_EMAIL + " TEXT UNIQUE,"
                + COL_PASSWORD + " TEXT,"
                + COL_USER_TYPE + " TEXT)");

        // Create houses table with price column
        db.execSQL("CREATE TABLE " + TABLE_HOUSES + "("
                + COL_HOUSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USER_ID + " INTEGER,"
                + COL_LOCATION + " TEXT,"
                + COL_ROOMS + " INTEGER,"
                + COL_BATHROOMS + " INTEGER,"
                + COL_FLOOR_TYPE + " TEXT,"
                + COL_CONTACT + " TEXT,"
                + COL_IMAGE + " TEXT,"
                + COL_STATUS + " TEXT DEFAULT 'available',"
                + COL_PRICE + " INTEGER)"); // Added price column

        // Create comments table
        db.execSQL("CREATE TABLE " + TABLE_COMMENTS + "("
                + COL_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_HOUSE_ID + " INTEGER,"
                + COL_USER_ID + " INTEGER,"
                + COL_PARENT_ID + " INTEGER DEFAULT 0,"
                + COL_COMMENT_TEXT + " TEXT,"
                + COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOUSES);
        onCreate(db);
    }

    // Singleton method
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // User operations (unchanged)
    public boolean insertUser(String name, String email, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_USER_TYPE, userType);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE "
                + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID}, COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public String getUserType(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_TYPE}, COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            String type = cursor.getString(0);
            cursor.close();
            return type;
        }
        cursor.close();
        return null;
    }

    // House operations with price calculation
    public boolean addHouse(House house, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Calculate price
        int price = calculateCleaningPrice(
                Integer.parseInt(house.getRooms()),
                Integer.parseInt(house.getBathrooms()),
                house.getFloorType()
        );

        values.put(COL_USER_ID, userId);
        values.put(COL_LOCATION, house.getLocation());
        values.put(COL_ROOMS, house.getRooms());
        values.put(COL_BATHROOMS, house.getBathrooms());
        values.put(COL_FLOOR_TYPE, house.getFloorType());
        values.put(COL_CONTACT, house.getContact());
        values.put(COL_IMAGE, house.getImage());
        values.put(COL_PRICE, price); // Add calculated price

        return db.insert(TABLE_HOUSES, null, values) != -1;
    }

    // Price calculation logic
    private int calculateCleaningPrice(int rooms, int bathrooms, String floorType) {
        // Base price components
        int roomCharge = rooms * ROOM_PRICE;
        int bathroomCharge = bathrooms * BATHROOM_PRICE;

        // Floor type multipliers
        double floorMultiplier = 1.0;
        switch (floorType.toLowerCase()) {
            case "hardwood":
                floorMultiplier = 1.5;
                break;
            case "tile":
                floorMultiplier = 1.0;
                break;
            case "carpet":
                floorMultiplier = 1.2;
                break;
            case "laminate":
                floorMultiplier = 1.3;
                break;
            case "concrete":
                floorMultiplier = 1.1;
                break;
            default:
                floorMultiplier = 1.0;
        }

        // Calculate total price
        return (int) ((BASE_PRICE + roomCharge + bathroomCharge) * floorMultiplier);
    }

    // Updated house retrieval methods to include price
    public List<House> getHousesByUserId(int userId) {
        List<House> houses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HOUSES + " WHERE "
                + COL_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                House house = createHouseFromCursor(cursor);
                houses.add(house);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return houses;
    }

    public List<House> getAvailableHouses() {
        List<House> houses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HOUSES + " WHERE "
                + COL_STATUS + "=?", new String[]{"available"});

        if (cursor.moveToFirst()) {
            do {
                House house = createHouseFromCursor(cursor);
                houses.add(house);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return houses;
    }

    public House getHouseById(int houseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOUSES, null, COL_HOUSE_ID + "=?",
                new String[]{String.valueOf(houseId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            House house = createHouseFromCursor(cursor);
            cursor.close();
            return house;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // Helper method to create House object from cursor
    private House createHouseFromCursor(Cursor cursor) {
        House house = new House(
                cursor.getString(cursor.getColumnIndex(COL_LOCATION)),
                cursor.getString(cursor.getColumnIndex(COL_ROOMS)),
                cursor.getString(cursor.getColumnIndex(COL_BATHROOMS)),
                cursor.getString(cursor.getColumnIndex(COL_FLOOR_TYPE)),
                cursor.getString(cursor.getColumnIndex(COL_CONTACT)),
                cursor.getString(cursor.getColumnIndex(COL_IMAGE))
        );
        house.setId(cursor.getInt(cursor.getColumnIndex(COL_HOUSE_ID)));
        house.setUserId(cursor.getInt(cursor.getColumnIndex(COL_USER_ID)));
        house.setStatus(cursor.getString(cursor.getColumnIndex(COL_STATUS)));
        house.setPrice(cursor.getString(cursor.getColumnIndex(COL_PRICE))); // Add price
        return house;
    }

    // Other house operations (unchanged)
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

    // Comment operations (unchanged)
    public boolean addComment(int houseId, int userId, String comment, int parentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HOUSE_ID, houseId);
        values.put(COL_USER_ID, userId);
        values.put(COL_PARENT_ID, parentId);
        values.put(COL_COMMENT_TEXT, comment);
        return db.insert(TABLE_COMMENTS, null, values) != -1;
    }

    public List<Comment> getCommentsByHouseId(int houseId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COL_COMMENT_TEXT + ", " + COL_TIMESTAMP +
                " FROM " + TABLE_COMMENTS +
                " WHERE " + COL_HOUSE_ID + " = ?" +
                " ORDER BY " + COL_TIMESTAMP + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(houseId)});

        if (cursor.moveToFirst()) {
            do {
                comments.add(new Comment(
                        cursor.getString(0),  // comment text
                        cursor.getString(1)   // timestamp
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return comments;
    }

    public boolean updateHouseStatus(int houseId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, newStatus);
        return db.update(TABLE_HOUSES, values, COL_HOUSE_ID + "=?",
                new String[]{String.valueOf(houseId)}) > 0;
    }
}
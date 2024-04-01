package com.example.androidactivitylifecycle;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {


    private static final String DB_NAME = "auditPrompt";

    private static final int DB_VERSION = 1;

    private static final String PROMPT_TABLE_NAME = "prompt";

    private static final String RESPONSE_TABLE_NAME = "response";

    private static final String SEQ_NUM = "id";

    private static final String TIMESTAMP = "timestamp";

    private static final String PROMPT = "prompt";

    private static final String RESPONSE = "response";



    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + PROMPT_TABLE_NAME + " ("
                + SEQ_NUM + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TIMESTAMP + " TEXT,"
                + PROMPT + " VARCHAR(1024))";

        String query2 = "CREATE TABLE " + RESPONSE_TABLE_NAME + " ("
                + SEQ_NUM + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TIMESTAMP + " TEXT,"
                + RESPONSE + " VARCHAR(4096))";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
        db.execSQL(query2);

    }

    // this method is use to add new course to our sqlite database.
    public void addNewPrompt(Integer id, String timestamp, String prompt) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SEQ_NUM, id);
        values.put(TIMESTAMP, timestamp);
        values.put(PROMPT, prompt);

        db.insert(PROMPT_TABLE_NAME, null, values);


        db.close();
    }

    public void addNewResponse(Integer id, String timestamp, String response) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SEQ_NUM, id);
        values.put(TIMESTAMP, timestamp);
        values.put(RESPONSE, response);

        db.insert(RESPONSE_TABLE_NAME, null, values);


        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + PROMPT_TABLE_NAME);
        onCreate(db);
    }
}
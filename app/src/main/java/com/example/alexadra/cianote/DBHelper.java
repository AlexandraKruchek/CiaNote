package com.example.alexadra.cianote;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public  static final int DATABASE_VERSION = 1;
    public  static final String DATABASE_NAME = "noteDb";
    public  static final String TABLE_NOTES = "notes";
    public  static final String TABLE_LIST = "work_list";
    public  static final String TABLE_SUBTASK = "subtask";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NOTE = "note";

    public static final String KEY_NAME = "name";
    public static final String KEY_STEXT = "subtask_text";
    public static final String KEY_CREATE_DATE = "date_create";
    public static final String KEY_REMINDER = "reminder";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_NOTED = "noted";
    public static final String KEY_CHECKED = "checked";
    public static final String KEY_TASK = "main_task";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NOTES + "(" + KEY_ID
                + " integer primary key," + KEY_TITLE + " text," + KEY_NOTE + " text" + ")");

        db.execSQL("create table " + TABLE_LIST + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_CREATE_DATE + " date,"
                + KEY_REMINDER + " datetime," + "" + "" + KEY_PRIORITY + " integer," + KEY_NOTED + " boolean" + ")");

        db.execSQL("create table " + TABLE_SUBTASK + "(" + KEY_ID
                + " integer primary key," + KEY_STEXT + " text," + "KEY_CHECKED" + " integer,"
                + KEY_TASK + " integer," + "foreign key " + "(" + KEY_TASK + ")" + " references "
                + TABLE_LIST + "(" + KEY_ID + ")" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NOTES);

        onCreate(db);

    }
}


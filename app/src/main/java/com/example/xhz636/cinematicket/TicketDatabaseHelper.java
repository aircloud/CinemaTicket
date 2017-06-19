package com.example.xhz636.cinematicket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TicketDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_BOOK = "create table ticket ( "
            + "id integer primary key autoincrement,"
            + "userid text,"
            + "movie text,"
            + "cinema text,"
            + "begintime text,"
            + "hall text,"
            + "dimension text,"
            + "row int,"
            + "column int,"
            + "ordernumber text)";
    private Context context;

    public TicketDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists ticket");
        onCreate(db);
    }

}

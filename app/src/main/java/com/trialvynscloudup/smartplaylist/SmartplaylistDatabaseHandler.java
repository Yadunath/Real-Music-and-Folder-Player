package com.trialvynscloudup.smartplaylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yedunath on 9/1/16.
 */
public class SmartplaylistDatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "smartPlaylist";

    private static final String TABLE_NAME="mostlyPlayed";

    private static final String KEY_ID="id";

    private static final String KEY_TITLE="title";

    private static final String KEY_ALBUM="album";

    private static final String KEY_ARTIST="artist";

    private static final String KEY_DATAPATH="datapath";

    private static final String KEY_ALBUMID="albumid";


    public SmartplaylistDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+ KEY_ID+ " INTEGER PRIMARY KEY,"+KEY_TITLE+" TEXT,"
                +KEY_ALBUM+" TEXT,"+KEY_ARTIST+" TEXT,"+KEY_DATAPATH+" TEXT,"+KEY_ALBUMID+" TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }
    /*      Add new song details to mostly played table*/
    public void addValues()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(KEY_TITLE,"");
        contentValues.put(KEY_ALBUM,"");
        contentValues.put(KEY_ARTIST,"");
        contentValues.put(KEY_DATAPATH,"");
        contentValues.put(KEY_ALBUMID,"");
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        sqLiteDatabase.close();
    }
}

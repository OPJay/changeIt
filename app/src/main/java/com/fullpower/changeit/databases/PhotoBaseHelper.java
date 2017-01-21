package com.fullpower.changeit.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fullpower.changeit.databases.PhotoDbSchema.PhotoTable;

/**
 * Created by OJaiswal153939 on 1/8/2016.
 */
public class PhotoBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "photoBase.db";
    public PhotoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PhotoTable.NAME+ "(" +
                PhotoTable.Cols.IMAGEID+
                " TEXT PRIMARY KEY UNIQUE," +
                //PhotoTable.Cols.IMAGEID + ", " +
                PhotoTable.Cols.TITLE + ", " +
                PhotoTable.Cols.URL + ", " +
                PhotoTable.Cols.PROFILE_URL + ", " +
                PhotoTable.Cols.USER_URL + ", " +
                PhotoTable.Cols.TAGS + ", " +
                PhotoTable.Cols.DESCRIPTION +"," +
                PhotoTable.Cols.IMAGETYPE+","+
                PhotoTable.Cols.USERNAME+","+
                PhotoTable.Cols.SMALLIMAGEURL+
                ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int
            newVersion) {
    }
}

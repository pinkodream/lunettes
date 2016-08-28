package com.pinkodream.lunettes.business.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pinkodream.lunettes.global.App;
import com.pinkodream.lunettes.global.Config;

public class DBManager {

    private static DBHelper mDB;
    private static SQLiteDatabase mReadableDB;
    private static SQLiteDatabase mWritableDB;

    public static SQLiteDatabase getReadableDB() {
        if (mReadableDB == null) {
            mReadableDB = getDB().getReadableDatabase();
        }
        return mReadableDB;
    }

    public static SQLiteDatabase getWritableDB() {
        if (mWritableDB == null) {
            mWritableDB = getDB().getWritableDatabase();
        }
        return mWritableDB;
    }

    public static void close() {
        if (mReadableDB != null) {
            mReadableDB.close();
            mReadableDB = null;
        }
        if (mWritableDB != null) {
            mWritableDB.close();
            mWritableDB = null;
        }
    }

    private static DBHelper getDB() {
        if (mDB == null) {
            mDB = new DBHelper(App.getContext());
        }
        return mDB;
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, Config.DB_NAME, null, Config.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(BlogTable.CREATE_SQL);
            db.execSQL(ArticleTable.CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

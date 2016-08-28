package com.pinkodream.lunettes.business.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pinkodream.lunettes.data.Article;

public class ArticleTable extends DBTable {

    private static final String TAG = "ArticleTable";
    private static final String TABLE_NAME = "article";

    private static final String KEY_ID = "id";
    private static final String KEY_BLOG_ID = "blog_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LINK = "link";
    private static final String KEY_THUMBNAIL_URL = "thumbnail_url";
    private static final String KEY_PUBLISH_DATE = "publish_date";


    public static final String CREATE_SQL = "create table if not exists " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_BLOG_ID + " INTEGER, "
            + KEY_TITLE + " TEXT, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_LINK + " TEXT, "
            + KEY_THUMBNAIL_URL + " TEXT, "
            + KEY_PUBLISH_DATE + " TEXT);";

    public static final String[] ALL_COLUMN = new String[]{
            KEY_ID, KEY_BLOG_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_LINK, KEY_THUMBNAIL_URL, KEY_PUBLISH_DATE
    };


    public static boolean insertOrUpdateArticles(ArrayList<Article> articles) {
        boolean ret = false;
        if (articles != null && articles.size() > 0) {
            SQLiteDatabase db = null;
            try {
                db = DBManager.getWritableDB();
                db.beginTransaction();
                for (Article article : articles) {
                    ContentValues cv = new ContentValues();
                    cv.put(KEY_ID, article.getId());
                    cv.put(KEY_BLOG_ID, article.blogId);
                    cv.put(KEY_TITLE, escape(article.title));
                    cv.put(KEY_DESCRIPTION, escape(article.description));
                    cv.put(KEY_LINK, escape(article.link));
                    cv.put(KEY_THUMBNAIL_URL, escape(article.thumbnailUrl));
                    cv.put(KEY_PUBLISH_DATE, article.publishDate);
                    if (isArticleExists(article)) {
                        db.update(TABLE_NAME, cv, eq(KEY_ID, article.getId()), null);
                    } else {
                        db.insert(TABLE_NAME, null, cv);
                    }
                }
                db.setTransactionSuccessful();
                ret = true;
            } catch (Exception e) {
                Log.e(TAG, "[insertOrUpdateArticles] " + e.toString());
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                }
            }
        }
        return ret;
    }

    public static ArrayList<Article> queryArticles(long blogId) {
        ArrayList<Article> articles = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = DBManager.getReadableDB();
            cursor = db.query(TABLE_NAME, ALL_COLUMN, eq(KEY_BLOG_ID, blogId), null, null, null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Article article = new Article(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)), blogId);
                article.title = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                article.description = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                article.link = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LINK)));
                article.thumbnailUrl = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_THUMBNAIL_URL)));
                article.publishDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PUBLISH_DATE));
                articles.add(article);
            }
        } catch (Exception e) {
            Log.e(TAG, "[queryArticles] " + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return articles;
    }

    public static boolean isArticleExists(Article article) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = DBManager.getReadableDB();
            cursor = db.query(TABLE_NAME, new String[]{KEY_ID}, eq(KEY_LINK, article.link), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                exists = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "[isArticleExists] " + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
}

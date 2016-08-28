package com.pinkodream.lunettes.business.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pinkodream.lunettes.data.Blog;

public class BlogTable extends DBTable {

    private static final String TAG = "BlogTable";
    private static final String TABLE_NAME = "blog";

    private static final String KEY_ID = "id";
    private static final String KEY_RSS_URL = "rss_url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LINK = "link";
    private static final String KEY_IMAGE_URL = "image_url";

    public static final String CREATE_SQL = "create table if not exists " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_RSS_URL + " TEXT, "
            + KEY_TITLE + " TEXT, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_LINK + " TEXT, "
            + KEY_IMAGE_URL + " TEXT);";

    public static final String[] ALL_COLUMN = new String[]{
            KEY_ID, KEY_RSS_URL, KEY_TITLE, KEY_DESCRIPTION, KEY_LINK, KEY_IMAGE_URL
    };

    public static boolean insertOrUpdateBlog(Blog blog) {
        boolean ret = false;
        if (blog != null) {
            SQLiteDatabase db = null;
            try {
                db = DBManager.getWritableDB();
                db.beginTransaction();
                ContentValues cv = new ContentValues();
                cv.put(KEY_ID, blog.getId());
                cv.put(KEY_RSS_URL, escape(blog.rssUrl));
                cv.put(KEY_TITLE, escape(blog.title));
                cv.put(KEY_DESCRIPTION, escape(blog.description));
                cv.put(KEY_LINK, escape(blog.link));
                cv.put(KEY_IMAGE_URL, escape(blog.imageUrl));
                if (isBlogExists(blog.rssUrl)) {
                    db.update(TABLE_NAME, cv, eq(KEY_ID, blog.getId()), null);
                } else {
                    db.insert(TABLE_NAME, null, cv);
                }

                if (blog.articleList != null && blog.articleList.size() > 0) {
                    ArticleTable.insertOrUpdateArticles(blog.articleList);
                }

                db.setTransactionSuccessful();
                ret = true;
            } catch (Exception e) {
                Log.e(TAG, "[insertOrUpdateBlog] " + e.toString());
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                }
            }
        }
        return ret;
    }

    public static ArrayList<String> queryRssUrls() {
        ArrayList<String> urls = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = DBManager.getReadableDB();
            cursor = db.query(TABLE_NAME, new String[]{KEY_RSS_URL}, null, null, null, null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                urls.add(unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RSS_URL))));
            }
        } catch (Exception e) {
            Log.e(TAG, "[queryRssUrls] " + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return urls;
    }

    public static ArrayList<Blog> queryAllBlog() {
        ArrayList<Blog> blogList = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = DBManager.getReadableDB();
            cursor = db.query(TABLE_NAME, ALL_COLUMN, null, null, null, null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Blog blog = new Blog(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
                blog.rssUrl = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RSS_URL)));
                blog.title = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                blog.description = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                blog.link = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LINK)));
                blog.imageUrl = unescape(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL)));
                blog.articleList = ArticleTable.queryArticles(blog.getId());
                blogList.add(blog);
            }
        } catch (Exception e) {
            Log.e(TAG, "[queryBlog] " + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return blogList;
    }

    public static boolean isBlogExists(String url) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = DBManager.getReadableDB();
            cursor = db.query(TABLE_NAME, new String[]{KEY_ID}, eq(KEY_RSS_URL, url), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                exists = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "[isBlogExists] " + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
}

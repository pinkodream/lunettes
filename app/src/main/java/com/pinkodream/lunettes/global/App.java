package com.pinkodream.lunettes.global;

import java.util.concurrent.atomic.AtomicLong;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.pinkodream.lunettes.business.RSSHelper;
import com.pinkodream.lunettes.business.db.DBManager;

public class App extends Application {

    private static final String TAG = "App";
    private static Context sContext;
    private static AtomicLong sGenerator;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        long createTime = System.currentTimeMillis();
        sGenerator = new AtomicLong(createTime);
        Log.i(TAG, "onCreate time=" + createTime);
    }

    public static void exit() {
        Log.i(TAG, "[exit]");
        RSSHelper.get().destroy();
        DBManager.close();
    }

    public static Context getContext() {
        return sContext;
    }

    public static long generateId() {
        return sGenerator.getAndIncrement();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
    }

    public static boolean isUrlLegal(String url) {
        return !isEmpty(url) && (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://"));
    }
}

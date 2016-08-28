package com.pinkodream.lunettes.business;

import java.util.ArrayList;

import android.util.Log;

import com.pinkodream.lunettes.business.db.BlogTable;
import com.pinkodream.lunettes.business.network.Network;
import com.pinkodream.lunettes.business.network.RSSRequest.OnRSSResponseListener;
import com.pinkodream.lunettes.data.Blog;
import com.pinkodream.lunettes.data.Event;
import com.pinkodream.lunettes.global.App;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class RSSHelper {

    private static final String TAG = "RSSHelper";

    public static RSSHelper get() {
        return InstanceHolder.INSTANCE;
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    public ArrayList<Blog> getAllBlogList() {
        return BlogTable.queryAllBlog();
    }

    public void updateBlogList() {
        EventBus.getDefault().post(new Event(Event.TYPE_UPDATE_BLOG_LIST));
    }

    public void addRSS(String url) {
        EventBus.getDefault().post(new Event(Event.TYPE_ADD_RSS, url));
    }

    public void updateBlog(String url) {
        updateBlog(url, Event.TYPE_UPDATE_BLOG_RESULT);
    }

    private void updateBlog(String url, final int eventType) {
        Network.get().requestRSS(url, new OnRSSResponseListener() {
            @Override
            public void onResponse(int code, Blog blog) {
                Log.i(TAG, "[updateBlogList.onResponse] code=" + code);
                if (code == OnRSSResponseListener.SUCCESS && blog != null) {
                    boolean ret = BlogTable.insertOrUpdateBlog(blog);
                    Log.i(TAG, "[updateBlogList.onResponse.insertOrUpdateBlog] ret=" + ret);
                    EventBus.getDefault().post(new Event(eventType, Event.SUCCESS, blog));
                } else {
                    EventBus.getDefault().post(new Event(eventType, Event.ERROR, null));
                }
            }
        });
    }

    private void updateBlogListInner() {
        ArrayList<String> urls = BlogTable.queryRssUrls();
        if (urls != null && urls.size() > 0) {
            for (String url : urls) {
                updateBlog(url, Event.TYPE_UPDATE_BLOG_LIST_RESULT);
            }
        } else {
            EventBus.getDefault().post(new Event(Event.TYPE_UPDATE_BLOG_LIST_RESULT, Event.EMPTY, null));
        }
    }

    private void addRSSInner(String url) {
        if (App.isUrlLegal(url)) {
            if (!BlogTable.isBlogExists(url)) {
                updateBlog(url, Event.TYPE_ADD_RSS_RESULT);
            } else {
                EventBus.getDefault().post(new Event(Event.TYPE_ADD_RSS_RESULT, Event.REPEAT, null));
            }
        } else {
            EventBus.getDefault().post(new Event(Event.TYPE_ADD_RSS_RESULT, Event.BAD_URL, null));
        }
    }

    @Subscribe(threadMode = ThreadMode.BackgroundThread)
    public void handleBlckgroundEvent(Event event) {
        switch (event.type) {
            case Event.TYPE_UPDATE_BLOG_LIST:
                updateBlogListInner();
                break;
            case Event.TYPE_ADD_RSS:
                addRSSInner((String) event.data);
                break;
        }
    }

    private RSSHelper() {
        EventBus.getDefault().register(this);
    }

    private static class InstanceHolder {
        public static final RSSHelper INSTANCE = new RSSHelper();
    }

}

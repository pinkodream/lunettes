package com.pinkodream.lunettes.business.network;

import android.os.Build.VERSION;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.pinkodream.lunettes.business.network.RSSRequest.OnRSSResponseListener;
import com.pinkodream.lunettes.global.App;

public class Network {

    private static final String TAG = "Network";
    private ImageLoader mImageLoader;
    private RequestQueue mQueue;

    public static Network get() {
        return InstanceHolder.INSTANCE;
    }

    public void requestRSS(String url, OnRSSResponseListener listener) {
        mQueue.add(new RSSRequest(url, listener));
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            ImageCache cache = VERSION.SDK_INT >= 12 ? new LruImageCache() : new NoImageCache();
            mImageLoader = new ImageLoader(mQueue, cache);
        }
        return mImageLoader;
    }

    private Network() {
        mQueue = Volley.newRequestQueue(App.getContext());
    }

    private static class InstanceHolder {
        public static final Network INSTANCE = new Network();
    }

}

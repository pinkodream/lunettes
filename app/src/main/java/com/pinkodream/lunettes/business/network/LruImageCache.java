package com.pinkodream.lunettes.business.network;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.pinkodream.lunettes.global.Config;

@TargetApi(12)
public class LruImageCache implements ImageCache {

    private LruCache<String, Bitmap> mCache;

    public LruImageCache() {
        mCache = new LruCache<String, Bitmap>(Config.IMAGE_CACHE_MAX_SIZE) {
            @Override
            protected int sizeOf(String url, Bitmap bitmap) {
                if (bitmap != null) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
                return 1;
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }
}

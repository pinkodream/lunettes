package com.pinkodream.lunettes.business.network;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class NoImageCache implements ImageCache {

    @Override
    public Bitmap getBitmap(String url) {
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
    }
}

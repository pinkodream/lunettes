package com.pinkodream.lunettes.business.network;

import java.io.ByteArrayInputStream;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.pinkodream.lunettes.business.parser.XmlParser;
import com.pinkodream.lunettes.data.Blog;

public class RSSRequest extends Request<Blog> {

    private OnRSSResponseListener mListener;

    public RSSRequest(String url, final OnRSSResponseListener listener) {
        super(Method.GET, url, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onResponse(OnRSSResponseListener.ERROR, null);
                }
            }
        });
        mListener = listener;
    }

    @Override
    protected Response<Blog> parseNetworkResponse(NetworkResponse response) {
        if (response != null && response.data != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(response.data);
            Blog blog = XmlParser.parse(bis);
            blog.rssUrl = getUrl();
            return Response.success(blog, null);
        }
        return Response.error(new VolleyError("response is null or response.data is null"));
    }

    @Override
    protected void deliverResponse(Blog response) {
        if (mListener != null) {
            mListener.onResponse(OnRSSResponseListener.SUCCESS, response);
        }
    }

    public interface OnRSSResponseListener {
        int SUCCESS = 1;
        int ERROR = 0;

        void onResponse(int code, Blog blog);
    }
}

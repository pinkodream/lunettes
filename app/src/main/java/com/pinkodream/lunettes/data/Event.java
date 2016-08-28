package com.pinkodream.lunettes.data;

public class Event {

    public static final int TYPE_UPDATE_BLOG_RESULT = 1;
    public static final int TYPE_UPDATE_BLOG_LIST = 2;
    public static final int TYPE_UPDATE_BLOG_LIST_RESULT = 3;
    public static final int TYPE_ADD_RSS = 4;
    public static final int TYPE_ADD_RSS_RESULT = 5;
    public static final int TYPE_REFRESH_BLOG_LIST_VIEW = 6;

    public static final int NONE = 0;
    public static final int SUCCESS = 1;
    public static final int EMPTY = 2;
    public static final int REPEAT = 3;
    public static final int ERROR = -1;
    public static final int BAD_URL = -2;

    public final int type;
    public final int code;
    public final Object data;

    public Event(int type) {
        this(type, NONE, null);
    }

    public Event(int type, Object data) {
        this(type, NONE, data);
    }

    public Event(int type, int code, Object data) {
        this.type = type;
        this.code = code;
        this.data = data;
    }
}

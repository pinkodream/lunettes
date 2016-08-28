package com.pinkodream.lunettes.business.db;

import com.pinkodream.lunettes.global.App;

public class DBTable {

    protected static String escape(String source) {
        if (!App.isEmpty(source)) {
            source = source.replaceAll("'", "''").replaceAll("/", "//");
        }
        return source;
    }

    protected static String unescape(String source) {
        if (!App.isEmpty(source)) {
            source = source.replaceAll("''", "'").replaceAll("//", "/");
        }
        return source;
    }

    protected static String eq(String key, String value) {
        return key + "='" + escape(value) + "'";
    }

    protected static String eq(String key, long value) {
        return key + "=" + value;
    }
}

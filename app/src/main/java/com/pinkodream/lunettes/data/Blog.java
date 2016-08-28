package com.pinkodream.lunettes.data;

import java.util.ArrayList;

import com.pinkodream.lunettes.global.App;

public class Blog {

    private long id;
    public String rssUrl;
    public String title;
    public String description;
    public String link;
    public String imageUrl;
    public String lastBuildDate;
    public ArrayList<Article> articleList; // blog articles

    public Blog() {
        this(App.generateId());
    }

    public Blog(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}

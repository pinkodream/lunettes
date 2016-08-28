package com.pinkodream.lunettes.data;

import com.pinkodream.lunettes.global.App;

public class Article {

    private long id;
    public long blogId;
    public String title;
    public String description;
    public String link;
    public String thumbnailUrl;
    public String publishDate;

    public Article(long blogId) {
        this(App.generateId(), blogId);
    }

    public Article(long id, long blogId) {
        this.id = id;
        this.blogId = blogId;
    }

    public long getId() {
        return this.id;
    }
}

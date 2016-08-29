package com.pinkodream.lunettes.business.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.util.Log;
import android.util.Xml;

import com.pinkodream.lunettes.data.Article;
import com.pinkodream.lunettes.data.Blog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlParser {

    private static final String TAG = "XmlParser";

    private static final String BLOG_CHANNEL = "channel";

    private static final String BLOG_TITLE = "title";
    private static final String BLOG_DESCRIPTION = "description";
    private static final String BLOG_LINK = "link";
    private static final String BLOG_IMAGE = "image";
    private static final String BLOG_IMAGE_URL = "url";
    private static final String BLOG_LAST_BUILD_DATE = "lastBuildDate";

    private static final String ITEM = "item";

    private static final String ARTICLE_TITLE = "title";
    private static final String ARTICLE_DESCRIPTION = "description";
    private static final String ARTICLE_LINK = "link";
    private static final String ARTICLE_PUBDATE = "pubDate";
    private static final String ARTICLE_MEDIA_THUMBNAIL = "media:thumbnail";
    private static final String ARTICLE_MEDIA_THUMBNAIL_URL = "url";


    public static Blog parse(InputStream in) {
        Blog blog = null;
        if (in != null) {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                blog = parseRss(parser);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return blog;
    }


    // Under RSS tag
    private static Blog parseRss(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals(BLOG_CHANNEL)) {
                return parseBlog(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    // Under Channel tag
    private static Blog parseBlog(XmlPullParser parser) throws XmlPullParserException, IOException {
        Blog blog = new Blog();
        blog.articleList = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case BLOG_TITLE:
                    blog.title = parseText(parser);
                    break;
                case BLOG_DESCRIPTION:
                    blog.description = parseText(parser);
                    break;
                case BLOG_LINK:
                    blog.link = parseText(parser);
                    break;
                case BLOG_IMAGE:
                    blog.imageUrl = parseBlogImage(parser);
                    break;
                case BLOG_LAST_BUILD_DATE:
                    blog.lastBuildDate = parseText(parser);
                    break;
                case ITEM:
                    blog.articleList.add(parseArticle(parser, blog.getId()));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return blog;
    }

    /**
     * image > url/ title/ link
     */
    // Under blog image tag
    private static String parseBlogImage(XmlPullParser parser) throws XmlPullParserException, IOException {
        String url = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals(BLOG_IMAGE_URL)) {
                url = parseText(parser);
            } else {
                skip(parser);
            }
        }
        return url;
    }


    /**
     * Parses the contents of an item. If it encounters a title, descrition, link, pubDate
     * or media:thumbnail tag, hands them off to their respective "read" methods for processing.
     * Otherwise, skips the tag.
     */
    private static Article parseArticle(XmlPullParser parser, long blogId) throws XmlPullParserException, IOException {
        Article article = new Article(blogId);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ARTICLE_TITLE:
                    article.title = parseText(parser);
                    break;
                case ARTICLE_DESCRIPTION:
                    article.description = parseText(parser);
                    break;
                case ARTICLE_LINK:
                    article.link = parseText(parser);
                    break;
                case ARTICLE_PUBDATE:
                    article.publishDate = parseText(parser);
                    break;
                case ARTICLE_MEDIA_THUMBNAIL:
                    article.thumbnailUrl = parseThumbNailUrl(parser);
                    break;
                default:
                    skip(parser);
            }
        }
        return article;
    }


    private static String parseThumbNailUrl(XmlPullParser parser) throws XmlPullParserException, IOException {
        String thumbNailUrl = null;
        if (parser.getEventType() == XmlPullParser.START_TAG) {
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                if (attrName.equals(ARTICLE_MEDIA_THUMBNAIL_URL)) {
                    thumbNailUrl = parser.getAttributeValue(i);
                }
            }
            parser.next();
        }
        return thumbNailUrl;
    }

    // The text of tags we need
    private static String parseText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;

    }


    /**
     * Skip the tags that dosen't need to be parsed.
     */
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}


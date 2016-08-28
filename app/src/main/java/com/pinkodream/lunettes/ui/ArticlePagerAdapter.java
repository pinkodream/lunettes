package com.pinkodream.lunettes.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.pinkodream.lunettes.R;
import com.pinkodream.lunettes.business.network.Network;
import com.pinkodream.lunettes.data.Article;
import com.pinkodream.lunettes.data.Blog;

public class ArticlePagerAdapter extends PagerAdapter {

    private Blog mBlog;
    private Context mContext;
    private ViewGroup mParentView;
    private ArrayList<View> mViews;

    public ArticlePagerAdapter(Context context, ViewGroup parent) {
        mContext = context;
        mParentView = parent;
        mViews = new ArrayList<>();
        updateView();
    }

    public void updateBlog(Blog blog) {
        mBlog = blog;
        updateView();
        notifyDataSetChanged();
    }

    private void updateView() {
        mViews.clear();
        if (mBlog != null && mBlog.articleList.size() > 0) {
            for (int i = 0; i < getCount(); i++) {
                mViews.add(getView(i));
            }
        }
    }

    private View getView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_item_article_list, mParentView, false);
        View blogLayout = view.findViewById(R.id.blog_layout);
        View articleLayout1 = view.findViewById(R.id.article_layout_1);
        View articleLayout2 = view.findViewById(R.id.article_layout_2);
        View articleLayout3 = view.findViewById(R.id.article_layout_3);
        View articleLayout4 = view.findViewById(R.id.article_layout_4);
        View articleLayout5 = view.findViewById(R.id.article_layout_5);

        if (position == 0) {
            setBlogViewInfo(blogLayout);
            blogLayout.setVisibility(View.VISIBLE);

            setArticleViewInfo(0, articleLayout1);
            setArticleViewInfo(1, articleLayout2);
            setArticleViewInfo(2, articleLayout3);

            articleLayout4.setVisibility(View.GONE);
            articleLayout5.setVisibility(View.GONE);
        } else {
            blogLayout.setVisibility(View.GONE);
            int articlePositionStart = 5 * (position - 1) + 3;
            setArticleViewInfo(articlePositionStart, articleLayout1);
            setArticleViewInfo(articlePositionStart + 1, articleLayout2);
            setArticleViewInfo(articlePositionStart + 2, articleLayout3);
            setArticleViewInfo(articlePositionStart + 3, articleLayout4);
            setArticleViewInfo(articlePositionStart + 4, articleLayout5);
        }

        return view;
    }

    private void setBlogViewInfo(View blogLayout) {
        TextView title = (TextView) blogLayout.findViewById(R.id.blog_title);
        TextView description = (TextView) blogLayout.findViewById(R.id.blog_description);
        NetworkImageView cover = (NetworkImageView) blogLayout.findViewById(R.id.blog_cover_image);

        title.setText(mBlog.title);
        description.setText(mBlog.description);
        cover.setImageUrl(mBlog.imageUrl, Network.get().getImageLoader());

        blogLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.KEY_ARTICLE_URL, mBlog.link);
                mContext.startActivity(intent);
            }
        });
    }

    private void setArticleViewInfo(final int position, View articleLayout) {
        if (mBlog.articleList.size() > position) {
            TextView title = (TextView) articleLayout.findViewById(R.id.article_title);
            TextView description = (TextView) articleLayout.findViewById(R.id.article_description);
            NetworkImageView thumbnail = (NetworkImageView) articleLayout.findViewById(R.id.article_thumbnail);

            Article article = mBlog.articleList.get(position);
            title.setText(article.title);
            description.setText(article.description);
            thumbnail.setImageUrl(article.thumbnailUrl, Network.get().getImageLoader());
            articleLayout.setVisibility(View.VISIBLE);

            articleLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(DetailActivity.KEY_ARTICLE_URL, mBlog.articleList.get(position).link);
                    mContext.startActivity(intent);
                }
            });

        } else {
            articleLayout.setVisibility(View.INVISIBLE);
            articleLayout.setOnClickListener(null);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position >= 0 && position < mViews.size()) {
            View view = mViews.get(position);
            container.addView(view);
            return view;
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position >= 0 && position < mViews.size()) {
            container.removeView(mViews.get(position));
        }
    }

    @Override
    public int getCount() {
        if (mBlog == null || mBlog.articleList == null || mBlog.articleList.size() <= 0) {
            return 0;
        } else {
            int articleCount = mBlog.articleList.size();
            if (articleCount <= 3) {
                return 1;
            } else {
                return (articleCount - 3) / 5 + 2;
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

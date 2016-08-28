package com.pinkodream.lunettes.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pinkodream.lunettes.R;
import com.pinkodream.lunettes.data.Blog;

public class BlogListAdapter extends BaseAdapter {

    private static final float SCALE = 0.8f;

    private Context mContext;
    private ArrayList<Blog> mBlogList;

    private ViewPager.PageTransformer mPageTransformer = new ViewPager.PageTransformer() {
        @TargetApi(11)
        @Override
        public void transformPage(View view, float v) {
            if (v <= -1 || v >= 1) {
                view.setScaleX(SCALE);
                view.setScaleX(SCALE);
            } else {
                // zoom in, zoom out
                view.setScaleX(SCALE + (1 - SCALE) * (1 - Math.abs(v)));
                view.setScaleY(SCALE + (1 - SCALE) * (1 - Math.abs(v)));
            }
        }
    };

    public BlogListAdapter(Context context) {
        mContext = context;
        mBlogList = new ArrayList<>();
    }

    public void updateBlogList(ArrayList<Blog> blogList) {
        mBlogList = blogList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBlogList.size();
    }

    @Override
    public Blog getItem(int position) {
        return mBlogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PagerHolder holder;
        if (convertView == null) {
            holder = new PagerHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_blog, parent, false);
            holder.viewPager = (ViewPager) convertView.findViewById(R.id.article_view_pager);
            holder.adapter = new ArticlePagerAdapter(mContext, parent);
            holder.viewPager.setAdapter(holder.adapter);
            holder.viewPager.setOffscreenPageLimit(3);
            if (VERSION.SDK_INT >= 11) {
                holder.viewPager.setPageMargin(-140);
                holder.viewPager.setPageTransformer(true, mPageTransformer);
            } else {
                holder.viewPager.setPageMargin(-100);
            }
            convertView.setTag(holder);
        } else {
            holder = (PagerHolder) convertView.getTag();
        }

        holder.adapter.updateBlog(getItem(position));
        holder.viewPager.setCurrentItem(0);

        return convertView;
    }

    private class PagerHolder {
        ViewPager viewPager;
        ArticlePagerAdapter adapter;
    }
}

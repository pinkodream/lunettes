package com.pinkodream.lunettes.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.pinkodream.lunettes.R;
import com.pinkodream.lunettes.business.RSSHelper;
import com.pinkodream.lunettes.data.Blog;
import com.pinkodream.lunettes.data.Event;
import com.pinkodream.lunettes.global.App;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";
    private View mEmptyView;
    private EditText mEditText;
    private ListView mBlogListView;
    private BlogListAdapter mBlogListAdapter;
    private AlertDialog mAddRSSDialog;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        mEmptyView = findViewById(R.id.empty_text);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mBlogListView = (ListView) findViewById(R.id.blog_list_view);
        mBlogListAdapter = new BlogListAdapter(this);
        mBlogListView.setAdapter(mBlogListAdapter);

        findViewById(R.id.top_bar_menu_button).setOnClickListener(this);
        findViewById(R.id.add_rss_button).setOnClickListener(this);
        findViewById(R.id.menu_container).setOnClickListener(this);
        RSSHelper.get().updateBlogList();
    }

    private void showAddRSSDialog() {
        if (mAddRSSDialog == null) {
            mEditText = new EditText(this);
            Builder builder = new Builder(MainActivity.this);
            builder.setTitle(R.string.add_rss_title);
            builder.setMessage(R.string.add_rss_content);
            builder.setView(mEditText, 20, 20, 20, 20);
            builder.setNegativeButton(R.string.button_cancel, null);
            builder.setPositiveButton(R.string.button_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = mEditText.getText().toString();
                    Log.d(TAG, "EditText.toString=" + url);
                    RSSHelper.get().addRSS(url);
                }
            });
            mAddRSSDialog = builder.show();
        } else {
            mEditText.setText("");
            mAddRSSDialog.show();
        }
    }

    private void showSuccessTips(int stringResId) {
        Snackbar bar = Snackbar.make(mDrawerLayout, getText(stringResId), Snackbar.LENGTH_SHORT);
        bar.getView().setBackgroundResource(R.color.lunettes_success);
        bar.show();
    }

    private void showErrorTips(int stringResId) {
        Snackbar bar = Snackbar.make(mDrawerLayout, getText(stringResId), Snackbar.LENGTH_SHORT);
        bar.getView().setBackgroundResource(R.color.lunettes_warning);
        bar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        App.exit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_bar_menu_button:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.add_rss_button:
                showAddRSSDialog();
                break;
            case R.id.menu_container:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.BackgroundThread)
    public void handleBackgroundEvent(Event event) {
        switch (event.type) {
            case Event.TYPE_UPDATE_BLOG_LIST_RESULT:
                if (event.code == Event.EMPTY) {
                    EventBus.getDefault().post(new Event(Event.TYPE_REFRESH_BLOG_LIST_VIEW, Event.EMPTY, null));
                } else if (event.code == Event.SUCCESS) {
                    EventBus.getDefault().post(new Event(Event.TYPE_REFRESH_BLOG_LIST_VIEW, Event.SUCCESS, RSSHelper.get().getAllBlogList()));
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void handleMainThreadEvent(Event event) {
        if (event.type == Event.TYPE_REFRESH_BLOG_LIST_VIEW) {
            if (event.code == Event.EMPTY) {
                mEmptyView.setVisibility(View.VISIBLE);
                mBlogListView.setVisibility(View.GONE);
            } else if (event.code == Event.SUCCESS && event.data instanceof ArrayList) {
                Log.d(TAG, "[handleMainThreadEvent.TYPE_REFRESH_BLOG_LIST_VIEW.SUCCESS]");
                ArrayList blogList = (ArrayList) event.data;
                if (blogList.size() > 0 && blogList.get(0) instanceof Blog) {
                    mBlogListAdapter.updateBlogList((ArrayList<Blog>) blogList);
                    mEmptyView.setVisibility(View.GONE);
                    mBlogListView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mBlogListView.setVisibility(View.GONE);
                }
            }
        } else if (event.type == Event.TYPE_ADD_RSS_RESULT) {
            if (event.code == Event.BAD_URL) {
                Log.d(TAG, "BAD URL");
                showErrorTips(R.string.add_rss_bad_url);
            } else if (event.code == Event.REPEAT) {
                Log.d(TAG, "REPEAT");
                showErrorTips(R.string.add_rss_repeat);
            } else if (event.code == Event.ERROR) {
                Log.d(TAG, "ERROR");
                showErrorTips(R.string.add_rss_error);
            } else if (event.code == Event.SUCCESS) {
                Log.d(TAG, "SUCCESS");
                showSuccessTips(R.string.add_rss_success);
                EventBus.getDefault().post(new Event(Event.TYPE_UPDATE_BLOG_LIST_RESULT, Event.SUCCESS, null));
            }
        }
    }
}

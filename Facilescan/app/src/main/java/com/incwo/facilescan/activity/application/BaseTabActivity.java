
package com.incwo.facilescan.activity.application;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.transition.Fade;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.incwo.facilescan.activity.desktop.DesktopFragment;
import com.incwo.facilescan.R;
import com.incwo.facilescan.activity.news.NewsFragment;
import com.incwo.facilescan.activity.scan.ScanFragment;
import com.incwo.facilescan.activity.videos.CategoriesFragment;
import com.incwo.facilescan.managers.SingleApp;

import java.util.HashMap;
import java.util.Stack;

public class BaseTabActivity extends AppCompatActivity {
    // TabIds
    public static final String TAB_DESKTOP = "desktop";
    public static final String TAB_SCAN = "scan";
    public static final String TAB_VIDEO = "video";
    public static final String TAB_NEWS = "news";

    private AsyncTask<?, ?, ?> LoadAll = null;

    private BottomNavigationView mBottomNavigationView;
    private BottomNavigationViewBadge mNewsBadge;

    // There's a navigation stack for each tab. Indexed by the TabIds.
    private HashMap<String, Stack<Fragment>> mNavigationStacks;
    private String mCurrentTab;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_tabs);


        if (LoadAll != null && LoadAll.getStatus() == AsyncTask.Status.RUNNING) {
            LoadAll.cancel(true);
        }
        LoadAll = new AsyncTaskLoadAll().execute();
        CookieSyncManager.createInstance(this);

        mNavigationStacks = new HashMap<String, Stack<Fragment>>();
        mNavigationStacks.put(TAB_DESKTOP, new Stack<Fragment>());
        mNavigationStacks.put(TAB_SCAN, new Stack<Fragment>());
        mNavigationStacks.put(TAB_VIDEO, new Stack<Fragment>());
        mNavigationStacks.put(TAB_NEWS, new Stack<Fragment>());

        mCurrentTab = TAB_DESKTOP;
        pushFragment(TAB_DESKTOP, new DesktopFragment(), true);

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_desktop:
                        selectTab(TAB_DESKTOP);
                        break;
                    case R.id.action_scan:
                        selectTab(TAB_SCAN);
                        break;
                    case R.id.action_videos:
                        selectTab(TAB_VIDEO);
                        break;
                    case R.id.action_news:
                        selectTab(TAB_NEWS);
                        break;
                }
                return true;
            }
        });


        mNewsBadge = new BottomNavigationViewBadge(this, mBottomNavigationView, R.id.action_news);
        mNewsBadge.setCount(0);
    }



    @Override
    protected void onResume() {
        super.onResume();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        SingleApp.loadSessionId();
        SingleApp.loadAccount();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (LoadAll != null && LoadAll.getStatus() == AsyncTask.Status.RUNNING) {
            LoadAll.cancel(true);
        }
        LoadAll = null;
    }

    public void selectTab(String tabId) {
        mCurrentTab = tabId;

        if (mNavigationStacks.get(tabId).size() == 0) { // This is the first time that this tab is selected.
            // Add the root fragment at the bottom of the stack.
            pushFragment(tabId, newRootFragmentForTab(tabId), true);
        } else { // Switching between tabs
            pushFragment(tabId, mNavigationStacks.get(tabId).lastElement(), false);
        }
    }

    private Fragment newRootFragmentForTab(String tabId) {
        if (tabId.equals(TAB_DESKTOP)) {
            return new DesktopFragment();
        } else if (tabId.equals(TAB_SCAN)) {
            return new ScanFragment();
        } else if (tabId.equals(TAB_VIDEO)) {
            return new CategoriesFragment();
        } else if (tabId.equals(TAB_NEWS)) {
            return new NewsFragment();
        }

        return null; // Unexpected
    }

    public void pushFragment(String tabId, Fragment fragment) {
        pushFragment(tabId, fragment,true);
    }

    /* 
     *      To add fragment to a tab. 
     *  tabId           ->  Tab identifier
     *  fragment        ->  Fragment to show in the tab.
     *  shouldAdd       ->  Whether the fragment should be added to the navigation stack.
     *                      false when switching between tabs, or adding the first fragment to a tab.
     *                      true when pushing more fragments onto the navigation stack.
     */
    private void pushFragment(String tabId, Fragment fragment, boolean shouldAdd) {
        if(shouldAdd) {
            mNavigationStacks.get(tabId).push(fragment);
        }

        if(tabId.equals(mCurrentTab)) {
            fragment.setEnterTransition(new Fade());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();
            getSupportActionBar().setDisplayHomeAsUpEnabled(mNavigationStacks.get(mCurrentTab).size() > 1);
        }
    }

    public void popFragment() {
        popFragment(mCurrentTab);
    }

    public void popFragment(String tabId) {
        Fragment topFragment = mNavigationStacks.get(tabId).elementAt(mNavigationStacks.get(tabId).size() - 1);
        Fragment fragmentBelow = mNavigationStacks.get(tabId).elementAt(mNavigationStacks.get(tabId).size() - 2);

        // Pop the top fragment from the stack
        mNavigationStacks.get(tabId).pop();

        // Show the fragmentBelow
        topFragment.setExitTransition(new Fade());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragmentBelow)
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(mNavigationStacks.get(tabId).size() > 1);
    }

    public void popToRoot(String tabId) {
        Stack<Fragment> navigationStack = mNavigationStacks.get(tabId);
        while(navigationStack.size() > 1) {
            popFragment(tabId);
        }
    }

    @Override
    public void onBackPressed() {
        if(mNavigationStacks.get(mCurrentTab).size() == 1) { // At the root of the stack ?
            finish();
        } else {
            popFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Back arrow in the action bar
            onBackPressed();
            return true;
        }
        return false;
    }

    public void logOut() {
        SingleApp.logOut();
        popToRoot(TAB_SCAN);

        if (!mNavigationStacks.get(TAB_SCAN).isEmpty()) {
            ScanFragment scanFragment = (ScanFragment) mNavigationStacks.get(TAB_SCAN).elementAt(0);
            scanFragment.checkLogin();
        }
    }

    public void logIn() {
        if (!mNavigationStacks.get(TAB_DESKTOP).isEmpty()) {
            DesktopFragment desktopFragment = (DesktopFragment) mNavigationStacks.get(TAB_DESKTOP).elementAt(0);
            desktopFragment.checkLogin();
        }

        if (!mNavigationStacks.get(TAB_SCAN).isEmpty()) {
            ScanFragment scanFragment = (ScanFragment) mNavigationStacks.get(TAB_SCAN).elementAt(0);
            scanFragment.checkLogin();
        }
    }


    /*
     *   Imagine if you wanted to get an image selected using ImagePicker intent to the fragment. Ofcourse I could have created a public function
     *  in that fragment, and called it from the activity. But couldn't resist myself.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mNavigationStacks.get(mCurrentTab).size() == 0) {
            return;
        }

        /*Now current fragment on screen gets onActivityResult callback..*/
        mNavigationStacks.get(mCurrentTab).lastElement().onActivityResult(requestCode, resultCode, data);
    }


    public void setNewsBadgeCount(int newsCount) {
        mNewsBadge.setCount(newsCount);
    }


    private class AsyncTaskLoadAll extends AsyncTask<String, Integer, Long> {

        protected void onPreExecute() {

        }

        protected Long doInBackground(String... tasks) {
            long result = 0;
            SingleApp.loadAll();
            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            setNewsBadgeCount(SingleApp.getCountOfUnreadNews());
        }
    }





}
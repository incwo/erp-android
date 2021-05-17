
package com.incwo.facilescan.activity.desktop;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.incwo.facilescan.R;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.helpers.Account;
import com.incwo.facilescan.helpers.fragments.TabFragment;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.WebService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class DesktopFragment extends TabFragment {

    static private int CONNECT_OR_CREATE = 0;
    static private int CREATE_ACCOUNT = 1;
    static private int FORM = 2;
    static private int LOGGED_IN = 3;


    private AsyncTask<?, ?, ?> logToDesktop = null;

    private View mRoot;
    private WebView mWv;
    private ViewFlipper mViewFlipper;
    private ImageView mReloadButton;
    private ImageView mStopImageView;
    private ImageView mBackImageView;
    private ImageView mForwardImageView;
    private ImageView mLogOutImageView;
    private ProgressBar mLoadProgressBar;
    private String mCurrentUrl = null;
    private WebService mWebService;
    private boolean mIsFirstLoad = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.desktop_fragment, null);
        mViewFlipper = (ViewFlipper) mRoot;
        mViewFlipper.setDisplayedChild(CONNECT_OR_CREATE);
        mWebService = new WebService();

        // CONNECT_OR_CREATE
        Button loginButton = (Button) mRoot.findViewById(R.id.signin_loginButton);
        loginButton.setOnClickListener(mLoginButtonListener);

        // LOGGED_IN
        mWv = (WebView) mRoot.findViewById(R.id.WEBVIEW);
        mReloadButton = (ImageView) mRoot.findViewById(R.id.nav_reload);
        mStopImageView = (ImageView) mRoot.findViewById(R.id.nav_stop);
        mBackImageView = (ImageView) mRoot.findViewById(R.id.nav_back);
        mForwardImageView = (ImageView) mRoot.findViewById(R.id.nav_forward);
        mLogOutImageView = (ImageView) mRoot.findViewById(R.id.log_out);
        mLoadProgressBar = (ProgressBar) mRoot.findViewById(R.id.LOADER);

        // this prevent previous fragment to catch onTouch event
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mReloadButton.setOnClickListener(reloadListener);
        mReloadButton.setVisibility(View.GONE);
        mStopImageView.setOnClickListener(stopListener);
        mBackImageView.setOnClickListener(backListener);
        mForwardImageView.setOnClickListener(forwardListener);
        mLogOutImageView.setOnClickListener(logoutListener);

        mBackImageView.setAlpha(0.3f);
        mForwardImageView.setAlpha(0.3f);

        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AccountCreation(view, mViewFlipper);
    }

    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        if (SingleApp.isLoggedIn() == false && mViewFlipper.getDisplayedChild() != CONNECT_OR_CREATE){
            mViewFlipper.setDisplayedChild(CONNECT_OR_CREATE);
            return true;
        }
        return false;
    }

    static class AccountCreation {

        ViewFlipper mViewFlipper;
        View root;

        AccountCreation(View root, ViewFlipper viewFlipper){
            this.mViewFlipper = viewFlipper;
            this.root = root;
            setListeners();
        }

         private void setListeners() {
            View connect_or_create_root = root.findViewById(R.id.connect_or_create_root_layout);
            connect_or_create_root.findViewById(R.id.account_connect_button).setOnClickListener(mAccountConnect);
            connect_or_create_root.findViewById(R.id.account_creation_button).setOnClickListener(mAccountCreation);
        }

        private View.OnClickListener mAccountConnect = new View.OnClickListener() {
            public void onClick(View view) {
                mViewFlipper.setDisplayedChild(FORM);
            }
        };

        private View.OnClickListener mAccountCreation = new View.OnClickListener() {
            public void onClick(View view) {
                mViewFlipper.setDisplayedChild(CREATE_ACCOUNT);
                loadHtml();
            }
        };

        private void loadHtml() {
            // Init WebView
            WebView webView = (WebView)root.findViewById(R.id.accountCreationWebview);
            webView.scrollTo(0, 0);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setNeedInitialFocus(true);
            webView.setBackgroundColor(0xffffffff);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    View account_creation_root = root.findViewById(R.id.account_creation_root);
                    ViewFlipper vf = (ViewFlipper) account_creation_root.findViewById(R.id.account_creation_viewflipper);
                    vf.setDisplayedChild(0);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    View account_creation_root = root.findViewById(R.id.account_creation_root);
                    ViewFlipper vf = (ViewFlipper) account_creation_root.findViewById(R.id.account_creation_viewflipper);
                    vf.setDisplayedChild(1);
                }


                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (url.equals("theURLYouDontWantToLoadInBrowser")) {
                        return true;
                    } else {
                        if (url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("skype:")) {
                            SingleApp.getInstance().startIntent(view.getContext(), url);
                            return true;
                        } else if (url.contains("http://facilepos.app/signin?email=")) {
                            EditText usernameField = (EditText) root.findViewById(R.id.edit_mail);
                            String[] split = url.split("=");
                            try {
                                usernameField.setText(URLDecoder.decode(split[1], "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            mViewFlipper.setDisplayedChild(FORM);

                            return true;
                        }
                        return false;
                    }
                }

            });
            webView.loadUrl(SingleApp.getBaseURL() + SingleApp.ACCOUNT_CREATION_URL);
        }
    }

    private View.OnClickListener reloadListener = new View.OnClickListener() {
        public void onClick(View view) {
            mLoadProgressBar.setVisibility(View.VISIBLE);
            mStopImageView.setVisibility(View.VISIBLE);
            mReloadButton.setVisibility(View.GONE);
            if (mCurrentUrl == null)
                loadHtml(formatHtml());
            else
                mWv.loadUrl("javascript:window.location.reload( true )");
        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View view) {
            mLoadProgressBar.setVisibility(View.GONE);
            mStopImageView.setVisibility(View.GONE);
            mReloadButton.setVisibility(View.VISIBLE);
            mWv.stopLoading();
        }
    };

    private View.OnClickListener backListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (mWv.canGoBack()) {
                mLoadProgressBar.setVisibility(View.VISIBLE);
                mStopImageView.setVisibility(View.VISIBLE);
                mReloadButton.setVisibility(View.GONE);

                WebBackForwardList webBackForwardList = mWv.copyBackForwardList();
                if (webBackForwardList.getCurrentIndex() == 1 || mCurrentUrl == null) // if no previous page
                {
                    mWv.goBack();
                    loadHtml(formatHtml());
                } else
                    mWv.goBack();
            }

        }
    };

    private View.OnClickListener forwardListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (mWv.canGoForward()) {
                mLoadProgressBar.setVisibility(View.VISIBLE);
                mStopImageView.setVisibility(View.VISIBLE);
                mReloadButton.setVisibility(View.GONE);
                mWv.goForward();
            }
        }
    };

    private String formatHtml() {
        return mWebService.body;
    }

    private void loadHtml(String html) {
        // Init WebView

        mWv.scrollTo(0, 0);
        mWv.getSettings().setAllowFileAccess(true);
        mWv.getSettings().setJavaScriptEnabled(true);
        mWv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWv.getSettings().setLightTouchEnabled(true);
        mWv.getSettings().setNeedInitialFocus(true);
        mWv.setBackgroundColor(0xffffffff);
        mWv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWv.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url) {
                mStopImageView.setVisibility(View.VISIBLE);
                mReloadButton.setVisibility(View.GONE);
                mLoadProgressBar.setVisibility(View.VISIBLE);
            }


            public void onPageFinished(WebView view, String url) {
                mStopImageView.setVisibility(View.GONE);
                mReloadButton.setVisibility(View.VISIBLE);
                mLoadProgressBar.setVisibility(View.GONE);
                if (!mWv.canGoBack()) {
                    mBackImageView.setAlpha(0.3f);
                    mCurrentUrl = null;
                } else {
                    mBackImageView.setAlpha(1.0f);
                }
                if (!mWv.canGoForward()) {
                    mForwardImageView.setAlpha(0.3f);
                } else {
                    mForwardImageView.setAlpha(1.0f);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("theURLYouDontWantToLoadInBrowser")) {
                    return true;
                } else {
                    mStopImageView.setVisibility(View.VISIBLE);
                    mLoadProgressBar.setVisibility(View.VISIBLE);
                    mReloadButton.setVisibility(View.GONE);
                    mCurrentUrl = url;
                    //				        	SingleApp.setSelectedDesktopURL(url);

                    if (url.equals(SingleApp.getBaseURL() + SingleApp.LOGOUT_URL)) {
                        if (logToDesktop != null && logToDesktop.getStatus() == AsyncTask.Status.RUNNING) {
                            logToDesktop.cancel(true);
                        }
                        mIsFirstLoad = true;
                        view.loadUrl(url);
                        mViewFlipper.setDisplayedChild(FORM);
                        EditText editText = (EditText) mRoot.findViewById(R.id.edit_mail);
                        editText.setText("");
                        editText = (EditText) mRoot.findViewById(R.id.edit_password);
                        editText.setText("");
                        view.loadUrl("about:blank");
                        getTabActivity().logOut();
                        return false;

                    } else if (url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("skype:")) {
                        SingleApp.getInstance().startIntent(getActivity(), url);
                    } else {
                        view.loadUrl(url);
                    }
                    return true;
                }
            }

        });

        mStopImageView.setVisibility(View.VISIBLE);
        mReloadButton.setVisibility(View.GONE);
        mWv.loadDataWithBaseURL(SingleApp.getBaseURL(), html, "text/html", "utf-8", null);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (logToDesktop != null && logToDesktop.getStatus() == AsyncTask.Status.RUNNING) {
            logToDesktop.cancel(true);
        }
        if (SingleApp.isLoggedIn()) {
            mViewFlipper.setDisplayedChild(LOGGED_IN);
            logToDesktop = new AsyncTaskLogToDesktop(SingleApp.getAccount()).execute();
        }
    }

    public void checkLogin() {
        if (logToDesktop != null && logToDesktop.getStatus() == AsyncTask.Status.RUNNING) {
            logToDesktop.cancel(true);
        }
        if (SingleApp.isLoggedIn()) {
            mViewFlipper.setDisplayedChild(LOGGED_IN);
            if (mIsFirstLoad)
                logToDesktop = new AsyncTaskLogToDesktop(SingleApp.getAccount()).execute();

        } else {
            mViewFlipper.setDisplayedChild(FORM);
            mIsFirstLoad = true;

        }
    }

    @Override
    public void onDestroy() {
        if (logToDesktop != null) {
            logToDesktop.cancel(true);
        }
        super.onDestroy();
    }


    private View.OnClickListener logoutListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (logToDesktop != null && logToDesktop.getStatus() == AsyncTask.Status.RUNNING) {
                logToDesktop.cancel(true);
            }
            mIsFirstLoad = true;
            mWv.loadUrl(SingleApp.getBaseURL() + SingleApp.LOGOUT_URL);
            mViewFlipper.setDisplayedChild(FORM);
            EditText editText = (EditText) mRoot.findViewById(R.id.edit_mail);
            editText.setText("");
            editText = (EditText) mRoot.findViewById(R.id.edit_password);
            editText.setText("");
            mWv.loadUrl("about:blank");
            getTabActivity().logOut();

        }
    };

    private View.OnClickListener mLoginButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            mRoot.findViewById(R.id.signin_loginButton).setVisibility(View.GONE);
            mRoot.findViewById(R.id.signin_bottomProgressBar).setVisibility(View.VISIBLE);

            EditText editText = (EditText) mRoot.findViewById(R.id.edit_mail);
            String username = editText.getText().toString();
            editText = (EditText) mRoot.findViewById(R.id.edit_password);
            String password = editText.getText().toString();

            logToDesktop = new AsyncTaskLogToDesktop(new Account(username, password)).execute();
        }
    };


    private class AsyncTaskLogToDesktop extends AsyncTask<String, Integer, Long> {
        Account mAccount;

        public AsyncTaskLogToDesktop(Account account) {
            mAccount = account;
        }

        protected void onPreExecute() {
        }

        protected Long doInBackground(String... tasks) {
            long result = 0;
            mWebService = new WebService();
            mWebService.logToDesktop(mAccount);
            result = mWebService.responseCode;

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {

            if (result >= 200 && result < 300) {
                mViewFlipper.setDisplayedChild(LOGGED_IN);
                SingleApp.setLoggedIn(true);
                mIsFirstLoad = false;
                BaseTabActivity activity = getTabActivity();
                if(activity != null) {
                    activity.logIn();
                    loadHtml(formatHtml());
                }
            } else {
                WebService.showError(result);
                SingleApp.setLoggedIn(false);

            }

            mRoot.findViewById(R.id.signin_loginButton).setVisibility(View.VISIBLE);
            mRoot.findViewById(R.id.signin_bottomProgressBar).setVisibility(View.GONE);
            logToDesktop = null;
        }
    }

    private class AsyncTaskGetHtml extends AsyncTask<String, Integer, Long> {

        String url;

        public AsyncTaskGetHtml(String url) {
            this.url = url;
        }

        protected void onPreExecute() {
        }

        protected Long doInBackground(String... tasks) {
            long result = 0;
            WebService ws = new WebService();
            ws.get(url);
            result = ws.responseCode;

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {

            if (result >= 200 && result < 300) {
                loadHtml(formatHtml());
            } else {
                WebService.showError(result);
                SingleApp.setLoggedIn(false);

            }

            mRoot.findViewById(R.id.submit).setVisibility(View.VISIBLE);
            mRoot.findViewById(R.id.LOADING_BOTTOM).setVisibility(View.GONE);
            logToDesktop = null;
        }
    }

}
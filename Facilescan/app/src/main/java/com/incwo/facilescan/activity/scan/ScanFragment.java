
package com.incwo.facilescan.activity.scan;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.fragments.BaseListFragment;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.WebService;
import com.incwo.facilescan.scan.BusinessFile;
import com.incwo.facilescan.scan.ScanXml;

import java.util.ArrayList;

public class ScanFragment extends BaseListFragment {

    static private int formIndexView = 0;
    static private int ConnectedIndexView = 1;

    private AsyncTask<?, ?, ?> mAsyncScanLoggingIn = null;

    private ScanXml xml;
    private WebService ws;

    private View mRoot;
    private ViewFlipper viewFlipper;
    private boolean firstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.scan_fragment, null);
        viewFlipper = (ViewFlipper) mRoot.findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(formIndexView);

        // connectionIndexView
        Button loginButton = (Button) mRoot.findViewById(R.id.signin_loginButton);
        loginButton.setOnClickListener(mLogInButtonListener);

        // this prevent previous fragment to catch onTouch event
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        checkLogin();

        return mRoot;
    }

    public void checkLogin() {
        if (SingleApp.isLoggedIn()) {
            viewFlipper.setDisplayedChild(ConnectedIndexView);
            if (firstLoad)
                mAsyncScanLoggingIn = new AsyncScanLoggingIn(SingleApp.getUsername(), SingleApp.getPassword()).execute();
        } else {
            viewFlipper.setDisplayedChild(formIndexView);
            EditText editText = (EditText) mRoot.findViewById(R.id.edit_mail);
            editText.setText("");
            editText = (EditText) mRoot.findViewById(R.id.edit_password);
            editText.setText("");
            firstLoad = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (SingleApp.isLoggedIn()) {
            xml = SingleApp.getScanXml();
            if (xml.businessFiles != null) {
                if (xml.businessFiles.size() > 0)
                    mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);
                BusinessItemAdapter scanAdapter = new BusinessItemAdapter(this.getActivity(), xml.businessFiles);
                setListAdapter(scanAdapter);
            }
            viewFlipper.setDisplayedChild(ConnectedIndexView);
            if (firstLoad)
                mAsyncScanLoggingIn = new AsyncScanLoggingIn(SingleApp.getUsername(), SingleApp.getPassword()).execute();
        } else {
            viewFlipper.setDisplayedChild(formIndexView);
            firstLoad = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAsyncScanLoggingIn != null) {
            mAsyncScanLoggingIn.cancel(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SingleApp.setSelectedBusinessScanItem(xml.businessFiles.get(position));
        getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, new ObjectScanFragment());
    }

    private class BusinessItemAdapter extends ArrayAdapter<BusinessFile> {
        private LayoutInflater mInflater;

        BusinessItemAdapter(Activity context, ArrayList<BusinessFile> businessFiles) {
            super(context, R.layout.row_business_file, businessFiles);
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            BusinessFile businessFile = getItem(position);
            String title =  businessFile.getName();

            Row rowView = new Row();
            convertView = mInflater.inflate(R.layout.row_business_file, null);

            rowView.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            convertView.setTag(title);

            rowView.titleTextView.setText(title);
            return (convertView);
        }

        // Object container for view fields
        class Row {
            TextView titleTextView;
        }
    }


    private View.OnClickListener mLogInButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            mRoot.findViewById(R.id.signin_loginButton).setVisibility(View.GONE);
            mRoot.findViewById(R.id.signin_bottomProgressBar).setVisibility(View.VISIBLE);

            EditText editText = (EditText) mRoot.findViewById(R.id.edit_mail);
            String username = editText.getText().toString();
            editText = (EditText) mRoot.findViewById(R.id.edit_password);
            String password = editText.getText().toString();

            mAsyncScanLoggingIn = new AsyncScanLoggingIn(username, password).execute();
        }
    };

    private class AsyncScanLoggingIn extends AsyncTask<String, Integer, Long> {
        String login;
        String password;

        public AsyncScanLoggingIn(String username, String pass) {
            login = username;
            password = pass;
        }

        protected void onPreExecute() {
        }

        protected Long doInBackground(String... tasks) {
            long result = 0;
            ws = new WebService();
            ws.logToScan(login, password);
            result = ws.responseCode;
            if (ws.responseCode >= 200 && ws.responseCode < 300) {
                SingleApp.getScanXml().processXmLContent(ws.body);
            }
            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            BaseTabActivity activity = getTabActivity();
            if(activity == null) { // Method called while the fragment is not on screen anymore
                return;
            }

            if (result >= 200 && result < 300) {
                xml = SingleApp.getScanXml();
                viewFlipper.setDisplayedChild(ConnectedIndexView);
                BusinessItemAdapter businessAdapter = new BusinessItemAdapter(getActivity(), xml.businessFiles);
                setListAdapter(businessAdapter);
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                SingleApp.setLoggedIn(true);
                firstLoad = false;
                activity.logIn();
                mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);

            } else {
                WebService.showError(result);

                SingleApp.setLoggedIn(false);
            }
            mRoot.findViewById(R.id.signin_loginButton).setVisibility(View.VISIBLE);
            mRoot.findViewById(R.id.signin_bottomProgressBar).setVisibility(View.GONE);
            mAsyncScanLoggingIn = null;
        }
    }
}
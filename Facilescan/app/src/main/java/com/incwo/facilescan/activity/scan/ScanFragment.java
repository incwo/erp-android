
package com.incwo.facilescan.activity.scan;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.incwo.facilescan.scan.BusinessFilesFetch;

import java.util.ArrayList;

public class ScanFragment extends BaseListFragment {

    static final private int SIGN_IN_FLIPPER_INDEX = 0;
    static final private int LOADING_FLIPPER_INDEX = 1;
    static final private int BUSINESS_FILES_FLIPPER_INDEX = 2;

    private BusinessFilesFetch mBusinessFilesFetch = null;

    private View mRoot;
    private ViewFlipper viewFlipper;
    private boolean firstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.scan_fragment, null);
        viewFlipper = (ViewFlipper) mRoot.findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(SIGN_IN_FLIPPER_INDEX);

        // connectionIndexView
        Button loginButton = (Button) mRoot.findViewById(R.id.signin_loginButton);
        loginButton.setOnClickListener(mLogInButtonListener);

        // this prevent previous fragment to catch onTouch event
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        updateListAdapter(SingleApp.getBusinessFilesList().businessFiles);
        checkLogin();

        return mRoot;
    }

    public void checkLogin() {
        if (SingleApp.isLoggedIn()) {
            viewFlipper.setDisplayedChild(BUSINESS_FILES_FLIPPER_INDEX);
            if (firstLoad) {
                fetch();
            }
        } else {
            viewFlipper.setDisplayedChild(SIGN_IN_FLIPPER_INDEX);
            EditText editText = (EditText) mRoot.findViewById(R.id.edit_mail);
            editText.setText("");
            editText = (EditText) mRoot.findViewById(R.id.edit_password);
            editText.setText("");
            firstLoad = true;
        }

    }

    private void fetch() {
        mBusinessFilesFetch = new BusinessFilesFetch(SingleApp.getUsername(), SingleApp.getPassword());
        viewFlipper.setDisplayedChild(LOADING_FLIPPER_INDEX);
        mBusinessFilesFetch.fetch(new BusinessFilesFetch.Listener() {
            @Override
            public void onSuccess(ArrayList<BusinessFile> businessFiles) {
                BaseTabActivity activity = getTabActivity();
                if (activity == null) { // Method called while the fragment is not on screen anymore
                    return;
                }

                SingleApp.getBusinessFilesList().businessFiles = businessFiles;
                viewFlipper.setDisplayedChild(BUSINESS_FILES_FLIPPER_INDEX);
                updateListAdapter(businessFiles);
                SingleApp.setLoggedIn(true);
                firstLoad = false;
                activity.logIn();
                //mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);
            }

            @Override
            public void onFailed(int responseCode) {
                WebService.showError(responseCode);
                firstLoad = true;

                // On failures, the user is logged out !!!
                SingleApp.setLoggedIn(false);
                viewFlipper.setDisplayedChild(SIGN_IN_FLIPPER_INDEX);
            }

            @Override
            public void always() {
                mRoot.findViewById(R.id.signin_loginButton).setVisibility(View.VISIBLE);
                mRoot.findViewById(R.id.signin_bottomProgressBar).setVisibility(View.GONE);
                mBusinessFilesFetch =null;
            }
        });
    }

    private void updateListAdapter(ArrayList<BusinessFile> businessFiles) {
        BusinessItemAdapter businessAdapter = new BusinessItemAdapter(getActivity(), businessFiles);
        setListAdapter(businessAdapter);
        businessAdapter.notifyDataSetChanged();
        //((BaseAdapter) getListAdapter()).notifyDataSetChanged();
        // Warning: the "Loading view" is below the list. It shows if the list is empty.
    }

    @Override
    public void onResume() {
        super.onResume();

        //checkLogin();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mBusinessFilesFetch != null) {
            mBusinessFilesFetch.cancel();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SingleApp.setSelectedBusinessScanItem(SingleApp.getBusinessFilesList().businessFiles.get(position));
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

            checkLogin();
        }
    };
}
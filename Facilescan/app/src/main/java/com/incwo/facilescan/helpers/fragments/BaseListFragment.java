
package com.incwo.facilescan.helpers.fragments;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.incwo.facilescan.activity.application.BaseTabActivity;

public class BaseListFragment extends ListFragment {
    @Nullable
    public BaseTabActivity getTabActivity() {
        return (BaseTabActivity)getActivity();
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
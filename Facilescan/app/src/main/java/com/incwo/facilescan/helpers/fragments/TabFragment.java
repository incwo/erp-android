
package com.incwo.facilescan.helpers.fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.incwo.facilescan.activity.application.BaseTabActivity;

public class TabFragment extends Fragment {
    @Nullable
  public BaseTabActivity getTabActivity() {
    return (BaseTabActivity) getActivity();
  }

  public boolean onBackPressed(){
    return false;
  }

//  public void onActivityResult(int requestCode, int resultCode, Intent data){
//  }

}
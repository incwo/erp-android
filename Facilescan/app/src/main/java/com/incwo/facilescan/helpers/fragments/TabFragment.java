
package com.incwo.facilescan.helpers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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
package com.incwo.facilescan.activity.application;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.incwo.facilescan.R;

public class BottomNavigationViewBadge {
    private FrameLayout mLayout;
    private TextView mTextView;

    public BottomNavigationViewBadge(Context context, BottomNavigationView parent, int itemResId)  {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) parent.getChildAt(0);

        int itemIndex = indexOfMenuItem(itemResId, parent.getMenu());
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(itemIndex);

        mLayout = (FrameLayout) LayoutInflater.from(context)
                .inflate(R.layout.badge, menuView, false);
        itemView.addView(mLayout);

        mTextView = mLayout.findViewById(R.id.badge);
    }

    private int indexOfMenuItem(int itemResId, Menu menu) {
        int index;
        for(index = 0; index < menu.size(); index++) {
            if(menu.getItem(index).getItemId() == itemResId) {
                return  index;
            }
        }

        return -1; // Not found
    }

    public void setCount(int count) {
        mLayout.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        mTextView.setText(String.valueOf(count));
    }
}

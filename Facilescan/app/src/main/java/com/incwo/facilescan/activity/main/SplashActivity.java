package com.incwo.facilescan.activity.main;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.support.constraint.ConstraintLayout;

import com.incwo.facilescan.R;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.managers.SingleApp;


public class SplashActivity extends AppCompatActivity {
    final Handler handler = new Handler();
    Runnable wait;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.splash);
        setListeners();
        handler.postDelayed(wait = new Runnable() {
            @Override
            public void run() {
                endSplash();
            }
        }, SingleApp.SPLASH_SCREEN_DURATION);
    }

    private void setListeners() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.splash);
        if (layout != null) {
            layout.setClickable(true);
            layout.setOnClickListener(mSplashListener);
        }
    }

    private View.OnClickListener mSplashListener = new View.OnClickListener() {
        public void onClick(View view) {
            handler.removeCallbacks(wait);
            endSplash();
        }
    };

    private void endSplash() {
        Intent intent = new Intent(SplashActivity.this, BaseTabActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.portsip.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.portsip.R;
/**
 * Created by Niggui on 31.10.2015.
 */
public class SplashActivity extends FragmentActivity {
    private Typeface font;
    private TextView text1;
    private Handler handler;
    private Runnable callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.bgimage);
        setContentView(R.layout.activity_splash);
        text1 = (TextView) findViewById(android.R.id.text1);
        font = Typeface.createFromAsset(getAssets(), "fonts/Font.ttf");
        text1.setTypeface(font);
        handler = new Handler();
        callback = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        handler.postDelayed(callback, 10000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(callback);
    }
}



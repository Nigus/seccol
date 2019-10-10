package com.portsip.util;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.portsip.R;

/**
 * Created by Nigussie on 13.06.2015.
 */
public class IncomingCaller extends Activity {
    private Context mContext;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highlight_sms_call);

        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);

        mContext = getApplicationContext();


    }

}

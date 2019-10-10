package com.portsip.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.portsip.R;
import com.portsip.SettingFragment;

/**
 * Created by Nigussie on 29.05.2015.
 */
public class SettingMenu extends FragmentActivity{

    SettingFragment settingFragment = null;
    android.support.v4.app.Fragment frontFragment;
    public ViewGroup layout=null;
    RelativeLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        mainLayout=(RelativeLayout)findViewById(R.id.relativelay);
        mainLayout.setBackgroundResource(R.drawable.bgimage);
        loadSettingFragment();
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void loadSettingFragment() {
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }
        frontFragment = settingFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, settingFragment).commit();
    }

}

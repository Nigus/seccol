package com.portsip.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.portsip.LoginFragment;
import com.portsip.R;
import com.portsip.SettingFragment;

/**
 * Created by Nigussie on 29.05.2015.
 */
public class LoginMenu extends FragmentActivity{
    LoginFragment loginFragment = null;
    android.support.v4.app.Fragment frontFragment;
    public ViewGroup layout=null;
    RelativeLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);
        mainLayout=(RelativeLayout)findViewById(R.id.relativelay);
        mainLayout.setBackgroundResource(R.drawable.bgimage);
        loadLoginFragment();
        /*Intent intent= new Intent(this,TestKeyboard.class);
        startActivity(intent);*/
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void loadLoginFragment() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        frontFragment = loginFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, loginFragment).commit();
    }
}

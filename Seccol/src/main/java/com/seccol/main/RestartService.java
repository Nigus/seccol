package com.portsip.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.portsip.R;
import com.portsip.LoginFragment;

/**
 * Created by Nigussie on 02.06.2015.
 */
public class RestartService extends FragmentActivity {

    LoginFragment restartService = null;
    android.support.v4.app.Fragment frontFragment;
    public ViewGroup layout=null;
    RelativeLayout mainLayout;
    public boolean _flagg=false;

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
        // Set the cancle flag true to let the sdk manager to unsubscribe the Service
        _flagg=true;
        loadRestartFragment();
        /*Intent intent= new Intent(this,TestKeyboard.class);
        startActivity(intent);*/
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void loadRestartFragment() {
        Bundle args = new Bundle();
        args.putBoolean("DUKEM", _flagg);
        //args.putString("FLAGG", String.valueOf(flagg));
        if (restartService == null) {
            restartService = new LoginFragment();
        }
        frontFragment = restartService;
        restartService.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, restartService).commit();
   }
}
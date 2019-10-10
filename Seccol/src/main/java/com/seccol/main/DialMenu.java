package com.portsip.main;

/**
 * Created by Nigussie on 21.10.2015.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.portsip.NumpadFragment;
import com.portsip.R;

/**
 * Created by Nigussie on 29.05.2015.
 */
public class DialMenu extends FragmentActivity {

    NumpadFragment numpadFragment = null;
    android.support.v4.app.Fragment frontFragment;
    public ViewGroup layout=null;
    RelativeLayout mainLayout;
    String phone_number;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        mainLayout=(RelativeLayout)findViewById(R.id.relativelay);
        mainLayout.setBackgroundResource(R.drawable.bgimage);
        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            //Intent intent = getIntent();
            phone_number = extras.getString("nummer");
        }
        Log.d("DialMenu", "The number you are trying to call is: " + phone_number);
        loadNumpadFragment(phone_number);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void loadNumpadFragment(String dest_num) {
        Bundle bundle = new Bundle();
        bundle.putString("value", dest_num);

        if (numpadFragment == null) {
            numpadFragment = new NumpadFragment();
        }
        frontFragment = numpadFragment;
        numpadFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, numpadFragment).commit();
    }

}

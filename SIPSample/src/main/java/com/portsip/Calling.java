package com.portsip;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.portsip.bulk.chooseservice;
import com.portsip.util.Line;

public class Calling extends FragmentActivity {
    private static final String TAG = "Calling";
    PortSipSdk sdk;
    final int MENU_QUIT = 0;
    Context mContext;
    LoginFragment loginFragment = null;
    NumpadFragment numpadFragment = null;
    VideoCallFragment videoCallFragment = null;
    MessageFragment messageFragment = null;
    SettingFragment settingFragment = null;
    Fragment frontFragment;
    View contentView = null;
    String phone_number;
    String name;
    String phonenr;
    String my_email;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        Button mybtn= (Button) findViewById(R.id.tab_numpad);
        mybtn.setVisibility(View.INVISIBLE);
        mContext = this;
        contentView = findViewById(R.id.content);
        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            //Intent intent = getIntent();
            phone_number = extras.getString("nummer");
        }
        Log.d(TAG,"The number you are trying to call is: "+phone_number);
        loadNumPadFragment(phone_number);
        RadioGroup menuGroup = (RadioGroup) findViewById(R.id.tab_menu);
        menuGroup.check(R.id.tab_numpad);
        menuGroup.setOnCheckedChangeListener(new MyOnCheckChangeListen());
    }
    // The momentum point array struct
    class MyOnCheckChangeListen implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.tab_numpad:
                    loadNumPadFragment(phone_number);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    protected void onResume() {
        //((MyApplication) getApplicationContext()).setMainActivity2(this);
        super.onResume();
    }
    @Override
    protected void onPause() {
        //((MyApplication) getApplicationContext()).setMainActivity2(this);
        super.onPause();

    }
    public NumpadFragment getNumpadFragment() {
        if (frontFragment == numpadFragment) {
            return numpadFragment;
        }
        return null;
    }
    private void loadNumPadFragment(String phone_num) {
        Bundle bundle = new Bundle();
        bundle.putString("value", phone_num);

        if (numpadFragment == null) {
            numpadFragment = new NumpadFragment();
        }
        frontFragment = numpadFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, numpadFragment).commit();
        numpadFragment.setArguments(bundle);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_QUIT, 0, "Quit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_QUIT:
                quit();
                break;
            default:
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            quit();
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    private void quit() {
        MyApplication myApplication = (MyApplication) getApplicationContext();
        sdk = myApplication.getPortSIPSDK();

        if (myApplication.isOnline()) {
            Line[] mLines = myApplication.getLines();
            for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
                if (mLines[i].getRecvCallState()) {
                    sdk.rejectCall(mLines[i].getSessionId(), 486);
                } else if (mLines[i].getSessionState()) {
                    sdk.hangUp(mLines[i].getSessionId());
                }

                mLines[i].reset();

            }
            myApplication.setOnlineState(false);
            sdk.unRegisterServer();
            sdk.DeleteCallManager();
        }
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancelAll();
        Intent intet= new Intent(mContext,chooseservice.class);
        this.finish();
        startActivity(intet);

    }

}

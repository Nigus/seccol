package com.portsip.bulk;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.LoginFragment;
import com.portsip.MessageFragment;
import com.portsip.MyApplication;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.SettingFragment;
import com.portsip.main.Aboutoss;
import com.portsip.main.Canclemenu;
import com.portsip.main.HomeTabActivity;
import com.portsip.main.Legal;
import com.portsip.main.LoginActivity;
import com.portsip.main.RecentActivity;
import com.portsip.main.SendSms;
import com.portsip.main.SettingMenu;
import com.portsip.main.invite_new.InviteMain;
import com.portsip.main.phonebook.PhonebookMain;
import com.portsip.main.privacypolicy;

import java.util.ArrayList;

import helper.SQLiteHandler;
import helper.SessionManager;

public class chooseservice extends FragmentActivity{
    private View mCall;
    private View mSms;
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private SQLiteHandler db;
    private SessionManager session;
    private MyApplication myApp;
    MyApplication myApplication;
    Context context = null;
    private View v;
    LoginFragment loginFragment;
    ArrayList<String> take_res= new ArrayList<>();
    ArrayList<String> take_res_savedInstance;//= new ArrayList<>();
    SettingFragment settingFragment = null;
    View contentView = null;
    String name,phone,password,email;
    PortSipSdk mSipSdk;
    MessageFragment messageFragment = null;
    Fragment frontFragment;
    public ViewGroup layout=null;
    //@SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        setContentView(R.layout.valg_sms_call);
        getActionBar().setTitle(R.string.app_name);
        getActionBar().setIcon(R.drawable.it_icon);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(true);
        // session manager
        db= new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        selectService();
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("mylist", take_res);
        //123456, nigus@itman.no, Nigussie Girma, 45034734
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        take_res_savedInstance=new ArrayList<>();
        myApplication=(MyApplication)getApplication().getApplicationContext();
        if(savedInstanceState!=null){
            take_res_savedInstance=savedInstanceState.getStringArrayList("mylist");
            myApplication.setUserDetail(take_res_savedInstance);
        }
    }
    @Override
    public void onResume() {
        //UserInfo userInfo= new UserInfo();
        //name,phone,password,email
        /*try {
            userInfo.setAuthName(take_res_savedInstance.get(1).toString());
            userInfo.setUserName(take_res_savedInstance.get(2).toString());
            userInfo.setUserPwd(take_res.get(3).toString());
            Intent intet= new Intent(chooseservice.this, MainActivity.class);
            intet.putExtra("val", take_res_savedInstance.get(2).toString());
            intet.putExtra("val2", take_res_savedInstance.get(3).toString());
            intet.putExtra("val3", take_res_savedInstance.get(4).toString());
            intet.putExtra("val4", take_res_savedInstance.get(1).toString());
            startActivity(intet);
        }catch(Exception e)
        {
            Log.d("chooseservice",e.getMessage() );
        }
        Log.d("chooseservice", "choose service value on savedinstance " + take_res_savedInstance);*/
        super.onResume();
    }
    public void selectService(){
        ImageButton button = (ImageButton) findViewById(R.id.call);
        ImageButton smsbtn=(ImageButton) findViewById(R.id.sms);
        ImageButton phone_bookbtn= (ImageButton) findViewById(R.id.phonebookbtn);
        ImageButton recentbtn=(ImageButton) findViewById(R.id.recentbtn);
        ImageButton invitfriendebtn=(ImageButton) findViewById(R.id.invitebtn);
        final Intent int1 = new Intent(getApplicationContext(),HomeTabActivity.class);
        final Intent int2 = new Intent(getApplicationContext(),SendSms.class);
        final Intent int3= new Intent(getApplicationContext(), PhonebookMain.class);
        final Intent int4= new Intent(getApplicationContext(), RecentActivity.class);
        //final Intent int5= new Intent (getApplicationContext(), InviteFriend.class);
        final Intent int5= new Intent (getApplicationContext(), InviteMain.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something in response to button click
                startActivity(int1);
            }
        });
        smsbtn.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v){
            try {
                //      loadMessageFragment();
                   startActivity(int2);
            }catch(Exception e){
                e.printStackTrace();
            }
            }
        });
        phone_bookbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(int3);
            }
        });
        recentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something in response to button click
                try {
                    startActivity(int4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        invitfriendebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(int5);
            }
        });
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void logoutUser() {
        session.setLogin(false,session.getName(),session.getPhone(),session.getPassword(),session.getEmail());
        db.deleteUsers();
        // Launching the login activity
        /*myApplication=(MyApplication)getApplication().getApplicationContext();
        myApplication.clearApplicationData();*/
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
    public void unregister_service(){
    Intent intent2 = new Intent(getApplicationContext(),Canclemenu.class);
    startActivity(intent2);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.about:
                Aboutoss myfragment = Aboutoss.newInstance();
                myfragment.show(getFragmentManager(), "issues");
                break;
            case R.id.setting:
                Intent intent = new Intent(this,SettingMenu.class);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "Setting will come..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                logoutUser();
                Toast.makeText(getApplicationContext(), "Logged out..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.privacy_policy:
                privacypolicy pfragment= privacypolicy.newInstance();
                pfragment.show(getFragmentManager(), "issues");
                break;
            case R.id.terms:
                Legal newFragment = Legal.newInstance();
                newFragment.show(getFragmentManager(), "issues");
                break;

            case R.id.cancel_service:
                cancel_subs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void cancel_subs(){
        String sub_title=getResources().getString(R.string.subs_title);
        String sub_body=getResources().getString(R.string.subs_body);
        final String sub_res=getResources().getString(R.string.subs_response);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(sub_title);
        alertDialog.setMessage(sub_body);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //delete_db();
                        alertDialog.setMessage(sub_res);
                        unregister_service();
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }
}


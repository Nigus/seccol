package com.portsip.main;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.R;
import com.portsip.main.phonebook.PhonebookMain;

import helper.SQLiteHandler;
import helper.SessionManager;
public class HomeTabActivity extends TabActivity{
    /** Called when the activity is first created. */
    private SQLiteHandler db;
    private MyApplication myApp;
    private SessionManager session;
    Fragment frontFragment;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        //????? Replace the tabHost with fragment classes i.e. need to be re-programmed
        //It needs to be re-programmed with fragment classes, i accepted that
        TabHost tabHost = getTabHost();
        // Tab for calling
        db= new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        myApp=(MyApplication)getApplication();
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        TabHost.TabSpec dialspec = tabHost.newTabSpec("Call"); // for dialing
        //setting Title and Icon for the Tab
        dialspec.setIndicator("", getResources().getDrawable(R.drawable.tabdialericon));
        Intent dialIntent = new Intent(this, Dialer_activity.class);
        dialspec.setContent(dialIntent);
        //Tab for phonebook
        TabHost.TabSpec contactspec = tabHost.newTabSpec("Phonebook");
        contactspec.setIndicator("", getResources().getDrawable(R.drawable.tabcontact1));
        Intent contactIntent = new Intent(this.getApplicationContext(), PhonebookMain.class);
        contactspec.setContent(contactIntent);
        // Tab for recent_call
        TabHost.TabSpec recentspec = tabHost.newTabSpec("Recent");
        recentspec.setIndicator("", getResources().getDrawable(R.drawable.tabrecent1));
        Intent recentIntent = new Intent(this, RecentActivity.class);
        recentspec.setContent(recentIntent);
        // Adding all TabSpec to TabHost
        tabHost.addTab(dialspec); // Adding dialer tab
        tabHost.addTab(contactspec); // Adding contact tab
        tabHost.addTab(recentspec); // Adding recent tab
        //the follwing class will be used to make an internactive tab layout.
        getActionBar().setDisplayHomeAsUpEnabled(true);
}
    @Override
    protected void onResume() {
        try {
            ((MyApplication) getApplicationContext()).setHometabActivity(this);
        }catch(Exception e){
            //((MyApplication) getApplicationContext()).setHometabActivity((HomeTabActivity) this.getApplicationContext());
            //Intent intet = new Intent(this.getApplicationContext(),MainActivity.class);
            //startActivity(intet);
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        ((MyApplication) getApplicationContext()).setHometabActivity(this);
        super.onPause();
    }
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intet= new Intent(this.getApplicationContext(),MainActivity.class);
        intet.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intet);
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
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }
    public void unregister_service(){
        Intent intent2 = new Intent(this.getApplicationContext(),Canclemenu.class);
        startActivity(intent2);
    }
    private void logoutUser() {
        //boolean isLoggedIn,String name,String phone,String password,String email
        session.setLogin(false,session.getName(),session.getPhone(),session.getPassword(),session.getEmail());
        db.deleteUsers();
        // Launching the login activity
        myApp.setOnlineState(false);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
/*

* */
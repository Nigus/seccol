package com.portsip;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.main.Aboutoss;
import com.portsip.main.Canclemenu;
import com.portsip.main.HomeTabActivity;
import com.portsip.main.Legal;
import com.portsip.main.LoginActivity;
import com.portsip.main.phonebook.PhonebookMain;
import com.portsip.main.RecentActivity;
import com.portsip.main.SendSms;
import com.portsip.main.SettingMenu;
import com.portsip.main.invite_new.InviteMain;
import com.portsip.main.privacypolicy;
import com.portsip.util.Line;

import java.util.ArrayList;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Nigussie on 16.10.2015.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    private static final String TAG = "HomeFragment";
    PortSipSdk sdk;
    Button mbtnReg, mbtnUnReg;
    public String phone_number;
    public String name;
    public String email;
    public String password;
    TextView mtxStatus;
    String statuString;
    MyApplication myApplication;
    Context context = null;
    String LogPath = null;
    ArrayList<String> take_res= new ArrayList<>();
    private SQLiteHandler db;
    private SessionManager session;
    private ImageButton callBtn,smsBtn,inviteBtn,recentBtn,phonebookBtn;
    private LinearLayout addLinear;
    View rootView;

    private String key;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.valg_sms_call, null);
        context = getActivity().getApplicationContext();
        session = new SessionManager(context);
        if (session.isLoggedIn()) {
            Log.d(TAG,"Session on progress====>");
        }
        db= new SQLiteHandler(context);
        session = new SessionManager(context);
        key=session.getlicKey();

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        myApplication = ((MyApplication) context);

        initView(rootView);
        return rootView;
    }
    @Override
    public void onResume(){
          super.onResume();
        if(!myApplication.isOnline()){
            //Reconnect_Server server= new Reconnect_Server(context);
            //server.online();
            Log.d(TAG,"-----I AM OFFLINE----");
        }
    }
    private void initView(View view) {
            callBtn=(ImageButton) view.findViewById(R.id.call);
            smsBtn=(ImageButton)view.findViewById(R.id.sms);
            addLinear=(LinearLayout)view.findViewById(R.id.dialpadAdditionalButtons);
            inviteBtn=(ImageButton)addLinear.findViewById(R.id.invitebtn);
            recentBtn=(ImageButton)addLinear.findViewById(R.id.recentbtn);
            phonebookBtn=(ImageButton)addLinear.findViewById(R.id.phonebookbtn);
            callBtn.setOnClickListener(this);
            smsBtn.setOnClickListener(this);
            inviteBtn.setOnClickListener(this);
            recentBtn.setOnClickListener(this);
            phonebookBtn.setOnClickListener(this);
    //selectService();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:
                Intent intent=new Intent(getActivity(),HomeTabActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.sms:
                Intent intent2=new Intent(getActivity(),SendSms.class);
                session.setSmsInf(null,null,null,null,null,false);
                getActivity().startActivity(intent2);
                break;
            case R.id.invitebtn:
                Intent intent3=new Intent(getActivity(),InviteMain.class);
                getActivity().startActivity(intent3);
                break;
            case R.id.recentbtn:
                Intent intent4=new Intent(getActivity(),RecentActivity.class);
                getActivity().startActivity(intent4);
                break;
            case R.id.phonebookbtn:
                Intent intent5=new Intent(getActivity(),PhonebookMain.class);
                getActivity().startActivity(intent5);
                break;
            default:
                break;
        }
    }
    public void onRegisterSuccess(String reason, int code) {
        Log.d(TAG,"Registered on sip server");
        //undateStatus();
    }
    public void onRegisterFailure(String reason, int code) {
        statuString = reason;
        Log.d(TAG,"Reason to log out of asterisk is: "+statuString);
			/*mContext=getActivity().getApplicationContext();
			final Reconnect_Server reconnect_server=new Reconnect_Server(mContext);
			new Thread(new Runnable() {
				public void run() {
					getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "About to register on sip asterisk");
                            reconnect_server.online();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    });
				}
			}).start();*/
        //undateStatus();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    private void logoutUser() {
        session.setLogin(false,session.getName(),session.getPhone(),session.getPassword(),session.getEmail());
        db.deleteUsers();
        // Launching the login activity
        /*myApplication=(MyApplication)getApplication().getApplicationContext();
        myApplication.clearApplicationData();*/
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();

    }
    public void unregister_service(){
        Intent intent2 = new Intent(getActivity(),Canclemenu.class);
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
                myfragment.show(getActivity().getFragmentManager(), "issues");
                break;
            case R.id.setting:
                Intent intent = new Intent(getActivity(),SettingMenu.class);
                startActivity(intent);
                break;
            case R.id.logout:
                logoutUser();
                quit();
                Toast.makeText(getActivity(), "Logged out..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.privacy_policy:
                privacypolicy pfragment= privacypolicy.newInstance();
                pfragment.show(getActivity().getFragmentManager(), "issues");
                break;
            case R.id.terms:
                Legal newFragment = Legal.newInstance();
                newFragment.show(getActivity().getFragmentManager(), "issues");
                break;

            case R.id.cancel_service:
                cancel_subs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void quit() {
        MyApplication myApplication = (MyApplication) getActivity().getApplicationContext();
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
        ((NotificationManager) getActivity().getSystemService(MyApplication.NOTIFICATION_SERVICE))
                .cancelAll();
        getActivity().finish();
    }
    public void cancel_subs(){
        String sub_title=getResources().getString(R.string.subs_title);
        String sub_body=getResources().getString(R.string.subs_body);
        final String sub_res=getResources().getString(R.string.subs_response);
        final AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity().getApplicationContext()).create();
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

package com.portsip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.portsip.util.Line;
import com.portsip.util.Network;
import com.portsip.util.SettingConfig;
import com.portsip.util.UserInfo;

import java.util.Random;

import helper.SessionManager;

/**
 * Created by Nigussie on 26.11.2015.
 */
public class Reconnect_Server{
    public static final String TAG="Reconnect_Server";
    Context context;
    String statuString;
    SessionManager session;
    PortSipSdk mSipSdk;
    MyApplication myApplication;
    String name,password,phone_number;
    String licenseKey;
    String LogPath = null;

    public Reconnect_Server(Context context) {
        this.context=context;

    }

public int online() {
    //context=this.myApplication.getApplicationContext();
    session= new SessionManager(context);
    myApplication = ((MyApplication) context);
    mSipSdk = myApplication.getPortSIPSDK();
    licenseKey=session.getlicKey();
    int result = setUserInfo();
    if (result == PortSipErrorcode.ECoreErrorNone) {
        result = mSipSdk.registerServer(90,3);
        if(result!=PortSipErrorcode.ECoreErrorNone ){
            statuString = "register server failed";
            //undateStatus();
            Log.d(TAG, "RESULT is--: " + result);
        }
    }else {
        //undateStatus();
        Log.d(TAG,"RESULT is--: "+result);
    }
    //Log.d(TAG,"Login succeddeddd --OK--");
    return result;
}
    int setUserInfo() {
        //userUpdate();
        Environment.getExternalStorageDirectory();
        LogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + '/';
        String localIP = new Network(context).getLocalIP(false);// ipv4
        int localPort = new Random().nextInt(4940) + 5060;
        UserInfo info = saveUserInfo();
        if(info.isAvailable())
        {
            mSipSdk.CreateCallManager(context.getApplicationContext());// step 1
            int result = mSipSdk.initialize(info.getTransType(),
                    PortSipEnumDefine.ENUM_LOG_LEVEL_DEBUG, LogPath,
                    Line.MAX_LINES, "PortSIP VoIP SDK for Android",
                    0,0);// step 2
            if (result != PortSipErrorcode.ECoreErrorNone) {
                statuString = "init Sdk Failed";
                return result;
            }
            int nSetKeyRet = mSipSdk.setLicenseKey(licenseKey);// step 3
            if (nSetKeyRet == PortSipErrorcode.ECoreTrialVersionLicenseKey) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Prompt").setMessage(R.string.trial_version_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else if (nSetKeyRet == PortSipErrorcode.ECoreWrongLicenseKey) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Prompt").setMessage(R.string.wrong_lisence_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return -1;
            }
            result = mSipSdk.setUser(info.getUserName(), info.getUserDisplayName(), info.getAuthName(), info.getUserPassword(),
                    localIP, localPort, info.getUserdomain(), info.getSipServer(), info.getSipPort(),
                    info.getStunServer(), info.getStunPort(), null, 3479);// step 4
            if (result != PortSipErrorcode.ECoreErrorNone) {
                statuString = "setUser resource failed";
                return result;
            }
        } else {
            return -1;
        }
        SettingConfig.setAVArguments(context, mSipSdk);
        return PortSipErrorcode.ECoreErrorNone;
    }
    /*void loadUserInfo(){
        //userUpdate();
        UserInfo userInfo = SettingConfig.getUserInfo(context);
        //String item= userInfo.getUserName()==null?"":userInfo.getUserName();
        String item= phone_number;//userInfo.getUserName()==null?"":userInfo.getUserName();
        userInfo.setUserName(item);//
        Log.d(TAG,"==============ITEM============="+item+password+phone_number);
        ((EditText) findViewById(R.id.etusername)).setText(item);
        item= userInfo.getUserPassword()==null?"":userInfo.getUserPassword();
        item=password;
        userInfo.setUserPwd(password);
        ((EditText) findViewById(R.id.etpwd)).setText(item);
        item= userInfo.getSipServer()==null?"":userInfo.getSipServer();
        userInfo.setSipServer("itmansecurity.cloudapp.net;
        ((EditText) findViewById(R.id.etsipsrv)).setText(item);
        item= ""+userInfo.getSipPort();
        ((EditText) findViewById(R.id.etsipport)).setText(item);
        item= userInfo.getUserdomain()==null?"":userInfo.getUserdomain();
        ((EditText) findViewById(R.id.etuserdomain)).setText(item);
        item= userInfo.getAuthName()==null?"":userInfo.getAuthName();
        ((EditText) findViewById(R.id.etauthName)).setText(item);
        item= name;//userInfo.getUserDisplayName()==null?"":userInfo.getUserDisplayName();
        userInfo.setUserName(name);
        ((EditText) findViewById(R.id.etdisplayname)).setText(item);
        item= userInfo.getStunServer()==null?"":userInfo.getStunServer();
        ((EditText) findViewById(R.id.etStunServer)).setText(item);
        item= ""+userInfo.getStunPort();
        ((EditText) findViewById(R.id.etStunPort)).setText(item);
        Spinner spTransport = (Spinner) findViewById(R.id.spTransport);
        Spinner spSRTP = (Spinner) findViewById(R.id.spSRTP);
        userInfo.setStunServer("itmansecurity.cloudapp.net");
        spTransport.setAdapter(new ArrayAdapter<String>(context,
                R.layout.viewspinneritem, getResources().getStringArray(
                R.array.transpots)));
        spSRTP.setAdapter(new ArrayAdapter<String>(context,
                R.layout.viewspinneritem, getResources().getStringArray(
                R.array.srtp)));
        spSRTP.setOnItemSelectedListener(this);
        spTransport.setOnItemSelectedListener(this);
        int SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_FORCE;
        SettingConfig.setSrtpType(context, SrtType, mSipSdk);
        spTransport.setSelection(userInfo.getTransType());
        spSRTP.setSelection(SettingConfig.getSrtpType(context));
    }*/
    private UserInfo saveUserInfo(){
        int port;
        String cloudserver;
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(phone_number);
        userInfo.setUserPwd(password);
        userInfo.setUserDisplayName(name);
        String item1 = session.getPhone();//((EditText) findViewById(R.id.etusername)).getText().toString();
        userInfo.setUserName(item1);
        String item2= session.getPassword();
        //String item2 = ((EditText) findViewById(R.id.etpwd)).getText().toString();
        userInfo.setUserPwd(item2);
        String item = "itmansecurity.cloudapp.net";//((EditText) findViewById(R.id.etsipsrv)).getText().toString();
        try{
            cloudserver=item.toString();
        }
        catch(Exception e){
            cloudserver="itmansecurity.cloudapp.net";
        }
        userInfo.setSipServer(cloudserver);
        item = "5060";//((EditText) findViewById(R.id.etsipport)).getText().toString();
        try{
            port = Integer.parseInt(item);
        }catch(NumberFormatException e){
            port = 5060;
        }
        userInfo.setSipPort(port);
        item = null;//((EditText) findViewById(R.id.etuserdomain)).getText().toString();
        userInfo.setUserDomain(item);
        item = null;//((EditText) findViewById(R.id.etauthName)).getText().toString();
        userInfo.setAuthName(item);
        item = session.getName();//((EditText) findViewById(R.id.etdisplayname)).getText().toString();
        userInfo.setUserDisplayName(item);
        item = "itmansecurity.cloudapp.net";//((EditText) findViewById(R.id.etStunServer)).getText().toString();
        userInfo.setStunServer(item);
        item = "3478";//((EditText) findViewById(R.id.etStunPort)).getText().toString();
        try{
            port = Integer.parseInt(item);
        }catch(NumberFormatException e){
            port = 3478;
        }
        userInfo.setStunPort(port);
        userInfo.setTranType(PortSipEnumDefine.ENUM_TRANSPORT_UDP);// ((Spinner) findViewById(R.id.spTransport)).getSelectedItemId());
        SettingConfig.setUserInfo(context, userInfo);
        return userInfo;
    }


}

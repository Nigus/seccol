package com.portsip.main;
/*
 * Property of IT Man AS, Bryne Norway. It is strictly forbidden to copy or modify the authors file,
 * (C) 2015 IT Man AS
 * Created by Nigussie on 03.03.2015.
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.Reconnect_Server;
import com.portsip.register_user.RegisterUserActivity;
import com.portsip.util.Line;
import com.portsip.util.Network;
import com.portsip.util.SettingConfig;
import com.portsip.util.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import helper.SQLiteHandler;
import helper.SessionManager;

//import com.microsoft.windowsazure.mobileservices.*;
public class LoginActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener {
    // LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private String phone;
    private String resp;
    private String passwordSend;
    String intet_email;
    private String login_at;
    public String phoneNumber;
    public String mName;
    Intent callerIntent;
    MyApplication myapp;
    ArrayList<String> mySavedValues;
    ArrayList<String> savedArrays;
    String localname,localemail,localpassword,localphone;
    View myview;
    private ViewGroup layout=null;
    PortSipSdk mSipSdk;
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
    /************* Do not edit the following key code.************  IT is a licensed key and no one has a right to use it**********/
    String licenseKey;
    private SQLiteHandler db;
    private SessionManager session;
    private boolean rec_flag=false;
    private boolean res_flag;
    public String storedName;
    LayoutInflater inf_copy;
    Context mContext;
    ArrayList<String> mylistRec;
    Reconnect_Server reconnect_server;
    //private MobileServiceClient mClient;
    //@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.setErr (new HProfDumpingStderrPrintStream (System.err));
        setContentView(R.layout.activity_login);
        //Get the values from the layout file
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        Button rsetpass;
        rsetpass=(Button) findViewById(R.id.btnReset);
        // NotificationsManager.handler
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // For portsip logging lets inflate the login here
        // Session manager
        session = new SessionManager(getApplicationContext());
        session.setlicKey();
        licenseKey=session.getlicKey();
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not
        myapp=(MyApplication)getApplication();
        context=getApplicationContext();
        myApplication = ((MyApplication) context);
        mSipSdk = myApplication.getPortSIPSDK();
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //myview=inflater.inflate(R.layout.loginview, layout);
        initView();
        if (session.isLoggedIn()) {
            session.setCallingFlag(false);
            if(!myApplication.isOnline()){
                mContext=this.getApplicationContext();
                reconnect_server=new Reconnect_Server(mContext);
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"About to register on sip asterisk");
                                reconnect_server.online();
                                //online();
                            }

                        });
                    }
                }).start();
            }
        try {
            localemail=session.getEmail();
            localname=session.getName();
            localpassword=session.getPassword();
            localphone=session.getPhone();
        }catch(Exception e){
            Log.d(TAG,"Error"+e.getMessage());
        }
            Log.d(TAG,"&&&&&&&&&& sharedpreference value is: "+"\n"+session.getName()+"\n"+session.getEmail()+"\n"+session.getPassword());
            Log.d(TAG,"The app is logged inn"+session.isLoggedIn());
            Intent intent = new Intent(this.getApplicationContext(),MainActivity.class);
            intent.putExtra("phoneIn", localphone);//
            intent.putExtra("passwordIn", localpassword);
            intent.putExtra("emailIn", localemail);
            intent.putExtra("nameIn", localname);
            intent.putExtra("flag", false);//to flag the dialer
            intent.putExtra("dialingStateNr", "null");
            startActivity(intent);
            finish();
            /*
            callerIntent.putExtra("val", phoneInput);
            callerIntent.putExtra("passwordIn", passwordSend);
            callerIntent.putExtra("emailIn", intet_email);
            callerIntent.putExtra("nameIn", nameInput);
            callerIntent.putExtra("flag",false);//to flagg the dialer
            callerIntent.putExtra("dialingStateNr", "null");
            */
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        login_at = sdf.format(c.getTime());
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                passwordSend=password;
                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                    //}
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter your email!", Toast.LENGTH_LONG)
                            .show();
                }
                //finish();
            }
        });
        //plus_sign_in_button login code will be
        /*Button plus_btn=(Button) findViewById(R.id.plus_sign_in_button);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        chooseservice.class);
                startActivity(i);
                finish();
            }
       });
       */
       //Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterUserActivity.class);
                startActivity(i);
                finish();
            }
        });
        rsetpass.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent in= new Intent(getApplicationContext(),sendNewPass.class);
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        myapp=(MyApplication)getApplication();
        savedInstanceState.putStringArrayList("userList", myapp.getUserDetail());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null){
            myapp.setUserDetail(savedInstanceState.getStringArrayList("userList"));
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    /*@Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }*/
    private void launchMain(final String nameInput, final String phoneInput,
                            final String password,final String email){
        session.setCallingFlag(false);
        // Inflate the variables here
        callerIntent=new Intent(this,
                MainActivity.class);
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        online();
                        callerIntent.putExtra("phoneIn", phoneInput);
                        callerIntent.putExtra("passwordIn", password);
                        callerIntent.putExtra("emailIn", email);
                        callerIntent.putExtra("nameIn", nameInput);
                        callerIntent.putExtra("flag",false);//to flagg the dialer
                        callerIntent.putExtra("dialingStateNr", "null");
                        startActivity(callerIntent);
                        finish();
                    }
                });
            }
        }).start();

   }
    private void initView() {
        //mtxStatus = (TextView) view.findViewById(R.id.txtips);
        loadUserInfo();
    }
    private void SetTransType(int index) {
        int transType = PortSipEnumDefine.ENUM_TRANSPORT_UDP;
        switch (index) {
            case 0:
                transType = PortSipEnumDefine.ENUM_TRANSPORT_UDP;
                break;

            case 1:
                transType = PortSipEnumDefine.ENUM_TRANSPORT_TLS;
                break;
            case 2:
                transType = PortSipEnumDefine.ENUM_TRANSPORT_TCP;
                break;

            case 3:
                transType = PortSipEnumDefine.ENUM_TRANSPORT_PERS;
                break;
        }

        SettingConfig.setTransType(context, transType);
    }

     /**
     *  function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
       //Tag used to cancel the request
        String tag_string_req = "req_login";
       //pDialog.setMessage("Logging in ...");
        intet_email=email.toString();
        Log.d(TAG, "intet_email=email" + intet_email);
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                resp=response;
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.getString("email");
                        phone = user.getString("phone");
                        /////////////////////////////
                        jObj = jObj.getJSONObject("user");
                        mName=jObj.getString("name");
                        phoneNumber=jObj.getString("phone");
                        Log.d(TAG, "2. Inside JSON parsor: " + mName + phoneNumber);
                        //boolean isLoggedIn,String name,String phone,String password,String email
                        session.setLogin(true,mName,phoneNumber,password,email);
                        /********** addTimer i want to recode it ************/
                        //addTimer(email);
                        //if (session.isLoggedIn()){
                        launchMain(mName,phoneNumber,password,email);
                        //finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
             //   params.put("phone",phone);
                return params;
            }
        };
        //phoneNumber=phone;
        Log.d(TAG, "HERE---" + phoneNumber);
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    /*
    *
    * Adding timer
    **/
    private void addTimer(final String email) {
        String tag_string_req = "req_timer";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "The response is ::::::::" + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.getString("email");
                      //  String login_at = user.getString("login_at");
                        Log.d(TAG, "Email and login_at inside JSON" + email + login_at);
                        db.setTimeStamp(uid,email,login_at);
                        //finish();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "my_timer");
                params.put("email", email);
                //params.put("login_at",login_at);
                //params.put("password",password);
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

     private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    void loadUserInfo(){
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
        userInfo.setSipServer("itmansecurity.cloudapp.net");
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
    }
    private UserInfo saveUserInfo(){
        int port;
        String cloudserver;
        UserInfo userInfo = new UserInfo();
        SessionManager sessionManager= new SessionManager(context);
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
    private int online() {
        int result = setUserInfo();
        if (result == PortSipErrorcode.ECoreErrorNone) {
            result = mSipSdk.registerServer(90, 3);
            if(result!=PortSipErrorcode.ECoreErrorNone ){
                statuString = "register server failed";
                //undateStatus();
                Log.d(TAG,"RESULT is--: "+result);
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
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        switch (arg0.getId()) {
            case R.id.spSRTP:
                SetSRTPType(arg2);
                break;
            case R.id.spTransport:
                SetTransType(arg2);
            default:
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void SetSRTPType(int index) {
        int SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;
        switch (index) {
            case 0:
                SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;
                break;
            case 1:
                SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_FORCE;
                break;
            case 2:
                SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_PREFER;
                break;
        }
        SettingConfig.setSrtpType(context, SrtType, mSipSdk);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intet= new Intent(this.getApplicationContext(),LoginActivity.class);
        //intet.setFlags()
        startActivity(intet);
    }

}




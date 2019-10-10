package com.portsip.bulk;
/**
 * Created @ IT Man, Bryne Norway, (C) 2015 IT Man AS
 *
 * by Nigussie on 03.03.2015.
 **/
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.main.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;
//import com.google.android.gms.internal.is;
public class MyMainActivity extends Activity {
    private static final String TAG = "MyMainActivity";
    ActionBar.Tab dialertab, contacttab, recenttab;
    private EditText username;
    private EditText password;
    private Button login;
    private Button register;
    private TextView loginLockedTV;
    private TextView attemptsLeftTV;
    private TextView numberOfRemainingLoginAttemptsTV;
    int numberOfRemainingLoginAttempts = 3;
    //Fragment dialerFragmentTab = new DialerFragment();
    //Fragment contactFragmentTab= new contactFragment();
    //Fragment recentFragTab= new RecentFragment();
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private SQLiteHandler db;
    private SessionManager session;
    private String phone_number,email,name;
    private String mypassword;
    private String myEmail;
    String response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            //Intent intent = getIntent();
            phone_number=extras.getString("val");
            mypassword=extras.getString("val2");
            email=extras.getString("val3");
            name=extras.getString("val4");
        }
        Log.d(TAG,"phone+email+name: "+ phone_number+email+mypassword+name);
      //session = new SessionManager(getApplicationContext());
      /*  try {
         JSONObject jObj = new JSONObject(response);
         JSONObject jObj = new JSONObject(response);
         jObj = jObj.getJSONObject("user");
         name = jObj.getString("name");
         phone_number=jObj.getString("phone");
        //Toast.makeText(getBaseContext(), "" + name+phone_number, Toast.LENGTH_LONG).show();
    }
    catch(Exception e){
        e.printStackTrace();
    }*/
       // addTimer(email, login_at);
        Intent reg_intent= new Intent(getApplicationContext(),MainActivity.class); // to call the main class here
        reg_intent.putExtra("value1",name);
        reg_intent.putExtra("value2", phone_number);
        reg_intent.putExtra("value3",mypassword);
        startActivity(reg_intent);
        finish();
    }

    private void logoutUser() {
        session.setLogin(false,session.getName(),session.getPhone(),session.getPassword(),session.getEmail());
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

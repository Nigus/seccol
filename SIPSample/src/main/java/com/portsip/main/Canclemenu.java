package com.portsip.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.R;
import com.portsip.LoginFragment;
import com.portsip.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Nigussie on 02.06.2015.
 */
public class Canclemenu extends FragmentActivity {
    private static final String TAG = Canclemenu.class.getSimpleName();
    LoginFragment cancleService = null;
    android.support.v4.app.Fragment frontFragment;
    public ViewGroup layout = null;
    RelativeLayout mainLayout;
    public boolean flagg = false;
    private SessionManager session;
    private SQLiteHandler db;
    String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);
        mainLayout = (RelativeLayout) findViewById(R.id.relativelay);
        mainLayout.setBackgroundResource(R.drawable.bgimage);
        // Set the cancle flag true to let the sdk manager to unsubscribe the Service
        flagg = true;
        session = new SessionManager(this.getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(this.getApplicationContext());
        //delete_db();
        ///////////////////////////////////////////////////*************************////////////////////////////////
        loadCancelFragment();
        uninstall_me();
        ///////////////////////////////////////////////////*************************////////////////////////////////
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void delete_db() {
        String tag_string_req = "req_delete";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully updated in MySQL
                        // Now delete the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        //String name = user.getString("name");
                         email = user.getString("email");
                         password = user.getString("password");
                        //String created_at = user
                        //       .getString("created_at");
                        //Deleting a row from a database
                        db.Delete_user_byemail(uid,email);//(uid, email, password, created_at);
                        finish();
                    }else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.getMessage();//printStackTrace();
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
                params.put("tag", "deleting");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void uninstall_me(){

        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:com.portsip"));
        startActivity(intent);
/*
        Uri packageUri = Uri.parse("com.portsip");
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        startActivity(uninstallIntent);*/
    }
    private void loadCancelFragment() {
        Bundle args = new Bundle();
        args.putBoolean("FLAGG", flagg);
        //args.putString("FLAGG", String.valueOf(flagg));
        if (cancleService == null) {
            cancleService = new LoginFragment();
        }
        frontFragment = cancleService;
        cancleService.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cancleService).commit();
    }
}

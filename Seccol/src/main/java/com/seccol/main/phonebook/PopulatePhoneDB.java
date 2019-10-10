package com.portsip.main.phonebook;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MyApplication;
import com.portsip.main.app_utils.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Arsema on 09.10.2015.
 */
public class PopulatePhoneDB {
    public SessionManager session;
    public SQLiteHandler db;
    MyApplication myapp;
    ArrayList<String> tempo=new ArrayList<>();
    final static String TAG="PopulatePhoneDB";
    Context context;
    public void PopulatePhoneDB(Context context){
        this.context=context;
    }
    public void mymain(Context context){

        session = new SessionManager(this.context.getApplicationContext());
        db = new SQLiteHandler(this.context.getApplicationContext());
        myapp=new MyApplication();
        check_dbEn();
        Log.d(TAG,":____________________:"+tempo);
        //myapp.setPhone_list(tempo);
    }
    public void check_dbEn(){

        // Let me call the backend class here
        // Tag used to cancel the request
        String tag_string_req = "req_checkin";
        final int count=0;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetching Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        //User successfully stored in MySQL
                        //Now store the user in sqlite
                        //            String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        Log.d(TAG,"users are: "+user);
                        String name = user.getString("name");
                        String phone = user.getString("phone");
                        tempo.add(phone);
                        Log.d(TAG, "name  and phone from db are" + phone);
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        //Toast.makeText(context.getApplicationContext(),
                        //           errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetching Error: " + error.getMessage());

                Toast.makeText(context.getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "checkin");
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}

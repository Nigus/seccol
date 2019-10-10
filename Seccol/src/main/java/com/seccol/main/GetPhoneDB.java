package com.portsip.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Nigussie on 07.10.2015.
 */
public class GetPhoneDB{
    final static String TAG=GetPhoneDB.class.getSimpleName();//"getPhoneDB";
    SessionManager session;
    SQLiteHandler db;
    int count;
    String name,phone;
    String[] result;
    boolean checkFlag;
    Person bContact;
    ArrayList<Person> contactList2;
    ArrayList<String> dbPhone,dbName;
    MyApplication myAppl=new MyApplication();
    public void checkDbMain(Context context) {
        dbPhone=new ArrayList<String>();
        dbName= new ArrayList<String>();
        session=new SessionManager(context);
        db=new SQLiteHandler(context);
        check_db();
        Log.d(TAG, "-------------DB_Local list-------------"+dbPhone);
    }
    public void check_db(){
        // Let me call the backend class here
        // Tag used to cancel the request
        count++;
        Log.d(TAG, "Count value" + count);
        //result=new String[count];
        String tag_string_req = "req_getin";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetching Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    bContact=new Person();
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                        phone = user.getString("phone").toString();
                        dbPhone.add(phone);
                        Log.d(TAG,"Phone and name are:"+phone);
                    } else {
                        // Error occurred in registration. Get the error
                        // message

                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetching Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getin");
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

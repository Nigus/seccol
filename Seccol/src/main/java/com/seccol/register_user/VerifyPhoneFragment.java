package com.portsip.register_user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.R;
import com.portsip.main.AppConfig;
import com.portsip.main.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

public class VerifyPhoneFragment extends BaseFlagFragment {
    private static final String TAG = "VerifyPhoneFragment";
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    Context context;
    public VerifyPhoneFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_register, container, false);
        context=getActivity().getApplicationContext();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(context);
        // SQLite database handler
        db = new SQLiteHandler(context);
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(context,
                    MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        initUI(rootView);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCodes(getActivity());
    }
    @Override
    protected void send() {
        hideKeyboard(mPhoneEdit);
        mPhoneEdit.setError(null);
        String phone = validate();
        if (phone == null) {
            mPhoneEdit.requestFocus();
            mPhoneEdit.setError(getString(R.string.invalid_sip_uri));
            return;
        }
        Toast.makeText(getActivity(), "send to " + phone, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected  void registerUser(final String name, final String email, final String phone, final String password){
        String tag_string_req = "req_register";
        pDialog=new ProgressDialog(getActivity().getApplicationContext());
        pDialog.setMessage("Registering ..." + name + phone);
        //showDialog();
        final String myphone = phone.replaceAll("[^0-9.+]", "");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                //hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String phone = user.getString("phone");
                        String created_at = user
                                .getString("created_at");
                        // Inserting row in users table sqllite
                        session.setCountryCode(phone);
                        db.addUser(name, email, phone, uid, created_at);
                        // Launch login activity
                        Intent intent2 = new Intent(
                                getActivity().getApplicationContext(),
                                LoginActivity.class);
                        startActivity(intent2);
                        getActivity().finish();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        /*Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                /*Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();*/
                //hideDialog();
                //if(error.getMessage()=="usera")
                Intent intent2 = new Intent(
                        getActivity().getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent2);
                getActivity().finish();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("phone", myphone);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //Send email bekreftelse
        String text= String.valueOf(R.string.emailconf);
        Log.d(TAG, "Email body is:" + text);
        //sendEmail(email,text);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

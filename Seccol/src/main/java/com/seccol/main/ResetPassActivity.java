package com.portsip.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MainActivity;
import com.portsip.R;
import com.portsip.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Niggui on 02.04.2015.
 */
public class ResetPassActivity extends Activity {
    private static final String TAG = ResetPassActivity.class.getSimpleName();
    private Button btnSave;
    EditText reset_kode;
    private EditText inputPassword;
    private EditText inputretypePassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    //public final String myemail;
    public String gobalEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);

        //inputEmail = (EditText) findViewById(R.id.email);
        reset_kode = (EditText) findViewById(R.id.kodesms);
        inputPassword = (EditText) findViewById(R.id.rpassword1);
        //btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSave = (Button) findViewById(R.id.btnReset);
        btnSave = (Button) findViewById(R.id.btnReset);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(ResetPassActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        String tempEmail="";
        String resetcode="";

        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            //Intent intent = getIntent();
            tempEmail = extras.getString("tempEmail");
            resetcode = extras.getString("SMScode");
        }
        //String resetcode="45332";
        // myemail
        // Check the onetime code with the email sent item
        // Register Button Click event
        final String finalResetcode = resetcode;
        final String finalEmail = tempEmail;
        //final String finalEngangkode = engangkode;
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ////
                String password = inputPassword.getText().toString();
                String engangkode=reset_kode.getText().toString();
                assert finalResetcode != null;
                if(finalResetcode.equals(engangkode.toString())){
                    resetUser(finalEmail, password);
                    Intent intent = new Intent(
                            ResetPassActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
            }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Invalid Code!", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
            }
        });
        /*String alert1 = "email: " + finalEmail;
        String alert2 = "resetcode: " + finalResetcode;
        String alert3 = "engangskode: " + reset_kode.getText().toString();
        alertDialog.setMessage(alert1 +"\n"+ alert2 +"\n"+ alert3);
        alertDialog.show();*/
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void resetUser(final String email,
                              final String password) {
        // Tag used to cancel the request
        //String tag_string_req = "req_register";
        String tag_string_req = "req_reset";
        pDialog.setMessage("Reseting password ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully updated in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String password=user.getString("password");
                        String created_at = user
                                .getString("created_at");
                        // Inserting row in users table
                        //db.addUser(name, email, uid, created_at);
                        db.updateUser(uid, email, password, created_at);
                        // Launch login activity
                        Intent intent = new Intent(
                                ResetPassActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
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
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "reset");
                params.put("email", email);
                params.put("password", password);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.setting) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


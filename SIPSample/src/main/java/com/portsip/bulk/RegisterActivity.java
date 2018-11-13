package com.portsip.bulk;
/*
 *
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * © 2015 IT Man AS
 *
 * Created by Nigussie on 03.03.2015.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MainActivity;
import com.portsip.R;
import com.portsip.MyApplication;
import com.portsip.main.AppConfig;
import com.portsip.main.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputphone;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPasswordretype;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    public String tempnumber;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        final EditText emailValidate = (EditText)findViewById(R.id.email);
        final TextView textView = (TextView)findViewById(R.id.text);
        inputphone= (EditText) findViewById(R.id.phone);
        //inputphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        inputPassword = (EditText) findViewById(R.id.password);
        //new retype pw
        inputPasswordretype = (EditText) findViewById(R.id.password2);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        //Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
        //Lets get the number from the diler if the user wants to save the values of the number here by this
        Bundle extras;
        extras=getIntent().getExtras();
        if(extras!=null) {
            //Intent intent = getIntent();
            tempnumber = extras.getString("phone_number");
        }
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
               // String email = inputEmail.getText().toString();
                String unformated_phone;
                if(tempnumber!=null) {
                    unformated_phone=tempnumber;
                }
                else{
                    unformated_phone = inputphone.getText().toString();
                }
                unformated_phone = inputphone.getText().toString();
                String password = inputPassword.getText().toString();
                String password2= inputPasswordretype.getText().toString();
                //if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                //PhoneNumberUtils.formatNumber(Editable text, int defaultFormattingType);

                //String phone = PhoneNumberUtils.formatNumber(unformated_phone);
                String phone = unformated_phone;

                /////// Validate email
                final String email = emailValidate.getText().toString().trim();
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                emailValidate .addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        if (email.matches(emailPattern) && s.length() > 0)
                        {
                            Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                            // or
                            textView.setText("valid email");
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
                            //or
                            textView.setText("invalid email");
 //                           textView.setTextColor(Color.parseColor("EDB61225"));
                            return;
                                    //"EDB61225");
                        }
                    }
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // other stuffs
                    }
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // other stuffs
                    }
                });
                ///////////////
                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "Please enter values!", Toast.LENGTH_LONG)
                            .show();
                }
                //}
                if(password.length()<6){
                    Toast.makeText(getApplicationContext(), "Password must be six or more characters", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!password.equals(password2))
                {
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                }
                else{

                    registerUser(name, email, phone, password);

                }
                Toast.makeText(getApplicationContext(),
                        phone, Toast.LENGTH_LONG)
                        .show();
            }
        });
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email, final String phone,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("Registering ...");
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
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
                        // Inserting row in users table
                        db.addUser(name, email, phone, uid, created_at);
                        // Launch login activity
                        Intent intent2 = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent2);
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
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
    public void validate_email(){
    }
}

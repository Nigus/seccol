package com.portsip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.main.AppConfig;
import com.portsip.bulk.chooseservice;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import helper.SQLiteHandler;
import helper.SessionManager;
/**
 * Created by Nigussie on 15.06.2015.
 */
public class UninstallIntentActivity extends Activity {
    private static final String TAG = UninstallIntentActivity.class.getSimpleName();
    private SessionManager session;
    private String phone;
    private SQLiteHandler db;
    String email, password;
    Context context;
    //497872873313 Project number
    public static final String SENDER_ID = "497872873313";
    @Override
    public void onStart(){
        super.onStart();
        BroadcastReceiver br = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);
        String sub_title=getResources().getString(R.string.subs_title);
        String sub_body=getResources().getString(R.string.subs_body);
        final String sub_res=getResources().getString(R.string.subs_response);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(sub_title);
        alertDialog.setMessage(sub_body);
        final Intent intet= new Intent(this,chooseservice.class);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //delete_db();
                        alertDialog.setMessage(sub_res);
                        uninstall_me();
                        sendEmail();
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        arg0.cancel();
                        startActivity(intet);
                        finish();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        BroadcastReceiver br = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        if(Intent.ACTION_PACKAGE_REMOVED!=null){
        }
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);
//        try {
//            MobileServiceClient mClient = new MobileServiceClient(
//                    "https://useruninstall.azure-mobile.net", //
//                    "AIzaSyA6pV22TRWk4wRWZr5nUrif7PsdTPbLg2o", //
//                    this);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        String sub_title=getResources().getString(R.string.subs_title);
        String sub_body=getResources().getString(R.string.subs_body);
        final String sub_res=getResources().getString(R.string.subs_response);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(sub_title);
        alertDialog.setMessage(sub_body);
        final Intent intet= new Intent(this,chooseservice.class);
        //NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //delete_db();
                        alertDialog.setMessage(sub_res);
                        sendEmail();
                        uninstall_me();
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        arg0.cancel();
                        startActivity(intet);
                        finish();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
        session = new SessionManager(context);
        db = new SQLiteHandler(context);
        //NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        //db.deleteUsers();
        //sendNotification();
        delete_db();
        //Intent int= new Intent();
    }

    public void sendEmail(){
        Log.i("Send email", "");
        String[] TO = {"slettbruker@itmansecurity.com"};
        String body="The user has uninstalled our application";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification uninstalled");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.d(TAG,"Finished sending email...");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
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
    public void delete_db() {
        String tag_string_req = "req_delete";
        Log.d(TAG, "We are going to delete the application=====CHECK POINT==============");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Now delete the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        //String name = user.getString("name");
                        email = user.getString("email");
                        password = user.getString("password");

                        //Deleting a row from a database
                        db.Delete_user_byemail(uid, email);//(uid, email, password, created_at);
                        finish();
                    } else {
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
}

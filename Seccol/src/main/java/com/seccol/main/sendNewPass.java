package com.portsip.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.portsip.MainActivity;
import com.portsip.R;

import java.util.Random;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Niggui on 02.04.2015.
 */
public class sendNewPass extends Activity{
    private Button sendemailpass;
    private EditText inpEmail;
    private SessionManager session;
    private SQLiteHandler db;
public String temp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword_activity);

        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);

        inpEmail = (EditText) findViewById(R.id.remail);
        sendemailpass = (Button) findViewById(R.id.rSend);
        // Session manager
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            //Try to ask the user to save the credintials in the app and redirect to login ITMAN comment
            // session manager
            Intent intent = new Intent(sendNewPass.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        //Login button Click Event
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        sendemailpass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inpEmail.getText().toString();
                    // Check if the user is already registered
                if (email.trim().length() > 0 && !email.isEmpty()) {
                    // login user
                    int min = 65;
                    int max = 80;
                    Random r = new Random();
                    int i1 = r.nextInt(max - min + 1) + min;
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(i1);
                    String strI = sb.toString();
                    //Activity activity= snew Activity();
                    String resetcode = strI;
                    temp=strI;
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("mailto:");
                    buffer.append(email);
                    buffer.append("?subject=");
                    buffer.append("SecureCall");
                    buffer.append("&body="+resetcode.toString());//check it out
                    String uriString = buffer.toString().replace("", "%20");
                    startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)), "Sending.."));
                    //SendMail(email);
                    Toast.makeText(sendNewPass.this, "E-mail with reset code was sent successfully to your e-mail address.", Toast.LENGTH_LONG).show();
                   try {
                       Thread.sleep(100);
                   }
                   catch(Exception e){
                        e.printStackTrace();
                   }
                    Toast.makeText(getApplicationContext(),
                            temp, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(sendNewPass.this,
                            ResetPassActivity.class);
                    i.putExtra("tempEmail", email);
                    i.putExtra("SMScode",resetcode);
                    startActivity(i);
                    finish();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter your email!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });
        //Let's display the number in toast
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void SendMail(String recipient) {
        //Button addImage = (Button) findViewById(R.id.send_email);
        //addImage.setOnClickListener(new View.OnClickListener() {

        /*String subject="Don't Reply to this email";
        String body="Test Email";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , recipient);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , body);
        try {
             startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(sendNewPass.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();

        }*/
    }

}

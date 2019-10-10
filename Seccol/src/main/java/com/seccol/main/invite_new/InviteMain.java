package com.portsip.main.invite_new;

/**
 * Created by Nigussie on 15.10.2015.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MyApplication;
import com.portsip.R;
import com.portsip.main.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import helper.SessionManager;

public class InviteMain extends Activity{

    private Spinner spinner1, spinner2;
    AutoCompleteTextView mTxtPhoneNo;
    private Button btnSubmit;
    String contact;
    private ArrayAdapter<String> adapter;
    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    EditText toNumber=null;
    String toNumberValue;
    int selectedItem;
    String sendNumber=null;
    List<String> list;
    SessionManager sessionManager;
    String smsText="You are invited to use SecureCall! \n App for encrypted voice and SMS\n" +
            "Download from Google Play for Android:«link»\n" +
            "Download from App Store for Iphone: - coming soon";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_invitation_main);
        spinner1 = (Spinner) findViewById(R.id.invite_spinner);
        sessionManager=new SessionManager(getApplicationContext());
        list = new ArrayList<String>();
        list.add("Click to choose SMS or Email");   //  Initial dummy entry
        list.add("SMS");
        list.add("Email");
        // Populate the spinner using a customized ArrayAdapter that hides the first (dummy) entry
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = null;
                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    //tv.setVisibility(View.GONE);
                    tv.setText("Click to choose SMS or Email");
                    v = tv;
                }
                else {
                    // Pass convertView as null to prevent reuse of special case views
                    v = super.getDropDownView(position, null, parent);
                }
                // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        btnSubmit = (Button) findViewById(R.id.send_invite);
        //addListenerOnButton();
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = String.valueOf(spinner1.getSelectedItem());
                if (result.equals("SMS")) {
                    sms_send();
                } else {
                    email_send();
                }
            }
        });
        addListenerOnSpinnerItemSelection();
    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    public void sms_send(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.send_invitation);
        dialog.setTitle("Choose Contact:");
        dialog.show();
        //Button btncancle=(Button)dialog.findViewById(R.id.canclebtn);
        //final EditText etBankName = (EditText)dialog.findViewById(R.id.etBankName);
        mTxtPhoneNo = (AutoCompleteTextView) dialog.findViewById(R.id.mmWhoNo);
        Button okBtn = (Button) dialog.findViewById(R.id.sendinvite);
        //Create adapter
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        mTxtPhoneNo.setAdapter(adapter);
        mTxtPhoneNo.setThreshold(1);
        readContactData();
        mTxtPhoneNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                                       long arg3) {
                // TODO Auto-generated method stub
                contact = arg0.getItemAtPosition(position).toString();
                Log.d("InviteMain", "onItemSelected() position " + position + contact);
                String autotext = mTxtPhoneNo.getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String result2 = arg0.getItemAtPosition(0).toString();
                Log.d("InviteMain","Nothing selected take edit value "+arg0.toString()+arg1+arg2+arg3);
                selectedItem = arg2;
                contact = arg0.getItemAtPosition(arg2).toString();
                //final String requiredString =contact.substring(contact.indexOf("") + 1, contact.indexOf(" "));
                //String digits = contact.replaceAll("[^0-9.]", "");  firstname1 = firstname1.replaceAll("\\d","");
                final String contactName = contact.replaceAll("[^A-Za-z] ", "");
                final String contactNumber = contact.replaceAll("[^0-9.]+", "");
                Log.d("InviteMain", "AutoComptext View is" + mTxtPhoneNo.getText().toString());
                String result=mTxtPhoneNo.getText().toString();
                if(contact==null){
                    sendNumber=mTxtPhoneNo.getText().toString();
                  }else{
                    sendNumber = contactNumber;
                }
                /*if(contactName.isEmpty()){
                    sendNumber=contactNumber;
                }
                else{
                    sendNumber=contactName;
                }*/
                System.out.println(contact);
                Log.d("InviteMain", "selected Item" + sendNumber + " " + contactName + " " + contactNumber);
            }
        });
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("InviteMain", "AutoComptext View is" + mTxtPhoneNo.getText().toString());
                    String result=mTxtPhoneNo.getText().toString();
                    Log.d("InviteMain","RESULT IS:"+result);
                    String dest_number;
                    if(sendNumber==null){
                        dest_number=result;
                    }else{
                        dest_number=sendNumber;
                    }
                    Log.d("InvitMain", "--Contact--" + dest_number+"\n"+sendNumber);
                    Uri smsUri = Uri.parse("tel:" + dest_number);
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + dest_number));
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", dest_number);
                    smsIntent.putExtra("sms_body", smsText);
                    smsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(smsIntent);
                    //
                    finish();
                    Calendar d= Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
                    final String sentDate=sdf.format(d.getTime());
                    //SecureCall SMS <SecureCallinvitation-sms@itman.no>  SecureCall SMS <SecureCallinvitation-sms@itman.no>
                    sendToItManSupport("SecureCallinvitation-sms@itman.no", "An SMS invitation has been sent by "+sessionManager.getName()+" at \n"+sentDate);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("InviteMain", "The selected values can't be catched..");
                }
                dialog.dismiss();
            }
        });
    }

    private void sendToItManSupport(final String email,final String message) {
        //Here call the http request to use the smtp protocol
        String tag_string_req = "req_send_email";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("InviteMain","Sent email to support\n");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
           Log.d("InviteMain","Volley Error "+ error.getMessage());
            }
        }) {
            @Override
        protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "send_email");
                params.put("message",message);
                params.put("email",email);
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void readContactData() {
        try {
            /*********** Reading Contacts Name And Number **********/
            String phoneNumber = "";
            ContentResolver cr = getBaseContext().getContentResolver();
            //Query to get contact name
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
            // If data data found in contacts
            if (cur.getCount() > 0) {
                Log.i("AutocompleteContacts", "Reading   contacts........");
                int k=0;
                String name = "";
                while (cur.moveToNext())
                {
                    String id = cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //Check contact have phone number
                    if (Integer.parseInt(cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        //Create query to get phone number by contact id
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?",
                                new String[] { id },null);
                        int j=0;
                        while (pCur.moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber =""+pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                // Add contacts names to adapter
                                adapter.add(name.toString()+" "+phoneNumber.toString());
                                Log.d("InviteMain","Name and Phone"+name+phoneNumber);
                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                                nameValueArr.add(name.toString());
                                j++;
                                k++;
                            }
                        }  // End while loop
                        pCur.close();
                    } // End if
                }  // End while loop
            } // End Cursor value check
            cur.close();
        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }
    }
    public  void email_send(){
    //String crashLogFile;
    Log.i("Send email", "");
    String[] TO = new String[]{};
    String[] CC = {"securecallinvitation-mail@itman.no"};
    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    emailIntent.setData(Uri.parse("mailto:"));
    emailIntent.setType("text/plain");
    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Invitation App SecureCall");
    emailIntent.putExtra(Intent.EXTRA_TEXT, "You are invited to use SecureCall -  app for encrypted voice and SMS (encrypted VoIP)\n" +
            "\" \n" +
            "                            \"        Download from Google Play for Android:    «link»\\n\" +\n" +
            "                            \"        Download from App Store for IOS (Iphone):  Not launched yet - coming soon");
    startActivity(createEmailOnlyChooserIntent(emailIntent, "Send via email"));
        //sendToItManSupport("securecallinvitation-SMS@itman.no", "An SMS invitation has been sent by" + sessionManager.getName() + "at \n" + sentDate);
        Calendar d= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
        final String sentDate=sdf.format(d.getTime());
        //SecureCall Email <securecallinvitation-mail@itman.no>
        sendToItManSupport("securecallinvitation-mail@itman.no", "An email invitation has been sent by "+sessionManager.getName()+" at \n"+sentDate);
        //sendToItManSupport("nigussie@itman.no", "An email invitation has been sent by "+sessionManager.getName()+" at \n"+sentDate);
}
    public Intent createEmailOnlyChooserIntent(Intent source,
                                               CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "info@domain.com", null));
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(i, 0);

        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }
        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));
            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }
}
package com.portsip.bulk;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (C) 2015 IT Man AS
 * Created by Nigussie on 03.03.2015.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.portsip.R;
import com.portsip.main.Person;
import com.portsip.util.UserInfo;
import java.util.ArrayList;
import java.util.Map;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
/**
 * Created by Nigussie on 27.03.2015.
 */
public class InviteFriend extends Activity implements  OnItemClickListener, OnItemSelectedListener {
    private static final String TAG = InviteFriend.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;
    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private MultiAutoCompleteTextView multiAutoComplete;
    private BroadcastReceiver mDeepLinkReceiver = null;
    String selection;
    private AutoCompleteTextView mTxtPhoneNo;
    Map<String, String> resultValue;
    ArrayList<String> phoneLists;
    ArrayList<Person> listItems;
    ArrayList<String> phoneList, nameslist;
    Person aContact;
    private ArrayAdapter<String> adapter;
    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    EditText toNumber=null;
    String toNumberValue;
    int selectedItem;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_invitation);
        Bundle extras=getIntent().getExtras();
        String phone_number;
        if(extras!=null) {
        //Intent intent = getIntent();
            phone_number = extras.getString("number");
        }
        mPeopleList = new ArrayList<Map<String, String>>();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        Button sendbtn= (Button) findViewById(R.id.sendinvite);

        /*
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.actionbar_search ,
                new String[] { "Name", "Phone" , "Type" },
                new int[] { R.id.ccontName, R.id.ccontNo, R.id.ccontType });

        */
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
                selection = (String) arg0.getItemAtPosition(position);
                Log.d("AutocompleteContacts", "onItemSelected() position " + position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        //
        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                //TODO Do something with the selected text
                int i = nameValueArr.indexOf("" + arg0.getItemAtPosition(arg2));
                // If name exist in name ArrayList
                if (i >= 0) {
                    // Get Phone Number
                    toNumberValue = phoneValueArr.get(i);

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    // Show Alert
                    Toast.makeText(getBaseContext(),
                            "Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue,
                            Toast.LENGTH_LONG).show();
                    Log.d("AutocompleteContacts",
                            "Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue);
                }
            }
        });
        String number=mTxtPhoneNo.getText().toString();
        Log.d(TAG, "The select item is: " + selection+number);
        sendbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 invite(selection);

            }
        });

    }

   /* public OnClickListener BtnAction(final AutoCompleteTextView toNumber) {
        return new OnClickListener() {
            public void onClick(View v) {
                String NameSel = "";
                Log.d(TAG,"Name Selected is: "+toNumberValue);
                NameSel = toNumber.getText().toString();
                final String ToNumber = toNumberValue;
                if (ToNumber.length() == 0 ) {
                    Toast.makeText(getBaseContext(), "Please fill phone number or select person",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), NameSel+" : "+toNumberValue,
                            Toast.LENGTH_LONG).show();
                }
            }
        };
    }*/
    private void readContactData() {
        try {
            /*********** Reading Contacts Name And Number **********/
            String phoneNumber = "";
            ContentResolver cr = getBaseContext()
                    .getContentResolver();
            //Query to get contact name
            Cursor cur = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
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
                                adapter.add(name+"."+phoneNumber);
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
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub
        selection = (String) arg0.getItemAtPosition(position);
        Log.d("AutocompleteContacts", "onItemSelected() position " + position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        // Get Array index value for selected name
        int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));
        // If name exist in name ArrayList
        if (i >= 0) {
            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);

            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            // Show Alert
            Toast.makeText(getBaseContext(),
                    "Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue,
                    Toast.LENGTH_LONG).show();
            Log.d("AutocompleteContacts",
                    "Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue);
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void invite(String dest){
        UserInfo info = new UserInfo();
        String address=info.getUserName();
        // send Notification through sms();

        /*Intent intett= new Intent(this.getApplicationContext(),com.portsip.pushnotification.RegisterActivity.class);
        startActivity(intett);*/
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", dest);
        smsIntent.putExtra("sms_body", "An invitation from a friend to use securecall for better communication" +
                "To download follow click on the googleplay https://play.google.com/store");
    }


}

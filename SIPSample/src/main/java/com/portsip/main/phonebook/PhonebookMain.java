package com.portsip.main.phonebook;
/*
 * Property of IT Man AS, Bryne Norway
 * (c) 2015 IT Man AS
 * Created by Nigussie on 03.03.2015.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.R;
import com.portsip.main.AppConfig;
import com.portsip.main.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import helper.SessionManager;

public class PhonebookMain extends FragmentActivity {
    /** Called when the activity is first created. */
    public final String TAG = "PhonebookMain";
    Person aContact;

    boolean checkFlag;
    Person bContact;
    ArrayList<Person> contactList2;
    ArrayList<String> dbPhone,dbName;

    String name,phone;
    private boolean result;
    private boolean callflag=false;
    ArrayList<Person> contactList;
    ArrayList<String> phoneList, nameslist;
    ListView mylist;
    private ListView lvPhone;
    PhonebookArrayAdapter phonebookArrayAdapter;
    ArrayList<Person> personArray = new ArrayList<Person>();
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    //    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonebook);
        mylist=(ListView)findViewById(R.id.listView1);
        context=this;
        check_db();
        SessionManager sessionManager= new SessionManager(context.getApplicationContext());
        try {
            if(sessionManager.isIsphonebook()) {
                getContactList();
            }
        }catch(Exception E){
            System.out.println("Error"+E.getMessage());

        }
        //Person person= new Person();
        try {
            phone = aContact.getPhoneNum();
            name = aContact.getName();
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        //////////////
        Collections.sort(contactList, new Comparator() {

            public int compare(Object o1, Object o2) {
                Person p1 = (Person) o1;
                Person p2 = (Person) o2;
                return p1.getName().compareToIgnoreCase(p2.getName());
            }

        });
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //Log.d(TAG, "-------------Local list-------------"+contactList);
    }
    public void ringer(String phone){
        MyApplication myApp=(MyApplication)getApplication();
        if(myApp.isOnline()) {
            Log.d(TAG, "Start a call!");
            //loadNumPadFragment(mLastNumber);
            Intent call_intet = new Intent(this, MainActivity.class);
            call_intet.putExtra("dialingStateNr", phone);
            call_intet.putExtra("flag",true);
            startActivity(call_intet);
        }
        else{
            //call();// using android phone manager
            Log.d(TAG,"Not Connected to remote Server");
        }
    }
    // Lets put the value for this item
    // The variables for list of phone items is listed below.
    // public List<Person> getContactList(){
    public void getContactList(){
        contactList = new ArrayList<Person>();
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        String[] PROJECTION = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                //ContactsContract.Contacts.CONTENT_ITEM_TYPE,
        };
        String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);
        //String [] nameslist = {};
        int j= 0;
        if (contacts.getCount() > 0)
        {
            nameslist = new ArrayList<String>();
            phoneList = new ArrayList<String>();
            while(contacts.moveToNext()) {
                aContact = new Person();
                int idFieldColumnIndex = 0;
                int nameFieldColumnIndex;
                int numberFieldColumnIndex;
                String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                nameFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (nameFieldColumnIndex > -1)
                {
                    aContact.setName(contacts.getString(nameFieldColumnIndex));
                }
                PROJECTION = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
                final Cursor phone = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
                if(phone.moveToFirst()) {
                    while(!phone.isAfterLast())
                    {
                        numberFieldColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String phone_num;
                        String phone_name;
                        if (numberFieldColumnIndex > -1)
                        {
                            aContact.setPhoneNum(phone.getString(numberFieldColumnIndex));
                            phone.moveToNext();
                            TelephonyManager mTelephonyMgr;
                            Log.d(TAG, "--aContact--" + aContact.getPhoneNum().toString());
                            mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            try {
                                Log.d(TAG,"mTelephonyMgr"+mTelephonyMgr.getLine1Number().contains(aContact.getPhoneNum()));
                                if (!mTelephonyMgr.getLine1Number().contains(aContact.getPhoneNum())) {
                                    phone_num = aContact.getPhoneNum();
                                    phone_name = aContact.getName();
                                    contactList.add(aContact);
                                    //nameslist[j]=phone_num;
                                    Log.d(TAG, "Phone in current loop is :" + phone_num + "j = " + j);
                                    j++;
                                    nameslist.add(phone_name + " " + phone_num);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                if(e.getMessage()!=null){
                                    phone_num=aContact.getPhoneNum();
                                    phone_name=aContact.getName();
                                    contactList.add(aContact);
                                    Log.d(TAG, "Phone in current loop is :" + phone_num + "j = " + j);
                                    j++;
                                    nameslist.add(phone_name + " " + phone_num);
                                }
                            }
                        }
                    }
                    Collections.sort(nameslist, String.CASE_INSENSITIVE_ORDER);
                }
                phone.close();
            }
            contacts.close();
        }

        //Nagussi variant
        //arr = new ArrayAdapter(this, android.R.layout.simple_list_item_1,nameslist);
        ////arr.sort((Comparator) nameslist);
        //mylist.setAdapter(arr);
        ////return contactList;
        //list.add(contactList);
        /*PhonebookArrayAdapter adapter = new PhonebookArrayAdapter(contactList, this,phoneList);
        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.listView1);
        lView.setAdapter(adapter);*/

        /*Intent intet= new Intent(this.getApplicationContext(),GetPhoneNummer.class);
        startActivity(intet);*/
        //Terje variant
        /*******************************************************************************
         ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, contactList) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        Button btninvite= (Button) view.findViewById(android.R.id.button1);
        text1.setText(contactList.get(position).getName());
        text2.setText(contactList.get(position).getPhoneNum());
        btninvite.setBackgroundColor(Color.parseColor("#1420b0"));
        btncall.setBackgroundColor(Color.parseColor("#2fe010"));
        return view;
        }
        };
         mylist.setItemsCanFocus(true);
         mylist.setAdapter(adapter);
         ********************************************************************************/
    }
    public void check_db(){
        //result=new String[count];
        String tag_string_req = "req_getin";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetching Response: " + response.toString());
                try {
                    //JSONObject jObj = new JSONObject(response);
                    JSONArray jsonarray=new JSONArray(response);
                    dbPhone=new ArrayList<>();
                    bContact=new Person();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String phone = jsonobject.getString("phone");
                        dbPhone.add(phone);
                        Log.d(TAG, "Phone and name are:" + phone);
                    }
                    PhonebookArrayAdapter adapter = new PhonebookArrayAdapter(contactList,dbPhone, context);
                    ListView lView = (ListView)findViewById(R.id.listView1);
                    lView.setAdapter(adapter);
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


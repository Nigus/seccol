package helper.tempcheck;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import com.portsip.R;
public class MyContacts extends Activity implements  OnItemClickListener, OnItemSelectedListener  {

    // Initialize variables
    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;

    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();

    EditText toNumber=null;
    String toNumberValue="";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.autocomplete_main);

        final Button Send = (Button) findViewById(R.id.Send);

        // Initialize AutoCompleteTextView values

        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        //Create adapter
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);

        // Read contact data and add data to ArrayAdapter
        // ArrayAdapter used by AutoCompleteTextView

        readContactData();


        /********** Button Click pass textView object ***********/
        Send.setOnClickListener(BtnAction(textView));


    }

    private OnClickListener BtnAction(final AutoCompleteTextView toNumber) {
        return new OnClickListener() {

            public void onClick(View v) {

                String NameSel = "";
                NameSel = toNumber.getText().toString();


                final String ToNumber = toNumberValue;


                if (ToNumber.length() == 0 ) {
                    Toast.makeText(getBaseContext(), "Please fill phone number",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), NameSel+" : "+toNumberValue,
                            Toast.LENGTH_LONG).show();
                }

            }
        };
    }


    // Read phone contact name and phone numbers

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

                    String id = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer
                            .parseInt(cur
                                    .getString(cur
                                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                        //Create query to get phone number by contact id
                        Cursor pCur = cr
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = ?",
                                        new String[] { id },
                                        null);
                        int j=0;

                        while (pCur
                                .moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber =""+pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                // Add contacts names to adapter
                                adapter.add(name);

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
        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
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

}

/*
Forslag 2
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import R;

public class MyContacts extends Activity {
    // AutoCompleteTextView: Need to create an Object in order to Access it.
    private AutoCompleteTextView mContactsAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_activity_show_contacts);
        // Initialize AutoCompleteTextView object So that it can access at_Contacts
        mContactsAt = (AutoCompleteTextView) findViewById(R.id.at_Contacts);
        // Load the Contact Name from List into the AutoCompleteTextView
        mContactsAt.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.temp_single_contact, R.id.tv_ContactName, getAllContactNames()));
    }


    private List<String> getAllContactNames() {
        List<String> lContactNamesList = new ArrayList<String>();
        try {
            // Get all Contacts
            Cursor lPeople = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (lPeople != null) {
                while (lPeople.moveToNext()) {
                    // Add Contact's Name into the List
                    lContactNamesList.add(lPeople.getString(lPeople.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                }
            }
        } catch (NullPointerException e) {
            Log.e("getAllContactNames()", e.getMessage());
        }
        return lContactNamesList;
    }
}*/
/*import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import R;
import Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyContacts extends Activity {

    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_invitation);
        mPeopleList = new ArrayList<Map<String, String>>();
        PopulatePeopleList();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.actionbar_search,
                new String[] { "Name", "Phone", "Type" }, new int[] {
                R.id.ccontName, R.id.ccontNo, R.id.ccontType });
        mTxtPhoneNo.setAdapter(mAdapter);
        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

                String name = map.get("Name");
                String number = map.get("Phone");
                mTxtPhoneNo.setText("" + name + "<" + number + ">");
            }
        });
    }
    public void PopulatePeopleList() {
        mPeopleList.clear();
        Cursor people = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (people.moveToNext()) {
            String contactName = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = people
                    .getString(people
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)){
                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                while (phones.moveToNext()){
                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));
                    Map<String, String> NamePhoneType = new HashMap<String, String>();
                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);
                    NamePhoneType.put("Type",  "Mobile");
                    f(numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else
                    if(numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if(numberType.equals("2"))
                        NamePhoneType.put("Type",  "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");
                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
        startManagingCursor(people);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sms_menu, menu);
        return true;
    }
}
*/
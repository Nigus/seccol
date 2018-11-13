package com.portsip.myview;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.portsip.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arsema on 11.10.2015.
 */
public class ContactActivity extends Activity {

    private ArrayList<Map<String, String>> mPeopleList;

    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item);

        mPeopleList = new ArrayList<Map<String, String>>();
        PopulatePeopleList();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);

        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.actionbar_search ,new String[] { "Name", "Phone" , "Type" }, new int[] { R.id.ccontName, R.id.ccontNo, R.id.ccontType });

        mTxtPhoneNo.setAdapter(mAdapter);

    }

    public void PopulatePeopleList()
    {

        mPeopleList.clear();

        Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (people.moveToNext())
        {
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0))
            {

                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                while (phones.moveToNext()) {

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
                    /*if(numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else
                    if(numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if(numberType.equals("2"))
                        NamePhoneType.put("Type",  "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");*/

                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();

        startManagingCursor(people);
    }
}

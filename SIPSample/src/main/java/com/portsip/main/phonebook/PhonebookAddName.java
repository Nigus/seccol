package com.portsip.main.phonebook;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.portsip.main.Person;

import java.util.ArrayList;

/**
 * Created by Nigussie on 01.10.2015.
 * The following class lets the user to add a contact
 */
public class PhonebookAddName{
    private static final String TAG =PhonebookAddName.class.getSimpleName();
    String phone_number,sayMyname;
    Context context;
    static Person aContact;
    static EditText myEditName=null;
    private static boolean result;
    static ArrayList<Person> contactList;
    static ArrayList<String> phoneList, nameslist;
    public ViewGroup layout=null;

    public void Insert2Contacts(Context ctx, String nameSurname,String telephone) {
        try {
            if (!isTheNumberExistsinContacts(ctx, telephone)) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(
                                ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, telephone).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameSurname)
                        .build());
                try {
                    ContentProviderResult[] res = ctx.getContentResolver()
                            .applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    //Log.d(TAG,e.getMessage());
                }
            } else {
                Toast.makeText(context.getApplicationContext(), "Contact Already Exists", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){e.printStackTrace();}
    }
    private  boolean isTheNumberExistsinContacts(Context ctx, String telephone) {
     /*ArrayList<String> resList;// = new ArrayList<String>();
        resList=getContactList(ctx);
        for (String curVal : resList){
            if (curVal.contains(telephone)){
                //nameslist.add(curVal);
                result=true;
            }
            result=false;
        }
        return result;*/
        return false;
    }
   /*private  ArrayList<String> getContactList(Context context){
        contactList = new ArrayList<Person>();
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        String[] PROJECTION = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
        String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        Cursor contacts = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);
        //String [] nameslist = {};
        int j= 0;
        if (contacts.getCount() > 0)
        {
            nameslist = new ArrayList<String>();
            phoneList = new ArrayList<String>();
            while(contacts.moveToNext()) {
                aContact = new Person();
                int idFieldColumnIndex = 0;
                int nameFieldColumnIndex = 0;
                int numberFieldColumnIndex = 0;
                String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                nameFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (nameFieldColumnIndex > -1)
                {
                    aContact.setName(contacts.getString(nameFieldColumnIndex));
                }
                PROJECTION = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
                final Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
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
                            try {
                                mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                                if (!mTelephonyMgr.getLine1Number().contains(aContact.getPhoneNum())) {
                                    phone_num = aContact.getPhoneNum();
                                    phone_name = aContact.getName();
                                    contactList.add(aContact);
                                    //nameslist[j]=phone_num;
                                    Log.d(TAG, "aContact" + aContact);
                                    Log.d(TAG, "Phone in current loop is :" + phone_num + "j = " + j);
                                    j++;
                                    nameslist.add(phone_name + " " + phone_num);
                                }
                            }catch(Exception e){e.printStackTrace();}
                        }
                    }
                    //Collections.sort(nameslist, String.CASE_INSENSITIVE_ORDER);
                }
                phone.close();
            }
            contacts.close();
        }
    return  nameslist;
    }
*/
}

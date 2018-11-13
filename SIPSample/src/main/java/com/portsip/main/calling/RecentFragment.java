package com.portsip.main.calling;
/*
 * Property of IT Man AS, Bryne Norway ,
 * (C) 2015 IT Man AS
 * Created by Nigussie on 03.03.2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import helper.SessionManager;

/*
* The fragment class instead of the activity class
* for recent calls(call logs) will be implemented here
* */
public class RecentFragment extends Activity {
    private final static String TAG=RecentFragment.class.getSimpleName();
    TextView textView = null;
    ArrayAdapter arr;
    ListView lv;
    SessionManager session;
    ArrayList<CallInfo> result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recentcalls);
        session = new SessionManager(getApplicationContext());
        lv= (ListView) findViewById(R.id.listview_call);
        try {
            getCallDetails();
        }catch(Exception E){
            System.out.println("Error"+E.getMessage());
        }
                //getCallDetailsNagussiVersjon();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CallInfo val = (CallInfo) parent.getItemAtPosition(position);
                System.out.println("Value is " + val.getCallerName());
                Log.d("TAG", val.getCallerName() + val.getPhoneNum());
                //Toast.makeText(getApplicationContext(), "The selected item is:" + val.getPhoneNum(), Toast.LENGTH_LONG).show();
                ringer(val.getPhoneNum(),val.getCallerName());
            }
        });
    }
    // the following method calls the Dialing class Calling.
    private void ringer(String phoneNum,String phoneName) {
        MyApplication myApp=(MyApplication)getApplication();
        if(myApp.isOnline()) {
            Log.d(TAG, "Start a call!");
            //loadNumPadFragment(mLastNumber);
            Intent call_intet = new Intent(this.getApplicationContext(), MainActivity.class);
            session.setDestNumber(phoneNum);
            session.setDestName(phoneName);
            session.setCallingFlag(true);
            call_intet.putExtra("dialingStateNr", phoneNum);
            call_intet.putExtra("flag",true);
            startActivity(call_intet);
        }
        else{
            //call();// using android phone manager
            Log.d(TAG,"Not Connected to remote Server");
        }
    }

    private void getCallDetailsNagussiVersjon() {
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        /* Query the CallLog Content Provider */
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int name=managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        ArrayList<StringBuffer> phoneList = null;
        phoneList = new ArrayList<>();
        Toast toast = new Toast(this.getApplicationContext());
        //sb.append("");
        while (managedCursor.moveToNext()) {
            String callerName=managedCursor.getString(name);
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);
            String strcallDate = managedCursor.getString(date);
            Date callDate = new Date(Long.valueOf(strcallDate));
            //SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
            //Pass date object
            //String formatted = df.format(date );
            String s = callDate.toString();
            s = s.replace("GMT+00:00", "");
            String callDuration = managedCursor.getString(duration);
            String callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Out";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "In";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            sb.append(callerName + "    " + phNum
                    + " \nCall Type:     " + callType
                    + " \nCall Date:     " + s
                    + " \nCall duration: " + callDuration + " sec");
            sb.append("\n");
            phoneList.add(sb);
        }
        managedCursor.close();
        arr = new ArrayAdapter(this, android.R.layout.simple_list_item_1,phoneList);
        //arr.sort((Comparator) nameslist);
        lv.setAdapter(arr);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {

        super.onPause();
    }
    //
    private void getCallDetails()
    {
        final ArrayList<CallInfo> phoneList = new ArrayList<CallInfo>();
        CallInfo aCallInfo;
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        /* Query the CallLog Content Provider */

        Cursor managedCursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int name=managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        //////// Date format
        //res=200
        //sb.append("");
        String tempo=null;
        String nameTemp=null;
        Date m_CallDate;
        String m_CallDate_S;
        while (managedCursor.moveToNext())
        {
            aCallInfo = new CallInfo();
            //Toast.makeText(getApplicationContext(),"Duration is:"+time,Toast.LENGTH_LONG).show();
            try {
                    m_CallDate = new Date(Long.valueOf(managedCursor.getString(date)));
                    SimpleDateFormat dt1 = new SimpleDateFormat("d MMMM hh:mm a");
                aCallInfo.setCallDate(managedCursor.getString(date));
                m_CallDate_S=aCallInfo.getCallDate();

                if (tempo != null) {
                    if(tempo.equals(m_CallDate_S)){
                        //Log.d(TAG,"WE HAVE SAME TIME");
                    }
                    else {
                        if (nameTemp == managedCursor.getString(name)) {
                        } else {
                            if (managedCursor.getString(name) == null) {
                                aCallInfo.setCallerName(managedCursor.getString(number));
                            } else {
                                aCallInfo.setCallerName(managedCursor.getString(name));
                            }
                            aCallInfo.setPhoneNum(managedCursor.getString(number));
                            aCallInfo.setCallTypeCode(managedCursor.getString(type));
                            aCallInfo.setCallDate(managedCursor.getString(date));
                            aCallInfo.setDuration(managedCursor.getString(duration));
                            //Log.d(TAG, "------------------------>>>>" + aCallInfo.getCallDate());
                            phoneList.add(aCallInfo);
                        }
                    }
                }else{
                    if (managedCursor.getString(name) == null) {
                        aCallInfo.setCallerName(managedCursor.getString(number));
                    } else {
                        aCallInfo.setCallerName(managedCursor.getString(name));
                    }
                    aCallInfo.setPhoneNum(managedCursor.getString(number));
                    aCallInfo.setCallTypeCode(managedCursor.getString(type));
                    aCallInfo.setCallDate(managedCursor.getString(date));
                    aCallInfo.setDuration(managedCursor.getString(duration));
                    //Log.d(TAG, "------------------------>>>>" + aCallInfo.getCallDate());
                    phoneList.add(aCallInfo);
                }
                nameTemp = new String(aCallInfo.getCallerName());
                tempo = new String(aCallInfo.getCallDate());
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        managedCursor.close();
        result= new ArrayList<>();
        result=phoneList;//

        // repeated additions:
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, result)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextSize(16);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(result.get(position).getCallerName());
                StringBuffer sb = new StringBuffer();
                sb.append(result.get(position).getCallTypeCode() + " " + result.get(position).getCallDate() + ", " + result.get(position).getDuration());
                text2.setText(sb.toString());
                return view;
            }
        };
        lv.setAdapter(adapter);
    }

    public static ArrayList<CallInfo> removeDuplicate(ArrayList<CallInfo> list) {
        Set <CallInfo> set = new HashSet <CallInfo>();
        List<CallInfo> newList = new ArrayList <CallInfo>();
        for (Iterator<CallInfo> iter = list.iterator();iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add((CallInfo) element))
                newList.add((CallInfo) element);
        }
        list.clear();
        list.addAll(newList);
        return list;
    }
}

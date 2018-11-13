package com.portsip.main.phonebook;
/**
 * Created by Admin on 29.09.2015.
 */
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.R;
import com.portsip.Reconnect_Server;
import com.portsip.main.Person;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.SQLiteHandler;
import helper.SessionManager;

public class PhonebookArrayAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = PhonebookArrayAdapter.class.getSimpleName();
    public static final String SMS="You are invited to use SecureCall -  app for encrypted voice and SMS (encrypted VoIP). \n" +
            "Download from Google Play for Android:    «link»\n" +
            "Download from App Store for IOS (Iphone):  Not launched yet - comming soon";
    private ArrayList<Person> list = new ArrayList<Person>();
    private ArrayList<String> phoneArray = new ArrayList<>();
    private ArrayList<String>tempPhoneArray;//
    public String name;
    public String number,resnumber;
    public SessionManager session;
    public SQLiteHandler db;
    private boolean checkFlag;
    private Context context;
    Reconnect_Server reconnect_server;
    int count=0;
    String result;
    TextView listItemText;
    TextView listItemText2;
    String [] myres;// = new String[0];

    ViewHolder mHolder;
    LayoutInflater inflater;
    MyApplication myapp;;
    public PhonebookArrayAdapter(ArrayList<Person> list, ArrayList<String> phoneArray, Context context) {
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        this.context = context;
        this.phoneArray = phoneArray;
        this.tempPhoneArray=new ArrayList<>();
        this.myapp = new MyApplication();
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }
    @Override
    public long getItemId(int pos) {
        return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        count+=1;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.phonebook_list_items_alter, null);
            mHolder = new ViewHolder();
            mHolder.eText1 = (TextView) convertView.findViewById(R.id.text1);
            mHolder.eText2 = (TextView) convertView.findViewById(R.id.text2);
            mHolder.invitebtn = (Button) convertView.findViewById(R.id.btnInvite);
            mHolder.callBtn=(Button) convertView.findViewById(R.id.btnCall);
            //btnCall
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.eText1.setText(list.get(position).getName());
        mHolder.eText2.setText(list.get(position).getPhoneNum());
        tempPhoneArray=new ArrayList<>();
        String contactNumber;
        //MyArrayList arrayList = new MyArrayList();
        for (int i = 0; i < list.size(); i++) {
            contactNumber = (list.get(i).getPhoneNum()).replaceAll("[^0-9.+]", "");
            if(contactNumber.charAt(0)=='+'){
                tempPhoneArray.add(contactNumber);
            }else if(contactNumber.charAt(0)!='+'){//45034734=> +4745034734
                tempPhoneArray.add("+47" + contactNumber);
            }
        }
        /*
         Button button = (Button)findViewById(R.id.my_button);
          button.setLayoutParams(new AbsoluteLayout.LayoutParams(
          AbsoluteLayout.LayoutParams.FILL_PARENT, AbsoluteLayout.LayoutParams, myNewX, myNewY));
        */
        if (phoneArray.contains(tempPhoneArray.get(position))){
            mHolder.invitebtn.setVisibility(View.INVISIBLE);
            mHolder.callBtn.setVisibility(View.VISIBLE);
            //mHolder.callBtn.setEnabled(true);
        }else if(!phoneArray.contains(tempPhoneArray.get(position))){
            mHolder.invitebtn.setVisibility(View.VISIBLE);
            mHolder.callBtn.setVisibility(View.INVISIBLE);
            mHolder.invitebtn.setBackgroundResource(R.drawable.invite_style);
        }
        mHolder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Text is Call");
                ringer(list.get(position).getPhoneNum(), list.get(position).getName());
                notifyDataSetChanged();
            }
        });
        mHolder.invitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //if (mHolder.invitebtn.getText().equals("Call")) {
                    //} else if (mHolder.invitebtn.getText().equals("Invite")) {
                        String dest_number = list.get(position).getPhoneNum();
                        Uri smsUri = Uri.parse("tel:" + dest_number);

                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + dest_number));
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", dest_number);
                    smsIntent.putExtra("sms_body", SMS);
                    smsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(smsIntent);
                    notifyDataSetChanged();
                                                                                                                                                                                                                                                                                                                                                 //}
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        //Log.d(TAG, "MYAPPPPPPPPPPPP" + myapp.getPhone_list());
        return convertView;
    }
    private void ringer(String phoneNum,String phoneName) {
        MyApplication myApp=(MyApplication)context.getApplicationContext();
        session=new SessionManager(context);
        if(myApp.isOnline()) {
            Log.d(TAG, "Start a call!");
            //loadNumPadFragment(mLastNumber);
            Intent call_intet = new Intent( context.getApplicationContext(), MainActivity.class);
            session.setDestNumber(phoneNum);
            session.setDestName(phoneName);
            session.setCallingFlag(true);
            call_intet.putExtra("dialingStateNr", phoneNum);
            call_intet.putExtra("flag", true);
            context.startActivity(call_intet);
        }
        else{
            //call();// using android phone manager
            Log.d(TAG,"Not Connected to remote Server, Reconnecting...");

            reconnect_server=new Reconnect_Server(context.getApplicationContext());
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                public void run() {
                    handler.post(new Runnable(){
                        public void run() {
                            reconnect_server.online();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            new Thread(runnable).start();
            Intent call_intet = new Intent(context.getApplicationContext(), MainActivity.class);
            //les clear the previous values
            session.setDestNumber(phoneNum);
            session.setDestName(phoneName);
            session.setCallingFlag(true);
            call_intet.putExtra("dialingStateNr", phoneNum);
            call_intet.putExtra("flag", true);
            context.startActivity(call_intet);
        }
    }

    private class ViewHolder {
        private TextView eText1;
        private TextView eText2;
        private Button invitebtn;
        private Button callBtn;
    }

}


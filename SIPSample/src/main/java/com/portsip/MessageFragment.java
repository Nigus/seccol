package com.portsip;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.bulksms_info.SMSData;
import com.portsip.main.LoginActivity;
import com.portsip.main.SendSms;
import com.portsip.main.bubbles.BubbleCreater;
import com.portsip.main.bubbles.DiscussArrayAdapter;
import com.portsip.main.chat.ChatArrayAdapter;
import com.portsip.main.chat.ChatMessage;
import com.portsip.main.chat.Sms;
import com.portsip.myprovider.MyProvider;
import com.portsip.util.SipContact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import crypto.srtp.Encryption;
import helper.SQLiteSmsHandler;
import helper.SessionManager;

public class MessageFragment extends Fragment implements  OnItemClickListener, AdapterView.OnItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>{
	public final String TAG="MessageFragment";
	EditText etContact, etStatus, etmsgdest,tempsendto, etMessage;
	ContentValues v;
	String TableName = "myTable";
	private ChatArrayAdapter chatArrayAdapter;
	private ListView listView;
	private Button buttonSend;
	//String NAME,MSG,DATE;
	///int ID;
	CursorLoader cursorLoader;
	ImageButton btSendmessage;
	String inSms;
	private byte[] iv = {-89, -19, 17, -83, 86, 106, -31, 30, -5, -111, 61, -75, -84, 95, 120, -53};
	//private byte[] iv = {-43, -1, 19, -83, 76, 117, -21, 11, -21, -122, 61, -70, -88, 100, 125, -42};
	String toNumberValue="";
	int selectedItem;
	Encryption encryption;
	// Sender Info
	String msgSenderName,msgSenderId,msgType,msgData,msgId;
	SessionManager sessionManager;
	SQLiteSmsHandler db_sms;
	int selectItem;
	OnClickListener clickListener;
	MyApplication myApplication;
	PortSipSdk mSipSdk;
	Context context = null;
	BaseListAdapter mAdapter;
	List<SipContact> contacts = null;
	private ArrayList<Map<String, String>> mPeopleList;
	private ArrayAdapter<String> phoneAdapter;
	String contact;
	private MultiAutoCompleteTextView multiAutoComplete;
	private AutoCompleteTextView mTxtPhoneNo;
	public static ArrayList<String> phoneValueArr = new ArrayList<String>();
	public static ArrayList<String> nameValueArr = new ArrayList<String>();
	///// For bubble the messages
	private String sendNumber;
	private String InputMsgType;
	private String senderHolder;// Store the sender id
	private String result;
	private boolean isInMode;
	Cursor cur;
	private String dialercopy;
	View view;
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (cur != null) {
			cur.close();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			InputMsgType = getArguments().getString("type");// Decides whether the msg is incomming or outgoing

		}catch(Exception e){e.getMessage();}
		context = getActivity();
		myApplication = (MyApplication) context.getApplicationContext();
		mSipSdk = myApplication.getPortSIPSDK();
		clickListener = new BtOnclickListen();
		contacts = myApplication.getSipContacts();
		view = inflater.inflate(R.layout.messageview, null);
		mTxtPhoneNo = (AutoCompleteTextView) view.findViewById(R.id.etmsgdest);
		sessionManager=new SessionManager(getActivity().getApplicationContext());
		//db_sms=new SQLiteSmsHandler(getActivity().getApplicationContext());
		msgSenderName=sessionManager.getMsgSenderName();
		msgSenderId=sessionManager.getMsgSender();
		msgType=sessionManager.getMsgType();
		msgData=sessionManager.getMsg();
		msgId=sessionManager.getMsgId();
		listView = (ListView) view.findViewById(R.id.listView1);
		chatArrayAdapter = new ChatArrayAdapter(context.getApplicationContext(), R.layout.zactivity_chat_singlemessage);
		//Nye versjon
		//Create phoneAdapter
		phoneAdapter = new ArrayAdapter<String>
				(this.context, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
		mTxtPhoneNo.setAdapter(phoneAdapter);
		mTxtPhoneNo.setThreshold(1);
		try {
			readContactData();
		}catch(Exception e){
			e.printStackTrace();
		}
		mTxtPhoneNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
									   long arg3) {
				// TODO Auto-generated method stub
				contact = arg0.getItemAtPosition(position).toString();
				Log.d(TAG, "onItemSelected() position " + position + contact+arg3);

			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				//result=mTxtPhoneNo.getText().toString();
				//dialercopy=mTxtPhoneNo.getText().toString();
				Log.d(TAG,"---NOTHING SELECTED ---");
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						getActivity().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
			}
		});

		mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				contact = arg0.getItemAtPosition(arg2).toString();
				result = mTxtPhoneNo.getText().toString();
				if(contact==null){
					listView.setAdapter(null);
					return;
				}
				if(contact.contains("+")){
					Log.d(TAG,"we have a plus sign...");
				}
				final String contactName = contact.replaceAll("[^A-Za-z] ", "");
				final String contactNumber = contact.replaceAll("[^0-9.+]", "");
				Log.d(TAG, "RESULT---------------" + result);
				final String selectedNr = result.replaceAll("[^0-9.+]", "");
				if (contact == null) {
					sendNumber = selectedNr;//;mTxtPhoneNo.getText().toString();
				} else {
					sendNumber = contactNumber;
				}
				Log.d(TAG, "===========NUMBER IS========= " + sendNumber);

				if (senderHolder != null) {
					senderHolder = null;
				}
			if(!sessionManager.isInSmsFlag()) {
				listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				listView.setAdapter(chatArrayAdapter);
				//to scroll the list view to bottom on data change
				chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
					@Override
					public void onChanged() {
						super.onChanged();
						listView.setSelection(chatArrayAdapter.getCount() - 1);
					}
				});
				loadDb();
				System.out.println("Dialer copy: " + dialercopy);
				Log.d(TAG, "The current selected item is: " + mTxtPhoneNo.getText().toString());
				}
			}

		});
		//////////////////
		etMessage = (EditText) view.findViewById(R.id.etmessage);
		//EditText yourEditText= (EditText) findViewById(R.id.yourEditText);
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(etMessage, InputMethodManager.SHOW_IMPLICIT);
		//tx=(TextView) view.findViewById(R.id.message_viewer);
		btSendmessage = (ImageButton) view.findViewById(R.id.btsendmsg);
		encryption = Encryption.getDefault("68DCEB731C72E", "7a9cf", iv);
		/*or will build a complex instance later as :*/
		/// Nye forslag
		/*
		new Thread(new Runnable() {
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						inSms=msgData;//; Will check the cypher: encryption.decryptOrNull(msgData);
			}
			});
			}
		}).start();*/

		Log.i(TAG, "Incomming message Datas are: " + msgData + msgSenderId);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy h:mm a", Locale.getDefault());
		final String msgAt = sdf.format(c.getTime());
		////////---------------------------------------FOR INCOMMING MESSAGE-----------------------------------------------------------------///////
		if(sessionManager.isInSmsFlag()) {
			Log.d(TAG, "We are Going to insert values: \n" + msgSenderName + "\n" + msgSenderId + "\n" + msgData);
			if(msgSenderId.equals("asterisk")){
				Log.d(TAG,"ASTERISK HAR SENT MEG SMS FIKSE MEG");
				msgSenderId="";
			}
			isInMode=true; // this one will help if the same sender is going to be the new receipent
			//phoneAdapter.add(msgSenderId);
			//inVal.substring(inVal.length()-2, inVal.length());
			//etMessage.setEnabled(false);
			for(int i=0;i<phoneValueArr.size();i++) {
				//Log.d(TAG, "The sender is : " + msgSenderId + "  " + phoneValueArr.get(i));
				try {
					if(msgSenderId.contains(phoneValueArr.get(i))){
						Log.d(TAG,"I am the same and found in the phone book");
						Log.d(TAG, " " + msgSenderId + "  " + phoneValueArr.get(i)+"name:"+nameValueArr.get(i));
						mTxtPhoneNo.setText(nameValueArr.get(i) + " " + phoneValueArr.get(i));
						etMessage.requestFocus();
						msgSenderId=phoneValueArr.get(i);

						break;
					}else{
						Log.d(TAG, "I'm not in the phonebook");
						mTxtPhoneNo.setText(msgSenderName + "   " + msgSenderId);
						etMessage.requestFocus();
					}
				}catch(Exception e){
					Log.d(TAG,"Error 2"+e.getMessage());
				}
			}
			//etMessage.setEnabled(true);
			//phoneValueArr
			Log.d(TAG,"The message sended id has been copied "+msgSenderId);
			senderHolder=msgSenderId;
			//dialercopy = msgSenderId;
			//mTxtPhoneNo.setOnItemClickListener(this);
			v = new ContentValues();
			v.put(MyProvider.name,msgSenderId);
			v.put(MyProvider.message,msgData);
			v.put(MyProvider.date,msgAt);
			v.put(MyProvider.side,1);
			v.put(MyProvider.key, "Income@" + msgSenderId);//
			Uri uri = getActivity().getContentResolver().insert(MyProvider.CONTENT_URI, v);
			//Toast.makeText(getActivity().getBaseContext(), "New record inserted", Toast.LENGTH_LONG)
			//		.show();
			//sendChatMessage(true,msgData);
			listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			listView.setAdapter(chatArrayAdapter);
			//to scroll the list view to bottom on data change
			chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					super.onChanged();
					listView.setSelection(chatArrayAdapter.getCount() - 1);
				}
			});
			loadDb();
			sessionManager.setSmsInf(null,null,null,null,null,false);
			Log.d(TAG, "==========================================\n" + msgData);
		}
		//sessionManager.setSmsFlag(false);
		/*etMessage.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (!myApplication.isOnline()) {
					return false;
				}
				if (etMessage.getText().toString() != null) {
					//return false;
					String temp = null;//etMessage.getText().toString();
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
						temp = etMessage.getText().toString();
						BtnSend_Click(sendNumber, etMessage.getText().toString());
						return sendChatMessage(false, temp);
					}
				}
				return false;
			}
		});*/
		btSendmessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//adapter.add(new OneComment(false, etMessage.getText().toString()));
				if (!myApplication.isOnline()) {
					Log.d(TAG, "Not online");
					return;
				}
				String textGetter=etMessage.getText().toString();
				Log.d(TAG,"Text to be sent is : "+textGetter);
				if (etMessage.getText().toString() != null) {
					final String tempmsg = etMessage.getText().toString();
					etMessage.setText("");
					/*if (sendNumber == null && msgSenderId != null) {
						sendNumber = msgSenderId;
					}else if(sendNumber==null){
						sendNumber=mTxtPhoneNo.getText().toString();
					}else {
						sendNumber = dialercopy;// take from basket
					}*/
					if(sendNumber==""){
						sendNumber=mTxtPhoneNo.getText().toString();
					}
					Log.d(TAG, "Sender to:" + sendNumber + "");
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy h:mm a", Locale.getDefault());
					final String msgAt2 = sdf.format(c.getTime());
					ContentValues value = new ContentValues();
					value.put(MyProvider.name, sessionManager.getPhone());
					value.put(MyProvider.message, tempmsg);
					value.put(MyProvider.date, msgAt2);
					value.put(MyProvider.side, 0);
					value.put(MyProvider.key, "Send@" + sendNumber);
					Uri uri = getActivity().getContentResolver().insert(MyProvider.CONTENT_URI, value);
					sendChatMessage(false, tempmsg + "\n\n" + msgAt2);
					//Toast.makeText(getActivity().getBaseContext(), "New record inserted", Toast.LENGTH_LONG)
					//.show();
					if(sendNumber!=null) {
						BtnSend_Click(sendNumber, tempmsg);// This is to send message
					}else{
						BtnSend_Click(msgSenderId, tempmsg);// This is to send message
					}
					/*new Thread(new Runnable() {
						public void run() {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(sendNumber!=null) {
										BtnSend_Click(sendNumber, tempmsg);// This is to send message
									}else{
										BtnSend_Click(msgSenderId, tempmsg);// This is to send message
									}
								}
							});
						}
					}).start();*/
					//adapter=
					Log.d(TAG, "");
					/*chatArrayAdapter = new ChatArrayAdapter(context.getApplicationContext(), R.layout.zactivity_chat_singlemessage);
					listView.setAdapter(null);
					//if (isInMode) {
					listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				    listView.setAdapter(chatArrayAdapter);
					loadDb();*/
					//sendChatMessage(false, tempmsg + "\n\n" + msgAt2);
					//sendChatMessage();
				} else {
					Log.d(TAG, "Empty Set");
				}
			}
		});
		Log.d(TAG, "Coversations: " + chatArrayAdapter.toString());
		return view;
	}
	private void loadDb() {
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
	}
	private boolean sendChatMessage(boolean direction,String msg){
		Log.d(TAG,"msg "+ msg);
		chatArrayAdapter.add(new ChatMessage(direction, msg));
		return true;
	}
	private void readContactData() {
		try {
			/*********** Reading Contacts Name And Number **********/
			String phoneNumber = "";
			String phoneType="";
			ContentResolver cr = myApplication.getBaseContext().getContentResolver();
			//Query to get contact name
			cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
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
								/*phoneType=pCur.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE));*/
								//Log.d(TAG," Phone Type: ++++++ "+phoneType);
								// Add contacts names to adapter
								phoneAdapter.add(name.toString() + " " + phoneNumber.toString());
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
			Log.i("AutocompleteContacts", "Exception : " + e);
		}
	}
	// The message inflator
	class BtOnclickListen implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btsendmsg:
					break;
				case R.id.btaddcontact:
					BtnAddContact_Click();
					break;
				default:
					break;
			}
		}
	}
	private void BtnAddContact_Click() {
		if (!myApplication.isOnline()) {
			return;
		}
		String sendTo = etContact.getText().toString();
		if (sendTo == null || sendTo.length() <= 0) {
			return;
		}
		String subject = "";
		mSipSdk.presenceSubscribeContact(sendTo, subject);
		for (int i = 0; i < contacts.size(); i++)// already added
		{
			SipContact tempReference = contacts.get(i);
			String SipUri = tempReference.getSipAddr();
			if (SipUri.equals(sendTo)) {
				tempReference.setSubscribed(true);
				updateLV();
				return;
			}
		}
		SipContact newContact = new SipContact();
		newContact.setSipAddr(sendTo);
		newContact.setSubstatus(false);// off line
		newContact.setSubscribed(true);// weigher send my status to remote
		// subscribe
		newContact.setAccept(false);   // weigher rev remote subscribe
		newContact.setSubId(0);
		contacts.add(newContact);
		updateLV();
	}
	private void BtnDelContact_Click() {
		if (contacts.size() > 0) {
			contacts.remove(selectItem);
		}
		updateLV();
	}
	private void BtnUpdateContact_Click() {
		for (int i = 0; i < contacts.size(); ++i) {
			SipContact tempReference = contacts.get(i);
			String SipUri = tempReference.getSipAddr();
			String subject = "";
			long subscribeId = tempReference.getSubId();
			if (tempReference.isSubscribed()) {
				mSipSdk.presenceSubscribeContact(SipUri, subject);
			}
			String statusText = etStatus.getText().toString();
			if (tempReference.isAccept() && subscribeId != -1) {
				mSipSdk.presenceOnline(subscribeId, statusText);
			}
		}
	}
	private void BtnClearContact_Click() {
		contacts.clear();
		updateLV();
	}
	private void updateLV() {
		mAdapter.notifyDataSetChanged();
	}
	private void BtnSetStatus_Click() {
		if (!myApplication.isOnline()) {
			return;
		}
		String content = etStatus.getText().toString();
		if (content == null || content.length() <= 0) {
			showTips("please input status description string");
			return;
		}
		for (int i = 0; i < contacts.size(); ++i) {
			SipContact tempReferece = contacts.get(i);
			long subscribeId = tempReferece.getSubId();
			String statusText = etStatus.getText().toString();
			if (tempReferece.isAccept() && subscribeId != -1) {
				mSipSdk.presenceOnline(subscribeId, statusText);
			}
		}
		//String sendTo="sip:"+send+"@callman.cloudapp.net:5060";
	}
	private void BtnSend_Click(String send,String content) {
		if (!myApplication.isOnline()) {
			Log.d(TAG,"not online need to be registed");
			return;
		}
		//String encrypted = encryption.encryptOrNull(content);
		if(send==null){
			send=mTxtPhoneNo.getText().toString();
		}
		String Number=null;
		String finalDestination;
		try {
			Number = send.replaceAll("[^0-9.+]", "");
			if (Number.contains("+")) {
				finalDestination = Number;
			} else if (Number.charAt(0) == '4' && Number.charAt(1) == '7') {
				finalDestination = "+" + Number;
			} else {
				finalDestination = "+47" + Number;
			}
			String sendTo="sip:"+finalDestination+"@itmansecurity.cloudapp.net:5060";
		//SIP ADDRESS sip:asterisk 222@callman.cloudapp.net:5060.....asterisk 222
		//String sendTo="sip:"+send+"@10.14.4.26:5060";
		//Log.d(TAG,"SIP ADDRESS "+sendTo+"....."+send);
		if (send == null || sendTo.length() <= 0) {
			Toast.makeText(context, "Please input send to target",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (content == null || content.length() <= 0) {
			Toast.makeText(context, "Please input message content",
					Toast.LENGTH_SHORT).show();
			return;
		}

		etMessage.setText("");
		mSipSdk.sendOutOfDialogMessage(sendTo, "text", "plain",
				content.getBytes(), content.length());
		}catch (Exception e){
			//e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),"Can't send to Service Provider, Please contact US",Toast.LENGTH_LONG).show();
		}
	}
	void showTips(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	private class BaseListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		public BaseListAdapter(Context mContext) {
			inflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			return contacts.size();
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tvRefrence;
			ImageButton btDelContact;
			SipContact contactRefrence;
			contactRefrence = contacts.get(position);
			convertView = inflater.inflate(R.layout.viewlistitem, null);
			tvRefrence = (TextView) convertView.findViewById(R.id.tvsipaddr);
			tvRefrence.setText(contactRefrence.getSipAddr());
			tvRefrence = (TextView) convertView
					.findViewById(R.id.tvsubdescription);
			tvRefrence.setText(contactRefrence.currentStatusToString());
			btDelContact = (ImageButton) convertView
					.findViewById(R.id.btdelcontact);
			btDelContact.setOnClickListener(clickListener);
			return convertView;
		}
	}
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//updateLV();
			Toast.makeText(getActivity().getApplicationContext(),"We got message",Toast.LENGTH_LONG).show();
			// The message will come hre
			// If TextView is empty - copy string from EditText
			Log.d(TAG, "WE GOT MESSAGE ");
		}
	};
	@Override
	public void onStop(){
		super.onStop();
		//Intent intet= new Intent(getActivity(),LoginActivity.class);
		//startActivity(intet);
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

		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				getActivity().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

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
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
					getActivity().INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
			// Show Alert
			Toast.makeText(getActivity().getBaseContext(),
					"Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue,
					Toast.LENGTH_LONG).show();

			Log.d("AutocompleteContacts",
					"Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue);
		}

	}
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MyApplication.CONTACT_CHANG);
		context.registerReceiver(mReceiver, mIntentFilter);
		Context mContext;
		mContext=context.getApplicationContext();
		final Reconnect_Server reconnect_server;
		reconnect_server=new Reconnect_Server(mContext);
		if(!myApplication.isOnline()) {
			final Handler handler = new Handler();
			final Runnable runnable = new Runnable() {
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							reconnect_server.online();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
				}
			};
			new Thread(runnable).start();
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		//sessionManager.setSmsInf(null,null,null,null,null,false);
		context.unregisterReceiver(mReceiver);
	}
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		cursorLoader= new CursorLoader(getActivity(), Uri.parse("content://com.portsip.myprovider.MyProvider/cte"), null, null, null, null);
		return cursorLoader;
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		cursor.moveToFirst();
		StringBuilder res=new StringBuilder();
		int check=0;
		while (!cursor.isAfterLast()) {
			res.append("\n"+cursor.getString(cursor.getColumnIndex("id"))+ "-"+ cursor.getString(cursor.getColumnIndex("name"))
					+ "-"+ cursor.getString(cursor.getColumnIndex("message"))
					+ "-"+ cursor.getString(cursor.getColumnIndex("date")));
			int ID=cursor.getColumnIndex("id");
			String NAME=cursor.getString(cursor.getColumnIndex("name"));
			String MSG=cursor.getString(cursor.getColumnIndex("message"));
			String DATE=cursor.getString(cursor.getColumnIndex("date"));
			String tag=cursor.getString(cursor.getColumnIndex("key"));
			//Log.d(TAG, "VALEEE   " + cursor.getColumnIndex("side")+"\n"+tag);
			Integer result=cursor.getInt(cursor.getColumnIndex("side"));
			boolean parameter;
			if(result ==  null || result == 0) {
				parameter = false; // Let me put the informatoin reight
			} else {
				parameter = true; // Let me put the information right
			}
			//sendChatMessage(parameter,MSG+"\n\n"+DATE);//+"\n\n"+DATE
			String leftString=null;
			String rightString=null;
			try {
				String[] parts = tag.split("@");
				leftString = parts[0].trim().toString();
				rightString = parts[1].trim().toString();
			}catch(Exception e){
				Log.d(TAG,"Error"+e.getMessage());
			}
			//Log.d(TAG,"required Strings are: "+leftString+"  "+rightString);
			//Log.d(TAG,"Message Sender ID: "+msgSenderId+"\n"+NAME);
			String comparison=null;
			if(senderHolder!=null){
				comparison=senderHolder; //Log.d(TAG,"Current comparator is: "+senderHolder);
			}
			else{
				comparison=sendNumber; //Log.d(TAG,"Current comparator is: "+sendNumber);
			}
			//Log.d(TAG,"comparison :"+comparison+"----NAME: "+NAME+"--- parameter: "+parameter);
			//Log.d(TAG,"NAME: "+NAME+"----RightString: "+rightString+"--- parameter: "+parameter);
			try {
				if (comparison.trim().toString().equals(NAME.trim().toString())) {
					sendChatMessage(true, MSG+"\n\n"+DATE);//+"\n\n"+DATE
					check += 1;
				}
				else if (sessionManager.getPhone().equals(NAME.trim().toString())
						&& comparison.toString().equals(rightString)) {
					sendChatMessage(false, MSG+"\n\n"+DATE);//+"\n\n"+DATE
					check += 1;
				}
				else{
					// Nothing to show
					check=0;
				}
			}catch(Exception E){
				E.printStackTrace();
			}
			cursor.moveToNext();
		}
			if(check<1){
				senderHolder=null;
				//chatArrayAdapter = new ChatArrayAdapter(context.getApplicationContext(), R.layout.zactivity_chat_singlemessage);
				//listView.setAdapter(null);
		}
		Log.d(TAG, "Database vaues are: " + res);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}
}
/* Encryption encryption = new Encryption.Builder()
                .setKeyLength(128)
                .setKey("Key")
                .setSalt("Salt")
                .setIv(yourByteIvArray)
                .setCharsetName("UTF8")
                .setIterationCount(65536)
                .setDigestAlgorithm("SHA1")
                .setBase64Mode(Base64.DEFAULT)
                .setAlgorithm("AES/CBC/PKCS5Padding")
                .setSecureRandomAlgorithm("SHA1PRNG")
                .setSecretKeyType("PBKDF2WithHmacSHA1")
                .build();
		* */

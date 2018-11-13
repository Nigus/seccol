package com.portsip;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.portsip.register_user.PhoneUtils;
import com.portsip.main.CallInfo;
import com.portsip.main.ConnectedCall;
import com.portsip.util.Line;
import com.portsip.util.Session;

import java.lang.reflect.Field;

import helper.SessionManager;

public class NumpadFragment extends Fragment{
	private static final String TAG="NumpadFragment";
	protected PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
	PortSipSdk mPortSipSdk;
	MyApplication myApp;
	long sessionId;
	private TableLayout dialerPad, functionPad;
	private EditText etSipNum;
	private TextView mtips;
	private Spinner spline;
	private Context context = null;
	CheckBox cbsendVideo, cbrecVideo, cbConfrence, cbSendSdp;
	private MyItemClickListener myItemClickListener;
	int _CurrentlyLine = 0;
	Line[] lines = null;
	ArrayAdapter<Session> spinerAdapter;
	private boolean call_flag_here;
	private String destNumber;
	private NumpadFragment fragment;
	SessionManager session;
	private String phone_number;
	private String callerNameID;
	String status;
	View view;
	private TextView tempview;
	private TextView timerValue;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	String duration;
	int dur_int;
	private EditText mDigits;
	private View mDelete;
	private View mDialpad;
	private View mAdditionalButtonsRow;
	private View mSearchButton;
	private View mDialButton;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			call_flag_here = getArguments().getBoolean("callflag");
			//destNumber=getArguments().getString("dialingStateNr");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		session=new SessionManager(getActivity().getApplicationContext());
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		context = getActivity();
		myApp = (MyApplication) context.getApplicationContext();
		myApp.setStatusFlag(true);
		mPortSipSdk = myApp.getPortSIPSDK();
		// bring the destination number
		session=new SessionManager(context.getApplicationContext());
		destNumber=session.getDestValue();
		view = inflater.inflate(R.layout.numpad, null);
		dialerPad = (TableLayout) view.findViewById(R.id.dialer_pad);
		functionPad = (TableLayout) view.findViewById(R.id.function_pad);
		etSipNum = (EditText) view.findViewById(R.id.etsipaddress);
		cbSendSdp = (CheckBox) view.findViewById(R.id.sendSdp);
		cbConfrence = (CheckBox) view.findViewById(R.id.conference);
		cbsendVideo = (CheckBox) view.findViewById(R.id.sendVideo);
		cbrecVideo = (CheckBox) view.findViewById(R.id.acceptVideo);
		etSipNum.setText(destNumber);
		mDigits = (EditText) view.findViewById(R.id.digits);
		mDigits.setText(destNumber);
		lines = myApp.getLines();
		spline = (Spinner) view.findViewById(R.id.sp_lines);
		spinerAdapter = new ArrayAdapter<Session>(context,
				R.layout.viewspinneritem, lines);
		spline.setAdapter(spinerAdapter);
		spline.setOnItemSelectedListener(new MyItemSelectListener());
		myItemClickListener = new MyItemClickListener();
		Button bt = (Button) view.findViewById(R.id.dialer);
		bt.setOnClickListener(myItemClickListener);
		mtips = (TextView) view.findViewById(R.id.txtips);
		ImageButton imgbt = (ImageButton) view.findViewById(R.id.pad);
		imgbt.setOnClickListener(myItemClickListener);
		imgbt = (ImageButton) view.findViewById(R.id.delete);
		imgbt.setOnClickListener(myItemClickListener);
		cbConfrence.setChecked(myApp.isConference());
		cbConfrence.setOnCheckedChangeListener(new ConferenceBoxOnChange());
		setTableItemClickListener(dialerPad, myItemClickListener);
		setTableItemClickListener(functionPad, myItemClickListener);
		View oneButton = view.findViewById(R.id.one1);
		if (oneButton != null)
		{
			//setupKeypad(v);
		}
		mAdditionalButtonsRow = view.findViewById(R.id.dialpadAdditionalEnd);
		mSearchButton = mAdditionalButtonsRow.findViewById(R.id.btn1);
		if (mSearchButton != null)
		{
			mSearchButton.setOnClickListener(myItemClickListener);
		}
		// Check whether we should show the onscreen "Dial" button.
		mDialButton = mAdditionalButtonsRow.findViewById(R.id.endButton);
		mDialButton.setOnClickListener(myItemClickListener);
		mDialButton.setEnabled(true);
		mDelete = mAdditionalButtonsRow.findViewById(R.id.delbtn);
		mDelete.setOnClickListener(myItemClickListener);
		mDialpad = view.findViewById(R.id.dialpad); // This is null in landscape
		// mode.
		// In landscape we put the keyboard in phone mode.
		if (mDialpad==null)
		{
			mDigits.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
		}
		else // If the digit is empty just ignore it
		{
			mDigits.setCursorVisible(false);
		}
		//        Set up the "dialpad chooser" UI; see showDialpadChooser().
		//        mDialpadChooser = (ListView) findViewById(R.id.dialpadChooser);
		//        mDialpadChooser.setOnItemClickListener(this);
		tempview = (TextView) view.findViewById(R.id.textViewconnected);

		if(session.getCallingFlag()){
			phone_number=session.getDestValue();
			callerNameID=session.getDestName();
			status="Calling";
		}
		String str;
		if(callerNameID==null){
			str = " " + phone_number;
		}else {
			str = " " + callerNameID;
		}
		mDigits.setTextSize(12);
		mDigits.setText(str.toString());
			String finalDestination;
		String areaCode=PhoneUtils.getCountryRegionFromPhone(context);
		String areaCodeNumber=session.getAreaCode();
		Log.d(TAG,"AREACODE IS"+areaCode+"\n"+areaCodeNumber);
		String result=validate(destNumber);
		Log.d(TAG,"Phone After validation is "+ result);
		destNumber=destNumber.trim();
		if(destNumber.contains("+")){
			finalDestination=destNumber;
		}else if(destNumber.charAt(0)=='4'&&destNumber.charAt(1)=='7'){
			finalDestination="+"+destNumber;
		}else{
			finalDestination="+47"+destNumber;
		}
		Log.d(TAG,"The destinaion value after prefix added is "+finalDestination);
		callme(finalDestination);
		//onClick(view.findViewById(R.id.btonline));
		//onClick(view.findViewById(R.id.dial));
		return null;
	}
	protected String validate(String mLastEnteredPhone) {
		String region = null;
		String phone = null;
		if (mLastEnteredPhone != null) {
			try {
				Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(mLastEnteredPhone, null);
				StringBuilder sb = new StringBuilder(16);
				sb.append('+').append(p.getCountryCode()).append(p.getNationalNumber());
				phone = sb.toString();
				region = mPhoneNumberUtil.getRegionCodeForNumber(p);

			} catch (NumberParseException ignore) {
			}
		}
		if (region != null) {
			return phone;
		} else {
			return null;
		}
	}
	/*private void setupKeypad(View v)
	{
		// Setup the listeners for the buttons
		View view = v.findViewById(R.id.one1);
		view.setOnClickListener((OnClickListener) this);
		view.setOnLongClickListener((View.OnLongClickListener) this);
		v.findViewById(R.id.two1).setOnClickListener((OnClickListener) this);
		v.findViewById(R.id.three1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.four1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.five1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.six1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.seven1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.eight1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.nine1).setOnClickListener((OnClickListener)this);
		v.findViewById(R.id.star1).setOnClickListener((OnClickListener)this);
		view = v.findViewById(R.id.zero1);
		view.setOnClickListener((OnClickListener)this);
		view.setOnLongClickListener((View.OnLongClickListener) this);
		v.findViewById(R.id.pound).setOnClickListener((OnClickListener) this);
	}*/
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;
			CallInfo callInfo=new CallInfo();
			String str=Long.toString(updatedTime);
			callInfo.setDuration(str);
			int secs = (int) (updatedTime / 1000);
			dur_int=secs;
			int mins = secs / 60;
			secs = secs % 60;
			int milliseconds = (int) (updatedTime % 1000);
			duration="" + mins + ":"
					+ String.format("%02d", secs) + ":"
					+ String.format("%03d", milliseconds);
			//duration=callInfo.getDuration();
            /*timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));*/
			customHandler.postDelayed(this, 0);
		}
	};
	void switchVisability(View view) {
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.INVISIBLE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}
	public void callme(String dest){

		if (dest == null || dest.length() <= 0) {
			showTips("The phone number is empty.");
			return;
		}
		Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
		if (currentLine.getSessionState()
				|| currentLine.getRecvCallState()) {
			showTips("Current line is busy now, please switch a line.");
			return;
		}
		//Ensure that we have been added one audio codec at least
		if (mPortSipSdk.isAudioCodecEmpty()) {
			//showTips("Audio Codec Empty,add audio codec at first");
			mPortSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ILBC);
			mPortSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
			//return;
		}
		//Usually for 3PCC need to make call without SDP
		sessionId = mPortSipSdk.call(dest,
				cbSendSdp.isChecked(), cbsendVideo.isChecked());

		if (sessionId <= 0) {
			showTips("Call failure");
			return;
		}
		currentLine.setSessionId(sessionId);
		currentLine.setSessionState(true);
		currentLine.setVideoState(cbsendVideo.isChecked());
		myApp.setCurrentLine(lines[_CurrentlyLine]);
		tempview.setText("Calling");
		showTips(lines[_CurrentlyLine].getLineName() + ": Calling...");
		myApp.updateSessionVideo();
		//Utils.playTone(this.getActivity(), ToneGenerator.TONE_SUP_DIAL);
		Intent intet = new Intent(getActivity().getApplicationContext(),ConnectedCall.class);
		intet.putExtra("dial_num",dest);
		intet.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intet);
}
	class MyItemSelectListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {
			int index = arg2;
			if (_CurrentlyLine == (index + Line.LINE_BASE)) {
				return;
			}

			if (!myApp.isOnline()) {
				showTips("Not Registered, please register at first.");
				return;
			}

			if (cbConfrence.isChecked()) {
				_CurrentlyLine = arg2 + Line.LINE_BASE;
				return;
			}
			// To switch the line, must hold currently line first
			Line currentLine = myApp.findSessionByIndex(_CurrentlyLine);
			if (currentLine.getSessionState()
					&& !currentLine.getHoldState()) {
				mPortSipSdk.hold(currentLine.getSessionId());
				currentLine.setHoldState(true);
				showTips(lines[_CurrentlyLine].getLineName() + ": Hold");
			}
			_CurrentlyLine = arg2 + Line.LINE_BASE;
			currentLine = myApp.findSessionByIndex(_CurrentlyLine);// update
			// current
			// line
			myApp.setCurrentLine(currentLine);
			// If target line was in hold state, then un-hold it
			if (currentLine.getSessionState()
					&& currentLine.getHoldState()) {
				mPortSipSdk.unHold(currentLine.getSessionId());
				currentLine.setHoldState(false);
				tempview.setText("Secure Connected");
				showTips(lines[_CurrentlyLine].getLineName()
						+ ": UnHold - call established");
			}
			spinerAdapter.notifyDataSetChanged();
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
	private void endCall(){
		endcall();
		session.setDestNumber(null);
		session.setDestName(null);
		myApp.sendSessionChangeMessage("", myApp.SESSION_CHANG);
		//tempSession.setDescriptionString("Call Ended");
		tempview.setText("Call Ended");
		session.setCallingFlag(false);
		Intent intent= new Intent(getActivity(),MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	public void endcall(){
		Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
		if (currentLine.getRecvCallState()) {
			mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
			currentLine.reset();
			showTips(lines[_CurrentlyLine].getLineName()
					+ ": Rejected call");
			return;
		}
		if (currentLine.getSessionState()) {
			mPortSipSdk.hangUp(currentLine.getSessionId());
			currentLine.reset();
			Log.d(TAG, "===================Call is disconnected:======================");
			showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
		}
		myApp.updateSessionVideo();
	}
	class MyItemClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!myApp.isOnline()) {
				showTips("Not Registered, please register at first.");
				return;
			}
			switch (v.getId()) {
				case R.id.zero:
				case R.id.one:
				case R.id.two:
				case R.id.three:
				case R.id.four:
				case R.id.five:
				case R.id.six:
				case R.id.seven:
				case R.id.eight:
				case R.id.nine:
				case R.id.star:
				case R.id.sharp: {
					String numberString = ((Button) v).getText().toString();
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
					char number = numberString.charAt(0);
					etSipNum.getText().append(number);
					if (myApp.isOnline()
							&& currentLine.getSessionState()) {
						if (number == '*') {
							mPortSipSdk.sendDtmf(currentLine.getSessionId(),
									PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 10,
									160, true);
							return;
						}
						if (number == '#') {
							mPortSipSdk.sendDtmf(currentLine.getSessionId(),
									PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 11,
									160, true);
							return;
						}
						int sum = Integer.valueOf(numberString);// 0~9
						mPortSipSdk.sendDtmf(currentLine.getSessionId(),
								PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, sum,
								160, true);
					}
				}
				break;
				case R.id.endButton:
					endCall();
					break;
				case R.id.delete:
					int cursorpos = etSipNum.getSelectionStart();
					if (cursorpos - 1 >= 0) {
						etSipNum.getText().delete(cursorpos - 1, cursorpos);
					}
					break;
				case R.id.pad:
					//switchVisability(dialerPad);
					break;
				case R.id.dial: {
					String callTo = etSipNum.getText().toString();
					if (callTo == null || callTo.length() <= 0) {
						showTips("The phone number is empty.");
						return;
					}
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
					if (currentLine.getSessionState()
							|| currentLine.getRecvCallState()) {
						showTips("Current line is busy now, please switch a line.");
						return;
					}
					// Ensure that we have been added one audio codec at least
					if (mPortSipSdk.isAudioCodecEmpty()) {
						showTips("Audio Codec Empty,add audio codec at first");
						return;
					}
					// Usually for 3PCC need to make call without SDP
					long sessionId = mPortSipSdk.call(callTo,
							cbSendSdp.isChecked(), cbsendVideo.isChecked());
					if (sessionId <= 0) {
						showTips("Call failure");
						return;
					}
					currentLine.setSessionId(sessionId);
					currentLine.setSessionState(true);
					currentLine.setVideoState(cbsendVideo.isChecked());
					myApp.setCurrentLine(lines[_CurrentlyLine]);
					showTips(lines[_CurrentlyLine].getLineName() + ": Calling...");
					tempview.setText("Calling");
					myApp.updateSessionVideo();
				}
				break;
				case R.id.hangup: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
					if (currentLine.getRecvCallState()) {
						mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
						currentLine.reset();
						showTips(lines[_CurrentlyLine].getLineName()
								+ ": Rejected call");

						return;
					}
					if (currentLine.getSessionState()) {
						mPortSipSdk.hangUp(currentLine.getSessionId());
						currentLine.reset();

						showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
					}
					myApp.updateSessionVideo();
				}
				break;
				case R.id.answer: {
					Line currentLine = myApp.findSessionByIndex(_CurrentlyLine);

					if (!currentLine.getRecvCallState()) {
						showTips("No incoming call on current line, please switch a line.");
						return;
					}

					currentLine.setRecvCallState(false);
					myApp.answerSessionCall(currentLine,cbrecVideo.isChecked());
				}
				break;
				case R.id.reject: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
					if (currentLine.getRecvCallState()) {
						mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
						currentLine.reset();
						showTips(lines[_CurrentlyLine].getLineName()
								+ ": Rejected call");
						return;
					}
					break;
				}

				case R.id.hold: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


					if (!currentLine.getSessionState()
							|| currentLine.getHoldState()) {
						return;
					}

					int rt = mPortSipSdk.hold(currentLine.getSessionId());
					if (rt != 0) {
						showTips("hold operation failed.");
						return;
					}
					currentLine.setHoldState(true);
				}
				break;
				case R.id.unhold: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


					if (!currentLine.getSessionState()
							|| !currentLine.getHoldState()) {
						return;
					}

					int rt = mPortSipSdk.unHold(currentLine.getSessionId());
					if (rt != 0) {

						currentLine.setHoldState(false);
						showTips(lines[_CurrentlyLine].getLineName()
								+ ": Un-Hold Failure.");
						return;
					}

					currentLine.setHoldState(false);
					showTips(lines[_CurrentlyLine].getLineName() + ": Un-Hold");
				}
				break;
				case R.id.attenttransfer: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


					if (!currentLine.getSessionState()) {
						showTips("Need to make the call established first");
						return;
					}
					showTransferDialog(R.id.attenttransfer);
				}
				break;
				case R.id.mic:
					if (((Button) v).getText().equals("SpeekOn")) {
						mPortSipSdk.setLoudspeakerStatus(true);
						((Button) v).setText("SpeekOff");
					} else {
						mPortSipSdk.setLoudspeakerStatus(false);
						((Button) v).setText("SpeekOn");
					}
					break;
				case R.id.mute: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
					if (((Button) v).getText().equals("Mute")) {
						mPortSipSdk.muteSession(currentLine.getSessionId(), true,
								true, true, true);
						((Button) v).setText("UnMute");
					} else {
						mPortSipSdk.muteSession(currentLine.getSessionId(), false,
								false, false, false);
						((Button) v).setText("Mute");
					}
				}
				break;
				case R.id.transfer: {
					Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


					if (!currentLine.getSessionState()) {
						showTips("Need to make the call established first.");
						return;
					}

					showTransferDialog(R.id.transfer);
				}
				break;
			}
			spinerAdapter.notifyDataSetChanged();
		}
	}

	private class ConferenceBoxOnChange implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			{
				if (!myApp.isOnline()) {
					showTips("Not Registered, please register at first.");
					buttonView.setChecked(false);
					myApp.setConfrenceMode(false);
					return;
				}

				int videoResolution = PortSipEnumDefine.ENUM_RESULUTION_CIF; // you
				// need
				// use
				// the
				// appropriate
				// resolution
				if (isChecked) {
					int rt = mPortSipSdk
							.createConference(myApp.getRemoteSurfaceView(),
									videoResolution, true);
					if (rt == 0) {
						showTips("Make conference succeeded");
						Line[] sessions = myApp.getLines();
						for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
							if (sessions[i].getSessionState()) {
								if (sessions[i].getHoldState()) {
									mPortSipSdk.unHold(sessions[i]
											.getSessionId());
								}
								mPortSipSdk.joinToConference(sessions[i]
										.getSessionId());
								sessions[i].setHoldState(false);
							}
						}

						myApp.setConfrenceMode(true);
					} else {
						showTips("Failed to create conference");
						myApp.setConfrenceMode(false);
						buttonView.setChecked(false);
					}
				} else {
					// Stop conference
					// Before stop the conference, MUST place all lines to hold
					// state
					myApp.setConfrenceMode(false);
					Line[] sessions = myApp.getLines();
					for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
						if (sessions[i].getSessionState()
								&& !sessions[i].getHoldState()) {
							if (i != _CurrentlyLine) {
								// Hold the line
								mPortSipSdk.hold(sessions[i].getSessionId());
								sessions[i].setHoldState(true);
							}
						}
					}
					mPortSipSdk.destroyConference();
					showTips("Taken off Conference");

				}
			}
		}
	}
	private void setTableItemClickListener(TableLayout table,
										   OnClickListener listener) {
		int row = table.getChildCount();
		for (int i = 0; i < row; i++) {
			TableRow tableRow = (TableRow) table.getChildAt(i);
			int line = tableRow.getChildCount();
			for (int index = 0; index < line; index++) {
				tableRow.getChildAt(index).setOnClickListener(
						myItemClickListener);
			}
		}
	}

	void showTips(String text) {
		mtips.setText(text);
		spinerAdapter.notifyDataSetChanged();
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mtips.setText(intent.getStringExtra("description"));
			spinerAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MyApplication.SESSION_CHANG);
		context.registerReceiver(mReceiver, mIntentFilter);
		String mytext=tempview.getText().toString();
		if(mytext=="Calling"){
			//myApp.playAlertTone(this.context,"");
		}
		else{

			if(MyApplication.SESSION_CHANG!="Calling"){
				//myApp.playAlertTone(this.context,"stop");
				tempview.setText("SecureConnected");
			}
		}
	}
	@SuppressWarnings("unused")
	// please call this function in appropriate place
	private void startMediaRecord(String fileName) {
		if (!myApp.isOnline()) {
			return;
		}
		SharedPreferences mpreferences = context.getSharedPreferences(
				String.format("%s_preferences", context.getPackageName()),
				Context.MODE_PRIVATE);
		String filePath = mpreferences.getString(
				getString(R.string.str_avpathkey), "");
		if (filePath.length() <= 0 || fileName == null
				|| fileName.length() <= 0) {
			return;
		}

		// Start recording
		Session curSession = myApp.getCurrentSession();
		if (curSession != null
				&& curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
			mPortSipSdk.startRecord(curSession.getSessionId(), filePath,
					fileName, true, PortSipEnumDefine.ENUM_VIDEOCODEC_H264,
					PortSipEnumDefine.ENUM_RECORD_MODE_BOTH,
					PortSipEnumDefine.ENUM_VIDEOCODEC_H264,
					PortSipEnumDefine.ENUM_RECORD_MODE_BOTH);
		}
	}

	@SuppressWarnings("unused")
	// please call this function in appropriate place
	private void stopMediaRecord() {
		if (!myApp.isOnline()) {
			return;
		}

		Session curSession = myApp.getCurrentSession();
		if (curSession != null
				&& curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
			mPortSipSdk.stopRecord(curSession.getSessionId());
		}
	}

	void showTransferDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(
				R.layout.transfer_inputdialog, null);
		builder.setIcon(R.drawable.icon);
		builder.setTitle("Transfer input");
		builder.setView(textEntryView);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				EditText tranferto = (EditText) textEntryView
						.findViewById(R.id.ettransferto);
				EditText tranferline = (EditText) textEntryView
						.findViewById(R.id.ettransferline);
				String referTo = tranferto.getText().toString();
				if (referTo.length() <= 0) {
					showTips("The transfer number is empty");
					return;
				}
				String lineString = tranferline.getText().toString();
				switch (id) {
					case R.id.transfer: {
						int rt = mPortSipSdk.refer(currentLine.getSessionId(),
								referTo);
						if (rt != 0) {
							showTips(lines[_CurrentlyLine].getLineName()
									+ ": failed to Transfer");
						} else {
							showTips(lines[_CurrentlyLine].getLineName()
									+ " failed to Transfer");
						}
					}
					break;
					case R.id.attenttransfer: {
						int line = Line.LINE_BASE - 1;
						try {
							line = Integer.valueOf(lineString);
						} catch (NumberFormatException e) {
							showTips("The replace line wrong");
						}

						if (line < Line.LINE_BASE || line >= Line.MAX_LINES) {
							showTips("The replace line out of range");
							return;
						}
						Session replaceSession = myApp.findSessionByIndex(line);
						if (replaceSession == null
								|| !replaceSession.getSessionState()) {
							showTips("The replace line does not established yet");
							return;
						}

						int rt = mPortSipSdk.attendedRefer(
								currentLine.getSessionId(),
								replaceSession.getSessionId(), referTo);

						if (rt != 0) {
							showTips(lines[_CurrentlyLine].getLineName()
									+ ": failed to Attend transfer");
						} else {
							showTips(lines[_CurrentlyLine].getLineName()
									+ ": Transferring");
						}
					}
					break;
				}
			}
		});

		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
		builder.create().show();
	}
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		context.unregisterReceiver(mReceiver);
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		Log.d(TAG, "Duration in Long data type is: " + updatedTime);
		Log.d(TAG, "Duraion is: " + duration);
		//Toast.makeText(getActivity().getApplicationContext(),"§§§§ Duration is \n"+duration,Toast.LENGTH_LONG).show();
			// Call log for outgoing call
		/*HomeFragment homeFragment= new HomeFragment();
		insertPlaceholderCallDialed(getActivity().getContentResolver(), phone_number, callerNameID,dur_int);
		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, homeFragment).commit();*/
		//view=null;
	}
	public static void insertPlaceholderCallDialed(ContentResolver contentResolver, String number,String name,int dura) {
		ContentValues values = new ContentValues();
		values.put(CallLog.Calls.NUMBER, number);
		values.put(CallLog.Calls.DATE, System.currentTimeMillis());
		values.put(CallLog.Calls.DURATION, dura);
		values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
		values.put(CallLog.Calls.NEW, 1);
		values.put(CallLog.Calls.CACHED_NAME, name);
		values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
		values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
		Log.d(TAG, "Inserting call log placeholder for " + number);
		contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
	}

}

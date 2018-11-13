package com.portsip;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.portsip.bulk.chooseservice;
import com.portsip.main.ConnectedCall;
import com.portsip.main.HomeTabActivity;
import com.portsip.main.RecentActivity;
import com.portsip.main.SendSms;
import com.portsip.main.chat.Sms;
import com.portsip.pushnotification.WakeLocker;
import com.portsip.service.PortSipService;
import com.portsip.service.PortSipService.MyBinder;
import com.portsip.util.Line;
import com.portsip.util.Session;
import com.portsip.util.SipContact;
import com.portsip.wakelock.ScreenIncomming;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import helper.SessionManager;
public class MyApplication extends Application implements OnPortSIPEvent{
	public static final String TAG = MyApplication.class.getSimpleName();
	private static ArrayList<String> userDetail=new ArrayList<String>();
	private static int FM_NOTIFICATION_ID=1000;
	private static int FM_NOTIFICATION_ID_2=2000;
	private static int FM_NOTIFICATION_ID_SMS=3000;
	private static ArrayList<String> phone_list=new ArrayList<>();
	private RequestQueue mRequestQueue;
	AlertDialog alertDialog2;
	public int playToneC;
	private static MyApplication mInstance;
	private String globalVariable;
	private static String gName;
	private static String gPhone;
	private static String gPassword;
	private static String gEmail;
	private boolean dialogFlag;
	private static boolean gFlag;
	private static boolean gDialogFlag;
	private String callerName;
	private String callerId;
	int counter;
	private String status;
	private String status2;
	Ringtone r;
	Vibrator vib;
	Uri notification2;
	SessionManager sessionManager;
	private Thread t;
	boolean statusFlag;
	Intent srvIntent = null;
	PortSipService portSrv = null;
	MyServiceConnection connection = null;
	PortSipSdk sdk;
	boolean conference = false;
	private boolean _SIPLogined = false;// record register status
	HomeTabActivity callscreen;
	chooseservice serv;
	MainActivity mainActivity;
	ConnectedCall connectedCall;
	HomeTabActivity hometabActivity;
	SendSms sendSms;
	MediaPlayer player;
	Context mContext;
	Reconnect_Server reconnect_server;
	//AlertDialog alertDialog2;
	Calling Callingact;
	private String inputNumber;
	private SurfaceView remoteSurfaceView = null;
	private SurfaceView localSurfaceView = null;
	static final private Line[] _CallSessions = new Line[Line.MAX_LINES];// record
	// all
	// audio-video
	// sessions
	static final private ArrayList<SipContact> contacts = new ArrayList<SipContact>();
	private Line _CurrentlyLine = _CallSessions[Line.LINE_BASE];// active line
	static final String SIP_ADDRRE_PATTERN = "^(sip:)(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}(:[0-9]{2,5})?$";
	public static final String SESSION_CHANG = MyApplication.class
			.getCanonicalName() + "Session change";
	public static final String CONTACT_CHANG = MyApplication.class
			.getCanonicalName() + "Contact change";
	private boolean flag;
	private static List<Sms> smsList;
	public void sendSessionChangeMessage(String message, String action) {
		//Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.squarelogo)
						.setContentTitle("SecureCall")
						.setOngoing(true);
						//.setContentText(message);//.setSound(soundUri);
		Intent notificationIntent;
		try {
			notificationIntent = new Intent(this, MainActivity.class);
		}catch (Exception e){
			notificationIntent = new Intent(this, HomeTabActivity.class);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		// Add as notification
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(FM_NOTIFICATION_ID, builder.build());
		Intent broadIntent = new Intent(action);
		broadIntent.putExtra("description", message);
		sendBroadcast(broadIntent);
	}

	public Line[] getLines() {
		return _CallSessions;
	}
	List<SipContact> getSipContacts() {
		return contacts;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		sdk = new PortSipSdk();
		srvIntent = new Intent(this, PortSipService.class);
		connection = new MyServiceConnection();
		sdk.setOnPortSIPEvent(this);
		localSurfaceView = Renderer.CreateLocalRenderer(this);
		remoteSurfaceView = Renderer.CreateRenderer(this, true);
		bindService(srvIntent, connection, BIND_AUTO_CREATE);
		for (int i = 0; i < _CallSessions.length; i++) {
			_CallSessions[i] = new Line(i);
		}
		//_CallSessions[0];
		//startNotification();
		//sendSessionChangeMessage(comingCallTips, SESSION_CHANG);
		sendSessionChangeMessage("SecureCall", SESSION_CHANG);
		Log.d(TAG,"State: App Created");
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		unbindService(connection);
		connection = null;
		Log.d(TAG,"State: Terminated Terminated");
	}
	public SurfaceView getRemoteSurfaceView() {
		return remoteSurfaceView;
	}

	public SurfaceView getLocalSurfaceView() {
		return localSurfaceView;
	}
	public PortSipSdk getPortSIPSDK() {
		return sdk;
	}

	public void setStatusFlag(boolean flag) {
		this.statusFlag = flag;
	}

	public boolean getStatusFlag(){return this.statusFlag;}
	public ArrayList<String> dbPhone;
	public void setdbPhone(ArrayList<String> dbPhone) {
		this.dbPhone = dbPhone;
	}
	public ArrayList<String> getdbPhone(){return this.dbPhone;}
	class MyServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			portSrv = binder.getService();
			Log.d(TAG,"SERVICE CONNECTED");
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			portSrv = null;
			Log.d(TAG,"SERVICE DISCONNECTEED");
		}
	}
	public void startNotification(){
			int mNotificationId = 001;
			// Build Notification , setOngoing keeps the notification always in status bar
			android.support.v7.app.NotificationCompat.Builder mBuilder =
					(android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(this)
							.setSmallIcon(R.drawable.squarelogo)
							.setContentTitle("SecureCall")
							.setContentText("")
							.setOngoing(true);
			// Gets an instance of the NotificationManager service
			//notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			NotificationManager mNotifyMgr =
					(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			// Build the notification and issues it.
			mNotifyMgr.notify(mNotificationId, mBuilder.build());
		}

	public boolean isOnline() {
		return _SIPLogined;
	}
	public void setConfrenceMode(boolean state) {
		conference = state;
	}
	public boolean isConference() {
		return conference;
	}
	public void setOnlineState(boolean state) {
		_SIPLogined = state;
	}
	public void showTipMessage(String text){
		if (mainActivity != null) {
			NumpadFragment fragment = mainActivity.getNumpadFragment();
			HomeFragment fragment2=mainActivity.getHomeFragment();
			try {
				if (fragment != null) {
					if(text!=null){fragment.showTips(text);}
				}
			}catch(Exception e){e.printStackTrace();}
		}
	}
	public int answerSessionCall(Line sessionLine, boolean videoCall){
		long seesionId = sessionLine.getSessionId();
		int rt = PortSipErrorcode.INVALID_SESSION_ID;
		if(seesionId != PortSipErrorcode.INVALID_SESSION_ID) {
			rt = sdk.answerCall(sessionLine.getSessionId(), videoCall);
		}
		if(rt == 0){
			sessionLine.setSessionState(true);
			setCurrentLine(sessionLine);
			if(videoCall) {
				sessionLine.setVideoState(true);
			}else{
				sessionLine.setVideoState(false);
			}
			updateSessionVideo();
			if (conference) {
				sdk.joinToConference(sessionLine.getSessionId());
			}
			showTipMessage(sessionLine.getLineName()
					+ ": Call established");
		}else{
			sessionLine.reset();
			showTipMessage(sessionLine.getLineName()
					+ ": failed to answer call !");
			Log.d(TAG,"Failed to answer the call");
		}
		return rt;
	}
	public void updateSessionVideo(){
		if( mainActivity != null) {
			VideoCallFragment fragment = mainActivity.getVideoCallFragment();
			if (fragment != null) {
				fragment.updateVideo();
			}
		}
	}
	//register event listener
	@Override
	public void onRegisterSuccess(String reason, int code) {
		_SIPLogined = true;
		if (mainActivity != null) {
			HomeFragment fragment;
			fragment = mainActivity.getHomeFragment();
			if (fragment != null) {
				fragment.onRegisterSuccess(reason, code);
			}
		}
		Log.d(TAG,"State: Registered on Sip Server"+reason);
	}
	@Override
	public void onRegisterFailure(String reason, int code) {
		_SIPLogined = false;
		if (mainActivity != null) {
			HomeFragment fragment;
			fragment = mainActivity.getHomeFragment();
			if (fragment != null) {
				fragment.onRegisterFailure(reason, code);
			}
		}
		Log.d(TAG, "State: Unregistered from Sip Server" + reason);

	}
	//public KeyguardManager.KeyguardLock newKeyguardLock (String tag);
	public void notifyincome(){
		final NotificationCompat.Builder notif = new android.support.v7.app.NotificationCompat.Builder(getApplicationContext())
				.setContentTitle(getString(R.string.app_name))
				.setContentText("Incomging Call")
				//      .setTicker(getString(R.string.tick)) removed, seems to not show at all
				//      .setWhen(System.currentTimeMillis()) removed, match default
				//      .setContentIntent(contentIntent) removed, I don't neet it
				.setColor(R.color.wallet_holo_blue_light)//ok
				.setSmallIcon(R.drawable.it_icon) //ok
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.it_icon))
				.setAutoCancel(true)
				//.setDefaults(Notification.DEFAULT_VIBRATE)
				//      .setCategory(Notification.CATEGORY_CALL) does not seem to make a difference
				.setPriority(Notification.PRIORITY_HIGH); //does not seem to make a difference
		//      .setVisibility(Notification.VISIBILITY_PRIVATE); //does not seem to make a difference
		Intent notificationIntent;
		try {
			notificationIntent = new Intent(this, MainActivity.class);
		}catch (Exception e){
			notificationIntent = new Intent(this, HomeTabActivity.class);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notif.setContentIntent(contentIntent);
		// Add as notification
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(FM_NOTIFICATION_ID_2, notif.build());
		Intent broadIntent = new Intent(SESSION_CHANG);
		broadIntent.putExtra("description", "Incoming Call");
		sendBroadcast(broadIntent);
	}
	// call event listener
	@Override
    public void onInviteIncoming(long sessionId, final String callerDisplayName,String caller, final String calleeDisplayName,String callee,
								 String audioCodecs, String videoCodecs, boolean existsAudio,
								 boolean existsVideo) {
		final Line tempSession = findIdleLine();
		if (tempSession == null)// all sessions busy
		{
			sdk.rejectCall(sessionId, 486);
			Log.d(TAG, "Busy Busy");
			return;
		} else {
			tempSession.setRecvCallState(true);
		}
		//Add wake lock here
		KeyguardManager kgm = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		boolean isKeyguardUp = kgm.inKeyguardRestrictedInputMode();
		KeyguardManager.KeyguardLock kgl = kgm.newKeyguardLock("PortSipService");
		if(isKeyguardUp){
			kgl.disableKeyguard();
			isKeyguardUp = false;
		}
		//acquirewakeLock();
		WakeLocker wakeLocker= new WakeLocker() {
			@Override
			protected Object clone() throws CloneNotSupportedException {
				return super.clone();
			}
		};
		wakeLocker.acquire(this.getApplicationContext());
		Intent intent = new Intent(this, ScreenIncomming.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1222222, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		//TextView mTimer = null;
		//mTimer.setText("5 sec");
		//if (mTimer.getText().toString().trim().equalsIgnoreCase("5 sec")) {
		int i = 5;
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
		//	Toast.makeText(this, "Fake call scheduled after " + i + " sec",Toast.LENGTH_LONG).show();
		//} // this is the toast make function
		//else if (mTimer.getText().toString().trim().equalsIgnoreCase("10 sec")) {
		//	int i = 10;
		//	alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (i * 1000), pendingIntent);
		//	Toast.makeText(this, "Fake call scheduled after " + i + " sec",Toast.LENGTH_LONG).show();
		//}
		if (existsVideo) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.

			if (sdk.isAudioCodecEmpty()) {
				Log.d(TAG,"________________EMPTY Codec in incoming call");
				sdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
			}
		}
		tempSession.setSessionId(sessionId);
		tempSession.setVideoState(existsVideo);
		String comingCallTips = " Incoming Call \n\n\n   " + callerDisplayName;// + "<" + caller +">";
		tempSession.setDescriptionString(comingCallTips);
		sendSessionChangeMessage(comingCallTips, SESSION_CHANG);
		setCurrentLine(tempSession);
		/*if(existsVideo){
			updateSessionVideo();
			final Line curSession = tempSession;
			AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
			alertDialog.setTitle("Incoming Video Call");
			alertDialog.setMessage(comingCallTips);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Audio",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//Answer Audio call
							answerSessionCall(curSession,false);
						}
					});
			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Video",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//Answer Video call
							answerSessionCall(curSession,true);
						}
					});
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Reject",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//Reject call
							if(curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
								sdk.rejectCall(curSession.getSessionId(), 486);
							}
							curSession.reset();
							showTipMessage("Rejected call");
						}
					});
			// Showing Alert Message
			alertDialog.show();
		}
		else {*///Audiocall
		notifyincome();
		try {
			Intent startActivity = new Intent();
			startActivity.setClass(this.getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pi = PendingIntent.getActivity(this, 0, startActivity, 0);
			pi.send(this, 0, null);
			//alertDialog2.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Line curSession = tempSession;
		String incomingnumber;
		incomingnumber = caller;
		String mycaller = incomingnumber.toString();
		String s = mycaller;
		final String requiredString = s.substring(s.indexOf(":") + 1, s.indexOf("@"));
		Log.d(TAG, "=====>The incoming number is:" + requiredString);
		sessionManager= new SessionManager(this);
		try {
			notification2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification2);
			r.play();
			// Get instance of Vibrator from current Context
			vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			if(sessionManager.isvibraterOk()){
				long[] pattern = {0, 1000, 1000};
				vib.vibrate(pattern, 0);
				Log.d(TAG,"Vibrator is on");
			}
			else{
				Log.d(TAG,"Vibrator is off");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//AlertDialog alertDialog;
		try {
			alertDialog2 = new AlertDialog.Builder(mainActivity).create();
			alertDialog2.setTitle("Incoming Call");
			Log.d(TAG, "Incomming caller id" + comingCallTips.toString());
			alertDialog2.setMessage(comingCallTips);
			alertDialog2.show();
			callerId=requiredString;// copy the call infor if the user hangup before answered
			callerName=callerDisplayName;
			final Window win = alertDialog2.getWindow();
			win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
			win.setContentView(R.layout.incoming_call);
			//Answer
			TextView txtView = (TextView) win.findViewById(R.id.txtName);
			txtView.setText(comingCallTips);
			Button accept_btn = (Button) win.findViewById(R.id.answer);
			accept_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					answerSessionCall(curSession, false);
					//insertPlaceholderCall(getContentResolver(), requiredString, calleeDisplayName);
					status = "AnseweredIncome";
					counter = 0;
					showTipMessage("Secure Connected");
					beginConnected(requiredString, callerDisplayName, "Secure Connected");
					dialogFlag = true;
					//answered_status="answered";// if nothing to be shown show this one
					r.stop();
					if(vib.hasVibrator()) {
						vib.cancel();
					}
					alertDialog2.dismiss();
					//win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
				}
			});
			//Reject
			Button reject_btn = (Button) win.findViewById(R.id.reject);
			reject_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					sdk.rejectCall(curSession.getSessionId(), 486);
					curSession.reset();
					showTipMessage("Rejected call");
					insertPlaceholderCallMissed(getContentResolver(), requiredString, callerDisplayName);
					status = "RejectedIncome";
					counter = 0;
					//rejected_status="rejected";
					r.stop();
					if(vib.hasVibrator()) {
						vib.cancel();
					}
					alertDialog2.dismiss();
				}
			});
			alertDialog2.setOnKeyListener(new AlertDialog.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						//finish();
						Log.d(TAG, "Back Pressed");
						alertDialog2.show();
						return true;
						//dialog.dismiss();
					} else if (keyCode == KeyEvent.KEYCODE_HOME) {
						alertDialog2.show();
						return false;
					}
					return true;
				}
			});

			// the incoming call(incoming tone);
		} catch (Exception e) {
			showTipMessage("Incomming call");
			if (hometabActivity != null) {
				alertDialog2 = new AlertDialog.Builder(hometabActivity).create();
				alertDialog2.setTitle("Incoming Call");
				Log.d(TAG, "Incomming caller id" + comingCallTips.toString());
				alertDialog2.setMessage(comingCallTips);
				alertDialog2.show();
				Window win = alertDialog2.getWindow();
				win.setContentView(R.layout.incoming_call);
				//Answer//
				TextView txtView = (TextView) win.findViewById(R.id.txtName);
				txtView.setText(comingCallTips);
				Button accept_btn = (Button) win.findViewById(R.id.answer);
				accept_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						answerSessionCall(curSession, false);
						//insertPlaceholderCall(getContentResolver(), requiredString, calleeDisplayName);
						status = "AnseweredIncome";
						counter = 0;
						beginConnected(requiredString, calleeDisplayName, "Secure Connected");
						dialogFlag = true;
						//answered_status="answered";
						r.stop();
						if(vib.hasVibrator()) {
							vib.cancel();
						}
						alertDialog2.dismiss();
					}
				});
				//Reject
				Button reject_btn = (Button) win.findViewById(R.id.reject);
				reject_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						sdk.rejectCall(curSession.getSessionId(), 486);
						curSession.reset();
						status = "RejectedIncome";
						counter = 0;
						//rejected_status="rejected";
						insertPlaceholderCallMissed(getContentResolver(), requiredString, callerDisplayName);
						showTipMessage("Rejected call");
						r.stop();

						if(vib.hasVibrator()) {
							vib.cancel();
						}
						alertDialog2.dismiss();
					}
				});
				alertDialog2.setOnKeyListener(new AlertDialog.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							//finish();
							Log.d(TAG, "Back Pressed");
							alertDialog2.show();
							return true;
							//dialog.dismiss();
						} else if (keyCode == KeyEvent.KEYCODE_HOME) {
							alertDialog2.show();
							return false;
						}
						return true;
					}
				});
			} else {
				Log.d(TAG, "HOME ACTIVITY IS ALSO NULL");
			}
		}

		Log.d(TAG, "-----------Incomgin call number is---------------" + comingCallTips);
		//bringToFront(requiredString,calleeDisplayName,"Secure Connected");
		//Toast.makeText(this, "Incoming Call"+comingCallTips, Toast.LENGTH_LONG).show();

		Log.d(TAG,"State: Incoming Call");
	}
	public static void insertPlaceholderCallMissed(ContentResolver contentResolver, String number,String displayName){
		ContentValues values = new ContentValues();
		values.put(CallLog.Calls.NUMBER, number);
		values.put(CallLog.Calls.DATE, System.currentTimeMillis());
		values.put(CallLog.Calls.DURATION, 0);
		values.put(CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);
		values.put(CallLog.Calls.NEW, 1);
		values.put(CallLog.Calls.CACHED_NAME, displayName + "");
		values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
		values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
		Log.d(TAG, "Inserting call log placeholder for " + number);
		contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
	}
	//@Override
	@Override
	public void onInviteTrying(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Call is trying...");
		sendSessionChangeMessage("Call is trying...", SESSION_CHANG);

		status="calltrying";Log.d(TAG, "status: " + status);
		Calendar c2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
		String msgAt2 = sdf2.format(c2.getTime());
		Log.d(TAG, "First Start calling at §§§ : " + msgAt2);
		//Toast.makeText(getApplicationContext(),"Call is trying",Toast.LENGTH_LONG).show();
		//Utils.playTone(this, ToneGenerator.TONE_SUP_DIAL);
		Log.d(TAG, "State: Call is trying");
	}

	@Override
	public void onInviteSessionProgress(long sessionId, String audioCodecs,
										String videoCodecs, boolean existsEarlyMedia, boolean existsAudio,
										boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		if (existsVideo) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		tempSession.setSessionState(true);
		tempSession.setDescriptionString("Call session progress.");
		sendSessionChangeMessage("Call session progress.", SESSION_CHANG);
		tempSession.setEarlyMeida(existsEarlyMedia);
		//Log.d(TAG, "Call session on progress");
		Log.d(TAG,"State: Call session on progress");
		Toast.makeText(getApplicationContext(),"Call on progress",Toast.LENGTH_LONG).show();
	}

	@Override
	public void onInviteRinging(long sessionId, String statusText,
								int statusCode) {
		Line tempSession = findLineBySessionId(sessionId);
		Log.d(TAG, "?????????????? Ringing.....");
		if (tempSession == null) {
			return;
		}
		if (!tempSession.hasEarlyMeida()) {
			// Hasn't the early media, you must play the local WAVE file for
			Log.d(TAG,"Play media file");
			// Ringing tone
		}
		tempSession.setDescriptionString("Ringing...");
		sendSessionChangeMessage("Ringing...", SESSION_CHANG);
		Log.d(TAG, "State: Call Ringing");
		//Toast.makeText(getApplicationContext(),"Ringing...",Toast.LENGTH_SHORT).show();
		//call it like this from your activity' any method
	}

	private volatile Thread runner;
	public synchronized void startThread(){
		if(runner == null){
			runner = new Thread();
			runner.start();
		}
	}
	public synchronized void stopThread(Thread thread){
		runner=thread;
		if(runner != null){
			Thread moribund = runner;
			runner = null;
			moribund.interrupt();
			Log.d(TAG, "!!! Interupted ....\n\n\n\n");
		}
		Log.d(TAG,"!!! Interupted ....\n\n\n\n");
	}
	public void run(){
		while(Thread.currentThread() == runner){
			//do stuff which can be interrupted if necessary
			Log.d(TAG,"!!! Continue in action....");
		}
	}
	public int playAlertTone(final Context context,boolean mybool){
		Log.d(TAG,"!!! Start beep");
			t = new Thread() {
				public void run() {
						int countBeep = 0;
					Log.d(TAG,"!!! ... in prog");
						while (countBeep < 7&&status=="callout") {
							player = MediaPlayer.create(context, R.raw.outcall);
							if (status == "callout") {
								player.start();
								countBeep += 1;
								Log.d(TAG,"!!! Continue beep in loop"+countBeep);
								try {
									//100 milisecond is duration gap between two beep
									Thread.sleep(player.getDuration() + 100);
									player.release();
								} catch (InterruptedException e) {
									//TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if(status=="callout"&&status2!="AnsweredByRemote"){
								displayText(getConnectedCall(), R.id.textViewconnected, "Secure Connected");
								Log.d(TAG, "!!! intrupted beep in second loop");
								player.stop();
								player.reset();
								t.interrupt();
								stopThread(this);
								//bringToFront();
								return;
							}else{
								displayText(getConnectedCall(),R.id.textViewconnected,"Secure Connected");
								Log.d(TAG, "!!! interupted beep in else cond");
								player.stop();
								player.reset();
								t.interrupt();
								stopThread(this);
								Log.d(TAG, "2. =============INTRUPPTED Answered by remote");//Answered by remote
								return;
							}
							Log.d(TAG,"!!! in loop progress after beep");
						}
					displayText(getConnectedCall(),R.id.textViewconnected,"Secure Connected");
					Log.d(TAG,"!!! out of loop");
				    Intent startActivity = new Intent();
					startActivity.setClass(context.getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(),0,startActivity,0);
					try {
						pi.send(context.getApplicationContext(), 0,null);
					} catch (CanceledException e) {
						e.printStackTrace();
					}
				}
			};t.start();
		//displayText(getConnectedCall(), R.id.textViewconnected, "Secure Connected");
		return playToneC;
	}
	public static void displayText(Activity activity, int id, String text) {
		try {
			TextView tv = (TextView) activity.findViewById(id);
			tv.setText(text);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInviteAnswered(long sessionId, String callerDisplayName, String caller,
								 String calleeDisplayName, String callee,
								 String audioCodecs, String videoCodecs, boolean existsAudio,
								 boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		if (existsVideo) {
			//Do nothing for the time being on version 1.0.1 SecureCall
			sdk.sendVideo(tempSession.getSessionId(), true);
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// sdk.addAudioCodec("g.729#GSM#AMR#H264#H263");
			/*if(sdk.isAudioCodecEmpty()) {
				sdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
			}*/
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.

		  /*NSArray *components=[strAudiocodec componentsSeparatedByString:@"#"];
			NSString *codec1 -  [components objectAtIndex:0] ；
			if ([codec1 isEqualToString: @"AMR"]) {
				sdk.addAudioCodec(AUDIOCODEC_AMR);//You should add audio codec before call incoming
			}*/
			if(sdk.isAudioCodecEmpty()){
				Log.d(TAG,"1. ____________________AUDIO IS EMPRTY:______________________");
			}
		}
		//int res=playAlertTone(this,true);
		tempSession.setVideoState(existsVideo);
		tempSession.setSessionState(true);
		tempSession.setDescriptionString("Call established");
		setStatusFlag(true);// true the calling state
		sendSessionChangeMessage("Call established", SESSION_CHANG);
		if (isConference()) {
			sdk.joinToConference(tempSession.getSessionId());
			tempSession.setHoldState(false);
		}
		// If this is the refer call then need set it to normal
		if (tempSession.isReferCall()) {
			tempSession.setReferCall(false, 0);
		}
		Log.d(TAG, "---------------------My Application Connected" + caller + "\n"
				+ callerDisplayName + "\n"
				+ callee + "\n"
				+ calleeDisplayName);
		status="callout";
		Log.d(TAG,"status: "+status);
		Log.d(TAG, "===================Call is connected:======================");
		counter=0;
		Calendar c2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
		String msgAt2 = sdf2.format(c2.getTime());
		Log.d(TAG, "calling at §§§ : " + msgAt2);
		Log.d(TAG, "State: Call Answered");
		//String caller, String displayName,String status
		//beginConnected(callee, calleeDisplayName, "Secure Connected");
	}
	@Override
	public void onInviteFailure(long sessionId, String reason, int code) {
		Line tempSession = findLineBySessionId(sessionId);
		Log.d(TAG, "call Rejected : " + reason);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("call failure" + reason);
		sendSessionChangeMessage("call failure" + reason, SESSION_CHANG);
		if (tempSession.isReferCall()) {
			// Take off the origin call from HOLD if the refer call is failure
			Line originSession = findLineBySessionId(tempSession
					.getOriginCallSessionId());
			if (originSession != null) {
				sdk.unHold(originSession.getSessionId());
				originSession.setHoldState(false);
				// Switch the currently line to origin call line
				setCurrentLine(originSession);
				tempSession.setDescriptionString("refer failure:" + reason
						+ "resume orignal call");
				sendSessionChangeMessage("call failure" + reason, SESSION_CHANG);
			}
		}
		Toast.makeText(this,"Call Failed",Toast.LENGTH_SHORT).show();
		tempSession.reset();
		Intent startActivity = new Intent();
		status="callfailed";
		Log.d(TAG,"status: "+status);
		//startActivity.setClass(this, HomeTabActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity.setClass(this, HomeTabActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pi = PendingIntent.getActivity(this,0,startActivity,0);
		try {
			pi.send(this, 0,null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
		Calendar c2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
		String msgAt2 = sdf2.format(c2.getTime());
		Log.d(TAG, "Call failed at §§§ : " + msgAt2);
		Log.d(TAG, "State: Call Failer");
	}
	@Override
	public void onInviteUpdated(long sessionId, String audioCodecs,
								String videoCodecs, boolean existsAudio, boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		if (existsVideo) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
			if(sdk.isAudioCodecEmpty()){

				Log.d(TAG,"2. ____________________AUDIO IS EMPRTY:______________________");
			}
		}
		Log.d(TAG, "UPDATED CALL");
		displayText(getConnectedCall(), R.id.textViewconnected, "Secure Connected");
		if(status!="callout"){
			Log.d(TAG,"counter is 1");
			Intent startActivity = new Intent();
			startActivity.setClass(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pi = PendingIntent.getActivity(this,0,startActivity,0);
			try {
				pi.send(this, 0,null);
			} catch (CanceledException e) {
				e.printStackTrace();
			}
			return;
		}
		if(status=="InviteClosed"||status=="callfailed"){
			//Intent killintent=
			//((Activity)getContext()).finish();
			Log.d(TAG,"?????Closed call by someone");
		}
		if(tempSession.getRecvCallState()) {
			Log.d(TAG, "regRecvCallState is " + String.valueOf(tempSession.getRecvCallState()));
		}
		tempSession.setDescriptionString("Call on progress");
		Calendar c2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
		String msgAt2 = sdf2.format(c2.getTime());
		Log.d(TAG, "Updated at §§§ : " + msgAt2);
		//Toast.makeText(this,"Ansewered",Toast.LENGTH_SHORT).show();
		setStatusFlag(false);
		status="AnsweredByRemote";
		Log.d(TAG,"status: "+status);
		Toast.makeText(this.getApplicationContext(),"Ansered by remote",Toast.LENGTH_SHORT).show();
		status2=status;// place into holder
		counter+=1;//shows the dialing is answered

		if(counter>1){
			Log.d(TAG,"counter is greated that 1");//
		}
		Log.d(TAG, "State: call updated" + status + " " + counter);
	}
	@Override
	public void onInviteConnected(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			Toast.makeText(this,"tempSession is Null",Toast.LENGTH_LONG).show();
			return;

		}
		tempSession.setDescriptionString("Connected");
		sendSessionChangeMessage("Connected", SESSION_CHANG);
		updateSessionVideo();
		Log.d(TAG, "onInviteConnected !!!!!!!!!!!!!!!Connected!!!!!!!!!!!!!!! MY APPLICATION CLASS");
		Calendar c2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
		String msgAt2 = sdf2.format(c2.getTime());

		Log.d(TAG, "Connected at §§§ : " + msgAt2);
		//beginConnected("Default","Default","Secure Connected");
		Log.d(TAG,"State: call connected");
		displayText(getConnectedCall(), R.id.textViewconnected, "Secure Connected");

	}
	@Override
	public void onInviteBeginingForward(String forwardTo) {
		sendSessionChangeMessage("An incoming call was forwarded to: "
				+ forwardTo, SESSION_CHANG);
		Log.d(TAG, "State: call start forwarding");
	}
	@Override
	public void onInviteClosed(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.reset();
		updateSessionVideo();
		tempSession.setDescriptionString(": Call closed.");
		sendSessionChangeMessage(": Call closed.", SESSION_CHANG);
		status="InviteClosed";Log.d(TAG,"status: "+status);
		try {

			vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			if(vib.hasVibrator()){
				vib.cancel();
			}// only for incoming calls
			r = RingtoneManager.getRingtone(getApplicationContext(), notification2);
			if(r.isPlaying()){
				r.stop();

			}
			if(alertDialog2.isShowing()){
				alertDialog2.dismiss();
				Log.d(TAG,"CALLER INFO ARE:_ "+callerId+callerName);
				insertPlaceholderCallMissed(getContentResolver(), callerId, callerName);
			}// only for incoming calls

		}catch(Exception e){
			System.out.println("Error-------------------" + e.getMessage());
		}
		setStatusFlag(false);
		setStatus(status);Log.d(TAG, "status: " + status);
		Log.d(TAG, "State: Call closed");

		Intent intent = new Intent();
		intent.setClass(this,RecentActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		try {
			pi.send(this, 0, null);
		} catch (CanceledException e1) {
			e1.printStackTrace();
			Log.d(TAG,"--Error while loading the call is: "+e1.getMessage());
		}
		//String caller, String displayName,String status
		//beginConnected("Caller ID","Caller Name","Call Ended");
		//bringToFront();
	}
	@Override
	public void onRemoteHold(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Placed on hold by remote.");
		sendSessionChangeMessage("Placed on hold by remote.", SESSION_CHANG);
		Log.d(TAG, "Hold by remote");
		//Intent startActivity = new Intent();
		/*startActivity.setClass(this, HomeTabActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pi = PendingIntent.getActivity(this,0,startActivity,0);
		try {
			pi.send(this,0,null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}*/
		//String caller, String displayName,String status
		//beginConnected();
		Log.d(TAG,"State: call on hold state");
	}
	@Override
	public void onRemoteUnHold(long sessionId, String audioCodecs,
							   String videoCodecs, boolean existsAudio, boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Take off hold by remote.");
		sendSessionChangeMessage("Take off hold by remote.", SESSION_CHANG);
		Log.d(TAG, "State: call unholded");
	}
	@Override
	public void onRecvDtmfTone(long sessionId, int tone) {
		// TODO Auto-generated method stub
		Log.d(TAG,"DTMF ");
		/*try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	@Override
	public void onReceivedRefer(long sessionId, final long referId, String to,
								String from, final String referSipMessage) {
		final Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			sdk.rejectRefer(referId);
			return;
		}
		final Line referSession = findIdleLine();
		if (referSession == null)// all sessions busy
		{
			sdk.rejectRefer(referId);
			return;
		} else {
			referSession.setSessionState(true);
		}
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_NEGATIVE: {

						sdk.rejectRefer(referId);
						referSession.reset();
					}
					break;
					case DialogInterface.BUTTON_POSITIVE: {

						sdk.hold(tempSession.getSessionId());// hold current session
						tempSession.setHoldState(true);

						tempSession
								.setDescriptionString("Place currently call on hold on line: ");
						long referSessionId = sdk.acceptRefer(referId,
								referSipMessage);
						if (referSessionId <= 0) {
							referSession
									.setDescriptionString("Failed to accept REFER on line");

							referSession.reset();
							// Take off hold
							sdk.unHold(tempSession.getSessionId());
							tempSession.setHoldState(false);
						} else {
							referSession.setSessionId(referSessionId);
							referSession.setSessionState(true);
							referSession.setReferCall(true,
									tempSession.getSessionId());

							referSession
									.setDescriptionString("Accepted the refer, new call is trying on line ");

							_CurrentlyLine = referSession;

							tempSession
									.setDescriptionString("Now current line is set to: "
											+ _CurrentlyLine.getLineName());
							updateSessionVideo();
						}
					}
				}

			}
		};
		showGloableDialog("Received REFER", "accept", listener, "reject",
				listener);
		Log.d(TAG, "State: on Refer Receive");
	}
	@Override
	public void onReferAccepted(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("the REFER was accepted.");
		sendSessionChangeMessage("the REFER was accepted.", SESSION_CHANG);
		Log.d(TAG,"State: on refer accepted");
	}
	@Override
	public void onReferRejected(long sessionId, String reason, int code) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		Log.d(TAG,"---------------_______--------------Refer Rjected");
		tempSession.setDescriptionString("the REFER was rejected.");
		sendSessionChangeMessage("the REFER was rejected.", SESSION_CHANG);
		Log.d(TAG, "State: on refer Rejected");
	}
	@Override
	public void onTransferTrying(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Transfer Trying.");
		sendSessionChangeMessage("Transfer Trying.", SESSION_CHANG);
		Log.d(TAG, "State: on transfre trying");
	}
	@Override
	public void onTransferRinging(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Transfer Ringing.");
		sendSessionChangeMessage("Transfer Ringing.", SESSION_CHANG);
		Log.d(TAG, "State: on transfer ringing");
	}

	@Override
	public void onACTVTransferSuccess(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Transfer succeeded.");
	}

	@Override
	public void onACTVTransferFailure(long sessionId, String reason, int code) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Transfer failure");

		// reason is error reason
		// code is error code

	}

	public Line findLineBySessionId(long sessionId) {
		for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
			if (_CallSessions[i].getSessionId() == sessionId) {
				return _CallSessions[i];
			}
		}

		return null;
	}

	public Line findSessionByIndex(int index) {

		if (Line.LINE_BASE <= index && index < Line.MAX_LINES) {
			return _CallSessions[index];
		}

		return null;
	}

	static Line findIdleLine() {
		/*for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i)// get idle session
		{
			if (!_CallSessions[i].getSessionState()
					&& !_CallSessions[i].getRecvCallState()) {
				return _CallSessions[i];
			}
		}*/
		int i = Line.LINE_BASE;
		if (!_CallSessions[i].getSessionState()
				&& !_CallSessions[i].getRecvCallState()) {
			return _CallSessions[i];
		}
		return null;
	}
	public void setCurrentLine(Line line) {
		if (line == null) {
			_CurrentlyLine = _CallSessions[Line.LINE_BASE];
		} else {
			_CurrentlyLine = line;
		}
	}

	public Session getCurrentSession() {
		return _CurrentlyLine;
	}

	@Override
	public void onReceivedSignaling(long sessionId, String message) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onSendingSignaling(long sessionId, String message) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onWaitingVoiceMessage(String messageAccount,
									  int urgentNewMessageCount, int urgentOldMessageCount,
									  int newMessageCount, int oldMessageCount) {
		String text = messageAccount;
		text += " has voice message.";

		showMessage(text);
		// You can use these parameters to check the voice message count

		// urgentNewMessageCount;
		// urgentOldMessageCount;
		// newMessageCount;
		// oldMessageCount;

	}

	@Override
	public void onWaitingFaxMessage(String messageAccount,
									int urgentNewMessageCount, int urgentOldMessageCount,
									int newMessageCount, int oldMessageCount) {
		String text = messageAccount;
		text += " has FAX message.";

		showMessage(text);
		// You can use these parameters to check the FAX message count

		// urgentNewMessageCount;
		// urgentOldMessageCount;
		// newMessageCount;
		// oldMessageCount;

	}

	@Override
	public void onPresenceRecvSubscribe(long subscribeId,
										String fromDisplayName, String from, String subject) {

		String fromSipUri = "sip:" + from;
		final long tempId = subscribeId;
		DialogInterface.OnClickListener onClick;
		SipContact contactRefrence = null;
		boolean contactExist = false;
		for (int i = 0; i < contacts.size(); ++i) {
			contactRefrence = contacts.get(i);
			String SipUri = contactRefrence.getSipAddr();

			if (SipUri.equals(fromSipUri)) {
				contactExist = true;
				if (contactRefrence.isAccept()) {
					long nOldSubscribID = contactRefrence.getSubId();
					sdk.presenceAcceptSubscribe(tempId);
					String status = "Available";Log.d(TAG,"status: "+status);
					sdk.presenceOnline(tempId, status);
					if (contactRefrence.isSubscribed() && nOldSubscribID >= 0) {
						sdk.presenceSubscribeContact(fromSipUri, subject);
					}
					return;
				} else {
					break;
				}
			}
		}
		//
		if (!contactExist) {
			contactRefrence = new SipContact();
			contacts.add(contactRefrence);
			contactRefrence.setSipAddr(fromSipUri);
		}
		final SipContact contact = contactRefrence;
		onClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						sdk.presenceAcceptSubscribe(tempId);
						contact.setSubId(tempId);
						contact.setAccept(true);
						String status = "Available";
						sdk.presenceOnline(tempId, status);
						contact.setSubstatus(true);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						contact.setAccept(false);// reject subscribe
						contact.setSubId(0);
						contact.setSubstatus(false);// offline

						sdk.presenceRejectSubscribe(tempId);
						break;
					default:
						break;
				}
				dialog.dismiss();
			}
		};
		showGloableDialog(from, "Accept", onClick, "Reject", onClick);
	}
	@Override
	public void onPresenceOnline(String fromDisplayName, String from,
								 String stateText) {
		String fromSipUri = "sip:" + from;
		SipContact contactRefernce;
		for (int i = 0; i < contacts.size(); ++i) {
			contactRefernce = contacts.get(i);
			String SipUri = contactRefernce.getSipAddr();
			if (SipUri.endsWith(fromSipUri)) {
				contactRefernce.setSubDescription(stateText);
				contactRefernce.setSubstatus(true);// online
			}
		}
		sendSessionChangeMessage("contact status change.", CONTACT_CHANG);
	}
	@Override
	public void onPresenceOffline(String fromDisplayName, String from) {
		String fromSipUri = "sip:" + from;
		SipContact contactRefernce;
		for (int i = 0; i < contacts.size(); ++i) {
			contactRefernce = contacts.get(i);
			String SipUri = contactRefernce.getSipAddr();
			if (SipUri.endsWith(fromSipUri)) {
				contactRefernce.setSubstatus(false);// "Offline";
				contactRefernce.setSubId(0);
			}
		}
		sendSessionChangeMessage("contact status change.", CONTACT_CHANG);
	}

	@Override
	public void onRecvOptions(String optionsMessage) {
		// String text = "Received an OPTIONS message: ";
		// text += optionsMessage.toString();
		// showTips(text);
	}

	@Override
	public void onRecvInfo(String infoMessage) {
		String text = "Received a INFO message: ";
		text += infoMessage.toString();
		Log.d(TAG, "Info received");
		// showTips(text);
	}

	@Override
	public void onRecvMessage(long sessionId, String mimeType,
							  String subMimeType, byte[] messageData, int messageDataLength) {
		Log.d(TAG,"State: on Receiving message");
	}
	@Override
	public void onRecvOutOfDialogMessage(String fromDisplayName, String from,
										 String toDisplayName, String to, String mimeType,
										 String subMimeType, byte[] messageData, int messageDataLength) {
		String text = "Received a " + mimeType + "message from ";
		String msg= messageData.toString();
		Log.d(TAG,":::: sjekke meldingen her"+msg
				+"\n Sender will be: "+from);
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(400);
		String[] parts=from.split("@");
		String requiredString = parts[0].trim();//=from.substring(from.indexOf(""), from.indexOf("@"));
		Log.d(TAG,"required String is: "+requiredString);
		// This is text messaging
		String receivedMsg = new String(messageData);
		text += requiredString;
		SessionManager session= new SessionManager(this);
		// Generate for the message ID
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();

		} catch (Exception e) {
			e.printStackTrace();
		}
		int min = 10;
		int max = 99;
		Random r = new Random();
		int i1 = r.nextInt(max - min + 1) + min;
		//String temp="";
		//if(requiredString.equals("asterisk")){
		//	session.setSmsInf(fromDisplayName,temp, mimeType, receivedMsg, String.valueOf(i1), true);
		//}else{
			session.setSmsInf(fromDisplayName,requiredString, mimeType, receivedMsg, String.valueOf(i1), true);
		//}

		if (mimeType.equals("text") && subMimeType.equals("plain")) {
			smsNotify("SMS",SESSION_CHANG,fromDisplayName,requiredString,receivedMsg);
			//showMessage(text + "  \n" + receivedMsg);
		} else if (mimeType.equals("application")
				&& subMimeType.equals("vnd.3gpp.sms")) {
			// The messageData is binary data
			showMessage(text);
		} else if (mimeType.equals("application")
				&& subMimeType.equals("vnd.3gpp2.sms")) {
			// The messageData is binary data
			showMessage(text);
			// Test 
		}
	}

	public void smsNotify(String message, String action,final String from,final String number,final String Income_message) {
		//Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		/*
		*
	*
	Notification notification = new Notification(icon, tickerText, when);
	notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
	// Cancel the notification after its selected
	notification.flags |= Notification.FLAG_AUTO_CANCEL;
		* */
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.stat_notify_sms)
						.setContentTitle("SecureCall")
						.setContentText(from+" "+Income_message)
				.setAutoCancel(true);//.setSound(soundUri)	;
		Intent notificationIntent = new Intent(this, SendSms.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	try {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		// Add as notification
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(FM_NOTIFICATION_ID_SMS, builder.build());
		Intent broadIntent = new Intent(action);
		broadIntent.putExtra("description", message);
		sendBroadcast(broadIntent);
		}catch (Exception e){
			Log.d(TAG,"CAn't open sms"+e.getMessage());
		}
	}
	@Override
	public void onPlayAudioFileFinished(long sessionId, String fileName) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onPlayVideoFileFinished(long sessionId) {
		// TODO Auto-generated method stub
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	public void setConnetedCAll(ConnectedCall connectedCall) {
		this.connectedCall = connectedCall;
	}
	public ConnectedCall getConnectedCall(){
		return this.connectedCall;
	}
	public void setHometabActivity(HomeTabActivity hometabActivity){
		this.hometabActivity=hometabActivity;
	}
	public void setSmsActivity(SendSms sendSms){
		this.sendSms=sendSms;
	}
	@Override
	public void onSendMessageSuccess(long sessionId, long messageId) {
		// TODO Auto-generated method stub
		Toast.makeText(this.getApplicationContext(),"Delivered",Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onSendMessageFailure(long sessionId, long messageId,
									 String reason, int code) {
		// TODO Auto-generated method stub
		Log.d(TAG, "State: Message sending failed");

	}
	@Override
	public void onSendOutOfDialogMessageSuccess(long messageId,
												String fromDisplayName, String from, String toDisplayName, String to) {
		Log.d(TAG, "State: Message delivered");
		Toast.makeText(this.getApplicationContext(),"Delivered",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSendOutOfDialogMessageFailure(long messageId,
												String fromDisplayName, String from, String toDisplayName,
												String to, String reason, int code) {
		Toast.makeText(this,"Failed to send message",Toast.LENGTH_SHORT).show();
		Log.d(TAG, "Failed to send the message to " + to + " And the reason is: " + reason);
	}

	@Override
	public void onReceivedRTPPacket(long sessionId, boolean isAudio,
									byte[] RTPPacket, int packetSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendingRTPPacket(long sessionId, boolean isAudio,
								   byte[] RTPPacket, int packetSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAudioRawCallback(long sessionId, int enum_audioCallbackMode,
								   byte[] data, int dataLength, int samplingFreqHz) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onVideoRawCallback(long sessionId, int enum_videoCallbackMode,
								   int width, int height, byte[] data, int dataLength) {
		// TODO Auto-generated method stub

	}
	//@Override
	//public void onBackPressed
	void showMessage(String message) {
		OnClickListener listener = null;
		final String msg= message;
		showGloableDialog(message, "", listener, "Cancel",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d(TAG, "Breakpoint");
						dialog.dismiss();
					}
				});
	}
	void showGloableDialog(String message, String strPositive,
						   OnClickListener positiveListener, String strNegative,
						   OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		if (positiveListener != null) {
			builder.setPositiveButton(strPositive, positiveListener);
			Log.d(TAG,"The message is "+message);
		}
		if (negativeListener != null) {
			builder.setNegativeButton(strNegative, negativeListener);
			Log.d(TAG, "We got the message here" + message);
		}
		//Intent intent= new Intent(this,MessageFragment.class);
		//getApplicationContext().StartActivity().class;
		// Asset By Itman
		AlertDialog ad = builder.create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false);
		ad.show();
	}
	public void myRejectcall(){
		/*Intent intent= new Intent(this,chooseservice.class);
		startActivity(intent);*/
	}
	public void beginConnected(String caller, String displayName,String status){
		//String whatyouaresearching = myString.subString(0,myString.lastIndexOf("/"))
		//SessionManager sessionManager= new SessionManager(this);
		//sessionManager.setDestName(displayName);
		setGName(displayName);
		setGPhone(caller);
		setInCallFlag(true);
		setDialogFlag(dialogFlag);
		//answered=false;// flag false the incoming dialog accept
		//rejected=false;// flag false for the incoming dialog reject
		try {
			Intent startActivity = new Intent();
			startActivity.setClass(this, ConnectedCall.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
					.putExtra("value", caller)
					.putExtra("nameValue", displayName)
					.putExtra("incallFlag", true)
					.putExtra("status", status);
			PendingIntent pi = PendingIntent.getActivity(this,0,startActivity,0);
			pi.send(this,0,null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
		//bringToFront();
	}
	public void bringToFront(){
		//dialogFlag=true;
		try {
			Intent startActivity = new Intent();
			startActivity.setClass(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK)
					.putExtra("mybool",dialogFlag);
			PendingIntent pi = PendingIntent.getActivity(this,0,startActivity,0);
			pi.send(this,0,null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	public static synchronized MyApplication getInstance() {
		return mInstance;
	}
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}
	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	public ArrayList<String> getPhone_list() {
		return phone_list;
	}
	public void setPhone_list(ArrayList<String> someVariable) {
		//addValues.add(someVariable);
		this.phone_list = someVariable;
	}
	public static ArrayList<String> getUserDetail() {
		return userDetail;
	}
	public void setUserDetail(ArrayList<String> someVariable) {
		userDetail=new ArrayList<String>();
		//addValues.add(someVariable);
		this.userDetail = someVariable;
	}
	public String getGName(){
		return gName;
	}
	public void setGName(String name){
		this.gName=name;
	}

	public String getGPhone(){
		return gPhone;
	}
	public void setGPhone(String phone){
		this.gPhone=phone;
	}
	public String getGPassword(){
		return gPassword;
	}
	public void setGPassword(String password){
		this.gPassword=password;
	}
	public String getGEmail(){
		return gEmail;
	}
	public void setGEmail(String email){
		this.gEmail=email;
	}
	private void setInCallFlag(boolean flag) {
		this.gFlag=flag;
	}
	public boolean getInCallFlag(){
		return gFlag;
	}
	private void setDialogFlag(boolean flag) {
		this.gDialogFlag=flag;
	}
	public boolean getDialogFlag(){
		return gDialogFlag;
	}
	public String getStatusString(){
		return status;
	}
	public void setStatus(String mystatus){
		this.status=mystatus;

	}
	public enum Profil {
		ALWAYS,
		WIFI,
	}
	public static Profil globalwifi;
	public static Profil globalAlways;
	public void setGlobalWifi(Profil mode){
		this.globalwifi=mode;
	}
	public Profil getGlobalWifi(){return globalwifi;}
	public void setGlobalAlways(Profil mode){
		this.globalAlways=mode;
	}
	public Profil getGlobalAlways(){return globalAlways;}

}

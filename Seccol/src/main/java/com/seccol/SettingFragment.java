package com.portsip;

import com.portsip.main.LoginActivity;
import com.portsip.util.PreferenceFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import helper.SessionManager;


public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	PortSipSdk mSipSdk;
	PreferenceManager mprefmamager;
	SharedPreferences mpreferences;
	boolean changed = true;
	Context context = null;
	Preference preference;
	MyApplication myapp;
	private static final String ONWIFI="OnWifi";
	private static final String ONALWAYS="OnAlways";
	private static final String WIFI="Wi-Fi";
	private static final String GSM="gsm";//2g
	private static final String THREEG="3g";
	private static final String FOURG="4g";
	private static final String ANY = "Any";//This can include all the above
	private static final String KEY_VIB="vib";
	private static final String KEY_PHONE="phoneb";
	private static boolean isvib;
	private static boolean isphoneB;
	// The user's current network preference setting.
	public static String sPref = null;
	// The BroadcastReceiver that tracks network connectivity changes.
	//private NetworkReceiver receiver = new NetworkReceiver();
	private NetworkReceiver receiver = new NetworkReceiver();
	public static final String KEY_LIST_PREFERENCE = "listPref";
	private ListPreference mListPreference;

	private CheckBox globIntegrate;
	public static String globProfileAlways;
	public static String globProfileWifi;
	private CheckBox globGsm;
	private boolean vibrate;
	private boolean phoneOk;

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		try {
			if (key.equals(KEY_LIST_PREFERENCE)) {
				mListPreference.setSummary("Current value is " + mListPreference.getEntry().toString());
			} else if (key.equals(getString(R.string.vibrator))) {
				if(sharedPreferences.getBoolean(getString(R.string.vibrator),false)){
					setVibrate(false); Log.d("SettingFragment","setVib false");
				}else{
					setVibrate(true); Log.d("SettingFragment", "setVib true");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.media_set);
		context = getActivity();
		mSipSdk = ((MyApplication) getActivity().getApplicationContext())
				.getPortSIPSDK();
		//mprefmamager.setSharedPreferencesMode();
		myapp= (MyApplication) getActivity().getApplication();
		mprefmamager = getPreferenceManager();
		mpreferences = mprefmamager.getSharedPreferences();
		MyOnChangeListen changeListen = new MyOnChangeListen();
		//findPreference(getString(R.string.str_bitrate)).setOnPreferenceChangeListener(changeListen);
		// put the preference values  for connection here
		findPreference(getString(R.string.str_bitrate)).setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.always_available))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.wifi_available))
				.setOnPreferenceChangeListener(changeListen);
		mListPreference = (ListPreference)getPreferenceScreen().findPreference(KEY_LIST_PREFERENCE);
		preference=mprefmamager.findPreference(getString(R.string.availability_profile));
		String key = preference.getKey();
		if (key.equals(R.string.always_available)&&key.equals(R.string.wifi_available)) {
			//Reset other items
			CheckBoxPreference p1 = (CheckBoxPreference)findPreference(getString(R.string.always_available));
			p1.setChecked(true);
			CheckBoxPreference p2 = (CheckBoxPreference)findPreference(getString(R.string.wifi_available));
			p2.setChecked(false);
		}
		//String key2=mprefmamager.findPreference(getString(R.string.vibrator)).getKey();
		if(mpreferences.getBoolean(getString(R.string.vibrator), true)){
			Log.d("SettingFragment","Vibrator is on");
			isvib=true;
		}
		findPreference(getString(R.string.network1))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.network22))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.network2))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.network3))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.vibrator))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.phonebook))
				.setOnPreferenceChangeListener(changeListen);
		/*findPreference(getString(R.string.str_resolution))
				.setOnPreferenceChangeListener(changeListen);*/
		/*findPreference(getString(R.string.str_fwtokey))
				.setOnPreferenceChangeListener(changeListen);*/
		mpreferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				changed = true;
				Log.d("SettingFragment","Change occured \n"+key+" \n"+sharedPreferences);
				if(key.equals(getString(R.string.vibrator))){
					if(sharedPreferences.getBoolean(getString(R.string.vibrator), true)){
						Log.d("SettingFragment","vib off");
					}
					else if(sharedPreferences.getBoolean(getString(R.string.vibrator), false)){
						Log.d("1. SettingFragment","vib on");
					}else{
						Log.d("2. SettingFragment","vib on");
					}
				}
			}
		});
		// for network monitoring we will use the following code
//For General settings
	}
	@Override
	public void onResume() {
		super.onResume();
		// Registers a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!changed)
			return;
		//mSipSdk.enable3GppTags(true);
		//~~~~Network preferences~~~~//
		//Availablitiy
		if (mpreferences.getBoolean(getString(R.string.always_available), true)) {
			mpreferences.getBoolean(getString(R.string.wifi_available), false);
			// continue the network here
		} else if (mpreferences.getBoolean(getString(R.string.wifi_available), true)) {
			mpreferences.getBoolean(getString(R.string.always_available), false);
			//Set tha app to work on only wifi network
			// flag the global context
		}
		//2g/GSM,3g,4g
		if (mpreferences.getBoolean(getString(R.string.network1), true)) {
			if (!iswificonnected()) {
				toggleWiFi(true);
			}
			//Toast.makeText(getActivity().getApplicationContext(),"Wifi is selected",Toast.LENGTH_SHORT).show();
			toggleWiFi(true);
		} else if (mpreferences.getBoolean(getString(R.string.network22), true)) {
			//toggleumt_2g(false);
		} else if (mpreferences.getBoolean(getString(R.string.network2), true)) {
			toggleumt_3g(true);
		} else if (mpreferences.getBoolean(getString(R.string.network3), true)) {
			toggleumt_4g(true);
		}
			//Audio preference
		mSipSdk.clearAudioCodec();
		if (mpreferences.getBoolean(getString(R.string.MEDIA_G722), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_G729), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_AMR), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMR);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_AMRWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMRWB);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_GSM), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_GSM);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMA), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMA);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMU), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMU);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_SPEEX), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEX);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_SPEEXWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEXWB);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_ILBC), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ILBC);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_ISACWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACWB);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_ISACSWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACSWB);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_OPUS), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_OPUS);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_DTMF), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_DTMF);
		}
		mSipSdk.enableVAD(mpreferences.getBoolean(
				getString(R.string.MEDIA_VAD), true));
		mSipSdk.enableAEC(mpreferences.getBoolean(
				getString(R.string.MEDIA_AEC), true) ? PortSipEnumDefine.ENUM_EC_DEFAULT : PortSipEnumDefine.ENUM_EC_NONE);
		mSipSdk.enableANS(mpreferences.getBoolean(
				getString(R.string.MEDIA_ANS), false) ? PortSipEnumDefine.ENUM_NS_DEFAULT : PortSipEnumDefine.ENUM_NS_NONE);
		mSipSdk.enableAGC(mpreferences.getBoolean(
				getString(R.string.MEDIA_AGC), true) ? PortSipEnumDefine.ENUM_AGC_DEFAULT : PortSipEnumDefine.ENUM_AGC_NONE);
		mSipSdk.enableCNG(mpreferences.getBoolean(
				getString(R.string.MEDIA_CNG), true));
		// audio codecs
		mSipSdk.clearVideoCodec();
		if (mpreferences.getBoolean(getString(R.string.MEDIA_H263), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_H26398), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263_1998);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_H264), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H264);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_VP8), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_VP8);
		}
		// sdk.setAudioSamples(20,0);
		mSipSdk.setRtpPortRange(2000, 3000, 3002, 4000);
		setForward();
		mSipSdk.enableReliableProvisional(mpreferences.getBoolean(
				getString(R.string.str_pracktitle), false));


	}
	// Checks the network connection and sets the wifiConnected and mobileConnected
	// variables accordingly.

	public void toggleWiFi(boolean status) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (status == true && !wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);

		} else if (status == false && wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}
	public boolean iswificonnected(){
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if(wifiManager.isWifiEnabled()){return true;}
		else{
				return false;
			}
	}
	public void toggleumt_4g(boolean status){
		boolean state = isMobileDataEnable();
		// toggle the state for 4g
		if(state==true&&status==false){toggleMobileDataConnection(false);}
		else if(state==false&&status==true){toggleMobileDataConnection(true);}
	}
	public void toggleumt_3g(boolean status){
		boolean state = isMobileDataEnable();
		// toggle the state
		if(state==true&&status==false){toggleMobileDataConnection(false);}
		else if(state==false&&status==true){toggleMobileDataConnection(true);}
	}
	public void toggleumt_Gsm(boolean status){
		boolean state = isMobileDataEnable();
		// toggle the state for GSM network
		if(state==true&&status==false){toggleMobileDataConnection(false);}
		else if(state==false&&status==true){toggleMobileDataConnection(true);}
	}
	public boolean isMobileDataEnable() {
		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");//true
			method.setAccessible(true); // Make the method callable
			// get the setting for "mobile data"
			mobileDataEnabled = (Boolean)method.invoke(cm);
		} catch (Exception e) {
			// Some problem accessible private API and do whatever error handling you want here
		}
		return mobileDataEnabled;
	}

	public boolean toggleMobileDataConnection(boolean ON)
	{
		try {
			//create instance of connectivity manager and get system connectivity service
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//create instance of class and get name of connectivity manager system service class
			final Class conmanClass  = Class.forName(conman.getClass().getName());
			//create instance of field and get mService Declared field
			final Field iConnectivityManagerField= conmanClass.getDeclaredField("mService");
			//Attempt to set the value of the accessible flag to true
			iConnectivityManagerField.setAccessible(true);
			//create instance of object and get the value of field conman
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			//create instance of class and get the name of iConnectivityManager field
			final Class iConnectivityManagerClass=  Class.forName(iConnectivityManager.getClass().getName());
			//create instance of method and get declared method and type
			final Method setMobileDataEnabledMethod= iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);
			//Attempt to set the value of the accessible flag to true
			setMobileDataEnabledMethod.setAccessible(true);
			//dynamically invoke the iConnectivityManager object according to your need (true/false)
			setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
		} catch (Exception e){
		}
		return true;
	}
	public void setVibrate(boolean vibrate) {
		SessionManager sessionManager= new SessionManager(getActivity().getApplicationContext());
		if(vibrate){
			sessionManager.setVibrate(true);
			Log.d("SettingFragment","VIBRATOR STATUS IS: "+vibrate);
		}else {
			sessionManager.setVibrate(false);
			Log.d("SettingFragment", "VIBRATOR STATUS IS: " + vibrate);
		}
	}

	public void setPhoneOk(boolean phoneOk) {
		SessionManager sessionManager= new SessionManager(getActivity().getApplicationContext());
		if(phoneOk){
			sessionManager.setPhoneOk(true);
		}else{
			sessionManager.setPhoneOk(false);
		}
	}

	class MyOnChangeListen implements OnPreferenceChangeListener {
		@Override
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			if (arg0.getKey().equals(getString(R.string.str_bitrate))) {
				mSipSdk.setVideoBitrate((Integer) arg1);
			} else if (arg0.getKey().equals(getString(R.string.str_resolution))) {
				mSipSdk.setVideoResolution(Integer.valueOf((String) arg1));
			} else if (arg0.getKey().equals(getString(R.string.str_fwtokey))) {
				String forwardto = (String) arg1;
				if (!forwardto.matches(MyApplication.SIP_ADDRRE_PATTERN)) {
					Toast.makeText(context,"The forward address must likes sip:xxxx@sip.portsip.com.",
							Toast.LENGTH_LONG).show();
				}
			}
			else if (arg0.getKey().equals(getString(R.string.always_available))) {
				//The app will alwaus be available despite the network status
				mpreferences.getBoolean(getString(R.string.wifi_available), false);
				Log.d("SettingFragment", "--------------Availability state changed-------------");
				CheckBoxPreference p = (CheckBoxPreference)findPreference(getString(R.string.wifi_available));
				p.setChecked(false);
				myapp=(MyApplication)getActivity().getApplication();
				if(!myapp.isOnline()){
					Intent inteEt= new Intent(getActivity(), LoginActivity.class);
					startActivity(inteEt);
				}
				//setProfile(myapp.Profil.ALWAYS);
				//continue the network Status here , The app will run no matter how it is minimized
			}else if(arg0.getKey().equals(getString(R.string.wifi_available))){
				mpreferences.getBoolean(getString(R.string.always_available), false);
				Log.d("SettingFragment", "--------------Availability on wifi state changed-------------");
				CheckBoxPreference p = (CheckBoxPreference)findPreference(getString(R.string.always_available));
				p.setChecked(false);
				//setProfile(myapp.getApplicationContext());
				//Set tha app to work on only wifi network
			}else if(!arg0.getKey().equals(getString(R.string.wifi_available))&!arg0.getKey().equals(getString(R.string.always_available))){
				CheckBoxPreference p = (CheckBoxPreference)findPreference(getString(R.string.always_available));
				p.setChecked(true);
			}
			else if(arg0.getKey().equals(getString(R.string.network1))){
				if(!iswificonnected()){
					toggleWiFi(true);
				}
			}else if(arg0.getKey().equals(getString(R.string.network2))){
					toggleumt_3g(true);
			}else if(arg0.getKey().equals(getString(R.string.network3))){
				toggleumt_4g(true);
			}else if(arg0.getKey().equals(getString(R.string.network22))){
				// code for enabling the GMS network
			}else if(arg0.getKey().equals(getString(R.string.vibrator))){
				isvib=true;
				setVibrate(true);
				Log.d("SettingFragment","Vib Enabled");
			}else if(!arg0.getKey().equals(getString(R.string.vibrator))){
				isvib=false;
				setVibrate(false);
				Log.d("SettingFragment", "Vib Enabled");
			}else if(arg0.getKey().equals(getString(R.string.phonebook))){
				setPhoneOk(true);
			}else if(!arg0.getKey().equals(getString(R.string.phonebook))){
				setPhoneOk(false);
			}
			return true;
		}
	}
/*
*
* */
	private int setForward() {
		int ret = PortSipErrorcode.ECoreArgumentNull;
		boolean forwardopen = mpreferences.getBoolean(
				getString(R.string.str_fwopenkey), false);

		if (!forwardopen) {
			mSipSdk.disableCallForward();
			return ret;
		}

		String forwardTo = mpreferences.getString(
				getString(R.string.str_fwtokey), "");
		boolean forwardonbusy = mpreferences.getBoolean(
				getString(R.string.str_fwbusykey), true);

		if (forwardTo.length() <= 0
				|| !forwardTo.matches(MyApplication.SIP_ADDRRE_PATTERN)) {
			// Toast.makeText(context,"The forward address must likes sip:xxxx@sip.portsip.com.",
			// Toast.LENGTH_LONG).show();
			mSipSdk.disableCallForward();
			return ret;
		}

		if (forwardonbusy) {
			ret = mSipSdk.enableCallForward(true, forwardTo);
		} else {
			ret = mSipSdk.enableCallForward(false, forwardTo);
		}
		return ret;
	}
	private void setProfile(MyApplication.Profil mode) {
		myapp.setGlobalWifi(mode= MyApplication.Profil.WIFI);
		myapp.setGlobalAlways(mode = MyApplication.Profil.ALWAYS);
	}
	public class NetworkReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connMgr =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				// Checks the user prefs and the network connection. Based on the result, decides
				// whether
				// to refresh the display or keep the current display.
				// If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
			if (WIFI.equals(sPref) && networkInfo != null
					&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// If device has its Wi-Fi connection, sets refreshDisplay
				// to true. This causes the display to be refreshed when the user
				// returns to the app.
				//refreshDisplay = true;
				Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
				// If the setting is ANY network and there is a network connection
				// (which by process of elimination would be mobile), sets refreshDisplay to true.
			} else if (ANY.equals(sPref) && networkInfo != null) {
				//refreshDisplay = true;
				// Otherwise, the app can't download content--either because there is no network
				// connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
				// is no Wi-Fi connection.
				// Sets refreshDisplay to false.
			} else {
				//refreshDisplay = false;
				Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
			}
		}
	}
}

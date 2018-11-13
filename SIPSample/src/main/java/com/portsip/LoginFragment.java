package com.portsip;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.portsip.util.Line;
import com.portsip.util.Network;
import com.portsip.util.SettingConfig;
import com.portsip.util.UserInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import helper.SQLiteHandler;
import helper.SessionManager;

public class LoginFragment extends Fragment implements OnItemSelectedListener,OnClickListener{
	private static final String TAG = "LoginFragment";
	PortSipSdk mSipSdk;
	Button mbtnReg, mbtnUnReg;
	public String phone_number;
	public String name;
	public String email;
	public String password;
	TextView mtxStatus;
	String statuString;
	MyApplication myApplication;
	Context context = null;
	String LogPath = null;
	ArrayList<String> take_res= new ArrayList<>();
	private ProgressDialog pDialog;
	/************* Do not edit the following key code.************  IT is a licensed key and no one has a right to use it**********/
	String licenseKey ="3Zh1CRTNENzY1RUYzM0ZDQkM1OUEyOEQyQzYwOTJBMENFRkA0NEIxRDEwMDM4MjY2NzFCRjYwMDdCNTI1ODQ1ODdBMUBEMkE4Nzc3Mzg3MEQzODg3NUZGMTg2MDI5MTg4M0Q1OUBGRUU2MjE2OUFCOEYyMDlDRDYxODQzMEI2ODJGRTY1QQ";
	//String licenseKey ="1dR4zREEzNkUzNEVFNDkwNTU3NUJCN0RDMzNBRUNBQkM1OUA0NTlGN0I5NjI1QTQ4MUNGNDYxODkzQzBDQThEQkE2QUBFRUE0RkFGNzhEQUM4QTczNkUwNkJDN0QzQjI2OUE0QUA2OUI3QUQ5NTM3RTNEM0I0RkE4QkVCMkU4NDFFNzBDNA";
	private SQLiteHandler db;
	private SessionManager session;
	private boolean rec_flag=false;
	private boolean res_flag;
	public String storedName;
	View rootView;
	LayoutInflater inf_copy;
	Context mContext;
	ArrayList<String> mylistRec;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			rec_flag = getArguments().getBoolean("FLAGG");
			res_flag=getArguments().getBoolean("DUKEM");
			name=getArguments().getString("name");
			phone_number=getArguments().getString("phone");
			password=getArguments().getString("password");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		context = getActivity();
		session = new SessionManager(context.getApplicationContext());
		if (session.isLoggedIn()) {
			Log.d(TAG,"Session on progress====>");
		}

		name=session.getName();
		password=session.getPassword();
		phone_number=session.getPhone();
		Log.d(TAG,"############"+name+
				password+phone_number);// Just for console application test
		SettingConfig conf= new SettingConfig();
		conf.db_Info(name,password,phone_number);
		myApplication = ((MyApplication) context.getApplicationContext());
		mSipSdk = myApplication.getPortSIPSDK();
		//Bundle i= getIntent().getExtras();
		rootView = inflater.inflate(R.layout.loginview, null);
		initView(rootView);
		//Check the flagg if he user want unsubscription
		if(rec_flag==true){
			offline();// To make the person offline
			quit();//To make the person quit
			//extrainf();
			return null;//inflater.inflate(R.layout.valg_sms_call, container, false);
		}
		//userUpdate();//onClick(rootView.findViewById(R.id.btonline));
		//makeonline();
		return rootView;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putStringArrayList("mylist", mylistRec);
		savedInstanceState.putString("myphone", phone_number);
		savedInstanceState.putString("mypassword", password);
		savedInstanceState.putString("myemail", email);
		savedInstanceState.putString("myname", name);
		savedInstanceState.putBoolean("recflag", rec_flag);
		savedInstanceState.putBoolean("resflag", res_flag);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mylistRec = savedInstanceState.getStringArrayList("mylist");
			phone_number=savedInstanceState.getString("myphone");
			password=savedInstanceState.getString("mypassword");
			email=savedInstanceState.getString("myemail");
			name=savedInstanceState.getString("myname");
			rec_flag=savedInstanceState.getBoolean("recflag");
			res_flag=savedInstanceState.getBoolean("resflag");
		}
	}
	/*@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		initView(rootView);
		userUpdate();
		onClick(rootView.findViewById(R.id.btonline));
		Log.d(TAG, "call Login");
	}
	//
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		userUpdate();
		onClick(rootView.findViewById(R.id.btonline));
		Log.d(TAG, "call Login");
	}*/
	@Override
	public void onResume() {
		//initView(rootView);
		//	UserInfo userInfo= new UserInfo();
		//name,phone,email,password
		/*try {
			userInfo.setAuthName(name);
			userInfo.setUserName(phone_number);
			//userInfo.setAuthName(take_res.get(3).toString());
			userInfo.setUserPwd(password.toString());
		}catch(Exception e){e.printStackTrace();}*/
		//	userUpdate();
		//initView(getView());
		//makeonline();
		//onClick(rootView.findViewById(R.id.btonline));
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	////////////////
	public void getUserinfo(){
		UserInfo info = new UserInfo();
		phone_number=info.getUserName();
		name=info.getUserDisplayName();
		password=info.getUserPassword();

	}
	public void userUpdate(){
		UserInfo info= new UserInfo();
		info.setUserName(phone_number);
		info.setUserPwd(password);
		info.setUserDisplayName(name);
	}
	private void extrainf() {
		UserInfo infor= new UserInfo();
		storedName=infor.getUserName().toString();
		infor.setUserName("null");
		infor.setSipPort(0);
		infor.setSipServer("null");
	}
	public void onRegisterSuccess(String reason, int code) {
		//undateStatus();
	}
	public void onRegisterFailure(String reason, int code) {
		statuString = reason;
		//undateStatus();
	}
	void undateStatus() {
		if (myApplication.isOnline()) {
			mtxStatus.setText(R.string.online);
			statuString= null;
		} else {
			if(statuString!=null)
				mtxStatus.setText(getString(R.string.unregister)+": "+statuString);
			else {
				mtxStatus.setText(R.string.unregister);
			}
		}
	}
	private void initView(View view) {
 		//mtxStatus = (TextView) view.findViewById(R.id.txtips);
		mbtnReg = (Button) view.findViewById(R.id.btonline);
		//if()
		mbtnUnReg = (Button) view.findViewById(R.id.btoffline);
		loadUserInfo(view);
		mbtnReg.setOnClickListener(this);
		mbtnUnReg.setOnClickListener(this);
		//undateStatus();
	}
	/*long[] items=0;
    for (int i=0;i<10;i++){
        items[i]=string(m);
    }*/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btoffline:
				offline();
				break;
			case R.id.btonline:
				online();
				Intent intent=new Intent(getActivity(),MainActivity.class);
				intent.putExtra("Home",true);
				getActivity().startActivity(intent);
				break;
			default:
				break;
		}
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
							   long arg3) {
		switch (arg0.getId()) {
			case R.id.spSRTP:
				SetSRTPType(arg2);
				break;
			case R.id.spTransport:
				SetTransType(arg2);
			default:
				break;
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	private void SetSRTPType(int index) {
		int SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;
		switch (index) {
			case 0:
				SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;
				break;
			case 1:
				SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_FORCE;
				break;
			case 2:
				SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_PREFER;
				break;
		}
		SettingConfig.setSrtpType(context, SrtType, mSipSdk);
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
	private void SetTransType(int index) {
		int transType = PortSipEnumDefine.ENUM_TRANSPORT_UDP;
		switch (index) {
			case 0:
				transType = PortSipEnumDefine.ENUM_TRANSPORT_UDP;
				break;

			case 1:
				transType = PortSipEnumDefine.ENUM_TRANSPORT_TLS;
				break;
			case 2:
				transType = PortSipEnumDefine.ENUM_TRANSPORT_TCP;
				break;

			case 3:
				transType = PortSipEnumDefine.ENUM_TRANSPORT_PERS;
				break;
		}

		SettingConfig.setTransType(context, transType);
	}
	void loadUserInfo(View view){
		//userUpdate();
		UserInfo userInfo = SettingConfig.getUserInfo(context);
		//String item= userInfo.getUserName()==null?"":userInfo.getUserName();
		String item= phone_number;//userInfo.getUserName()==null?"":userInfo.getUserName();
		userInfo.setUserName(item);//
		Log.d(TAG,"==============ITEM============="+item+password+phone_number);
		((EditText) view.findViewById(R.id.etusername)).setText(item);
		item= userInfo.getUserPassword()==null?"":userInfo.getUserPassword();
		item=password;
		userInfo.setUserPwd(password);
		((EditText) view.findViewById(R.id.etpwd)).setText(item);
		item= userInfo.getSipServer()==null?"":userInfo.getSipServer();
		userInfo.setSipServer("callman.cloudapp.net");
		((EditText) view.findViewById(R.id.etsipsrv)).setText(item);
		item= ""+userInfo.getSipPort();
		((EditText) view.findViewById(R.id.etsipport)).setText(item);
		item= userInfo.getUserdomain()==null?"":userInfo.getUserdomain();
		((EditText) view.findViewById(R.id.etuserdomain)).setText(item);
		item= userInfo.getAuthName()==null?"":userInfo.getAuthName();
		((EditText) view.findViewById(R.id.etauthName)).setText(item);
		item= name;//userInfo.getUserDisplayName()==null?"":userInfo.getUserDisplayName();
		userInfo.setUserName(name);
		((EditText) view.findViewById(R.id.etdisplayname)).setText(item);
		item= userInfo.getStunServer()==null?"":userInfo.getStunServer();
		((EditText) view.findViewById(R.id.etStunServer)).setText(item);
		item= ""+userInfo.getStunPort();
		((EditText) view.findViewById(R.id.etStunPort)).setText(item);
		Spinner spTransport = (Spinner) view.findViewById(R.id.spTransport);
		Spinner spSRTP = (Spinner) view.findViewById(R.id.spSRTP);
		userInfo.setStunServer("callman.cloudapp.net");
		spTransport.setAdapter(new ArrayAdapter<String>(context,
				R.layout.viewspinneritem, getResources().getStringArray(
				R.array.transpots)));
		spSRTP.setAdapter(new ArrayAdapter<String>(context,
				R.layout.viewspinneritem, getResources().getStringArray(
				R.array.srtp)));
		spSRTP.setOnItemSelectedListener(this);
		spTransport.setOnItemSelectedListener(this);
		int SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;
		SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_PREFER;
		SettingConfig.setSrtpType(context, SrtType, mSipSdk);
		spTransport.setSelection(userInfo.getTransType());
		spSRTP.setSelection(SettingConfig.getSrtpType(context));
	}
	private UserInfo saveUserInfo(View view){
		int port;
		String cloudserver;
		UserInfo userInfo = new UserInfo();
		SessionManager sessionManager= new SessionManager(context);
		userInfo.setUserName(phone_number);
		userInfo.setUserPwd(password);
		userInfo.setUserDisplayName(name);
		String item1 = session.getPhone();//((EditText) view.findViewById(R.id.etusername)).getText().toString();
		userInfo.setUserName(item1);
		String item2= session.getPassword();
		//String item2 = ((EditText) view.findViewById(R.id.etpwd)).getText().toString();
		userInfo.setUserPwd(item2);
		String item = "callman.cloudapp.net";//((EditText) view.findViewById(R.id.etsipsrv)).getText().toString();
		try{
			cloudserver=item.toString();
		}
		catch(Exception e){
			cloudserver="callman.cloudapp.net";
		}
		userInfo.setSipServer(cloudserver);
		item = "5060";//((EditText) view.findViewById(R.id.etsipport)).getText().toString();
		try{
			port = Integer.parseInt(item);
		}catch(NumberFormatException e){
			port = 5060;
		}
		userInfo.setSipPort(port);
		item = null;//((EditText) view.findViewById(R.id.etuserdomain)).getText().toString();
		userInfo.setUserDomain(item);
		item = null;//((EditText) view.findViewById(R.id.etauthName)).getText().toString();
		userInfo.setAuthName(item);
		item = session.getName();//((EditText) view.findViewById(R.id.etdisplayname)).getText().toString();
		userInfo.setUserDisplayName(item);
		item = "callman.cloudapp.net";//((EditText) view.findViewById(R.id.etStunServer)).getText().toString();
		userInfo.setStunServer(item);
		item = "3478";//((EditText) view.findViewById(R.id.etStunPort)).getText().toString();
		try{
			port = Integer.parseInt(item);
		}catch(NumberFormatException e){
			port = 3478;
		}
		userInfo.setStunPort(port);
		userInfo.setTranType(PortSipEnumDefine.ENUM_TRANSPORT_UDP);// ((Spinner) view.findViewById(R.id.spTransport)).getSelectedItemId());
		SettingConfig.setUserInfo(context, userInfo);
		return userInfo;
	}
	private int online() {
		int result = setUserInfo();
		if (result == PortSipErrorcode.ECoreErrorNone) {
			result = mSipSdk.registerServer(90, 3);

			if(result!=PortSipErrorcode.ECoreErrorNone ){
				statuString = "register server failed";
				//undateStatus();
			}
		}else {
			//undateStatus();
			Log.d(TAG,"Logged inn");
		}
		Log.d(TAG,"Login succeddeddd --OK--");
		return result;
	}
	private void makeonline(){
		int takeAction= online();
		Log.d(TAG,"Return result is: "+takeAction);
		Intent intent=new Intent(getActivity(),MainActivity.class);
		intent.putExtra("Home",true);
		getActivity().startActivity(intent);
	}
	private void offline() {
		Line[] mLines = myApplication.getLines();
		for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
			if (mLines[i].getRecvCallState()) {
				mSipSdk.rejectCall(mLines[i].getSessionId(), 486);
			} else if (mLines[i].getSessionState()) {
				mSipSdk.hangUp(mLines[i].getSessionId());
			}
			mLines[i].reset();
		}
		myApplication.setOnlineState(false);
		//undateStatus();
		mSipSdk.unRegisterServer();
		mSipSdk.DeleteCallManager();
	}

	int setUserInfo() {
		//userUpdate();
		Environment.getExternalStorageDirectory();
		LogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + '/';
		String localIP = new Network(context).getLocalIP(false);// ipv4
		int localPort = new Random().nextInt(4940) + 5060;
		UserInfo info = saveUserInfo(getView());
		if(info.isAvailable())
		{
			mSipSdk.CreateCallManager(context.getApplicationContext());// step 1
			int result = mSipSdk.initialize(info.getTransType(),
					PortSipEnumDefine.ENUM_LOG_LEVEL_DEBUG, LogPath,
					Line.MAX_LINES, "PortSIP VoIP SDK for Android",
					0,0);// step 2
			if (result != PortSipErrorcode.ECoreErrorNone) {
				statuString = "init Sdk Failed";
				return result;
			}
			int nSetKeyRet = mSipSdk.setLicenseKey(licenseKey);// step 3
			if (nSetKeyRet == PortSipErrorcode.ECoreTrialVersionLicenseKey) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Prompt").setMessage(R.string.trial_version_tips);
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else if (nSetKeyRet == PortSipErrorcode.ECoreWrongLicenseKey) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Prompt").setMessage(R.string.wrong_lisence_tips);
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
				return -1;
			}
			result = mSipSdk.setUser(info.getUserName(), info.getUserDisplayName(), info.getAuthName(), info.getUserPassword(),
					localIP, localPort, info.getUserdomain(), info.getSipServer(), info.getSipPort(),
					info.getStunServer(), info.getStunPort(), null, 3479);// step 4
			if (result != PortSipErrorcode.ECoreErrorNone) {
				statuString = "setUser resource failed";
				return result;
			}
		} else {
			return -1;
		}
		SettingConfig.setAVArguments(context, mSipSdk);
		return PortSipErrorcode.ECoreErrorNone;
	}
	/// To retrieve an information from the server
	private void quit() {
		mSipSdk = myApplication.getPortSIPSDK();

		if (myApplication.isOnline()) {
			Line[] mLines = myApplication.getLines();
			for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
				if (mLines[i].getRecvCallState()) {
					mSipSdk.rejectCall(mLines[i].getSessionId(), 486);
				} else if (mLines[i].getSessionState()) {
					mSipSdk.hangUp(mLines[i].getSessionId());
				}
				mLines[i].reset();
			}
			myApplication.setOnlineState(false);
			mSipSdk.unRegisterServer();
			mSipSdk.DeleteCallManager();
		}
	}

}

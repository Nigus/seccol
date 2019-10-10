package com.portsip;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.portsip.main.Aboutoss;
import com.portsip.main.Canclemenu;
import com.portsip.main.HomeTabActivity;
import com.portsip.main.Legal;
import com.portsip.main.LoginActivity;
import com.portsip.main.privacypolicy;
import com.portsip.main.SettingMenu;
import com.portsip.util.Line;

import java.util.ArrayList;

import helper.SQLiteHandler;
import helper.SessionManager;
public class MainActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	PortSipSdk sdk;
	final int MENU_QUIT = 0;
	Reconnect_Server reconnect_server;
	Context mContext;
	LoginFragment loginFragment = null;
	NumpadFragment numpadFragment = null;
	VideoCallFragment videoCallFragment = null;
	MessageFragment messageFragment = null;
	SettingFragment settingFragment = null;
	HomeFragment homeFragment=null;
	ConnectedFragment connectedFragment=null;
	Fragment frontFragment;
	View contentView = null;
	public String name;
	public String phone_number;
	public String password;
	public String email;
	public ViewGroup layout = null;
	MyApplication myAndroid = new MyApplication();
	ArrayList<String> returnAppArray = new ArrayList<>();
	ArrayList<String> temp_holder;
	private SQLiteHandler db;
	private SessionManager session;
	private boolean call_flag=false;
	private String dest_number;
	private boolean home_flag;
	Bundle savedInstanceState2;
	String callerId,callerIdName;
	boolean dialogFlag=false;
	boolean callerFlag=false;
	boolean smsFlag=false;
	Bundle extras;
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//LayoutInflater inflater=this.getLayoutInflater();
		//Window win=getWindow();
		int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

		getWindow().addFlags(flags);
		getActionBar().setTitle(R.string.app_name);
		getActionBar().setIcon(R.drawable.it_icon);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		mContext = this;
		setContentView(R.layout.mainview);
		contentView = findViewById(R.id.content);
		myAndroid = (MyApplication) getApplication();
		session= new SessionManager(getApplicationContext());
		db=new SQLiteHandler(getApplicationContext());
		Log.d(TAG, "Dialer Flag is: " + call_flag + dest_number);
		extras = getIntent().getExtras();
		if (extras != null) {
			name = extras.getString("nameIn");
			phone_number = extras.getString("phoneIn");
			password = extras.getString("passwordIn");
			email = extras.getString("emailIn");
			call_flag=extras.getBoolean("flag");
			dest_number=extras.getString("dialingStateNr");
			home_flag=extras.getBoolean("Home");
			//smsFlag=extras.getBoolean("putSmsWindow");
		}
		if(name==null||phone_number==null||password==null){
			name=session.getName();
			phone_number=session.getPhone();
			password=session.getPassword();
			email=session.getEmail();
		}
		//callerId,callerIdName,dialogFlag,callerFlag
		// For incoming call
		callerId=myAndroid.getGPhone();
		callerIdName=myAndroid.getGName();
		dialogFlag=myAndroid.getDialogFlag();
		callerFlag=myAndroid.getInCallFlag();
		session=new SessionManager(this.getApplicationContext());
		RadioGroup menuGroup = (RadioGroup) findViewById(R.id.tab_menu);
		boolean iscalling=session.getCallingFlag();
		if(iscalling){
			loadNumPadFragment(session.getDestValue());
			menuGroup.check(R.id.tab_numpad);
			Log.d(TAG,"---------------////----------------- iscalling");//
		}
	    else
			loadHomeFragment();
			menuGroup.check(R.id.home);
		menuGroup.setOnCheckedChangeListener(new MyOnCheckChangeListen());
		//menuGroup.setEnabled(true);
		//menuGroup.setVisibility(View.INVISIBLE);
	}
	class MyOnCheckChangeListen implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.home:
					loadHomeFragment();break;
				case R.id.tab_login:
					loadLoginFragment();
					break;
				case R.id.connected:
					loadConnectedFragment();
					break;
				case R.id.tab_numpad:
					loadNumPadFragment(dest_number);
					break;
				case R.id.tab_video:
					loadVideoFragment();
					break;
				case R.id.tab_message:
					loadMessageFragment();smsFlag=false;
					break;
				case R.id.tab_setting:
					loadSettingFragment();
					break;
				default:
					break;
			}
		}
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// etc.
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	protected void onResume() {
		super.onResume();
		((MyApplication) getApplicationContext()).setMainActivity(this);

	}
	@Override
	protected void onPause() {
		super.onPause();
		((MyApplication) getApplicationContext()).setMainActivity(this);
	}
	private void loadHomeFragment(){
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
		}
		frontFragment = homeFragment;
		//homeFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, homeFragment).commit();
	}
	private void loadConnectedFragment(){
	//I wanted to change the ConnectedCall class with fragment class
		Bundle args = new Bundle();
		if (connectedFragment == null) {
			connectedFragment = new ConnectedFragment();
		}
		frontFragment = connectedFragment;
		connectedFragment.setArguments(args);
		//callerId,callerIdName,dialogFlag,callerFlag
		args.putString("callerId",callerId);
		args.putString("callerIdName", callerIdName);
		//args.putBoolean("dialogFl");
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, connectedFragment).commit();
	}
	private void loadLoginFragment() {
		Bundle args = new Bundle();
		// Check the flags instead want to keep it on the sharedpreferences
		args.putBoolean("FLAGG", false);// Flags to control the app installation
		args.putBoolean("DUKEM",false);
		args.putString("name",name);
		args.putString("phone",phone_number);
		args.putString("password", password);
		if (loginFragment == null) {
			loginFragment = new LoginFragment();
		}
		frontFragment = loginFragment;
		loginFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, loginFragment).commit();
	}
	public NumpadFragment getNumpadFragment() {
		if (frontFragment == numpadFragment) {
			return numpadFragment;
		}
		return null;
	}
	private void loadNumPadFragment(String destin) {
		Bundle args = new Bundle();
		args.putString("dialingStateNr",destin);
		args.putBoolean("callflag",call_flag);
		if (numpadFragment == null) {
			numpadFragment = new NumpadFragment();
		}
		frontFragment = numpadFragment;
		numpadFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, numpadFragment).commit();
	}
	public VideoCallFragment getVideoCallFragment() {
		if (frontFragment == videoCallFragment) {
			return videoCallFragment;
		}
		return null;
	}

	private void loadVideoFragment() {
		if (videoCallFragment == null) {
			videoCallFragment = new VideoCallFragment();
		}
		frontFragment = videoCallFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, videoCallFragment).commit();
	}
	private void loadMessageFragment() {

		Bundle args = new Bundle();
		if (messageFragment == null) {
			messageFragment = new MessageFragment();
		}
		//args.putString("name",name);
		args.putString("phone", phone_number);
		args.putString("password",password);
		frontFragment = messageFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, messageFragment).commit();
	}
	private void loadSettingFragment() {
		if (settingFragment == null) {
			settingFragment = new SettingFragment();
		}
		frontFragment = settingFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, settingFragment).commit();
	}
	LoginFragment getLoginFragment() {
		if (frontFragment == loginFragment) {
			return loginFragment;
		}
		return null;
	}
	HomeFragment getHomeFragment(){
		if (frontFragment == homeFragment) {
			return homeFragment;
		}
		return null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		//menu.add(Menu.NONE, MENU_QUIT, 0, "Quit");
		return true;
	}
	/*@Override
	public void onBackPressed(){
		super.onBackPressed();
		//Intent intet= new Intent(this.getApplicationContext(),LoginActivity.class);
		//intet.setFlags()
		//startActivity(intet);
	}*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.about:
				Aboutoss myfragment = Aboutoss.newInstance();
				myfragment.show(getFragmentManager(), "issues");
				break;
			case R.id.setting:
				Intent intent = new Intent(this,SettingMenu.class);
				startActivity(intent);
				//Toast.makeText(getApplicationContext(), "Setting will come..", Toast.LENGTH_SHORT).show();
				break;
			case R.id.logout:
				logoutUser();
				Toast.makeText(getApplicationContext(), "Logged out..", Toast.LENGTH_SHORT).show();
				break;
			case R.id.privacy_policy:
				privacypolicy pfragment= privacypolicy.newInstance();
				pfragment.show(getFragmentManager(), "issues");
				break;
			case R.id.terms:
				Legal newFragment = Legal.newInstance();
				newFragment.show(getFragmentManager(), "issues");
				break;
			case R.id.cancel_service:
				cancel_subs();
				break;
			default:
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//quit();
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	/*@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
	}*/
	private void quit() {
		MyApplication myApplication = (MyApplication) getApplicationContext();
		sdk = myApplication.getPortSIPSDK();
		if (myApplication.isOnline()) {
			Line[] mLines = myApplication.getLines();
			for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
				if (mLines[i].getRecvCallState()) {
					sdk.rejectCall(mLines[i].getSessionId(), 486);
				} else if (mLines[i].getSessionState()) {
					sdk.hangUp(mLines[i].getSessionId());
				}
				mLines[i].reset();
			}
			myApplication.setOnlineState(false);
			sdk.unRegisterServer();
			sdk.DeleteCallManager();
		}
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
				.cancelAll();
		this.finish();
	}
	private void logoutUser() {
		quit();
		session.setLogin(false,session.getName(),session.getPhone(),session.getPassword(),session.getEmail());
		db.deleteUsers();
		Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
				Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		this.finish();
	}
	public void cancel_subs(){
		String sub_title=getResources().getString(R.string.subs_title);
		String sub_body=getResources().getString(R.string.subs_body);
		final String sub_res=getResources().getString(R.string.subs_response);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(sub_title);
		alertDialog.setMessage(sub_body);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//delete_db();
						alertDialog.setMessage(sub_res);
						unregister_service();
					}
				});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
	public void unregister_service(){
		Intent intent2 = new Intent(getApplicationContext(),Canclemenu.class);
		startActivity(intent2);
	}
}

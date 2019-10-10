package com.portsip.util;

/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.MyApplication;
import com.portsip.MyNetwork;
import com.portsip.TurnOnWIfi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public class network_preference extends Activity implements View.OnClickListener {
    PortSipSdk mSipSdk;
    PreferenceManager mprefmamager;
    SharedPreferences mpreferences;
    MyApplication myApp;
    boolean changed = true;
    Context context;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private RadioButton globProfileWifi;
    private RadioButton globProfileAonwifi;
    private WifiManager wifiManager;
    private View view;
    public RadioGroup rGroup;
    MyNetwork mn;
    enum Profile {
        UNKNOWN,
        ALWAYS,
        WIFI,
        NEVER
    }
    public network_preference(Context con){
        this.context = con;
    }
    public network_preference(){
        //super(context);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_setting);
        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);

        //Lets read the components
        globProfileWifi=(RadioButton)findViewById(R.id.glob_profile_always);
        globProfileAonwifi=(RadioButton)findViewById(R.id.glob_profile_onwifi);
        checkBox1=(CheckBox)findViewById(R.id.wifi_pref);
        checkBox2=(CheckBox)findViewById(R.id.three_g_pref);
        checkBox3=(CheckBox)findViewById(R.id.fg_pref);
        Button saveBtn = (Button) findViewById(R.id.save_bt);
        rGroup = (RadioGroup) findViewById(R.id.statusAlive);
        //wifi_pref
        //three_g_pref
        //fg_pref
        globProfileWifi.setOnClickListener(this);
        globProfileAonwifi.setOnClickListener(this);
        //context= getApplicationContext();
        //set_prefs();
        //wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    toggleWiFi(true);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Enabled!", Toast.LENGTH_LONG).show();
                } else {
                    toggleWiFi(false);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Disabled!", Toast.LENGTH_LONG).show();
                }
                /*if (isChecked){
                    setupWifi();
                }*/
            }
        });
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked){
                    toggleumt_3g(true);
                }
                else
                {
                    toggleumt_3g(false);

                }
            }
        });
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked){
                    toggleumt_3g(true);
                }
                else
                {
                    toggleumt_3g(false);

                }
            }
        });
    }
    public void toggleumt_3g(boolean status){
        boolean state = isMobileDataEnable();
        // toggle the state
        if(state==true&&status==false){toggleMobileDataConnection(false);}
        else if(state==false&&status==true){toggleMobileDataConnection(true);}
    }
    public void setupUmt_threeG(boolean status) {
        //setMobileDataEnabled(context,true);
        //mn.setMobileDataEnabled(getBaseContext(), true);
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        if(status==true&&mobileDataEnabled==false){
            setMobileDataEnabled(true);
        }
        else{
            setMobileDataEnabled(false);
        }
        /*ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(status == true && !conman.isDefaultNetworkActive()) {
            setMobileDataEnabled(getApplicationContext(), true);
        }*/
    }
    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (status == false && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }
    public void setMobileDataEnabled(boolean enabled){
        ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabledMethod = null;
        try {
            setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        setMobileDataEnabledMethod.setAccessible(true);
        try {
            setMobileDataEnabledMethod.invoke(conman, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
///////////////////
    public boolean isMobileDataEnable() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
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
            final ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    ///////////////
    public void setupWifi(){
      //boolean b=wifiManager.isWifiEnabled();
      //wifiManager.setWifiEnabled(true);
        TurnOnWIfi wifinOn = new TurnOnWIfi(context);
        try {
            wifinOn.turnOnWifi();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void setProfile(Profile mode) {
        globProfileWifi.setChecked(mode == Profile.ALWAYS);
        globProfileAonwifi.setChecked(mode == Profile.WIFI);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

    @Override
    public void onClick(View v) {
        int id = v.getId();


        int selectedId = rGroup.getCheckedRadioButtonId();

        if(selectedId == R.id.glob_profile_always) {
            setProfile(Profile.ALWAYS); // Always available
        }else if(selectedId == R.id.glob_profile_wifi ) {
            setProfile(Profile.WIFI); // available on wifi
        }else {
			/*if(!SipConfigManager.getPreferenceBooleanValue(this, PreferencesWrapper.HAS_ALREADY_SETUP, false) ) {
			    SipConfigManager.setPreferenceBooleanValue(this, PreferencesWrapper.HAS_ALREADY_SETUP, true);
			}*/
			//applyPrefs();
            finish();
        }
    }

    /**
     * Get preference key for the kind of bandwidth to associate to a network
     *
     * @param networkType Type of the network {@link ConnectivityManager}
     * @param subType Subtype of the network {@link TelephonyManager}
     * @return the preference key for the network kind passed in argument
     */
    public static String getBandTypeKey(int networkType, int subType) {
        return "band_for_" + keyForNetwork(networkType, subType);
    }
    /**
     * Get the preference <b>partial</b> key for a given network kind
     *
     * @param networkType Type of the network {@link ConnectivityManager}
     * @param subType Subtype of the network {@link TelephonyManager}
     * @return The partial key for the network kind
     */
    private static String keyForNetwork(int networkType, int subType) {
        if (networkType == ConnectivityManager.TYPE_WIFI) {
            return "wifi";
        } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
            // 3G (or better)
            if (subType >= TelephonyManager.NETWORK_TYPE_UMTS) {
                return "3g";
            }
            // GPRS (or unknown)
            if (subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                return "gprs";
            }
            // EDGE
            if (subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                return "edge";
            }
        }
        return "other";
    }
}

/*      context = getActivity();
        mSipSdk = ((MyApplication) getActivity().getApplicationContext())
                .getPortSIPSDK();
        mprefmamager = getPreferenceManager();
        mpreferences = mprefmamager.getSharedPreferences();
        MyOnChangeListen changeListen = new MyOnChangeListen();
        findPreference(getString(R.string.str_bitrate))
                .setOnPreferenceChangeListener(changeListen);
        findPreference(getString(R.string.str_resolution))
                .setOnPreferenceChangeListener(changeListen);
        findPreference(getString(R.string.str_fwtokey))
                .setOnPreferenceChangeListener(changeListen);
        mpreferences
                .registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

                    @Override
                    public void onSharedPreferenceChanged(
                            SharedPreferences sharedPreferences, String key) {
                        changed = true;
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (!changed)
            return;

        // audio codecs
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
                getString(R.string.MEDIA_AEC), true)?PortSipEnumDefine.ENUM_EC_DEFAULT:PortSipEnumDefine.ENUM_EC_NONE);
        mSipSdk.enableANS(mpreferences.getBoolean(
                getString(R.string.MEDIA_ANS), false)?PortSipEnumDefine.ENUM_NS_DEFAULT:PortSipEnumDefine.ENUM_NS_NONE);
        mSipSdk.enableAGC(mpreferences.getBoolean(
                getString(R.string.MEDIA_AGC), true)?PortSipEnumDefine.ENUM_AGC_DEFAULT:PortSipEnumDefine.ENUM_AGC_NONE);
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

    class MyOnChangeListen implements OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference arg0, Object arg1) {
            if (arg0.getKey().equals(getString(R.string.str_bitrate))) {
                mSipSdk.setVideoBitrate((Integer) arg1);
            } else if (arg0.getKey().equals(getString(R.string.str_resolution))) {
                mSipSdk.setVideoResolution(Integer.valueOf((String) arg1));

            } else if (arg0.getKey().equals(getString(R.string.str_fwtokey))) {
                String forwardto = (String) arg1;
               /* if (!forwardto.matches(MyApplication.SIP_ADDRRE_PATTERN)) {
                    Toast.makeText(
                            context,
                            "The forward address must likes sip:xxxx@sip.portsip.com.",
                            Toast.LENGTH_LONG).show();
                }*/
 /*           }

            return true;
        }
    }

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

        /*if (forwardTo.length() <= 0
                || !forwardTo.matches(MyApplication.SIP_ADDRRE_PATTERN)) {
            // Toast.makeText(context,"The forward address must likes sip:xxxx@sip.portsip.com.",
            // Toast.LENGTH_LONG).show();
            mSipSdk.disableCallForward();
            return ret;
        }*/

                    /*
        if (forwardonbusy) {
            ret = mSipSdk.enableCallForward(true, forwardTo);
        } else {
            ret = mSipSdk.enableCallForward(false, forwardTo);
        }

        return ret;
    }


}

*/
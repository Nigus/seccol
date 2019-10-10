package com.portsip;

/**
 * Created by Nigussie on 09.06.2015.
 */
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class TurnOnWIfi{
    private  WifiManager wifi_manager;
    private  WifiConfiguration wifi_config;
    private Context paramContext;
    public TurnOnWIfi(Context con) {
        this.paramContext=con;
    }
    public void turnOnWifi() throws Exception {
        wifi_manager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        wifi_manager.setWifiEnabled(true);
        Thread.sleep(2000L);
    }
}
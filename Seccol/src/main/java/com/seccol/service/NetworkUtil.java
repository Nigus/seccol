package com.portsip.service;

/**
 * Created by Nigussie on 26.11.2015.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static int TYPE_MOBILE_GPRS=3;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
                    //ConnectivityManager.EXTRA_NETWORK_TYPE;
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
            if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE_HIPRI)
                return TYPE_MOBILE_GPRS;
        }
        return TYPE_NOT_CONNECTED;
    }
    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi changed";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data changed";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }else if(conn==NetworkUtil.TYPE_MOBILE_GPRS){
            status="Other network type";
        }
        return status;
    }
}
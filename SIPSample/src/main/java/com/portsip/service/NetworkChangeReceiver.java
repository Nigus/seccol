package com.portsip.service;

/**
 * Created by Nigussie on 26.11.2015.
 */
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.PortSipSdk;
import com.portsip.Reconnect_Server;
import com.portsip.util.Line;

import helper.SessionManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
MyApplication myApplication;
Context mContext;
Reconnect_Server reconnect_server;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        mContext=context.getApplicationContext();
        myApplication = (MyApplication) mContext.getApplicationContext();
        SessionManager sessionManager=new SessionManager(mContext);
        quit();
        if(!myApplication.isOnline()) {
            if (sessionManager.isLoggedIn()) {
                reconnect_server = new Reconnect_Server(mContext);
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            reconnect_server.online();
                        }
                    });
                }
            };
            new Thread(runnable).start();
            }
        }
        String status = NetworkUtil.getConnectivityStatusString(context);
        Log.d("NetworkChangeReceiver","----Current network status is"+status);
    }
    private void quit() {
        //MyApplication myApplication = (MyApplication) mContext.getApplicationContext();
        PortSipSdk sdk = myApplication.getPortSIPSDK();
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
        ((NotificationManager) mContext.getSystemService(myApplication.NOTIFICATION_SERVICE))
                .cancelAll();
        //mContext.finish();
    }
}

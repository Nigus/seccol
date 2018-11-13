package com.portsip.wakelock;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import helper.Util.Constants;

/**
 * Created by Niggui on 10.12.2015.
 */
public class ScreenIncomming extends BroadcastReceiver {

    private PowerManager.WakeLock mWakelock;
    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock mLock;
    private static ContentResolver sResolver;

    /**
     * onReceive for Reciever
     */

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context paramContext, Intent intent) {

        this.mWakelock = ((PowerManager) paramContext.getSystemService("power"))
                .newWakeLock(805306394/* | PowerManager.ON_AFTER_RELEASE */,
                        "wakelock");
        this.mWakelock.acquire();
        this.mLock = ((KeyguardManager) paramContext
                .getSystemService("keyguard")).newKeyguardLock("");
        this.mLock.disableKeyguard();

        //if (Constants.LOG)
            Log.d("FAkceREciever Call", "================>");

        setLockPatternEnabled(true);

        sResolver = paramContext.getContentResolver();

        Intent startMain = new Intent();
     //   startMain = new Intent(paramContext, InComingCall.class);
        startMain.setAction("com.example.fakecall.MyService");
        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        paramContext.startActivity(startMain);
    }

    /**
     * used for to enable lock in all patterns
     *
     * @param enabled
     */
    @SuppressWarnings("deprecation")
    public static void setLockPatternEnabled(boolean enabled) {
        setBoolean(android.provider.Settings.System.LOCK_PATTERN_ENABLED,
                enabled);
    }

    private static void setBoolean(String systemSettingKey, boolean enabled) {
        android.provider.Settings.System.putInt(sResolver, systemSettingKey,
                enabled ? 1 : 0);
    }
}

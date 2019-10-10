package com.portsip.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.portsip.MyApplication;
import com.portsip.background_alarm.Alarm;

public class PortSipService extends Service {
	public static final int FM_NOTIFICATION_ID=20002;
	public static final String SESSION_CHANG = MyApplication.class
			.getCanonicalName() + "Session change";
	static PortSipService instance;

	public PortSipService() {
		super();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder {

		public PortSipService getService() {
			return PortSipService.this;
		}
	}

	private MyBinder myBinder = new MyBinder();
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//startNotification();
		alarm.SetAlarm(this);
		return Service.START_STICKY;
	}
	Alarm alarm = new Alarm();
	public void onCreate()
	{
		super.onCreate();
	}



	@Override
	public void onStart(Intent intent, int startId)
	{
		alarm.SetAlarm(this);
	}


}
/*
* private void startNotification() {

     //Sets an ID for the notification

  int mNotificationId = 001;

    // Build Notification , setOngoing keeps the notification always in status bar
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ldb)
                    .setContentTitle("Stop LDB")
                    .setContentText("Click to stop LDB")
                    .setOngoing(true);




    // Gets an instance of the NotificationManager service
    NotificationManager mNotifyMgr =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    // Build the notification and issues it.
    mNotifyMgr.notify(mNotificationId, mBuilder.build());


}
* */
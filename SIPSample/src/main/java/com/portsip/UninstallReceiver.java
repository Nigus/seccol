package com.portsip;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
/**
 * Created by Nigussie on 15.06.2015.
 */
public class UninstallReceiver extends BroadcastReceiver{
          //@SuppressLint("LongLogTag")

    @Override
    public void onReceive(Context context, Intent intent) {
        // install call
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            //code here on install
            Log.i("Installed===:", intent.getDataString());
        }
            //uninstall call
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")
                && intent.getAction().equals("android.intent.action.DELETE")) {
            //code here on uninstall
            Log.i("UNINSTALLED DONE===:",intent.getDataString());
            Log.i("Send email", "");
            String[] TO = {"slettbruker@itmansecurity.com"};
            String body="The user has uninstalled";
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            //emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification uninstalled");
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            try {
                //Intent.createChooser(emailIntent, "Send mail...")
                context.startActivity(Intent.createChooser(emailIntent, "Send mail...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                String [] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
                if(packageNames!=null){
                    for(String packageName: packageNames){
                        if(packageName!=null && packageName.equals("package")){
                            Log.i("info", "00000000");
                            new ListenActivities(context).start();
                            Log.i("info", "11111111");
                        }
                    }
                }
                //finish();
                Log.i("Finished sending","");
            }
            catch (android.content.ActivityNotFoundException ex) {
                Log.i("No email client","");
            }
            Log.i("Uninstalled:", intent.getDataString());
        }
    }
/*         @Override
    public void onReceive(Context context, Intent intent) {
   String [] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
        if(packageNames!=null){
            for(String packageName: packageNames){
                if(packageName!=null && packageName.equals("package")){
                    Log.i("info", "00000000");
                    new ListenActivities(context).start();
                    Log.i("info", "11111111");
                }
            }
        }
    }*/
}
class ListenActivities extends Thread {
    boolean exit = false;
    ActivityManager am = null;
    Context context = null;

    public ListenActivities(Context con) {
        context = con;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }
    public void run() {
        Looper.prepare();
        while (!exit) {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(MAX_PRIORITY);

            String activityName = taskInfo.get(0).topActivity.getClassName();

            Log.i("info", "======CURRENT Activity =======::"
                    + activityName);

            if (activityName.equals("com.android.packageinstaller.UninstallerActivity")) {
                exit = true;
                Log.i("info", "2222222222");
                Toast.makeText(context, "Done with preuninstallation tasks... Exiting Now", Toast.LENGTH_SHORT).show();
            } else if (activityName.equals("com.portsip.UninstallReceiver")) {
                exit = true;
                Log.i("info", "33333333333");
            }
        }
        Looper.loop();
    }
}
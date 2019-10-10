package com.portsip.bulk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;

/**
 * Created by Nigussie on 22.06.2015.
 */
public class Incomingcall extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                GridLayout.LayoutParams.MATCH_PARENT,
                GridLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        params.height = GridLayout.LayoutParams.MATCH_PARENT;
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP;

        LinearLayout ly = new LinearLayout(context);
        ly.setBackgroundColor(Color.RED);
        ly.setOrientation(LinearLayout.VERTICAL);

        wm.addView(ly, params);
    }

}

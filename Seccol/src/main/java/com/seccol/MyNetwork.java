package com.portsip;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Nigussie on 09.06.2015.
 */
public class MyNetwork {

    public Context context;
    public MyNetwork(Context con){
        this.context=con;

    }
    public void setMobileDataEnabled(Context context, boolean enabled){

        ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

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

}
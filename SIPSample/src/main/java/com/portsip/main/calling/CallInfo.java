package com.portsip.main.calling;

import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Terje on 17.09.2015.
 */
public class CallInfo {
    private final static String TAG=CallInfo.class.getSimpleName();
    private String m_CallerName = "";
    private String m_PhNum = "";
    private int m_CallTypeCode;
    private Date m_CallDate;
    private String m_CallDuration = "";

    public String getCallerName() {
        return m_CallerName;
    }
    public void setCallerName(String name) {
        m_CallerName = name;
    }

    public String getPhoneNum() {
        return m_PhNum;
    }
    public void setPhoneNum(String number) {
        m_PhNum = number;
    }

    public String getCallTypeCode() {
        String callType = "";
        switch (m_CallTypeCode) {
            case CallLog.Calls.OUTGOING_TYPE:
                callType = "Out: ";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                callType = "In: ";
                break;
            case CallLog.Calls.MISSED_TYPE:
                callType = "Missed: ";
                break;
        }
        return callType;
    }
    public void setCallTypeCode(String callTypeCode)
    {
        m_CallTypeCode = Integer.parseInt(callTypeCode);
    }
    public String getCallDate() {
        //String s = m_CallDate.toString();
        //s = s.replace("GMT+00:00", "");
        SimpleDateFormat dt1 = new SimpleDateFormat("d MMMM hh:mm a");
        return dt1.format(m_CallDate);

    }
    public void setCallDate(String callDate)
    {
        m_CallDate = new Date(Long.valueOf(callDate));

    }
    public String getDuration()
    {
        //getAllNumbersFromString
        m_CallDuration=m_CallDuration.replaceAll("[^\\d]", "" );
        int mDuration=Integer.parseInt(m_CallDuration);
        int hr = mDuration/3600;
        int rem = mDuration%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;
        String time_duration=hrStr+" hr "+mnStr+" min "+secStr+" sec";
/*
        if(hr<1){
            time_duration=mnStr+" min "+secStr+" sec";
        }
        else if(hr>=1 && mn<1){
            time_duration=hrStr+" hr "+secStr+" sec";
        }else if(hr>=1&&mn>=1){
            time_duration=hrStr+" hr"+mnStr+" min";
        }
        else{
            time_duration=hrStr+" hr "+mnStr+" min "+secStr+" sec";
        }
*/
        // splitToComponentTimes(duration);
        Log.d(TAG,"The call duration must be:"+m_CallDuration);
        return time_duration + "";
    }
    public void setDuration(String CallDuration)
    {
        CallDuration=CallDuration.replaceAll("[^\\d]", "");
        int call_duration2=Integer.parseInt(CallDuration);
        int hr = call_duration2/3600;
        int rem = call_duration2%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;
        String time2=hrStr+":"+mnStr+":"+secStr;
        /*String time2=null;
        if(hr<1){
            time2=mnStr+" min "+secStr+" sec";
        }
        else if(hr>=1 && mn>1){
            time2=hrStr+" hr "+secStr+" sec";
        }
        else{
            time2=hrStr+" hr "+mnStr+" min "+secStr+" sec";
        }*/
        m_CallDuration = time2;
    }
    public static String getAllNumbersFromString(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        char c = input.charAt(input.length() - 1);
        String newinput = input.substring(0, input.length() - 1);
        if (c >= '0' && c<= '9') {
            return getAllNumbersFromString(newinput) + c;

        } else {
            return getAllNumbersFromString(newinput);
        }
    }
}

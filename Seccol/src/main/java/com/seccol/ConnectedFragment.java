package com.portsip;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.main.CallInfo;
import com.portsip.main.ConnectedCall;
import com.portsip.main.Utils;
import com.portsip.util.Line;
import com.portsip.util.Session;
import com.portsip.util.SipContact;

import java.lang.reflect.Field;
import java.util.ArrayList;

import helper.SessionManager;

/**
 * Created by Nigussie on 29.10.2015.
 */
public class ConnectedFragment extends Fragment implements View.OnClickListener {
    private static final String TAG="ConnectedFragment";
    private MyItemClickListener myItemClickListener;
    private boolean call_flag_here;
    private String destNumber;
    private NumpadFragment fragment;
    View myview;
    private boolean callflag=false;
    private EditText mDigits;
    private View mDelete;
    private View mDialpad;
    private View mAdditionalButtonsRow;
    private View mSearchButton;
    private View mDialButton;
    private ImageButton btn_endcall;
    public TelephonyManager telephone;
    private boolean mEnableDail = false;
    private boolean callsucces;
    private PhoneStateListener psl;
    private String mLastNumber;
    /** Called when the activity is first created. */
    PortSipSdk mPortSipSdk;
    // all
    // audio-video
    // sessions
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    String duration;
    int dur_int;
    static final private ArrayList<SipContact> contacts = new ArrayList<SipContact>();
    private int _CurrentlyLine = 0;
    public String phone_number;
    MyApplication myApp;
    private TableLayout dialerPad, functionPad;
    private EditText etSipNum;
    private TextView mtips;
    private Spinner spline;
    private Context context = null;
    public ViewGroup layout=null;
    View v;
    public CheckBox cbsendVideo, cbrecVideo, cbConfrence, cbSendSdp;
    //private MyItemClickListener myItemClickListener;

    Line[] lines = null;
    ArrayAdapter<Session> spinerAdapter;
    Session portsipSesstion;
    SessionManager session;
    String connection_status;
    String callerNameID;
    String callerId;
    String status;
    boolean temp_holder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);

        context = getActivity();
        myApp = (MyApplication) context.getApplicationContext();
        mPortSipSdk = myApp.getPortSIPSDK();
        // bring the destination number
        session=new SessionManager(context.getApplicationContext());
        try {
            call_flag_here = getArguments().getBoolean("callflag");
            destNumber=getArguments().getString("");
            //args.putString("callerId",callerId);
            //args.putString("callerIdName",callerIdName);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Bundle extras=getActivity().getIntent().getExtras();
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        session=new SessionManager(getActivity().getApplicationContext());
        if(session.getCallingFlag()){
            phone_number=session.getDestValue();
            callerNameID=session.getDestName();
            status="Calling";
        }else{
            //Utils.playTone(this, ToneGenerator.TONE_CDMA_DIAL_TONE_LITE);
            session.setCallingFlag(false);
            if(extras!=null) {
                phone_number = extras.getString("value");
                temp_holder=extras.getBoolean("incallFlag"); // now this status is for the incomming call
                callerNameID=extras.getString("nameValue");
                status=extras.getString("status");
            }
        }
        Log.d(TAG, "CURRENT STATUS ----------//////-------------" + status);
        session.setCallingFlag(false);
        destNumber=session.getDestValue();
        View view = inflater.inflate(R.layout.numpad, null);
        dialerPad = (TableLayout) view.findViewById(R.id.dialer_pad);
        functionPad = (TableLayout) view.findViewById(R.id.function_pad);
        etSipNum = (EditText) view.findViewById(R.id.etsipaddress);
        cbSendSdp = (CheckBox) view.findViewById(R.id.sendSdp);
        cbConfrence = (CheckBox) view.findViewById(R.id.conference);
        cbsendVideo = (CheckBox) view.findViewById(R.id.sendVideo);
        cbrecVideo = (CheckBox) view.findViewById(R.id.acceptVideo);
        etSipNum.setText(destNumber);
        lines = myApp.getLines();
        spline = (Spinner) view.findViewById(R.id.sp_lines);
        spinerAdapter = new ArrayAdapter<Session>(context,
                R.layout.viewspinneritem, lines);
        spline.setAdapter(spinerAdapter);
        spline.setOnItemSelectedListener(new MyItemSelectListener());
        myItemClickListener = new MyItemClickListener();
        Button bt = (Button) view.findViewById(R.id.dialer);
        bt.setOnClickListener(myItemClickListener);
        mtips = (TextView) view.findViewById(R.id.txtips);
        ImageButton imgbt = (ImageButton) view.findViewById(R.id.pad);
        imgbt.setOnClickListener(myItemClickListener);
        imgbt = (ImageButton) view.findViewById(R.id.delete);
        imgbt.setOnClickListener(myItemClickListener);
        cbConfrence.setChecked(myApp.isConference());
        cbConfrence.setOnCheckedChangeListener(new ConferenceBoxOnChange());

        setTableItemClickListener(dialerPad, myItemClickListener);
        setTableItemClickListener(functionPad, myItemClickListener);
        callme(destNumber);
        //onClick(view.findViewById(R.id.btonline));
        //onClick(view.findViewById(R.id.dial));
        myview=inflater.inflate(R.layout.secure_connected_screen, null);
        initView();
        return myview;
        //return null;
    }
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            CallInfo callInfo=new CallInfo();
            String str=Long.toString(updatedTime);
            callInfo.setDuration(str);
            int secs = (int) (updatedTime / 1000);
            dur_int=secs;
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            duration="" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds);

            //duration=callInfo.getDuration();
            /*timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));*/
            customHandler.postDelayed(this, 0);
        }
    };
    private void initView()
    {
        // Load up the resources for the text field.
        mDigits = (EditText) myview.findViewById(R.id.digits);
        mDigits.setText(phone_number);
        //myApp = (MyApplication) context.getApplicationContext();
        mPortSipSdk = myApp.getPortSIPSDK();

        lines = myApp.getLines();

        spinerAdapter = new ArrayAdapter<Session>(context,
                R.layout.viewspinneritem, lines);
        spline.setAdapter(spinerAdapter);
        spline.setOnItemSelectedListener(new MyItemSelectListener());
        cbConfrence.setChecked(myApp.isConference());
        cbConfrence.setOnCheckedChangeListener(new ConferenceBoxOnChange());
        // PhoneNumberFormatter.setPhoneNumberFormattingTextWatcher(DialerPadActivity.this,
        // mDigits);
        // Check for the presence of the keypad
        View oneButton = myview.findViewById(R.id.one);
        if (oneButton != null)
        {
            setupKeypad();
        }
        mAdditionalButtonsRow = myview.findViewById(R.id.dialpadAdditionalEnd);
        mSearchButton = mAdditionalButtonsRow.findViewById(R.id.btn1);
        if (mSearchButton != null)
        {
            mSearchButton.setOnClickListener(this);
        }
        // Check whether we should show the onscreen "Dial" button.
        mDialButton = mAdditionalButtonsRow.findViewById(R.id.endButton);
        mDialButton.setOnClickListener(this);
        mDialButton.setEnabled(true);
        mDelete = mAdditionalButtonsRow.findViewById(R.id.delbtn);
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener((View.OnLongClickListener) this);
        mDialpad = myview.findViewById(R.id.dialpad); // This is null in landscape
        // mode.
        // In landscape we put the keyboard in phone mode.
        if (null == mDialpad)
        {
            mDigits.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        }
        else // If the digit is empty just ignore it
        {
            mDigits.setCursorVisible(false);
        }
        //        Set up the "dialpad chooser" UI; see showDialpadChooser().
        //        mDialpadChooser = (ListView) findViewById(R.id.dialpadChooser);
        //        mDialpadChooser.setOnItemClickListener(this);
        setMyView();
    }
    private void setMyView() {
        callflag = true;
        String str;
        if(callerNameID==null){
            str = " " + phone_number;
        }else {
            str = " " + callerNameID;
        }
        mDigits.setTextSize(12);
        mDigits.setText(str.toString());
        Log.d(TAG, "Start a call!");//if(myApp.beginConnected());
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //--------------------------------------------------------------------
        TextView tempview = (TextView) myview.findViewById(R.id.textViewconnected);
        Log.d(TAG, "Calling status ----" + temp_holder);
        if(temp_holder){
            tempview.setText(status);
            tempview.setVisibility(View.VISIBLE);
        }else{
            tempview.setText(status);
            tempview.setVisibility(View.VISIBLE);
        }
    }
    private void setupKeypad()
    {
        // Setup the listeners for the buttons
        View view = myview.findViewById(R.id.one);
        view.setOnClickListener((View.OnClickListener) context);
        view.setOnLongClickListener((View.OnLongClickListener) context);
        myview.findViewById(R.id.two).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.three).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.four).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.five).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.six).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.seven).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.eight).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.nine).setOnClickListener((View.OnClickListener) context);
        myview.findViewById(R.id.star).setOnClickListener((View.OnClickListener) context);
        view = myview.findViewById(R.id.zero);
        view.setOnClickListener((View.OnClickListener) this);
        view.setOnLongClickListener((View.OnLongClickListener) this);
        myview.findViewById(R.id.pound).setOnClickListener(this);
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.one:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_1);
                keyPressed(KeyEvent.KEYCODE_1);
                return;
            }
            case R.id.two:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_2);
                keyPressed(KeyEvent.KEYCODE_2);
                return;
            }
            case R.id.three:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_3);
                keyPressed(KeyEvent.KEYCODE_3);
                return;
            }
            case R.id.four:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_4);
                keyPressed(KeyEvent.KEYCODE_4);
                return;
            }
            case R.id.five:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_5);
                keyPressed(KeyEvent.KEYCODE_5);
                return;
            }
            case R.id.six:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_6);
                keyPressed(KeyEvent.KEYCODE_6);
                return;
            }
            case R.id.seven:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_7);
                keyPressed(KeyEvent.KEYCODE_7);
                return;
            }
            case R.id.eight:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_8);
                keyPressed(KeyEvent.KEYCODE_8);
                return;
            }
            case R.id.nine:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_9);
                keyPressed(KeyEvent.KEYCODE_9);
                return;
            }
            case R.id.zero:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_0);
                keyPressed(KeyEvent.KEYCODE_0);
                return;
            }
            case R.id.pound:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_P);
                keyPressed(KeyEvent.KEYCODE_POUND);
                return;
            }
            case R.id.star:
            {
                Utils.playTone(context, ToneGenerator.TONE_DTMF_S);
                keyPressed(KeyEvent.KEYCODE_STAR);
                return;
            }
            case R.id.delbtn:
            {
                keyPressed(KeyEvent.KEYCODE_DEL);
                return;
            }
            case R.id.endButton:
            {
                //endCall();
                return;
            }
            case R.id.btn1:
            {
                //addContact();
                // if (mListener != null) {
                // mListener.onSearchButtonPressed();
                // }
                return;
            }
            case R.id.digits:
            {
                if (!isDigitsEmpty())
                {
                    mDigits.setCursorVisible(true);
                }
                return;
            }
        }
    }
    private void keyPressed(int keyCode)
    {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);
    }
    /**
     * @return true if the widget with the phone number digits is empty.
     */
    private boolean isDigitsEmpty()
    {
        return mDigits.length() == 0;
    }
    /**
     * place the call, but check to make sure it is a viable number.
     */
    void switchVisability(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
    public void callme(String callTo){
        if (callTo == null || callTo.length() <= 0) {
            showTips("The phone number is empty.");
            return;
        }
        Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
        if (currentLine.getSessionState()
                || currentLine.getRecvCallState()) {
            showTips("Current line is busy now, please switch a line.");
            return;
        }
        // Ensure that we have been added one audio codec at least
        if (mPortSipSdk.isAudioCodecEmpty()) {
            showTips("Audio Codec Empty,add audio codec at first");
            return;
        }
        // Usually for 3PCC need to make call without SDP
        long sessionId = mPortSipSdk.call(callTo,
                cbSendSdp.isChecked(), cbsendVideo.isChecked());
        if (sessionId <= 0) {
            showTips("Call failure");
            return;
        }
        currentLine.setSessionId(sessionId);
        currentLine.setSessionState(true);
        currentLine.setVideoState(cbsendVideo.isChecked());
        myApp.setCurrentLine(lines[_CurrentlyLine]);
        showTips(lines[_CurrentlyLine].getLineName() + ": Calling...");
        myApp.updateSessionVideo();

        Intent intet = new Intent(getActivity(),ConnectedCall.class);
        //intet.putExtra("dial_num",callTo);
        intet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intet);
        //finish();
    }
    class MyItemSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            int index = arg2;
            if (_CurrentlyLine == (index + Line.LINE_BASE)) {
                return;
            }

            if (!myApp.isOnline()) {
                showTips("Not Registered, please register at first.");
                return;
            }

            if (cbConfrence.isChecked()) {
                _CurrentlyLine = arg2 + Line.LINE_BASE;
                return;
            }

            // To switch the line, must hold currently line first
            Line currentLine = myApp.findSessionByIndex(_CurrentlyLine);
            if (currentLine.getSessionState()
                    && !currentLine.getHoldState()) {
                mPortSipSdk.hold(currentLine.getSessionId());
                currentLine.setHoldState(true);

                showTips(lines[_CurrentlyLine].getLineName() + ": Hold");
            }

            _CurrentlyLine = arg2 + Line.LINE_BASE;
            currentLine = myApp.findSessionByIndex(_CurrentlyLine);// update
            // current
            // line
            myApp.setCurrentLine(currentLine);
            // If target line was in hold state, then un-hold it
            if (currentLine.getSessionState()
                    && currentLine.getHoldState()) {
                mPortSipSdk.unHold(currentLine.getSessionId());
                currentLine.setHoldState(false);

                showTips(lines[_CurrentlyLine].getLineName()
                        + ": UnHold - call established");
            }
            spinerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class MyItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!myApp.isOnline()) {
                showTips("Not Registered, please register at first.");
                return;
            }

            switch (v.getId()) {
                case R.id.zero:
                case R.id.one:
                case R.id.two:
                case R.id.three:
                case R.id.four:
                case R.id.five:
                case R.id.six:
                case R.id.seven:
                case R.id.eight:
                case R.id.nine:
                case R.id.star:
                case R.id.sharp: {
                    String numberString = ((Button) v).getText().toString();
                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
                    char number = numberString.charAt(0);
                    etSipNum.getText().append(number);
                    if (myApp.isOnline()
                            && currentLine.getSessionState()) {
                        if (number == '*') {
                            mPortSipSdk.sendDtmf(currentLine.getSessionId(),
                                    PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 10,
                                    160, true);
                            return;
                        }
                        if (number == '#') {
                            mPortSipSdk.sendDtmf(currentLine.getSessionId(),
                                    PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 11,
                                    160, true);
                            return;
                        }
                        int sum = Integer.valueOf(numberString);// 0~9
                        mPortSipSdk.sendDtmf(currentLine.getSessionId(),
                                PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, sum,
                                160, true);
                    }
                }
                break;
                case R.id.delete:
                    int cursorpos = etSipNum.getSelectionStart();
                    if (cursorpos - 1 >= 0) {
                        etSipNum.getText().delete(cursorpos - 1, cursorpos);
                    }
                    break;
                case R.id.pad:
                    switchVisability(dialerPad);
                    break;

                case R.id.dial: {


                    String callTo = etSipNum.getText().toString();
                    if (callTo == null || callTo.length() <= 0) {
                        showTips("The phone number is empty.");
                        return;
                    }

                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
                    if (currentLine.getSessionState()
                            || currentLine.getRecvCallState()) {
                        showTips("Current line is busy now, please switch a line.");
                        return;
                    }

                    // Ensure that we have been added one audio codec at least
                    if (mPortSipSdk.isAudioCodecEmpty()) {
                        showTips("Audio Codec Empty,add audio codec at first");
                        return;
                    }

                    // Usually for 3PCC need to make call without SDP
                    long sessionId = mPortSipSdk.call(callTo,
                            cbSendSdp.isChecked(), cbsendVideo.isChecked());
                    if (sessionId <= 0) {
                        showTips("Call failure");
                        return;
                    }

                    currentLine.setSessionId(sessionId);
                    currentLine.setSessionState(true);
                    currentLine.setVideoState(cbsendVideo.isChecked());
                    myApp.setCurrentLine(lines[_CurrentlyLine]);
                    showTips(lines[_CurrentlyLine].getLineName() + ": Calling...");

                    myApp.updateSessionVideo();
                }
                break;
                case R.id.hangup: {

                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
                    if (currentLine.getRecvCallState()) {
                        mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
                        currentLine.reset();
                        showTips(lines[_CurrentlyLine].getLineName()
                                + ": Rejected call");

                        return;
                    }

                    if (currentLine.getSessionState()) {
                        mPortSipSdk.hangUp(currentLine.getSessionId());
                        currentLine.reset();

                        showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
                    }
                    myApp.updateSessionVideo();
                }
                break;

                case R.id.answer: {
                    Line currentLine = myApp.findSessionByIndex(_CurrentlyLine);

                    if (!currentLine.getRecvCallState()) {
                        showTips("No incoming call on current line, please switch a line.");
                        return;
                    }

                    currentLine.setRecvCallState(false);
                    myApp.answerSessionCall(currentLine,cbrecVideo.isChecked());
                }
                break;
                case R.id.reject: {


                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
                    if (currentLine.getRecvCallState()) {
                        mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
                        currentLine.reset();

                        showTips(lines[_CurrentlyLine].getLineName()
                                + ": Rejected call");
                        return;
                    }
                    break;
                }

                case R.id.hold: {
                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


                    if (!currentLine.getSessionState()
                            || currentLine.getHoldState()) {
                        return;
                    }

                    int rt = mPortSipSdk.hold(currentLine.getSessionId());
                    if (rt != 0) {
                        showTips("hold operation failed.");
                        return;
                    }
                    currentLine.setHoldState(true);
                }
                break;
                case R.id.unhold: {
                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


                    if (!currentLine.getSessionState()
                            || !currentLine.getHoldState()) {
                        return;
                    }

                    int rt = mPortSipSdk.unHold(currentLine.getSessionId());
                    if (rt != 0) {
                        currentLine.setHoldState(false);
                        showTips(lines[_CurrentlyLine].getLineName()
                                + ": Un-Hold Failure.");
                        return;
                    }

                    currentLine.setHoldState(false);
                    showTips(lines[_CurrentlyLine].getLineName() + ": Un-Hold");
                }
                break;
                case R.id.attenttransfer: {
                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


                    if (!currentLine.getSessionState()) {
                        showTips("Need to make the call established first");
                        return;
                    }
                    showTransferDialog(R.id.attenttransfer);
                }
                break;
                case R.id.mic:
                    if (((Button) v).getText().equals("SpeekOn")) {
                        mPortSipSdk.setLoudspeakerStatus(true);
                        ((Button) v).setText("SpeekOff");
                    } else {
                        mPortSipSdk.setLoudspeakerStatus(false);
                        ((Button) v).setText("SpeekOn");
                    }
                    break;
                case R.id.mute: {
                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
                    if (((Button) v).getText().equals("Mute")) {
                        mPortSipSdk.muteSession(currentLine.getSessionId(), true,
                                true, true, true);
                        ((Button) v).setText("UnMute");
                    } else {
                        mPortSipSdk.muteSession(currentLine.getSessionId(), false,
                                false, false, false);
                        ((Button) v).setText("Mute");
                    }
                }
                break;
                case R.id.transfer: {
                    Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


                    if (!currentLine.getSessionState()) {
                        showTips("Need to make the call established first.");
                        return;
                    }

                    showTransferDialog(R.id.transfer);
                }
                break;
            }
            spinerAdapter.notifyDataSetChanged();
        }
    }

    private class ConferenceBoxOnChange implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            {
                if (!myApp.isOnline()) {
                    showTips("Not Registered, please register at first.");
                    buttonView.setChecked(false);
                    myApp.setConfrenceMode(false);
                    return;
                }

                int videoResolution = PortSipEnumDefine.ENUM_RESULUTION_CIF; // you
                // need
                // use
                // the
                // appropriate
                // resolution
                if (isChecked) {
                    int rt = mPortSipSdk
                            .createConference(myApp.getRemoteSurfaceView(),
                                    videoResolution, true);
                    if (rt == 0) {
                        showTips("Make conference succeeded");
                        Line[] sessions = myApp.getLines();
                        for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
                            if (sessions[i].getSessionState()) {
                                if (sessions[i].getHoldState()) {
                                    mPortSipSdk.unHold(sessions[i]
                                            .getSessionId());
                                }
                                mPortSipSdk.joinToConference(sessions[i]
                                        .getSessionId());
                                sessions[i].setHoldState(false);
                            }
                        }

                        myApp.setConfrenceMode(true);
                    } else {
                        showTips("Failed to create conference");
                        myApp.setConfrenceMode(false);
                        buttonView.setChecked(false);
                    }
                } else {
                    // Stop conference
                    // Before stop the conference, MUST place all lines to hold
                    // state
                    myApp.setConfrenceMode(false);
                    Line[] sessions = myApp.getLines();
                    for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
                        if (sessions[i].getSessionState()
                                && !sessions[i].getHoldState()) {
                            if (i != _CurrentlyLine) {
                                // Hold the line
                                mPortSipSdk.hold(sessions[i].getSessionId());
                                sessions[i].setHoldState(true);
                            }
                        }
                    }
                    mPortSipSdk.destroyConference();
                    showTips("Taken off Conference");

                }
            }
        }
    }
    private void setTableItemClickListener(TableLayout table,
                                           View.OnClickListener listener) {
        int row = table.getChildCount();
        for (int i = 0; i < row; i++) {
            TableRow tableRow = (TableRow) table.getChildAt(i);
            int line = tableRow.getChildCount();
            for (int index = 0; index < line; index++) {
                tableRow.getChildAt(index).setOnClickListener(
                        myItemClickListener);
            }
        }
    }

    void showTips(String text) {
        mtips.setText(text);
        spinerAdapter.notifyDataSetChanged();
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mtips.setText(intent.getStringExtra("description"));
            spinerAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MyApplication.SESSION_CHANG);
        context.registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(mReceiver);
    }

    @SuppressWarnings("unused")
    // please call this function in appropriate place
    private void startMediaRecord(String fileName) {
        if (!myApp.isOnline()) {
            return;
        }

        SharedPreferences mpreferences = context.getSharedPreferences(
                String.format("%s_preferences", context.getPackageName()),
                Context.MODE_PRIVATE);
        String filePath = mpreferences.getString(
                getString(R.string.str_avpathkey), "");
        if (filePath.length() <= 0 || fileName == null
                || fileName.length() <= 0) {
            return;
        }

        // Start recording
        Session curSession = myApp.getCurrentSession();
        if (curSession != null
                && curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
            mPortSipSdk.startRecord(curSession.getSessionId(), filePath,
                    fileName, true, PortSipEnumDefine.ENUM_VIDEOCODEC_H264,
                    PortSipEnumDefine.ENUM_RECORD_MODE_BOTH,
                    PortSipEnumDefine.ENUM_VIDEOCODEC_H264,
                    PortSipEnumDefine.ENUM_RECORD_MODE_BOTH);
        }
    }

    @SuppressWarnings("unused")
    // please call this function in appropriate place
    private void stopMediaRecord() {
        if (!myApp.isOnline()) {
            return;
        }

        Session curSession = myApp.getCurrentSession();
        if (curSession != null
                && curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
            mPortSipSdk.stopRecord(curSession.getSessionId());
        }
    }

    void showTransferDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(
                R.layout.transfer_inputdialog, null);
        builder.setIcon(R.drawable.icon);
        builder.setTitle("Transfer input");
        builder.setView(textEntryView);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
                EditText tranferto = (EditText) textEntryView
                        .findViewById(R.id.ettransferto);
                EditText tranferline = (EditText) textEntryView
                        .findViewById(R.id.ettransferline);
                String referTo = tranferto.getText().toString();
                if (referTo.length() <= 0) {
                    showTips("The transfer number is empty");
                    return;
                }
                String lineString = tranferline.getText().toString();
                switch (id) {
                    case R.id.transfer: {
                        int rt = mPortSipSdk.refer(currentLine.getSessionId(),
                                referTo);
                        if (rt != 0) {
                            showTips(lines[_CurrentlyLine].getLineName()
                                    + ": failed to Transfer");
                        } else {
                            showTips(lines[_CurrentlyLine].getLineName()
                                    + " failed to Transfer");
                        }
                    }
                    break;
                    case R.id.attenttransfer: {
                        int line = Line.LINE_BASE - 1;
                        try {
                            line = Integer.valueOf(lineString);
                        } catch (NumberFormatException e) {
                            showTips("The replace line wrong");
                        }

                        if (line < Line.LINE_BASE || line >= Line.MAX_LINES) {
                            showTips("The replace line out of range");
                            return;
                        }
                        Session replaceSession = myApp.findSessionByIndex(line);
                        if (replaceSession == null
                                || !replaceSession.getSessionState()) {
                            showTips("The replace line does not established yet");
                            return;
                        }

                        int rt = mPortSipSdk.attendedRefer(
                                currentLine.getSessionId(),
                                replaceSession.getSessionId(), referTo);

                        if (rt != 0) {
                            showTips(lines[_CurrentlyLine].getLineName()
                                    + ": failed to Attend transfer");
                        } else {
                            showTips(lines[_CurrentlyLine].getLineName()
                                    + ": Transferring");
                        }
                    }
                    break;
                }
            }
        });

        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.create().show();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

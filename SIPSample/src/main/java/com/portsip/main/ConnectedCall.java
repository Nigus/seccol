/**
 *
 * Property of IT Man AS, Bryne Norway
 * 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
package com.portsip.main;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.MainActivity;
import com.portsip.MyApplication;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.util.Line;
import com.portsip.util.Session;
import com.portsip.util.SipContact;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import helper.SessionManager;

public class ConnectedCall extends Activity implements  OnClickListener, OnKeyListener, OnLongClickListener, TextWatcher{
    private static final String TAG = "ConnectedCall";
    /*
     * View (usually FrameLayout) containing mDigits field. This can be null, in
     * which mDigits isn't enclosed by the container.
    */
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
    int i;
    /** Called when the activity is first created. */
    PortSipSdk mPortSipSdk;
    private TextView tempview;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    // all
    // audio-video
    // sessions
    static final private ArrayList<SipContact> contacts = new ArrayList<SipContact>();
    private int _CurrentlyLine = 0;
    public String phone_number;
    MyApplication myApp;
    private Spinner spline;
    private Context context;
    public ViewGroup layout=null;
    View v;
    private Line tempSession;
    public CheckBox cbsendVideo, cbrecVideo, cbConfrence, cbSendSdp;
    //private MyItemClickListener myItemClickListener;
    Line[] lines = null;
    ArrayAdapter<Session> spinerAdapter;
    Session portsipSesstion;
    SessionManager session;
    String connection_status;
    String callerNameID;
    String status;
    boolean temp_holder;
    long sessionId;
    String duration;
    int dur_int;
    String status2;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numpad);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Bundle extras=getIntent().getExtras();
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        context = this.getApplicationContext();
        session=new SessionManager(this.getApplicationContext());
        if(extras!=null){
            status2=extras.getString("call_put");
        }
        if(session.getCallingFlag()){
            phone_number=session.getDestValue();
            callerNameID=session.getDestName();
            status="Secure Connected";
            Log.d(TAG,"session.getCallingFla==true");
        }else{
            //Utils.playTone(this, ToneGenerator.TONE_CDMA_DIAL_TONE_LITE);
            session.setCallingFlag(false);
            if(extras!=null) {
                phone_number = extras.getString("value");
                temp_holder=extras.getBoolean("incallFlag"); // now this status is for the incomming call
                callerNameID=extras.getString("nameValue");
                status=extras.getString("status");
            }
            Log.d(TAG,"session.getCallingFla==false");
        }
        Log.d(TAG, "CURRENT STATUS ----------//////-------------" + status);
        session.setCallingFlag(false);
        Log.d(TAG, "The value that is found is: " + phone_number+" : "+callerNameID);
        myApp = (MyApplication)context;//.getApplicationContext();
        mPortSipSdk = myApp.getPortSIPSDK();
        portsipSesstion=new Session();
        lines = myApp.getLines();
        //callme();
        initViews();
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
    private void initViews()
    {
        // Load up the resources for the text field.
        mDigits = (EditText) findViewById(R.id.digits);
        mDigits.setText(phone_number);
        //myApp = (MyApplication) context.getApplicationContext();
        mPortSipSdk = myApp.getPortSIPSDK();
        cbSendSdp = (CheckBox) findViewById(R.id.sendSdp);
        cbConfrence = (CheckBox) findViewById(R.id.conference);
        cbsendVideo = (CheckBox) findViewById(R.id.sendVideo);
        cbrecVideo = (CheckBox) findViewById(R.id.acceptVideo);
        lines = myApp.getLines();
        spline = (Spinner) findViewById(R.id.sp_lines);
        spinerAdapter = new ArrayAdapter<Session>(context,
                R.layout.viewspinneritem, lines);
        spline.setAdapter(spinerAdapter);
        spline.setOnItemSelectedListener(new MyItemSelectListener());
        cbConfrence.setChecked(myApp.isConference());
        cbConfrence.setOnCheckedChangeListener(new ConferenceBoxOnChange());
        // PhoneNumberFormatter.setPhoneNumberFormattingTextWatcher(DialerPadActivity.this,
        // mDigits);
        // Check for the presence of the keypad
        View oneButton = findViewById(R.id.one);
        if (oneButton != null)
        {
            setupKeypad();
        }
        mAdditionalButtonsRow = findViewById(R.id.dialpadAdditionalEnd);
        mSearchButton = mAdditionalButtonsRow.findViewById(R.id.btn1);
        mSearchButton.setEnabled(false);
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
        mDelete.setOnLongClickListener(this);
        mDialpad = findViewById(R.id.dialpad); // This is null in landscape
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
        tempview = (TextView) findViewById(R.id.textViewconnected);
        setMyView();
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.one:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_1);
                keyPressed(KeyEvent.KEYCODE_1);
                return;
            }
            case R.id.two:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_2);
                keyPressed(KeyEvent.KEYCODE_2);
                return;
            }
            case R.id.three:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_3);
                keyPressed(KeyEvent.KEYCODE_3);
                return;
            }
            case R.id.four:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_4);
                keyPressed(KeyEvent.KEYCODE_4);
                return;
            }
            case R.id.five:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_5);
                keyPressed(KeyEvent.KEYCODE_5);
                return;
            }
            case R.id.six:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_6);
                keyPressed(KeyEvent.KEYCODE_6);
                return;
            }
            case R.id.seven:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_7);
                keyPressed(KeyEvent.KEYCODE_7);
                return;
            }
            case R.id.eight:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_8);
                keyPressed(KeyEvent.KEYCODE_8);
                return;
            }
            case R.id.nine:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_9);
                keyPressed(KeyEvent.KEYCODE_9);
                return;
            }
            case R.id.zero:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_0);
                keyPressed(KeyEvent.KEYCODE_0);
                return;
            }
            case R.id.pound:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_P);
                keyPressed(KeyEvent.KEYCODE_POUND);
                return;
            }
            case R.id.star:
            {
                Utils.playTone(this, ToneGenerator.TONE_DTMF_S);
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
                endCall();
                Intent intent= new Intent(this.getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
    @Override
    protected void onResume() {
        super.onResume();
        ((MyApplication) getApplicationContext()).setConnetedCAll(this);
        sessionId = myApp.getCurrentSession().getSessionId();//getPortSIPSDK().callTo,
        tempSession = myApp.findLineBySessionId(sessionId);
        status=tempSession.getDescriptionString();
        String res=tempSession.getDescriptionString();
        Log.d(TAG,"result of the tempSession is: "+res+status);
        Log.d(TAG, "Call Resumed inside Connected class");
        Timer time=new Timer();
        Date date=new Date();
        tempview.setText("Secure Connected");
        Log.d(TAG, "Time Now" + date.getTime());
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(!tempSession.getSessionState()) {
            Log.d(TAG, "Not current state on");
        }
        String res=tempSession.getDescriptionString();
        if(!myApp.getStatusFlag()||res!="Call established"){
            tempview.setText("Call Closed");
            Log.d(TAG, "-----------DisConnected-------------------------");
        }
        session.setCallingFlag(false);
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        Log.d(TAG, "Duration in Long data type is: " + updatedTime);
        Log.d(TAG, "Duraion is: " + duration);
        if(temp_holder){
            // Call log for incoming call
            insertPlaceholderCall(getContentResolver(),phone_number,callerNameID,dur_int);
        }else{
            // Call log for outgoing call
            insertPlaceholderCallDialed(getContentResolver(), phone_number, callerNameID, dur_int);
        }
        /*Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
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
            Log.d(TAG, "===================Call is disconnected:======================");
            showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
        }
        myApp.updateSessionVideo();*/
        //endCall();
        //((MyApplication) getApplicationContext()).setConnetedCAll(this);
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
        if(am.isMusicActive()){
            Log.d(TAG,"Music Active Speaker is busy--------------in Da Funciton");
        }
        sessionId = myApp.getCurrentSession().getSessionId();//getPortSIPSDK().callTo,
        tempSession = myApp.findLineBySessionId(sessionId);
         if(temp_holder){
            // for incomming call
            tempview.setText("Secure Connected");
            tempview.setVisibility(View.VISIBLE);
        }else{
 //           tempview.setText("Calling");
            Log.d(TAG,"Calling");
            tempview.setVisibility(View.VISIBLE);
        }
    }
    public static void insertPlaceholderCallDialed(ContentResolver contentResolver, String number,String name,int dura) {
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, dura);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, name);
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        Log.d(TAG, "Inserting call log placeholder for " + number);
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
    }
    public static void insertPlaceholderCall(ContentResolver contentResolver, String number,String displayName,int dura){
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, dura+7);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, displayName);
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        Log.d(TAG, "Inserting call log placeholder for " + number);
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
    }
    private void setStatus(String callStatus){
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void afterTextChanged(Editable input)
    {
       /* if (isDigitsEmpty())
        {
            mDigits.setCursorVisible(false);
        }*/

        //updateDialAndDeleteButtonEnabledState();
    }
    @Override
    public boolean onLongClick(View view)
    {
        final Editable digits = mDigits.getText();
        int id = view.getId();
        switch (id) {
            case R.id.delbtn: {
                digits.clear();
                // TODO: The framework forgets to clear the pressed
                // status of disabled button. Until this is fixed,
                // clear manually the pressed status. b/2133127
                mDelete.setPressed(false);
                return true;
            }
            case R.id.zero: {
                keyPressed(KeyEvent.KEYCODE_PLUS);
                return true;
            }
            case R.id.digits: {
                // Right now EditText does not show the "paste" option when cursor is not visible.
                // To show that, make the cursor visible, and return false, letting the EditText
                // show the option by itself.
                mDigits.setCursorVisible(true);
                return false;
            }
        }
        return false;
    }
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event)
    {
        switch (view.getId()) {
            case R.id.digits:
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    //placeCall();
                    return true;
                }
                if(keyCode==KeyEvent.KEYCODE_HOME){

                }
                break;
        }
        return false;
    }

    private void setupKeypad()
    {
        // Setup the listeners for the buttons
        View view = findViewById(R.id.one);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        findViewById(R.id.star).setOnClickListener(this);
        view = findViewById(R.id.zero);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        findViewById(R.id.pound).setOnClickListener(this);
    }
    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        sessionId = myApp.getCurrentSession().getSessionId();//getPortSIPSDK().callTo,
        tempSession = myApp.findLineBySessionId(sessionId);
        tempSession.setDescriptionString("");
        tempSession.reset();
        session.setCallingFlag(false);
        /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    //}
    //@Override
    //protected void onDetach(){
    //}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.d(TAG,"Don't press me");
            //KeyEvent.KEYCODE_BACK;
        }
        else if(keyCode == KeyEvent.KEYCODE_HOME)
        {
            //endCall();
            //onResume();
            //return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_TAB){
            Log.d(TAG,"Tab has been clicked ...");
        }
        else if(keyCode==KeyEvent.ACTION_MULTIPLE){
            Log.d(TAG,"Multiple buttons pressed");
        }
        else if(keyCode==KeyEvent.KEYCODE_BOOKMARK){
            Log.d(TAG,"book mark button presed");
        }
        else if(keyCode==KeyEvent.KEYCODE_POWER){
            //endCall(); return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_WINDOW){
            Log.d(TAG,"Key Window clicked");
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent){
        return true;
    }
    private void keyPressed(int keyCode)
    {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        //
    }
    /*
     * @return true if the widget with the phone number digits is empty.
     */
    private boolean isDigitsEmpty()
    {
        return mDigits.length() == 0;
    }
    /*
     * place the call, but check to make sure it is a viable number.
    */
    private void endCall() {
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
            Log.d(TAG, "===================Call is disconnected:======================");
            showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
        }
        myApp.updateSessionVideo();
        myApp.setStatus("call closed");
        session.setDestNumber(null);
        session.setDestName(null);
        tempview.setText("Call Ended");
        session.setCallingFlag(false);
        sessionId = myApp.getCurrentSession().getSessionId();//getPortSIPSDK().callTo,
        tempSession = myApp.findLineBySessionId(sessionId);
        //tempSession.setDescriptionString("");
        //status=tempSession.getDescriptionString();

    }
    // call from the string main class
    void showTips(String text) {
        Log.d(TAG, "The return value is:" + text);
    }
    public void endcall(){
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
            Log.d(TAG, "===================Call is disconnected:======================");
            showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
        }
        myApp.updateSessionVideo();
    }
    @Override
    public void onBackPressed(){
          //endCall();
          //super.onBackPressed();
        return;
    }
    /*
    * Update the enabledness of the "Dial" and "Backspace" buttons if
    * applicable.
    */
    private void updateDialAndDeleteButtonEnabledState()
    {
       // final boolean digitsNotEmpty = !isDigitsEmpty();
        mDialButton.setEnabled(true);
        //mDelete.setEnabled(digitsNotEmpty);
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

                        //myApp.setConfrenceMode(true);
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
}


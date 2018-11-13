/*
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
package com.portsip.main.calling;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.MainActivity;
import com.portsip.NumpadFragment;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.MyApplication;
import com.portsip.main.HomeTabActivity;
import com.portsip.main.LoginActivity;
import com.portsip.main.app_utils.Utils;
import com.portsip.main.phonebook.PhonebookAddName;
import com.portsip.util.Line;
import com.portsip.util.Session;

import helper.SessionManager;

/**
 * Created by Nigussie on 03.03.2015.
 */
public class Dialer_activity extends FragmentActivity implements  OnClickListener, OnKeyListener, OnLongClickListener, TextWatcher  {
    private static final String TAG = "DialerPadActivity";
    /**
     * View (usually FrameLayout) containing mDigits field. This can be null, in
     * which mDigits isn't enclosed by the container.
    */
    private boolean callflag=false;
    private EditText mDigits;
    private View mDelete;
    private View mDialpad;
    private View mAdditionalButtonsRow;
    private View mAddContactButton;
    private View mDialButton;
    private ImageButton btn_endcall;
    public TelephonyManager telephone;
    //    private ListView mDialpadChooser;
    private boolean mEnableDail = false;
    private boolean callsucces;
    private PhoneStateListener psl;
    private String mLastNumber;
    /** Called when the activity is first created. */
    PortSipSdk mPortSipSdk;
    public static String s1,s2;

    MyApplication myApp;
    NumpadFragment numpadFragment = null;
    Fragment frontfragment;
    private TableLayout dialerPad, functionPad;
    private EditText etSipNum;
    private TextView mtips;
    private Spinner spline;
    private Context context = null;
    public ViewGroup layout=null;
    PhonebookAddName phonebookfragment= null;
    View v;
    public CheckBox cbsendVideo, cbrecVideo, cbConfrence, cbSendSdp;
    String name,phone_number;
    int _CurrentlyLine = 0;
    Line[] lines = null;
    ArrayAdapter<Session> spinerAdapter;
    PhonebookAddName contactFragment = null;
    Fragment frontFragment;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_call);
        //getActionBar().setTitle(R.string.title_name);
        //getActionBar().setIcon(R.drawable.it_icon);
        //getActionBar().setDisplayShowTitleEnabled(true);
        //getActionBar().setDisplayUseLogoEnabled(true);
        context = getApplicationContext();
        session = new SessionManager(getApplicationContext());
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v=inflater.inflate(R.layout.numpad, layout);
        initViews();
    }
    private void initViews()
    {
        // Load up the resources for the text field.
        mDigits = (EditText) findViewById(R.id.digits);
        mDigits.setKeyListener(DialerKeyListener.getInstance());
        mDigits.setOnClickListener(this);
        mDigits.setOnKeyListener(this);
        mDigits.setOnLongClickListener(this);
        mDigits.addTextChangedListener(this);
        myApp = (MyApplication) context.getApplicationContext();

        mPortSipSdk = myApp.getPortSIPSDK();
        cbSendSdp = (CheckBox) v.findViewById(R.id.sendSdp);
        cbConfrence = (CheckBox) v.findViewById(R.id.conference);
        cbsendVideo = (CheckBox) v.findViewById(R.id.sendVideo);
        cbrecVideo = (CheckBox) v.findViewById(R.id.acceptVideo);
        lines = myApp.getLines();
        lines = myApp.getLines();
        spline = (Spinner) v.findViewById(R.id.sp_lines);
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
        mAdditionalButtonsRow = findViewById(R.id.dialpadAdditionalButtons);
        mAddContactButton = mAdditionalButtonsRow.findViewById(R.id.addContactButton);
        if (mAddContactButton != null)
        {
            mAddContactButton.setOnClickListener(this);
        }
        //mAddContactButton.setEnabled(mEnableDail);

        // Check whether we should show the onscreen "Dial" button.
        mDialButton = mAdditionalButtonsRow.findViewById(R.id.dialButton);
        mDialButton.setOnClickListener(this);
        mDialButton.setEnabled(mEnableDail);
        mDelete = mAdditionalButtonsRow.findViewById(R.id.deleteButton);
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener(this);
        mDialpad = findViewById(R.id.dialpad); // This is null in landscape
        // mode.
        // In landscape we put the keyboard in phone mode.
        if (null == mDialpad)
        {
            mDigits.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        }
        else
        {
            mDigits.setCursorVisible(false);
        }
        //Set up the "dialpad chooser" UI; see showDialpadChooser().
        //mDialpadChooser = (ListView) findViewById(R.id.dialpadChooser);
        //mDialpadChooser.setOnItemClickListener(this);
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
            case R.id.deleteButton:
            {
                keyPressed(KeyEvent.KEYCODE_DEL);
                return;
            }
            case R.id.dialButton:
            {
                placeCall();
                if(callflag==true){
                //
                    final Intent intent = new Intent(getApplicationContext(), HomeTabActivity.class);
                    mDialButton.setEnabled(true);
                    /*mDialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            endcall();
                            startActivity(intent);
                        }
                    });*/
                    endcall();
                    startActivity(intent);
                }
                else {
                    placeCall();
                }
                return;
            }
            case R.id.addContactButton: // Obs: Not search buttun but add contact button
            {
                if(mDigits.length()!=0) {
                    String sendNumber = mDigits.getText().toString();
                    //args.putString("phone", sendNumber);
                    mAddContactButton.setEnabled(true);
                    addContactForslag(sendNumber);
                }
                else{
                    Log.d(TAG, "Please put a number ");
                    Toast.makeText(getApplicationContext(),"Please type a nummber to add to the phonebook",Toast.LENGTH_LONG).show();
                    //mAddContactButton.setEnabled(false);
                }
                return;
            }
            case R.id.digits:
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (!isDigitsEmpty())
                {
                    mDigits.setCursorVisible(true);
                }
                return;
            }
        }
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
        if (isDigitsEmpty())
        {
            mDigits.setCursorVisible(false);
        }

        updateDialAndDeleteButtonEnabledState();

    }
    @Override
    public boolean onLongClick(View view)
    {
        final Editable digits = mDigits.getText();
        int id = view.getId();
        switch (id) {
            case R.id.deleteButton: {
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

    private void keyPressed(int keyCode)
    {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);
        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
     * @param
     */
    public void addContactForslag(String sendNumber){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_contact);
        dialog.setTitle("Add Contact:");
        dialog.show();
        Button btnSave = (Button)dialog.findViewById(R.id.savebtn);
        Button btncancle=(Button)dialog.findViewById(R.id.canclebtn);
        final EditText et1 = (EditText) dialog.findViewById(R.id.phone_name);
        final EditText et2 = (EditText) dialog.findViewById(R.id.phone_book_num);
        et2.setText(sendNumber);
        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    s1 = et1.getText().toString();
                    s2 = et2.getText().toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT);
                Log.d(TAG, "__________" + s1 + s2);
                mDigits.setText(s1);
                mLastNumber=s2;
                PhonebookAddName phonebookAddName= new PhonebookAddName();
                phonebookAddName.Insert2Contacts(getApplicationContext(),s1,s2);
                dialog.dismiss();
            }
        });
        btncancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void placeCall()
    {
        mLastNumber = mDigits.getText().toString();
        Log.d(TAG, "EEEEEEE placing call to " + mLastNumber);
        // don't place the call if it is not a valid number
        if (mLastNumber == null || !TextUtils.isGraphic(mLastNumber))
        {
            // There is no number entered.
            // Utils.playTone(this, ToneGenerator.TONE_PROP_NACK);
            showTips("Empty number");
            return;
        }
        //insertPlaceholderCallDialed(getContentResolver(), mLastNumber);
        // Check whether there is data connection or not then call the dialing from telephone manager
        if(myApp.isOnline()) {
            showTips("Start a call!");
            //loadNumPadFragment(mLastNumber);
            Intent call_intet = new Intent(this.getApplicationContext(), MainActivity.class);
            session.setDestNumber(mLastNumber);
            session.setCallingFlag(true);
            call_intet.putExtra("dialingStateNr", mLastNumber);
            call_intet.putExtra("flag",true);
            startActivity(call_intet);
            //call();
        }
        else{
            //call();// using android phone manager
            Intent intet=new Intent(this.getApplicationContext(),LoginActivity.class);
            startActivity(intet);
            showTips("Not Connected to remote Server");
        }
        //mDigits.getText().delete(0, mDigits.getText().length());
    }
    public static void insertPlaceholderCallDialed(ContentResolver contentResolver, String number){
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, 0);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME,number);
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        Log.d(TAG, "Inserting call log placeholder for " + number);
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
    }

    // call from the string main class
    private void call() {
        callflag=true;
        String str = "Calling" + mLastNumber;
        mDigits.setEnabled(false);
        /*final boolean check = mDialButton.isEnabled();
        if (check) {
            mDialButton.setEnabled(true);
        }*/
        /*LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v=inflater.inflate(R.layout.secure_connected_screen, layout);*/
        //mDigits.setTextSize(12);
        mDigits.setText(mLastNumber);
        btn_endcall = (ImageButton) mAdditionalButtonsRow.findViewById(R.id.dialButton);
        btn_endcall.setImageResource(R.drawable.ic_lockscreen_decline_normal);
        Log.d(TAG, "Start a call!");
        callme(mLastNumber);
        /*String numberToDial = "tel:"+ mLastNumber;
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial)));*/
        TextView tempview = (TextView) findViewById(R.id.textViewconnected);
        tempview.setVisibility(View.VISIBLE);
    }
    public void callme(String callTo){
        //phone_number=input_number;
        // the phone numner you are trying to call is:
        showTips("The phone number is: "+callTo);
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
        showTips(lines[_CurrentlyLine].getLineName() + ": Calling");
        myApp.updateSessionVideo();
    }

    void showTips(String text) {
       //mtips.setText(text);
       //spinerAdapter.notifyDataSetChanged();
       //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
            showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
        }
        myApp.updateSessionVideo();
    }
    public void addContact(){
        //Intent inter= new Intent(this,AddtoPhonebook.class);
        //startActivity(inter);
    }
    /**
     * Update the enabledness of the "Dial" and "Backspace" buttons if
     * applicable.
     */
    private void updateDialAndDeleteButtonEnabledState()
    {
        final boolean digitsNotEmpty = !isDigitsEmpty();
        mDialButton.setEnabled(digitsNotEmpty);
        mDelete.setEnabled(digitsNotEmpty);
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
                // need to
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
    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(TelephonyManager.CALL_STATE_RINGING == state) {
                Log.i(TAG, "RINGING, number: " + incomingNumber);
            }
            if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
                //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
                Log.i(TAG, "OFFHOOK");
            }
            if(TelephonyManager.CALL_STATE_IDLE == state) {
                //when this state occurs, and your flag is set, restart your app
                Log.i(TAG, "IDLE");
            }
        }
    }
}


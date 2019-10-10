package com.portsip.util;

import com.portsip.PortSipErrorcode;

public class Session {
	public long mSessionId = PortSipErrorcode.INVALID_SESSION_ID;
	public boolean mHoldState = false;
	public boolean mSessionState = false;
	public boolean mConferenceState = false;
	public boolean mRecvCallState = false;
	public boolean mHasEarlyMedia = false;
    public boolean mVideoState = false;
	public boolean mIsReferCall = false;
	public long mOriginSessionId = PortSipErrorcode.INVALID_SESSION_ID;

	public void reset() {
		mSessionId = PortSipErrorcode.INVALID_SESSION_ID;
		mHoldState = false;
		mSessionState = false;
		mConferenceState = false;
		mRecvCallState = false;
        mHasEarlyMedia = false;
        mVideoState = false;
		mIsReferCall = false;
		mOriginSessionId = PortSipErrorcode.INVALID_SESSION_ID;
	}

	public boolean hasEarlyMeida() {
		return mHasEarlyMedia;
	}

	public void setEarlyMeida(boolean earlyMedia) {
		mHasEarlyMedia = earlyMedia;
	}

	public boolean isReferCall() {
		return mIsReferCall;
	}

	public long getOriginCallSessionId() {
		return mOriginSessionId;
	}

	public void setReferCall(boolean referCall, long l) {
		mIsReferCall = referCall;
		mOriginSessionId = l;
	}

	public void setSessionId(long sessionId) {
		mSessionId = sessionId;
	}

	public long getSessionId() {
		return mSessionId;
	}

	public void setHoldState(boolean state) {
		mHoldState = state;
	}

	public boolean getHoldState() {
		return mHoldState;
	}

	public void setSessionState(boolean state) {
		mSessionState = state;
	}

	public boolean getSessionState() {
		return mSessionState;
	}

	public void setRecvCallState(boolean state) {
		mRecvCallState = state;
	}

	public boolean getRecvCallState() {
		return mRecvCallState;
	}

    public void setVideoState(boolean state) {
        mVideoState = state;
    }

    public boolean getVideoState() {
        return mVideoState;
    }


	public boolean getReferState() {
		return mIsReferCall;
	}

	boolean getConferenceState() {
		return mConferenceState;
	}
}

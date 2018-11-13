package com.portsip.main;
/*
 * Property of IT Man AS, Bryne Norway
 * (c) 2015 IT Man AS
 *
 * Created by Nigussie on 03.03.2015.
 */
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.util.Log;

import com.portsip.Calling;
public class Utils {

    private static final String TAG = "Utils";

    /** The length of DTMF tones in milliseconds */
    private static final int TONE_LENGTH_MS = 100;

    /** The DTMF tone volume relative to other sounds in the stream */
    private static final int TONE_RELATIVE_VOLUME = 40;

    /**
     * Stream type used to play the DTMF tones off call, and mapped to the
     * volume control keys
     */
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_MUSIC;//STREAM_DTMF;//USE_DEFAULT_STREAM_TYPE;
    /**
     * determines if we want to play-back local DTMF tones.
     */
    private static boolean mDTMFToneEnabled = true;
    private static Object mToneGeneratorLock = new Object();
    private static ToneGenerator mToneGenerator = initToneGenerator();
    public static void playTone(Context context, int tone)
    {
        // if local tone playback is disabled, just return.
        if (!mDTMFToneEnabled)
        {
            return;
        }
        // Also do nothing if the phone is in silent mode.
        // We need to re-check the ringer mode for *every* playTone()
        // call, rather than keeping a local flag that's updated in
        // onResume(), since it's possible to toggle silent mode without
        // leaving the current activity (via the ENDCALL-longpress menu.)
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
                || (ringerMode == AudioManager.RINGER_MODE_VIBRATE))
        {
            return;
        }
        synchronized (mToneGeneratorLock)
        {
            if (mToneGenerator == null)
            {
                Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }
            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, TONE_LENGTH_MS);
        }
    }
    private static ToneGenerator initToneGenerator()
    {
        ToneGenerator toneGenerator = null;
        synchronized (mToneGeneratorLock)
        {
            try
            {
                // we want the user to be able to control the volume of the
                // dial tones outside of a call, so we use the stream type that
                // is also mapped to the volume control keys for this activity
                toneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE,
                        TONE_RELATIVE_VOLUME);
            }
            catch (RuntimeException e)
            {
                Log.w(TAG,
                        "Exception caught while creating local tone generator: "
                                + e);
                toneGenerator = null;
            }
        }
        return toneGenerator;
    }

}

package com.portsip.bulk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.portsip.R;

/**
 * Created by Nigussie on 02.11.2015.
 */
public class Timer extends Activity {

    TextView myTextView;
    boolean someCondition = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView) findViewById(R.id.refreshing_field);
        //starting our task which update textview every 1000 ms
        new RefreshTask().execute();
    }

    //class which updates our textview every second
    class RefreshTask extends AsyncTask {
        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            String text = String.valueOf(System.currentTimeMillis());
            myTextView.setText(text);

        }

        @Override
        protected Object doInBackground(Object... params) {
            while (someCondition) {
                try {
                    //sleep for 1s in background...
                    Thread.sleep(1000);
                    //and update textview in ui thread
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                ;
                return null;
            }
            return params;
        }
    }
}
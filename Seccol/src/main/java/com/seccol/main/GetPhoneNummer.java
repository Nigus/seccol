package com.portsip.main;

/**
 * Created by Nigussie on 09.10.2015.
 */
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.portsip.MyApplication;
import com.portsip.R;
import com.portsip.bulk.Numbers;

import helper.SQLiteHandler;
import helper.SessionManager;

public class GetPhoneNummer extends Activity {
final static String TAG="GetPhoneNummer";
    String data = "";
    TableLayout tl;
    TableRow tr;
    TextView label;
    MyApplication myapp;
    Context context;
    public SessionManager session;
    public SQLiteHandler db;
    ArrayList<String> myphones=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_view_addcontact);
        context=getApplicationContext();
        session = new SessionManager(context);
        db = new SQLiteHandler(context);
        tl = (TableLayout) findViewById(R.id.maintable);
        /*final GetDataFromDB getdb = new GetDataFromDB();
        new Thread(new Runnable() {
            public void run() {
                data = getdb.getDataFromDB();
                System.out.println("Data is: "+data);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<Numbers> Numbers = parseJSON(data);
                        myapp = new MyApplication();
                        //myapp.setPhone_list(Numbers);
                        Log.d(TAG, "!!!!!!!!!!!!!!!: " + Numbers);
                        //addData(Numbers);
                    }
                });
            }
        }).start();*/
    }

    public ArrayList<Numbers> parseJSON(String result) {
        ArrayList<Numbers> numbers = new ArrayList<Numbers>();
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Numbers user = new Numbers();
                user.setId(json_data.getInt("id"));
                user.setName(json_data.getString("name"));
                user.setNumber(json_data.getString("phone"));
                numbers.add(user);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        Log.d(TAG,"Numbers are: "+numbers);
        return numbers;
    }

    void addHeader(){
        /** Create a TableRow dynamically **/
        tr = new TableRow(this);

        /** Creating a TextView to add to the row **/
        label = new TextView(this);
        label.setText("User");
        label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        label.setPadding(5, 5, 5, 5);
        label.setBackgroundColor(Color.RED);
        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        //Ll.setPadding(10, 5, 5, 5);
        Ll.addView(label,params);
        tr.addView((View)Ll); // Adding textView to tablerow.

        /** Creating Qty Button **/
        TextView place = new TextView(this);
        place.setText("Place");
        place.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        place.setPadding(5, 5, 5, 5);
        place.setBackgroundColor(Color.RED);
        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 5, 5);
        //Ll.setPadding(10, 5, 5, 5);
        Ll.addView(place,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    }

    @SuppressWarnings({ "rawtypes" })
    public void addData(ArrayList<Numbers> Numbers) {

        addHeader();

        for (Iterator i = Numbers.iterator(); i.hasNext();) {

            Numbers p = (Numbers) i.next();

            /** Create a TableRow dynamically **/
            tr = new TableRow(this);

            /** Creating a TextView to add to the row **/
            label = new TextView(this);
            label.setText(p.getName());
            label.setId(p.getId());
            label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            label.setPadding(5, 5, 5, 5);
            label.setBackgroundColor(Color.GRAY);
            LinearLayout Ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 2, 2, 2);
            //Ll.setPadding(10, 5, 5, 5);
            Ll.addView(label,params);
            tr.addView((View)Ll); // Adding textView to tablerow.

            /** Creating Qty Button **/
            TextView place = new TextView(this);
            place.setText(p.getNumber());
            place.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            place.setPadding(5, 5, 5, 5);
            place.setBackgroundColor(Color.GRAY);
            Ll = new LinearLayout(this);
            params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 2, 2, 2);
            //Ll.setPadding(10, 5, 5, 5);
            Ll.addView(place,params);
            tr.addView((View)Ll); // Adding textview to tablerow.

            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }
    }
}


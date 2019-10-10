package com.portsip.myprovider;
/**
 * Created by Nigussie on 09.11.2015.
 */
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.portsip.R;
public class GetProviderValues extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    TextView resultView=null;
    CursorLoader cursorLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zactivity_provider);
        resultView= (TextView) findViewById(R.id.res);
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    public void onClickDisplayNames(View view) {
        getSupportLoaderManager().initLoader(1, null, this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        cursorLoader= new CursorLoader(this, Uri.parse("content://com.portsip.myprovider.MyProvider/cte"), null, null, null, null);
        return cursorLoader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        cursor.moveToFirst();
        StringBuilder res=new StringBuilder();
        while (!cursor.isAfterLast()) {
            res.append("\n"+cursor.getString(cursor.getColumnIndex("id"))+ "-"+ cursor.getString(cursor.getColumnIndex("name"))
                    + "-"+ cursor.getString(cursor.getColumnIndex("message"))
                    + "-"+ cursor.getString(cursor.getColumnIndex("side"))
            +"-"+ cursor.getString(cursor.getColumnIndex("key")));
            cursor.moveToNext();
        }
        final SpannableStringBuilder sb = new SpannableStringBuilder("HELLOO");
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        final StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC);
        sb.setSpan(bss, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold
        sb.setSpan(iss, 4, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make last 2 characters Italic
        resultView.setText(sb);
        resultView.setText(res);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

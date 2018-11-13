package com.portsip.main;

/**
 * Created by Nigussie on 28.05.2015.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.portsip.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)

public class privacypolicy extends DialogFragment {

    public static privacypolicy newInstance() {
        return new privacypolicy();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_gallery)
                .setTitle(R.string.privacyinfo)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .setView(getCustomView(getActivity().getLayoutInflater(), null, savedInstanceState))
                .create();

    }
    public View getCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.legal_information, container, false);
        WebView webView = (WebView) v.findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/privacypolicy.html");
        return v;
    }

}

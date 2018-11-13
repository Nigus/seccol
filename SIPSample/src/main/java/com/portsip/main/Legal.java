package com.portsip.main;

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
public class Legal extends DialogFragment {

    public static Legal newInstance() {
        return new Legal();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_gallery)
                .setTitle(R.string.legal_information)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .setView(getCustomView(getActivity().getLayoutInflater(), null, savedInstanceState))
                .create();
        /*WebSettings webSettings = WebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }*/
    }
    public View getCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.legal_information, container, false);
        WebView webView = (WebView) v.findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/legal.html");
        return v;
    }

}

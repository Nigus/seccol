package com.portsip.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arsema on 09.10.2015.
 */
public interface OnDownloadTaskCompleted {
    public void onTaskCompleted(ArrayList<String> list,  boolean error, String message);
}

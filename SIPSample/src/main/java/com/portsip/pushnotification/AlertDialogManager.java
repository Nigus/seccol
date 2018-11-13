package com.portsip.pushnotification;
import android.content.DialogInterface;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
import com.portsip.R;
public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        if(status != null)
            // Setting alert dialog icon  red: FAIL and green: Succuss
            alertDialog.setIcon((status) ? R.drawable.appwidget_settings_ind_on: R.drawable.appwidget_settings_ind_red);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
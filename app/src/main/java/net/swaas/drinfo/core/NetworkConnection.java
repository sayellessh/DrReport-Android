package net.swaas.drinfo.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.swaas.drinfo.R;

/**
 * Created by vinoth on 10/29/15.
 */
public class NetworkConnection {

    public static boolean isNetworkAvailable(final Context ctx) {
        return isNetworkAvailable(ctx, true);
    }

    public static boolean isNetworkAvailable(final Context ctx, boolean showSettingsAlert) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            if (showSettingsAlert) getDialog(ctx).show();
            return false;
        }
    }


    public static AlertDialog getDialog(final Context context){
        return new AlertDialog.Builder(context)

                .setMessage(context.getString(R.string.msg_enable_network))
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).create();
    }

}

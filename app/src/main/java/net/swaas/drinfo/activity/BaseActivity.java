package net.swaas.drinfo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import net.swaas.drinfo.R;
import net.swaas.drinfo.logger.LogTracer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by vinoth on 10/20/15.
 */
public class BaseActivity extends AppCompatActivity {

    private static final LogTracer LOG_TRACER = LogTracer.instance(BaseActivity.class);
    private ProgressDialog loader = null;

    public void hideInput() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
            View view = getCurrentFocus();
            if (view != null) {
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
            }
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e.toString(), e);
        }
    }

    public static Calendar parseCalendar(String sDate, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(sDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public void showLoader(String title, @NonNull String message) {
        showLoader(title, message, false);
    }

    public void showLoader(String title, @NonNull String message, boolean cancellable) {
        //hideLoader();
        if (loader == null) {
            // Create a progressbar
            loader = new ProgressDialog(this);
        }
        // Set progressbar title
        loader.setTitle(title != null ? title : getString(R.string.app_name));
        // Set progressbar message
        loader.setMessage(message != null ? message : getString(R.string.loading));
        loader.setIndeterminate(false);
        loader.setCancelable(cancellable);
        // Show progressbar;
        loader.show();
    }

    public void hideLoader() {
        try {
            if (loader != null) loader.dismiss();
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e.toString(), e);
        }
    }

    public static AlertDialog showLayoutDialog(Context context, int layoutId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layoutId);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public void openActionView(String url) {
        openActionView(url, null);
    }

    public void openActionView(String url, Bundle extras) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        if (extras != null) {
            i.putExtras(extras);
        }
        startActivity(i);
    }

    public interface AlertDialogHandler {

    }
}




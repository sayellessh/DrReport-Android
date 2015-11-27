package net.swaas.drinfo.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.swaas.drinfo.logger.LogTracer;

/**
 * Created by vinoth on 10/23/15.
 */
public class UploadImageAsyncTask extends AsyncTask<String, Void, String> {

    private static final LogTracer LOG_TRACER = LogTracer.instance(UploadImageAsyncTask.class);
    public static final String SUCCESS_DATA = "success_data";
    public static final String ERROR_DATA = "error_data";
    public static final String FLAG_RUNNING = "running";
    public static final String FLAG_CANCELLED = "cancelled";
    public static final String FLAG_ERROR = "error";

    private Context mContext;
    private Handler mHandler;
    private Message mMessage;
    private Bundle mBundle;

    public UploadImageAsyncTask(Context context, Handler handler) {
        super();
        this.mContext = context;
        this.mHandler = handler;
        this.mMessage = new Message();
        this.mBundle = new Bundle();
        this.mMessage.setData(mBundle);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
}

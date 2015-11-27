package net.swaas.drinfo.logger;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SwaaS on 7/9/2015.
 */
public class LogTracer {

    private static final String LOG_TAG = LogTracer.class.getCanonicalName();

    public static final boolean DEBUG = false;
    //private Context mContext = null;
    private String TAG = null;

    public LogTracer(String tag) {
        //this.mContext = context;
        this.TAG = tag;
    }

    public static LogTracer instance(Class tagClass) {
        return new LogTracer(tagClass.getCanonicalName());
    }

    public void d(Object message) {
        try {
            android.util.Log.d(TAG, message.toString());
            appendLog(message.toString());
        } catch (Exception e) {
            android.util.Log.e(LOG_TAG, "Error logging loggger data on Debug", e);
        }
    }

    public void e(Object message, Throwable tr) {
        try {
            android.util.Log.e(TAG, message.toString(), tr);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            String trace = sw.toString(); // stack trace as a string
            appendLog(message.toString() + "\n" + trace);
        } catch (Exception e) {
            android.util.Log.e(LOG_TAG, "Error logging loggger data on Exception", e);
        }
    }

    public void e(Throwable tr) {
        try {
            android.util.Log.e(TAG, tr.toString(), tr);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            String trace = sw.toString(); // stack trace as a string
            appendLog(trace);
        } catch (Exception e) {
            android.util.Log.e(LOG_TAG, "Error logging loggger data on Exception", e);
        }
    }

    public void e(String error) {
        try {
            android.util.Log.e(TAG, error);
            appendLog(error);
        } catch (Exception e) {
            android.util.Log.e(LOG_TAG, "Error logging loggger data on Exception", e);
        }
    }

    public void appendLog(String text) {
        if (DEBUG) {
            AsyncTask at = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    String sExtFileDir = Environment.getExternalStorageDirectory() + "/Android/data/" + "net.swaas.hidoctorapp/logs/";
                    try {
                        String message = params[0].toString();
                        File extFileDir = new File(sExtFileDir);
                        if (!extFileDir.exists()) {
                            extFileDir.mkdirs();
                        }
                        Integer[] dateArray = formatDateArray(new Date());
                        File logFile = new File(extFileDir, dateArray[0]
                                + "" + dateArray[1] + "" + dateArray[2] + ".log");
                        if (!logFile.exists()) {
                            try {
                                logFile.createNewFile();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                android.util.Log.e(LOG_TAG, e.toString(), e);
                            }
                        }
                        try {
                            //BufferedWriter for performance, true to set append to file flag
                            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                            buf.append(TAG + ": " + formatDate(new Date()) + " : " + message);
                            buf.newLine();
                            buf.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            android.util.Log.e(LOG_TAG, e.toString(), e);
                        }
                    } catch (Exception e) {
                        android.util.Log.e(LOG_TAG, e.toString(), e);
                    } finally {

                    }
                    return null;
                }
            };
            at.execute(text);
        }
    }

    public static Integer[] formatDateArray(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(d);
        String[] dateAry = sDate.split("-");
        Integer[] out = new Integer[3];
        if (dateAry.length == 3) {
            out[0] = Integer.parseInt(dateAry[0]);
            out[1] = Integer.parseInt(dateAry[1]);
            out[2] = Integer.parseInt(dateAry[2]);
        }
        return out;
    }

    public static String formatDate(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = sdf.format(date);
        return sDate;
    }

    public static String formatDate(Date date, String dateFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String sDate = sdf.format(date);
        return sDate;
    }
}

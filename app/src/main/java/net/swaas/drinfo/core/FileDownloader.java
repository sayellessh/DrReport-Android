package net.swaas.drinfo.core;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import net.swaas.drinfo.core.exception.NetworkException;
import net.swaas.drinfo.logger.LogTracer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by uc on 6/12/2015.
 */
public class FileDownloader extends AsyncTask<String, Object, String> {

    //private static final String TAG = FileDownloader.class.getCanonicalName();
    private static final LogTracer LOG_TRACER = LogTracer.instance(FileDownloader.class);
    public interface Task {
        public void onProgress(double downloaded, int fileLength, double progress);
        public void onComplete(String filePath);
        public void onError(Exception e);
    }

    private Context mContext;
    private Task task;
    private boolean interrupt = false;
    private String filePath;

    public FileDownloader(Context context, Task task) {
        this.mContext = context;
        this.task = task;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (task != null)
            task.onProgress(0D, 0, 0D);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        try {
            if (values != null && values.length > 0) {
                if (values[0] instanceof Exception) {
                    Exception e = (Exception) values[0];
                    if (e instanceof IOException) {
                        e = new NetworkException(e, CoreREST.MESSAGE_NETWORK_ERROR);
                    } else if (e instanceof ConnectException) {
                        e = new NetworkException(e, CoreREST.MESSAGE_NETWORK_ERROR);
                    }
                    if (task != null) task.onError(e);
                } else {
                    double downloaded = (double) values[0];
                    int fileLenth = (int) values[1];
                    double progress = (double) values[2];
                    if (task != null)
                        task.onProgress(downloaded, fileLenth, progress);
                }
            }
        } catch (Exception e) {
            LOG_TRACER.e(e);
            if (task != null) task.onError(e);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        URLConnection connection = null;
        String filename = null;
        try {
            if(TextUtils.isEmpty(this.filePath)) {
                filename = getFilePath(mContext, params[0]);
            } else {
                filename = this.filePath;
            }
            if (new File(filename).exists()) {
                return filename;
            }
            url = new URL(params[0]);
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(filename);
            connection = url.openConnection();
            connection.connect();
            int fileLenth = connection.getContentLength();
            final byte data[] = new byte[1024];
            int count;
            double downloaded = 0;
            double progress = 0;
            while ((count = in.read(data, 0, 1024)) != -1) {
                if (isCancelled())
                    throw new InterruptedException("File download interrupted by user.");
                fout.write(data, 0, count);
                downloaded += count;
                progress = (downloaded / fileLenth) * 100f;
                publishProgress(downloaded, fileLenth, progress);
            }
            return filename;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e.toString(), e);
            try {
                if(filename != null) new File(filename).delete();
            } catch (Exception e1) {
                //Log.e(TAG, e.toString(), e);
                LOG_TRACER.e(e.toString(), e);
            }
            publishProgress(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    //Log.e(TAG, e.toString(), e);
                    LOG_TRACER.e(e.toString(), e);
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    //Log.e(TAG, e.toString(), e);
                    LOG_TRACER.e(e.toString(), e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (task != null) {
            if (!TextUtils.isEmpty(s)) {
                task.onComplete(s);
            }
        }
    }

    public static String downloadFile(Context context, String sFile, Task task) {
        URL url = null;
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        URLConnection connection = null;
        String filename = null;
        try {
            filename = getFilePath(context, sFile);
            if (new File(filename).exists()) {
                return filename;
            }
            url = new URL(sFile);
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(filename);
            connection = url.openConnection();
            connection.connect();
            int fileLenth = connection.getContentLength();
            final byte data[] = new byte[1024];
            int count;
            double downloaded = 0;
            double progress = 0;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
                downloaded += count;
                progress = (downloaded / fileLenth) * 100f;
                if (task != null)
                    task.onProgress(downloaded, fileLenth, progress);
            }
            if (task != null) task.onComplete(filename);
            return filename;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e.toString(), e);
            try {
                if(filename != null) new File(filename).delete();
            } catch (Exception e1) {
                //Log.e(TAG, e.toString(), e);
                LOG_TRACER.e(e.toString(), e);
            }
            if (task != null) task.onError(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    //Log.e(TAG, e.toString(), e);
                    LOG_TRACER.e(e.toString(), e);
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    //Log.e(TAG, e.toString(), e);
                    LOG_TRACER.e(e.toString(), e);
                }
            }
        }
        return null;
    }
    public static String getFilePath(Context context, String fileUrl) {
        return context.getExternalCacheDir() + "/" + Integer.toString(fileUrl.hashCode());
    }
}

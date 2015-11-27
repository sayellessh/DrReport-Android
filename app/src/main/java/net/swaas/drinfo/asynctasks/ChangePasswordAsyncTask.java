package net.swaas.drinfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import net.swaas.drinfo.beans.CommonResponse;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.FileUpload;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.core.MultipartUtility;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.rest.DrBondPromotionREST;
import net.swaas.drinfo.utils.SettingsUtils;

import java.io.File;

/**
 * Created by vinoth on 10/26/15.
 */
public class ChangePasswordAsyncTask extends AsyncTask<String, Void, CommonResponse> {

    private static final LogTracer LOG_TRACER = LogTracer.instance(ChangePasswordAsyncTask.class);
    public static final String SUCCESS_DATA = "success_data";
    public static final String ERROR_DATA = "error_data";
    private Context mContext;
    private Handler mHandler;
    private Message mMessage;
    private Bundle mBundle;

    public ChangePasswordAsyncTask(Context context, Handler handler) {
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
    protected void onPostExecute(CommonResponse commonResponse) {
        super.onPostExecute(commonResponse);
    }

    @Override
    protected CommonResponse doInBackground(String... params) {
        User user = SettingsUtils.getUserInfo(mContext);
        try {
            CommonResponse commonResponse = null;
            commonResponse = DrBondPromotionREST.changePassword(user.getCompany_Code(), user.getUser_Id(), params[0]);
            if (commonResponse.isTransaction_Status()) {
                user.setPassword(params[0]);
                SettingsUtils.setUserInfo(mContext, user);
                mBundle.putSerializable(SUCCESS_DATA, commonResponse);
                mHandler.sendMessage(mMessage);
            } else {
                mBundle.putSerializable(ERROR_DATA, commonResponse.getMessage_To_Display());
                mHandler.sendMessage(mMessage);
            }
            return commonResponse;
        } catch (Exception e) {
            LOG_TRACER.e(e);
            mBundle.putSerializable(ERROR_DATA, e.getMessage());
            mHandler.sendMessage(mMessage);
        }
        return null;
    }
}

package net.swaas.drinfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.beans.UserAuthentication;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.rest.DrBondPromotionREST;
import net.swaas.drinfo.utils.SettingsUtils;

/**
 * Created by vinoth on 10/20/15.
 */
public class UserAuthenticationAsyncTask extends AsyncTask<User, Void, User> {

    private static final LogTracer LOG_TRACER = LogTracer.instance(UserAuthenticationAsyncTask.class);
    public static final String KEY_RESPOSE = "response";
    public static final String KEY_ERROR = "error";
    private Context mContext;
    private Handler mHandler;
    private Message mMessage;
    private Bundle mBundle;

    public UserAuthenticationAsyncTask(Context context, Handler handler) {
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
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
    }

    @Override
    protected User doInBackground(User... users) {
        try {
            User user = users[0];
            UserAuthentication userAuthentication = DrBondPromotionREST.checkUserAuthentication(user.getUsername(), user.getPassword());
            if (userAuthentication.isTransaction_Status()) {
                user.setCompany_Code(userAuthentication.getCompany_Code());
                user.setUser_Id(userAuthentication.getUser_Id());
                SettingsUtils.setUserInfo(mContext, user);
                SettingsUtils.setIsTrainer(mContext, userAuthentication.isTrainer());
                mBundle.putSerializable(KEY_RESPOSE, user);
                mHandler.sendMessage(mMessage);
            } else {
                mBundle.putString(KEY_ERROR, userAuthentication.getMessage_To_Display());
                mHandler.sendMessage(mMessage);
            }
            return user;
        } catch (Exception e) {
            LOG_TRACER.e(e);
            mBundle.putString(KEY_ERROR, e.getMessage());
            mHandler.sendMessage(mMessage);
        }
        return null;
    }
}

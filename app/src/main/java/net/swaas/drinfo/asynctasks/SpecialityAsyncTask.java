package net.swaas.drinfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.Speciality;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.rest.DrBondPromotionREST;
import net.swaas.drinfo.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinoth on 10/26/15.
 */
public class SpecialityAsyncTask extends AsyncTask<Void, Void, List<Speciality>> {

    private static final LogTracer LOG_TRACER = LogTracer.instance(SpecialityAsyncTask.class);
    public static final String SUCCESS_DATA = "success_data";
    public static final String ERROR_DATA = "error_data";
    private Context mContext;
    private Handler mHandler;
    private Message mMessage;
    private Bundle mBundle;

    public SpecialityAsyncTask(Context context, Handler handler) {
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
    protected void onPostExecute(List<Speciality> specialities) {
        super.onPostExecute(specialities);
        if (specialities != null && specialities.size() > 0) {
            mBundle.putSerializable(SUCCESS_DATA, specialities.toArray());
            mHandler.sendMessage(mMessage);
        }
    }

    @Override
    protected List<Speciality> doInBackground(Void... voids) {
        User user = SettingsUtils.getUserInfo(mContext);
        List<Speciality> specialities = new ArrayList<>();
        try {
            specialities = DrBondPromotionREST.getSpecialities(user.getCompany_Code());
        } catch (Exception e) {
            LOG_TRACER.e(e);
            mBundle.putSerializable(ERROR_DATA, e.getMessage());
            mHandler.sendMessage(mMessage);
        }
        return specialities;
    }
}

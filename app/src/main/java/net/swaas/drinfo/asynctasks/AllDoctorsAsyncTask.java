package net.swaas.drinfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.dao.DoctorDAO;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.rest.DrBondPromotionREST;
import net.swaas.drinfo.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinoth on 10/22/15.
 */
public class AllDoctorsAsyncTask extends AsyncTask<Void, Void, List<Doctor>> {

    private static final LogTracer LOG_TRACER = LogTracer.instance(AllDoctorsAsyncTask.class);
    public static final String SUCCESS_DATA = "success_data";
    public static final String ERROR_DATA = "error_data";
    private Context mContext;
    private Handler mHandler;
    private Message mMessage;
    private Bundle mBundle;
    private DoctorDAO mDoctorDAO;

    public AllDoctorsAsyncTask(Context context, Handler handler) {
        super();
        this.mContext = context;
        this.mHandler = handler;
        this.mDoctorDAO = new DoctorDAO(mContext);
        this.mMessage = new Message();
        this.mBundle = new Bundle();
        this.mMessage.setData(mBundle);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Doctor> doctors) {
        super.onPostExecute(doctors);
        if (doctors != null) {
            SettingsUtils.setDoctorUpdateRequired(mContext, false);
            mBundle.putSerializable(SUCCESS_DATA, doctors.toArray(new Doctor[doctors.size()]));
            mHandler.sendMessage(mMessage);
        }
    }

    @Override
    protected List<Doctor> doInBackground(Void... voids) {
        try {
            List<Doctor> doctors = new ArrayList<>();
            //boolean state = SettingsUtils.getDoctorUpdateRequired(mContext);
            doctors = mDoctorDAO.getAll();
            if (doctors != null && doctors.size() > 0) {
                //doctors = mDoctorDAO.getAll();
            } else {
                User user = SettingsUtils.getUserInfo(mContext);
                doctors = DrBondPromotionREST.getAllMyDoctors(user.getCompany_Code(), user.getUser_Id());
                mDoctorDAO.deleteAll();
                mDoctorDAO.insert(doctors);
            }
            return doctors;
        } catch (Exception e) {
            LOG_TRACER.e(e);
            mBundle.putSerializable(ERROR_DATA, e.getMessage());
            mHandler.sendMessage(mMessage);
        }
        return null;
    }
}

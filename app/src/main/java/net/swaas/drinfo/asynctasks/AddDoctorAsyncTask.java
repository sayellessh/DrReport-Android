package net.swaas.drinfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import net.swaas.drinfo.R;
import net.swaas.drinfo.beans.CommonResponse;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.FileUpload;
import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.core.MultipartUtility;
import net.swaas.drinfo.dao.DoctorDAO;
import net.swaas.drinfo.dao.RecentDAO;
import net.swaas.drinfo.logger.LogTracer;
import net.swaas.drinfo.rest.DrBondPromotionREST;
import net.swaas.drinfo.utils.FileUtils;
import net.swaas.drinfo.utils.SettingsUtils;

import java.io.File;
import java.util.List;

/**
 * Created by vinoth on 10/26/15.
 */
public class AddDoctorAsyncTask extends AsyncTask<Doctor, Void, Doctor[]> {

    private static final LogTracer LOG_TRACER = LogTracer.instance(AddDoctorAsyncTask.class);
    public static final String SUCCESS_DATA = "success_data";
    public static final String ERROR_DATA = "error_data";
    private Context mContext;
    private Handler mHandler;
    private Message mMessage;
    private Bundle mBundle;

    public AddDoctorAsyncTask(Context context, Handler handler) {
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
    protected void onPostExecute(Doctor[] doctors) {
        super.onPostExecute(doctors);
        if (doctors != null && doctors.length > 0) {
            for (Doctor doctor : doctors) {
                if (doctor != null) {
                    SettingsUtils.setRecentDoctorUpdateRequired(mContext, true);
                    SettingsUtils.setDoctorUpdateRequired(mContext, true);
                }
            }
        }
    }

    @Override
    protected Doctor[] doInBackground(Doctor... doctors) {
        User user = SettingsUtils.getUserInfo(mContext);
        Doctor[] result = new Doctor[doctors.length];
        DoctorDAO doctorDAO = new DoctorDAO(mContext);
        RecentDAO recentDAO = new RecentDAO(mContext);
        if (doctors != null && doctors.length > 0) {
            int cnt = 0;
            for (Doctor doctor : doctors) {
                try {
                    if (!TextUtils.isEmpty(doctor.getHospitalLocatPath())) {
                        File f = new File(doctor.getHospitalLocatPath());
                        if (f.exists()) {
                            FileUpload response = DrBondPromotionREST.chunkFileUpload(f, new MultipartUtility.OnProgress() {
                                @Override
                                public void onProgress(String fileName, double uploaded, long fileLength, double progress) {
                                    LOG_TRACER.d("Uploading: " + progress);
                                }
                            });
                            if (response != null) {
                                doctor.setHospital_Photo_Url(response.getUrl());
                            }
                            LOG_TRACER.d(response);
                        }
                    }
                    CommonResponse commonResponse = null;
                    if (doctor.getDoctor_Id() <= 0) {
                        commonResponse = DrBondPromotionREST.insertDoctorSubscription(user.getCompany_Code(), user.getUser_Id(), doctor);
                    } else {
                        commonResponse = DrBondPromotionREST.updateDoctorSubscription(user.getCompany_Code(), user.getUser_Id(), doctor);
                    }
                    if (commonResponse != null && commonResponse.isTransaction_Status()) {
                        if (!TextUtils.isEmpty(commonResponse.getAdditional_Context())) {
                            String[] addContx = commonResponse.getAdditional_Context().split("~");
                            if (addContx.length >= 3) {
                                doctor.setDoctor_Id(Integer.parseInt(addContx[0]));
                                doctor.setCreated_DateTime(addContx[1]);
                                doctor.setUpdated_DateTime(addContx[2]);
                                Doctor oldDoctor = doctorDAO.get(doctor.getDoctor_Id());
                                long out = 0;
                                if (oldDoctor != null) {
                                    out = doctorDAO.update(doctor);
                                } else {
                                    out = doctorDAO.insert(doctor);
                                }
                                Doctor oldRecent = recentDAO.get(doctor.getDoctor_Id());
                                if (oldRecent != null) {
                                    recentDAO.update(doctor);
                                } else {
                                    recentDAO.insert(doctor);
                                }
                                if (out > 0) {
                                    result[cnt++] = doctor;
                                } else {
                                    throw new Exception(mContext.getString(R.string.error_unable_to_save));
                                }
                            }
                        }
                    }
                    mBundle.putSerializable(SUCCESS_DATA, result);
                    mHandler.sendMessage(mMessage);
                } catch (Exception e) {
                    LOG_TRACER.e(e);
                    mBundle.putSerializable(ERROR_DATA, e.getMessage());
                    mHandler.sendMessage(mMessage);
                }
            }
        }
        return result;
    }
}

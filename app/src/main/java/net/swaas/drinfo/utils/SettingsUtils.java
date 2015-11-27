package net.swaas.drinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import net.swaas.drinfo.beans.User;
import net.swaas.drinfo.beans.UserAuthentication;
import net.swaas.drinfo.logger.LogTracer;

import java.util.Date;

/**
 * Created by SwaaS on 8/20/2015.
 */
public class SettingsUtils {

    private static final LogTracer LOG_TRACER = LogTracer.instance(SettingsUtils.class);

    public static final String PREF_FILE_NAME = "SettingsFile";
    public static final String USER_INFO = "user_info";
    public static final String IS_TRAINER = "is_trainer";
    public static final String DOCTOR_UPDATE_REQUIRED = "doctor_update_required";
    public static final String RECENT_DOCTOR_UPDATE_REQUIRED = "recent_doctor_update_required";

    public static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        return prefs;
    }

    public static SharedPreferences.Editor getSharedPreferenceEditor(Context context) {
        return getSharedPreference(context).edit();
    }

    public static User getUserInfo(Context context) {
        return new Gson().fromJson(getSharedPreference(context).getString(USER_INFO, null), User.class);
    }

    public static void setUserInfo(Context context, User user) {
        getSharedPreferenceEditor(context).putString(USER_INFO, new Gson().toJson(user)).commit();
    }

    public static boolean getIsTrainer(Context context) {
        return getSharedPreference(context).getBoolean(IS_TRAINER, false);
    }

    public static void setIsTrainer(Context context, boolean state) {
        getSharedPreferenceEditor(context).putBoolean(IS_TRAINER, state).commit();
    }

    public static boolean getDoctorUpdateRequired(Context context) {
        return getSharedPreference(context).getBoolean(DOCTOR_UPDATE_REQUIRED, true);
    }

    public static void setDoctorUpdateRequired(Context context, boolean state) {
        getSharedPreferenceEditor(context).putBoolean(DOCTOR_UPDATE_REQUIRED, state).commit();
    }

    public static boolean getRecentDoctorUpdateRequired(Context context) {
        return getSharedPreference(context).getBoolean(RECENT_DOCTOR_UPDATE_REQUIRED, true);
    }

    public static void setRecentDoctorUpdateRequired(Context context, boolean state) {
        getSharedPreferenceEditor(context).putBoolean(RECENT_DOCTOR_UPDATE_REQUIRED, state).commit();
    }

    public static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(
                context.getPackageName(), 0);
        return info;
    }

}

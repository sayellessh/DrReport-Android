package net.swaas.drinfo.rest;

import com.google.gson.Gson;

import net.swaas.drinfo.beans.CommonResponse;
import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.beans.FileUpload;
import net.swaas.drinfo.beans.Speciality;
import net.swaas.drinfo.beans.UserAuthentication;
import net.swaas.drinfo.core.CoreREST;
import net.swaas.drinfo.core.MultipartUtility;
import net.swaas.drinfo.core.ReflectionHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vinoth on 10/20/15.
 */
public class DrBondPromotionREST extends CoreREST {

    public static final String DRBOND_PROMOTION_API = "DrBondPromotionApi";

    public static UserAuthentication checkUserAuthentication(String username, String password) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "CheckUserAuthentication", username};
        String response = postMethodString(SERVER, context, password);
        return ReflectionHelper.parseJson(response, UserAuthentication.class);
    }

    public static List<Doctor> getAllMyDoctors(String companyCode, int userId) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "GetAllMyDoctors", companyCode, userId};
        String response = getMethodString(SERVER, context, null);
        return ReflectionHelper.parseJsonArray(response, Doctor.class);
    }

    public static List<Speciality> getSpecialities(String companyCode) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "GetSpecialities", companyCode};
        String response = getMethodString(SERVER, context, null);
        return ReflectionHelper.parseJsonArray(response, Speciality.class);
    }

    public static List<Doctor> getMyRecentDoctors(String companyCode, int userId) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "GetMyRecentDoctors", companyCode, userId};
        String response = getMethodString(SERVER, context, null);
        return ReflectionHelper.parseJsonArray(response, Doctor.class);
    }

    public static CommonResponse insertDoctorSubscription(String companyCode, int userId, Doctor doctor) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "InsertDoctorSubscription", companyCode, userId};
        Gson gson = new Gson();
        String response = postMethodString(SERVER, context, gson.toJson(doctor));
        return ReflectionHelper.parseJson(response, CommonResponse.class);
    }

    public static CommonResponse updateDoctorSubscription(String companyCode, int userId, Doctor doctor) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "UpdateDoctorSubscription", companyCode, userId};
        Gson gson = new Gson();
        String response = postMethodString(SERVER, context, gson.toJson(doctor));
        return ReflectionHelper.parseJson(response, CommonResponse.class);
    }

    public static FileUpload chunkFileUpload(File file, MultipartUtility.OnProgress onProgress) throws Exception {
        String[] context = { DRBOND_PROMOTION_API, "ChunkFileUpload" };
        Map<String, File> fileMap = new HashMap<>(1);
        fileMap.put(file.getName(), file);
        String response = CoreREST.postMethodFile(SERVER, context, fileMap, null, onProgress);
        return ReflectionHelper.parseJson(response, FileUpload.class);
    }

    public static CommonResponse changePassword(String companyCode, int userId, String password) throws Exception {
        Object[] context = {DRBOND_PROMOTION_API, "ChangePassword", companyCode, userId};
        String response = postMethodString(SERVER, context, password);
        return ReflectionHelper.parseJson(response, CommonResponse.class);
    }
}

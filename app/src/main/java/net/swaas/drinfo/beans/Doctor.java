package net.swaas.drinfo.beans;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.swaas.drinfo.core.annotations.ExcludeColumn;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vinoth on 10/22/15.
 */
public class Doctor implements Serializable {

    private int Doctor_Id;
    private String Company_Code;
    private String First_Name;
    private String Last_Name;
    private String Speciality_Name;
    private String Speciality_Code;
    private String Hospital_Name;
    private String Hospital_Photo_Url;
    private String Latitude;
    private String Longitude;
    private String Location_Full_Address;
    private String Phone_Number;
    private String Email_Id;
    private String Landmark;
    private String Assistant_Name;
    private String Assistant_Phone_Number;
    private String Remarks;
    private String Employee_Code;
    private String Region_Code;
    private int Region_Id;
    private String Manager_User_Code;
    private int Manager_User_Id;
    private String Manager_Region_Code;
    private int Manager_Region_Id;
    private String Created_DateTime;
    private String User_Code;
    private int User_Id;
    private int Created_By;
    private int Updated_By;
    private String Updated_DateTime;
    private String Working_From_Time;
    private String Working_To_Time;
    private String Working_From_Time_2;
    private String Working_To_Time_2;
    private String Working_From_Time_3;
    private String Working_To_Time_3;
    private BigInteger Trainer_Code;

    // annotation excludes from database insertion
    @ExcludeColumn
    private String hospitalLocatPath;

    public int getDoctor_Id() {
        return Doctor_Id;
    }

    public void setDoctor_Id(int doctor_Id) {
        Doctor_Id = doctor_Id;
    }

    public String getCompany_Code() {
        return Company_Code;
    }

    public void setCompany_Code(String company_Code) {
        Company_Code = company_Code;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getSpeciality_Name() {
        return Speciality_Name;
    }

    public void setSpeciality_Name(String speciality_Name) {
        Speciality_Name = speciality_Name;
    }

    public String getSpeciality_Code() {
        return Speciality_Code;
    }

    public void setSpeciality_Code(String speciality_Code) {
        Speciality_Code = speciality_Code;
    }

    public String getHospital_Name() {
        return Hospital_Name;
    }

    public void setHospital_Name(String hospital_Name) {
        Hospital_Name = hospital_Name;
    }

    public String getHospital_Photo_Url() {
        return Hospital_Photo_Url;
    }

    public void setHospital_Photo_Url(String hospital_Photo_Url) {
        Hospital_Photo_Url = hospital_Photo_Url;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLocation_Full_Address() {
        return Location_Full_Address;
    }

    public void setLocation_Full_Address(String location_Full_Address) {
        Location_Full_Address = location_Full_Address;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }

    public String getEmail_Id() {
        return Email_Id;
    }

    public void setEmail_Id(String email_Id) {
        Email_Id = email_Id;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String landmark) {
        Landmark = landmark;
    }

    public String getAssistant_Name() {
        return Assistant_Name;
    }

    public void setAssistant_Name(String assistant_Name) {
        Assistant_Name = assistant_Name;
    }

    public String getAssistant_Phone_Number() {
        return Assistant_Phone_Number;
    }

    public void setAssistant_Phone_Number(String assistant_Phone_Number) {
        Assistant_Phone_Number = assistant_Phone_Number;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getEmployee_Code() {
        return Employee_Code;
    }

    public void setEmployee_Code(String employee_Code) {
        Employee_Code = employee_Code;
    }

    public String getRegion_Code() {
        return Region_Code;
    }

    public void setRegion_Code(String region_Code) {
        Region_Code = region_Code;
    }

    public int getRegion_Id() {
        return Region_Id;
    }

    public void setRegion_Id(int region_Id) {
        Region_Id = region_Id;
    }

    public String getManager_User_Code() {
        return Manager_User_Code;
    }

    public void setManager_User_Code(String manager_User_Code) {
        Manager_User_Code = manager_User_Code;
    }

    public int getManager_User_Id() {
        return Manager_User_Id;
    }

    public void setManager_User_Id(int manager_User_Id) {
        Manager_User_Id = manager_User_Id;
    }

    public String getManager_Region_Code() {
        return Manager_Region_Code;
    }

    public void setManager_Region_Code(String manager_Region_Code) {
        Manager_Region_Code = manager_Region_Code;
    }

    public int getManager_Region_Id() {
        return Manager_Region_Id;
    }

    public void setManager_Region_Id(int manager_Region_Id) {
        Manager_Region_Id = manager_Region_Id;
    }

    public String getCreated_DateTime() {
        return Created_DateTime;
    }

    public void setCreated_DateTime(String created_DateTime) {
        Created_DateTime = created_DateTime;
    }

    public String getUser_Code() {
        return User_Code;
    }

    public void setUser_Code(String user_Code) {
        User_Code = user_Code;
    }

    public int getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(int user_Id) {
        User_Id = user_Id;
    }

    public int getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(int created_By) {
        Created_By = created_By;
    }

    public int getUpdated_By() {
        return Updated_By;
    }

    public void setUpdated_By(int updated_By) {
        Updated_By = updated_By;
    }

    public String getUpdated_DateTime() {
        return Updated_DateTime;
    }

    public void setUpdated_DateTime(String updated_DateTime) {
        Updated_DateTime = updated_DateTime;
    }

    public String getWorking_From_Time() {
        return Working_From_Time;
    }

    public void setWorking_From_Time(String working_From_Time) {
        Working_From_Time = working_From_Time;
    }

    public String getWorking_To_Time() {
        return Working_To_Time;
    }

    public void setWorking_To_Time(String working_To_Time) {
        Working_To_Time = working_To_Time;
    }

    public String getWorking_From_Time_2() {
        return Working_From_Time_2;
    }

    public void setWorking_From_Time_2(String working_From_Time_2) {
        Working_From_Time_2 = working_From_Time_2;
    }

    public String getWorking_To_Time_2() {
        return Working_To_Time_2;
    }

    public void setWorking_To_Time_2(String working_To_Time_2) {
        Working_To_Time_2 = working_To_Time_2;
    }

    public String getWorking_From_Time_3() {
        return Working_From_Time_3;
    }

    public void setWorking_From_Time_3(String working_From_Time_3) {
        Working_From_Time_3 = working_From_Time_3;
    }

    public String getWorking_To_Time_3() {
        return Working_To_Time_3;
    }

    public void setWorking_To_Time_3(String working_To_Time_3) {
        Working_To_Time_3 = working_To_Time_3;
    }

    public BigInteger getTrainer_Code() {
        return Trainer_Code;
    }

    public void setTrainer_Code(BigInteger trainer_Code) {
        Trainer_Code = trainer_Code;
    }

    public Date getUpdatedDate() throws ParseException {
        return parseDate(getUpdated_DateTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public Date getCreatedDate() throws ParseException {
        return parseDate(getCreated_DateTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public String getFormattedUpdatedDate() throws ParseException {
        return formatDate(getUpdatedDate(), "dd MMMM yyyy");
    }

    public String getFormattedCreatedDate() throws ParseException {
        return formatDate(getCreatedDate(), "dd MMMM yyyy");
    }

    public long getUpdatedDateLong() throws ParseException {
        return Long.parseLong(formatDate(getUpdatedDate(), "yyyy-MM-dd").replace("-", ""));
    }

    public long getCreatedDateLong() throws ParseException {
        return Long.parseLong(formatDate(getCreatedDate(), "yyyy-MM-dd").replace("-", ""));
    }

    @Nullable
    public String getHospitalLocatPath() {
        return hospitalLocatPath;
    }

    public void setHospitalLocatPath(@Nullable String hospitalLocatPath) {
        this.hospitalLocatPath = hospitalLocatPath;
    }

    public Date parseDate(String sDate, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(sDate);
        return date;
    }

    public static String formatDate(Date date, String dateFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String sDate = sdf.format(date);
        return sDate;
    }

    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(getFirst_Name());
        if (!TextUtils.isEmpty(getLast_Name())) {
            sb.append(" ");
            sb.append(getLast_Name());
        }
        return sb.toString();
    }
}

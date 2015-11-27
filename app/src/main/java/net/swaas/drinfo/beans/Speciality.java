package net.swaas.drinfo.beans;

import java.io.Serializable;

/**
 * Created by vinoth on 10/26/15.
 */
public class Speciality implements Serializable {
    private String Speciality_Code;
    private String Speciality_Name;

    public String getSpeciality_Code() {
        return Speciality_Code;
    }

    public void setSpeciality_Code(String speciality_Code) {
        Speciality_Code = speciality_Code;
    }

    public String getSpeciality_Name() {
        return Speciality_Name;
    }

    public void setSpeciality_Name(String speciality_Name) {
        Speciality_Name = speciality_Name;
    }


}

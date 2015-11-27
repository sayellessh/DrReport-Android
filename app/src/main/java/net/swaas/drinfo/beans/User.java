package net.swaas.drinfo.beans;

import java.io.Serializable;

/**
 * Created by vinoth on 10/20/15.
 */
public class User implements Serializable {

    private String username;
    private String password;
    private String Company_Code;
    private int User_Id;
    private String Display_Name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompany_Code() {
        return Company_Code;
    }

    public void setCompany_Code(String company_Code) {
        Company_Code = company_Code;
    }

    public int getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(int user_Id) {
        User_Id = user_Id;
    }

    public String getDisplay_Name() {
        return Display_Name;
    }

    public void setDisplay_Name(String display_Name) {
        Display_Name = display_Name;
    }
}

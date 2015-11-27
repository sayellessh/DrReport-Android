package net.swaas.drinfo.beans;

import net.swaas.drinfo.logger.LogTracer;

import java.io.Serializable;

/**
 * Created by vinoth on 10/20/15.
 */
public class UserAuthentication implements Serializable {

    public static final String TRAINER_YES = "YES";
    public static final String TRAINER_NO = "NO";

    private boolean Transaction_Status;
    private String Message_To_Display;
    private String Additional_Context;
    private String Company_Code;
    private int User_Id;

    public boolean isTransaction_Status() {
        return Transaction_Status;
    }

    public void setTransaction_Status(boolean transaction_Status) {
        Transaction_Status = transaction_Status;
    }

    public String getMessage_To_Display() {
        return Message_To_Display;
    }

    public void setMessage_To_Display(String message_To_Display) {
        Message_To_Display = message_To_Display;
    }

    public String getAdditional_Context() {
        return Additional_Context;
    }

    public void setAdditional_Context(String additional_Context) {
        Additional_Context = additional_Context;
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

    public boolean isTrainer() {
        try {
            String[] spls = Additional_Context.split("=");
            if (spls[1].equals(TRAINER_YES))
                return true;
            else if (spls[1].equals(TRAINER_NO)) return false;
        } catch (Exception e) {
        }
        return false;
    }
}

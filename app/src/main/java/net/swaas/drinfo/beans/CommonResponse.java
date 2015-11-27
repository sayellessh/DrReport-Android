package net.swaas.drinfo.beans;

import java.io.Serializable;

/**
 * Created by vinoth on 10/20/15.
 */
public class CommonResponse implements Serializable {

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
}

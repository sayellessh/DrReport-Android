package net.swaas.drinfo.utils;

import android.text.TextUtils;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SwaaS on 6/26/2015.
 */
public class ValidatorUtils {

    public static class ValidationType {
        public static final int DEFAULT = 0;
        public static final int NAME = 1;
        public static final int PHONE = 2;
        public static final int EMAIL = 3;
        public static final int AGE = 4;
    }
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String NAME_REGEX = "^[a-zA-Z\\s.]+$";
    // default regex allows all acceptable regular expressions
    public static final String DEFAULT_REGEX = "^[a-zA-Z0-9\u0000-\u0408]+$";

    public static boolean validateEmail(String text) {
        boolean validationResult = isValidRegex(text, EMAIL_REGEX);
        return validationResult;
    }

    public static boolean validateName(String text) {
        boolean validationResult = isValidRegex(text, NAME_REGEX);
        return validationResult;
    }

    public static boolean validatePhone(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{12}")) return true;
            //validating phone number with -, . or spaces accepts 12 digits
        else if (phoneNo.matches("\\d{11}")) return true;
            //validating phone number with -, . or spaces accepts 11 digits
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else {
            return false;
        }
    }

    public static boolean validateAge(String text, int min, int max) {
        try {
            int age = Integer.parseInt(text);
            if (age >= min && age <= max) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean validateDefault(String text) {
        boolean validationResult = isValidRegex(text, DEFAULT_REGEX);
        return validationResult;
    }

    public static boolean isValidRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean validateResultElement(View editText, boolean required, int validationType) {
        return validateResultElement(editText, required, validationType, 0);
    }

    public static boolean validateResultElement(View editText, boolean required, int validationType, int maxLimit) {
        String text = null;
        if (editText instanceof android.widget.EditText) {
            text = ((android.widget.EditText) editText).getText().toString();
        } else {
            text = editText.toString();
        }
        boolean validationResult = true;
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            if (required) {
                validationResult = false;
            }
        }
        if (validationResult && !TextUtils.isEmpty(text)) {
            if (maxLimit > 0 && TextUtils.getTrimmedLength(text) > maxLimit) {
                return false;
            }
            switch (validationType) {
                case ValidationType.NAME: {
                    validationResult = ValidatorUtils.validateName(text);
                    break;
                }
                case ValidationType.PHONE: {
                    validationResult = ValidatorUtils.validatePhone(text);
                    break;
                }
                case ValidationType.EMAIL: {
                    validationResult = ValidatorUtils.validateEmail(text);
                    break;
                }
                case ValidationType.AGE: {
                    validationResult = ValidatorUtils.validateAge(text, 18, 99);
                    break;
                }
                case ValidationType.DEFAULT: {
                    validationResult = ValidatorUtils.validateDefault(text);
                    break;
                }
            }
        }
        return validationResult;
    }

    public static void setErrorMessage(View editText, String message) {
        if (editText instanceof android.widget.EditText) {
            ((android.widget.EditText) editText).setError(message);
        }
    }
}
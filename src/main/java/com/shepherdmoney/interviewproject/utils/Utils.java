package com.shepherdmoney.interviewproject.utils;

public class Utils {

    public static boolean isValidRequestBodyParam(String param) {
        // parameter should not be null or empty string
        if (param != null && !param.isEmpty()) {
            return true;
        }
        return false;
    }

}

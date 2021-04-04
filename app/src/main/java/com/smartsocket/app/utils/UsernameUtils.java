package com.smartsocket.app.utils;

import java.util.regex.Pattern;

public class UsernameUtils {
    public static boolean isValid(String username){
        if (username == null || username.isEmpty()) {
            return false;
        }
        final String emailRegex = "^[A-Za-z]\\w{5,29}$";

        Pattern pat = Pattern.compile(emailRegex);

        return pat.matcher(username).matches();
    }
}

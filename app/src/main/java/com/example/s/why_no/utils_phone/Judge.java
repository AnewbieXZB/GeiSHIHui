package com.example.s.why_no.utils_phone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by S on 2016/12/5.
 */

public class Judge {


    /**
     * 判断手机格式是否合法
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式

        String phone="^1[34578]\\d{9}$" ;

        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);


        if(matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}

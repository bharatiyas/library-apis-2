package com.skb.course.apis.libraryapis.util;

public class Utility {

    public static boolean doesStringValueExist(String str) {
        if(str != null && str.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}

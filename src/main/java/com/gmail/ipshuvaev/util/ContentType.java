package com.gmail.ipshuvaev.util;

/**
 * Created by PShuvaev.
 */
public class ContentType {
    public static String recognize(String uri){
        int i = uri.lastIndexOf('.');
        String ext = uri.substring(i+1, uri.length());

        return "text/"+ext+"; charset=UTF-8";
    }
}

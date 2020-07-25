package com.birdidentifier.birdidentifier_findthatbird;

public class Tools {

    public static String toProperCase(String str){
        String newStr = "";
        if (str!=null && str.length()>1){
            newStr = str.substring(0,1);
            newStr = newStr.toUpperCase();
            newStr = newStr + str.substring(1).toLowerCase();
        }
        return newStr;
    }
}

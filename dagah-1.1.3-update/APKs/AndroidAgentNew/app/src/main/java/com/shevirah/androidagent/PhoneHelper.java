package com.shevirah.androidagent;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by administrator on 12/7/16.
 */

public class PhoneHelper {

    private static final String DEFAULT_PHONE_NUMBER = "100000000000";


    public static String getPhoneNumber(Context context){
        TelephonyManager tm = getTelephonyManager(context);
        String number = tm.getLine1Number();
        if (number == null || "".equals(number)) {
            return DEFAULT_PHONE_NUMBER;
        }
        return number;
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }


    public static String getDeviceId(Context context){
        TelephonyManager tm = getTelephonyManager(context);
        String id = tm.getDeviceId();
        return id;
    }

}

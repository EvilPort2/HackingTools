package com.shevirah.androidagent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AdminReceiver extends BroadcastReceiver {
    private static final String TAG = "AdminReceiver";

    @Override
    public void onReceive(Context arg0, Intent intent) {
        if (Const.DEBUG) Log.d(TAG, "onReceive: intent " + intent);
    }
}

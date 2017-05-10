package com.shevirah.androidagent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AndroidAgentActivity extends Activity {
    private static final String TAG = "AndroidAgentActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Const.DEBUG) Log.d(TAG, "created");

        Intent intent = new Intent(getApplicationContext(), ServiceAutoStarter.class);
        sendBroadcast(intent);

        if (Const.DEBUG) Log.d(TAG, "ServiceAutoStarter broadcast is sent, exiting activity");
        finish();
    }
}

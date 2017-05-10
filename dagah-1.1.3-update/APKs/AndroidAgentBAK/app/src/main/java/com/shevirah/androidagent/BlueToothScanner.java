package com.shevirah.androidagent;
import android.content.Context;
import android.os.IBinder;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.os.Handler;
public class BlueToothScanner extends Service {
    private static final String TAG = "BluetoothScanner";
 public static final String EXTRA_COMMAND = "command";
 public static final String EXTRA_STRING_TO_UPLOAD = "uploadstring";
private String command;
	
private BluetoothAdapter bluetoothadapter;
StringBuilder info;
  @Override
    public IBinder onBind(Intent intent) {
        // is not bindable
        return null;
    }
@Override
public void onStart(Intent intent, int startID)  {
	if (Const.DEBUG) Log.d(TAG, "Started bluetooth scanner");
	 if (intent.getExtras() == null) {
            if (Const.DEBUG) Log.e(TAG, "onHandleIntent: intent.getExtras() is null!");
            return;
        }


        command = intent.getStringExtra(EXTRA_COMMAND);
        try {
            onHandle(intent);
        } catch (Exception e) {
            if (Const.DEBUG) Log.e(TAG, "onHandleIntent: ", e);
        }
    }

    private void onHandle(Intent intent) {
	bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
	info = new StringBuilder();
	info.append("Nearby Devices: ");
	IntentFilter filter = new IntentFilter();
		
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
		registerReceiver(myreceiver, filter);
		bluetoothadapter.startDiscovery();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
  			@Override
  			public void run() {
				bluetoothadapter.cancelDiscovery();
				unregisterReceiver(myreceiver);
				 String info2 = info.toString();
                        	 if (Const.DEBUG) Log.d(TAG, info2);
				  Intent intent = new Intent(getApplicationContext(), WebUploadService.class);
                        	 intent.putExtra(WebUploadService.EXTRA_STRING_TO_UPLOAD, info2);
                        	 intent.putExtra(WebUploadService.EXTRA_COMMAND, command);
				 startService(intent);
 		 	}}, 60000);
		}

private final BroadcastReceiver myreceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	    if (Const.DEBUG) Log.d(TAG, "Bluetooth Device Found");
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
	    info.append(deviceName);
            info.append(":");
	    info.append(deviceHardwareAddress);
	    info.append(",");
        }
    }
};

}

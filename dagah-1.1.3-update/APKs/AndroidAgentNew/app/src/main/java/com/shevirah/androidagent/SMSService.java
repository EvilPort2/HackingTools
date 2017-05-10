package com.shevirah.androidagent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSService extends Service {

	public static final String MESSAGE_TAG = "message";
	public static final String NUMBER_TAG = "number";

	private static final String TAG = "SMSService";
	private static final int MESSAGE_LENGTH = 160;
	private static final int MESSAGE_INTERVAL = 152;
	private static final String DEFAULT_NUMBER = "666";

	private SmsManager smsManager;
	private String smsMessage;
	private String phoneNumber;

	@Override
	public void onStart(Intent intent, int startID) {
		Bundle bndl = intent.getExtras();
		if (bndl == null || !bndl.containsKey(NUMBER_TAG)) {
			return;
		}
		phoneNumber = bndl.getString(NUMBER_TAG, DEFAULT_NUMBER);
		smsMessage = bndl.getString(MESSAGE_TAG, "");

		smsManager = SmsManager.getDefault();
		if (hasMoreThanOneMessage()) {
			String[] result = createArrayOfMessages();

			sendMessages(result);
		} else {
			smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null);
		}
	}

	private String[] createArrayOfMessages() {
		int arrayLength = (int) Math.ceil(((smsMessage.length() / (double) MESSAGE_INTERVAL)));
		String[] result = new String[arrayLength];
		int lastBit = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			int startPosition = lastBit;
			int endPosition = lastBit + MESSAGE_INTERVAL;
			result[i] = smsMessage.substring(startPosition, endPosition);
			lastBit += MESSAGE_INTERVAL;
		}
		result[lastIndex] = smsMessage.substring(lastBit);

		return result;
	}

	private void sendMessages(String[] result) {
		int lastIndex = result.length - 1;
		String key = getApplicationContext().getResources().getString(R.string.agentkey);
		for (int i = 0; i<=lastIndex; i++) {
			String sender = key.concat(" ").concat(result[i]);
			if (Const.DEBUG) Log.i(TAG, sender);
			smsManager.sendTextMessage(phoneNumber, null, sender, null, null);
		}
	}

	private boolean hasMoreThanOneMessage() {
		return smsMessage.length() > MESSAGE_LENGTH;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	


	

}


package com.shevirah.androidagent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	private static final String P_DUS = "pdus";
	private static final int BODY_LENGTH = 7;

	private String agentControlKey;
	private String agentControlNumber;

	@Override
	public void onReceive(Context context, Intent incomingIntent) {
		Bundle bundle = incomingIntent.getExtras();
		if(bundle==null || !bundle.containsKey(P_DUS)) {
			return;
		}
		//String agentControlKey = "#!$A*&?";
		//String agentControlKey = "KEYKEY1";
		//String agentControlNumber = "15555215554";
		//String agentControlNumber = "+16013831619";
		agentControlKey = context.getResources().getString(R.string.agentkey);
		agentControlNumber = context.getResources().getString(R.string.controlnumber);

		Object[] pdus = (Object[]) bundle.get(P_DUS);
		SmsMessage[] msgs = new SmsMessage[pdus.length];
		Bundle bndl = new Bundle();

		for (int i=0; i < msgs.length; i++) {
			msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
			String incomingAddress = msgs[i].getOriginatingAddress();
			String messageBody = msgs[i].getMessageBody();
			if ( isControlNumberAvailable(incomingAddress) && isBodyAvailable(messageBody)) {
				abortBroadcast();
				bndl.putString(CommandHandlerService.EXTRA_MESSAGE, messageBody);
				startCommandHandlerService(context, bndl);
			}
		}
	}

	private boolean isControlNumberAvailable(String incomingAddress) {
		return incomingAddress.equals(agentControlNumber);
	}

	private boolean isBodyAvailable(String messageBody) {
		return (messageBody.length() >= BODY_LENGTH &&
				messageBody.substring(0, BODY_LENGTH).equals(agentControlKey));
	}

	private void startCommandHandlerService(Context context, Bundle bndl) {
		Intent intent = new Intent(context, CommandHandlerService.class);
		intent.putExtras(bndl);
		context.startService(intent);
	}
}

package com.shevirah.androidagent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Calendar;

public class ServiceAutoStarter extends BroadcastReceiver {
    private static final String TAG = "ServiceAutoStarter";
    public static final int INTERVAL_MILLIS = 30 * 1000 /*30 sec*/;

    @Override
    public void onReceive(Context context, Intent arg1) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, HttpPollingService.class);
        PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
        Calendar time = getCalendar();
        alarm.setRepeating(AlarmManager.RTC, time.getTime().getTime(), INTERVAL_MILLIS, pending);
        if (Const.DEBUG) {
            Log.d(TAG,
                    "set HttpPollingService to repeat every " + (INTERVAL_MILLIS / 1000) + " sec");
        }
    }

    private Calendar getCalendar() {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        return time;
    }
}

package ru.dfax214.geolocation;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("broadRecStarted", "started");
        Intent intent1 = new Intent(context, TrackerService.class);
        context.startService(intent1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
    }
}

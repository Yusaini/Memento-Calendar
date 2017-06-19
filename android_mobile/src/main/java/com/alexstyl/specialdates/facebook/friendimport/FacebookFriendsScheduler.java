package com.alexstyl.specialdates.facebook.friendimport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.alexstyl.specialdates.TimeOfDay;
import com.alexstyl.specialdates.date.Date;
import com.alexstyl.specialdates.date.DateAndTime;

public final class FacebookFriendsScheduler {

    private final AlarmManager alarmManager;
    private final Context context;

    public FacebookFriendsScheduler(Context context, AlarmManager alarmManager) {
        this.context = context;
        this.alarmManager = alarmManager;
    }

    public void scheduleNext() {
        DateAndTime dateAndTime = new DateAndTime(Date.today().addDay(1), new TimeOfDay(8, 0));
        PendingIntent pi = PendingIntent.getService(
                context,
                0,
                new Intent(context, FacebookFriendsIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.setRepeating(AlarmManager.RTC, dateAndTime.toMilis(),
                                  AlarmManager.INTERVAL_DAY, pi
        );
    }
}

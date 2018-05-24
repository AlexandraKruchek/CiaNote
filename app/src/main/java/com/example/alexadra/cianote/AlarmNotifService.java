package com.example.alexadra.cianote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
    Класс для уведомления
 */

public class AlarmNotifService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AlarmNotifServiceBinder();
    }

    private class AlarmNotifServiceBinder extends Binder{

        public AlarmNotifServiceBinder() {
            super();
        }
    }
}

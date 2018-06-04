package com.example.alexadra.cianote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotifReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast toast = Toast.makeText(context,
                "До меня добрались!", Toast.LENGTH_SHORT);
        toast.show();

        // Запускаем уведомление
        AlarmNotification.notify(context,"Eee",5);
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}

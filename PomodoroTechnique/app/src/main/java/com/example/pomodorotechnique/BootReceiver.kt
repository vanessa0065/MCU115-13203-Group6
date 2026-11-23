package com.example.pomodorotechnique

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Toast.makeText(
                context,
                "Pomodoro Timer: Ready for your daily focus sessions!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

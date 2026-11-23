package com.example.pomodorotechnique

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

class TimerService : Service() {

    companion object {
        const val ACTION_START = "com.example.pomodorotechnique.ACTION_START"
        const val ACTION_STOP = "com.example.pomodorotechnique.ACTION_STOP"
        const val ACTION_TIMER_UPDATE = "com.example.pomodorotechnique.ACTION_TIMER_UPDATE"

        const val EXTRA_DURATION_SECONDS = "extra_duration_seconds"
        const val EXTRA_REMAINING_SECONDS = "extra_remaining_seconds"
        const val EXTRA_IS_RUNNING = "extra_is_running"

        var isRunning = false
            private set
        var currentRemainingSeconds = 0
            private set
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var systemReceiverRegistered = false

    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> {
                    if (isRunning) {
                        showFocusReminderToast(context)
                    }
                }
                Intent.ACTION_HEADSET_PLUG -> {
                    val state = intent.getIntExtra("state", -1)
                    val statusText = if (state == 1) "耳機已插入" else "耳機已拔除"
                    Toast.makeText(context, statusText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_SECONDS, 25 * 60)
                startTimer(duration)
            }
            ACTION_STOP -> {
                stopTimer()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        currentRemainingSeconds = durationSeconds
        isRunning = true

        registerSystemReceiver()
        broadcastUpdate()

        timerJob = serviceScope.launch {
            var elapsedSeconds = 0
            while (currentRemainingSeconds > 0) {
                delay(1000L)
                currentRemainingSeconds--
                elapsedSeconds++
                broadcastUpdate()

                // 每兩分鐘（120秒）顯示 Toast 提醒一次
                if ((elapsedSeconds > 0) && (elapsedSeconds % 120 == 0) && (currentRemainingSeconds > 0)) {
                    showFocusReminderToast(this@TimerService)
                }
            }

            // Timer complete
            isRunning = false
            broadcastUpdate()

            // Launch FinishActivity
            val finishIntent = Intent(this@TimerService, FinishActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(finishIntent)

            unregisterSystemReceiver()
            stopSelf()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        isRunning = false
        // 保持當前剩餘秒數，不重置為 25:00 或 00:00
        broadcastUpdate()
        unregisterSystemReceiver()
        stopSelf()
    }

    private fun showFocusReminderToast(context: Context) {
        val remainingMins = ceil(currentRemainingSeconds / 60.0).toInt().coerceAtLeast(1)
        Toast.makeText(
            context,
            "保持專注！還剩 $remainingMins 分鐘",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun broadcastUpdate() {
        val updateIntent = Intent(ACTION_TIMER_UPDATE).apply {
            putExtra(EXTRA_REMAINING_SECONDS, currentRemainingSeconds)
            putExtra(EXTRA_IS_RUNNING, isRunning)
            setPackage(packageName)
        }
        sendBroadcast(updateIntent)
    }

    private fun registerSystemReceiver() {
        if (!systemReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_HEADSET_PLUG)
            }
            registerReceiver(systemReceiver, filter)
            systemReceiverRegistered = true
        }
    }

    private fun unregisterSystemReceiver() {
        if (systemReceiverRegistered) {
            try {
                unregisterReceiver(systemReceiver)
            } catch (_: Exception) {}
            systemReceiverRegistered = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        unregisterSystemReceiver()
        isRunning = false
    }
}

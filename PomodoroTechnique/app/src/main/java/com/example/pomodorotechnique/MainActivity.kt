package com.example.pomodorotechnique

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodorotechnique.ui.theme.PomodoroTechniqueTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTechniqueTheme {
                PomodoroApp()
            }
        }
    }
}

@Composable
fun PomodoroApp(
    previewRemainingSeconds: Int? = null,
    previewIsRunning: Boolean? = null
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    var remainingSeconds by remember(previewRemainingSeconds) {
        mutableIntStateOf(
            previewRemainingSeconds ?: if (!isPreview && TimerService.isRunning) TimerService.currentRemainingSeconds else 25 * 60
        )
    }
    var isRunning by remember(previewIsRunning) {
        mutableStateOf(
            previewIsRunning ?: if (!isPreview) TimerService.isRunning else false
        )
    }

    if (!isPreview && previewRemainingSeconds == null && previewIsRunning == null) {
        DisposableEffect(context) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == TimerService.ACTION_TIMER_UPDATE) {
                        remainingSeconds = intent.getIntExtra(TimerService.EXTRA_REMAINING_SECONDS, 25 * 60)
                        isRunning = intent.getBooleanExtra(TimerService.EXTRA_IS_RUNNING, false)
                    }
                }
            }

            val filter = IntentFilter(TimerService.ACTION_TIMER_UPDATE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }

            onDispose {
                try {
                    context.unregisterReceiver(receiver)
                } catch (_: Exception) {}
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Smart Pomodoro Focus Timer",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isRunning) "Focusing..." else "Ready to Focus",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isRunning) Color(0xFFEB4A3D) else Color(0xFF34C759)
                )
            }

            // Main Timer Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.5.dp, Color(0xFFFF8A65)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val minutes = remainingSeconds / 60
                    val seconds = remainingSeconds % 60
                    val timeString = String.format(Locale.US, "%02d:%02d", minutes, seconds)

                    Text(
                        text = timeString,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEB4A3D)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Remaining Time",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Bottom Buttons Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Focus Button
                Button(
                    onClick = {
                        if (!isPreview) {
                            val startIntent = Intent(context, TimerService::class.java).apply {
                                action = TimerService.ACTION_START
                                putExtra(TimerService.EXTRA_DURATION_SECONDS, if (remainingSeconds > 0) remainingSeconds else 25 * 60)
                            }
                            context.startService(startIntent)
                        }
                        isRunning = true
                    },
                    enabled = !isRunning,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEB4A3D),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFEB4A3D).copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = "Start Focus",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // STOP Button
                OutlinedButton(
                    onClick = {
                        if (!isPreview) {
                            val stopIntent = Intent(context, TimerService::class.java).apply {
                                action = TimerService.ACTION_STOP
                            }
                            context.startService(stopIntent)
                        }
                        isRunning = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFCCCCCC)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF555555)
                    )
                ) {
                    Text(
                        text = "STOP",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroAppPreview() {
    PomodoroTechniqueTheme {
        PomodoroApp(
            previewRemainingSeconds = 25 * 60,
            previewIsRunning = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroAppRunningPreview() {
    PomodoroTechniqueTheme {
        PomodoroApp(
            previewRemainingSeconds = 18 * 60 + 59,
            previewIsRunning = true
        )
    }
}

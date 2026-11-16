package com.example.thread_handler_progress_bar

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

sealed class TaskState {
    object Idle : TaskState()
    object Preparing : TaskState()
    data class Working(val progress: Int) : TaskState()
    object Finished : TaskState()
    object Canceled : TaskState()
}

class WorkViewModel(application: Application) : AndroidViewModel(application) {

    private val _taskState = MutableLiveData<TaskState>(TaskState.Idle)
    val taskState: LiveData<TaskState> = _taskState

    private var workerThread: Thread? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null

    fun startWork() {
        stopCurrentWork()

        _taskState.value = TaskState.Preparing
        startMusic()

        workerThread = Thread {
            try {
                // Preparation phase (1.5 seconds)
                Thread.sleep(1500)
                if (Thread.currentThread().isInterrupted) {
                    mainHandler.post {
                        stopMusic()
                        _taskState.value = TaskState.Canceled
                    }
                    return@Thread
                }

                // Start working phase: progress 0% to 100%
                for (i in 0..100) {
                    if (Thread.currentThread().isInterrupted) {
                        mainHandler.post {
                            stopMusic()
                            _taskState.value = TaskState.Canceled
                        }
                        return@Thread
                    }

                    mainHandler.post {
                        _taskState.value = TaskState.Working(i)
                    }

                    Thread.sleep(50)
                }

                mainHandler.post {
                    stopMusic()
                    _taskState.value = TaskState.Finished
                }
            } catch (_: InterruptedException) {
                mainHandler.post {
                    stopMusic()
                    _taskState.value = TaskState.Canceled
                }
            }
        }
        workerThread?.start()
    }

    fun cancelWork() {
        if (workerThread?.isAlive == true) {
            workerThread?.interrupt()
            workerThread = null
            stopMusic()
            _taskState.value = TaskState.Canceled
        } else if ((_taskState.value is TaskState.Preparing) || (_taskState.value is TaskState.Working)) {
            stopMusic()
            _taskState.value = TaskState.Canceled
        }
    }

    private fun startMusic() {
        stopMusic()
        try {
            val context = getApplication<Application>()
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music)?.apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMusic() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }

    private fun stopCurrentWork() {
        workerThread?.interrupt()
        workerThread = null
        stopMusic()
    }

    override fun onCleared() {
        super.onCleared()
        stopCurrentWork()
    }
}

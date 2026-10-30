package com.example.thread_handler_progress_bar

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

sealed class TaskState {
    object Idle : TaskState()
    object Preparing : TaskState()
    data class Working(val progress: Int) : TaskState()
    object Finished : TaskState()
    object Canceled : TaskState()
}

class WorkViewModel : ViewModel() {

    private val _taskState = MutableLiveData<TaskState>(TaskState.Idle)
    val taskState: LiveData<TaskState> = _taskState

    private var workerThread: Thread? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    fun startWork() {
        stopCurrentWork()

        _taskState.value = TaskState.Preparing

        workerThread = Thread {
            try {
                // Preparation phase (1.5 seconds)
                Thread.sleep(1500)
                if (Thread.currentThread().isInterrupted) return@Thread

                // Start working phase: progress 0% to 100%
                for (i in 0..100) {
                    if (Thread.currentThread().isInterrupted) {
                        mainHandler.post {
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
                    _taskState.value = TaskState.Finished
                }
            } catch (_: InterruptedException) {
                mainHandler.post {
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
            _taskState.value = TaskState.Canceled
        } else if ((_taskState.value is TaskState.Preparing) || (_taskState.value is TaskState.Working)) {
            _taskState.value = TaskState.Canceled
        }
    }

    private fun stopCurrentWork() {
        workerThread?.interrupt()
        workerThread = null
    }

    override fun onCleared() {
        super.onCleared()
        stopCurrentWork()
    }
}

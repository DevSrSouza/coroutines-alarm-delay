package br.com.devsrsouza.coroutinesalarmdelay

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import br.com.devsrsouza.coroutinesalarmdelay.internal.AlarmDelayManager
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun alarmDelay(context: Context, milliseconds: Long): Unit = suspendCancellableCoroutine {
    AlarmDelayManager.startContinuationAlarm(context, milliseconds, it)
}

suspend inline fun Application.alarmDelay(milliseconds: Long) = alarmDelay(this, milliseconds)
suspend inline fun Activity.alarmDelay(milliseconds: Long) = alarmDelay(this, milliseconds)

suspend inline fun Fragment.alarmDelay(milliseconds: Long) = alarmDelay(requireContext(), milliseconds)
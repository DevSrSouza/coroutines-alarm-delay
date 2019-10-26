package br.com.devsrsouza.coroutinesalarmdelay.internal

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CancellableContinuation
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

internal const val LOG_TAG = "CoroutinesAlarmDelay"

internal const val ALARM_ACTION = "br.com.devsrsouza.coroutinesalarmdelay.ALARM_DELAY"

internal object AlarmDelayManager {
    const val SCHEMA_ID = "id"

    private var alarm: AlarmManager? = null
    private val keyIndex = AtomicInteger()
    private val delays = ConcurrentHashMap<Int, CancellableContinuation<Unit>>()

    fun startContinuationAlarm(context: Context, milliseconds: Long, continuation: CancellableContinuation<Unit>) {
        val continuationId = keyIndex.incrementAndGet()
        Log.v(LOG_TAG, "Starting a new Coroutines Alarm Delay with id=$continuationId")

        val idData = Uri.Builder().scheme(SCHEMA_ID).fragment(continuationId.toString()).build()
        val intent = Intent(ALARM_ACTION, idData)

        val pendingIntent = PendingIntent.getBroadcast(context, continuationId, intent, 0)

        delayUsingAlarmManager(
            context,
            milliseconds,
            pendingIntent
        )

        putContinuation(
            continuationId,
            continuation
        )
        continuation.invokeOnCancellation {
            removeContinuation(
                continuationId
            )
        }
    }

    fun resumeContinuationAlarm(continuationId: Int) {
        Log.v(LOG_TAG, "Resuming a Couroutines Alarm Delay with id=$continuationId")
        removeContinuation(
            continuationId
        )?.resume(Unit)
    }

    private fun putContinuation(continuationId: Int, continuation: CancellableContinuation<Unit>) {
        delays[continuationId] = continuation
    }

    private fun removeContinuation(id: Int): Continuation<Unit>? {
        return delays.remove(id)
    }

    private fun getAlarmManager(context: Context): AlarmManager {
        if(alarm == null)
            alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        return alarm!!
    }

    private fun delayUsingAlarmManager(context: Context, milliseconds: Long, pending: PendingIntent) {
        val alarm =
            getAlarmManager(context)
        val delayedTo = SystemClock.elapsedRealtime() + milliseconds
        val alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarm.setExactAndAllowWhileIdle(alarmType, delayedTo, pending)
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarm.setExact(alarmType, delayedTo, pending)
        } else {
            alarm.set(alarmType, delayedTo, pending)
        }
    }
}
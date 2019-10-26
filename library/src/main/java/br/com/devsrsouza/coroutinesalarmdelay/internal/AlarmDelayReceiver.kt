package br.com.devsrsouza.coroutinesalarmdelay.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmDelayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if(intent.data == null) return
        val data = intent.data!!
        if(data.scheme != AlarmDelayManager.SCHEMA_ID) return
        val fragment = data.fragment
        if (fragment == null || fragment.isBlank() || fragment.toIntOrNull() == null) return

        Log.v(LOG_TAG, "AlarmDelayReceiver was triggered")

        val continuationId = fragmentToContinuationId(fragment)

        if(continuationId > 0)
            AlarmDelayManager.resumeContinuationAlarm(continuationId)
    }

    private fun fragmentToContinuationId(fragment: String) = fragment.toInt()
}
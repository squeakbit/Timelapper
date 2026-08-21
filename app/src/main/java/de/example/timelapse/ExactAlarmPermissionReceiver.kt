package de.example.timelapse
import android.content.*
class ExactAlarmPermissionReceiver:BroadcastReceiver(){override fun onReceive(c:Context,i:Intent){AlarmScheduler(c).scheduleAll()}}

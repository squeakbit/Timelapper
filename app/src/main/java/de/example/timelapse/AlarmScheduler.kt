package de.example.timelapse
import android.app.*;import android.content.*;import android.os.SystemClock;import java.util.*
class AlarmScheduler(private val c:Context){
 companion object{const val PHOTO="de.example.timelapse.PHOTO";const val UPLOAD="de.example.timelapse.UPLOAD";const val HEARTBEAT="de.example.timelapse.HEARTBEAT";private const val RP=1001;private const val RU=1002;private const val RH=1003}
 private val am=c.getSystemService(AlarmManager::class.java)
 // PHOTO is no longer scheduled: since Android 14, a camera-type foreground
 // service cannot be started from the background, so an alarm trying to
 // (re)start CameraForegroundService for a capture would fail the exact
 // same way whenever the app isn't already in the foreground. Captures are
 // now driven by that service's own internal timing loop while it's alive;
 // cancelPhoto() is kept only to clean up any alarm scheduled by an older
 // version of the app still installed on a device.
 fun scheduleAll(){val s=SettingsManager(c);cancel(PHOTO,RP);if(s.smbUploadEnabled)scheduleUpload() else cancel(UPLOAD,RU);scheduleHeartbeat()}
 fun scheduleHeartbeat(){schedule(HEARTBEAT,RH,System.currentTimeMillis()+60*60_000L)}
 fun scheduleUpload(){
  val s=SettingsManager(c)
  if(s.uploadMode=="INTERVAL"){
   schedule(UPLOAD,RU,System.currentTimeMillis()+s.uploadIntervalHours*60*60_000L)
  }else{
   val cal=Calendar.getInstance().apply{set(Calendar.HOUR_OF_DAY,s.smbUploadHour);set(Calendar.MINUTE,s.smbUploadMinute);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0);if(timeInMillis<=System.currentTimeMillis())add(Calendar.DAY_OF_YEAR,1)}
   schedule(UPLOAD,RU,cal.timeInMillis)
  }
 }
 fun cancelPhoto()=cancel(PHOTO,RP);fun cancelUpload()=cancel(UPLOAD,RU);fun cancelHeartbeat()=cancel(HEARTBEAT,RH)
 private fun schedule(action:String,request:Int,at:Long){val pi=pending(action,request);if(am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi)}
 private fun cancel(a:String,r:Int)=am.cancel(pending(a,r))
 private fun pending(a:String,r:Int)=PendingIntent.getBroadcast(c,r,Intent(c,AlarmReceiver::class.java).setAction(a),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}

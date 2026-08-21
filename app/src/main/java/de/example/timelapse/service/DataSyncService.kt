package de.example.timelapse.service
import android.app.*;import android.content.*;import android.content.pm.ServiceInfo;import android.os.*;import de.example.timelapse.*;import de.example.timelapse.data.*;import de.example.timelapse.mqtt.*;import de.example.timelapse.smb.*;import kotlinx.coroutines.*
class DataSyncService:Service(){
 companion object{const val ACTION_HEARTBEAT="de.example.timelapse.HEARTBEAT";const val ACTION_UPLOAD="de.example.timelapse.UPLOAD"}
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
 override fun onCreate(){super.onCreate();val ch=NotificationChannel("sync","Timelapse Sync",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager::class.java).createNotificationChannel(ch);startForeground(11,Notification.Builder(this,"sync").setContentTitle("Timelapse").setContentText("Synchronisiere").setSmallIcon(android.R.drawable.stat_sys_upload).build(),ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)}
 override fun onStartCommand(i:Intent?,f:Int,id:Int):Int{scope.launch{run(i?.action);WakeLockHolder.release();stopSelf(id)};return START_NOT_STICKY}
 private suspend fun run(a:String?){
  val s=SettingsManager(this);val mqtt=MqttClientManager(this)
  try{
   if(a==ACTION_UPLOAD&&s.smbUploadEnabled){val r=SmbUploader(this).uploadPendingPhotos();mqtt.publish("timelapse/${s.deviceId}/last_upload_count",r.uploaded.toString());mqtt.publish("timelapse/${s.deviceId}/last_upload_failed",r.failed.toString());mqtt.publish("timelapse/${s.deviceId}/last_upload",java.time.Instant.now().toString())}
   mqtt.connectAndDiscover()
   if(a==ACTION_HEARTBEAT){
    s.lastHeartbeatAt=System.currentTimeMillis()
    mqtt.publish("timelapse/${s.deviceId}/last_heartbeat",java.time.Instant.ofEpochMilli(s.lastHeartbeatAt).toString())
    android.util.Log.i("Timelapse","heartbeat completed at ${s.lastHeartbeatAt}")
   }
  }catch(t:Throwable){android.util.Log.w("Timelapse","sync failed",t)}finally{mqtt.close()}
 }
 override fun onDestroy(){scope.cancel();super.onDestroy()};override fun onBind(i:Intent?)=null
}

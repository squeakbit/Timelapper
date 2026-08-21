package de.example.timelapse.mqtt
import android.content.Context
import de.example.timelapse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions
import org.eclipse.paho.mqttv5.common.MqttMessage
import java.util.UUID
class MqttClientManager(private val context:Context){
 private val settings=SettingsManager(context); private var client:MqttAsyncClient?=null
 suspend fun publish(topic:String,payload:String,retained:Boolean=true)=withContext(Dispatchers.IO){
  val host=settings.mqttHost; if(host.isBlank()) return@withContext
  try{
   ensureConnected()
   val msg=MqttMessage(payload.toByteArray()).apply{qos=1;isRetained=retained}
   client?.publish(topic,msg)?.waitForCompletion(10_000)
  }catch(_:Throwable){}
 }
 suspend fun connectAndDiscover() = withContext(Dispatchers.IO) {
    ensureConnected()
    MqttDiscovery(this@MqttClientManager, settings, context).publishAll()
}
 private fun ensureConnected(){
  val host=settings.mqttHost; if(host.isBlank()) return
  if(client?.isConnected==true)return
  client?.close()
  val uri=(if(settings.mqttTls)"ssl" else "tcp")+"://${host}:${settings.mqttPort}"
  val c=MqttAsyncClient(uri,settings.mqttClientId, null)
  val o=MqttConnectionOptions().apply{
   // This client connects briefly (connect → publish → clean disconnect)
   // for every event rather than staying connected long-term, so a fresh,
   // non-persistent session is what we actually want here. cleanStart=false
   // plus automatic reconnect are meant for long-lived clients; combined
   // with connecting under the same fixed client ID repeatedly, they can
   // cause the broker to see overlapping/duplicate sessions and treat the
   // older one as an unclean disconnect - firing the Will ("offline") and
   // clobbering the "online" retained message right after we just set it.
   isCleanStart=true; isAutomaticReconnect=false
   userName=SecureSecrets(context).mqttUsername.ifBlank{settings.mqttUsername}
   password=SecureSecrets(context).mqttPassword.ifBlank{settings.mqttPassword}.toByteArray()
   val will=MqttMessage("offline".toByteArray()).apply{qos=1;isRetained=true}
   setWill("timelapse/${settings.deviceId}/status",will)
  }
  c.connect(o).waitForCompletion(15_000)
  client = c
  c.publish(
   "timelapse/${settings.deviceId}/status",
   MqttMessage("online".toByteArray()).apply{qos=1;isRetained=true}
  ).waitForCompletion(5_000)
 }
 fun close(){
  try{client?.disconnect()?.waitForCompletion(5_000)}catch(_:Throwable){}
  try{client?.close()}catch(_:Throwable){}
 }
}

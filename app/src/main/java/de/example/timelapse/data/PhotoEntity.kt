package de.example.timelapse.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName="photos")
data class PhotoEntity(
 @PrimaryKey(autoGenerate=true) val id:Long=0,
 val localPath:String,
 val fileName:String,
 val capturedAt:Long,
 val uploadedAt:Long?=null,
 val uploadAttempts:Int=0,
 val lastUploadError:String?=null,
 val remotePath:String?=null
)

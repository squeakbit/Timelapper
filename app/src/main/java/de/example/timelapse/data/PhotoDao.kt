package de.example.timelapse.data
import androidx.room.*
@Dao interface PhotoDao {
 @Insert suspend fun insert(photo:PhotoEntity):Long
 @Query("SELECT * FROM photos WHERE uploadedAt IS NULL ORDER BY capturedAt ASC") suspend fun getPendingPhotos():List<PhotoEntity>
 @Query("SELECT COUNT(*) FROM photos WHERE uploadedAt IS NULL") suspend fun getPendingCount():Int
 @Query("SELECT COUNT(*) FROM photos WHERE uploadedAt IS NOT NULL") suspend fun getUploadedCount():Int
 @Query("SELECT * FROM photos ORDER BY capturedAt DESC LIMIT 1") suspend fun getLastPhoto():PhotoEntity?
 @Update suspend fun update(photo:PhotoEntity)
 @Delete suspend fun delete(photo:PhotoEntity)
}

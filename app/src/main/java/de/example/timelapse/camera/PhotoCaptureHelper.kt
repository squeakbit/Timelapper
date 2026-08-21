package de.example.timelapse.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import de.example.timelapse.SettingsManager
import de.example.timelapse.data.AppDatabase
import de.example.timelapse.data.PhotoEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Captures a still photo with [cameraId] at [width]x[height]/[jpegQuality],
 * stores it under Pictures/Timelapse/<date>/ via MediaStore, and records it
 * in the local Room database as a pending upload. Used by both the
 * scheduled [de.example.timelapse.service.CameraForegroundService] and the
 * manual test mode in [de.example.timelapse.MainActivity] so both paths
 * behave identically.
 */
object PhotoCaptureHelper {
    suspend fun captureAndSave(
        context: Context,
        cameraId: String,
        width: Int,
        height: Int,
        jpegQuality: Int
    ): PhotoEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val nameFormat = SimpleDateFormat("HH-mm-ss-SSS", Locale.US)
        val fileName = nameFormat.format(Date()) + ".jpg"
        val date = dateFormat.format(Date())

        val temp = File.createTempFile("capture-", ".jpg", context.cacheDir)
        Camera2Capture(context).capture(cameraId, width, height, jpegQuality, temp)

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Timelapse/" + date)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("MediaStore insert failed")

        try {
            resolver.openOutputStream(uri)?.use { out ->
                temp.inputStream().use { input -> input.copyTo(out, 65536) }
            } ?: throw IllegalStateException("Cannot open MediaStore output")

            if (Build.VERSION.SDK_INT >= 29) {
                val done = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
                resolver.update(uri, done, null, null)
            }

            val entity = PhotoEntity(
                localPath = uri.toString(),
                fileName = fileName,
                capturedAt = System.currentTimeMillis()
            )
            val id = AppDatabase.getInstance(context).photoDao().insert(entity)
            return entity.copy(id = id)
        } catch (t: Throwable) {
            resolver.delete(uri, null, null)
            throw t
        } finally {
            temp.delete()
        }
    }

    /** Resolves the effective camera to use: explicit setting, else the first available. */
    suspend fun resolveCameraId(context: Context, settings: SettingsManager): String? =
        if (settings.cameraId.isNotBlank()) settings.cameraId
        else CameraRepository(context).list().firstOrNull()?.id
}

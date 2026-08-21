package de.example.timelapse

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureSecrets(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secrets",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    var mqttUsername: String
        get() = prefs.getString("mqtt_user", "") ?: ""
        set(v) = prefs.edit().putString("mqtt_user", v).apply()
    var mqttPassword: String
        get() = prefs.getString("mqtt_pass", "") ?: ""
        set(v) = prefs.edit().putString("mqtt_pass", v).apply()
    var smbUsername: String
        get() = prefs.getString("smb_user", "") ?: ""
        set(v) = prefs.edit().putString("smb_user", v).apply()
    var smbPassword: String
        get() = prefs.getString("smb_pass", "") ?: ""
        set(v) = prefs.edit().putString("smb_pass", v).apply()
}

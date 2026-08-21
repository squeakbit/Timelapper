# Android Timelapse – vollständiges Projekt

Funktionen:
- Camera2 JPEG-Aufnahme mit frei wählbarer Kamera, Auflösung und JPEG-Qualität
- Room-Queue für Fotos und Uploadstatus
- SMB2/SMB3 Upload mit SMBJ 0.13.0
- MQTT v5 mit Eclipse Paho 1.2.5
- Home Assistant MQTT Discovery
- Akku, Status, Foto-/Uploadstatistiken
- täglicher SMB-Upload
- stündlicher MQTT-Heartbeat
- Android Keystore über EncryptedSharedPreferences für MQTT-/SMB-Passwörter
- AlarmManager + Exact-Alarm-Fallback
- Boot-/Package-Recovery
- Foreground Services getrennt für Kamera und Datensynchronisation

WICHTIG Android 14–16:
Die Kamera-FGS-Berechtigung ist while-in-use eingeschränkt. Android erlaubt nicht generell, einen Kamera-FGS aus BOOT_COMPLETED im Hintergrund zu starten. Deshalb stellt BootReceiver nur die Alarme wieder her; nach einem Reboot muss der Nutzer die App einmal öffnen bzw. den Kameradienst sichtbar starten, bevor die Kamera im Hintergrund zuverlässig weiterläuft. Das ist eine Plattformbeschränkung, keine App-Einstellung.

Für Android 12+ kann SCHEDULE_EXACT_ALARM erforderlich sein. Die App fällt ohne diesen Zugriff auf setAndAllowWhileIdle zurück.

Die Projektdateien sind als vollständiger Ausgangsstand gedacht, aber vor Produktion müssen sie auf dem Zielgerät gebaut und getestet werden, insbesondere Camera2-Hardwarekombinationen, LineageOS-Hintergrundlimits, SMB-Server-ACLs und TLS-Zertifikate.


## Build/JDK note

The project explicitly uses Java/Kotlin JVM target 17. This is intentional: Android Studio/Gradle may run on JDK 25, but Kotlin 2.2.10 does not need to emit JVM-25 bytecode for this Android app. The toolchain is therefore pinned to JDK 17 for compilation.

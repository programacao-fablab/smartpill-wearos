package com.example.smartpill_wearos.presentation.utils

import android.content.Context
import java.util.UUID

object DeviceUtils {
    private const val PREFS_NAME = "smartpill_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Tenta ler o ID salvo
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)

        // Se não existir, cria um novo e salva
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }

        return deviceId
    }
}
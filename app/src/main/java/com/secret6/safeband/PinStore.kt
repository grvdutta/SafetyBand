package com.secret6.safeband

import android.content.Context

class PinStore(context: Context) {
    private val prefs = context.getSharedPreferences("safeband_security", Context.MODE_PRIVATE)

    fun setPin(pin: String) = prefs.edit().putString("pin", pin).apply()
    fun hasPin(): Boolean = prefs.contains("pin")
    fun verifyPin(entered: String): Boolean = prefs.getString("pin", null) == entered
}
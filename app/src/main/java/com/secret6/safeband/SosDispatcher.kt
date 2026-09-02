package com.secret6.safeband

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SmsManager
import com.google.android.gms.location.LocationServices

class SosDispatcher(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun sendSosToContacts(contacts: List<String>, dangerPercentage: Int) {
        if (contacts.isEmpty()) return
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                val mapsLink = if (location != null)
                    "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                else "location unavailable"
                val confidenceText = if (dangerPercentage > 0) " (Confidence: $dangerPercentage%)" else ""
                sendSms(contacts, "EMERGENCY SOS: I need help.$confidenceText My location: $mapsLink")
            }
            .addOnFailureListener {
                sendSms(contacts, "EMERGENCY SOS: I need help. Location unavailable.")
            }
    }

    private fun sendSms(contacts: List<String>, message: String) {
        val smsManager = context.getSystemService(SmsManager::class.java)
        contacts.forEach { number ->
            try {
                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(number, null, parts, null, null)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
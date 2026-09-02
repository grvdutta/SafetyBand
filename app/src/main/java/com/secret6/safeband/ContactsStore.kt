package com.secret6.safeband

import android.content.Context

class ContactsStore(context: Context) {
    private val prefs = context.getSharedPreferences("safeband_contacts", Context.MODE_PRIVATE)

    fun getContacts(): List<String> =
        prefs.getString("contacts", "")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    fun addContact(phoneNumber: String) {
        val normalized = normalizeNumber(phoneNumber)
        val updated = getContacts().toMutableList().apply { add(normalized) }
        prefs.edit().putString("contacts", updated.joinToString(",")).apply()
    }

    private fun normalizeNumber(raw: String): String {
        val digitsOnly = raw.filter { it.isDigit() || it == '+' }
        return when {
            digitsOnly.startsWith("+") -> digitsOnly
            digitsOnly.length == 10 -> "+91$digitsOnly"   // assumes Indian numbers by default
            else -> digitsOnly
        }
    }

    fun removeContact(phoneNumber: String) {
        val updated = getContacts().toMutableList().apply { remove(phoneNumber) }
        prefs.edit().putString("contacts", updated.joinToString(",")).apply()
    }
}
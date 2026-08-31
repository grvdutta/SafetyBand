package com.secret6.safeband.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContactsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Emergency Contacts", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("No contacts added yet.") // TODO: Room-backed list + add-contact form
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
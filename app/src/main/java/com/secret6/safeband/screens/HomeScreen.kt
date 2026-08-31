package com.secret6.safeband.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    bandConnected: Boolean,
    onSimulateDanger: () -> Unit,
    onOpenContacts: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (bandConnected) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
            contentDescription = null,
            tint = if (bandConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (bandConnected) "Band Connected" else "Band Not Connected",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(48.dp))
        Text(
            text = "You're safe. The band is monitoring for possible danger.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(48.dp))

        // Stand-in for the real BLE signal until hardware is ready
        Button(
            onClick = onSimulateDanger,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Simulate Danger Signal (test)")
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onOpenContacts) {
            Text("Emergency Contacts")
        }
    }
}
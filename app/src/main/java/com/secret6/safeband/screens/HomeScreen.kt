package com.secret6.safeband

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    discoveredDevices: List<BluetoothDevice>,
    onStartScan: () -> Unit,
    onConnect: (BluetoothDevice) -> Unit,
    onSimulateDanger: () -> Unit,
    onOpenContacts: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = if (bandConnected) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
            contentDescription = null,
            tint = if (bandConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (bandConnected) "Band Connected" else "Band Not Connected",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        if (!bandConnected) {
            Button(onClick = onStartScan, modifier = Modifier.fillMaxWidth()) {
                Text("Scan for Band")
            }
            Spacer(Modifier.height(12.dp))

            if (discoveredDevices.isNotEmpty()) {
                Text("Nearby devices — tap to connect:", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f, fill = false).fillMaxWidth()) {
                    items(discoveredDevices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onConnect(device) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = device.name ?: "Unknown device",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = device.address,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "You're safe. The band is monitoring for possible danger.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        // Manual alert button — always available, independent of BLE connection
        Button(
            onClick = onSimulateDanger,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Manual SOS Alert", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenContacts, modifier = Modifier.fillMaxWidth()) {
            Text("Emergency Contacts & PIN Settings")
        }
        Spacer(Modifier.height(16.dp))
    }
}
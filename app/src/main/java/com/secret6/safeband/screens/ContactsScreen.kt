package com.secret6.safeband.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ContactsScreen(
    contacts: List<String>,
    onAddContact: (String) -> Unit,
    onRemoveContact: (String) -> Unit,
    onSetPin: (String) -> Unit,
    onBack: () -> Unit
) {
    var newNumber by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinSaved by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Emergency Contacts", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = newNumber,
                onValueChange = { newNumber = it },
                label = { Text("Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (newNumber.isNotBlank()) {
                    onAddContact(newNumber.trim())
                    newNumber = ""
                }
            }) { Text("Add") }
        }

        Spacer(Modifier.height(16.dp))

        if (contacts.isEmpty()) {
            Text("No contacts added yet.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { number ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(number, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { onRemoveContact(number) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                    Divider()
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        Text("Cancellation PIN", style = MaterialTheme.typography.titleMedium)
        Text(
            "Set a PIN that must be entered to cancel an SOS alert once triggered.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = newPin,
                onValueChange = { newPin = it; pinSaved = false },
                label = { Text("New PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (newPin.length >= 4) {
                    onSetPin(newPin)
                    pinSaved = true
                    newPin = ""
                }
            }) { Text("Save") }
        }
        if (pinSaved) {
            Text("PIN saved.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
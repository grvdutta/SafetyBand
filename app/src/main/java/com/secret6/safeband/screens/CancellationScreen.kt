package com.secret6.safeband.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CancellationScreen(
    secondsRemaining: Int,
    onAttemptCancel: (String) -> Boolean
) {
    var showPinEntry by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Possible danger detected", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(32.dp))
        Text("$secondsRemaining", fontSize = 96.sp, fontWeight = FontWeight.Bold)
        Text("seconds until SOS is sent")
        Spacer(Modifier.height(48.dp))

        if (!showPinEntry) {
            Button(onClick = { showPinEntry = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("I'm Safe — Enter PIN to Cancel")
            }
        } else {
            OutlinedTextField(
                value = pinInput,
                onValueChange = { pinInput = it; pinError = false },
                label = { Text("Enter PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                isError = pinError,
                modifier = Modifier.fillMaxWidth()
            )
            if (pinError) Text("Incorrect PIN", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (!onAttemptCancel(pinInput)) { pinError = true; pinInput = "" }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Confirm") }
        }
    }
}
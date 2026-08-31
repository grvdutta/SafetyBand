package com.secret6.safeband.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CancellationScreen(
    secondsRemaining: Int,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Possible danger detected",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = "$secondsRemaining",
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold
        )
        Text("seconds until SOS is sent")
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("I'm Safe — Cancel Alert", style = MaterialTheme.typography.titleMedium)
        }
    }
}
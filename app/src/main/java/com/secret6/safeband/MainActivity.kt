package com.secret6.safeband

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secret6.safeband.screens.*
import com.secret6.safeband.ui.theme.SafeBandTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = mutableListOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())

        setContent {
            SafeBandTheme {
                SafeBandApp()
            }
        }
    }
}

@Composable
fun SafeBandApp(viewModel: AlertViewModel = viewModel()) {
    val navController = rememberNavController()
    val alertState by viewModel.alertState.collectAsState()
    val bandConnected by viewModel.bandConnected.collectAsState()
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()

    LaunchedEffect(alertState) {
        when (alertState) {
            is AlertState.Idle -> navController.navigate("home") { popUpTo(0) }
            is AlertState.CancellationWindow -> navController.navigate("cancellation")
            is AlertState.SosSent -> navController.navigate("sos_sent")
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                bandConnected = bandConnected,
                discoveredDevices = discoveredDevices,
                onStartScan = { viewModel.startBleScan() },
                onConnect = { device -> viewModel.connectToDevice(device) },
                onSimulateDanger = { viewModel.onDangerDetected() },
                onOpenContacts = { navController.navigate("contacts") }
            )
        }
        composable("cancellation") {
            CancellationScreen(
                secondsRemaining = secondsRemaining,
                onAttemptCancel = { pin -> viewModel.attemptCancelWithPin(pin) }
            )
        }
        composable("sos_sent") {
            SosSentScreen(onDismiss = { viewModel.resetToIdle() })
        }
        composable("contacts") {
            ContactsScreen(
                contacts = viewModel.getContacts(),
                onAddContact = { number -> viewModel.addContact(number) },
                onRemoveContact = { number -> viewModel.removeContact(number) },
                onSetPin = { pin -> viewModel.setPin(pin) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
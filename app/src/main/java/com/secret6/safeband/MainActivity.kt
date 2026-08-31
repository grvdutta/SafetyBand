package com.secret6.safeband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secret6.safeband.screens.*
import com.secret6.safeband.ui.theme.SafeBandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    // Navigate automatically whenever the state machine changes
    LaunchedEffect(alertState) {
        when (alertState) {
            is AlertState.Idle -> navController.navigate("home") {
                popUpTo(0)
            }
            is AlertState.CancellationWindow -> navController.navigate("cancellation")
            is AlertState.SosSent -> navController.navigate("sos_sent")
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                bandConnected = bandConnected,
                onSimulateDanger = { viewModel.onDangerDetected() },
                onOpenContacts = { navController.navigate("contacts") }
            )
        }
        composable("cancellation") {
            CancellationScreen(
                secondsRemaining = secondsRemaining,
                onCancel = { viewModel.cancelAlert() }
            )
        }
        composable("sos_sent") {
            SosSentScreen(onDismiss = { viewModel.resetToIdle() })
        }
        composable("contacts") {
            ContactsScreen(onBack = { navController.popBackStack() })
        }
    }
}
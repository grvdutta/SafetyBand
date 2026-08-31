package com.secret6.safeband

sealed class AlertState {
    object Idle : AlertState()
    object CancellationWindow : AlertState()
    object SosSent : AlertState()
}
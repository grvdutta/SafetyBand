package com.secret6.safeband

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class AlertViewModel : ViewModel() {

    private val _alertState = MutableStateFlow<AlertState>(AlertState.Idle)
    val alertState: StateFlow<AlertState> = _alertState.asStateFlow()

    private val _bandConnected = MutableStateFlow(false)
    val bandConnected: StateFlow<Boolean> = _bandConnected.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(30)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private var countdownJob: kotlinx.coroutines.Job? = null

    // Call this from your BLE layer later when the band signals danger.
    // For now, your "Simulate Danger" button calls it directly.
    fun onDangerDetected() {
        if (_alertState.value != AlertState.Idle) return
        _alertState.value = AlertState.CancellationWindow
        startCountdown()
    }

    private fun startCountdown() {
        _secondsRemaining.value = 30
        countdownJob = viewModelScope.launch {
            while (_secondsRemaining.value > 0) {
                delay(1000)
                _secondsRemaining.value -= 1
            }
            // Countdown hit zero without being cancelled -> send SOS
            triggerSos()
        }
    }

    fun cancelAlert() {
        countdownJob?.cancel()
        _alertState.value = AlertState.Idle
    }

    private fun triggerSos() {
        _alertState.value = AlertState.SosSent
        // TODO: fetch GPS location and POST to backend here (Step 5 of the roadmap)
    }

    fun resetToIdle() {
        countdownJob?.cancel()
        _alertState.value = AlertState.Idle
    }

    fun setBandConnected(connected: Boolean) {
        _bandConnected.value = connected
    }
}
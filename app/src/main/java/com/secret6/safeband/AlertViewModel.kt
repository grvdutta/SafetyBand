package com.secret6.safeband

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val _alertState = MutableStateFlow<AlertState>(AlertState.Idle)
    val alertState: StateFlow<AlertState> = _alertState.asStateFlow()

    private val _bandConnected = MutableStateFlow(false)
    val bandConnected: StateFlow<Boolean> = _bandConnected.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(30)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    private val _lastDangerPercentage = MutableStateFlow(0f)
    val lastDangerPercentage: StateFlow<Float> = _lastDangerPercentage.asStateFlow()

    private val DANGER_THRESHOLD = 20.0f //danger value

    private var countdownJob: kotlinx.coroutines.Job? = null
    private val pinStore = PinStore(application)
    private val contactsStore = ContactsStore(application)
    private val sosDispatcher = SosDispatcher(application)

    private val bleManager = BleManager(
        context = application,
        onDangerSignalReceived = { percentage ->
            _lastDangerPercentage.value = percentage
            if (percentage > DANGER_THRESHOLD) {
                onDangerDetected()
            }
        },
        onConnectionStateChange = { connected -> _bandConnected.value = connected },
        onDeviceFound = { device ->
            val current = _discoveredDevices.value
            if (current.none { it.address == device.address }) {
                _discoveredDevices.value = current + device
            }
        }
    )

    fun startBleScan() = bleManager.startScan()
    fun stopBleScan() = bleManager.stopScan()
    fun connectToDevice(device: BluetoothDevice) = bleManager.connect(device)

    // Manual button also calls this directly — percentage stays 0 (no ML confidence behind a manual press)
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
            triggerSos()
        }
    }

    fun attemptCancelWithPin(enteredPin: String): Boolean {
        if (!pinStore.hasPin()) return true
        return if (pinStore.verifyPin(enteredPin)) {
            countdownJob?.cancel()
            _alertState.value = AlertState.Idle
            true
        } else false
    }

    fun setPin(newPin: String) = pinStore.setPin(newPin)
    fun addContact(number: String) = contactsStore.addContact(number)
    fun removeContact(number: String) = contactsStore.removeContact(number)
    fun getContacts(): List<String> = contactsStore.getContacts()

    private fun triggerSos() {
        _alertState.value = AlertState.SosSent
        sosDispatcher.sendSosToContacts(contactsStore.getContacts(), _lastDangerPercentage.value)
    }

    fun resetToIdle() {
        countdownJob?.cancel()
        _alertState.value = AlertState.Idle
        _lastDangerPercentage.value = 0f
    }
}
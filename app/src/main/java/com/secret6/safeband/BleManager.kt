package com.secret6.safeband

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.util.UUID

class BleManager(
    private val context: Context,
    private val onDangerSignalReceived: (percentage: Float) -> Unit,   // now Float, not Int
    private val onConnectionStateChange: (Boolean) -> Unit,
    private val onDeviceFound: (BluetoothDevice) -> Unit
) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private var bluetoothGatt: BluetoothGatt? = null
    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    // TODO: replace with your real agreed-upon UUIDs from your hardware teammate
    private val DANGER_SERVICE_UUID = UUID.fromString("0000181c-0000-1000-8000-00805f9b34fb")
    private val DANGER_CHARACTERISTIC_UUID = UUID.fromString("00002a6e-0000-1000-8000-00805f9b34fb")

    @SuppressLint("MissingPermission")
    fun startScan() {
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(DANGER_SERVICE_UUID))
            .build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        scanner?.startScan(listOf(scanFilter), settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("BleManager", "Found: ${result.device.name ?: "Unknown"} (${result.device.address})")
            onDeviceFound(result.device)
        }
        override fun onScanFailed(errorCode: Int) {
            Log.e("BleManager", "Scan failed: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        stopScan()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                onConnectionStateChange(true)
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                onConnectionStateChange(false)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val characteristic = gatt.getService(DANGER_SERVICE_UUID)
                ?.getCharacteristic(DANGER_CHARACTERISTIC_UUID)
            characteristic?.let { gatt.setCharacteristicNotification(it, true) }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == DANGER_CHARACTERISTIC_UUID) {
                val value = characteristic.value
                if (value != null && value.size >= 4) {
                    val buffer = java.nio.ByteBuffer.wrap(value).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    val percentage = buffer.getFloat()
                    onDangerSignalReceived(percentage)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
    }
}
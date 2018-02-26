package kr.or.payot.poin.Bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface BluetoothController {
    fun connect(bluetoothDevice: BluetoothDevice)

    fun sendMessage(byteArray: ByteArray)

    fun findServiceAndChar(bluetoothGatt: BluetoothGatt): Pair<BluetoothGattService, BluetoothGattCharacteristic>?

    fun disconnect()
}
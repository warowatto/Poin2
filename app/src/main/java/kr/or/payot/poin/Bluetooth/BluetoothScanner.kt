package kr.or.payot.poin.Bluetooth

import android.bluetooth.BluetoothDevice
import io.reactivex.Observable

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface BluetoothScanner {

    fun scanDevice(macAddress: String, timer: Long): Observable<BluetoothDevice>

    fun stopScan()
}
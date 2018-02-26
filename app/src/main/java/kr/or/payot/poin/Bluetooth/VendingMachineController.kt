package kr.or.payot.poin.Bluetooth

import android.bluetooth.BluetoothDevice

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface VendingMachineController {

    fun connect(device: BluetoothDevice)

    fun start(id: String)

    fun insertCoin(coin: Int)

    fun finish()
}
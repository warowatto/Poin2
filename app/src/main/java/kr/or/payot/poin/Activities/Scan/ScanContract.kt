package kr.or.payot.poin.Activities.Scan

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.bluetooth.BluetoothDevice
import kr.or.payot.poin.RESTFul.MachineResponse

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface ScanContract {

    interface View {
        fun showProgress()

        fun hideProgress()

        fun findDevice(data: MachineResponse, device: BluetoothDevice)

        fun notSupportServiceDevice()

        fun notFoundDevice()

        fun serverError()

        fun needCard()

        fun enableBluetooth()

        fun scanMode()

        fun scanModeDisable()
    }

    interface Presenter : LifecycleObserver {
        fun checkState()

        fun startScan(macAddress: String)

        fun stopScan()
    }
}
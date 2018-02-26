package kr.or.payot.poin.Service

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.cantrowitz.rxbroadcast.RxBroadcast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kr.or.payot.poin.App
import kr.or.payot.poin.Bluetooth.MessageBuilder
import kr.or.payot.poin.Bluetooth.VendingMachineController
import kr.or.payot.poin.DI.Component.DaggerServiceComponent
import kr.or.payot.poin.DI.Modules.BluetoothModule
import kr.or.payot.poin.IVendingMachineService
import kr.or.payot.poin.R
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 16..
 */
class PoinDeviceService : Service() {

    @Inject
    lateinit var vendingMachineController: VendingMachineController

    override fun onCreate() {
        super.onCreate()

        DaggerServiceComponent.builder()
                .bluetoothModule(BluetoothModule(this))
                .build().inject(this)

        RxBroadcast.fromBroadcast(this, IntentFilter(getString(R.string.boradcast_bluetooth_name)))
                .filter { it.extras.getString("type") == "status" }
                .map { it.extras.getInt("message") }
                .filter { it == 4 }.singleOrError()
                .map { App.user?.id }
                .map { it.toString() }
                .subscribe(
                        {
                            if (it != null) vendingMachineController.start(it)
                        },
                        {
                            it.printStackTrace()
                        })
    }

    override fun onBind(p0: Intent?): IBinder = PoinServiceBinder()

    inner class PoinServiceBinder : IVendingMachineService.Stub() {
        override fun connect(device: BluetoothDevice?) {
            vendingMachineController.connect(device!!)
        }

        override fun insertCoin(coin: Int) {
            vendingMachineController.insertCoin(coin)
        }

        override fun disconnect() {
            vendingMachineController.finish()
        }
    }
}
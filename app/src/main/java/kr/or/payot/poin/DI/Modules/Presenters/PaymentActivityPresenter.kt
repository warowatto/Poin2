package kr.or.payot.poin.DI.Modules.Presenters

import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.cantrowitz.rxbroadcast.RxBroadcast
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kr.or.payot.poin.Activities.Payment.PaymentActivity
import kr.or.payot.poin.Activities.Payment.PaymentContract
import kr.or.payot.poin.DI.Modules.BluetoothModule
import kr.or.payot.poin.DI.Modules.NetworkModule
import kr.or.payot.poin.DI.PerActivity
import kr.or.payot.poin.IVendingMachineService
import kr.or.payot.poin.R
import kr.or.payot.poin.Service.PoinDeviceService
import kr.or.payot.poin.Utils.observeOnMainThread
import kotlin.math.log

/**
 * Created by yongheekim on 2018. 2. 16..
 */

@Module(includes = arrayOf(BluetoothModule::class, NetworkModule::class))
class PaymentActivityPresenter(val activity: PaymentActivity, val view: PaymentContract.View) {

    @PerActivity
    @Provides
    fun presenter(): PaymentContract.Presenter = object : PaymentContract.Presenter {
        val dispose = CompositeDisposable()

        var machineController: IVendingMachineService? = null

        val serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {
                machineController = null
            }

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                machineController = IVendingMachineService.Stub.asInterface(binder)
                val machine = activity.intent.getParcelableExtra<BluetoothDevice>("device")
                view.connect()
                machineController?.connect(machine)
            }

        }

        override fun pay(machineId: Int, productId: Int, cardId: Int, eventId: Int?) {
            if (machineController == null) {

            } else {
                view.payLoading()
                machineController?.insertCoin(4000)
            }
        }

        override fun disconnect() {
            machineController?.disconnect()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun initalize() {
            val broadCastName = "kr.or.poin.bluetooth"
            val bluetoothEventFilter = IntentFilter(broadCastName)
            val bluetoothRecive = RxBroadcast.fromBroadcast(activity, bluetoothEventFilter)
                    .doOnNext { println(it.extras.get("response")) }
                    .share()

            bluetoothRecive.filter { it.extras.getString("type") == "status" }
                    .map { it.extras.getInt("message") }
                    .observeOnMainThread()
                    .subscribe {
                        Log.d("메시지 받음", it.toString())
                        when (it) {
                            BluetoothProfile.STATE_CONNECTED -> { }
                            BluetoothProfile.STATE_DISCONNECTED -> view.disconnected()
                            4 -> view.ready()
                        }
                    }.addTo(dispose)

            bluetoothRecive.filter { it.extras.getString("type") == "response" }
                    .map { it.extras.getStringArray("message") }
                    .doOnNext { view.payLoadingFinish() }
                    .observeOnMainThread()
                    .subscribe {
                        if (it[2] == "2") {
                            view.payLoadingFinish()
                        }
                    }.addTo(dispose)

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun serviceBind() {
             activity.bindService(Intent(activity, PoinDeviceService::class.java), serviceConnection, Service.BIND_AUTO_CREATE)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            activity.unbindService(serviceConnection)
            dispose.dispose()
        }

    }
}
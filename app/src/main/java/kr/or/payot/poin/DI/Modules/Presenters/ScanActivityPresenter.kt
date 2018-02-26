package kr.or.payot.poin.DI.Modules.Presenters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.cantrowitz.rxbroadcast.RxBroadcast
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import kr.or.payot.poin.Activities.Scan.ScanActivity
import kr.or.payot.poin.Activities.Scan.ScanContract
import kr.or.payot.poin.App
import kr.or.payot.poin.Bluetooth.BluetoothScanner
import kr.or.payot.poin.DI.PerActivity
import kr.or.payot.poin.RESTFul.MachineAPI
import kr.or.payot.poin.RESTFul.MachineResponse
import kr.or.payot.poin.Utils.observeOnMainThread
import retrofit2.HttpException

/**
 * Created by yongheekim on 2018. 2. 16..
 */

@Module
class ScanActivityPresenter(val activity: ScanActivity, val view: ScanContract.View) {

    @PerActivity
    @Provides
    fun persenter(api: MachineAPI, bluetoothScanner: BluetoothScanner): ScanContract.Presenter =
            object : ScanContract.Presenter {
                override fun checkState() {
                    val user = App.user!!

                    val hasCard = user.cards?.size ?: 0 != 0
                    val bluetoothState = BluetoothAdapter.getDefaultAdapter().isEnabled
                    if (!hasCard) {
                        view.needCard()
                        return
                    } else if (!bluetoothState) {
                        view.enableBluetooth()
                        return
                    } else {
                        view.scanMode()
                    }
                }

                val dispose = CompositeDisposable()

                override fun startScan(macAddress: String) {
                    // QR Code Text = AA:BB:CC:DD:EE:FF
                    // ServerRequest = AA:BB:CC:DD:EE:FF
                    // BluetoothDeviceName = AABBCCDDEEFF

                    // 서버로 부터 장치가 활성화 되었는지 확인
                    val requestServer = api.getMachine(macAddress)
                            .doOnSuccess { if (!it.isRunning) throw IllegalStateException("This machine is stopped service") }
                            .toObservable()

                    val scanningDeviceName = macAddress.replace(":", "")
                    val scanningDevice = bluetoothScanner.scanDevice(scanningDeviceName, 2000).take(1)

                    view.showProgress()
                    Observable.zip(requestServer, scanningDevice, BiFunction { data: MachineResponse, device: BluetoothDevice ->
                        return@BiFunction data to device
                    })
                            .observeOnMainThread()
                            .subscribe(
                                    {
                                        view.findDevice(it.first, it.second)
                                        view.hideProgress()
                                    },
                                    {
                                        view.hideProgress()
                                        it.printStackTrace()
                                        when (it) {
                                            is HttpException -> view.serverError()
                                            is IllegalStateException -> view.notSupportServiceDevice()
                                            is NullPointerException -> view.notFoundDevice()
                                        }
                                    })
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                fun onCreate() {
                    val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    RxBroadcast.fromBroadcast(activity, intentFilter)
                            .map { it.extras.getInt(BluetoothAdapter.EXTRA_STATE) }
                            .filter { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_OFF }
                            .map { it == BluetoothAdapter.STATE_ON }
                            .subscribe {
                                if (!it) view.enableBluetooth()
                                else checkState()
                            }.apply { dispose.add(this) }

                    val nowBluetoothState = BluetoothAdapter.getDefaultAdapter().isEnabled
                    if (!nowBluetoothState) view.enableBluetooth()
                    else checkState()
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                override fun stopScan() {
                    bluetoothScanner.stopScan()
                }

            }
}
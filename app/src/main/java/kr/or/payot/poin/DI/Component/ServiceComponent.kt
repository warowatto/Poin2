package kr.or.payot.poin.DI.Component

import dagger.Component
import kr.or.payot.poin.DI.Modules.BluetoothModule
import kr.or.payot.poin.DI.PerService
import kr.or.payot.poin.Service.PoinDeviceService

/**
 * Created by yongheekim on 2018. 2. 21..
 */

@PerService
@Component(modules = arrayOf(BluetoothModule::class))
interface ServiceComponent {

    fun inject(service: PoinDeviceService)
}
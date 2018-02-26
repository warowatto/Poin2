package kr.or.payot.poin.DI.Component.Presenters

import dagger.Component
import kr.or.payot.poin.Activities.Scan.ScanActivity
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Modules.BluetoothModule
import kr.or.payot.poin.DI.Modules.Presenters.ScanActivityPresenter
import kr.or.payot.poin.DI.PerActivity

/**
 * Created by yongheekim on 2018. 2. 16..
 */

@PerActivity
@Component(modules = arrayOf(BluetoothModule::class, ScanActivityPresenter::class),
        dependencies = arrayOf(ApplicationComponent::class))
interface ScanComponent {

    fun inject(activity: ScanActivity)
}
package kr.or.payot.poin.DI.Component.Presenters

import dagger.Component
import kr.or.payot.poin.Activities.Payment.PaymentActivity
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Modules.Presenters.PaymentActivityPresenter
import kr.or.payot.poin.DI.PerActivity

/**
 * Created by yongheekim on 2018. 2. 19..
 */

@PerActivity
@Component(modules = arrayOf(PaymentActivityPresenter::class),
        dependencies = arrayOf(ApplicationComponent::class))
interface PaymentComponent {

    fun inject(activity: PaymentActivity)
}
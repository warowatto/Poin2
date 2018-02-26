package kr.or.payot.poin.DI.Component.Presenters

import dagger.Component
import kr.or.payot.poin.Activities.CardAdd.CardAddActivity
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Modules.Presenters.CardAddActivityPresenter
import kr.or.payot.poin.DI.PerActivity

/**
 * Created by yongheekim on 2018. 2. 26..
 */

@PerActivity
@Component(modules = arrayOf(CardAddActivityPresenter::class),
        dependencies = arrayOf(ApplicationComponent::class))
interface CardAddComponent {
    fun inject(activity: CardAddActivity)
}
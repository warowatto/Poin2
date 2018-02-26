package kr.or.payot.poin.DI.Component.Presenters

import dagger.Component
import kr.or.payot.poin.Activities.UserPage.UserActivity
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Modules.Presenters.UserActivityPresenter
import kr.or.payot.poin.DI.PerActivity

/**
 * Created by yongheekim on 2018. 2. 22..
 */

@PerActivity
@Component(modules = arrayOf(UserActivityPresenter::class), dependencies = arrayOf(ApplicationComponent::class))
interface UserComponent {

    fun inject(activity: UserActivity)
}
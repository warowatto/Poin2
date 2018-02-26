package kr.or.payot.poin.DI.Component.Presenters

import dagger.Component
import kr.or.payot.poin.Activities.Login.LoginActivity
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Modules.Presenters.LoginActivityPresenter
import kr.or.payot.poin.DI.PerActivity

/**
 * Created by yongheekim on 2018. 2. 16..
 */

@PerActivity
@Component(modules = arrayOf(LoginActivityPresenter::class),
        dependencies = arrayOf(ApplicationComponent::class))
interface LoginComponent {

    fun inject(activity: LoginActivity)
}
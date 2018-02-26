package kr.or.payot.poin.DI.Component.Presenters

import dagger.Component
import kr.or.payot.poin.Activities.SignUp.SignUpActivity
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Modules.Presenters.SignupActivityPresenter
import kr.or.payot.poin.DI.PerActivity

/**
 * Created by yongheekim on 2018. 2. 26..
 */

@PerActivity
@Component(modules = arrayOf(SignupActivityPresenter::class),
        dependencies = arrayOf(ApplicationComponent::class))
interface SignupComponent {
    fun inject(activity: SignUpActivity)
}
package kr.or.payot.poin.Activities.Login

import android.arch.lifecycle.LifecycleObserver

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface LoginContract {

    interface View {

        fun showProgress()

        fun hideProgress()

        fun loginError(message: String)

        fun signupPage()

        fun mainPage()
    }

    interface Presenter : LifecycleObserver {

    }
}
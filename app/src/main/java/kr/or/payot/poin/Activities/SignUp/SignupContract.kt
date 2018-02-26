package kr.or.payot.poin.Activities.SignUp

import kr.or.payot.poin.RESTFul.Data.User
import retrofit2.HttpException

/**
 * Created by yongheekim on 2018. 2. 26..
 */
interface SignupContract {

    interface View {
        fun showProgress()

        fun endProgress()

        fun signOK(user: User)

        fun serverError(throwable: HttpException)

        fun error(throwable: Throwable)
    }

    interface Presenter {
        fun signup(name: String, gender: String)
    }
}
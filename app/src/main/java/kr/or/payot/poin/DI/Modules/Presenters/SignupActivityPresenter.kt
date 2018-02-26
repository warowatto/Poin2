package kr.or.payot.poin.DI.Modules.Presenters

import com.kakao.auth.Session
import com.kakao.usermgmt.response.model.UserProfile
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import kr.or.payot.poin.Activities.SignUp.SignUpActivity
import kr.or.payot.poin.Activities.SignUp.SignupContract
import kr.or.payot.poin.DI.PerActivity
import kr.or.payot.poin.RESTFul.UserAPI
import kr.or.payot.poin.Utils.observeOnMainThread
import retrofit2.HttpException

/**
 * Created by yongheekim on 2018. 2. 26..
 */

@Module
class SignupActivityPresenter(val activity: SignUpActivity, val view: SignupContract.View) {

    @PerActivity
    @Provides
    fun presenter(api: UserAPI, userProfile: Single<UserProfile>): SignupContract.Presenter =
            object : SignupContract.Presenter {
                override fun signup(name: String, gender: String) {
                    val token = Session.getCurrentSession().tokenInfo.accessToken

                    view.showProgress()
                    userProfile.map {
                        return@map mapOf<String, String>("email" to it.email,
                                "name" to name,
                                "gender" to gender,
                                "profileImage" to it.profileImagePath,
                                "thumbnailImage" to it.thumbnailImagePath)
                    }.flatMap {
                        api.signup(
                                "kakao", token,
                                it["email"], it["name"], it["gender"], it["profileImage"], it["thumbnailImage"])
                    }.observeOnMainThread()
                            .doOnEvent { user, throwable -> view.endProgress() }
                            .subscribe(
                                    { view.signOK(it) },
                                    {
                                        when (it) {
                                            is HttpException -> view.serverError(it)
                                            else -> view.error(it)
                                        }
                                    })
                }

            }
}
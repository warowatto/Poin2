package kr.or.payot.poin.DI.Modules.Presenters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.support.annotation.StringRes
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import dagger.Module
import dagger.Provides
import kr.or.payot.poin.Activities.Login.LoginActivity
import kr.or.payot.poin.Activities.Login.LoginContract
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.PerActivity
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.UserAPI
import kr.or.payot.poin.Utils.convert
import kr.or.payot.poin.Utils.observeOnMainThread
import retrofit2.HttpException

/**
 * Created by yongheekim on 2018. 2. 16..
 */

@Module
class LoginActivityPresenter(val activity: LoginActivity, val view: LoginContract.View) {

    @PerActivity
    @Provides
    fun sessionCallback(api: UserAPI): ISessionCallback = object : ISessionCallback {

        override fun onSessionOpenFailed(exception: KakaoException?) {
            // 카카오 토큰 발행 오류
            val errorMessage = when (exception?.errorType) {
            // 카카오톡 미설치시
                KakaoException.ErrorType.KAKAOTALK_NOT_INSTALLED -> getString(R.string.error_kakao_not_install)
            // 카카오 인증 오류시
                KakaoException.ErrorType.AUTHORIZATION_FAILED -> getString(R.string.error_auth)
                else -> getString(R.string.error_message)
            }
            view.loginError(errorMessage)
        }

        override fun onSessionOpened() {
            // 카카오 토큰 발행 성공
            // 토큰 발행에 성공하였다면
            // Poin Server의 기존 사용자인지 신규유저인지 확인한다
            val token = Session.getCurrentSession().tokenInfo.accessToken
            view.showProgress()
            api.login("kakao", token)
                    .observeOnMainThread()
                    .doOnDispose { view.hideProgress() }
                    .subscribe(
                            {
                                App.user = it
                                // 회원이 등록되어 있는 상태라면 메인페이지로 이동
                                view.mainPage()
                            },
                            {
                                when (it) {
                                    is HttpException -> {
                                        if (it.code() == 403) {
                                            view.signupPage()
                                        } else {
                                            poinServerError(it)
                                        }
                                    }
                                    else -> view.loginError(getString(R.string.error_message))
                                }
                            })
        }

        fun poinServerError(exception: HttpException) =
                if (exception.code() == 404) {
                    // Poin 회원등록이 안되어있을 경우
                    view.signupPage()
                } else {
                    // Poin 서버에 오류가 있을 경우
                    val message = exception.convert().get("message") as String
                    view.loginError(message)
                }

        fun getString(@StringRes id: Int): String = activity.getString(id)
    }

    @PerActivity
    @Provides
    fun presenter(sessionCallback: ISessionCallback): LoginContract.Presenter = object : LoginContract.Presenter {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun initKako() {
            // test
            Session.getCurrentSession().addCallback(sessionCallback)
            Session.getCurrentSession().checkAndImplicitOpen()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun removeCallback() {
            Session.getCurrentSession().removeCallback(sessionCallback)
        }
    }
}
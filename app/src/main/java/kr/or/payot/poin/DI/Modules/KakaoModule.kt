package kr.or.payot.poin.DI.Modules

import android.content.Context
import com.kakao.auth.*
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by yongheekim on 2018. 2. 16..
 */

@Module
class KakaoModule {

    @Singleton
    @Provides
    fun iApplicationConfig(@Named("AppContext") context: Context): IApplicationConfig = object : IApplicationConfig {
        override fun getApplicationContext(): Context = context
    }

    @Singleton
    @Provides
    fun sessionConfig(): ISessionConfig = object : ISessionConfig {
        override fun isSaveFormData(): Boolean = true

        override fun getAuthTypes(): Array<AuthType> = arrayOf(
                AuthType.KAKAO_LOGIN_ALL
        )

        override fun isSecureMode(): Boolean = false

        override fun getApprovalType(): ApprovalType = ApprovalType.INDIVIDUAL

        override fun isUsingWebviewTimer(): Boolean = false
    }

    @Singleton
    @Provides
    fun adapter(iApplicationConfig: IApplicationConfig, sessionConfig: ISessionConfig): KakaoAdapter = object : KakaoAdapter() {
        override fun getApplicationConfig(): IApplicationConfig = iApplicationConfig

        override fun getSessionConfig(): ISessionConfig = sessionConfig
    }

    @Singleton
    @Provides
    fun userInfo(): Single<UserProfile> = Single.create { emitter ->
        UserManagement.getInstance().requestMe(object : MeResponseCallback() {
            override fun onSuccess(result: UserProfile?) {
                result?.let { emitter.onSuccess(it) }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                errorResult?.exception?.let { emitter.onError(it) }
            }

            override fun onNotSignedUp() {
                emitter.onError(NullPointerException("카카오 계정으로 가입되지 않은 회원입니다"))
            }

        })
    }
}
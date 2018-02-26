package kr.or.payot.poin.DI.Component

import android.content.Context
import android.content.SharedPreferences
import com.kakao.auth.KakaoAdapter
import com.kakao.usermgmt.response.model.UserProfile
import dagger.Component
import io.reactivex.Single
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Modules.ApplicationModule
import kr.or.payot.poin.DI.Modules.KakaoModule
import kr.or.payot.poin.DI.Modules.NetworkModule
import kr.or.payot.poin.RESTFul.MachineAPI
import kr.or.payot.poin.RESTFul.UserAPI
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by yongheekim on 2018. 2. 13..
 */

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, NetworkModule::class, KakaoModule::class))
interface ApplicationComponent {

    fun inject(app: App)

    @Named("AppContext")
    fun context(): Context

    fun sharedPreference(): SharedPreferences

    fun machineApi(): MachineAPI

    fun userApi(): UserAPI

    fun kakaoUserProfile(): Single<UserProfile>
}
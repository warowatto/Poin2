package kr.or.payot.poin

import android.app.Application
import com.kakao.auth.KakaoAdapter
import com.kakao.auth.KakaoSDK
import kr.or.payot.poin.DI.Component.ApplicationComponent
import kr.or.payot.poin.DI.Component.DaggerApplicationComponent
import kr.or.payot.poin.DI.Modules.ApplicationModule
import kr.or.payot.poin.RESTFul.Data.User
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 13..
 */
class App : Application() {

    companion object {
        lateinit var component: ApplicationComponent
        var user: User? = null
    }

    @Inject
    lateinit var kakaoAdapter: KakaoAdapter

    override fun onCreate() {
        super.onCreate()

        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()

        component.inject(this)

        KakaoSDK.init(kakaoAdapter)
    }
}
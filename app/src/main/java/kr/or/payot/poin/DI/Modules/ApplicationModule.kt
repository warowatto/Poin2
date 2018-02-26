package kr.or.payot.poin.DI.Modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import kr.or.payot.poin.App
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by yongheekim on 2018. 2. 13..
 */
@Module
class ApplicationModule(val app: App) {

    @Singleton
    @Named("AppContext")
    @Provides
    fun context(): Context = app.applicationContext

    @Singleton
    @Provides
    fun sharedPreference(): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(app)
}
package kr.or.payot.poin.DI.Modules

import com.burgstaller.okhttp.AuthenticationCacheInterceptor
import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import kr.or.payot.poin.RESTFul.MachineAPI
import kr.or.payot.poin.RESTFul.UserAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by yongheekim on 2018. 2. 13..
 */

@Module
class NetworkModule {

    private val id = "mobile"
    private val pass = "18d523a04ec5"

    private val HOST = "https://api.payot-poin.com/"

    @Singleton
    @Provides
    fun gson():Gson = GsonBuilder()
            .setDateFormat(DateFormat.FULL)
            .create()

    @Singleton
    @Provides
    fun authenticator(): DigestAuthenticator =
            DigestAuthenticator(
                    Credentials(id, pass))

    @Singleton
    @Provides
    fun okhttp(digestAuthenticator: DigestAuthenticator): OkHttpClient =
            OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .authenticator(digestAuthenticator)
                    .addInterceptor(AuthenticationCacheInterceptor(ConcurrentHashMap()))
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .build()

    @Singleton
    @Provides
    fun retrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
            Retrofit.Builder()
                    .baseUrl(HOST)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build()

    @Singleton
    @Provides
    fun userAPI(retrofit: Retrofit): UserAPI =
            retrofit.create(UserAPI::class.java)

    @Singleton
    @Provides
    fun machineAPI(retrofit: Retrofit): MachineAPI =
            retrofit.create(MachineAPI::class.java)
}
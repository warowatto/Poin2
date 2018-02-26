package kr.or.payot.poin.Utils

import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by yongheekim on 2018. 2. 16..
 */

fun <T> Observable<T>.observeOnMainThread(): Observable<T> =
        this.observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())

fun <T> Single<T>.observeOnMainThread(): Single<T> =
        this.observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
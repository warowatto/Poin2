package kr.or.payot.poin.Activities

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by yongheekim on 2018. 2. 13..
 */
open class BaseActivity : AppCompatActivity() {
    val dispose: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        dispose.dispose()
        super.onDestroy()
    }
}
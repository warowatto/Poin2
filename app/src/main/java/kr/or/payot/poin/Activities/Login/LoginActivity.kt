package kr.or.payot.poin.Activities.Login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kakao.auth.Session
import kotlinx.android.synthetic.main.activity_login.*
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.Activities.Scan.ScanActivity
import kr.or.payot.poin.Activities.SignUp.SignUpActivity
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Component.Presenters.DaggerLoginComponent
import kr.or.payot.poin.DI.Modules.Presenters.LoginActivityPresenter
import kr.or.payot.poin.R
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 13..
 */
class LoginActivity : BaseActivity(), LoginContract.View {

    @Inject
    lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        DaggerLoginComponent.builder()
                .applicationComponent(App.component)
                .loginActivityPresenter(LoginActivityPresenter(this, this))
                .build().inject(this)

        lifecycle.addObserver(presenter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showProgress() {
        loginProgressGroup.visibility = View.VISIBLE
        btnKakaoLogin.visibility = View.INVISIBLE

    }

    override fun hideProgress() {
        loginProgressGroup.visibility = View.INVISIBLE
        btnKakaoLogin.visibility = View.VISIBLE
    }

    override fun loginError(message: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.error_login)
                .setMessage(message)
                .setNeutralButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { finish() }
                .show()
    }

    override fun signupPage() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    override fun mainPage() {
        startActivity(Intent(this, ScanActivity::class.java))
        finish()
    }
}
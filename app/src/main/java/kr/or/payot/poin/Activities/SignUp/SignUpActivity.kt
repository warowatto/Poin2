package kr.or.payot.poin.Activities.SignUp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_signup.*
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.Activities.Scan.ScanActivity
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Component.Presenters.DaggerSignupComponent
import kr.or.payot.poin.DI.Modules.Presenters.SignupActivityPresenter
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.Data.User
import kr.or.payot.poin.Utils.convert
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class SignUpActivity : BaseActivity(), SignupContract.View {

    @Inject
    lateinit var presenter: SignupContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        DaggerSignupComponent.builder()
                .applicationComponent(App.component)
                .signupActivityPresenter(SignupActivityPresenter(this, this))
                .build().inject(this)

        btnSignup.setOnClickListener {
            if (editName.text.toString().isNullOrEmpty()) {
                editName.error = getString(R.string.error_form_name)
                return@setOnClickListener
            }

            val name = editName.text.toString()
            val gender = if (radioMan.isChecked) "M" else "F"
            presenter.signup(name, gender)
        }
    }

    override fun showProgress() {
        btnSignup.isEnabled = false
        progress.visibility = View.VISIBLE
    }

    override fun endProgress() {
        btnSignup.isEnabled = true
        progress.visibility = View.INVISIBLE
    }

    override fun signOK(user: User) {
        App.user = user
        startActivity(Intent(this, ScanActivity::class.java))
        finish()
    }

    override fun serverError(throwable: HttpException) {
        val message = throwable.convert()["message"] as String
        AlertDialog.Builder(this)
                .setTitle(R.string.error_message)
                .setMessage(message)
                .setNeutralButton(R.string.button_ok) { _, _ -> }
                .setOnDismissListener { finish() }
                .show()
    }

    override fun error(throwable: Throwable) {
        AlertDialog.Builder(this)
                .setMessage(R.string.error_message)
                .setNeutralButton(R.string.button_ok) { _, _ -> }
                .setOnDismissListener { finish() }
                .show()
    }
}
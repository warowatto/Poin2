package kr.or.payot.poin.Activities.CardAdd

import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_card.*
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Component.Presenters.DaggerCardAddComponent
import kr.or.payot.poin.DI.Modules.Presenters.CardAddActivityPresenter
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.Data.Card
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class CardAddActivity : BaseActivity(), CardAddContract.View {

    @Inject
    lateinit var presenter: CardAddContract.Presenter

    val progress: ProgressDialog by lazy {
        ProgressDialog(this@CardAddActivity).apply {
            setMessage(getString(R.string.dialog_progress_card_add))
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        DaggerCardAddComponent.builder()
                .applicationComponent(App.component)
                .cardAddActivityPresenter(CardAddActivityPresenter(this, this))
                .build().inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            val colorFilter = getDrawable(R.drawable.abc_ic_ab_back_material).apply {
                setColorFilter(resources.getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP)
            }

            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(colorFilter)
        }

        initView()
    }

    private fun initView() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        btnSubmit.setOnClickListener {
            val userId = App.user!!.id
            presenter.addCard(
                    userId,
                    editCardNumber.text.toString(),
                    editCardExpiry.text.toString(),
                    editBirth.text.toString(),
                    editPassword.text.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showProgress() {
        progress.show()
    }

    override fun hideProgress() {
        progress.dismiss()
    }

    override fun formError(message: String) {
        val rootView = window.decorView.rootView
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .show()
    }

    override fun addError(result: String) {
        val rootView = window.decorView.rootView
        Snackbar.make(rootView, result, Snackbar.LENGTH_LONG)
                .setAction(R.string.button_ok) { }
                .show()
    }

    override fun registCard(result: Card) {
        AlertDialog.Builder(this@CardAddActivity)
                .setTitle(R.string.dialog_title_card)
                .setMessage(getString(R.string.dialog_message_card, result.displayName, result.bankName))
                .setNeutralButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { finish() }
                .show()
    }
}
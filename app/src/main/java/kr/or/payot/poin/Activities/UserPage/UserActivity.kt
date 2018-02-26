package kr.or.payot.poin.Activities.UserPage

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_user.*
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.Activities.UserPage.View.UserPaymentList
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Component.Presenters.DaggerUserComponent
import kr.or.payot.poin.DI.Modules.Presenters.UserActivityPresenter
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.Data.Purchase
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class UserActivity : BaseActivity(), UserContract.View {

    @Inject
    lateinit var presenter: UserContract.Presenter

    val purchaseAdapter: UserPaymentList by lazy { UserPaymentList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        DaggerUserComponent.builder()
                .applicationComponent(App.component)
                .userActivityPresenter(UserActivityPresenter(this))
                .build().inject(this)

        initView()
        App.user?.id?.let { presenter.loadList(it) }
    }

    private fun initView() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        recyclerView.run {
            layoutManager = LinearLayoutManager(this@UserActivity)
            adapter = purchaseAdapter
        }

        btnRetry.setOnClickListener {
            App.user?.id?.let { presenter.loadList(it) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showList(data: List<Purchase>) {
        purchaseAdapter.clearItem()
        purchaseAdapter.addAllItem(data)
        purchaseAdapter.notifyDataSetChanged()
        recyclerView.visibility = View.VISIBLE
    }

    override fun showProgress() {
        progressView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyContentView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    override fun dismissProgress() {
        progressView.visibility = View.GONE
    }

    override fun emptyList() {
        emptyContentView.visibility = View.VISIBLE
    }

    override fun serverError() {
        errorView.visibility = View.VISIBLE
    }
}
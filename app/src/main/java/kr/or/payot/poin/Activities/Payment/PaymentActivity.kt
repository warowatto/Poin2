package kr.or.payot.poin.Activities.Payment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.bottom_payment.*
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Component.Presenters.DaggerPaymentComponent
import kr.or.payot.poin.DI.Modules.Presenters.PaymentActivityPresenter
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.Data.Product
import kr.or.payot.poin.RESTFul.MachineResponse
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 13..
 */
class PaymentActivity : BaseActivity(), PaymentContract.View {

    @Inject
    lateinit var presenter: PaymentContract.Presenter

    lateinit var bottomSheet: BottomSheetBehavior<View>

    lateinit var response: MachineResponse

    val adapter: PaymentAdapter by lazy { PaymentAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        response = intent.getParcelableExtra("info")

        DaggerPaymentComponent.builder()
                .applicationComponent(App.component)
                .paymentActivityPresenter(PaymentActivityPresenter(this, this))
                .build().inject(this)

        initView()

        lifecycle.addObserver(presenter)
    }

    private fun initView() {
        bottomSheet = BottomSheetBehavior.from(bottom_sheet)

        recyclerProducts.layoutManager = LinearLayoutManager(this)
        recyclerProducts.adapter = adapter
        adapter.products.addAll(response.products!!)
        adapter.notifyDataSetChanged()

        txtCompanyName.text = response.company.name
        txtCompanyTel.text = response.company.tel

        txtMachineName.text = response.machine.displayName
        txtMachineType.text = response.machine.type
    }

    val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage(getString(R.string.dialog_progress_machine_connect))
            setCancelable(true)
        }
    }

    lateinit var payDialog: AlertDialog

    override fun connect() {
        progressDialog.show()
    }

    override fun disconnected() {
        AlertDialog.Builder(this)
                .setTitle(R.string.device_disconnect)
                .setMessage(R.string.device_disconnect_message)
                .setNeutralButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { finish() }
                .show()
    }

    override fun ready() {
        progressDialog.dismiss()
    }

    override fun showProduct(products: List<Product>) {
        adapter.products.addAll(products)
        adapter.notifyDataSetChanged()
    }

    override fun payLoading() {
        payDialog = AlertDialog.Builder(this)
                .setTitle(R.string.pay_loading_title)
                .setMessage(R.string.pay_loading_message)
                .setCancelable(false)
                .create()

        payDialog.show()
    }

    override fun payLoadingFinish() {
        payDialog.dismiss()
    }

    override fun payError() {
        payDialog.dismiss()
    }

    @SuppressLint("WrongViewCast")
    override fun showPayment(product: Product) {
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        val point = App.user?.point!!.toString()

        val machineId = response.machine.id
        val productId = product.productId

        txtBottomProductName.text = product.name
        txtBottomPrice.text = product.price.toString()
        txtBottomDevicePosition.text = response.machine.displayName
        txtBottomPoint.text = point

        findViewById<TextView>(R.id.txtPrice).text = product.price.toString()

        imgBottomClose.setOnClickListener { bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN }

        if (point.toInt() < product.price) {
            btnBottomPayPoint.isEnabled = false
        }

        btnBottomPayCard.setOnClickListener {
            presenter.pay(machineId, productId, 5, 0)
        }

    }
}
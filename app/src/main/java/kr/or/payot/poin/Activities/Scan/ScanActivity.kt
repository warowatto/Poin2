package kr.or.payot.poin.Activities.Scan

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import kotlinx.android.synthetic.main.activity_scan.*
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.Activities.CardAdd.CardAddActivity
import kr.or.payot.poin.Activities.Payment.PaymentActivity
import kr.or.payot.poin.Activities.Preference.UserPreferenceActivity
import kr.or.payot.poin.Activities.UserPage.UserActivity
import kr.or.payot.poin.App
import kr.or.payot.poin.DI.Component.Presenters.DaggerScanComponent
import kr.or.payot.poin.DI.Modules.BluetoothModule
import kr.or.payot.poin.DI.Modules.Presenters.ScanActivityPresenter
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.MachineResponse
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 13..
 */
class ScanActivity : BaseActivity(), ScanContract.View, QRCodeReaderView.OnQRCodeReadListener {

    @Inject
    lateinit var presenter: ScanContract.Presenter

    var torchState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        DaggerScanComponent.builder()
                .applicationComponent(App.component)
                .bluetoothModule(BluetoothModule(this))
                .scanActivityPresenter(ScanActivityPresenter(this, this))
                .build().inject(this)

        lifecycle.addObserver(presenter)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)

        qrcodeRederView.setOnQRCodeReadListener(this@ScanActivity)
        qrcodeRederView.setAutofocusInterval(1000)
        qrcodeRederView.setBackCamera()
        scanMode()

        fabTorch.setOnClickListener { toggleTorch() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_scan, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_card -> startActivity(Intent(this, CardAddActivity::class.java))
            R.id.menu_user -> startActivity(Intent(this, UserActivity::class.java))
            R.id.menu_preference -> startActivity(Intent(this, UserPreferenceActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    fun toggleTorch() {
        torchState = !torchState
        qrcodeRederView.setTorchEnabled(torchState)
    }

    override fun onResume() {
        super.onResume()
        presenter.checkState()
    }

    override fun onPause() {
        super.onPause()
        qrcodeRederView.stopCamera()
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        overlayView.setPoints(points)
        text?.let {
            scanModeDisable()

            Log.d("qr text", it)
            val valide = Pattern.compile("^([0-9A-Fa-f]{2}:|-){5}([0-9A-Fa-f]{2})\$")
                    .matcher(it).matches()

            if (valide) {
                presenter.startScan(it)
            } else {
                runOnUiThread { notSupportQrTextData() }
            }
        }
    }

    override fun needCard() {
        scanModeDisable()

        groupError.visibility = View.VISIBLE
        imgError.setImageDrawable(getDrawable(R.drawable.ic_card_one))
        txtError.text = getString(R.string.error_need_card)
        btnError.text = getString(R.string.button_card_add)
        btnError.setOnClickListener { startActivity(Intent(this, CardAddActivity::class.java)) }
    }

    override fun scanMode() {
        overlayView.setPoints(null)
        qrcodeRederView.setQRDecodingEnabled(true)
        groupError.visibility = View.GONE
        txtScanMessage.visibility = View.VISIBLE
    }

    override fun scanModeDisable() {
        qrcodeRederView.setQRDecodingEnabled(false)
        groupError.visibility = View.INVISIBLE
        txtScanMessage.visibility = View.INVISIBLE
    }

    val progress: ProgressDialog by lazy {
        ProgressDialog(this@ScanActivity).apply {
            setMessage("장치를 찾는 중 입니다")
            setCancelable(false)
        }
    }

    override fun showProgress() {
        progress.show()
        scanModeDisable()
    }

    override fun hideProgress() {
        progress.dismiss()
    }

    override fun enableBluetooth() {
        groupError.visibility = View.VISIBLE
        imgError.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_disabled))
        txtError.text = getString(R.string.error_bluetooth_enable)
        btnError.text = getString(R.string.btn_error_blueooth_enable)
        btnError.setOnClickListener { BluetoothAdapter.getDefaultAdapter().enable() }
    }

    override fun notSupportServiceDevice() {
        AlertDialog.Builder(this)
                .setTitle(R.string.error_not_support_service_device)
                .setMessage(R.string.error_not_support_service_device_message)
                .setNeutralButton(R.string.button_ok) { _, _ -> }
                .setOnDismissListener { presenter.checkState() }
                .show()
    }

    override fun serverError() {
        AlertDialog.Builder(this)
                .setTitle(R.string.error_server_title)
                .setMessage(R.string.error_server)
                .setNeutralButton(R.string.button_ok) { _, _ -> }
                .setOnDismissListener { presenter.checkState() }
                .show()
    }

    override fun findDevice(data: MachineResponse, device: BluetoothDevice) {
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("device", device)
            putExtra("info", data)
        }

        startActivity(intent)
    }

    // 해당 장치에 이벤트가 존재하면 이벤트 사항을 먼저 알립니다
    fun showEventInfoDialog(title: String, message: String, success: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.button_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.button_connect, success)
                .show()
    }

    override fun notFoundDevice() {
        AlertDialog.Builder(this)
                .setTitle(R.string.error_device_find_title)
                .setMessage(R.string.error_device_find_message)
                .setNegativeButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { scanMode() }
                .show()
    }

    fun notSupportQrTextData() {
        val rootView = window.decorView.rootView
        Snackbar.make(rootView, R.string.snackbar_qr_error, Snackbar.LENGTH_LONG)
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        scanMode()
                    }
                })
                .setAction(R.string.button_ok) { v -> }
                .show()
    }
}
package kr.or.payot.poin.Utils.UI

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_progress.*
import kr.or.payot.poin.R

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class ProgressFragmentDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun setMessage(message: String) {
        txtMessage.text = message
    }
}
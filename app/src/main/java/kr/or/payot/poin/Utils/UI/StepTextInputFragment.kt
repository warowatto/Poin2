package kr.or.payot.poin.Utils.UI

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError

import java.util.regex.Pattern

import kr.or.payot.poin.R

/**
 * Created by yongheekim on 2018. 2. 22..
 */

class StepTextInputFragment : Fragment(), Step {

    lateinit var editText: TextInputEditText

    var pattern: String? = null
    var hint: String? = null
        set

    var error: String? = null
        set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_step_text, container, false)
        editText = view.findViewById(R.id.editInputText)
        editText.hint = hint

        return view
    }

    override fun verifyStep(): VerificationError? {
        val text = editText.text.toString()
        if (Pattern.matches(pattern, text)) {
            return null
        } else {
            val errorMessage = error
            return VerificationError(errorMessage)
        }
    }

    override fun onSelected() {
        val hint = getString(arguments!!.getInt("hint"))
        editText.hint = hint
    }

    override fun onError(error: VerificationError) {
        val errorMessage = error.errorMessage
        editText.error = errorMessage
    }
}

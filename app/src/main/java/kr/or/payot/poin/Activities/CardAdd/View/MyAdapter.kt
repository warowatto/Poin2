package kr.or.payot.poin.Activities.CardAdd.View

import android.content.Context
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import kr.or.payot.poin.Utils.UI.StepTextInputFragment

/**
 * Created by yongheekim on 2018. 2. 22..
 */
class MyAdapter : AbstractFragmentStepAdapter {
    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createStep(position: Int): Step {
        val fragment = StepTextInputFragment()

        var pattern: String? = null
        var hint: String? = null
        var errorMessage: String? = null
        when (position) {
            // 카드 페이지
            0 -> {
                pattern = "[d]{4}"
            }
        }
        return fragment
    }

    constructor(fm: FragmentManager, context: Context) : super(fm, context)
}
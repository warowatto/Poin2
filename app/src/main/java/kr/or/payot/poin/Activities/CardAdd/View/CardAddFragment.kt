package kr.or.payot.poin.Activities.CardAdd.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.or.payot.poin.Activities.CardAdd.CardAddContract
import kr.or.payot.poin.R
import javax.inject.Inject

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class CardAddFragment : Fragment() {

    @Inject
    lateinit var presenter:CardAddContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_card_add, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}
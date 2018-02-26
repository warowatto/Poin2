package kr.or.payot.poin.Activities.UserPage

import kr.or.payot.poin.RESTFul.Data.Purchase

/**
 * Created by yongheekim on 2018. 2. 15..
 */
interface UserContract {

    interface View {
        fun showProgress()

        fun dismissProgress()

        fun showList(data: List<Purchase>)

        fun emptyList()

        fun serverError()
    }

    interface Presenter {
        fun loadList(userId: Int)
    }
}
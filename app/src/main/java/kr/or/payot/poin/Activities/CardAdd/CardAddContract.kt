package kr.or.payot.poin.Activities.CardAdd

import kr.or.payot.poin.RESTFul.Data.Card

/**
 * Created by yongheekim on 2018. 2. 15..
 */
interface CardAddContract {

    interface View {
        fun showProgress()

        fun hideProgress()

        fun registCard(result: Card)

        fun formError(message: String)

        fun addError(result: String)
    }

    interface Presenter {
        fun addCard(userId: Int, cardNumber: String, expiry: String, birth: String, pwd2digit: String)
    }
}
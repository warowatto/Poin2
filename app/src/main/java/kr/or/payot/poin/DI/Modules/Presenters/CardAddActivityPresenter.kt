package kr.or.payot.poin.DI.Modules.Presenters

import android.content.Context
import android.support.annotation.StringRes
import dagger.Module
import dagger.Provides
import kr.or.payot.poin.Activities.CardAdd.CardAddActivity
import kr.or.payot.poin.Activities.CardAdd.CardAddContract
import kr.or.payot.poin.DI.PerActivity
import kr.or.payot.poin.DI.PerFragment
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.UserAPI
import kr.or.payot.poin.Utils.convert
import kr.or.payot.poin.Utils.observeOnMainThread
import retrofit2.HttpException
import java.util.regex.Pattern

/**
 * Created by yongheekim on 2018. 2. 15..
 */

@Module()
class CardAddActivityPresenter(val activity:CardAddActivity, val view: CardAddContract.View) {

    @PerActivity
    @Provides
    fun presenter(api: UserAPI): CardAddContract.Presenter = object : CardAddContract.Presenter {
        override fun addCard(userId: Int, cardNumber: String, expiry: String, birth: String, pwd2digit: String) {
            val validateCardNumber = Pattern.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}", cardNumber)
            val validateExpiry = Pattern.matches("\\d{4}-\\d{2}", expiry)
            val validateBirth = Pattern.matches("\\d{6}", birth)
            val validatePass = Pattern.matches("\\d{2}", pwd2digit)

            if (!validateCardNumber) { view.formError(getString(R.string.error_from_cardnumber)); return }
            if (!validateExpiry) { view.formError(getString(R.string.error_from_expiry)); return }
            if (!validateBirth) { view.formError(getString(R.string.error_from_birth)); return }
            if (!validatePass) { view.formError(getString(R.string.error_from_pass)); return }

            view.showProgress()
            api.addCard(userId, "", cardNumber, expiry, birth, pwd2digit)
                    .observeOnMainThread()
                    .doOnDispose { view.hideProgress() }
                    .subscribe(
                            { view.registCard(it) },
                            {
                                if (it is HttpException) {
                                    val message = it.convert()
                                    view.addError(message.get("message") as String)
                                }
                            }
                    )
        }

        fun getString(@StringRes id: Int): String = activity.getString(id)

    }
}
package kr.or.payot.poin.DI.Modules.Presenters

import dagger.Module
import dagger.Provides
import kr.or.payot.poin.Activities.UserPage.UserContract
import kr.or.payot.poin.DI.PerActivity
import kr.or.payot.poin.RESTFul.UserAPI
import kr.or.payot.poin.Utils.observeOnMainThread
import retrofit2.HttpException

/**
 * Created by yongheekim on 2018. 2. 22..
 */

@Module
class UserActivityPresenter(val view: UserContract.View) {

    @PerActivity
    @Provides
    fun presenter(api: UserAPI): UserContract.Presenter = object : UserContract.Presenter {
        override fun loadList(userId: Int) {
            view.showProgress()
            api.list(userId)
                    .observeOnMainThread()
                    .subscribe(
                            {
                                view.dismissProgress()
                                view.showList(it)
                            },
                            {
                                view.dismissProgress()
                                if (it is HttpException) {
                                    val code = it.code()
                                    if (code == 404) {
                                        view.emptyList()
                                    } else {
                                        view.serverError()
                                    }
                                }
                            })
        }
    }
}
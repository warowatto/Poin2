package kr.or.payot.poin.Activities.Payment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import kr.or.payot.poin.RESTFul.Data.Product

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface PaymentContract {

    interface View {
        fun showProduct(products: List<Product>)

        fun showPayment(product: Product)

        fun payLoading()

        fun payLoadingFinish()

        fun payError()

        fun connect()

        fun disconnected()

        fun ready()
    }

    interface Presenter : LifecycleObserver {
        fun pay(machineId: Int, productId: Int, cardId: Int, eventId: Int?)

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun disconnect()
    }
}
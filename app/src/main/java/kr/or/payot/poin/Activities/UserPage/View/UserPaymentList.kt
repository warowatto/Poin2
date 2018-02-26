package kr.or.payot.poin.Activities.UserPage.View

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.Data.Purchase
import kr.or.payot.poin.Utils.UI.DefaultRecyclerAdapter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class UserPaymentList : DefaultRecyclerAdapter<Purchase, UserPaymentList.ViewHolder>() {
    val format = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.listitem_purchase, parent, false)

        return ViewHolder(view)
    }

    override fun onBind(holder: ViewHolder, data: Purchase, position: Int) {
        holder.run {
            txtAmount.text = data.amount.toString()
            txtProductName.text = data.productName
            txtMachineName.text = data.machineName
            txtPayAt.text = dateConvert(data.pay_at)
        }
    }

    fun dateConvert(date: Date): String = format.format(date)

    inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val txtAmount: TextView
        val txtPayAt: TextView
        val txtMachineName: TextView
        val txtProductName: TextView

        init {
            this.txtAmount = rootView.findViewById(R.id.txtAmount)
            this.txtPayAt = rootView.findViewById(R.id.txtPayAt)
            this.txtMachineName = rootView.findViewById(R.id.txtMachineName)
            this.txtProductName = rootView.findViewById(R.id.txtProductName)
        }
    }
}
package kr.or.payot.poin.Activities.Payment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.or.payot.poin.R
import kr.or.payot.poin.RESTFul.Data.Company
import kr.or.payot.poin.RESTFul.Data.Machine
import kr.or.payot.poin.RESTFul.Data.Product

/**
 * Created by yongheekim on 2018. 2. 22..
 */
class PaymentAdapter(val view: PaymentContract.View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val products: ArrayList<Product> = arrayListOf()

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ProductInfoViewHolder) {
            val data = products[position]
            holder.txtProductName.text = data.name
            holder.txtProductPrice.text = data.price.toString()

            holder.rootView.setOnClickListener {
                view.showPayment(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)

        val view = inflater.inflate(viewType, parent, false)

        return ProductInfoViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.listitem_product
    }

    inner class ProductInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: View
        val txtProductName: TextView
        val txtProductPrice: TextView

        init {
            rootView = view
            txtProductName = view.findViewById(R.id.txtProductName)
            txtProductPrice = view.findViewById(R.id.txtPrice)
        }
    }
}
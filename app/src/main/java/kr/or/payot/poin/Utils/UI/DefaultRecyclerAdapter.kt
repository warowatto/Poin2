package kr.or.payot.poin.Utils.UI

import android.support.v7.widget.RecyclerView

/**
 * Created by yongheekim on 2018. 2. 15..
 */
open abstract class DefaultRecyclerAdapter<D : Any, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    val items: ArrayList<D> = arrayListOf()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = getItem(position)
        onBind(holder, data, position)
    }

    abstract fun onBind(holder: VH, data: D, position: Int)

    fun clearItem() =
            items.clear()

    fun addItem(data: D) {
        items.add(data)
        notifyDataSetChanged()
    }

    fun addAllItem(datas: List<D>) {
        items.addAll(datas)
        notifyDataSetChanged()
    }

    fun removeItem(data: D) =
            items.remove(data)

    fun removeItem(position: Int) =
            items.removeAt(position)

    fun getItem(position: Int): D = items[position]

    fun isEmptyList() = items.isEmpty()
}
package kr.or.payot.poin.RESTFul.Data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by yongheekim on 2018. 2. 21..
 */
data class Product(
        val productId: Int,
        val name: String,
        val description: String,
        val price: Int,
        val serviceTime: Long) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(productId)
        writeString(name)
        writeString(description)
        writeInt(price)
        writeLong(serviceTime)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
            override fun createFromParcel(source: Parcel): Product = Product(source)
            override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
        }
    }
}
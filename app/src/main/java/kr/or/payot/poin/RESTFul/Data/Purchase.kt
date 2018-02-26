package kr.or.payot.poin.RESTFul.Data

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by yongheekim on 2018. 2. 22..
 */
data class Purchase(val machineName: String, val productName: String, val amount: Int, val pay_at: Date) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readSerializable() as Date
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(machineName)
        writeString(productName)
        writeInt(amount)
        writeSerializable(pay_at)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Purchase> = object : Parcelable.Creator<Purchase> {
            override fun createFromParcel(source: Parcel): Purchase = Purchase(source)
            override fun newArray(size: Int): Array<Purchase?> = arrayOfNulls(size)
        }
    }
}
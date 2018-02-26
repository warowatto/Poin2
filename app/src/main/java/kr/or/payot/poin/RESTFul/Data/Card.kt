package kr.or.payot.poin.RESTFul.Data

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by yongheekim on 2018. 2. 22..
 */
data class Card(val id: Int, val bankName: String, val displayName: String, val create_at: Date) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readSerializable() as Date
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(bankName)
        writeString(displayName)
        writeSerializable(create_at)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Card> = object : Parcelable.Creator<Card> {
            override fun createFromParcel(source: Parcel): Card = Card(source)
            override fun newArray(size: Int): Array<Card?> = arrayOfNulls(size)
        }
    }
}
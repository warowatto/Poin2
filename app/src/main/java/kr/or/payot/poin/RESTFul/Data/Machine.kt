package kr.or.payot.poin.RESTFul.Data

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by yongheekim on 2018. 2. 21..
 */
data class Machine(
        val id: Int,
        val macAddress: String,
        val displayName: String,
        val type: String,
        val description: String,
        val defaultProductId: Int,
        val create_at: Date) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readSerializable() as Date
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(macAddress)
        writeString(displayName)
        writeString(type)
        writeString(description)
        writeInt(defaultProductId)
        writeSerializable(create_at)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Machine> = object : Parcelable.Creator<Machine> {
            override fun createFromParcel(source: Parcel): Machine = Machine(source)
            override fun newArray(size: Int): Array<Machine?> = arrayOfNulls(size)
        }
    }
}
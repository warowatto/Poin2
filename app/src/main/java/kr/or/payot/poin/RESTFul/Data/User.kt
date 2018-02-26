package kr.or.payot.poin.RESTFul.Data

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by yongheekim on 2018. 2. 21..
 */
data class User(val id: Int,
        // M(Man) or F(Female)
                val gender: String,
                val profileImage: String?,
                val thumbnailImage: String?,
                val point: Int,
                val create_at: Date,
                val cards: List<Card>?) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readSerializable() as Date,
            source.createTypedArrayList(Card.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(gender)
        writeString(profileImage)
        writeString(thumbnailImage)
        writeInt(point)
        writeSerializable(create_at)
        writeTypedList(cards)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
package kr.or.payot.poin.RESTFul

import android.os.Parcel
import android.os.Parcelable
import io.reactivex.Single
import kr.or.payot.poin.RESTFul.Data.Company
import kr.or.payot.poin.RESTFul.Data.Machine
import kr.or.payot.poin.RESTFul.Data.Product
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by yongheekim on 2018. 2. 16..
 */
interface MachineAPI {

    @GET("machine/{address}")
    fun getMachine(@Path("address") address: String)
            : Single<MachineResponse>
}

data class MachineResponse(
        val company: Company,
        val machine: Machine,
        var products: List<Product>? = null,
        val isRunning: Boolean) : Parcelable {
    constructor(source: Parcel) : this(
            source.readParcelable<Company>(Company::class.java.classLoader),
            source.readParcelable<Machine>(Machine::class.java.classLoader),
            source.createTypedArrayList(Product.CREATOR),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(company, 0)
        writeParcelable(machine, 0)
        writeTypedList(products)
        writeInt((if (isRunning) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MachineResponse> = object : Parcelable.Creator<MachineResponse> {
            override fun createFromParcel(source: Parcel): MachineResponse = MachineResponse(source)
            override fun newArray(size: Int): Array<MachineResponse?> = arrayOfNulls(size)
        }
    }
}
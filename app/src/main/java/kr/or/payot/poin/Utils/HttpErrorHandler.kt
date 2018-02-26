package kr.or.payot.poin.Utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

/**
 * Created by yongheekim on 2018. 2. 16..
 */

fun HttpException.convert(): Map<String, Any> {
    val message = this.response().errorBody().toString()
    val typeToken = object : TypeToken<Map<String, String>>(){}.type

    return Gson().fromJson(message, typeToken)
}
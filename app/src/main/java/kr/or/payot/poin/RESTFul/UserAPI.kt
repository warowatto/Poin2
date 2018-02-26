package kr.or.payot.poin.RESTFul

import io.reactivex.Single
import kr.or.payot.poin.RESTFul.Data.Card
import kr.or.payot.poin.RESTFul.Data.Purchase
import kr.or.payot.poin.RESTFul.Data.User
import retrofit2.http.*

/**
 * Created by yongheekim on 2018. 2. 13..
 */

interface UserAPI {

    // 로그인
    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("flatform") flatform: String, @Field("token") token: String)
            : Single<User>

    @FormUrlEncoded
    @POST("/user")
    fun signup(@Field("flatform") flatform: String,
               @Field("token") token: String,
               @Field("email") email: String?,
               @Field("name") name: String?,
               @Field("gender") gender: String?,
               @Field("profileImage") profileImage: String?,
               @Field("thumbnailImage") thumbnailImage: String?)
            : Single<User>

    // 계정 삭제
    @DELETE("/user/{flatform}/{token}")
    fun unregist(@Path("flatform") flatform: String, @Path("token") token: String)
            : Single<Map<String, Any>>

    // 결제 목록 가져오기
    @GET("/user/{userId}/payment")
    fun list(@Path("userId") userId: Int)
            : Single<List<Purchase>>

    // 결제하기
    @FormUrlEncoded
    @POST("/user/payment")
    fun payment(
            @Field("cardId") cardId: Int,
            @Field("machineId") machineId: Int,
            @Field("productId") productId: Int,
            @Field("amount") amount: Int)
            : Single<Map<String, Any>>

    // 결제 취소
    @FormUrlEncoded
    @DELETE("/user/payment/cencel/{paymentId}")
    fun payback(@Path("paymentId") paymentId: Int)
            : Single<Map<String, Any>>

    // 카드 등록
    @FormUrlEncoded
    @POST("/user/{userId}/card")
    fun addCard(@Path("userId") userId: Int,
                @Field("displayName") displayName: String,
                @Field("card_number") cardNumber: String,
                @Field("expiry") expiry: String,
                @Field("birth") birth: String,
                @Field("pwd_2digit") pwd2digit: String)
            : Single<Card>

    // 카드 삭제
    @DELETE("/user/{userId}/card/{cardId}")
    fun removeCard(@Path("userId") userId: Int,
                   @Path("cardId") cardId: Int)
            : Single<Map<String, Any>>
}
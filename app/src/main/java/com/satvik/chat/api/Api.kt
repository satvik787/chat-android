package com.satvik.chat.api

import retrofit2.Call
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("/login")
    fun login(@Field("user_name") userName:String,
              @Field("password") password:String): Call<String>

    @FormUrlEncoded
    @POST("/signup")
    fun signUp(@Field("user_name") userName:String,
              @Field("password") password:String): Call<String>

    @GET("/channel/all")
    fun getChannels(@Query("user_id") userId:Int):Call<String>

    @GET("/msg/all")
    fun getMessages(@Query("user_id") userId:Int,@Query("channel_id") channelId:Int):Call<String>

    @FormUrlEncoded
    @POST("/msg/new")
    fun postMessage(
        @Field("user_id") userId: Int,
        @Field("channel_id") channelId: Int,
        @Field("text") text:String
    ):Call<String>

    @GET("/user/search")
    fun getSearchResults(@Query("user_name") userName:String):Call<String>

    @GET("/channel/new")
    fun createChannel(@Query("user_one") userOne:Int,@Query("user_two") userTwo:Int):Call<String>

}
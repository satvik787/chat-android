package com.satvik.chat

import android.content.Context
import androidx.preference.PreferenceManager
import com.satvik.chat.api.Api
import com.satvik.chat.api.Requests
import com.satvik.chat.api.Res
import com.satvik.chat.api.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap

class ChatRepository private constructor(private val context: Context) {


    private val retrofitObj = Retrofit.Builder()
        .baseUrl("https://bruhchatapp.herokuapp.com")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val api = retrofitObj.create(Api::class.java)

    val requests = Requests(api)


    fun geUserId():Int{
        return sharedPreferences.getInt(USER_ID,-1)
    }



    fun addUser(user: User){
        sharedPreferences
            .edit()
            .putInt(USER_ID,user.userId)
            .putString(USER_NAME,user.userName)
            .putString(USER_CREATED_ON,user.createdOn)
            .putString(USER_IMAGE,user.image)
            .apply()
    }

    companion object{
        var isActive = false
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_CREATED_ON = "created_on"
        const val USER_IMAGE = "user_image"
        private var instance:ChatRepository? = null
        fun init(context: Context){
            if(instance == null){
                instance = ChatRepository(context)
            }
        }
        fun get():ChatRepository{
            if (instance != null){
                return instance!!
            }else{
                throw IllegalStateException()
            }
        }
    }

    fun getPostMessageCall(userId:Int,channelId:Int,text:String):Call<String>{
        return api.postMessage(userId,channelId,text)
    }

    fun getMessagesCall(userId: Int,channelId: Int):Call<String>{
        return api.getMessages(userId,channelId)
    }

    fun getSearchUserCall(userName:String):Call<String>{
        return api.getSearchResults(userName)
    }

    interface Callback{
        fun onComplete(it: Res<Int>)
    }
}
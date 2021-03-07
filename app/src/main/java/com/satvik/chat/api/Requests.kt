package com.satvik.chat.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.satvik.chat.ChatRepository
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap

class Requests(private val api: Api) {

    private val hashMap = ConcurrentHashMap<Int, Call<*>>()


    fun login(userName:String, password:String): LiveData<Res<User>> {
        val call = api.login(userName,password)
        val liveData = MutableLiveData<Res<User>>()
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let {
                    val json = JSONObject(it)
                    liveData.value = Res(
                        User.toUser(json),
                        json.getString("msg"),
                        json.getInt("status")
                    )
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                liveData.value = Res(null,t.message!!,1)
            }

        })
        return liveData
    }

    fun signUp(userName: String,password: String): LiveData<Res<User>> {
        val call = api.signUp(userName,password)
        val liveData = MutableLiveData<Res<User>>()
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let {
                    val json = JSONObject(it)
                    liveData.value = Res(
                        User.toUser(json),
                        json.getString("msg"),
                        json.getInt("status")
                    )
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                liveData.value = Res(null,t.message!!,1)
            }

        })
        return liveData
    }

    fun getAllChannels(userId: Int,liveData:MutableLiveData<Res<List<Channel>>>){
        val call = api.getChannels(userId)
        call.enqueue(object:Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let {
                    val obj = JSONObject(it)
                    liveData.value = Res(
                        Channel.toChannels(obj.getJSONArray("data")),
                        obj.getString("msg"),
                        obj.getInt("status")
                    )
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                liveData.value = Res(null,t.message!!,1)
            }

        })
    }

    fun getAllMsg(userId:Int,channelId: Int):LiveData<Res<List<Chat>>>{
        val liveData = MutableLiveData<Res<List<Chat>>>()
        val call = api.getMessages(userId,channelId)
        hashMap[call.hashCode()] = call
        call.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                hashMap.remove(call.hashCode())
                response.body()?.let {
                    val obj = JSONObject(it)
                    val list = Chat.toChats(obj.getJSONArray("data"))
                    if(liveData.value != null && liveData.value!!.data?.size ?: 0 < list.size) {
                        liveData.value = Res(
                            list,
                            obj.getString("msg"),
                            obj.getInt("status"),
                        )
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                hashMap.remove(call.hashCode())
                liveData.value = Res(null,t.message!!,1)
            }

        })
        return liveData
    }

    fun searchUser(userName: String,liveData:MutableLiveData<List<User>>){
        val call = api.getSearchResults(userName)
        call.enqueue(object:Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let {
                    val obj = JSONObject(it)
                    liveData.value = User.toUsers(obj.getJSONArray("data"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                liveData.value = listOf()
            }

        })
    }

    fun createChannel(userOne:Int,userTwo:Int, callback: ChatRepository.Callback){
        val call = api.createChannel(userOne,userTwo)
        call.enqueue(object:Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let{
                    val obj = JSONObject(it)
                    var data = 0
                    if (obj.has("data")){
                        data = obj.getJSONObject("data").getInt("channel_id")
                    }
                    if(ChatRepository.isActive){
                        callback.onComplete(Res(
                            data,
                            obj.getString("msg"),
                            obj.getInt("status")
                        ))
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                if(ChatRepository.isActive){
                    callback.onComplete(Res(
                        0,
                        t.message!!,
                        1
                    ))
                }
            }
        })
    }

}
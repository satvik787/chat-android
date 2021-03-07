
package com.satvik.chat

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.lifecycle.*
import com.satvik.chat.api.Channel
import com.satvik.chat.api.Chat
import com.satvik.chat.api.Res
import org.json.JSONObject
import retrofit2.Call

class BackgroundThread:HandlerThread("chat thread"),LifecycleObserver {

    companion object{
        const val POST_MSG = 1
        const val GET_MSG = 2
    }

    private lateinit var liveData:MutableLiveData<Res<List<Chat>>>
    private lateinit var handler: Handler
    override fun onLooperPrepared() {
        handler = object :Handler(looper){
            override fun handleMessage(msg: Message) {
                if(msg.what == POST_MSG){
                    val call = msg.obj as Call<String>
                    call.execute().body()?.let {
                        val obj = JSONObject(it)
                        if(obj.getInt("status") == 1){
                            println("ERROR ${obj.getString("msg")}")
                        }
                    }
                }else if(msg.what == GET_MSG){
                    val call = msg.obj as Call<String>
                    call.execute().body()?.let {
                        val obj = JSONObject(it)
                        val list = Chat.toChats(obj.getJSONArray("data"))
                        if(liveData.value == null || liveData.value!!.data?.size ?: 0 < list.size) {
                            liveData.postValue(Res(
                                list,
                                obj.getString("msg"),
                                obj.getInt("status")
                            ))
                        }
                    }
                    val o = ChatRepository.get().getMessagesCall(msg.arg1,msg.arg2)
                    val m = obtainMessage(GET_MSG,o).apply { arg1 = msg.arg1;arg2 = msg.arg2 }
                    sendMessageDelayed(m,1000)
                }
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun startThread(){
        start()
        looper
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopThread(){
        quit()
    }

    fun postMessage(userId:Int,channelId:Int,text:String){
        val call = ChatRepository.get().getPostMessageCall(userId,channelId,text)
        handler.obtainMessage(POST_MSG,call).sendToTarget()
    }

    fun getMessages(userId: Int,channelId: Int,liveData: MutableLiveData<Res<List<Chat>>>){
        this.liveData = liveData
        val call = ChatRepository.get().getMessagesCall(userId,channelId)
        handler.obtainMessage(GET_MSG,call).apply {
            arg1 = userId
            arg2 = channelId
        }.sendToTarget()
    }

    fun getSearchResult(userName:String,liveData: MutableLiveData<List<Channel>>){

    }
}
package com.satvik.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satvik.chat.api.Channel
import com.satvik.chat.api.Res
import com.satvik.chat.api.User

class ChannelViewModel:ViewModel() {
    var allChannel:MutableLiveData<Res<List<Channel>>> = MutableLiveData()
    val searchUsers:MutableLiveData<List<User>> = MutableLiveData()

    fun getChannels(userId: Int){
        ChatRepository.get().requests.getAllChannels(userId,allChannel)
    }

    fun getUsers(userName:String){
        ChatRepository.get().requests.searchUser(userName,searchUsers)
    }

    fun createChannel(userOne: Int,userTwo:Int,callback: ChatRepository.Callback){
        ChatRepository.get().requests.createChannel(userOne,userTwo,callback)
    }
}
package com.satvik.chat


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satvik.chat.api.Chat
import com.satvik.chat.api.Res

class ChatViewModel:ViewModel() {
    val liveData = MutableLiveData<Res<List<Chat>>>()

}
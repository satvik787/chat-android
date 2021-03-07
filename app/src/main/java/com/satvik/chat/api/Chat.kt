package com.satvik.chat.api

import org.json.JSONArray

data class Chat(
    val msgID:Int,
    val channelID:Int,
    val userID:Int,
    val msg:String,
    val chainVal:Double?,
    val sentAt:String
)
{
    companion object{
        fun toChats(obj: JSONArray):List<Chat>{
            val list: MutableList<Chat> = mutableListOf()
            for (i in 0 until obj.length()){
                val chat = obj.getJSONObject(i)
                list.add(Chat(
                    chat.getInt("msg_id"),
                    chat.getInt("channel_id"),
                    chat.getInt("user_id"),
                    chat.getString("msg"),
                    chat.getDouble("chain_val"),
                    chat.getString("sent_at")
                ))
            }
            return list.sortedBy { c -> c.msgID }
        }
    }
}
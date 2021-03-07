package com.satvik.chat.api

import org.json.JSONArray
import org.json.JSONObject

data class Channel(
    val channelId: Int,
    val userTwoId: Int,
    val userTwoName:String,
    val profileImage:String)
{
    companion object{
        fun channelIdFromUser(obj:JSONObject):Int{
            return obj.getInt("channel_id")
        }
        private fun toChannel(obj: JSONObject):Channel{
            return Channel(
                obj.getInt("channel_id"),
                obj.getInt("user_two_id"),
                obj.getString("user_two_name"),
                obj.getString("user_two_image")
            )
        }
        fun toChannels(obj: JSONArray):MutableList<Channel>{
            val list = mutableListOf<Channel>()
            for(i in 0 until obj.length()){
                list.add(toChannel(obj.getJSONObject(i)))
            }
            println("Channel list $list")
            return list
        }
    }
}
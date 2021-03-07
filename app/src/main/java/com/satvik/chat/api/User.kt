package com.satvik.chat.api

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class User(
    val userId:Int,
    val userName:String,
    val image:String?,
    val createdOn:String
){
    override fun toString(): String {
        return userId.toString() + userName
    }
    companion object{
        fun toUser(obj: JSONObject):User?{
            val user:User
            try {
                user = User(
                    obj.getInt("id"),
                    obj.getString("user_name"),
                    obj.getString("user_profile"),
                    obj.getString("created_on")
                )
            }catch (e: JSONException){
                println(e.message)
                return null
            }
            return user
        }
        fun toUsers(obj:JSONArray):List<User>{
            val list = mutableListOf<User>()
            for(i in 0 until obj.length()){
                list.add(toUser(obj.getJSONObject(i))!!)
            }
            return list
        }
    }

}
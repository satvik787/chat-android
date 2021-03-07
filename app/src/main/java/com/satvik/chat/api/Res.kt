package com.satvik.chat.api


data class Res<T>(
    val data: T?,
    val msg:String,
    val status:Int)
package com.example.myapplication

data class ChatModel(
    val buyerId: String,
    val sellerId : String,
    val itemTitle: String,
    val key: Long
){
    constructor(): this("","","",0)
}
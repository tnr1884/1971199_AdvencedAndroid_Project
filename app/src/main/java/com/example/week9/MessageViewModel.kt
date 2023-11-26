package com.example.week9

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Message(val sender : String, val receiver : String, val message : String)
class MessageViewModel : ViewModel() {
    val messagesListData = MutableLiveData<ArrayList<Message>>()
    val messages = ArrayList<Message>()
    val messageClickEvent = MutableLiveData<Int>()
    var messageLongClick = -1
    fun addMessage(message: Message) {
        messages.add(message)
        messagesListData.value = messages // let the observer know the livedata changed
    }
    fun updateItem(pos: Int, message: Message) {
        messages[pos] = message
        messagesListData.value = messages // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun clear() {
        messages.clear()
    }


}
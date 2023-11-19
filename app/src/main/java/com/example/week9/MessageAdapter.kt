package com.example.week9

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MessageAdapter(private val messageViewModel: MessageViewModel) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    inner class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.receivedmessage_layout, parent, false)
        val viewHolder = ViewHolder(view)

        view.setOnClickListener {
           //viewModel.messageClickEvent.value = viewHolder.adapterPosition
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return messageViewModel.messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
/*        var database = Firebase.database
        var itemsRef = database.getReference("items")*/
        var messageSender = view.findViewById<TextView>(R.id.messagesender)
        var sendedMessage = view.findViewById<TextView>(R.id.sendedmessage)

        messageSender.text=messageViewModel.messages[position].sender
        sendedMessage.text=messageViewModel.messages[position].message


    }
}
package com.example.week9

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReceivedMessageDialog() : DialogFragment() {
    private var db : FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("chat")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_received_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        val recyclerView = view.findViewById<RecyclerView>(R.id.messagerecyclerview)
        val adapter = MessageAdapter(messageViewModel)
        /*var sender : String
        var receiver : String
        var msg :String*/

        addMessageInViewModel(messageViewModel)

        itemsCollectionRef.get().addOnSuccessListener {
            for (doc in it) {
                if (doc["receiver"] == Firebase.auth.currentUser?.email) {
                    messageViewModel.addMessage(Message(doc["sender"].toString(), doc["receiver"].toString(), doc["message"].toString()))

                }
            }
            adapter.notifyDataSetChanged()
        }


        recyclerView.adapter=adapter
        recyclerView.layoutManager= LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)


        view.findViewById<TextView>(R.id.textView23).setOnClickListener {
            println(messageViewModel.messages.size)
            //println(messageViewModel.messages[0].message)
        }

    }

    private fun addMessageInViewModel(viewModel: MessageViewModel) {

    }

}
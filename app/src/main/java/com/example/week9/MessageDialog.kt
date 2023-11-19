package com.example.week9

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MessageDialog(private var seller: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.message_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        var sender = view.findViewById<TextView>(R.id.sender)
        var receiver = view.findViewById<TextView>(R.id.receiver)
        var message = view.findViewById<EditText>(R.id.editTextTextMultiLine)

        sender.text=Firebase.auth.currentUser?.email ?: "No User"
        receiver.text=seller


        view.findViewById<Button>(R.id.sendmessagebutton).setOnClickListener {
            var db : FirebaseFirestore = Firebase.firestore
            val itemsCollectionRef = db.collection("chat")


            val itemMap = hashMapOf(
                "sender" to sender.text.toString(),
                "receiver" to receiver.text.toString(),
                "message" to message.text.toString()
            )
            itemsCollectionRef.add(itemMap).addOnSuccessListener {
                println("gg")

            }.addOnFailureListener {
                Log.d(ContentValues.TAG, it.toString())
            }
           // itemsRef.push().setValue("hello world")
        }
    }
}
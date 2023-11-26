package com.example.week9

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItemDialog(private val adapter: CustomAdapter, private val viewModel: MyViewModel) : DialogFragment() {
    private val db : FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("product")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.productregisterbutton).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.editname).text.toString()
            val price = view.findViewById<EditText>(R.id.productprice).text.toString().toInt()
            val title = view.findViewById<EditText>(R.id.edittitle).text.toString()
            val isSelled = true
            val imageUrl = "gs://prac-ebd62.appspot.com/국문시그니쳐.jpg"
            val seller = Firebase.auth.currentUser?.email ?: "no user"

            val itemMap = hashMapOf(
                "name" to name,
                "price" to price,
                "title" to title,
                "isSelled" to isSelled,
                "imageUrl" to imageUrl,
                "seller" to seller
            )

            itemsCollectionRef.add(itemMap).addOnSuccessListener {
                viewModel.addItem(Item(imageUrl,title,name,price,isSelled, seller, it.id))
                adapter.notifyDataSetChanged()
                dismiss()

            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
        }

    }


}
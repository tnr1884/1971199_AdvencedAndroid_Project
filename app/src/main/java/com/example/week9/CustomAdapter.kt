package com.example.week9

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class CustomAdapter(private val viewModel : MyViewModel) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private val storage = Firebase.storage
    inner class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_layout, parent, false)
        val viewHolder = ViewHolder(view)

        view.setOnClickListener {
            viewModel.itemClickEvent.value = viewHolder.adapterPosition
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return viewModel.items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val text1 = view.findViewById<TextView>(R.id.textView1)
        val text2 = view.findViewById<TextView>(R.id.textView2)
        val image = view.findViewById<ImageView>(R.id.imageView2)

        val url = viewModel.items[position].imageUrl
        //println(url)
        val imageRef = storage.getReferenceFromUrl(url)
        displayImage(imageRef, image)

        text1.text = viewModel.items[position].firstName
        text2.text = viewModel.items[position].lastName

    }
    private fun displayImage(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
            println("failed image")
        }
    }
}
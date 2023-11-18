package com.example.week9

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class CustomAdapter(private val viewModel : MyViewModel, private val context: Context) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
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
        val productTitle = view.findViewById<TextView>(R.id.producttitle)
        val isSelled = view.findViewById<TextView>(R.id.isSelled)
        val image = view.findViewById<ImageView>(R.id.imageView2)
        val name = view.findViewById<TextView>(R.id.name)
        val price = view.findViewById<TextView>(R.id.price)
        val seller = view.findViewById<TextView>(R.id.seller)

        val url = viewModel.items[position].imageUrl
        //println(url)
        val imageRef = storage.getReferenceFromUrl(url)
        /*Fatal Exception: java.lang.IllegalArgumentException
                location must not be null or empty*/


        productTitle.text = viewModel.items[position].title
        price.text = viewModel.items[position].price.toString() + " 원"
        name.text=viewModel.items[position].name
        seller.text=viewModel.items[position].seller
        if (viewModel.items[position].isSelled) {
            isSelled.setTextColor(Color.parseColor("#FF0000"))
            isSelled.text="판매 중"
        }
        else{
            isSelled.setTextColor(Color.parseColor("#0000FF"))
            isSelled.text="판매 완료"
        }
        displayImage(imageRef, image)

        /*view.findViewById<Button>(R.id.productdetail).setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("title", viewModel.items[position].title)
            intent.putExtra("name", viewModel.items[position].name)
            intent.putExtra("price", viewModel.items[position].price)
            intent.putExtra("name", viewModel.items[position].name)
            context.startActivity(intent)
        }*/


    }
    private fun displayImage(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
            exception -> println("실패 : $exception")
        }
    }

}
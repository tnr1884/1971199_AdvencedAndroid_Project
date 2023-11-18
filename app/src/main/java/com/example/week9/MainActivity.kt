package com.example.week9

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.integrity.internal.t
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var scope: CoroutineScope

    private var db : FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("product")
    val count=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*Firebase.auth.signInWithEmailAndPassword("hansung@gmail.com", "hansung").addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, user, Toast.LENGTH_SHORT).show()
            }
            else {
                println("fail")
            }
        }*/
        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.user)?.text = Firebase.auth.currentUser?.email ?: "No User"

        val viewModel by viewModels<MyViewModel>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CustomAdapter(viewModel, this)

        /*itemsCollectionRef.document("book").get().addOnSuccessListener {
            val img = "gs://prac-ebd62.appspot.com/대한민국_대통령기.png"
            val name = it["name"].toString()
            val title = it["title"].toString()
            val price = it["price"].toString()
            viewModel.addItem(Item(img,title,name,price.toInt(),true))
            adapter.notifyDataSetChanged()
        }.addOnFailureListener {
            exception -> println("실패 : $exception")
        }*/
        itemsCollectionRef.get().addOnSuccessListener {
            for(doc in it) {
                viewModel.addItem(
                    Item(
                        doc["imageUrl"].toString(),
                        doc["title"].toString(),
                        doc["name"].toString(),
                        doc["price"].toString().toInt(),
                        doc["isSelled"].toString().toBoolean(),
                        doc["seller"].toString(),
                        doc.id
                    )
                )
                adapter.notifyDataSetChanged()

            }
        }.addOnFailureListener {
                exception -> println("실패 : $exception")
        }
        //viewModel.addItem(Item("gs://prac-ebd62.appspot.com/대한민국_대통령기.png", "sdf", "sdf", 156, true))


        val imageRef = Firebase.storage.getReferenceFromUrl("gs://prac-ebd62.appspot.com/문장.png")
        displayImage(imageRef, findViewById<ImageView>(R.id.imageView))


        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        findViewById<FloatingActionButton>(R.id.floatingActionButton)?.setOnClickListener {
            println(Firebase.auth.currentUser?.email ?: "no user")
        }


        findViewById<Button>(R.id.buttonRegister)?.setOnClickListener {
            ItemDialog(adapter, viewModel).show(supportFragmentManager, "")
        }

        viewModel.itemClickEvent.observe(this) {
            val item = viewModel.items[it]

            if (item.seller==Firebase.auth.currentUser?.email ?: "no user") {
                updateDialog(adapter, viewModel, it, item).show(supportFragmentManager, "")
            }
            else {
                //Toast.makeText(this, "내가 등록한 상품이 아닙니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("title", item.title)
                intent.putExtra("name", item.name)
                intent.putExtra("price", item.price)
                intent.putExtra("isSelled", item.isSelled)
                intent.putExtra("seller", item.seller)
                startActivity(intent)

            }
        }




    }
    private fun refreshProductPicture(adapter: CustomAdapter) {
        scope = CoroutineScope(Dispatchers.Default).apply { // 코루틴 컨텍스트(디스패처만 지정함)로 코루틴범위 생성
            launch { // 코루틴범위 객체의 메소드 launch를 사용하여 코루틴 생성
                while(true) {
                    delay(2000) // 1000 ms 대기
                    withContext(Dispatchers.Main) { // runOnUiThread 대신 코루틴으로 사용
                        println("adapter")
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        /*itemsCollectionRef.document("2DKGIZj2TGFrXnXBF1JM").addSnapshotListener { snapshot, error ->
            Log.d(TAG, "${snapshot?.id} ${snapshot?.data}")
        }*/
        //Firebase.auth.signOut()
    }
    private fun displayImage(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
        }
    }


}
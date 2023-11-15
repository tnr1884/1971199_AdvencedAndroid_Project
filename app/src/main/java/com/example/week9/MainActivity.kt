package com.example.week9

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Firebase.auth.signInWithEmailAndPassword("hansung@gmail.com", "hansung").addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, Firebase.auth.currentUser?.uid ?: "no user", Toast.LENGTH_SHORT).show()
            }
            else {
                println("fail")
            }
        }
        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        //findViewById<TextView>(R.id.textView)?.text = Firebase.auth.currentUser?.uid ?: "No User"

        val viewModel by viewModels<MyViewModel>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        viewModel.addItem(Item("gs://prac-ebd62.appspot.com/대한민국_대통령기.png", "sdf", "sdf"))
        viewModel.addItem(Item("gs://prac-ebd62.appspot.com/문장.png", "sdf23", "123"))
        viewModel.addItem(Item("gs://prac-ebd62.appspot.com/문장.png", "sdf23", "123"))


        val imageRef = Firebase.storage.getReferenceFromUrl("gs://prac-ebd62.appspot.com/문장.png")
        displayImage(imageRef, findViewById<ImageView>(R.id.imageView))

        val adapter = CustomAdapter(viewModel)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        findViewById<FloatingActionButton>(R.id.floatingActionButton)?.setOnClickListener {

            ItemDialog().show(supportFragmentManager, "")
        }
        viewModel.itemClickEvent.observe(this) {
            val item = viewModel.items[it]
            adapter.notifyItemChanged(it)

        }
        refreshProductPicture(adapter)

    }
    private fun refreshProductPicture(adapter: CustomAdapter) {
        scope = CoroutineScope(Dispatchers.Default).apply { // 코루틴 컨텍스트(디스패처만 지정함)로 코루틴범위 생성
            launch { // 코루틴범위 객체의 메소드 launch를 사용하여 코루틴 생성
                while(true) {
                    delay(3000) // 1000 ms 대기
                    withContext(Dispatchers.Main) { // runOnUiThread 대신 코루틴으로 사용
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Firebase.auth.signOut()
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
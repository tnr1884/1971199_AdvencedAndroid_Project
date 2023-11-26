package com.example.week9

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database

class MainActivity : AppCompatActivity() {
    private lateinit var scope: CoroutineScope

    private var db : FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("product")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.user)?.text = Firebase.auth.currentUser?.email ?: "No User"

        val viewModel by viewModels<MyViewModel>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CustomAdapter(viewModel)

        addItemListinViewModel(viewModel, adapter)

        val imageRef = Firebase.storage.getReferenceFromUrl("gs://prac-ebd62.appspot.com/영문로고.gif")
        displayImage(imageRef, findViewById<ImageView>(R.id.imageView))


        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        findViewById<Button>(R.id.fillterbutton)?.setOnClickListener {
            var checkedItemPosition = 0
            val array = arrayOf("모두", "판매 중", "판매 완료")
            AlertDialog.Builder(this)
                .setTitle("판매 여부")
                .setSingleChoiceItems(array, checkedItemPosition, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "which : $which")
                        checkedItemPosition = which
                        println(checkedItemPosition)
                    }
                })
                .setPositiveButton("ok", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        //Log.d("MyTag", "checkedItemPosition : $checkedItemPosition")
                        when(checkedItemPosition) {
                            0 -> {
                                viewModel.clear()
                                addItemListinViewModel(viewModel, adapter)
                                adapter.notifyDataSetChanged()
                            }
                            1 -> {
                                fillterbyisSelled(viewModel, adapter, true)
                            }
                            2 -> {
                                fillterbyisSelled(viewModel, adapter, false)
                            }
                        }
                    }
                })
                .show()
        }
        findViewById<Button>(R.id.signoutbutton).setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        //상품 등록
        findViewById<Button>(R.id.buttonRegister)?.setOnClickListener {
            ItemDialog(adapter, viewModel).show(supportFragmentManager, "")
        }

        findViewById<Button>(R.id.receivedmessagebutton).setOnClickListener {
            ReceivedMessageDialog().show(supportFragmentManager, "")
        }

        viewModel.itemClickEvent.observe(this) {
            val item = viewModel.items[it]

            if (item.seller==Firebase.auth.currentUser?.email ?: "no user") {
                UpdateDialog(adapter, viewModel, it, item).show(supportFragmentManager, "")
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0
            createNotificationChannel()
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

    private fun displayImage(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
        }
    }

    private fun addItemListinViewModel(viewModel: MyViewModel, adapter: CustomAdapter) {
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
    }
    private fun fillterbyisSelled(viewModel : MyViewModel, adapter: CustomAdapter, type : Boolean) {
        viewModel.clear()
        itemsCollectionRef.whereEqualTo("isSelled", type).get().addOnSuccessListener {
            for (doc in it) {
                viewModel.addItem(Item("gs://prac-ebd62.appspot.com/국문시그니쳐.jpg", "${doc["title"]}", "${doc["name"]}", "${doc["price"]}".toInt(), type, "${doc["seller"]}", doc.id))
                adapter.notifyDataSetChanged()
            }
        }
        adapter.notifyDataSetChanged()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "firebase-messaging", "firebase-messaging channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "This is firebase-messaging channel."
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == false) { // permission is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage("notification permission required!")
                }.show()
            }
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage("notification permission required!")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }

}
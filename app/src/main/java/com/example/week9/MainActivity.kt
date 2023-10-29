package com.example.week9

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        Firebase.auth.signInWithEmailAndPassword("hansung@gmail.com", "hansung")
            .addOnCompleteListener(this) {
                if(it.isSuccessful) {
                    textView.text = "sign-in success ${Firebase.auth.currentUser?.uid}"
                    displayImage()

                }
                else {
                    textView.text = "sign-in fail"
                }
            }

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1 // For test purpose only, 3600 seconds for production
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config)

        val textView2 = findViewById<TextView>(R.id.textView2)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { // it: task
                val test = remoteConfig.getBoolean("test")
                textView2.text="${test}"
            }
    }

    fun displayImage() {
        val storageRef = Firebase.storage.reference // reference to root
        //val imageRef1 = storageRef.child("images/computer_sangsangbugi.jpg")
        val imageRef = Firebase.storage.getReferenceFromUrl(
            "gs://prac-ebd62.appspot.com/대한민국_대통령기.png"
        )
        val view = findViewById<ImageView>(R.id.imageView)
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
        }

    }
}
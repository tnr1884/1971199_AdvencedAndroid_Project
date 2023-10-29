package com.example.week9

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(null)

        val textView = findViewById<TextView>(R.id.textView)
        Firebase.auth.signInWithEmailAndPassword("hansung@gmail.com", "hansu1ng")
            .addOnCompleteListener(this) {
                if(it.isSuccessful) {
                    textView.text = "sign-in success ${Firebase.auth.currentUser?.uid}"
                }
                else {
                    textView.text = "sign-in fail"
                }
            }
    }
}
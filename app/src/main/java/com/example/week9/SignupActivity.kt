package com.example.week9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.register)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.username)?.text.toString()
            val password = findViewById<EditText>(R.id.password)?.text.toString()
            doRegister(userEmail, password)
        }
    }

    private fun doRegister(userEmail: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "계정 생성 완료.", Toast.LENGTH_SHORT).show()
                    Firebase.auth.signInWithEmailAndPassword(userEmail, password).addOnSuccessListener {
                        Toast.makeText(this, "생성한 계정으로 로그인합니다.", Toast.LENGTH_SHORT).show()
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w("SignupActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
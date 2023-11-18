package com.example.week9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra("title")
        val name = intent.getStringExtra("name")
        val price = intent.getIntExtra("price",0)
        val isSelled = intent.getBooleanExtra("isSelled", true)
        val seller = intent.getStringExtra("seller")
        findViewById<TextView>(R.id.detailtitle).text=title
        findViewById<TextView>(R.id.detailname).text=name
        findViewById<TextView>(R.id.detailprice).text=price.toString() + " 원"
        if(isSelled) {
            findViewById<TextView>(R.id.detailisSelled).text= "판매 중"
        }
        else {
            findViewById<TextView>(R.id.detailisSelled).text= "판매 완료"
        }
        findViewById<TextView>(R.id.detailtitle).text=title

        findViewById<Button>(R.id.backproductlistbutton).setOnClickListener {
            finish()
        }
    }
}


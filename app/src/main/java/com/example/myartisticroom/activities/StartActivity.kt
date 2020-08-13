package com.example.myartisticroom.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myartisticroom.R
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        btn_sign_in_intro.setOnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }
        btn_sign_up_intro.setOnClickListener {
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }
    }
}
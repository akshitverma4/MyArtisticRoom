package com.example.myartisticroom.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myartisticroom.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        if (FirebaseAuth.getInstance().currentUser?.uid!=null)
        {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        else{

        }
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
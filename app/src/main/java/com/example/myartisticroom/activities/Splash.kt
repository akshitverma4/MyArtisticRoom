package com.example.myartisticroom.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myartisticroom.R
import com.example.myartisticroom.model.Chat
import com.example.myartisticroom.model.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_splash.*

class Splash : AppCompatActivity() {

    val firebase =FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val firestore = FirebaseFirestore.getInstance().collection("FullImage").document(firebase).get()
            .addOnSuccessListener {result ->
                if(result!=null){
                    val data = result.toObject(Image::class.java)
                    if (data != null) {
                        Glide.with(this).load(data.image).into(imageView)
                    }
                }
            }
        toMainScreen.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}
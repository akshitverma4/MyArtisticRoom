package com.example.myartisticroom.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myartisticroom.R
import com.example.myartisticroom.classes.DetailViewFragment
import com.example.myartisticroom.classes.User
import com.example.myartisticroom.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_news_feed.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.ArrayList


class NewsFeedActivity : AppCompatActivity() {

    private var mUsers: List<ContentDTO>? = null
    private var contentUidList : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed)
        button2.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this,AddPhotoActivity::class.java))
            }
        }
        mUsers = ArrayList()
        contentUidList = ArrayList()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        val firestore = FirebaseFirestore.getInstance().collection("images")
    .get().addOnSuccessListener { result ->
        for (document in result){
            val dta = document.toObject(ContentDTO::class.java)
            //val data = ArrayList<ContentDTO>()
           // data.add(dta)
            (mUsers as ArrayList<ContentDTO>).add(dta)
            (contentUidList as ArrayList<String>).add(document.id)
            val adapter = DetailViewFragment(this)
            detailviewfragment_recyclerview.adapter = adapter
                   detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(this)

        }

            }

    }


}
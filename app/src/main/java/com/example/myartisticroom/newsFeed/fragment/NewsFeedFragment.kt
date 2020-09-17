package com.example.myartisticroom.newsFeed.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myartisticroom.R
import com.example.myartisticroom.newsFeed.activity.AddPhotoActivity
import com.example.myartisticroom.newsFeed.classes.NewsFeedContent
import com.example.myartisticroom.newsFeed.classes.NewsFeedAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_news_feed.*
import kotlinx.android.synthetic.main.fragment_news_feed.view.*
import java.util.ArrayList

class NewsFeedFragment : Fragment() {

    private var mUsers: List<NewsFeedContent>? = null
    private var contentUidList : ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_news_feed, container, false)
        root.button2.setOnClickListener {
            if(activity?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                } == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(activity, AddPhotoActivity::class.java))
            }
        }
        mUsers = ArrayList()
        contentUidList = ArrayList()
        activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1) }

        val firestore = FirebaseFirestore.getInstance().collection("images")
            .get().addOnSuccessListener { result ->
                for (document in result){
                    val dta = document.toObject(NewsFeedContent::class.java)
                    //val data = ArrayList<ContentDTO>()
                    // data.add(dta)
                    (mUsers as ArrayList<NewsFeedContent>).add(dta)
                    (contentUidList as ArrayList<String>).add(document.id)
                    val adapter = activity?.let { NewsFeedAdapter(it) }
                    detailviewfragment_recyclerview.adapter = adapter
                    detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

                }

            }


        return root
    }
}
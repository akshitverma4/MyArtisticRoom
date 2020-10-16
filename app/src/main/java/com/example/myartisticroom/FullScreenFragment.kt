package com.example.myartisticroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.myartisticroom.model.Chat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_full__screen_.view.*

class FullScreenFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val views = inflater.inflate(R.layout.fragment_full__screen_, container, false)
        val firestore = FirebaseFirestore.getInstance().collection("Chats").document("HzVeJR9bnaMw0HDWzZwDat0sR0O2").get()
            .addOnSuccessListener {result ->
                if(result!=null){
                    val data = result.toObject(Chat::class.java)
                    //Glide.with(this).load(data!!.getUrl()).into(views.imageView)

                }


            }

        return views
    }
}
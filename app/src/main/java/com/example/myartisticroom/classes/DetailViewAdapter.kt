package com.example.myartisticroom.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myartisticroom.R
import com.example.myartisticroom.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailViewFragment(var context:Context):RecyclerView.Adapter<DetailViewFragment.ViewHolder>() {
    var contentDTO : ArrayList<ContentDTO> = arrayListOf()
    var contentUidList : ArrayList<String> = arrayListOf()

    init {


        FirebaseFirestore.getInstance().collection("images").get().addOnSuccessListener { result ->
            for (document in result){
                val dta = document.toObject(ContentDTO::class.java)
                (contentDTO as java.util.ArrayList<ContentDTO>).add(dta)
                (contentUidList as java.util.ArrayList<String>).add(document.id)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return contentDTO.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return DetailViewFragment.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: ContentDTO = contentDTO[position]
        Glide.with(holder.itemView).load(data.imageUrl).into(holder.image)
        holder.name.text = data.username.toString()
        holder.likeCount.text = data.favoriteCount.toString()
        holder.like.setOnClickListener {
            //This code is when the button is clicked
            Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show()
            favoriteEvent(position)

        }
        //This code is when the page is loaded
        if(contentDTO[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser?.uid)){
            //This is like status
            holder.like.setImageResource(R.drawable.ic_baseline_favorite_24)

        }else{
            //This is unlike status
            holder.like.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.detailviewitem_imageview_content)
        val name: TextView = itemView.findViewById(R.id.detailviewitem_profile_textview)
        val like:ImageView = itemView.findViewById(R.id.detailviewitem_favorite_imageview)
        val likeCount: TextView = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview)
    }


    fun favoriteEvent(position : Int) {
        val tsDoc = FirebaseFirestore.getInstance().collection("images").document(contentUidList[position])
        FirebaseFirestore.getInstance().runTransaction { transaction ->

            val uid = FirebaseAuth.getInstance().currentUser!!.uid


            val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)

            if(contentDTO!!.favorites.containsKey(uid)){
                //When the button is clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                contentDTO.favorites.remove(uid)

            }
            else
            {
                //When the button is not clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                contentDTO.favorites[uid] = true

            }
            transaction.set(tsDoc, contentDTO)
        }
    }




    }


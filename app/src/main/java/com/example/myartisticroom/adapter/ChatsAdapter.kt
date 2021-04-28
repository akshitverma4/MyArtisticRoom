package com.example.myartisticroom.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myartisticroom.R
import com.example.myartisticroom.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_image.view.*

class ChatsAdapter (
    mContext:Context,
    mChatList:List<Chat>
    //imageUrl:String
):RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private var mContext:Context? = null
    private var mChatList:List<Chat>? = null
  //  private var imageUrl:String? = null
    var firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mContext = mContext
        this.mChatList = mChatList
     //   this.imageUrl = imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1)
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return mChatList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat:Chat = mChatList!![position]

        //Picasso.get().load(imageUrl).into(holder.profile)

        if (chat.getMessage().equals("sent you an image.")&& !chat.getUrl().equals(""))

        {
            if (mChatList?.get(position)!!.getSender().equals(firebaseUser!!.uid))
            {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                //Picasso.get().load(chat.getUrl()).into(holder.right_image_view)
                mContext?.let { Glide.with(it).load(chat.getUrl()).into(holder.right_image_view!!) }

            }
            else if (!mChatList?.get(position)!!.getSender().equals(firebaseUser!!.uid))
            {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                //Picasso.get().load(chat.getUrl()).into(holder.left_image_view)
                mContext?.let { Glide.with(it).load(chat.getUrl()).into(holder.left_image_view!!) }
            }
        }
        else{
            holder.show_text_message!!.text = chat.getMessage()
        }



    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null


        init {
            profile = itemView.findViewById(R.id.profile)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            //text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (mChatList?.get(position)!!.getSender().equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }


    }


}



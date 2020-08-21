package com.example.myartisticroom.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myartisticroom.R
import com.example.myartisticroom.activities.MessageChatActivity
import com.example.myartisticroom.classes.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(mContext: Context, mList:List<User>, isChatChecked:Boolean
):RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private val mContext: Context
    private val mList: List<User>
    private val isChatChecked: Boolean

    init {
        this.mContext = mContext
        this.mList = mList
        this.isChatChecked = isChatChecked
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView
        //var ProfileImage:CircleImageView
        //var online:CircleImageView
        //var offline:CircleImageView
        //var lastMessage:TextView

        init {
            userName = itemView.findViewById(R.id.username1)
            //ProfileImage = itemView.findViewById(R.id.user_profile)
            //online = itemView.findViewById(R.id.online)
            //offline = itemView.findViewById(R.id.offline)
            //lastMessage = itemView.findViewById(R.id.lastMessage)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = mList[position]
        holder.userName.text = user.firstName

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )

            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->

                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.id)
                    mContext.startActivity(intent)
                }

                if (position == 1) {

                }

            })
             builder.show()

        }
    }
}

package com.example.myartisticroom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myartisticroom.R
import com.example.myartisticroom.adapter.UserAdapter
import com.example.myartisticroom.classes.User
import com.example.myartisticroom.model.Chatlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_chats.view.*


class ChatsFragment : Fragment() {

    private var userAdapter:UserAdapter? = null
    private var mUsers:List<User>? = null
    private var userChatlist:List<Chatlist>? = null
    //private val personCollectionRef = FirebaseFirestore.getInstance()
    private var firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var recyclerView:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerView = view.recycler_view_chatlist
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        //retrieveChatLists()
        firebaseUser = FirebaseAuth.getInstance().currentUser


        userChatlist =ArrayList()
        val refs = firebaseUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("chatlist").document(it).get()
                .addOnSuccessListener { result ->
                    if(result!=null){
                        Toast.makeText(activity,"Yo",Toast.LENGTH_SHORT).show()
                        val chatlist =result.toObject(Chatlist::class.java)
                        if (chatlist != null) {
                            (userChatlist as ArrayList).add(chatlist)
                        }
                    }
                    retrieveChatLists()
                }
        }


        return view
    }


    private fun retrieveChatLists(){

        mUsers = ArrayList()
        //val currentUsersId = FirebaseAuth.getInstance().currentUser!!.uid
        val querySnapshot = FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val boards = document.toObject(User::class.java)
                    for (eachChatList in userChatlist!!) {
                        if (boards.id.equals(eachChatList.id)) {
                            (mUsers as ArrayList<User>).add(boards)
                            userAdapter = UserAdapter(context!!, mUsers!!, false)
                            recycler_view_chatlist.adapter = userAdapter
                            recycler_view_chatlist.layoutManager = LinearLayoutManager(activity)
                        }
                    }
                }



            }
    }

}



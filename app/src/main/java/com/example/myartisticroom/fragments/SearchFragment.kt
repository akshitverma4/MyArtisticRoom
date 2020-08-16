package com.example.myartisticroom.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myartisticroom.R
import com.example.myartisticroom.activities.StartActivity
import com.example.myartisticroom.adapter.UserAdapter
import com.example.myartisticroom.classes.Constants
import com.example.myartisticroom.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<User>? = null
    private val personCollectionRef = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        mUsers = ArrayList()
        retrieveAllUsers()
        return view
    }

    private fun retrieveAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUsersId = FirebaseAuth.getInstance().currentUser!!.uid
                val querySnapshot = personCollectionRef.collection("users")

                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result)  {
                            val boards = document.toObject(User::class.java)!!
                            (mUsers as ArrayList<User>).add(boards)
                            userAdapter = UserAdapter(context!!, mUsers!!, false)
                            search.adapter = userAdapter
                            search.layoutManager = LinearLayoutManager(activity)

                        }
                    }


            }

            catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    //oast.makeText(this@Zx, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}




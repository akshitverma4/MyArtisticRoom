package com.example.myartisticroom.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myartisticroom.R
import com.example.myartisticroom.adapter.UserAdapter
import com.example.myartisticroom.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<User>? = null
    private val personCollectionRef = FirebaseFirestore.getInstance()
    private var searchField:EditText?=null
    private var recyclerView:RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView =view.search
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveAllUsers()
        searchField = view.searchEditText
        searchField!!.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                search(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        return view
    }

    private fun retrieveAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUsersId = FirebaseAuth.getInstance().currentUser!!.uid
                val querySnapshot = personCollectionRef.collection("users").get()
                    .addOnSuccessListener { result ->
                        (mUsers as ArrayList<User>).clear()
                        if (searchEditText.text!=null){
                            for (document in result)  {
                                val boards = document.toObject(User::class.java)
                                (mUsers as ArrayList<User>).add(boards)
                                userAdapter = UserAdapter(context!!, mUsers!!, false)
                                recyclerView!!.adapter = userAdapter
                                recyclerView!!.layoutManager = LinearLayoutManager(activity)

                            }
                        }
                    }


            }

            catch(e: Exception) {
                withContext(Dispatchers.Main) {
                }
            }
        }

    }
    private fun search(str:String){
        val currentUsersId = FirebaseAuth.getInstance().currentUser!!.uid
        val query = personCollectionRef.collection("users").orderBy("email")
            .startAt(str)
            .endAt(str + "\uf8ff")

        query.addSnapshotListener{ querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            (mUsers as ArrayList<User>).clear()
            if (querySnapshot != null) {
                for (document in querySnapshot) {
                    val boards = document.toObject(User::class.java)
                    (mUsers as ArrayList<User>).add(boards)
                }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter

                }
            }
    }
}




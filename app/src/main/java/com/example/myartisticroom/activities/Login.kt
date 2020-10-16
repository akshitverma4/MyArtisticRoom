package com.example.myartisticroom.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myartisticroom.R
import com.example.myartisticroom.classes.FirestoreClass
import com.example.myartisticroom.classes.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.user_search_item_layout.*
import kotlinx.android.synthetic.main.user_search_item_layout.username1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Login : BaseActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupActionBar()

        auth = FirebaseAuth.getInstance()

        login.setOnClickListener {
            loginUser()
        }
    }
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }

    }
    private fun loginUser() {
        val email = username2.text.toString().trim { it <= ' ' }
        val password = password.text.toString().trim { it <= ' ' }
        if (email.isNotEmpty() && password.isNotEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            CoroutineScope(Dispatchers.IO).launch {

                try {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            // TODO (Step 2: Remove the toast message and call the FirestoreClass signInUser function to get the data of user from database. And also move the code of hiding Progress Dialog and Launching MainActivity to Success function.)
                            // Calling the FirestoreClass signInUser function to get the data of user from database.
                            // FirestoreClass().signInUser(this@Login)
                            val intent = Intent(this@Login, MainActivity::class.java)
                            startActivity(intent)
                            // END
                        } else {

                        }
                    }.await()
                    withContext(Dispatchers.Main) {
                        val user = auth.currentUser
                        if (user!!.isEmailVerified) {

                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Login, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun signInSuccess(user: User) {
        val user = auth.currentUser
        if (user!!.isEmailVerified) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please Verify Email Address", Toast.LENGTH_SHORT).show()
        }


        //startActivity(Intent(activity, StartFragment::class.java))
    }

}
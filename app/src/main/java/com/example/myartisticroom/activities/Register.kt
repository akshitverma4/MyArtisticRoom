package com.example.myartisticroom.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.myartisticroom.R
import com.example.myartisticroom.classes.FirestoreClass
import com.example.myartisticroom.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Register : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    var registeredEmail:String? = null

    var firstName:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupActionBar()

        auth = FirebaseAuth.getInstance()
        signup.setOnClickListener {
            registerUser()
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }

    }

    private fun registerUser() {

        val email = UsernameTextField.text.toString()
         firstName = FirstNameTextField.text.toString()
        val lastName: String = LastNameTextField.text.toString().trim { it <= ' ' }
        val password = ConfirmPasswordTextField.text.toString()

        if (FirstNameTextField.text.toString().isEmpty()) {
            FirstNameTextField.error = "Please enter First name"
            FirstNameTextField.requestFocus()
            return
        }

        if (LastNameTextField.text.toString().isEmpty()) {
            LastNameTextField.error = "Please enter Last name"
            LastNameTextField.requestFocus()
            return
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(UsernameTextField.text.toString()).matches()) {
            UsernameTextField.error = "Please enter valid email"
            UsernameTextField.requestFocus()
            return
        }


        if (PasswordTextField.text.toString().isEmpty()) {
            PasswordTextField.error = "Please enter password"
            PasswordTextField.requestFocus()
            return
        }

        if (ConfirmPasswordTextField.text.toString() != ConfirmPasswordTextField.text.toString()) {
            ConfirmPasswordTextField.error = "Password and Confirm password should be same"
            ConfirmPasswordTextField.requestFocus()
            return
        }

        if (email.isNotEmpty() && password.isNotEmpty())
            showProgressDialog(resources.getString(R.string.please_wait))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                        hideProgressDialog()

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                             registeredEmail = firebaseUser.email!!


                            val user = User(firebaseUser.uid, firstName!!, lastName, registeredEmail!!)

                            // call the registerUser function of FirestoreClass to make an entry in the database.
                              FirestoreClass().registerUser(this@Register, user)
                        } else {

                        }
                    }.await()

                    withContext(Dispatchers.Main)
                    {
                        val user = auth.currentUser
                        user!!.sendEmailVerification()
                       val intent = Intent(this@Register,Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Register, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
    fun userRegisteredSuccess() {
//        Toast.makeText(
//            this,
//            "${auth.currentUser?.email}You have successfully registered.",
//            Toast.LENGTH_SHORT
//        ).show()

        Toast.makeText(
            this,
            "${firstName} You have successfully registered with email id $registeredEmail.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
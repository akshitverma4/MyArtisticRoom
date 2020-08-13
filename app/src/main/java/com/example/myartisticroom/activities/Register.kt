package com.example.myartisticroom.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.myartisticroom.R
import com.example.myartisticroom.classes.FirestoreClass
import com.example.myartisticroom.classes.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        signup.setOnClickListener {
            registerUser()
        }
    }
    private fun registerUser() {

        val email = UsernameTextField.text.toString()
        val firstName: String = FirstNameTextField.text.toString().trim { it <= ' ' }
        val lastName: String = LastNameTextField.text.toString().trim { it <= ' ' }
        val date: String = et_date.text.toString().trim { it <= ' ' }
        val password = PasswordTextField.text.toString()

        if (FirstNameTextField.text.toString().isEmpty()) {
            FirstNameTextField.error = "Please enter name"
            FirstNameTextField.requestFocus()
            return
        }



        if (!Patterns.EMAIL_ADDRESS.matcher(UsernameTextField.text.toString()).matches()) {
            UsernameTextField.error = "Please enter valid email"
            UsernameTextField.requestFocus()
            return
        }

        if (LastNameTextField.text.toString().isEmpty()) {
            LastNameTextField.error = "Please enter name"
            LastNameTextField.requestFocus()
            return
        }
        if (PasswordTextField.text.toString().isEmpty()) {
            PasswordTextField.error = "Please enter password"
            PasswordTextField.requestFocus()
            return
        }

        /*if (ConfirmTextField.text.toString() != PasswordTextField.text.toString()) {
            ConfirmTextField.error = "Password and Confirm password should be same"
            ConfirmTextField.requestFocus()
            return
        }*/

        if (email.isNotEmpty() && password.isNotEmpty())

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->

                            // If the registration is successfully done
                            if (task.isSuccessful) {

                                // Firebase registered user
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                // Registered Email
                                val registeredEmail = firebaseUser.email!!



                                val user = User(firebaseUser.uid,firstName,lastName,registeredEmail,date)

                                // call the registerUser function of FirestoreClass to make an entry in the database.
                                FirestoreClass().registerUser(this@Register, user)
                            } else {

                            }
                        }).await()

                    withContext(Dispatchers.Main)
                    {
                        val user = auth.currentUser
                        user!!.sendEmailVerification()
                       val intent = Intent(this@Register,Login::class.java)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Register, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
    fun userRegisteredSuccess() {

        Toast.makeText(
            this,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
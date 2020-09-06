package com.example.myartisticroom.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myartisticroom.R
import com.example.myartisticroom.adapter.ChatsAdapter
import com.example.myartisticroom.classes.FirestoreClass
import com.example.myartisticroom.classes.User
import com.example.myartisticroom.model.Chat
import com.example.myartisticroom.notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
const val TOPIC = "/topics/myTopic"
class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter:ChatsAdapter? = null
    var mChatlist:List<Chat>? = null
    val tokenss = "fAJKzPVJSt6VireRFr1IDG:APA91bE3sr2FSksvAKVfPcTugVy3zYzejTg2nRwQiHDhPuXU6ZwmQPa96VSSrG9XFWFLcBhudxha1kKtqpp-1Rt_QAYTYUreBcIbb9UcwhreU_SuHpLkB905mEDxM_eiYrVDgKPBBLyD"

    val TAG = "MainActivity"
    lateinit var recyclerView:RecyclerView
    private val personCollectionRef = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser


        recyclerView = recycler_view_chats
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)



        val reference = FirebaseFirestore.getInstance().collection("users").document(userIdVisit)
            .get()
            .addOnSuccessListener { result ->

                val user = result.toObject(User::class.java)!!

                username_mchat.text = user.firstName
            }
        retrieveMessage(firebaseUser!!.uid,userIdVisit)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            val tok = it.token
            val user = Tokens(tok)
            FirestoreClass().firestore(user)
            //etToken.setText(it.token)
        }


        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        send_message_btn.setOnClickListener {
            val refr = FirebaseFirestore.getInstance().collection("Tokens").document(userIdVisit)
                .get()
                .addOnSuccessListener {result ->
                    if (result!=null){
                        val message = "oye hoye"
                        val title = "You have one new notification"


                        //val mesg = "Tere bin"
                        //val recipientToken = etToken.text.toString()

                        val datta = result.toObject(Tokens::class.java)
                        val datt = datta!!.code
                        PushNotification(
                            NotificationData(title, message),datt
                            //recipientToken
                        ).also {
                            sendNotification(it)
                        }

                    }


                }

            //val title = text_message.text.toString()
            //val mesg = "Tere bin"
            //val recipientToken = etToken.text.toString()
            val message = text_message.text.toString()

            if(title.isNotEmpty() && message.isNotEmpty()) {

                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)

            }
            // val message = text_message.text.toString()

            /*f (message == "") {
                Toast.makeText(
                    this@MessageChatActivity,
                    "Please write a message",
                    Toast.LENGTH_LONG
                ).show()
            } */else {
                //sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)

            }

            text_message.setText("")
        }

        attach_image_file_btn.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image//*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)

        }
    }



    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {

        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference.child("chatsLists")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists())
                            {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference.child("chatsLists")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)

                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                    //implement the push notifications using fcm
                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)

                }

            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 438 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {


            /*val progressBar = ProgressDialog(this)
            progressBar.setMessage("Please Wait, image is sending...")
            progressBar.show()*/

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("chats").child(messageId!!).setValue(messageHashMap)
                    //progressBar.dismiss()
                }
            }
        }
    }
    private fun retrieveMessage(senderId: String, receiverId:String) {
        mChatlist = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("chats")

        reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (mChatlist as ArrayList<Chat>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId)&&chat.getSender().equals(receiverId)
                        ||chat!!.getReceiver().equals(receiverId)&&chat.getSender().equals(senderId) )
                    {
                        (mChatlist as ArrayList<Chat>).add(chat)
                    }
                }
                chatsAdapter = ChatsAdapter(this@MessageChatActivity,mChatlist as ArrayList<Chat>)
                recyclerView.adapter = chatsAdapter
            }
        })

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                //Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                //Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            //Log.e(TAG, e.toString())
        }
    }
}
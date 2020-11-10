package com.example.myartisticroom.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myartisticroom.R
import com.example.myartisticroom.adapter.ChatsAdapter
import com.example.myartisticroom.classes.FirestoreClass
import com.example.myartisticroom.classes.User
import com.example.myartisticroom.drawing.fragment.DrawingActivity
import com.example.myartisticroom.model.Chat
import com.example.myartisticroom.model.Chatlist
import com.example.myartisticroom.notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import io.grpc.InternalChannelz.id
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.fragment_chats.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//const val TOPIC = "/topics/myTopic"
class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var mess:String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter:ChatsAdapter? = null
    var mChatlist:List<Chat>? = null
    val TAG = "MainActivity"
    var datt:String = ""
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


        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)


        send_message_btn.setOnClickListener {
            val refr = FirebaseFirestore.getInstance().collection("Tokens").document(userIdVisit)
                .get()
                .addOnSuccessListener {result ->
                    if (result!=null){
                        Toast.makeText(this,"Yo",Toast.LENGTH_LONG).show()
                        val message = mess
                        val title = "My Artistic Room"


                        //val mesg = "Tere bin"
                        //val recipientToken = etToken.text.toString()

                        val datta = result.toObject(Tokens::class.java)
                        datt = datta!!.code
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
             mess = text_message.text.toString()

            if(title.isNotEmpty() && mess.isNotEmpty()) {

                sendMessageToUser(firebaseUser!!.uid, userIdVisit, mess)

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

            MaterialAlertDialogBuilder(this)
                .setTitle("Draw")
                .setMessage("Choose One Option")
                .setNeutralButton("Cancel") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Draw") { dialog, which ->

                    val intent = Intent(this, DrawingActivity::class.java)
                    intent.putExtra("visit_id", userIdVisit)
                        startActivity(intent)

                }
                .setPositiveButton("Select One") { dialog, which ->
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "image//*"
                    startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
                }
                .show()




        }
    }



    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {

        //val reference = FirebaseDatabase.getInstance().reference
        val reference = FirebaseFirestore.getInstance()
        //val messageKey = reference.push().key
        //Insert timestamp

val data:ArrayList<String> = ArrayList()
        if (receiverId != null) {
            data.add(receiverId)
        }
val chatlist = Chatlist(data)
        val messageHashMap = HashMap<String, Any?>()
        val messageHashMaps = HashMap<ArrayList<String>, Any?>()
        messageHashMap["timestamp"] = System.currentTimeMillis()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = ""
        reference.collection("chats")
            .document()
            .set(messageHashMap)
            .addOnSuccessListener  { result ->
                          var refr = firebaseUser?.uid?.let {
                              FirebaseFirestore.getInstance().collection("users")
                                  .document(it)
                                  .update("ids",FieldValue.arrayUnion(chatlist))

                          }



                }
        retrieveMessage(firebaseUser!!.uid,userIdVisit)


            }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 438 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            mess = "sent you an image."
            val message = mess
            val title = "My Artistic Room"
            PushNotification(
                NotificationData(title, message),datt
                //recipientToken
            ).also {
                sendNotification(it)
            }
            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chats")
            //val ref = FirebaseDatabase.getInstance().reference
            //val messageId = ref.push().key
            val ref = FirebaseFirestore.getInstance().collection("chats")
            val refs = FirebaseFirestore.getInstance().collection("Chats")
            val time = System.currentTimeMillis()
            val filePath = storageReference.child("$time.jpg")

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
                    //val time = System.currentTimeMillis()
                    messageHashMap["timestamp"] = System.currentTimeMillis()
                    messageHashMap["message"] = "sent you an image."
                    mess = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    //messageHashMap["messageId"] = messageId

                    //ref.child("chats").child(messageId!!).setValue(messageHashMap)
                    ref.document().set(messageHashMap)
                    refs.document(userIdVisit).set(messageHashMap)
                    //progressBar.dismiss()
                }
            }
        }
    }
    private fun retrieveMessage(senderId: String, receiverId:String) {
        mChatlist = ArrayList()
          val reference = FirebaseFirestore.getInstance().collection("chats").orderBy("timestamp",
              Query.Direction.ASCENDING)
              .addSnapshotListener { querySnapshot, _ ->

                  (mChatlist as ArrayList<Chat>).clear()
                  if (querySnapshot != null) {
                      for (results in querySnapshot){
                          val chat = results.toObject(Chat::class.java)
                          if (chat.getReceiver().equals(senderId)&&chat.getSender().equals(receiverId)
                              || chat.getReceiver().equals(receiverId)&&chat.getSender().equals(senderId) ) {
                              (mChatlist as ArrayList<Chat>).add(chat)
                          }
                          chatsAdapter = ChatsAdapter(this@MessageChatActivity,mChatlist as ArrayList<Chat>)
                          recyclerView.adapter = chatsAdapter

                      }
                  }

              }

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
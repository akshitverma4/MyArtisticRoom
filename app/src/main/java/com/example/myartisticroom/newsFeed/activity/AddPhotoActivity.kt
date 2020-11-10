package com.example.myartisticroom.newsFeed.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.myartisticroom.R
import com.example.myartisticroom.classes.User
import com.example.myartisticroom.newsFeed.classes.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.item_image.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = User()


        //Open the album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener { result ->
                if (result!=null){
                    val dta = result.toObject(User::class.java)
                    if (dta != null) {
                        user = dta

                    }
                }
            }

        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //This is path to the selected image
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)

            }else{
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()

            }
        }
    }
    fun contentUpload(){
        //Make filename

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_" + timestamp + "_.png"

        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //Promise method
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Username
            contentDTO.username = user.firstName

            //UserImage
            contentDTO.profileUrl = user.image

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = addphoto_edit_explainh.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()


            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()
        }

        /*//Callback method
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var contentDTO = ContentDTO()

                //Insert downloadUrl of image
                contentDTO.imageUrl = uri.toString()

                //Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                //Insert userId
                contentDTO.userId = auth?.currentUser?.email

                //Insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()

                //Insert timestamp
                contentDTO.timestamp = System.currentTimeMillis()

                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)

                finish()
            }
        }*/
    }
}

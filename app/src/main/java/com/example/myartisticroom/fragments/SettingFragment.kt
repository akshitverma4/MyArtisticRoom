package com.example.myartisticroom.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myartisticroom.R
import com.example.myartisticroom.activities.NewsFeedActivity
import com.example.myartisticroom.classes.Constants
import com.example.myartisticroom.classes.FirestoreClass
import com.example.myartisticroom.classes.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*
import java.io.IOException

class SettingFragment : Fragment() {


    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri: Uri? = null
    private var resolver = activity?.contentResolver
    private var mProfileImageURL: String = ""
    private lateinit var mUserDetails: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        view.newsFeedButton.setOnClickListener { view ->
            Log.d("btnSetup", "Selected")
            val intent = Intent(activity,NewsFeedActivity::class.java)
           startActivity(intent)
        }

        FirestoreClass().signIn(this)

        view.profile_image_setting.setOnClickListener {
            Toast.makeText(activity, "Clicked!",Toast.LENGTH_LONG).show()
            Log.d("profile_image", "Clicked!!")
            if (activity?.let { it1 -> ContextCompat.checkSelfPermission(it1, Manifest.permission.READ_EXTERNAL_STORAGE) }
                == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO (Step 8: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }

        view.btn_update.setOnClickListener {

            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {

                uploadUserImage()
            }
//            if else(mSelectedImageFileUri == null){
//           Toast.makeText(activity,"This is invoked!!",Toast.LENGTH_LONG).show()
//        }
            else{
                Log.e("SettingFragment","I shouldn't be here")
                Toast.makeText(activity,"This is invoked!!",Toast.LENGTH_LONG).show()
            }

        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
        } else{
            Toast.makeText(activity, "Oops you denied the permission for storage.You can allow it from settings",Toast.LENGTH_LONG).show()
        }
    }

    private fun showImageChooser(){

        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                activity?.let {
                    Glide
                        .with(it)
                        .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                        .centerCrop() // Scale type of the image.
                        .placeholder(R.drawable.ic_send) // A default place holder
                        .into(profile_image_setting)
                } // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun setUserDataInUI(user: User) {

        mUserDetails = user
        activity?.let {
            Glide
                .with(it)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_send)
                .into(profile_image_setting)
        }

    }




    private fun uploadUserImage() {

//        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUser()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        activity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

//                    hideProgressDialog()
                }
        }
    }


    private fun updateUser() {

        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

//        if (et_name.text.toString() != mUserDetails.name) {
//            userHashMap[Constants.NAME] = et_name.text.toString()
//        }
//
//        if (et_mobile.text.toString() != mUserDetails.mobile.toString()) {
//            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
//        }

        // Update the data in the database.
        FirestoreClass().updateUserProfileData(this, userHashMap)

    }
    private fun getFileExtension(uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(resolver?.getType(uri!!))
    }


}
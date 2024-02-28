package com.example.xold.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.xold.R
import com.example.xold.Utils
import com.example.xold.adapters.AdapterChat
import com.example.xold.databinding.ActivityChatBinding
import com.example.xold.models.ModelChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private companion object{

        private const val TAG = "CHAT_TAG"
    }

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var receiptUid = ""

    private var receiptFcmToken = ""

    private var myUid = ""

    private var myName = ""

    private var chatPath = ""

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        receiptUid = intent.getStringExtra("receiptUid")!!

        myUid = firebaseAuth.uid!!

        chatPath = Utils.chatPath(receiptUid, myUid)

        loadMyInfo()
        loadReceiptDetails()
        loadMessages()

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.attachFab.setOnClickListener {
            imagePickDialog()
        }

        binding.sendFab.setOnClickListener {
            validateData()
        }

    }

    private fun loadMyInfo(){

        Log.d(TAG, "loadMyInfo: ")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    myName = "${snapshot.child("name").value}"
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    private fun loadReceiptDetails(){

        Log.d(TAG, "loadReceiptDetails: ")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(receiptUid)
            .addValueEventListener(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    try{

                        val name = "${snapshot.child("name").value}"
                        val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                        receiptFcmToken = "${snapshot.child("fcmToken").value}"

                        Log.d(TAG, "onDataChange: name: $name")
                        Log.d(TAG, "onDataChange: profileImageUrl: $profileImageUrl")
                        Log.d(TAG, "onDataChange: receiptFcmToken: $receiptFcmToken")

                        binding.toolbarTitleTv.text = name

                        try {
                            Glide.with(this@ChatActivity)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.baseline_man_24)
                                .into(binding.toolbarProfileIv)
                        }catch (e: Exception){
                            Log.e(TAG, "onDataChange: ", e)
                        }

                    }catch (e: Exception){
                        Log.e(TAG, "onDataChange: ", e)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    private fun loadMessages(){

        Log.d(TAG, "loadMessages: ")
        val messageArrayList = ArrayList<ModelChat>()

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatPath)
            .addValueEventListener(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    messageArrayList.clear()

                    for(ds: DataSnapshot in snapshot.children){

                        try {

                            val modelChat = ds.getValue(ModelChat::class.java)
                            messageArrayList.add(modelChat!!)

                        }catch (e: Exception){
                            Log.e(TAG, "onDataChange: ", e)
                        }
                    }

                    val adapterChat = AdapterChat(this@ChatActivity, messageArrayList)
                    binding.chatRv.adapter = adapterChat
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


    }

    private fun imagePickDialog(){

        Log.d(TAG, "imagePickDialog: ")

        val popupMenu = PopupMenu(this, binding.attachFab)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem->

            val itemId = menuItem.itemId

            if(itemId==1){

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

                    requestCameraPermissions.launch(arrayOf(android.Manifest.permission.CAMERA))
                }else{

                    requestCameraPermissions.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }else if(itemId==2){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    pickImageGallery()
                } else{

                    requestStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            true
        }

    }


    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ result->

        Log.d(TAG, "requestCameraPermissions: ")

        var areAllGranted = true
        for (isGranted in result.values){
            areAllGranted = areAllGranted && isGranted
        }

        if(areAllGranted){

            Log.d(TAG, "requestCameraPermissions: All permissions e.g. Camera and Storage granted,")

            pickImageCamera()

        }else {


            Log.d(TAG, "requestCameraPermissions: All permissions or some of Camera and Storage denied")
            Utils.toast(this, "All permissions or some of Camera and Storage denied")
        }
    }


    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->

        Log.d(TAG, "requestStoragePermission: isGranted: $isGranted")

        if(isGranted){

            pickImageGallery()
        } else{

            Utils.toast(this, "Permission denied..!")
        }


    }


    private fun pickImageCamera(){
        Log.d(TAG, "pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "THE_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "THE_IMAGE_DESCRIPTION")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result->
        
        if(result.resultCode == Activity.RESULT_OK){

            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $imageUri")

            uploadToFirebaseStorage()

        }
        else{

            Utils.toast(this, "Cancelled..")
        }
    }


    private fun pickImageGallery(){

        Log.d(TAG, "pickImageGallery: ")

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        galleryActivityResultLauncher

    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result->

        if(result.resultCode == Activity.RESULT_OK){

            val data = result.data
            imageUri = data!!.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imageUri")

            uploadToFirebaseStorage()
        }
        else{

            Utils.toast(this, "Cancelled..")
        }
    }

    private fun uploadToFirebaseStorage(){

        Log.d(TAG, "uploadToFirebaseStorage: ")

        progressDialog.setMessage("Uploading image...")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()

        val filePathAndName = "ChatImages/$timestamp"

        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(imageUri!!)
            .addOnProgressListener {snapshot->

                val progress = 100 * snapshot.bytesTransferred / snapshot.totalByteCount

                progressDialog.setMessage("Uploading image: Progress ${progress.toUInt()} %")
            }
            .addOnSuccessListener {taskSnapshot->

                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = uriTask.result.toString()

                if(uriTask.isSuccessful){

                    sendMessage(Utils.MESSAGE_TYPE_IMAGE, uploadedImageUrl, timestamp)
                }
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Log.e(TAG, "uploadToFirebaseStorage: ", e)
                Utils.toast(this, "Failed to upload due to ${e.message}")

            }

    }

    private fun validateData(){

        Log.d(TAG, "validateData: ")

        val message = binding.messageEt.text.toString().trim()
        val timestamp = Utils.getTimestamp()

        if(message.isEmpty()){

            Utils.toast(this, "Enter message to send..")

        }else{

            sendMessage(Utils.MESSAGE_TYPE_TEXT, message, timestamp)
        }

    }

    private fun sendMessage(messageType: String, message: String, timestamp: Long){

        Log.d(TAG, "sendMessage: messageType; $messageType")
        Log.d(TAG, "sendMessage: message: $message")
        Log.d(TAG, "sendMessage: timestamp: $timestamp")

        progressDialog.setMessage("Sending message...")
        progressDialog.show()

        val refChat = FirebaseDatabase.getInstance().getReference("Chats")

        val keyId = "${refChat.push().key}"


        val hashMap = HashMap<String, Any>()
        hashMap["messageId"] = "$keyId"
        hashMap["messageType"] = "$messageType"
        hashMap["message"] = "$message"
        hashMap["fromUid"] = "$myUid"
        hashMap["toUid"] = "$receiptUid"
        hashMap["timestamp"] = timestamp

        refChat.child(chatPath)
            .child(keyId)
            .setValue(hashMap)
            .addOnSuccessListener {

                Log.d(TAG, "sendMessage: message sent")
                progressDialog.dismiss()

                binding.messageEt.setText("")

                if (messageType == Utils.MESSAGE_TYPE_TEXT){
                    prepareNotification(message)
                } else{

                    prepareNotification("Sent an attachment")
                }


            }
            .addOnFailureListener {e->
                Log.e(TAG, "sendMessage: ", e)

                progressDialog.dismiss()
                Utils.toast(this, "Failed to send message due to ${e.message}")
            }
    }

    private fun prepareNotification(message: String){
        Log.d(TAG, "prepareNotification: ")

        val notificationJo = JSONObject()
        val notificationDataJo = JSONObject()
        val notificationNotificationJo = JSONObject()

        try{

            notificationDataJo.put("notificationType", "${Utils.NOTIFICATION_TYPE_NEW_MESSAGE}")
            notificationDataJo.put("senderUid", "${firebaseAuth.uid}")

            notificationNotificationJo.put("title", "${myName}")
            notificationNotificationJo.put("body", "${message}")
            notificationNotificationJo.put("sound", "default")

            notificationJo.put("to", "$receiptFcmToken")
            notificationJo.put("notification", notificationNotificationJo)
            notificationJo.put("data", notificationDataJo)
        }catch (e: Exception){
            Log.e(TAG, "prepareNotification: ", e)
        }

        sendFcmNotification(notificationJo)

    }

    private fun sendFcmNotification(notificationJo: JSONObject){

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            notificationJo,
            Response.Listener{

                Log.d(TAG, "sendFcmNotification: Notification send $it")
            },
            Response.ErrorListener{e->

                Log.e(TAG, "sendFcmNotification: e")
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Context-Type"] = "application/json"
                headers["Authorization"] = "key=${Utils.FCM_SERVER_KEY}"

                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

}
package com.example.xold

import android.Manifest
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
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.example.xold.databinding.ActivityAdCreateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding

    private companion object{
        private const val TAG = "ADD_CREATE_TAG"
    }

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri? = null

    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>

    private lateinit var adapterImagePicked: AdapterImagePicked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        val adapterCategories = ArrayAdapter(this, R.layout.row_category_act, Utils.categories)
        binding.categoryAct.setAdapter(adapterCategories)

        val adapterConditions = ArrayAdapter(this, R.layout.row_condition_act, Utils.condition)
        binding.conditionAct.setAdapter(adapterConditions)

        imagePickedArrayList = ArrayList()

        loadImages()


        binding.toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.toolbarAdImageBtn.setOnClickListener {

            showImagePickOptions()
        }

        binding.postAdBtn.setOnClickListener {

            validateData()
        }

    }

    private fun loadImages() {

        Log.d(TAG, "loadImages: ")

        adapterImagePicked = AdapterImagePicked(this, imagePickedArrayList)

        binding.imageRv.adapter = adapterImagePicked
    }

    private fun showImagePickOptions(){
        Log.d(TAG, "showImagePickOptions: ")

        var popupMenu = PopupMenu(this, binding.toolbarAdImageBtn)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->

            val itemId = item.itemId

            if(itemId==1){

                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){

                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    requestCameraPermission.launch(cameraPermissions)
                }
                else{

                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestCameraPermission.launch(cameraPermissions)
                }

            }else if(itemId==2){

                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
                    pickImageGallery()
                }
                else{

                    val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                    requestStoragePermission.launch(storagePermission)

                }
            }
            true
        }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted->
        Log.d(TAG, "requestStoragePermission: isGranted: $isGranted")

        if (isGranted){

            pickImageGallery()
        }else{

            Utils.toast(this, "Storage permission denied..")
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ result->
        Log.d(TAG, "requestCameraPermission: result: $result")

        var areAllGranted = true
        for (isGranted in result.values){
            areAllGranted = areAllGranted && isGranted
        }

        if (areAllGranted){

            pickImageCamera()
        }else{

            Utils.toast(this, "Camera or Storage or both permission denied...")
        }
    }

    private fun pickImageGallery(){

        Log.d(TAG, "pickImageGallery: ")

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)

    }

    private fun pickImageCamera(){

        Log.d(TAG, "pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_IMAGE_DESCRIPTION")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result->

        Log.d(TAG, "galleryActivityResultLauncher: ")

        if (result.resultCode == Activity.RESULT_OK){

            val data = result.data

            imageUri = data!!.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imageUri")

            val timestamp = "${Utils.getTimestamp()}"

            val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)

            imagePickedArrayList.add(modelImagePicked)

            loadImages()
        } else {

            Utils.toast(this, "Cancelled..!")
        }

    }

    private val cameraActivityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
    ){ result->

        Log.d(TAG, "cameraActivityResultLauncher: ")
        if (result.resultCode == Activity.RESULT_OK){

            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $imageUri")

            val timestamp = "${Utils.getTimestamp()}"

            val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)

            imagePickedArrayList.add(modelImagePicked)

            loadImages()
        } else {

            Utils.toast(this, "Cancelled...!")
        }
    }

    private var brand = ""
    private var category = ""
    private var condition = ""
    private var address = ""
    private var price = ""
    private var title = ""
    private var description  = ""
    private var latitude = 0.0
    private var longitude = 0.0


    private fun validateData() {

        Log.d(TAG, "validateData: ")

        brand = binding.brandEt.text.toString().trim()
        category = binding.categoryAct.text.toString().trim()
        condition = binding.conditionAct.text.toString().trim()
        address = binding.locationAct.text.toString().trim()
        price = binding.priceEt.text.toString().trim()
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()


        if(brand.isEmpty()){

            binding.brandEt.error = "Enter Brand"
            binding.brandEt.requestFocus()
        } else if(category.isEmpty()){

            binding.categoryAct.error = "Choose Category"
            binding.categoryAct.requestFocus()

        } else if(condition.isEmpty()){

            binding.conditionAct.error = "Choose Condition"
            binding.conditionAct.requestFocus()

        } else if (title.isEmpty()){

            binding.titleEt.error = "Enter Title"
            binding.titleEt.requestFocus()

        }else if(description.isEmpty()){

            binding.descriptionEt.error = "Enter Description"
            binding.descriptionEt.requestFocus()

        }else{

            postAd()
        }
    }

    private fun postAd() {

        Log.d(TAG, "postAd: ")

        progressDialog.setMessage("Publishing Ad")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()

        val refAds= FirebaseDatabase.getInstance().getReference("Ads")

        val keyId = refAds.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$keyId"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["brand"] = "$brand"
        hashMap["category"] = "$category"
        hashMap["condition"] = "$condition"
        hashMap["address"] = "$address"
        hashMap["price"] = "$price"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["status"] = "${Utils.AD_STATUS_AVAILABLE}"
        hashMap["timestamp"] = timestamp
        hashMap["latitude"] = latitude
        hashMap["longitude"] = longitude


        refAds.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {

                Log.d(TAG, "postAd: Ad Published")
                uploadImagesStorage(keyId)

            }
            .addOnFailureListener { e->
                Log.e(TAG, "postAd: ", )
                progressDialog.dismiss()
                Utils.toast(this, "Failed due to ${e.message}")

            }
    }

    private fun uploadImagesStorage(adId: String){

        for(i in imagePickedArrayList.indices){

            val modelImagePicked = imagePickedArrayList[i]

            val imageName = modelImagePicked.id

            val filePathAndName = "Ads/$imageName"
            val imageIndexForProgress = i+ 1

            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
            storageReference.putFile(modelImagePicked.imageUri!!)
                .addOnProgressListener { snapshot->

                    val progress= 100* snapshot.bytesTransferred/ snapshot.totalByteCount

                    Log.d(TAG, "uploadImagesStorage: progress: $progress")
                    val message = "uploading $imageIndexForProgress of ${imagePickedArrayList.size} images...Progress ${progress.toInt()}"

                    progressDialog.setMessage(message)
                    progressDialog.show()

                }
                .addOnSuccessListener { taskSnapshot->
                    Log.d(TAG, "uploadImagesStorage: onSuccess")

                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val uploadedImageUrl = uriTask.result

                    if(uriTask.isSuccessful){

                        val hasMap = HashMap<String, Any>()
                        hasMap["id"] = "${modelImagePicked.id}"
                        hasMap["imageUrl"] = "$uploadedImageUrl"


                        val ref = FirebaseDatabase.getInstance().getReference("Ads")
                        ref.child(adId).child("Images")
                            .child(imageName)
                            .updateChildren(hasMap)
                    }
                    
                    progressDialog.dismiss()
                }
                .addOnFailureListener{ e->

                    Log.e(TAG, "uploadImagesStorage: ", e)
                    progressDialog.dismiss()
                }

        }
    }
}
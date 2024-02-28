package com.example.xold.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.xold.R
import com.example.xold.Utils
import com.example.xold.adapters.AdapterImageSlider
import com.example.xold.databinding.ActivityAdDetailsBinding
import com.example.xold.models.ModelAd
import com.example.xold.models.ModelImageSlider

class AdDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdDetailsBinding

    private companion object{

        private const val TAG = "AD_DETAILS_TAG"
    }

    private lateinit var firebaseAuth: FirebaseAuth

    private var adId = ""

    private var adLatitude= 0.0
    private var adLongitude = 0.0

    private var sellerUid = ""
    private var sellerPhone = ""

    private var favorite = false

    private lateinit var imageSliderArrayList: ArrayList<ModelImageSlider>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarEditBtn.visibility = View.GONE
        binding.toolbarDeleteBtn.visibility = View.GONE
        binding.chatBtn.visibility = View.GONE
        binding.callBtn.visibility = View.GONE
        binding.smsBtn.visibility = View.GONE


        firebaseAuth = FirebaseAuth.getInstance()

        adId = intent.getStringExtra("adId").toString()
        Log.d(TAG, "onCreate: adId: $adId")


        if(firebaseAuth.currentUser!= null){

            checkIsFavorite()
        }

        loadAdDetails()
        loadAdImages()

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.toolbarDeleteBtn.setOnClickListener {

            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            materialAlertDialogBuilder.setTitle("Delete Ad")
                .setMessage("Are you sure tou want to delete this Ad?")
                .setPositiveButton("DELETE"){ dialog, which ->

                    Log.d(TAG, "onCreate: DELETE clicked..!")
                    deleteAd()
                }
                .setNegativeButton("CANCEL"){dialog, which->
                    Log.d(TAG, "onCreate: CANCEL clicked..!")
                    dialog.dismiss()
                }
                .show()
        }

        binding.toolbarEditBtn.setOnClickListener {

            editOptionsDialog()

        }

        binding.toolbarFavBtn.setOnClickListener {
            if(favorite){

                Utils.removeFromFavorite(this, adId)
            }else{

                Utils.addToFavorite(this, adId)
            }
        }

        binding.sellerProfileIv.setOnClickListener {

            val intent = Intent(this, AdSellerProfileActivity::class.java)
            intent.putExtra("sellerUid", sellerUid)
            startActivity(intent)
        }

        binding.chatBtn.setOnClickListener {

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiptUid", sellerUid)
            startActivity(intent)
        }

        binding.callBtn.setOnClickListener {
            Utils.callIntent(this, sellerPhone)
        }

        binding.smsBtn.setOnClickListener {
            Utils.smsIntent(this, sellerPhone)
        }

        binding.mapBtn.setOnClickListener {
            Utils.mapIntent(this, adLatitude, adLongitude)
        }


    }

    private fun editOptionsDialog() {
        Log.d(TAG, "editOptionsDialog: ")

        val popupMenu = PopupMenu(this, binding.toolbarEditBtn)

        popupMenu.menu.add(Menu.NONE, 0, 0, "Edit")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Mark As Sold")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {menuItem->

            val itemId = menuItem.itemId

            if(itemId ==0){

                val intent = Intent(this, AdCreateActivity::class.java)
                intent.putExtra("isEditMode", true)
                intent.putExtra("adId", adId)
                startActivity(intent)
            }else if(itemId==1){

                showMarkAsSoldDialog()
            }

            return@setOnMenuItemClickListener true
        }

    }


    private fun showMarkAsSoldDialog(){
        Log.d(TAG, "showMarkAsSoldDialog: ")

        val alertDialogBuilder = MaterialAlertDialogBuilder(this)
        alertDialogBuilder.setTitle("Mark as Sold")
            .setMessage("Are you sure you want to mark this Ad as sold?")
            .setPositiveButton("SOLD"){dialog, which->
                Log.d(TAG, "showMarkAsSoldDialog: SOLD clicked")

                val hashMap = HashMap<String, Any>()
                hashMap["status"] = "${Utils.AD_STATUS_SOLD}"

                val ref = FirebaseDatabase.getInstance().getReference("Ads")
                ref.child(adId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {

                        Log.d(TAG, "showMarkAsSoldDialog: Marked as sold")
                    }
                    .addOnFailureListener { e->

                        Log.e(TAG, "showMarkAsSoldDialog: ", e)
                        Utils.toast(this, "Failed to mark as sold due to ${e.message}")
                    }
            }
            .setNegativeButton("CANCEL"){dialog, which->
                Log.d(TAG, "showMarkAsSoldDialog: CANCEL clicked")
                dialog.dismiss()
            }
            .show()
    }

    private fun loadAdDetails(){
        Log.d(TAG, "loadAdDetails: ")

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    try{

                        val modelAd = snapshot.getValue(ModelAd::class.java)
                        sellerUid = "${modelAd!!.uid}"
                        val title = modelAd.title
                        val description = modelAd.description
                        val address = modelAd.address
                        val condition = modelAd.condition
                        val category = modelAd.category
                        val price = modelAd.price
                        adLatitude = modelAd.latitude
                        adLongitude = modelAd.longitude
                        val timestamp = modelAd.timestamp
                        val formattedDate = Utils.formatTimestampDate(timestamp)

                        if(sellerUid == firebaseAuth.uid){

                            binding.toolbarEditBtn.visibility = View.VISIBLE
                            binding.toolbarDeleteBtn.visibility = View.VISIBLE

                            binding.chatBtn.visibility = View.GONE
                            binding.callBtn.visibility = View.GONE
                            binding.smsBtn.visibility = View.GONE

                            binding.sellerProfileLabelTv.visibility = View.GONE
                            binding.sellerProfileCv.visibility = View.GONE

                        }else{

                            binding.toolbarEditBtn.visibility = View.GONE
                            binding.toolbarDeleteBtn.visibility = View.GONE

                            binding.chatBtn.visibility = View.VISIBLE
                            binding.callBtn.visibility = View.VISIBLE
                            binding.smsBtn.visibility = View.VISIBLE
                        }

                        binding.titleTv.text = title
                        binding.descriptionTv.text = description
                        binding.addressTv.text = address
                        binding.conditionTv.text = condition
                        binding.categoryTv.text = category
                        binding.priceTv.text = price
                        binding.dateTv.text = formattedDate

                        loadSellerDetails()

                    }catch (e: Exception){
                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    private fun loadSellerDetails(){
        Log.d(TAG, "loadSellerDetails: ")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(sellerUid)
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {


                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    val timestamp = snapshot.child("timestamp").value as Long

                    val formattedDate = Utils.formatTimestampDate(timestamp)

                    sellerPhone = "$phoneCode$phoneNumber"

                    binding.sellerNameTv.text = name
                    binding.memberSinceLabelTv.text = formattedDate

                    try{
                        Glide.with(this@AdDetailsActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_man)
                            .into(binding.sellerProfileIv)
                    }catch (e: Exception){
                        Log.e(TAG, "onDataChange: ", e)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun checkIsFavorite(){

        Log.d(TAG, "checkIsFavorite: ")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}").child("Favorites").child(adId)
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    favorite = snapshot.exists()
                    Log.d(TAG, "onDataChange: favorite: $favorite")

                    if(favorite){
                        binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_yes)
                    }else{

                        binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_no)
                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
    }

    private fun loadAdImages(){

        Log.d(TAG, "loadAdImages: ")

        imageSliderArrayList = ArrayList()

        val ref= FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adId).child("Images")
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    imageSliderArrayList.clear()

                    for (ds in snapshot.children){

                        try{
                            val modelImageSlider = ds.getValue(ModelImageSlider::class.java)
                            imageSliderArrayList.add(modelImageSlider!!)

                        } catch (e: Exception){

                            Log.e(TAG, "onDataChange: ", e)
                        }

                        val adapterImageSlider = AdapterImageSlider(this@AdDetailsActivity, imageSliderArrayList)
                        binding.imageSliderVp.adapter = adapterImageSlider
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun deleteAd(){

        Log.d(TAG, "deleteAd: ")

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(TAG, "deleteAd: Deleted")
                Utils.toast(this, "Deleted..!")

                finish()
            }
            .addOnFailureListener { e->
                Log.e(TAG, "deleteAd: ", e)
                Utils.toast(this, "Failed to delete due to ${e.message}")

            }
    }

}
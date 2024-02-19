package com.example.xold

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.xold.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    private lateinit var mContext: Context

    private companion object{
        private const val TAG = "ACCOUNT_TAG"
    }
    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        loadMyInfo()

        binding.logoutCv.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, MainActivity::class.java))
            activity?.finishAffinity()
        }

        binding.editProfileCv.setOnClickListener{
            startActivity(Intent(mContext, ProfileEditActivity::class.java))

        }

        binding.changePassword.setOnClickListener {
            startActivity(Intent(mContext, ChangePasswordActivity::class.java))
        }

        binding.verifyAccountCv.setOnClickListener {
            verifyAccount()
        }


    }

    private fun loadMyInfo() {

        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dob = "${snapshot.child("dob").value}"
                    val email = "${snapshot.child("email").value}"
                    val name= "${snapshot.child("name").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    var timestamp = "${snapshot.child("timestamp").value}"
                    val userType = "${snapshot.child("userType").value}"

                    val phone = phoneCode+ phoneNumber

                    if(timestamp == "null"){
                        timestamp = "0"
                    }

                    val formattedDate = Utils.formatTimestampDate(timestamp.toLong())

                    binding.emailTv.text = email
                    binding.nameTv.text = name
                    binding.dobTv.text = dob
                    binding.phoneTv.text = phone
                    binding.memberSinceTv.text = formattedDate

                    if(userType== "Email"){

                        val isVerified = firebaseAuth.currentUser!!.isEmailVerified
                        if(isVerified){

                            binding.verifyAccountCv.visibility = View.GONE
                            binding.verificationTv.text = "Verified"
                        }else{

                            binding.verifyAccountCv.visibility = View.VISIBLE
                            binding.verificationTv.text= "Not Verified"

                        }
                    }
                    else{
                        binding.verifyAccountCv.visibility = View.GONE
                        binding.verificationTv.text = "Verified"
                    }

                    try{
                        Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.baseline_man_24)
                            .into(binding.profileIv)
                    }catch(e: Exception){
                        Log.d(TAG, "onDataChange: ",e)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun verifyAccount(){

        Log.d(TAG, "verifyAccount: ")
        
        progressDialog.setMessage("Sending account verification instructions to your email...")
        progressDialog.show()
        
        firebaseAuth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {

                Log.d(TAG, "verifyAccount: Successfully sent")
                progressDialog.dismiss()
                Utils.toast(mContext, "Account verification instruction sent to your email...")

            }
            .addOnFailureListener {e->
                Log.e(TAG, "verifyAccount: e")
                progressDialog.dismiss()
                Utils.toast(mContext, "Failed to send due to ${e.message}")

            }
    }
}

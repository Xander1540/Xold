package com.example.xold

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.example.xold.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

private lateinit var binding: ActivityForgotPasswordBinding

private companion object{

    private const val TAG = "FORGOT_PASSWORD"
}

private lateinit var firebaseAuth: FirebaseAuth

private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var email=""

    private fun validateData(){
        email = binding.emailEt.text.toString().trim()
        Log.d(TAG, "validateData: $email")

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            binding.emailEt.error = "Invalid Email Pattern!"
            binding.emailEt.requestFocus()
        }
        else{

            sendPasswordRecoveryInstructions()
        }
    }

    private fun sendPasswordRecoveryInstructions() {
        Log.d(TAG, "sendPasswordRecoveryInstructions: ")

        progressDialog.setMessage("Sending password reset instructions to $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Utils.toast(this, "Instructions to reset password sent to $email")
            }
            .addOnFailureListener {e->

                Log.e(TAG, "sendPasswordRecoveryInstructions: ",e)
                progressDialog.dismiss()
                Utils.toast(this, "Falied to send due to ${e.message}")

            }
    }

}
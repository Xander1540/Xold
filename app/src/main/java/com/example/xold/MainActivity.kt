package com.example.xold

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.xold.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser ==null){
            startLoginOptions()
        }

        showHomeFragment()

        binding.bottomNv.setOnItemSelectedListener{item ->
        when(item.itemId) {
            R.id.menu_home -> {
                showHomeFragment()
                true
            }
            R.id.menu_chats -> {

                if(firebaseAuth.currentUser==null){
                    Utils.toast(this, "Login Required")
                    startLoginOptions()

                    false
                }else{
                    showChatsFragment()
                    true
                }
            }
            R.id.menu_my_ads ->{

                if(firebaseAuth.currentUser==null){
                    Utils.toast(this, "Login Required")
                    startLoginOptions()

                    false
                }else {
                    showMyAdsFragment()

                    true
                }
            }
            R.id.menu_account ->{

                if(firebaseAuth.currentUser==null){
                    Utils.toast(this, "Login Required")
                    startLoginOptions()

                    false
                }else{
                    showAccountFragment()
                    true
                }
            }
            else ->{
                false
            }
        }
        }


        binding.sellFab.setOnClickListener {
            startActivity(Intent(this, AdCreateActivity::class.java))

        }

    }

    private fun showHomeFragment(){

        binding.toolbarTitleTv.text= "Home"

        val fragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "HomeFragment")
        fragmentTransaction.commit()
    }

    private fun showChatsFragment(){
        binding.toolbarTitleTv.text= "Chats"

        val fragment = ChatsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "ChatsFragment")
        fragmentTransaction.commit()
    }

    private fun showMyAdsFragment(){
        binding.toolbarTitleTv.text= "My Ads"

        val fragment = MyAdsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "MyAdsFragment")
        fragmentTransaction.commit()
    }

    private fun showAccountFragment(){
        binding.toolbarTitleTv.text= "Account"

        val fragment = AccountFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "AccountFragment")
        fragmentTransaction.commit()
    }

    private fun startLoginOptions(){
        startActivity(Intent(this, LoginOptionsActivity::class.java))
    }


}






















/*
1. Enable ViewBinding in build.gradle
2. Add Required colors in colors.xml
3. create menu_button.xml for bottom Navigator menu.
4. Add Bottom Navigation Menu in activity.xml .
5. Handle Bottom Navigation Menu item click in MainActivity.kt
6. Create required fragments to show on clicking bottom navigation menu.
7. Fragment Navigation
8. Create Login Options Activity
9. Add login options [ google, phone, email]
10. Enable Authentication Methods(Google, Phone Email) on Firebase
11. Add permission Internet inAndroid Manifest
12. Create Login Register Activities
13. Login UI
14. Login Coding
15. Register Ui
16. Register Coding
17. Make sure to Enable The Google Authentication Method on Firebase.
18. Add Google SignIn library
19. Google SignIn Coding
20. Test Google SignIn
21. Create LoginPhoneActivity
22. Make sure to Enable the phone authentication method on firebase.
23. Add country code picker library.
24. Phone Login UI
25. Phone Login Coding
26. Test Phone Login
27. ForgotPasswordActivity Create/Start
28. Forgot Password UI
29. Forgot Password Coding
30. Test Forgot Password
31. ChangePasswordActivity Create/Start
32. Change Password UI
33. Change Password Coding
34. Test Change Password
35. Verify Account
36. Verify Account Coding
37. Test Verify Account
38. DeleteAccountActivity Create/Start
39. Delete Account UI
40. Delete Account Coding
41. Test Delete Account
42. Setup MAP SDK
43. Create LocationPickerActivity
44. Location Picker UI
45. Location Picker Coding
46. Pick Location for Ad

 */
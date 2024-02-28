package com.example.xold.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.xold.R
import com.example.xold.Utils
import com.example.xold.databinding.ActivityMainBinding
import com.example.xold.fragments.AccountFragment
import com.example.xold.fragments.ChatsFragment
import com.example.xold.fragments.HomeFragment
import com.example.xold.fragments.MyAdsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    companion object{

        private const val TAG = "MAIN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser ==null){
            startLoginOptions()
        }else{

            updateFcmToken()
            askNotificationPermission()
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
            val intent = Intent(this, AdCreateActivity::class.java)
            intent.putExtra("isEditMode", true)
            startActivity(intent)

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

    private fun updateFcmToken() {

        val myUid = "${firebaseAuth.uid}"
        Log.d(TAG, "updateFcmToken: ")

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {fcmToken->

                Log.d(TAG, "updateFcmToken: fcmToken $fcmToken")
                val hashMap = HashMap<String, Any>()
                hashMap["fcmToken"] = "$fcmToken"

                val ref = FirebaseDatabase.getInstance().getReference("Users")
                ref.child(myUid)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {

                        Log.d(TAG, "updateFcmToken: FCM Token Update to db success")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "updateFcmToken: ", e)
                    }
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "updateFcmToken: ", e)
            }
    }
    
    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->

            Log.d(TAG, "requestPermissionLauncher: isGranted: $isGranted")
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
47. Ads UI - fragment_home.xml
48. Ad category UI - row_category.xml
49. Create Model (ModelCategory) Class for Ad Categories List to show in RecyclerView (Horizontal)
50. Create Custom Adapter (AdapterCategory) Class for Ad Categories List to show in RecyclerView (Horizontal)
51. Create interface (RvListenerCategory) to handle Ad Category click event in Fragment (HomeFragment)
52. Show Categories - Coding
53. Show Ads UI - row_ad.xml
54. Create Model (ModelAd) Class for Ads List to show in RecyclerView
55. Create Custom Adapter (AdapterAd) Class for Ads List to show in RecyclerView
56. Create Filter (FilterAd) Class to search Ads
57. Show Ads Coding - HomeFragment.java
58. Testing
59. Check/Add/Remove the Ad to/from favorite.
60. Create Fragments for Tabs e.g. My Ads & Favorite Ads
61. My Ads Tabs - UI
62. My Ads Tabs - Coding
63. Create/Setup MyAdsFragment & MyAdsFavoriteFragment
64. My Ads - UI
65. My Ads - Coding
66. Favorite Ads - UI
67. Favorite Ads - Coding
68. Testing
69. Create packages and Move Classes to relevant packages
70. Create/Start AdDetailsActivity
71. Ad Details - UI
72. Load the Ad Details
    row_image_slider
    ModelImageSlider
    AdapterImageSlider
    Load the Ad details
73. Load the seller info
74. Call, SMS Map intents
75. Testing
76. Edit Ad Dialog
77. Edit Ad
    Load Ad Details
    Edit Ad
78. Delete Ad Image
79. Testing
80. Show Seller Profile option if Ad is not by currently signed-in user (AdDetailsActivity)
81. Create/Start AdSellerProfileActivity
82. Seller Profile UI
83. Seller Profile Coding
84. Seller Ads Coding
85. Testing
86. Create/Start ChatActivity
87. Chat UI
88. ModelChat
89. AdapterChat
90. Show Chat
91. Testing
92. Chats UI
        fragment_chats.xml  RecyclerView to show chats
        row_chats.xml       RecyclerView row/item
93. Chats Code
        ModelChats
        AdapterChats
        FilterChats
        Load Chats
94. Testing
95. Add FCM (Firebase Cloud Messaging) library.
96. Update FCM token whenever app starts
97. Ask Notification permission requires for Android 13 and above
98. Send Chat Notification
99. Handle/Show Chat Notification
100. Testing
 */
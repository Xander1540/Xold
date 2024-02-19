package com.example.xold

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.xold.databinding.ActivityAdCreateBinding

class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}
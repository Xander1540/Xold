package com.example.xold.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xold.R
import com.example.xold.databinding.RowImageSliderBinding
import com.example.xold.models.ModelImageSlider
import com.google.android.material.imageview.ShapeableImageView

class AdapterImageSlider: RecyclerView.Adapter<AdapterImageSlider.HolderImageSlider> {

    private lateinit var binding: RowImageSliderBinding

    private companion object{
        private const val TAG = "IMAGE_SLIDER_TAG"
    }

    private var context: Context

    private var imageArrayList: ArrayList<ModelImageSlider>

    constructor(context: Context, imageArrayList: ArrayList<ModelImageSlider>) {
        this.context = context
        this.imageArrayList = imageArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImageSlider {

        binding = RowImageSliderBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderImageSlider(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImageSlider, position: Int) {

        val modelImageSlider = imageArrayList[position]

        val imageUrl = modelImageSlider.imageUrl
        val imageCount = "${position+1}/${imageArrayList.size}"

        holder.imageCountTv.text = imageCount

        try{
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_photo)
                .into(holder.imageIv)
        }catch (e: Exception){
            Log.e(TAG, "onBindViewHolder: ", e)
        }

        holder.itemView.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {

        return imageArrayList.size
    }

    inner class HolderImageSlider(itemView: View): RecyclerView.ViewHolder(itemView){


        var imageIv: ShapeableImageView = binding.imageIv
        var imageCountTv: TextView = binding.imageCountTv
    }


}
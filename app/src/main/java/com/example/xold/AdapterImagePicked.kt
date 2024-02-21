package com.example.xold

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.xold.databinding.RowImagesPickedBinding


class AdapterImagePicked(
    private val context: Context,
    private val imagesPickedArrayList: ArrayList<ModelImagePicked>
): Adapter<AdapterImagePicked.HolderImagePicked>() {


    private companion object{
        private const val TAG = "IMAGES_TAG"
    }
    private lateinit var binding: RowImagesPickedBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagePicked {

        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderImagePicked(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImagePicked, position: Int) {

        val model = imagesPickedArrayList[position]

        val imageUri = model.imageUri
        Log.d(TAG, "onBindViewHolder:  imageUri $imageUri")

        try{
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_photo)
                .into(holder.imageIv)
        }catch (e: Exception){
            Log.e(TAG, "onBindViewHolder: ", e)
        }

        holder.closeBtn.setOnClickListener {

            imagesPickedArrayList.remove(model)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {

        return  imagesPickedArrayList.size
    }

    inner class HolderImagePicked(itemView: View): ViewHolder(itemView){

        var imageIv = binding.imageIv
        var closeBtn = binding.closeBtn


    }

}
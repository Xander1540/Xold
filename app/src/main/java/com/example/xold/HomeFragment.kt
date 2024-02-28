package com.example.xold

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.util.Util
import com.example.xold.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private companion object{
        private const val TAG="HOME_TAG"
    }

    private lateinit var mContext: Context

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        loadCategories()
    }

    private fun loadCategories(){

        val categoryArrayList = ArrayList<ModelCategory>()

        for(i in 0 until Utils.categories.size){

            val modelCategory = ModelCategory(Utils.categories[i], Utils.categoryIcon[i])

            categoryArrayList.add(modelCategory)
        }

        val adapterCategory = AdapterCategory(mContext, categoryArrayList, object: RvListenerCategory{
            override fun onCategoryClick(modelCategory: ModelCategory) {


            }
        })

        binding.categoriesRv.adapter = adapterCategory

    }

}
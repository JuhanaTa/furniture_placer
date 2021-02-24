package com.example.furniture_placer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.OneImage
import com.example.furniture_placer.R
import com.example.furniture_placer.adapters.ImageSliderAdapter



class ImageSliderFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    var listOfImages = ArrayList<OneImage>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_slider, container, false)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = view.findViewById(R.id.sliderList)
        recyclerView.setLayoutManager(layoutManager)

        //recyclerView.layoutManager = LinearLayoutManager(view.context)

        listOfImages.add(OneImage("drawable://"))
        listOfImages.add(OneImage("drawable://"))
        listOfImages.add(OneImage("drawable://"))


        recyclerView.adapter = ImageSliderAdapter(listOfImages)


        return view
    }


}
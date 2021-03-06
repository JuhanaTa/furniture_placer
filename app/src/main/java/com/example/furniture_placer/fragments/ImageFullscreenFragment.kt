package com.example.furniture_placer.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.furniture_placer.R
import kotlinx.android.synthetic.main.fragment_image_fullscreen.view.*


class ImageFullscreenFragment(imageData: Bitmap) : Fragment() {

    private val image = imageData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_fullscreen, container, false)
        view.findViewById<ImageView>(R.id.imageView).setImageBitmap(image)

        WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
        view.imageView.setOnClickListener {
            val fm: FragmentManager? = fragmentManager

            fm?.popBackStack()
        }
        return view
    }
}
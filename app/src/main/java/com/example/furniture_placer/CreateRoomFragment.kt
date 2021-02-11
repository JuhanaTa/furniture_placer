package com.example.furniture_placer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_room.view.*

class CreateRoomFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_room, container, false)

        view.createRoomBtn.setOnClickListener {
            val intent = Intent(activity, ArFragmentView::class.java).apply {
            }
            startActivity(intent)
        }
        return view
    }


}
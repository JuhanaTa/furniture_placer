package com.example.furniture_placer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_room_detail.view.*


class RoomDetailFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_detail, container, false)

        view.modifyRoomBtn.setOnClickListener {
            val intent = Intent(activity, ArFragmentView::class.java).apply {
            }
            startActivity(intent)
        }

        return view
    }

}